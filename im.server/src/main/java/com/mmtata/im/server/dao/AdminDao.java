/**
 * 深圳市塔塔互动
 * 作者:张伟锐
 * 2015年5月17日
 */
package com.mmtata.im.server.dao;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.mmtata.im.server.bean.TabInfo;
import com.mmtata.im.server.bean.VersionUpdate;

/**
 * 后台管理运营DB操作
 */
public interface AdminDao {
	
	//查询标签信息
	public List<TabInfo> queryTabList(@Param("type") Integer type);
	
	//查询当前发布的版本信息
	public VersionUpdate queryCurrentVersionInfo();
}
