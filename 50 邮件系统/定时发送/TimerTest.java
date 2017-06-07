package com.sunshine.test;

import java.util.Calendar;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import com.sunshine.util.Mail;

public class TimerTest {
	public static void main(String[] args) {
//		Timer timer = new Timer();
//		TestTimer test = new TestTimer();
//		timer.schedule(test, 0, 3000);
		Mail mail = new Mail();
		mail.setTitle("1048593688@qq.com");
		mail.setTitle("sadfa");
		mail.setArea("adfjajdkfjaoef");
		TestTimer.showTimer(14, 24, 00, mail);
	}
}
