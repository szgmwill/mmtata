package com.mmtata.im.server.test.dao;

import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.mmtata.im.server.base.BaseSpringTestCase;
import com.mmtata.im.server.bean.UserReport;
import com.mmtata.im.server.dao.ExtendDao;

public class TestExtendDao extends BaseSpringTestCase {
	
	@Autowired
	private ExtendDao dao;
	
//	@Test
	public void testAddUserReport() {
		UserReport add = new UserReport();
		add.setUserId(10107);
		add.setTargetId(10108);
		add.setType(2);
		add.setRemark("unit test");
		
		dao.addNewUserReport(add);
		
		Assert.assertTrue(add.getPid() > 0);
	}
	
	@Test
	public void testQueryUserReport() {
		UserReport query = new UserReport();
		query.setUserId(10107);
		query.setTargetId(10108);
		
		List<UserReport> reportList = dao.queryUserReport(query);
		if(reportList != null && reportList.size() > 0) {
			System.out.println("testQueryUserReport:"+reportList.size());
			UserReport report = reportList.get(0);
			
			System.out.println("get[0]:"+report.toString());
		}
	}
}
