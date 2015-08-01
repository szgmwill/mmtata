/**
 * 深圳市塔塔互动
 * 作者:张伟锐
 * 2015年5月17日
 */
package com.mmtata.im.server.status;

public enum Sex {
	BOY, GIRL;
	
	public static Sex getSex(String input) {
		for(Sex sex : values()) {
			if(sex.name().equals(input)) {
				return sex;
			}
		}
		
		return null;
	}
}
