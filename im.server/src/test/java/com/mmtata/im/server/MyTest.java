package com.mmtata.im.server;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.commons.lang.StringUtils;
/**
 * 求10^8以内所有指定规则数(特殊回文数)之和
 * 这个规则数应该是：
 * 1.至少两个以上数的平方之后; 比如：5 = 1^2 + 2^;  55=2^2 + 3^2 + 4^2 + 5^2;
 * 2.并且这个数是一个回文数
 */
public class MyTest {
	
	private static final long SUM = 1000000000;
	
	private static long result = 0;
	
	private static List<Long> resultList = new ArrayList<Long>();
	
	public static void main(String[] args) {
		long startTime = System.currentTimeMillis();
		sumReverse();
		
		
		System.out.println("final result:"+result);
		Collections.sort(resultList);
		for(Long num : resultList) {
			System.out.println("==> "+num);
		}
		System.out.println("total time:"+(System.currentTimeMillis() - startTime));
	}
	
	/**
	 * 计算所有目标回文数之和
	 */
	private static void sumReverse() {
		long curIndex = 1;
		
		while(true) {
			long square = curIndex * curIndex;
			if(square < SUM/2) {
				long input = curIndex;
				long tempSum = square;
				
				square(input, tempSum);
				
				curIndex++;
			} else {
				break;
			}
		}
	}
	
	private static void square(long input, long total) {
		input++;
		long temp = total + input*input;
		
		if(temp < SUM) {
			total = temp;
			//判断是否回文数
			if(isReverse(total) && !resultList.contains(total)) {//是回文数并且没有出现过
				resultList.add(total);
//				System.out.println("reverse num:"+total);
				result = result + total;
			}
			//递归
			square(input, total);
		}
//		System.out.println("input ====> "+input);
	}
	
	private static boolean isReverse(long data) {
		String inputStr = String.valueOf(data);
		if(StringUtils.reverse(inputStr).equals(inputStr)) {
			return true;
		}
		
		return false;
	}
}
