package com.sunshine.test;

import java.util.Calendar;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import com.sunshine.util.Mail;
import com.sunshine.util.MailUitls;
import com.sunshine.util.MailUtils;

//public class TestTimer extends TimerTask{
public class TestTimer{

//	@Override
//	public void run() {
//		MailUitls.sendMail("1048593688@qq.com");
//	}
	
	static int count = 0;
    
   // public static void showTimer() {
    public static void showTimer(int hour,int minunt,int second,Mail mail) {
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
//                ++count;
//                System.out.println("时间=" + new Date() + " 执行了" + count + "次"); // 1次
//            	MailUitls.sendMail("1048593688@qq.com");
            	MailUtils.sendMail(mail);
            }
        };

        //设置执行时间
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);//每天
//        int year1 = calendar.get(Calendar.YEAR);
//        int month1 = calendar.get(month);
//        int day1 = calendar.get(Calendar.DATE);
//        int hour1 = calendar.get(hour);
//        int minunt1 = calendar.get(minunt);
//        int second1 = calendar.get(second);
        //定制每天的21:09:00执行，
        calendar.set(year, month, day, hour, minunt, second);
        Date date = calendar.getTime();
        Timer timer = new Timer();
//      System.out.println(date);
//        
//      int period = 2 * 1000;
//      //每天的date时刻执行task，每隔2秒重复执行
//      timer.schedule(task, date, period);
//      // 每天的date时刻执行task, 仅执行一次
        timer.schedule(task, date);
    }

}
