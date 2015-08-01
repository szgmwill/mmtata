package com.tata.imta.bean;

import java.util.Date;

/**
 * 版本升级信息
 */
public class VersionUpdate {

	/**
	 * 当前本地的版本号
	 */
	private int local_version_code;

	/**
	 * 当前要升级的app版本号
	 */
	private int version_code;
	
	/**
	 * 当前升级的版本名称
	 */
	private String version_name;
	
	/**
	 * 当前apk文件名
	 */
	private String apk_file_name;
	
	/**
	 * 当前的版本类型
	 */
	private String version_type;
	
	/**
	 * 发布时间
	 */
	private Date create_time;

	public int getVersion_code() {
		return version_code;
	}

	public void setVersion_code(int version_code) {
		this.version_code = version_code;
	}

	public String getVersion_name() {
		return version_name;
	}

	public void setVersion_name(String version_name) {
		this.version_name = version_name;
	}

	public String getApk_file_name() {
		return apk_file_name;
	}

	public void setApk_file_name(String apk_file_name) {
		this.apk_file_name = apk_file_name;
	}

	public String getVersion_type() {
		return version_type;
	}

	public void setVersion_type(String version_type) {
		this.version_type = version_type;
	}

	public Date getCreate_time() {
		return create_time;
	}

	public void setCreate_time(Date create_time) {
		this.create_time = create_time;
	}

	public int getLocal_version_code() {
		return local_version_code;
	}

	public void setLocal_version_code(int local_version_code) {
		this.local_version_code = local_version_code;
	}
}
