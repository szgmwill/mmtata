package com.mmtata.im.server.bean;

import java.util.List;

/**
 * 分页展示结果
 * @author 张伟锐
 *
 */
public class PageListResult<T> {
	
	/**
	 * 返回总数量
	 */
	private int total_count;
	
	/**
	 * 当前页索引
	 */
	private int page_index = 1;
	
	/**
	 * 每页展示数
	 */
	private int page_size = 20;
	
	/**
	 * 业务列表
	 */
	private List<T> data_list;

	public int getTotal_count() {
		return total_count;
	}

	public void setTotal_count(int total_count) {
		this.total_count = total_count;
	}

	public int getPage_index() {
		return page_index;
	}

	public void setPage_index(int page_index) {
		this.page_index = page_index;
	}

	public int getPage_size() {
		return page_size;
	}

	public void setPage_size(int page_size) {
		this.page_size = page_size;
	}

	public List<T> getData_list() {
		return data_list;
	}

	public void setData_list(List<T> data_list) {
		this.data_list = data_list;
	}
	
	
}
