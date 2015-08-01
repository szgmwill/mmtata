package com.mmtata.im.server.util;

import java.math.BigDecimal;

import org.apache.commons.lang.StringUtils;

public class PriceUtils {
	
	public static boolean isValidPrice(String price) {
		if(StringUtils.isBlank(price)) {
			return false;
		}
		
		try {
			BigDecimal validPrice = new BigDecimal(price);
			if(validPrice.compareTo(new BigDecimal(0)) > 0) {
				return true;
			}
		} catch (Exception e) {
			
		}
		return false;
	}
	
	public static boolean checkValidPrice(String price) {
		if(StringUtils.isBlank(price)) {
			return false;
		}
		
		try {
			BigDecimal validPrice = new BigDecimal(price);
			if(validPrice.compareTo(new BigDecimal(0)) >= 0) {
				return true;
			}
		} catch (Exception e) {
			
		}
		return false;
	}
	
	public static void main(String[] args) {
		String test = "0.0";
		
		System.out.println("ret :" + PriceUtils.isValidPrice(test));
	}
}
