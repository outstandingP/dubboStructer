package com.outstanding.test;

/**
 * Created by songll on 2017/5/25.
 */
public class IntegerClass {
	public static void main(String[] args) {
		Integer i1 = new Integer(123);
		Integer i2 = new Integer(123);
		System.out.println(i1 == i2);		//比较的是内存地址
		System.out.println(i1.equals(i2));	//equals底层会将包装类型转换为基本数据类型来进行比较

		Integer i3 = Integer.valueOf(250);
		Integer i4 = Integer.valueOf(250);
		System.out.println(i3 == i4);		//包装类中比较的是缓存中的数据，因为250不是“-128”-“127”之间，所以是false
		System.out.println(i3.equals(i4));  //equals底层会将包装类型转换为基本数据类型来进行比较

		Integer i5 = 125;
		Integer i6 = 125;
		System.out.println(i5 == i6);		//是自动装配，和第二段比较的是一样的。
		System.out.println(i5.equals(i6));	//equals底层会将包装类型转换为基本数据类型来进行比较

	}
}