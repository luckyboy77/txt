package tt.cxf.util;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 * @name XmlUtil
 * @description XmlUtil,封装了一些以DOM方式获得Xml对象的方法 。<br/><b>仅mulThreadParse方法是线程安全的</b>
 * @author xupf
 * @version 1.0
 */
public class XmlUtil {
	
	private static DocumentBuilder builder;
	private static DocumentBuilderFactory factory;
	
	private XmlUtil(){}
	static{
		factory=DocumentBuilderFactory.newInstance();
		try {
			builder= factory.newDocumentBuilder();
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 通过一个InputSource来创建Xml文档对象
	 * @author gaokuo.dai@hand-china.com
	 * 2017年5月16日
	 * 
	 * @param inputSource 包含Xml文本信息的InputSource
	 * @return 文档对象
	 * @throws SAXException
	 * @throws IOException
	 */
	public static final Document parse(InputSource inputSource) throws SAXException, IOException{
		return builder.parse(inputSource);
	}
	
	/**
	 * 通过一个InputStream来创建Xml文档对象
	 * @author gaokuo.dai@hand-china.com
	 * 2017年5月16日
	 * 
	 * @param inputStream 包含Xml文本信息的InputStream
	 * @return Xml文档对象
	 * @throws SAXException
	 * @throws IOException
	 */
	public static final Document parse(InputStream inputStream) throws SAXException, IOException{
		return builder.parse(inputStream);
	}
	
	/**
	 * 通过一个File来创建Xml文档对象
	 * @author gaokuo.dai@hand-china.com
	 * 2017年5月16日
	 * 
	 * @param file 包含Xml文本信息的File对象
	 * @return Xml文档对象
	 * @throws SAXException
	 * @throws IOException
	 */
	public static final Document parse(File file) throws SAXException, IOException{
		return builder.parse(file);
	}
	
	/**
	 * 通过一个url来创建Xml文档对象<br/>
	 * <b style="color:red;">注意此方法不是解析String类型Xml文本的方法,若想解析,请将String对象转化为InputStream</b>
	 * @author gaokuo.dai@hand-china.com
	 * 2017年5月16日
	 * 
	 * @param url 获得Xml文本的URL
	 * @return Xml文档对象
	 * @throws SAXException
	 * @throws IOException
	 */
	public static final Document parse(String url) throws SAXException, IOException{
		return builder.parse(url.replaceAll(" ", "%20"));
	}
	
	/**
	 * 可用于线程安全的解析函数<br/>
	 * <i>参数类型说明参考其他parse函数</i>
	 * @author gaokuo.dai@hand-china.com
	 * 2017年5月16日
	 * 
	 * @param param 参数，必须是String/File/InputStream/InputSource中的一种
	 * @return Xml文档对象
	 * @throws SAXException
	 * @throws IOException
	 * @throws ParserConfigurationException
	 */
	public static final Document mulThreadParse(Object param) throws SAXException, IOException, ParserConfigurationException{
		if(param instanceof String){
			return factory.newDocumentBuilder().parse((String)param);
		}
		if(param instanceof File){
			return factory.newDocumentBuilder().parse((File)param);
		}
		if(param instanceof InputStream){
			return factory.newDocumentBuilder().parse((InputStream)param);
		}
		if(param instanceof InputSource){
			return factory.newDocumentBuilder().parse((InputSource)param);
		}
		throw new IllegalArgumentException("不接受此类型的参数");
	}
}
