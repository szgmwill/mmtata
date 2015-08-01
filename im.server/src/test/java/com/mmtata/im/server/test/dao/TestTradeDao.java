package com.mmtata.im.server.test.dao;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.mmtata.im.server.base.BaseSpringTestCase;
import com.mmtata.im.server.bean.AcctInfo;
import com.mmtata.im.server.bean.AcctOrder;
import com.mmtata.im.server.bean.AcctWithdraw;
import com.mmtata.im.server.bean.OrderListItem;
import com.mmtata.im.server.bean.PageListResult;
import com.mmtata.im.server.dao.TradeDao;
import com.mmtata.im.server.status.AcctStatus;
import com.mmtata.im.server.status.AuditStatus;
import com.mmtata.im.server.status.FeeUnit;
import com.mmtata.im.server.status.OrderStatus;
import com.mmtata.im.server.status.PayChannelType;
import com.mmtata.im.server.status.WithdrawResult;
import com.mmtata.im.server.util.JsonHelper;

public class TestTradeDao extends BaseSpringTestCase {
	
	@Autowired
	private TradeDao dao;
	
	
//	@Test
	public void testOrderAction() {
		AcctOrder order = new AcctOrder();
		order.setUser_id(10108);
		order.setTarget_id(10107);
		order.setBuy_num(10);
		order.setExpire_time(new Date(System.currentTimeMillis()+3600*1000));
		order.setFee(new BigDecimal(20));
		order.setFee_duration(1);
		order.setFee_unit(FeeUnit.day);
		order.setPay_trade_no("pay_trade_no_123456");
		order.setPay_type(PayChannelType.WX);
		order.setStatus(OrderStatus.NEW);
		order.setTotal_amt(new BigDecimal(50));
		
		List<AcctOrder> findList = dao.queryOrderList(null, order.getUser_id(), order.getTarget_id(), null);
		if(findList != null && findList.size() > 0) {
			
			AcctOrder old = findList.get(0);
			System.out.println("find old one:"+old.toString());
			order.setOrder_id(old.getOrder_id());
			
			int row = dao.updateOrderStatus(old);
			System.out.println("update order row:"+row);
		} else {
			dao.addNewOrder(order);
			
			System.out.println("add new order done:"+order.getOrder_id());
		}
	}
	
	
	//@Test
	public void testWithdrawAction() {
		AcctWithdraw withdraw = new AcctWithdraw();
		withdraw.setUser_id(10108);
		withdraw.setAmount(new BigDecimal(30));
		withdraw.setBalance(new BigDecimal(100));
		withdraw.setChannel(PayChannelType.WX);
		withdraw.setComment("审核意见:失败");
		withdraw.setResult(WithdrawResult.DONE);
		withdraw.setStatus(AuditStatus.NEW);
		withdraw.setTrade_no("abc_trade_withdraw_0001");
		
		Map<String, Object> paraMap = new HashMap<String, Object>();
		paraMap.put("user_id", withdraw.getUser_id());
		paraMap.put("status", withdraw.getStatus());
		paraMap.put("offset", 1);
		paraMap.put("limit", 20);
		
		List<AcctWithdraw> findList = dao.queryWithdrawList(paraMap);
		if(findList != null && findList.size() > 0) {
			AcctWithdraw old = findList.get(0);
			System.out.println("find old withdraw:"+old.toString());
			
			withdraw.setWithdraw_id(old.getWithdraw_id());
			
			int row = dao.updateWithdrawStatus(withdraw);
			System.out.println("update withdraw row:"+row);
		} else {
			dao.addNewWithdraw(withdraw);
			
			System.out.println("add new withdraw done:"+withdraw.getWithdraw_id());
		}
	}
	
//	@Test
	public void testMyAcct() {
		AcctInfo acct = new AcctInfo();
		acct.setUser_id(10108);
		acct.setBalance(new BigDecimal(300));
		acct.setWithdraw_amt(new BigDecimal(80));
		acct.setPay_pw("aaaaaaaaaaaaaa");
		acct.setStatus(AcctStatus.NORMAL);
		
		AcctInfo old = dao.queryAcctInfo(acct.getUser_id());
		
		if(old != null) {
			System.out.println("find acct old:"+old.toString());
			acct.setLast_update_time(old.getLast_update_time());
			int row = dao.updateAcctInfo(acct);
			
			System.out.println("update acct info:"+row);
		} else {
			dao.addNewAcctInfo(acct);
			
			System.out.println("add new acct info");
		}
	}
	
	//@Test
	public void testMyOrderList() {
		long user_id = 10107;
		
		int page_index = 1;
		
		int page_size = 20;
		
		int offset = (page_index - 1)*page_size;
		
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("user_id", user_id);
		paramMap.put("offset", offset);
		paramMap.put("limit", page_size);
		paramMap.put("type", 1);
		paramMap.put("status", "done");
		
		List<OrderListItem> list = dao.queryMyOrderList(paramMap);
		
		PageListResult<OrderListItem> page = new PageListResult<OrderListItem>();
		
		page.setPage_index(page_index);
		page.setPage_size(page_size);
		page.setData_list(list);
		
		System.out.println("查询我的消费收入列表:"+JsonHelper.toJson(page));
		
	}
	
	@Test
	public void testMyTargetOrderList() {
		long user_id = 10107;
		long target_id = 10108;
		
		OrderStatus status = OrderStatus.PAID;
		
		List<AcctOrder> myList = dao.queryTargetOrderList(user_id, target_id, status);
		
		System.out.println("查询我与对象的服务订单列表:"+JsonHelper.toJson(myList));
	}
}
