package com.tata.imta.util;

import android.text.TextUtils;

import java.math.BigDecimal;

public class PriceUtils {
	
	public static boolean isValidPrice(String price) {
		if(TextUtils.isEmpty(price)) {
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
		if(TextUtils.isEmpty(price)) {
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

	public static BigDecimal getValidPrice(String price) {
		if(TextUtils.isEmpty(price)) {
			return null;
		}

		try {
			BigDecimal validPrice = new BigDecimal(price);
			if(validPrice.compareTo(new BigDecimal(0)) > 0) {
				return validPrice;
			}
		} catch (Exception e) {

		}
		return null;
	}

	public static void main(String[] args) {
		String test = "0.0";
		
		System.out.println("ret :" + PriceUtils.isValidPrice(test));
	}
}
