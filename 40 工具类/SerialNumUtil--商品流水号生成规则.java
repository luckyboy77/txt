package com.hand.hap.util;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.hand.hap.constants.Constants;
import com.hand.hap.order.dto.SerialNumDO;
/**
 * 
 * @author sudong
 *
 */
public class SerialNumUtil {
	/**
	 * 生成流水号，例如：默认编码格式为:P或B+六位年月日+四位流水，如：P1609050001
	 * @param type
	 * @param serialNumDO
	 * @return
	 */
	public  static SerialNumDO generaterNextNumber(String type,SerialNumDO serialNumDO) {
        String serialNum = null;
        String sno=null,generateDate=null;
        Date date = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("yyMMdd");
        
        SerialNumDO serialNumber=new SerialNumDO();
        
        String today=formatter.format(date);
		if(serialNumDO!=null){
			sno=serialNumDO.getSno();
			generateDate=serialNumDO.getGenerateDate();
			serialNumber.setInsert(false);
		}else{
			serialNumber.setInsert(true);
		}
        if(generateDate==null&&(sno==null||sno=="")){
        	serialNum=type+formatter.format(date)+"0001";
        	sno="0001";
        }else{
        DecimalFormat df = new DecimalFormat("0000");
        if(!today.equals(generateDate)){
        	sno="0001";
        }else{
        	sno=df.format(Integer.parseInt(sno)+1);
        }
        serialNum=type+today+sno;
        }
       
        serialNumber.setSno(sno);
        serialNumber.setGenerateDate(today);
        serialNumber.setSerialNumber(serialNum);
        if(serialNumDO!=null){
            serialNumber.setUserName(serialNumDO.getUserName());
        }
        serialNumber.setType(Constants.HEAD);
        return serialNumber;
    }
	
	/**
	 * 订单编号+三位流水号，例如：P1609050007+001
	 * @param type
	 * @param serialNumDO
	 * @return
	 */
	public  static SerialNumDO generaterNextSerialNumber(String serialNum,SerialNumDO serialNumDO) {
        String sno=null,generateDate=null;
        Date date = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("yyMMdd");
        String today=formatter.format(date);
        SerialNumDO serialNumber=new SerialNumDO();
		if(serialNumDO!=null){
			sno=serialNumDO.getSno();
			generateDate=serialNumDO.getGenerateDate();
			serialNumber.setInsert(false);
		}else{
			serialNumber.setInsert(true);
		}
        if(generateDate==null&&(sno==null||sno=="")){
        	serialNum=serialNum+"001";
        	sno="001";
        }else{
        String strSerialNumber=serialNumDO.getSerialNumber();
        if(strSerialNumber.length()==14){
        	strSerialNumber=strSerialNumber.substring(0, strSerialNumber.length()-3);
        }
        DecimalFormat df = new DecimalFormat("000");
        if(!serialNum.equals(strSerialNumber)){
        	sno="001";
        }else{
        	sno=df.format(Integer.parseInt(sno)+1);
        }
        serialNum=serialNum+sno;
        }
        //SerialNumDO serialNumber=new SerialNumDO();
        serialNumber.setSno(sno);
        serialNumber.setGenerateDate(today);
        serialNumber.setSerialNumber(serialNum);
        if(serialNumDO!=null){
            serialNumber.setUserName(serialNumDO.getUserName());
            }
        serialNumber.setType(Constants.LINE);
        return serialNumber;
    }
	
	
	
	/**
	 * 订单编号+三位流水号，例如：P1609050007+001,适用于查出订单后，
	 * 行上已经有订单行编号比如：P1609050007004，
	 * 在新增时应该是P1609050007005，而不是P1609050007001
	 * @param type
	 * @param serialNumDO
	 * @return
	 */
	public  static SerialNumDO NextSerialNumberAfterQuery(String serialNum,boolean insertOrupdate,SerialNumDO serialNumDO) {
        String sno=null;
        Date date = new Date();
        DecimalFormat df = new DecimalFormat("000");
        SimpleDateFormat formatter = new SimpleDateFormat("yyMMdd");
        String today=formatter.format(date);
        SerialNumDO serialNumber=new SerialNumDO();
		sno=serialNumDO.getSno();
		serialNumber.setInsert(insertOrupdate);
        sno=df.format(Integer.parseInt(sno)+1);
        serialNum=serialNum+sno;
        //SerialNumDO serialNumber=new SerialNumDO();
        serialNumber.setSno(sno);
        serialNumber.setGenerateDate(today);
        serialNumber.setSerialNumber(serialNum);
        if(serialNumDO!=null){
            serialNumber.setUserName(serialNumDO.getUserName());
            }
        serialNumber.setType(Constants.DB);
        return serialNumber;
    }
	
	/**
	 * 发货单据流水号，逻辑为时间(6位)+流水号（4位）（例如：1609050001）
	 * @param serialNumDO
	 * @return
	 */
	public  static SerialNumDO generateNextSerial(String type,SerialNumDO serialNumDO) {
		
		SerialNumDO serialNumber=new SerialNumDO();
		
        String sno=null,generateDate=null;
        Date date = new Date();String serialNum="";
        SimpleDateFormat formatter = new SimpleDateFormat("yyMMdd");
        String day=formatter.format(date);
		if(serialNumDO!=null){
			sno=serialNumDO.getSno();
			generateDate=serialNumDO.getGenerateDate();
			serialNumber.setInsert(false);
		}else{
			serialNumber.setInsert(true);
		}
		
		
		DecimalFormat df = new DecimalFormat("0000");
        if(!day.equals(generateDate)||sno==null||sno==""){
        	sno="0001";
        }else{
        	sno=df.format(Integer.parseInt(sno)+1);
        }
        serialNum=type+day+sno;
        serialNumber.setSno(sno);
        serialNumber.setGenerateDate(day);
        serialNumber.setSerialNumber(serialNum);
        if(serialNumDO!=null){
        serialNumber.setUserName(serialNumDO.getUserName());
        }
        serialNumber.setType(Constants.SEND);
        return serialNumber;
    }
}
