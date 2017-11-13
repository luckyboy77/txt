package com.wdw.common.util;

import java.net.URL;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.aliyun.oss.OSSClient;
import com.aliyun.oss.common.utils.BinaryUtil;
import com.aliyun.oss.model.MatchMode;
import com.aliyun.oss.model.PolicyConditions;
import com.wdw.common.exception.WdwException;
import com.wdw.enums.RespCodeEnum;

import net.sf.json.JSONObject;

/**
 * oss工具类
 * @author ljf
 * 2017-10-17
 */
public class OssUtil {
	// 终端
	private static final String END_POINTER = "oss-cn-shanghai.aliyuncs.com";
	
	// ACCESS_ID
	private static final String ACCESS_ID = "LTAIpHxoGym2Yodl";
	
	// ACCESS_KEY
	private static final String ACCESS_KEY = "wkE60t19VoVWC3b3gJF6Eu5HNryNug";
	
	// 空间名
	private static final String BUCKET_NAME = "wangdewang";
	
	/**
	 * 生成一个签名的URL
	 * @author ljf
	 * 2017-10-24
	 * @param bucketName 空间名
	 * @param objectKey 对象名
	 * @return
	 */
	public static String getAuthorizedUrl(String objectKey){
		// 创建OSSClient实例
		OSSClient client = new OSSClient(END_POINTER, ACCESS_ID, ACCESS_KEY);
		// 设置URL过期时间为1小时
		Date expiration = new Date(new Date().getTime() + 3600 * 1000);
		// 生成URL
		URL url = client.generatePresignedUrl(BUCKET_NAME, objectKey, expiration);
		return url.toString();
	}
	
	/**
	 * 获取上传凭证
	 * @author ljf
	 * 2017-10-24
	 * @param dir 子文件夹
	 * @param request
	 * @param response
	 * @throws WdwException
	 */
	public static JSONObject getUploadPolicy(String dir,HttpServletRequest request,HttpServletResponse response ) throws WdwException {
		String host = "http://" + BUCKET_NAME + "." + END_POINTER;
		// 创建OSSClient实例
        OSSClient client = new OSSClient(END_POINTER, ACCESS_ID, ACCESS_KEY);
        try { 	
        	long expireTime = 30;
        	long expireEndTime = System.currentTimeMillis() + expireTime * 1000;
            Date expiration = new Date(expireEndTime);
            PolicyConditions policyConds = new PolicyConditions();
            policyConds.addConditionItem(PolicyConditions.COND_CONTENT_LENGTH_RANGE, 0, 1048576000);
            policyConds.addConditionItem(MatchMode.StartWith, PolicyConditions.COND_KEY, dir);

            String postPolicy = client.generatePostPolicy(expiration, policyConds);
            byte[] binaryData = postPolicy.getBytes("utf-8");
            String encodedPolicy = BinaryUtil.toBase64String(binaryData);
            String postSignature = client.calculatePostSignature(postPolicy);
            
            Map<String, String> respMap = new LinkedHashMap<String, String>();
            respMap.put("accessid", ACCESS_ID);
            respMap.put("policy", encodedPolicy);
            respMap.put("signature", postSignature);
            //respMap.put("expire", formatISO8601Date(expiration));
            respMap.put("dir", dir);
            respMap.put("host", host);
            respMap.put("expire", String.valueOf(expireEndTime / 1000));
            JSONObject ja1 = JSONObject.fromObject(respMap);
            System.out.println(ja1.toString());
            return ja1;
            
        } catch (Exception e) {
            throw new WdwException(RespCodeEnum.CUSTOM_ERROR.getCode(), "获取上传凭证失败");
        }
	}
	
	/**
	 * 删除文件
	 * @author ljf
	 * 2017-10-24
	 * @param key 文件名（子文件夹名/文件名）
	 */
	public static void delFile(String key){
		// 创建OSSClient实例
		OSSClient ossClient = new OSSClient(END_POINTER, ACCESS_ID, ACCESS_KEY);
		// 删除Object
		ossClient.deleteObject(BUCKET_NAME, key);
		// 关闭client
		ossClient.shutdown();
	}
}
