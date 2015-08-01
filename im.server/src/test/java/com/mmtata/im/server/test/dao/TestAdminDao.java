/**
 * 深圳市塔塔互动
 * 作者:张伟锐
 * 2015年5月17日
 */
package com.mmtata.im.server.test.dao;

import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.mmtata.im.server.base.BaseSpringTestCase;
import com.mmtata.im.server.bean.TabInfo;
import com.mmtata.im.server.dao.AdminDao;

public class TestAdminDao extends BaseSpringTestCase {
	@Autowired
	private AdminDao dao;
	
	@Test
	public void testQueryTabList() {
		
		List<TabInfo> tablist = dao.queryTabList(2);
		
		Assert.assertNotNull(tablist);
	}
}
