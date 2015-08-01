package com.mmtata.im.server.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.mmtata.im.server.bean.AcctInfo;
import com.mmtata.im.server.bean.AcctOrder;
import com.mmtata.im.server.bean.AcctWithdraw;
import com.mmtata.im.server.bean.OrderListItem;
import com.mmtata.im.server.status.OrderStatus;

/**
 * 订单交易相关
 * @author 张伟锐
 *
 */
public interface TradeDao {
	
	public void addNewOrder(AcctOrder order);
	
	public int updateOrderStatus(AcctOrder order);
	
	public List<AcctOrder> queryOrderList(@Param("order_id") Long order_id,
			@Param("user_id") Long user_id, @Param("target_id") Long target_id,
			@Param("status") OrderStatus status);
	
	public void addNewWithdraw(AcctWithdraw withdraw);
	
	public int updateWithdrawStatus(AcctWithdraw withdraw);
	
	public List<AcctWithdraw> queryWithdrawList(Map<String, Object> paramMap);
	
	
	public void addNewAcctInfo(AcctInfo acct);
	
	public int updateAcctInfo(AcctInfo acct);
	
	public AcctInfo queryAcctInfo(@Param("user_id") long user_id);
	
	/**
	 * 查询我的消费明细
	 */
	public List<OrderListItem> queryMyOrderList(Map<String, Object> paramMap);
	
	/**
	 * 查询我与目标对象之间的订单信息
	 */
	public List<AcctOrder> queryTargetOrderList(@Param("user_id") Long user_id, @Param("target_id") Long target_id,
			@Param("status") OrderStatus status);
}
