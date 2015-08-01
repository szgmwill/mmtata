/**
 * 深圳市塔塔互动
 * 作者:张伟锐
 * 2015年5月18日
 */
package com.mmtata.im.server.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.mmtata.im.server.bean.TabInfo;
import com.mmtata.im.server.bean.VersionUpdate;
import com.mmtata.im.server.common.ResultObject;
import com.mmtata.im.server.constants.ErrorCode;
import com.mmtata.im.server.dao.AdminDao;

/**
 * 运营管理业务处理
 */

@Service
public class AdminService {
	private static final Logger logger = Logger.getLogger(AdminService.class);
	
	@Autowired
	private AdminDao adminDao;
	
	
	/**
	 * 查询可用标签列表
	 */
	public List<TabInfo> queryTabList(int type) {
		logger.debug("查询可用标签列表:"+type);
		
		List<TabInfo> retList = adminDao.queryTabList(type);
		if(retList != null) {
			logger.debug("查询当前可用标签列表结果:"+retList.size());
		}
		return retList;
		
	}
	
	//查询当前客户端版本信息
	public void queryVersionUpdate(ResultObject ro) {
		
		VersionUpdate vu = adminDao.queryCurrentVersionInfo();
		if(vu == null) {
			ro.setCode(ErrorCode.FAIL_BIZ_ERROR);
			ro.setMsg("当前没有版本信息");
		} else {
			Map<String, Object> retMap = new HashMap<String, Object>();
			retMap.put("version", vu);
			
			ro.setData(retMap);
		}
		
		return;
	}
}
