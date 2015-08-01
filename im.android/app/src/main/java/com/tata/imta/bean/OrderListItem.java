package com.tata.imta.bean;

import com.tata.imta.bean.status.FeeUnit;
import com.tata.imta.bean.status.OrderStatus;
import com.tata.imta.bean.status.PayChannelType;

import java.math.BigDecimal;
import java.util.Date;


/**
 * 订单展示对象
 */
public class OrderListItem {
	
	private long my_id;
	
	private long target_id;
	
	private String target_nick;
	
	private long order_id;
	
	/**
	 * 收支类型:1-支出；2-收入
	 */
	private int type;
	
	/**
	 * 聊天服务时长
	 */
	//聊天服务费价格
	private BigDecimal fee;
	
	//服务费计算标准
	private FeeUnit fee_unit;
	
	//服务费单位计费时长
	private int fee_duration;
	
	//购买数量
	private int buy_num;
	
	private Date expire_time;
	
	//订单总价
	private BigDecimal total_amt;
	
	private PayChannelType pay_type;
	
	private OrderStatus status;
	
	private Date create_time;
	
	public long getMy_id() {
		return my_id;
	}

	public void setMy_id(long my_id) {
		this.my_id = my_id;
	}

	public long getTarget_id() {
		return target_id;
	}

	public void setTarget_id(long target_id) {
		this.target_id = target_id;
	}

	public String getTarget_nick() {
		return target_nick;
	}

	public void setTarget_nick(String target_nick) {
		this.target_nick = target_nick;
	}

	public long getOrder_id() {
		return order_id;
	}

	public void setOrder_id(long order_id) {
		this.order_id = order_id;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
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

	public BigDecimal getTotal_amt() {
		return total_amt;
	}

	public void setTotal_amt(BigDecimal total_amt) {
		this.total_amt = total_amt;
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

	public Date getCreate_time() {
		return create_time;
	}

	public void setCreate_time(Date create_time) {
		this.create_time = create_time;
	}

	public Date getExpire_time() {
		return expire_time;
	}

	public void setExpire_time(Date expire_time) {
		this.expire_time = expire_time;
	}
	
	
}
