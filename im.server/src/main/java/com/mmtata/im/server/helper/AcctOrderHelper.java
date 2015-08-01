package com.mmtata.im.server.helper;

import java.math.BigDecimal;
import java.util.Date;

import com.mmtata.im.server.bean.AcctOrder;
import com.mmtata.im.server.status.FeeUnit;
import com.mmtata.im.server.util.DateUtils;

public class AcctOrderHelper {
	
	/**
	 * 计算订单服务到期时间
	 */
	public static Date getExpireTime(AcctOrder order) {
		if(order != null) {
			int buyNum = order.getBuy_num();//购买数量
			FeeUnit unit = order.getFee_unit();//服务时长单位
			int duration = order.getFee_duration();//服务时长
			
			long serviceTime = buyNum*FeeUnit.getTimeMillis(unit)*duration;
			
			if(serviceTime > 0) {
				return new Date(System.currentTimeMillis()+serviceTime);
			}
			
		}
		return null;
	}
	
	/**
	 * 计算订单总价
	 */
	public static BigDecimal getOrderTotalAmt(AcctOrder order) {
		if(order != null) {
			int buyNum = order.getBuy_num();
			
			BigDecimal fee = order.getFee();
			
			if(buyNum > 0 && fee != null) {
				return fee.multiply(new BigDecimal(buyNum));
			}
			
		}
		
		return new BigDecimal(0);
	}
	
	/**
	 * 判断订单服务是否有效(即还没有服务结束到点)
	 */
	public static boolean isOrderServing() {
		
		
		return false;
	}
	
	public static void main(String[] args) {
		AcctOrder order = new AcctOrder();
		order.setBuy_num(4);
		order.setFee(new BigDecimal(10));
		order.setFee_unit(FeeUnit.hour);
		order.setFee_duration(2);
		
		String expire = DateUtils.getTimeStrFromDate(AcctOrderHelper.getExpireTime(order));
		System.out.println("expire:"+expire);
		
		BigDecimal total = AcctOrderHelper.getOrderTotalAmt(order);
		
		System.out.println("total:"+total);
	}
}
