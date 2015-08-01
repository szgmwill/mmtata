package com.tata.imta.bean.status;

public enum FeeUnit {
	min("分钟"), hour("小时"), day("天"), week("周"), month("月");
	
	String desc;
	
	private FeeUnit(String desc) {
		this.desc = desc;
	}
	
	public String getDesc() {
		return desc;
	}



	public static FeeUnit getUnit(String input) {
		for(FeeUnit unit : values()) {
			if(unit.name().equalsIgnoreCase(input)) {
				return unit;
			}
		}
		return null;
	}
	
	/**
	 * 计算每个单位对应的毫秒
	 */
	public static long getTimeMillis(FeeUnit unit) {
		long ret = 0;
		if(unit != null) {
			switch (unit) {
			case min:
				ret = 60*1000;
				break;
			case hour:
				ret = 3600*1000;
				break;
			case day:
				ret = 24*3600*1000;
				break;
			case week:
				ret = 7*24*3600*1000;
				break;
			case month:
				ret = 30*24*3600*1000;//按30天算
				break;
			default:
				break;
			}
		}
		return ret;
	}
}
