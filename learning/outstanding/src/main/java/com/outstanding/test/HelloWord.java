package com.outstanding.test;

import java.util.Date;

public class HelloWord {

	/**
	 * @param args
	 * @throws InterruptedException 
	 */
	public static void main(String[] args) throws InterruptedException {
		for (int i = 0; i < 10; i++) {
			main1();
			Thread.currentThread().sleep(1000);
		}
		
	}

	public static void main1() {
		// TODO Auto-generated method stub
		Date date = new Date();
		long timel=date.getTime()/1000;
		System.out.println(timel);
	}
}
