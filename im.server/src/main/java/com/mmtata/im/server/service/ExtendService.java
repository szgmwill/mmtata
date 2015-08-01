package com.mmtata.im.server.service;

import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.fastjson.JSONObject;
import com.mmtata.im.server.bean.UserReport;
import com.mmtata.im.server.dao.ExtendDao;

/**
 * 用户相关扩展功能
 * @author 张伟锐
 *
 */
@Service
public class ExtendService {
	private static final Logger logger = Logger.getLogger(ExtendService.class);
	
	@Autowired
	private ExtendDao dao;
	
	/**
	 * 举报用户
	 */
	@Transactional
	public void addUserReport(JSONObject json) throws Exception {
		long userId = json.getLong("user_id");
		long targetId = json.getLong("target_id");
		int type = json.getIntValue("type");
		String remark = json.getString("remark");
		
		UserReport add = new UserReport();
		add.setUserId(userId);
		add.setTargetId(targetId);
		add.setType(type);
		add.setRemark(remark);
		
		//先查询是否已经举报过了
		List<UserReport> exsitList = dao.queryUserReport(add);
		if(exsitList != null && exsitList.size() > 0) {
			logger.debug("用户["+userId+"]举报["+targetId+"]已经操作过了");
		} else {
			//新增举报记录
			dao.addNewUserReport(add);
			
			logger.debug("用户["+userId+"]举报["+targetId+"]成功");
		}
	}
}
