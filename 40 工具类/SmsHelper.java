package wg.fnd.utils;

import com.aliyun.mns.client.CloudAccount;
import com.aliyun.mns.client.CloudTopic;
import com.aliyun.mns.client.MNSClient;
import com.aliyun.mns.common.ServiceException;
import com.aliyun.mns.model.BatchSmsAttributes;
import com.aliyun.mns.model.MessageAttributes;
import com.aliyun.mns.model.RawTopicMessage;
import com.google.common.collect.Maps;
import com.hand.hap.message.profile.GlobalProfileListener;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

/**
 * 短信处理消息
 * @name SmsHelper
 * @description 
 * @author xianzhi.chen@hand-china.com	2017年3月6日下午1:16:43
 * @version
 */
public class SmsHelper implements GlobalProfileListener{
	
	/**
	 * 成功标识
	 */
	protected final String SUCCESS = "S";
	/**
	 * 失败标识
	 */
	protected final String ERROR = "E";
	
	private final Logger logger = LoggerFactory.getLogger(SmsHelper.class);
	
	private String secret;
	// EndPoint地址
	private String aliyunEndpoint;
	// 主题名称
	private String smsTopic;
	// accessKeyId
	private String accessKeyId;
	// accessKeySecret
	private String accessKeySecret;

	@Override
	public List<String> getAcceptedProfiles() {
		return Arrays.asList("WG_FND_SMS_SECRET");//配置阿里云SMS权限（accessKeyId,secret）
	}

	@Override
	public void updateProfile(String profileName, String profileValue) {
		this.secret = profileValue;
		String[] secrets = this.secret.split(",");
		if (secrets.length >= 4) {
			this.aliyunEndpoint = secrets[0];
			this.smsTopic = secrets[1];
			this.accessKeyId = secrets[2];
			this.accessKeySecret = secrets[3];
		}
	}
	
	/**
	 * 发送消息方法
	 * @param recNum 移动手机号
	 * @param paramMap 参数Map
	 * @return Map code:msg, code为S:处理成功，E:处理失败，msg会出现错误信息
	 */
	public Map<String, String> sendMessage(String recNum,String signName,String templateCode,Map<String,String> paramMap){
		Map<String, String> resultMap = Maps.newHashMap();
		if (null == aliyunEndpoint || null == smsTopic || null == accessKeyId || null == accessKeySecret || StringUtils.isBlank(signName) || StringUtils.isBlank(templateCode)) {
			if (logger.isErrorEnabled()) {
				logger.error("SMS认证信息设置有误，请检查系统配置维护");
			}
			resultMap.put("code", ERROR);
			resultMap.put("msg", "SMS认证信息设置有误，请检查系统配置维护");
			return resultMap;
		}
		/**
         * Step 1. 获取主题引用
         */
        CloudAccount account = new CloudAccount(accessKeyId, accessKeySecret,aliyunEndpoint);
        MNSClient client = account.getMNSClient();
        CloudTopic topic = client.getTopicRef(smsTopic);
        /**
         * Step 2. 设置SMS消息体（必须）
         *
         * 注：目前暂时不支持消息内容为空，需要指定消息内容，不为空即可。
         */
        RawTopicMessage msg = new RawTopicMessage();
        msg.setMessageBody("sms-message");
        /**
         * Step 3. 生成SMS消息属性
         */
        MessageAttributes messageAttributes = new MessageAttributes();
        BatchSmsAttributes batchSmsAttributes = new BatchSmsAttributes();
        // 3.1 设置发送短信的签名（SMSSignName）
        batchSmsAttributes.setFreeSignName(signName);
        // 3.2 设置发送短信使用的模板（SMSTempateCode）
        batchSmsAttributes.setTemplateCode(templateCode);
        // 3.3 设置发送短信所使用的模板中参数对应的值（在短信模板中定义的，没有可以不用设置）
        BatchSmsAttributes.SmsReceiverParams smsReceiverParams = new BatchSmsAttributes.SmsReceiverParams();
        Iterator<Entry<String, String>> entries = paramMap.entrySet().iterator();  
        while (entries.hasNext()) {  
			Entry<String, String> entry = (Entry<String, String>) entries.next();  
			smsReceiverParams.setParam(entry.getKey(),entry.getValue());// 短信模板中的变量；数字需要转换为字符串；个人用户每个变量长度必须小于15个字符。" 
		}  
        // 3.4 增加接收短信的号码
        batchSmsAttributes.addSmsReceiver(recNum, smsReceiverParams);
        messageAttributes.setBatchSmsAttributes(batchSmsAttributes);

		logger.debug("短信消息："+batchSmsAttributes);
        /**
         * Step 4. 发布SMS消息
         */
        try {
            topic.publishMessage(msg, messageAttributes);
            resultMap.put("code", SUCCESS);
        } catch (ServiceException ex) {
        	logger.error(ex.getErrorCode() +":"+ ex.getMessage());
			resultMap.put("code", ERROR);
			resultMap.put("msg", ex.getErrorCode() +":"+ ex.getMessage());
        } catch (Exception e) {
        	logger.error(e.getMessage());
			resultMap.put("code", ERROR);
			resultMap.put("msg", e.getMessage());
        }finally{
        	if(client.isOpen()){
        		client.close();
        	}
        }
		return resultMap;
	}
	
}
