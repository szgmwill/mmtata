package com.mmtata.im.server.status;
/**
 * 服务订单状态
 * @author 张伟锐
 *
 */
public enum OrderStatus {
	NEW, PAID, FAIL, TIMEOUT, DONE;
	
	
	public static OrderStatus getStatus(String input) {
		for(OrderStatus status : values()) {
			if(status.name().equalsIgnoreCase(input)) {
				return status;
			}
		}
		
		return null;
	}
}
