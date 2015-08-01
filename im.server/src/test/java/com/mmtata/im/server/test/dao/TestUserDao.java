/**
 * 深圳市塔塔互动
 * 作者:张伟锐
 * 2015年5月16日
 */
package com.mmtata.im.server.test.dao;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.mmtata.im.server.base.BaseSpringTestCase;
import com.mmtata.im.server.bean.TabInfo;
import com.mmtata.im.server.bean.UserBase;
import com.mmtata.im.server.bean.UserExtend;
import com.mmtata.im.server.bean.UserFind;
import com.mmtata.im.server.bean.UserImg;
import com.mmtata.im.server.bean.UserLogin;
import com.mmtata.im.server.bean.UserRegister;
import com.mmtata.im.server.dao.UserBaseDao;
import com.mmtata.im.server.status.FeeUnit;
import com.mmtata.im.server.status.LoginStatus;
import com.mmtata.im.server.status.Sex;
import com.mmtata.im.server.util.JsonHelper;

public class TestUserDao extends BaseSpringTestCase {
	
	@Autowired
	private UserBaseDao dao;
	
	//@Test
	public void testAddLoginLog() {
		UserLogin login = new UserLogin();
		login.setUser_id(101008);
		login.setStatus(LoginStatus.OUT);
		login.setCreate_time(new Date());
		
		dao.addLoginLog(login);
		
		Assert.assertTrue(login.getPid() > 0);
	}
	
	//@Test
	public void testRegisterUser() {
		UserRegister register = new UserRegister();
		
		register.setWx_open_id("openid_123");
		register.setMobile("13824300047");
		register.setPassword("md5tokenpassword");
		register.setCreate_time(new Date());
		
		dao.registerUser(register);
		
		Assert.assertTrue(register.getUser_id() > 0);
	}
	
//	@Test
	public void testAddUser() {
		UserBase base = new UserBase();
		
		base.setUser_id(10199);
		base.setNick("张妮娅");
		base.setBirth(new Date());
		base.setCareer("公关");
		base.setLocation("深圳");
		base.setSex(Sex.GIRL);
		base.setSign("人生座佑铭:我选择，我喜欢");
		base.setHead(null);
		
		dao.addNewUser(base);
		
		Assert.assertTrue(base.getUser_id() > 0);
	}
	
	//@Test
	public void testAddUserTab() {
		long user_id = 10101;
		TabInfo tab = new TabInfo();
		
		tab.setTab_type(2);
		tab.setTab_name("高富帅");
		
		dao.addUserTab(user_id, tab);
		
		Assert.assertTrue(tab.getTab_id() > 0);
	}
	
	//@Test
	public void testAddUserImg () {
		UserImg img = new UserImg();
		img.setUser_id(10102);
		img.setIndex(7);
		img.setUrl("http://img.qiniu.com/jpg/001.jpg");
		
		dao.addUserImg(img);
		
		Assert.assertTrue(img.getPid() > 0);
	}
	
//	@Test
	public void testGetUserRegister() {
		long userId = 10107;
		UserRegister user = dao.getRegisterUserByUserId(userId);
		
		if(user != null) {
			System.out.println("getRegisterUserByUserId:"+user.toString());
		}
	}
	
//	@Test
	public void testDelete() {
		long userId=10100;
		
		int d1 = dao.deleteUserImg(userId);
		int d2 = dao.deleteUserTab(userId);
		
		System.out.println("testDelete: d1["+d1+"],d2["+d2+"]");
	}
	
//	@Test
	public void testFindUserList() {
		int offset = 0;
		
		int limit = 10;
		long userId = 10107;
//		Sex sex = Sex.BOY;
		
		List<UserFind> findList = dao.findOnlineUserList(null, offset, limit);
		if(findList != null) {
			System.out.println("find list size:"+findList.size());
			
			System.out.println("the first user:"+findList.get(0).toString());
			
			System.out.println("json:"+JsonHelper.toJson(findList));
		}
	}
	
	@Test
	public void testUserExtend() {
		
		
		
		UserExtend extend = new UserExtend();
		extend.setUser_id(10109);
		extend.setFee(new BigDecimal(40));
		extend.setFee_unit(FeeUnit.month);
		extend.setFee_duration(2);
		extend.setWx_acct("szgmwill@qq.com");
		extend.setZfb_acct("szgmwill@126.com");
		extend.setZfb_nick("小妮妮");
		extend.setFeedback(4.5f);
		extend.setBind_mobile("13824300047");
		extend.setAllow_free_chat(1);
		
		UserExtend old = dao.queryUserExtendByUserId(extend.getUser_id());
		if(old != null) {
			//更新
			int update = dao.updateUserExtend(extend);
			System.out.println("updateUserExtend update["+update+"]");
		} else {
			//新增
			dao.addNewUserExtend(extend);
			System.out.println("addNewUserExtend pid["+extend.getPid()+"]");
		}
		
		
	}
}
