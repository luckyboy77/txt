package com.hand.hap.util;

import java.text.DecimalFormat;

import org.springframework.beans.factory.annotation.Autowired;

import com.hand.hap.constants.Constants;
import com.hand.hap.customer.dto.ProCityNumber;
import com.hand.hap.customer.mapper.ProCityNumberMapper;

public class ProCityNumUtil {
	
	@Autowired
	private static ProCityNumberMapper proCityNumberMapper;
	
	/**
	 * 生成流水号，客户编码，例如：C03101001
	 * @param type
	 * @param proCityNumber
	 * @return
	 */
	public static ProCityNumber generaterNextNumber(String type,ProCityNumber proCity,String cityNumber){
		
		String serialNum = null;
	    String sno=null,proCityNum=null;
	    
	    ProCityNumber proNumber = new ProCityNumber();
	    
	    if(proCity!=null){
	    	proCityNum = proCity.getProCityNumber();
	    	sno = proCity.getSno();
	    	proNumber.setInsert(false); 
	    }else{
	    	proNumber.setInsert(true);
	    }
	    if(proCityNum==null&&(sno==null||sno=="")){
	    	serialNum = type+cityNumber+"001";
	    	sno="001";
	    }else{
	    	DecimalFormat df = new DecimalFormat("000");
	    	//需要做一个判断，如果从数据库中查到了一样的proCityNum,那吗sno+1,否则sno=001
	    	if(proCityNum.equals(proCity.getProCityNumber())){
	    		sno=df.format(Integer.parseInt(sno)+1);
	    	}else{
	    		sno = "001";
	    	}
	    	serialNum = type+cityNumber+sno;
	    }
	    
	    proNumber.setSno(sno);
	    proNumber.setProCityNumber(cityNumber);
	    proNumber.setSerialNumber(serialNum);
	    proNumber.setType(Constants.CUSTOMER);
	    
		return proNumber;
	}
}
