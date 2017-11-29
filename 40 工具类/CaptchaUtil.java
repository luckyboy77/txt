package com.password.utils;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Random;

import javax.imageio.ImageIO;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/*import com.sun.image.codec.jpeg.JPEGCodec;
import com.sun.image.codec.jpeg.JPEGImageEncoder;*/

/**
 * 验证码生成  工具类
 * @author zhusj
 * 2017-09-13
 */
public final class CaptchaUtil {

	private CaptchaUtil(){}
	
	
	//随机字符字典
	private static final char[] CHARS = { '2', '3', '4', '5', '6', '7', '8',
	        '9', 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'J', 'K', 'L', 'M',
	        'N', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z' };

	//随机数
	private static Random random = new Random();
	
	//获取6位随机数
	private static String getRandomString()
    {
		
        StringBuffer buffer = new StringBuffer();
        for(int i = 0; i < 6; i++)
        {
            buffer.append(CHARS[random.nextInt(CHARS.length)]);
        }
        
        return buffer.toString();
    }
	
	
	//获取随机数颜色
	private static Color getRandomColor()
    {
        return new Color(random.nextInt(255),random.nextInt(255),
                random.nextInt(255));
    }
	
	//返回某颜色的反色
	private static Color getReverseColor(Color c)
    {
        return new Color(255 - c.getRed(), 255 - c.getGreen(),
                255 - c.getBlue());
    }
    
	/**
	 * 生成验证码
	 * @param request
	 * @param response
	 * @throws ServletException
	 * @throws IOException
	 */
    public static void outputCaptchaPrimary(HttpServletRequest request, HttpServletResponse response,String key)throws ServletException, IOException 
    {

        response.setContentType("image/jpeg");

        String randomString = getRandomString();
        // 将验证码存入redis
        RedisUtil.getInstance().strings().set(key, randomString);

        int width = 100;
        int height = 30;

        Color color = getRandomColor();
        Color reverse = getReverseColor(color);

        BufferedImage bi = new BufferedImage(width, height,
                BufferedImage.TYPE_INT_RGB);
        Graphics2D g = bi.createGraphics();
        g.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 16));
        g.setColor(color);
        g.fillRect(0, 0, width, height);
        g.setColor(reverse);
        g.drawString(randomString, 18, 20);
        for (int i = 0, n = random.nextInt(100); i < n; i++) 
        {
            g.drawRect(random.nextInt(width), random.nextInt(height), 1, 1);
        }
        
        // 转成JPEG格式
        ServletOutputStream out = response.getOutputStream();
        /*JPEGImageEncoder encoder = JPEGCodec.createJPEGEncoder(out);
        encoder.encode(bi);*/
        ImageIO.write(bi, "jpg", out);
        out.flush();
    }	
    
    
    private static final int CAPTCHA_WIDTH = 90;
    private static final int CAPTCHA_HEIGHT = 32;
    private static final int CAPTCHA_CODE_COUNT = 5;
    //private static final int CAPTCHA_CODE_X = 19;// 15
    private static final int CAPTCHA_CODE_Y = 24;
    private static final int CAPTCHA_FONT_HEIGHT = 22;
    //private static final int CAPTCHA_EXPIRE = 60 * 5;

    private static final int LINE_COUNT = 30;// 40
    private static final int LINE_DY = 12;
    private static final int MAX_RGB = 255;

    private static final char[] CAPTCHA_CODES = "ABCDEFGHJKLMNPQRSTUVWXYZ23456789".toCharArray();
    
    /**
     * 生成captchaCode数字.
     * @return 随机生成的验证码
     */
    private static String generateCaptchaCode() {
        // randomCode用于保存随机产生的验证码，以便用户登录后进行验证。
        StringBuilder randomCode = new StringBuilder();
        // 创建一个随机数生成器类
        Random random = new Random();
        // 随机产生codeCount数字的验证码。
        for (int i = 0; i < CAPTCHA_CODE_COUNT; i++) {
            // 得到随机产生的验证码数字。
            String code = String.valueOf(CAPTCHA_CODES[random.nextInt(CAPTCHA_CODES.length)]);
            // 将产生的四个随机数组合在一起。
            randomCode.append(code);
        }
        return randomCode.toString();
    }
    
    /**
     * 验证码图片,较清晰的版本
     * @param key
     * @date        2017年11月20日上午11:51:06
     */
    public static void outputCaptcha(HttpServletRequest request, HttpServletResponse response,String key) throws ServletException, IOException 
    {
        response.setContentType("image/jpeg");
        String captchaCode = generateCaptchaCode();

        // 定义图像buffer
        BufferedImage buffImg = new BufferedImage(CAPTCHA_WIDTH, CAPTCHA_HEIGHT, BufferedImage.TYPE_INT_RGB);
        // Graphics2D gd = buffImg.createGraphics();
        // Graphics2D gd = (Graphics2D) buffImg.getGraphics();
        Graphics gd = buffImg.getGraphics();
        // 创建一个随机数生成器类
        Random random = new Random();
        // 将图像填充为白色
        gd.setColor(Color.WHITE);
        gd.fillRect(0, 0, CAPTCHA_WIDTH, CAPTCHA_HEIGHT);

        // 创建字体，字体的大小应该根据图片的高度来定。
        Font font = new Font("Fixedsys", Font.BOLD, CAPTCHA_FONT_HEIGHT);
        // 设置字体。
        gd.setFont(font);

        // 画边框。
//        gd.setColor(Color.BLACK);
        gd.drawRect(0, 0, CAPTCHA_WIDTH - 1, CAPTCHA_HEIGHT - 1);

        // 随机产生40条干扰线，使图象中的认证码不易被其它程序探测到。
        gd.setColor(Color.BLACK);
        for (int i = 0; i < LINE_COUNT; i++) {
            int x = random.nextInt(CAPTCHA_WIDTH);
            int y = random.nextInt(CAPTCHA_HEIGHT);
            int xl = random.nextInt(LINE_DY);
            int yl = random.nextInt(LINE_DY);
            gd.drawLine(x, y, x + xl, y + yl);
        }

        // randomCode用于保存随机产生的验证码，以便用户登录后进行验证。
        // StringBuffer randomCode = new StringBuffer();
        int red = 0, green = 0, blue = 0;

        // 随机产生codeCount数字的验证码。
        int sw = Math.floorDiv(CAPTCHA_WIDTH, captchaCode.length());
        for (int i = 0; i < captchaCode.length(); i++) {
            // 得到随机产生的验证码数字。
            String code = String.valueOf(captchaCode.charAt(i));
            // 产生随机的颜色分量来构造颜色值，这样输出的每位数字的颜色值都将不同。
            red = random.nextInt(MAX_RGB);
            green = random.nextInt(MAX_RGB);
            blue = random.nextInt(MAX_RGB);

            // 用随机产生的颜色将验证码绘制到图像中。
            gd.setColor(new Color(red, green, blue));
            gd.drawString(code, i * sw, CAPTCHA_CODE_Y);
        }
        // 将四位数字的验证码保存到Session中。
        // 将验证码存入redis
        RedisUtil.getInstance().strings().set(key, captchaCode);
        // 将图像输出到输出流中。
        try (OutputStream ignored = response.getOutputStream()) {
            ImageIO.write(buffImg, "jpeg", ignored);
        }
        
    }	

}

