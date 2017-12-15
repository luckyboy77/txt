package wg.fnd.utils;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileUploadException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.multipart.MultipartFile;

import com.aliyun.oss.ClientException;
import com.aliyun.oss.OSSClient;
import com.aliyun.oss.OSSException;
import com.aliyun.oss.model.CannedAccessControlList;
import com.aliyun.oss.model.CreateBucketRequest;
import com.aliyun.oss.model.OSSObject;
import com.google.common.collect.Maps;
import com.hand.hap.message.profile.GlobalProfileListener;

import wg.fnd.dto.FileObject;

/**
 * @author xianzhi.chen@hand-china.com
 * @name OssHelper
 * @description 附件对象上传处理类(基于阿里云OSS进行封装)
 * @date 2017/02/09
 */
public class OssHelper implements GlobalProfileListener {
	
	/**
	 * 成功标识
	 */
	protected final String SUCCESS = "S";
	/**
	 * 失败标识
	 */
	protected final String ERROR = "E";

	/**
	 * buffer 大小.
	 */
	private static final Integer BUFFER_SIZE = 1024;

	/**
	 * 文件下载默认编码.
	 */
	private static final String ENC = "UTF-8";

	/**
	 * 文件不存在提示.
	 */
	private static final String FILE_NOT_EXSIT = "file_not_exsit";

	private final Logger logger = LoggerFactory.getLogger(OssHelper.class);

	private String secret;
	private String endpoint;
	private String accessKeyId;
	private String accessKeySecret;
	private String proxyAddr;

	@Override
	public List<String> getAcceptedProfiles() {
		return Arrays.asList("WG_FND_OSS_SECRET"); // 配置阿里云OSS访问权限信息，格式为endpoint,accessKeyId,accessKeySecret,bucketName
	}

	@Override
	public void updateProfile(String profileName, String profileValue) {
		this.secret = profileValue;
		String[] secrets = this.secret.split(",");
		if (secrets.length >= 4) {
			this.endpoint = secrets[0];
			this.accessKeyId = secrets[1];
			this.accessKeySecret = secrets[2];
			this.proxyAddr = secrets[3];
		}
	}
	
	/**
	 * 初始化上传文件信息，验证不过的在集合中进行删除
	 * 
	 * @param request
	 * @return
	 * @throws FileUploadException
	 */
	public MultipartFile initFileItem(MultipartFile[] file) {
		if(file != null && file.length > 0){
			return file[0];
		}
		return null;
	}

	private String processFileName(HttpServletRequest request, String filename) throws UnsupportedEncodingException {
		String userAgent = request.getHeader("User-Agent");
		String new_filename = URLEncoder.encode(filename, "UTF8");
		String rtn = "filename=\"" + new_filename + "\"";
		if (userAgent != null) {
			userAgent = userAgent.toLowerCase();
			if (userAgent.indexOf("msie") != -1) {
				rtn = "filename=\"" + new String(filename.getBytes("GB2312"), "ISO-8859-1") + "\"";
			} else if (userAgent.indexOf("safari") != -1 || userAgent.indexOf("applewebkit") != -1) {
				rtn = "filename=\"" + new String(filename.getBytes("UTF-8"), "ISO8859-1") + "\"";
			} else if (userAgent.indexOf("opera") != -1 || userAgent.indexOf("mozilla") != -1) {
				rtn = "filename*=UTF-8''" + new_filename;
			}
		}
		return rtn;
	}

	/**
	 * 上传文件方法
	 * 
	 * @param FileName
	 *            文件名称
	 * @param inputStream
	 *            字符流
	 * @return 文件全路径
	 */
	public Map<String, String> uploadFile(String bucketName, String objectKey, InputStream inputStream) {
		Map<String, String> resultMap = Maps.newHashMap();
		if (null == endpoint || null == accessKeyId || null == accessKeySecret) {
			if (logger.isErrorEnabled()) {
				logger.error("OSS认证信息设置有误，请检查系统配置维护");
			}
			resultMap.put("code", ERROR);
			resultMap.put("msg", "OSS认证信息设置有误，请检查系统配置维护");
			return resultMap;
		}

		OSSClient ossClient = new OSSClient(endpoint, accessKeyId, accessKeySecret);

		try {
			// 如果bucket不存在则新建
			if (!ossClient.doesBucketExist(bucketName)) {
				ossClient.createBucket(bucketName);
				CreateBucketRequest createBucketRequest = new CreateBucketRequest(bucketName);
				createBucketRequest.setCannedACL(bucketName == WgFndUtil.WG_PRIVATE_BUCKET
						? CannedAccessControlList.Private : CannedAccessControlList.PublicRead);// 公共读：对object写操作需要进行身份验证；可以对object进行匿名读。
				ossClient.createBucket(createBucketRequest);
			}
			// 上传文件流
			ossClient.putObject(bucketName, objectKey, inputStream);
			resultMap.put("code", SUCCESS);
			resultMap.put("url",  proxyAddr + '/' + objectKey);
		} catch (OSSException oe) {
			if (logger.isErrorEnabled()) {
				logger.error("Caught an OSSException, which means your request made it to OSS, "
						+ "but was rejected with an error response for some reason.: ", oe.getErrorCode());
			}
			resultMap.put("code", ERROR);
			resultMap.put("msg", oe.getErrorMessage());
			return resultMap;
		} catch (ClientException ce) {
			if (logger.isErrorEnabled()) {
				logger.error("Caught an ClientException, which means the client encountered "
						+ "a serious internal problem while trying to communicate with OSS, "
						+ "such as not being able to access the network.: ", ce.getMessage());
			}
			resultMap.put("code", ERROR);
			resultMap.put("msg", ce.getMessage());
			return resultMap;
		} finally {
			ossClient.shutdown();
		}
		return resultMap;
	}

	/**
	 * 删除文件
	 * @param bucketName
	 * @param objectKey
	 */
	public void removeFile(String bucketName, String objectKey) {
		if (null == endpoint || null == accessKeyId || null == accessKeySecret) {
			if (logger.isErrorEnabled())
				logger.error("OSS认证信息设置有误，请检查系统配置维护");
		}

		OSSClient ossClient = new OSSClient(endpoint, accessKeyId, accessKeySecret);

		try {
			// 删除OSS对象
			ossClient.deleteObject(bucketName, objectKey);
		} catch (OSSException oe) {
			if (logger.isErrorEnabled()) {
				logger.error("Caught an OSSException, which means your request made it to OSS, "
						+ "but was rejected with an error response for some reason.: ", oe.getErrorCode());
			}
		} catch (ClientException ce) {
			if (logger.isErrorEnabled()) {
				logger.error("Caught an ClientException, which means the client encountered "
						+ "a serious internal problem while trying to communicate with OSS, "
						+ "such as not being able to access the network.: ", ce.getMessage());
			}
		} finally {
			ossClient.shutdown();
		}
	}

	/**
	 * 获取下载OSS文件
	 * @param request
	 * @param response
	 * @param fileObject 文件对象
	 * @throws IOException
	 */
	public void downloadFile(HttpServletRequest request, HttpServletResponse response, FileObject fileObject)
			throws IOException {

		if (null == endpoint || null == accessKeyId || null == accessKeySecret) {
			if (logger.isErrorEnabled())
				logger.error("OSS认证信息设置有误，请检查系统配置维护");
		}

		OSSClient ossClient = new OSSClient(endpoint, accessKeyId, accessKeySecret);

		if (!ossClient.doesObjectExist(fileObject.getBucketName(), fileObject.getObjectKey())) {
			response.getWriter().write(FILE_NOT_EXSIT);
		} else {
			try {
				// 获取OSS对象
				OSSObject ossObject = ossClient.getObject(fileObject.getBucketName(), fileObject.getObjectKey());
				// 获取数据流
				InputStream inputStream = ossObject.getObjectContent();

				// 设置Response相应对象
				response.setContentType(fileObject.getObjectType() + ";charset=" + ENC);
				response.setHeader("Accept-Ranges", "bytes");
				response.setHeader("Content-disposition","attachment;" + processFileName(request, fileObject.getObjectName()));
				Long fileLength = fileObject.getObjectSize();
				response.setContentLengthLong(fileLength);
				if (fileLength > 0) {
					byte[] buf = new byte[BUFFER_SIZE];
					try (ServletOutputStream outputStream = response.getOutputStream()) {
						int readLength;
						while (((readLength = inputStream.read(buf)) != -1)) {
							outputStream.write(buf, 0, readLength);
						}
						outputStream.flush();
						outputStream.close();
					}
				}
				inputStream.close();
				// 关闭连接
				ossClient.shutdown();
			} catch (Exception ex) {
				response.getWriter().write(FILE_NOT_EXSIT);
			}
		}
	}
	
	/**
	 * 设置文件对象链接控制权限ACL
	 * @param bucketName
	 * @param objectKey
	 * @param isPrivate
	 */
	public void setFileObjectACL(String bucketName, String objectKey,String isPrivate) {
		if (null == endpoint || null == accessKeyId || null == accessKeySecret) {
			if (logger.isErrorEnabled())
				logger.error("OSS认证信息设置有误，请检查系统配置维护");
		}

		OSSClient ossClient = new OSSClient(endpoint, accessKeyId, accessKeySecret);

		try {
			//设置OSS对象ACL
			ossClient.setObjectAcl(bucketName, objectKey, isPrivate == "Y" ? CannedAccessControlList.Private : CannedAccessControlList.PublicRead);
		} catch (OSSException oe) {
			if (logger.isErrorEnabled()) {
				logger.error("Caught an OSSException, which means your request made it to OSS, "
						+ "but was rejected with an error response for some reason.: ", oe.getErrorCode());
			}
		} catch (ClientException ce) {
			if (logger.isErrorEnabled()) {
				logger.error("Caught an ClientException, which means the client encountered "
						+ "a serious internal problem while trying to communicate with OSS, "
						+ "such as not being able to access the network.: ", ce.getMessage());
			}
		} finally {
			ossClient.shutdown();
		}
	}
	
	
}
