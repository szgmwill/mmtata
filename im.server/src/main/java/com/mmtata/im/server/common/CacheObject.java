package com.mmtata.im.server.common;
/**
 * 通用缓存对象
 * @author 张伟锐
 *
 */
public class CacheObject {
	
	/**
	 * 缓存内容对象
	 */
	private Object value;
	
	
	/**
	 * 缓存的时间戳
	 */
	private long createTime;
	
	/**
	 * 缓存有效时间
	 */
	private long cacheTime;
	
	/**
	 * 判断是否已经失效
	 */
	public boolean isExpire() {
		if(System.currentTimeMillis() - (createTime+cacheTime) > 0) {
			return true;
		}
		return false;
	}
	
	public Object getValue() {
		return value;
	}

	public void setValue(Object value) {
		this.value = value;
	}

	public long getCreateTime() {
		return createTime;
	}

	public void setCreateTime(long createTime) {
		this.createTime = createTime;
	}

	public long getCacheTime() {
		return cacheTime;
	}

	public void setCacheTime(long cacheTime) {
		this.cacheTime = cacheTime;
	}
	
	
}
