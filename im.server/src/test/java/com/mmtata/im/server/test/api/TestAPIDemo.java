/**
 * 深圳市塔塔互动
 * 作者:张伟锐
 * 2015年5月17日
 */
package com.mmtata.im.server.test.api;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSON;
import com.mmtata.im.server.bean.TabInfo;
import com.mmtata.im.server.bean.UserImg;
import com.mmtata.im.server.util.HttpUtils;
import com.mmtata.im.server.util.SignUtils;

public class TestAPIDemo {
	
	private static final String host = "127.0.0.1";
	private static final int port = 8080;
	
	private static Map<String, String> reqMap = new HashMap<String, String>();
	
	private static final String client = "android";
	private static final String timestamp = String.valueOf(System.currentTimeMillis());
	
	static {
		reqMap.put("client", client);
		reqMap.put("timestamp", timestamp);
		
	}
	
	private static void testRegisterUser() {
		String uri = "/im.server/user/register";
		
		reqMap.put("signkey", SignUtils.makeSign(uri, client, timestamp));
		
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("nick", "测试用户");
		map.put("openid", "abc123");
		map.put("sex", "BOY");
		map.put("head", "http://img.qiniu.com/001.jpg");
		List<TabInfo> testTablist = new ArrayList<TabInfo>();
		TabInfo tab1 = new TabInfo();
		tab1.setTab_type(1);
		tab1.setTab_name("我是大哥");
		TabInfo tab2 = new TabInfo();
		tab2.setTab_type(3);
		tab2.setTab_name("我是你妹");
		testTablist.add(tab1);
		testTablist.add(tab2);
		map.put("tab_list", testTablist);
		
		reqMap.put("reqData", JSON.toJSONString(map));
		
		try {
			String resp = HttpUtils.doPost(host, port, uri, reqMap);
			
			System.out.println("testRegisterUser:"+resp);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	private static void queryTablist() {
		String uri = "/im.server/admin/tablist";
		
		reqMap.put("signkey", SignUtils.makeSign(uri, client, timestamp));
		
		Map<String, String> params = new HashMap<String, String>();
		params.put("tab_type", "0");

		reqMap.put("reqData", JSON.toJSONString(params));
		
		try {
			String resp = HttpUtils.doGet(host, port, uri, reqMap);
			
			System.out.println("queryTablist:"+resp);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private static void userLogin() {
		String uri = "/im.server/login/action";

		reqMap.put("signkey", SignUtils.makeSign(uri, client, timestamp));
		
		Map<String, String> params = new HashMap<String, String>();
		params.put("user_id", "10105");
		params.put("status", "in");
		
		reqMap.put("reqData", JSON.toJSONString(params));
		
		try {
			String resp = HttpUtils.doPost(host, port, uri, reqMap);
			
			System.out.println("userLogin:"+resp);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private static void queryUserDetail() {
		String uri = "/im.server/user/query_info";

		reqMap.put("signkey", SignUtils.makeSign(uri, client, timestamp));
		
		Map<String, String> params = new HashMap<String, String>();
		params.put("user_id", "10106");

		reqMap.put("reqData", JSON.toJSONString(params));
		
		try {
			String resp = HttpUtils.doGet(host, port, uri, reqMap);
			
			System.out.println("queryUserDetail:"+resp);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
	 * 修改用户信息
	 * @param args
	 */
	private static void testUpdateUserBase() {
		String uri = "/im.server/user/edit_base";
		
		reqMap.put("signkey", SignUtils.makeSign(uri, client, timestamp));
		
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("nick", "修改用户信息");
		map.put("openid", "abc123");
		map.put("user_id", "10100");
		map.put("birth", "1988-10-10");
		map.put("sign", "我喜欢我任性");
		map.put("sex", "BOY");
		map.put("head", "http://img.qiniu.com/001.jpg");
		List<TabInfo> testTablist = new ArrayList<TabInfo>();
		TabInfo tab1 = new TabInfo();
		tab1.setTab_type(1);
		tab1.setTab_name("我是大哥");
		TabInfo tab2 = new TabInfo();
		tab2.setTab_type(3);
		tab2.setTab_name("我是你爷");
		testTablist.add(tab1);
		testTablist.add(tab2);
		map.put("tab_list", testTablist);
		
		List<UserImg> imgList = new ArrayList<UserImg>();
		for(int i=0; i<3; i++) {
			UserImg newImg = new UserImg();
			newImg.setUser_id(10100);
			newImg.setIndex(i);
			newImg.setUrl("http://img.baidu.com/will"+i);
			newImg.setDel_flag(0);
			
			imgList.add(newImg);
		}
		map.put("img_list", imgList);
		
		reqMap.put("reqData", JSON.toJSONString(map));
		
		try {
			String resp = HttpUtils.doPost(host, port, uri, reqMap);
			
			System.out.println("testRegisterUser:"+resp);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	private static void queryFindList() {
		String uri = "/im.server/user/query_userlist";

		reqMap.put("signkey", SignUtils.makeSign(uri, client, timestamp));
		
		Map<String, String> params = new HashMap<String, String>();
		params.put("user_id", "10107");
		params.put("page_index", "1");
		params.put("page_size", "10");
		params.put("sex", "0");

		reqMap.put("reqData", JSON.toJSONString(params));
		
		try {
			String resp = HttpUtils.doGet(host, port, uri, reqMap);
			
			System.out.println("queryUserDetail:"+resp);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private static void orderPay() {
		String uri = "/im.server/account/add_order";

		reqMap.put("signkey", SignUtils.makeSign(uri, client, timestamp));
		
//		user_id	long	是	用户id
//		target_id	long	是	目标用户id
//		buy_num	int	是	购买数量(时长)
//		fee	decimal	是	服务费单价：比如：5元/30分钟
//		fee_unit	string	是	服务费计算标准
//		fee_duration	int	是	服务费单位计费时长，一般情况下是1
//		total_amt	double	是	订单总价
//		pay_type	int		付款方式：0-余额支付；1-微信支付；2-支付宝；
		
		Map<String, String> params = new HashMap<String, String>();
		params.put("user_id", "10107");
		params.put("target_id", "1");
		params.put("buy_num", "10");
		params.put("fee", "10");
		params.put("fee_unit", "day");
		params.put("fee_duration", "1");
		params.put("total_amt", "0");
		params.put("pay_type", "0");

		reqMap.put("reqData", JSON.toJSONString(params));
		
		try {
			String resp = HttpUtils.doPost(host, port, uri, reqMap);
			
			System.out.println("orderPay:"+resp);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private static void orderStatus() {
		String uri = "/im.server/account/edit_order";

		reqMap.put("signkey", SignUtils.makeSign(uri, client, timestamp));
		
//		order_id	long	是	订单号id
//		pay_trade_no	string	否	支付成功后的交易流水号，第三方给出
//		status	int	是	支付成功状态码：1-支付成功；2-支付失败；3-用户取消；4-其它；
		
		Map<String, String> params = new HashMap<String, String>();
		params.put("order_id", "30000005");
		params.put("pay_trade_no", "pay_trade_no_11111");
		params.put("status", "1");
		

		reqMap.put("reqData", JSON.toJSONString(params));
		
		try {
			String resp = HttpUtils.doPost(host, port, uri, reqMap);
			
			System.out.println("orderStatus:"+resp);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private static void queryMyOrderList() {
		String uri = "/im.server/account/order_list";

		reqMap.put("signkey", SignUtils.makeSign(uri, client, timestamp));
		
		Map<String, String> params = new HashMap<String, String>();
		params.put("user_id", "10107");
		params.put("page_index", "1");
		params.put("page_size", "10");
		params.put("type", "0");
		params.put("status", "done");
		
		reqMap.put("reqData", JSON.toJSONString(params));
		
		try {
			String resp = HttpUtils.doGet(host, port, uri, reqMap);
			
			System.out.println("queryMyOrderList:"+resp);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private static void queryMyWithdrawList() {
		String uri = "/im.server/account/withdraw_list";

		reqMap.put("signkey", SignUtils.makeSign(uri, client, timestamp));
		
		Map<String, String> params = new HashMap<String, String>();
		params.put("user_id", "10108");
		params.put("page_index", "1");
		params.put("page_size", "10");
//		params.put("type", "0");
//		params.put("status", "done");
		
		reqMap.put("reqData", JSON.toJSONString(params));
		
		try {
			String resp = HttpUtils.doGet(host, port, uri, reqMap);
			
			System.out.println("queryMyWithdrawList:"+resp);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
	 * 支付宝回调测试
	 */
	private static void testAlipayNotify() {
		String uri = "/im.server/account/alipay_notify";

		reqMap.put("signkey", SignUtils.makeSign(uri, client, timestamp));
		
		Map<String, String> params = new HashMap<String, String>();
		params.put("m_id", "10108");
		params.put("test", "this is test for alipay notify");
		params.put("xxxx", "aaaaaaaaaaaaaaaa");

		
		reqMap.put("reqData", JSON.toJSONString(params));
		
		try {
			String resp = HttpUtils.doPost(host, port, uri, reqMap);
			
			System.out.println("testAlipayNotify:"+resp);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		
//		testRegisterUser();
//		queryTablist();
		
//		userLogin();
		
//		queryUserDetail();
		
//		testUpdateUserBase();
		
//		queryFindList();
		
//		orderPay();
		
//		orderStatus();
		
//		queryMyOrderList();
		
//		queryMyWithdrawList();
		
		testAlipayNotify();
	}
}
