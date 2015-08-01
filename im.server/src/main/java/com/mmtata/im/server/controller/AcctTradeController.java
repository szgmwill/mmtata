package com.mmtata.im.server.controller;

import java.math.BigDecimal;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSONObject;
import com.mmtata.im.server.bean.AcctOrder;
import com.mmtata.im.server.bean.AcctWithdraw;
import com.mmtata.im.server.common.ResultObject;
import com.mmtata.im.server.constants.ErrorCode;
import com.mmtata.im.server.exception.BizException;
import com.mmtata.im.server.service.AcctTradeService;
import com.mmtata.im.server.status.AuditStatus;
import com.mmtata.im.server.status.FeeUnit;
import com.mmtata.im.server.status.OrderStatus;
import com.mmtata.im.server.status.PayChannelType;
import com.mmtata.im.server.util.ExceptionUtils;
import com.mmtata.im.server.util.HttpUtils;

/**
 * 用户支付相关操作
 * @author 张伟锐
 *
 */
@Controller
@RequestMapping("/account")
public class AcctTradeController {
	private static final Logger logger = Logger.getLogger(AcctTradeController.class);
	
	@Autowired
	private AcctTradeService service;
	
	/**
	 * 接收支付宝的异步回调
	 * 详情请参考:
	 * https://cshall.alipay.com/enterprise/cateQuestion.htm?cateType=EE&cateId=250286&pcateId=250119
	 */
	@RequestMapping(value = "/alipay_notify", method = RequestMethod.POST)
	@ResponseBody
	public String alipayNotify(HttpServletRequest request) {
		
		String paramAll = HttpUtils.getAllParamFromHttpReq(request);
		logger.debug("支付宝移动支付回调通知:"+paramAll);
		
		//支付宝的所有支付结果理论上都要以主动回调通知为准,这里收到通知后要处理支付的结果
		//TO DO...
		
		return "success";
	}
	
	
	/**
	 * 生成ping++平台需要的支付凭证信息
	 * 如果是余额支付,将直接转账了,不返回凭证
	 */
	@RequestMapping(value = "/gen_charge", method = RequestMethod.POST)
	@ResponseBody
	public ResultObject generatePingCharge(HttpServletRequest request) {
		JSONObject reqJson = (JSONObject) request.getAttribute("reqJson");
		logger.debug("生成新支付凭证请求:" + reqJson.toString());
		
		ResultObject ret = new ResultObject();
		
		Long order_id = reqJson.getLong("order_id");
		PayChannelType pay_type = PayChannelType.getType(reqJson.getString("pay_type"));
		if(order_id == null || order_id <= 0) {
			ret.setCode(ErrorCode.FAIL_PARAM);
			ret.setMsg("请求订单号必填[order_id]");
			return ret;
		}
		if(pay_type == null) {
			ret.setCode(ErrorCode.FAIL_PARAM);
			ret.setMsg("请求支付渠道 必填[pay_type]");
			return ret;
		}
		
		//2.业务处理
		try {
			service.genPingCharge(order_id, pay_type, ret);
		} catch (BizException biz) {
			logger.error("生成新支付凭证业务异常:", biz);
			ret.setCode(biz.getErrCode());
			ret.setMsg(biz.getMessage());
			return ret;
		} catch (Exception e) {
			logger.error("生成新支付凭证操作异常:", e);
			ret.setCode(ErrorCode.FAIL_SYS_ERROR);
			ret.setMsg("生成新支付凭证操作异常");
			ret.setSysmsg(ExceptionUtils.traceExceptionMini(e));
			return ret;
		}
		
		
		//3.返回处理
		return ret;
	}
	
	/**
	 * 用户发起购买聊天服务
	 * 生成预支付订单
	 * 返回订单号和支付凭证
	 */
	@RequestMapping(value = "/add_order", method = RequestMethod.POST)
	@ResponseBody
	public ResultObject addNewPayOrder(HttpServletRequest request) {
		JSONObject reqJson = (JSONObject) request.getAttribute("reqJson");
		
		ResultObject ret = new ResultObject();
		AcctOrder order = new AcctOrder();
		//1.校验请求参数
		String paramErr = checkAddOrderParam(reqJson, order);
		if(paramErr != null) {
			ret.setCode(ErrorCode.FAIL_PARAM);
			ret.setMsg(paramErr);
			
			return ret;
		}
		
		//2.业务处理
		try {
			service.addNewOrderBuy(order, ret);
		} catch (BizException biz) {
			logger.error("新增订单业务异常:", biz);
			ret.setCode(biz.getErrCode());
			ret.setMsg(biz.getMessage());
			return ret;
		} catch (Exception e) {
			logger.error("新增订单操作异常:", e);
			ret.setCode(ErrorCode.FAIL_SYS_ERROR);
			ret.setMsg("新增订单操作异常");
			ret.setSysmsg(ExceptionUtils.traceExceptionMini(e));
			return ret;
		}
		
		
		//3.返回处理
		return ret;
		
	}
	
	/**
	 * 支付结果同步
	 * 用于客户端对生成的预支付单处理的最终结果回调服务端
	 */
	@RequestMapping(value = "/edit_order", method = RequestMethod.POST)
	@ResponseBody
	public ResultObject syncOrderStatus(HttpServletRequest request) {
		JSONObject reqJson = (JSONObject) request.getAttribute("reqJson");
		
		ResultObject ret = new ResultObject();
		String paramErr = null;
		//1.校验请求参数
		Long order_id = reqJson.getLong("order_id");
		OrderStatus status = OrderStatus.getStatus(reqJson.getString("status"));
		if(order_id == null || order_id <= 0) {
			paramErr = "订单号[order_id]必填";
		} else if(status == null) {
			paramErr = "支付结果[status]必填";	
		}
		
		if(paramErr != null) {
			ret.setCode(ErrorCode.FAIL_PARAM);
			ret.setMsg(paramErr);
			return ret;
		}
		
		//支付交易流水
		String pay_trade_no = reqJson.getString("pay_trade_no");
		
		
		//2.业务处理
		try {
			service.syncOrderStatus(order_id, status, pay_trade_no);
		} catch (BizException biz) {
			logger.error("同步支付结果业务异常:", biz);
			ret.setCode(biz.getErrCode());
			ret.setMsg(biz.getMessage());
			return ret;
		} catch (Exception e) {
			logger.error("同步支付结果操作异常:", e);
			ret.setCode(ErrorCode.FAIL_SYS_ERROR);
			ret.setMsg("同步支付结果操作异常");
			ret.setSysmsg(ExceptionUtils.traceExceptionMini(e));
			return ret;
		}
		
		
		//3.返回处理
		return ret;
		
	}
	
	
	/**
	 * 查询我的账户信息
	 */
	@RequestMapping(value = "/query_info", method = RequestMethod.GET)
	@ResponseBody
	public ResultObject queryAccountInfo(HttpServletRequest request) {
		JSONObject reqJson = (JSONObject) request.getAttribute("reqJson");
		
		ResultObject ret = new ResultObject();
		
		//1.参数校验
		Long user_id = reqJson.getLong("user_id");
		
		if(user_id == null || user_id <= 0) {
			ret.setCode(ErrorCode.FAIL_PARAM);
			ret.setMsg("查询用户id必填");
			return ret;
		}
		
		
		//2.业务处理
		try {
			service.queryMyAccount(user_id, ret);
		} catch (BizException biz) {
			logger.error("查询我的账户信息异常:", biz);
			ret.setCode(biz.getErrCode());
			ret.setMsg(biz.getMessage());
			return ret;
		} catch (Exception e) {
			logger.error("查询我的账户信息操作异常:", e);
			ret.setCode(ErrorCode.FAIL_SYS_ERROR);
			ret.setMsg("查询我的账户信息操作异常");
			ret.setSysmsg(ExceptionUtils.traceExceptionMini(e));
			return ret;
		}
		
		
		//3.返回处理
		return ret;
	}
	
	/**
	 * 申请提现
	 * 正常流程应该是：发起提现 -> 审核提现单 -> 发起第三方企业付款到用户账号 -> 提现单操作完成
	 * 目前暂时只考虑直接走打款
	 */
	@RequestMapping(value = "/add_withdraw", method = RequestMethod.POST)
	@ResponseBody
	public ResultObject addWithdraw(HttpServletRequest request) {
		JSONObject reqJson = (JSONObject) request.getAttribute("reqJson");
		
		ResultObject ret = new ResultObject();
		AcctWithdraw withdraw = new AcctWithdraw();
		
		//1.参数校验
		String paramErr = checkNewWithdraw(reqJson, withdraw);
		
		if(paramErr != null) {
			ret.setCode(ErrorCode.FAIL_PARAM);
			ret.setMsg(paramErr);
			return ret;
		}
		
		
		//2.业务处理
		try {
			service.reqWithdraw(withdraw, ret);
		} catch (BizException biz) {
			logger.error("申请提现异常:", biz);
			ret.setCode(biz.getErrCode());
			ret.setMsg(biz.getMessage());
			return ret;
		} catch (Exception e) {
			logger.error("申请提现操作异常:", e);
			ret.setCode(ErrorCode.FAIL_SYS_ERROR);
			ret.setMsg("申请提现操作异常");
			ret.setSysmsg(ExceptionUtils.traceExceptionMini(e));
			return ret;
		}
		
		
		//3.返回处理
		return ret;
	}
	
	/**
	 * 查看我的消费明细API
	 * 收支订单列表
	 */
	@RequestMapping(value = "/order_list", method = RequestMethod.GET)
	@ResponseBody
	public ResultObject orderlist(HttpServletRequest request) {
		JSONObject reqJson = (JSONObject) request.getAttribute("reqJson");
		
		ResultObject ret = new ResultObject();
		
		//1.参数校验
		String paramErr = checkListOrder(reqJson);
		
		if(paramErr != null) {
			ret.setCode(ErrorCode.FAIL_PARAM);
			ret.setMsg(paramErr);
			return ret;
		}
		
		
		//2.业务处理
		try {
			service.queryMyOrderList(reqJson, ret);
		} catch (BizException biz) {
			logger.error("查询订单列表异常:", biz);
			ret.setCode(biz.getErrCode());
			ret.setMsg(biz.getMessage());
			return ret;
		} catch (Exception e) {
			logger.error("查询订单列表操作异常:", e);
			ret.setCode(ErrorCode.FAIL_SYS_ERROR);
			ret.setMsg("查询订单列表操作异常");
			ret.setSysmsg(ExceptionUtils.traceExceptionMini(e));
			return ret;
		}
		
		
		//3.返回处理
		return ret;
	}
	
	/**
	 * 确认服务完成
	 * 同时转账
	 */
	@RequestMapping(value = "/confirm_order", method = RequestMethod.POST)
	@ResponseBody
	public ResultObject confirmOrderDone(HttpServletRequest request) {
		JSONObject reqJson = (JSONObject) request.getAttribute("reqJson");
		
		ResultObject ret = new ResultObject();
		
		//1.参数校验
		Long orderId = reqJson.getLong("order_id");
		Float rate = reqJson.getFloat("feedback_rate");
		
		if(orderId == null || orderId <= 0) {
			ret.setCode(ErrorCode.FAIL_PARAM);
			ret.setMsg("确认订单号[order_id]必填");
			return ret;
		}
		
		if(rate == null || rate < 0) {
			ret.setCode(ErrorCode.FAIL_PARAM);
			ret.setMsg("服务评分[feedback_rate]必填");
			return ret;
		}
		
		//2.业务处理
		try {
			service.confirmOrderDone(orderId, rate);
		} catch (BizException biz) {
			logger.error("确认服务完成异常:", biz);
			ret.setCode(biz.getErrCode());
			ret.setMsg(biz.getMessage());
			return ret;
		} catch (Exception e) {
			logger.error("查确认服务完成操作异常:", e);
			ret.setCode(ErrorCode.FAIL_SYS_ERROR);
			ret.setMsg("确认服务完成操作异常");
			ret.setSysmsg(ExceptionUtils.traceExceptionMini(e));
			return ret;
		}
		
		//3.返回处理
		return ret;
		
	}
	
	/**
	 * 查看我的提现记录
	 * 
	 */
	@RequestMapping(value = "/withdraw_list", method = RequestMethod.GET)
	@ResponseBody
	public ResultObject withdrawlist(HttpServletRequest request) {
		JSONObject reqJson = (JSONObject) request.getAttribute("reqJson");
		
		ResultObject ret = new ResultObject();
		
		//1.参数校验
		String paramErr = checkListWithdraw(reqJson);
		
		if(paramErr != null) {
			ret.setCode(ErrorCode.FAIL_PARAM);
			ret.setMsg(paramErr);
			return ret;
		}
		
		
		//2.业务处理
		try {
			service.queryMyWithdrawList(reqJson, ret);
		} catch (BizException biz) {
			logger.error("提现记录列表异常:", biz);
			ret.setCode(biz.getErrCode());
			ret.setMsg(biz.getMessage());
			return ret;
		} catch (Exception e) {
			logger.error("查询提现记录操作异常:", e);
			ret.setCode(ErrorCode.FAIL_SYS_ERROR);
			ret.setMsg("查询提现记录操作异常");
			ret.setSysmsg(ExceptionUtils.traceExceptionMini(e));
			return ret;
		}
		
		
		//3.返回处理
		return ret;
	}
	
	/**
	 * 查询用户与聊天服务对象之间的所有订单信息
	 */
	@RequestMapping(value = "/query_target_order", method = RequestMethod.GET)
	@ResponseBody
	public ResultObject queryTargetOrderList(HttpServletRequest request) {
		JSONObject reqJson = (JSONObject) request.getAttribute("reqJson");
		
		ResultObject ret = new ResultObject();
		
		//1.参数校验
		AcctOrder reqOrder = new AcctOrder();
		String paramErr = checkMyTargetOrderList(reqJson, reqOrder);
		
		if(paramErr != null) {
			ret.setCode(ErrorCode.FAIL_PARAM);
			ret.setMsg(paramErr);
			return ret;
		}
		
		
		//2.业务处理
		try {
			service.queryTargetOrderList(reqOrder, ret);
		} catch (BizException biz) {
			logger.error("查询用户与聊天服务对象之间的所有订单信息异常:", biz);
			ret.setCode(biz.getErrCode());
			ret.setMsg(biz.getMessage());
			return ret;
		} catch (Exception e) {
			logger.error("查询用户与聊天服务对象之间的所有订单信息操作异常:", e);
			ret.setCode(ErrorCode.FAIL_SYS_ERROR);
			ret.setMsg("查询用户与聊天服务对象之间的所有订单信息操作异常");
			ret.setSysmsg(ExceptionUtils.traceExceptionMini(e));
			return ret;
		}
		//jennia
		
		//3.返回处理
		return ret;
	}
	
	/**
	 * 提交订单参数校验
	 */
	private String checkAddOrderParam(JSONObject json, AcctOrder order) {
		Long user_id = json.getLong("user_id");
		if(user_id == null || user_id <= 0) {
			return "购买发起人[user_id]必填";
		}
		order.setUser_id(user_id);
		
		Long target_id = json.getLong("target_id");
		if(target_id == null || target_id <= 0) {
			return "服务人[target_id]必填";
		}
		order.setTarget_id(target_id);
		
		Integer buy_num = json.getInteger("buy_num");
		if(buy_num == null || buy_num <= 0) {
			return "购买数量必填[buy_num]";
		}
		order.setBuy_num(buy_num);

		BigDecimal fee = json.getBigDecimal("fee");
		if(fee == null) {
			return "服务费单价必填[fee]";
		}
		order.setFee(fee);;
		
		String unit = json.getString("fee_unit");
		FeeUnit feeUnit = FeeUnit.getUnit(unit);
		if(feeUnit == null) {
			return "服务费单位必填[fee_unit]";
		}
		order.setFee_unit(feeUnit);
		
		Integer fee_duration = json.getInteger("fee_duration");
		if(fee_duration == null || fee_duration <= 0) {
			fee_duration = 1;
		}
		order.setFee_duration(fee_duration);
		
//		PayChannelType pay_type = PayChannelType.getType(json.getString("pay_type"));
//		if(pay_type == null) {
//			return "付款方式[pay_type]必填";
//		}
//		order.setPay_type(pay_type);
		
		return null;
	}
	
	private String checkNewWithdraw(JSONObject json, AcctWithdraw withdraw) {
		Long user_id = json.getLong("user_id");
		if(user_id == null || user_id <= 0) {
			return "提现发起人[user_id]必填";
		}
		withdraw.setUser_id(user_id);
		
		String acct = json.getString("target_acct");
		PayChannelType acct_type = PayChannelType.getType(acct);
		//目前只限制只能提现到支付宝
		if(acct_type == null || acct_type != PayChannelType.ZFB) {
			return "target_acct填写有误";
		}
		withdraw.setChannel(acct_type);
		
		BigDecimal amt = json.getBigDecimal("amount");
		if(amt == null || !(amt.compareTo(new BigDecimal(0)) > 0)) {
			return "提现金额[amount]填写有误";
		}
		withdraw.setAmount(amt);
		
		return null;
	}
	
	private String checkListOrder(JSONObject json) {
		Integer page_index = json.getInteger("page_index");
		Integer page_size = json.getInteger("page_size");
		Long user_id = json.getLong("user_id");
		Integer type = json.getInteger("type");
		String status = json.getString("status");
		
		if(page_index == null || page_index < 1) {
			return "查询分页[page_index]填写有误";
		}
		
		if(page_size == null || page_size < 1 || page_size > 30) {
			return "查询分页[page_size]填写有误,取值[1,30]";
		}
		
		if(user_id == null || user_id <= 0) {
			return "查询用户[user_id]填写有误";
		}
		
		if(type == null || type > 2 || type < 0) {
			return "查询类型[type]填写有误";
		}
		OrderStatus st = OrderStatus.getStatus(status);
		if(st != null && st != OrderStatus.PAID && st != OrderStatus.DONE) {
			return "查询订单状态[status]填写有误";
		}
		
		return null;
	}
	
	private String checkListWithdraw(JSONObject json) {
		Integer page_index = json.getInteger("page_index");
		Integer page_size = json.getInteger("page_size");
		Long user_id = json.getLong("user_id");
		String status = json.getString("status");
		
		if(page_index == null || page_index < 1) {
			return "查询分页[page_index]填写有误";
		}
		
		if(page_size == null || page_size < 1 || page_size > 30) {
			return "查询分页[page_size]填写有误,取值[1,30]";
		}
		
		if(user_id == null || user_id <= 0) {
			return "查询用户[user_id]填写有误";
		}
		
		
		AuditStatus st = AuditStatus.getStatus(status);
		if(status != null && st == null) {
			return "查询状态[status]填写有误";
		}
		
		return null;
	}
	
	private String checkMyTargetOrderList(JSONObject json, AcctOrder req) {
		Long user_id = json.getLong("user_id");
		Long target_id = json.getLong("target_id");
		String status = json.getString("status");
		
		
		if(user_id == null || user_id <= 0) {
			return "查询用户[user_id]填写有误";
		}
		
		if(target_id == null || target_id <= 0) {
			return "查询目标对象[target_id]填写有误";
		}
		
		OrderStatus st = OrderStatus.getStatus(status);
		if(status != null && st == null) {
			return "查询状态[status]填写有误";
		}
		
		if(st != null && st != OrderStatus.PAID && st != OrderStatus.DONE) {
			return "查询状态[status]填写有误";
		}
		
		req.setUser_id(user_id);
		req.setTarget_id(target_id);
		req.setStatus(st);
		
		return null;
	}
}
