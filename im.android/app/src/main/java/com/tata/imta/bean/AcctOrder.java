package com.tata.imta.bean;

import com.tata.imta.bean.status.FeeUnit;
import com.tata.imta.bean.status.OrderStatus;
import com.tata.imta.bean.status.PayChannelType;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;



/**
 * 用户收支订单详情
 * @author 张伟锐
 *
 */
public class AcctOrder implements Serializable {
	
	/**
	 * 主键,标记一笔支付订单
	 */
	private long order_id;
	
	/**
	 * 用户ID，即买方id
	 */
	private long user_id;
	
	/**
	 * 服务端用户id
	 */
	private long target_id;
	
	//聊天服务费价格
	private BigDecimal fee;
	
	//服务费计算标准
	private FeeUnit fee_unit;
	
	//服务费单位计费时长
	private int fee_duration;
	
	/**
	 * 购买数量
	 */
	private int buy_num;
	
	/**
	 * 服务到期时间 =当前时间+(fee_unit*fee_duration*buy_num)
	 */
	private Date expire_time;
	
	/**
	 * 订单总价 订单总价=fee*buy_num
	 */
	private BigDecimal total_amt;
	
	/**
	 * 付款方式：0-余额支付；1-微信支付；2-支付宝
	 */
	private PayChannelType pay_type;
	
	/**
	 * 付款成功后第三方支付返回的交易流水号
	 */
	private String pay_trade_no;
	
	/**
	 * 订单当前状态：0-已提交；1-已支付；2-支付失败；3-失效(一定时间未成功后自动失效)；4-双方已确认服务完成
	 */
	private OrderStatus status;
	
	/**
	 * 创建时间
	 */
	private Date create_time;
	
	/**
	 * 最后修改时间
	 */
	private Date last_update_time;

	public long getOrder_id() {
		return order_id;
	}

	public void setOrder_id(long order_id) {
		this.order_id = order_id;
	}

	public long getUser_id() {
		return user_id;
	}

	public void setUser_id(long user_id) {
		this.user_id = user_id;
	}

	public long getTarget_id() {
		return target_id;
	}

	public void setTarget_id(long target_id) {
		this.target_id = target_id;
	}

	public BigDecimal getFee() {
		return fee;
	}

	public void setFee(BigDecimal fee) {
		this.fee = fee;
	}

	public FeeUnit getFee_unit() {
		return fee_unit;
	}

	public void setFee_unit(FeeUnit fee_unit) {
		this.fee_unit = fee_unit;
	}

	public int getFee_duration() {
		return fee_duration;
	}

	public void setFee_duration(int fee_duration) {
		this.fee_duration = fee_duration;
	}

	public int getBuy_num() {
		return buy_num;
	}

	public void setBuy_num(int buy_num) {
		this.buy_num = buy_num;
	}

	public Date getExpire_time() {
		return expire_time;
	}

	public void setExpire_time(Date expire_time) {
		this.expire_time = expire_time;
	}

	public BigDecimal getTotal_amt() {
		return total_amt;
	}

	public void setTotal_amt(BigDecimal total_amt) {
		this.total_amt = total_amt;
	}

	public String getPay_trade_no() {
		return pay_trade_no;
	}

	public void setPay_trade_no(String pay_trade_no) {
		this.pay_trade_no = pay_trade_no;
	}

	
	public Date getCreate_time() {
		return create_time;
	}

	public void setCreate_time(Date create_time) {
		this.create_time = create_time;
	}

	public Date getLast_update_time() {
		return last_update_time;
	}

	public void setLast_update_time(Date last_update_time) {
		this.last_update_time = last_update_time;
	}

	public PayChannelType getPay_type() {
		return pay_type;
	}

	public void setPay_type(PayChannelType pay_type) {
		this.pay_type = pay_type;
	}

	public OrderStatus getStatus() {
		return status;
	}

	public void setStatus(OrderStatus status) {
		this.status = status;
	}

	@Override
	public String toString() {
		return "AcctOrder [order_id=" + order_id + ", user_id=" + user_id
				+ ", target_id=" + target_id + ", fee=" + fee + ", fee_unit="
				+ fee_unit + ", fee_duration=" + fee_duration + ", buy_num="
				+ buy_num + ", expire_time=" + expire_time + ", total_amt="
				+ total_amt + ", pay_type=" + pay_type + ", pay_trade_no="
				+ pay_trade_no + ", status=" + status + ", create_time="
				+ create_time + ", last_update_time=" + last_update_time + "]";
	}

	
}
