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
//                System.out.println("ʱ��=" + new Date() + " ִ����" + count + "��"); // 1��
//            	MailUitls.sendMail("1048593688@qq.com");
            	MailUtils.sendMail(mail);
            }
        };

        //����ִ��ʱ��
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);//ÿ��
//        int year1 = calendar.get(Calendar.YEAR);
//        int month1 = calendar.get(month);
//        int day1 = calendar.get(Calendar.DATE);
//        int hour1 = calendar.get(hour);
//        int minunt1 = calendar.get(minunt);
//        int second1 = calendar.get(second);
        //����ÿ���21:09:00ִ�У�
        calendar.set(year, month, day, hour, minunt, second);
        Date date = calendar.getTime();
        Timer timer = new Timer();
//      System.out.println(date);
//        
//      int period = 2 * 1000;
//      //ÿ���dateʱ��ִ��task��ÿ��2���ظ�ִ��
//      timer.schedule(task, date, period);
//      // ÿ���dateʱ��ִ��task, ��ִ��һ��
        timer.schedule(task, date);
    }

}
