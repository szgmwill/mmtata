package com.mmtata.im.server.service;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.fastjson.JSONObject;
import com.mmtata.im.server.bean.AcctInfo;
import com.mmtata.im.server.bean.AcctOrder;
import com.mmtata.im.server.bean.AcctWithdraw;
import com.mmtata.im.server.bean.OrderListItem;
import com.mmtata.im.server.bean.PageListResult;
import com.mmtata.im.server.bean.PingCharge;
import com.mmtata.im.server.bean.UserExtend;
import com.mmtata.im.server.common.ResultObject;
import com.mmtata.im.server.constants.ErrorCode;
import com.mmtata.im.server.dao.TradeDao;
import com.mmtata.im.server.dao.UserBaseDao;
import com.mmtata.im.server.exception.BizException;
import com.mmtata.im.server.helper.AcctOrderHelper;
import com.mmtata.im.server.helper.PingPPHelper;
import com.mmtata.im.server.status.AcctStatus;
import com.mmtata.im.server.status.AuditStatus;
import com.mmtata.im.server.status.OrderStatus;
import com.mmtata.im.server.status.PayChannelType;
import com.mmtata.im.server.status.WithdrawResult;
import com.mmtata.im.server.util.JsonHelper;
import com.pingplusplus.model.Charge;

/**
 * 交易账户相关业务实现
 * @author 张伟锐
 *
 */
@Service
public class AcctTradeService {
	private static final Logger logger = Logger.getLogger(AcctTradeService.class);
	
	@Autowired
	private TradeDao tradeDao;
	
	@Autowired
	private UserBaseDao userDao;
	
	@Transactional
	public void genPingCharge(long order_id, PayChannelType pay_type, ResultObject ro) throws Exception {
		//返回信息
		Map<String, Object> retMap = new HashMap<String, Object>();
		//找出订单信息
		List<AcctOrder> order_list = tradeDao.queryOrderList(order_id, null, null, null);
		if(order_list == null || order_list.size() == 0) {
			throw new BizException(ErrorCode.FAIL_BIZ_ERROR, "查询不到订单信息");
		}
		AcctOrder order = order_list.get(0);
		//更新订单支付渠道
		order.setPay_type(pay_type);
		int row = tradeDao.updateOrderStatus(order);
		logger.debug("更新订单["+order.getOrder_id()+"]支付渠道成功["+row+"]:"+pay_type.name());
		
		//如果是余额支付,将直接打款
		if(pay_type == PayChannelType.ACCT) {
			logger.debug("订单["+order.getOrder_id()+"]直接余额支付");
			
			//判断当前余额是否足够
			AcctInfo myAcct = tradeDao.queryAcctInfo(order.getUser_id());
			AcctInfo tarAcct = tradeDao.queryAcctInfo(order.getTarget_id());
			
			if(myAcct == null || tarAcct == null) {
				throw new BizException(ErrorCode.FAIL_BIZ_ERROR, "双方账户信息异常");
			}
			
			//判断余额是否够支付
			if(myAcct.getBalance().compareTo(order.getTotal_amt()) < 0) {
				throw new BizException(ErrorCode.FAIL_BIZ_ERROR, "当前余额不足");
			}
			
			//余额转账 
			BigDecimal order_amt = order.getTotal_amt();
			logger.debug("余额转账金额:"+order_amt);
			BigDecimal my_amt = myAcct.getBalance();
			BigDecimal tar_amt = tarAcct.getBalance();
			
			my_amt = my_amt.subtract(order_amt);
			tar_amt = tar_amt.add(order_amt);
			logger.debug("order_id["+order_id+"],余额转账后:my_amt["+my_amt+"],tar_amt["+tar_amt+"]");
			
			myAcct.setBalance(my_amt);
			tarAcct.setBalance(tar_amt);
			//更新
			int update = tradeDao.updateAcctInfo(myAcct);
			if(update != 1) {
				throw new BizException(ErrorCode.FAIL_BIZ_ERROR, "余额账户操作失败");
			}
			update = tradeDao.updateAcctInfo(tarAcct);
			if(update != 1) {
				throw new BizException(ErrorCode.FAIL_BIZ_ERROR, "余额账户操作失败");
			}
			
			//成功后更新订单状态
			order.setStatus(OrderStatus.PAID);
			order.setPay_trade_no("acct_"+System.currentTimeMillis());
			
			tradeDao.updateOrderStatus(order);
			
			logger.debug("余额支付,更新订单成功:"+order.toString());
			
		} else {
			//获取第三方支付ping++的请求凭证号
			Charge ping_charge = PingPPHelper.sendReqCharge(genPingChargeReq(order));
			if(ping_charge == null) {
				throw new BizException(ErrorCode.FAIL_BIZ_ERROR, "生成支付凭证失败");
			}
			
			retMap.put("charge", ping_charge);
			ro.setData(retMap);
		}
		
		
		
		return;
	}
	
	/**
	 * 发起购买服务
	 * 返回生成预支付订单
	 */
	@Transactional
	public void addNewOrderBuy(AcctOrder order, ResultObject ro) throws Exception {
		logger.debug("addNewOrderBuy ==> req:"+order.toString());
		
		//返回信息
		Map<String, Object> retMap = new HashMap<String, Object>();
		
		//相关逻辑判断,保证生成订单前的业务逻辑是正常的
		List<AcctOrder> allList = tradeDao.queryOrderList(null, order.getUser_id(), order.getTarget_id(), null);
		if(allList != null && allList.size() > 0) {
			for(AcctOrder old : allList) {
				if(old.getStatus() == OrderStatus.PAID) {
					//如果有订单已经支付但没有确认,暂不允许再购买
					throw new BizException(ErrorCode.FAIL_BIZ_ERROR, "目前有订单已支付完但未确认,不能再购买");
				}
				if(old.getStatus() == OrderStatus.NEW) {
					//如果有未完成的订单,直接更新就可以了,免得生成太多垃圾记录
					order.setOrder_id(old.getOrder_id());
				}
			}
		}
		
		//计算服务到期时间
		Date expireTime = AcctOrderHelper.getExpireTime(order);
		if(expireTime != null) {
			order.setExpire_time(expireTime);
		}
		
		//计算订单总价
		order.setTotal_amt(AcctOrderHelper.getOrderTotalAmt(order));
		
		if(order.getOrder_id() > 0) {
			//更新
			int row = tradeDao.updateOrderStatus(order);
			logger.debug("订单更新成功:order_id["+order.getOrder_id()+"],row["+row+"]");
		} else {
			//新增订单
			order.setStatus(OrderStatus.NEW);
			
			tradeDao.addNewOrder(order);
			logger.debug("订单新增入库成功:order_id["+order.getOrder_id()+"]");
		}
		
		retMap.put("order_id", order.getOrder_id());
		
		retMap.put("total_amt", order.getTotal_amt());
		
		retMap.put("expire_time", order.getExpire_time());
		
		//查询一下当前的账户余额
		AcctInfo acctInfo = tradeDao.queryAcctInfo(order.getUser_id());
		if(acctInfo == null) {
			throw new BizException(ErrorCode.FAIL_BIZ_ERROR, "账户异常");
		}
		
		retMap.put("acct_balance", acctInfo.getBalance());
		
		ro.setData(retMap);
		logger.debug("新增订单业务成功:"+JsonHelper.toJson(retMap));
		return;
	}
	
	/**
	 * 同步支付结果
	 */
	@Transactional
	public void syncOrderStatus(long order_id, OrderStatus status, String trade_no) throws Exception {
		logger.debug("同步支付结果:order_id["+order_id+"], status["+status+"], trade_no["+trade_no+"]");
		
		//业务判断
		List<AcctOrder> orderList = tradeDao.queryOrderList(order_id, null, null, null);
		if(orderList == null || orderList.size() == 0) {
			throw new BizException(ErrorCode.FAIL_BIZ_ERROR, "查询不到订单信息");
		}
		//只针对创建的订单进行处理
		AcctOrder order = orderList.get(0);
		if(order.getStatus() != OrderStatus.NEW) {//只能是 NEW -> FAIL 或  NEW -> PAID
			throw new BizException(ErrorCode.FAIL_BIZ_ERROR, "同步订单状态有误:ori_status["+order.getStatus()+"], status["+status+"]");
		}
		order.setStatus(status);
		order.setPay_trade_no(trade_no);
		
		int row = tradeDao.updateOrderStatus(order);
		if(row != 1) {
			throw new BizException(ErrorCode.FAIL_BIZ_ERROR, "更新订单状态异常:row["+row+"]");
		}
		
		//如果说是支付成功的话,先把这笔订单的钱暂时存放到用户的余额里,待对方用户确认完成服务后,
		//再从该账户余额里转账到对方账户余额里
		if(status == OrderStatus.PAID) {
			AcctInfo acct = tradeDao.queryAcctInfo(order.getUser_id());
			if(acct == null) {
				acct = new AcctInfo();
				acct.setUser_id(order.getUser_id());
				acct.setBalance(order.getTotal_amt());
				acct.setPay_pw("");
				acct.setWithdraw_amt(new BigDecimal(0));
				acct.setStatus(AcctStatus.NORMAL);
				//新增账户信息
				tradeDao.addNewAcctInfo(acct);
				logger.debug("新增账户信息成功:"+acct.toString());
			} else {
				//更新余额
				BigDecimal nowAmt = acct.getBalance();
				nowAmt = nowAmt.add(order.getTotal_amt());
				acct.setBalance(nowAmt);
				int updateRow = tradeDao.updateAcctInfo(acct);
				logger.debug("更新账户余额成功["+updateRow+"]:"+acct.toString());
			}
		}
		
		logger.debug("同步订单状态成功:order_id["+order_id+"]");
		
		return;
	}
	
	/**
	 * 确认订单已完成
	 * 订单金额将会转账
	 */
	@Transactional
	public void confirmOrderDone(long order_id, float rating) throws Exception {
		logger.debug("确认订单已完成:order_id["+order_id+"]");
		
		//业务判断
		List<AcctOrder> orderList = tradeDao.queryOrderList(order_id, null, null, null);
		if(orderList == null || orderList.size() == 0) {
			throw new BizException(ErrorCode.FAIL_BIZ_ERROR, "查询不到订单信息");
		}
		AcctOrder order = orderList.get(0);
		if(order.getStatus() != OrderStatus.PAID) {//只有支付完成未确认的订单才可以确认 即PAID -> DONE
			throw new BizException(ErrorCode.FAIL_BIZ_ERROR, "确认订单状态有误:status["+order.getStatus()+"]");
		}
		order.setStatus(OrderStatus.DONE);//已完成
		
		int row = tradeDao.updateOrderStatus(order);
		if(row != 1) {
			throw new BizException(ErrorCode.FAIL_BIZ_ERROR, "确认订单完成异常:row["+row+"]");
		}
		
		//同步成功后,做相关处理(服务已支付并且双方已确认,订单金额将充值到收款方账户)
		//更新账户余额
		
		//1.先从付款方账户里扣减
		AcctInfo myAcct = tradeDao.queryAcctInfo(order.getUser_id());//付款方账户
		if(myAcct == null || (myAcct.getBalance().compareTo(order.getTotal_amt()) < 0)
				|| myAcct.getStatus() != AcctStatus.NORMAL) {
			throw new BizException(ErrorCode.FAIL_BIZ_ERROR, "付款方账户异常或余额不足");
		}
		
		//扣减
		BigDecimal myAmt = myAcct.getBalance();
		myAmt = myAmt.subtract(order.getTotal_amt());
		myAcct.setBalance(myAmt);//新的余额
		int updateRow = tradeDao.updateAcctInfo(myAcct);
		logger.debug("付款方余额扣减成功["+updateRow+"]:"+myAcct.toString());
		
		
		//2.再把钱充进收款方账户
		if(updateRow == 1) {
			AcctInfo targetAcct = tradeDao.queryAcctInfo(order.getTarget_id());//收款方账户
			if(targetAcct == null) {
				targetAcct = new AcctInfo();
				targetAcct.setUser_id(order.getTarget_id());
				targetAcct.setBalance(order.getTotal_amt());
				targetAcct.setPay_pw("");
				targetAcct.setWithdraw_amt(new BigDecimal(0));
				targetAcct.setStatus(AcctStatus.NORMAL);
				//新增账户信息
				tradeDao.addNewAcctInfo(targetAcct);
				logger.debug("新增账户信息成功:"+targetAcct.toString());
			} else {
				//更新余额
				BigDecimal nowAmt = targetAcct.getBalance();
				nowAmt = nowAmt.add(order.getTotal_amt());//充值后余额
				targetAcct.setBalance(nowAmt);
				updateRow = tradeDao.updateAcctInfo(targetAcct);
				logger.debug("收款方账户余额充值成功["+updateRow+"]:"+targetAcct.toString());
			}
		}
		
		
		//处理订单服务方(收款方)的服务评分
		//找出收款方用户扩展信息
		UserExtend targetExtend = userDao.queryUserExtendByUserId(order.getTarget_id());
		if(targetExtend == null) {
			throw new BizException(ErrorCode.FAIL_BIZ_ERROR, "收款方信息异常");
		}
		//总评分
		if(targetExtend.getFeedback() == null) {
			targetExtend.setFeedback(0f);
		}
		targetExtend.setOrder_num(targetExtend.getOrder_num() + 1);
		float newFeedback = (targetExtend.getFeedback() + rating)/targetExtend.getOrder_num();
		targetExtend.setFeedback(newFeedback);
		
		updateRow = userDao.updateUserExtend(targetExtend);
		
		if(updateRow != 1) {
			throw new BizException(ErrorCode.FAIL_BIZ_ERROR, "更新收款方服务评分失败");
		}
		
		logger.debug("用户["+order.getTarget_id()+"],评分["+targetExtend.getFeedback()+"] -> ["+newFeedback+"]");
		
		logger.debug("订单确认完成成功:order_id["+order_id+"]");
		
		return;
	}
	
	/**
	 * 生成请求支付凭证信息
	 */
	private PingCharge genPingChargeReq(AcctOrder order) {
		PingCharge chargeReq = new PingCharge();
		
		chargeReq.setOrder_no(order.getOrder_id());
		BigDecimal amt = order.getTotal_amt();
		chargeReq.setAmount(amt.multiply(new BigDecimal(100)).longValue());//将总价转换为分单位
		String desc = "陪你的Ta订单-"+order.getOrder_id();
		chargeReq.setSubject(desc);//商品标题
		chargeReq.setBody(desc);//商品描述
		String channel = "";
		if(order.getPay_type() == PayChannelType.WX) {
			channel = "wx";
		} else if (order.getPay_type() == PayChannelType.ZFB) {
			channel = "alipay";
		}
		chargeReq.setChannel(channel);
		return chargeReq;
	}
	
	/**
	 * 查询我的账户信息
	 */
	public void queryMyAccount(long user_id, ResultObject ro) throws Exception {
		
		//返回信息
		Map<String, Object> retMap = new HashMap<String, Object>();
		
		//先查询一下用户扩展信息
		String wxAcct = "";
		String zfbAcct = "";
		UserExtend extend = userDao.queryUserExtendByUserId(user_id);
		if(extend != null) {
//			throw new BizException(ErrorCode.FAIL_BIZ_ERROR, "找不到用户信息");
			wxAcct = extend.getWx_acct();
			zfbAcct = extend.getZfb_acct();
		}
		
		//查询用户账户信息
		AcctInfo acct = tradeDao.queryAcctInfo(user_id);
		if(acct == null) {
			throw new BizException(ErrorCode.FAIL_BIZ_ERROR, "找不到用户账户信息");
		}
		
		retMap.put("balance", acct.getBalance());
		retMap.put("withdraw_amt", acct.getWithdraw_amt());
		retMap.put("wx_acct", wxAcct);
		retMap.put("zfb_acct", zfbAcct);
//		retMap.put("status", value);
		
		ro.setData(retMap);
		
		return;
	}
	
	/**
	 * 申请提现发起
	 */
	@Transactional
	public void reqWithdraw(AcctWithdraw withdraw, ResultObject ro) throws Exception {
		//返回信息
		Map<String, Object> retMap = new HashMap<String, Object>();
		
		//查询账户信息
		AcctInfo acct = tradeDao.queryAcctInfo(withdraw.getUser_id());
		if(acct == null) {
			throw new BizException(ErrorCode.FAIL_BIZ_ERROR, "找不到用户账户信息");
		}
		
		//判断余额够不够提现
		BigDecimal myBalance = acct.getBalance();
		if(myBalance.compareTo(withdraw.getAmount()) < 0) {
			throw new BizException(ErrorCode.FAIL_BIZ_ERROR, "提现金额["+withdraw.getAmount()+"]大于当前账户余额["+myBalance+"]");
		}
		
		
		
		//新增提现记录
		withdraw.setBalance(myBalance);//提现前的账户余额
		//暂时将相关状态都标记为完成成功,待支付宝转账处理接入后修改
		withdraw.setResult(WithdrawResult.DONE);//提现转账成功
		withdraw.setStatus(AuditStatus.PASS);//审核通过
		withdraw.setTrade_no("tata_"+System.currentTimeMillis());
		
		tradeDao.addNewWithdraw(withdraw);
		logger.debug("新增提现单成功:"+withdraw.getWithdraw_id());
		
		//这里直接先调用API进行企业打款到用户账户操作,然后更新提现单
		//TO DO...
		
		
		//扣减当前用户的余额
		acct.setBalance(myBalance.subtract(withdraw.getAmount()));
		//累加总已经提现的金额
		acct.setWithdraw_amt(acct.getWithdraw_amt().add(withdraw.getAmount()));
		
		//更新账户
		int updateRow = tradeDao.updateAcctInfo(acct);
		
		if(updateRow != 1) {
			throw new BizException(ErrorCode.FAIL_BIZ_ERROR, "提现失败:更新账户信息异常");
		}
		
		logger.debug("提现申请成功:"+withdraw.getWithdraw_id());
		
		retMap.put("withdraw_id", withdraw.getWithdraw_id());
		
		ro.setData(retMap);
		
		return;
	}
	
	/**
	 * 查询我的收支订单列表
	 */
	public void queryMyOrderList(JSONObject json, ResultObject ro) throws Exception {
		
		//返回结果 
		PageListResult<OrderListItem> retPage = new PageListResult<OrderListItem>();
		int page_index = json.getIntValue("page_index");
		int page_size = json.getIntValue("page_size");
		long user_id = json.getLongValue("user_id");
		int type = json.getIntValue("type");
		OrderStatus status = OrderStatus.getStatus(json.getString("status"));
		
		retPage.setPage_index(page_index);
		retPage.setPage_size(page_size);
		
		int offset = (page_index - 1) * page_size;
		
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("user_id", user_id);
		paramMap.put("offset", offset);
		paramMap.put("limit", page_size);
		if(type > 0) {
			paramMap.put("type", type);	
		}
		if(status != null) {
			paramMap.put("status", status);
		}
		
		//根据条件查询订单列表
		List<OrderListItem> order_list = tradeDao.queryMyOrderList(paramMap);
		if(order_list == null || order_list.size() == 0) {
			retPage.setData_list(Collections.EMPTY_LIST);
		} else {
			retPage.setData_list(order_list);
		}
		
		logger.debug("查询我的收入支出明细列表:"+retPage.getData_list().size());
		
		ro.setData(retPage);
		
		return;
	}
	
	/**
	 * 查询我的提现记录信息
	 */
	public void queryMyWithdrawList(JSONObject json, ResultObject ro) throws Exception {
		
		//返回结果 
		PageListResult<AcctWithdraw> retPage = new PageListResult<AcctWithdraw>();
		int page_index = json.getIntValue("page_index");
		int page_size = json.getIntValue("page_size");
		long user_id = json.getLongValue("user_id");
		AuditStatus st = AuditStatus.getStatus(json.getString("status"));
		
		//根据条件查询提现列表
		retPage.setPage_index(page_index);
		retPage.setPage_size(page_size);
		
		int offset = (page_index - 1) * page_size;
		
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("user_id", user_id);
		paramMap.put("offset", offset);
		paramMap.put("limit", page_size);
		if(st != null) {
			paramMap.put("status", st);
		}
		
		
		List<AcctWithdraw> withdraw_list = tradeDao.queryWithdrawList(paramMap);
		
		if(withdraw_list == null || withdraw_list.size() == 0) {
			retPage.setData_list(Collections.EMPTY_LIST);
		} else {
			retPage.setData_list(withdraw_list);
		}
		
		logger.debug("查询我的提现列表:"+retPage.getData_list().size());
		
		ro.setData(retPage);
		
		return;
	}
	
	/**
	 * 查询我与目标对象之间的订单信息
	 */
	public void queryTargetOrderList(AcctOrder req, ResultObject ro) throws Exception {
		
		//根据条件查询订单列表
		long user_id = req.getUser_id();
		long target_id = req.getTarget_id();
		List<AcctOrder> order_list = tradeDao.queryTargetOrderList(user_id, 
				target_id, req.getStatus());
		
		//返回信息
		Map<String, Object> retMap = new HashMap<String, Object>();
		if(order_list != null && order_list.size() > 0) {
			logger.debug("查询["+user_id+"]与["+target_id+"]订单列表数:"+order_list.size());
			retMap.put("data_list", order_list);
		} else {
			logger.debug("查询["+user_id+"]与["+target_id+"]订单列表数为空");
			retMap.put("data_list", Collections.EMPTY_LIST);
		}
		
		
		//设置结果
		ro.setData(retMap);
		
		return;
	}
}
