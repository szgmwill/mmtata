package com.tata.imta.bean;

import com.tata.imta.bean.status.AcctStatus;

import java.math.BigDecimal;
import java.util.Date;


/**
 * 我的账户相关信息
 * @author 张伟锐
 *
 */
public class AcctInfo {
	
	/**
	 * 用户ID
	 */
	private long user_id;
	
	/**
	 * 当前账户余额
	 */
	private BigDecimal balance;
	
	/**
	 * 总已经提现金额
	 */
	private BigDecimal withdraw_amt;
	
	/**
	 * 账户状态：正常，冻结
	 */
	private AcctStatus status;
	
	/**
	 * 账户的操作密码，比如提现等，用于二次密码防护
	 */
	private String pay_pw;
	
	/**
	 * 创建时间
	 */
	private Date create_time;
	
	/**
	 * 最后修改时间
	 */
	private Date last_update_time;

	public long getUser_id() {
		return user_id;
	}

	public void setUser_id(long user_id) {
		this.user_id = user_id;
	}

	public BigDecimal getBalance() {
		return balance;
	}

	public void setBalance(BigDecimal balance) {
		this.balance = balance;
	}

	public BigDecimal getWithdraw_amt() {
		return withdraw_amt;
	}

	public void setWithdraw_amt(BigDecimal withdraw_amt) {
		this.withdraw_amt = withdraw_amt;
	}

	
	public AcctStatus getStatus() {
		return status;
	}

	public void setStatus(AcctStatus status) {
		this.status = status;
	}

	public String getPay_pw() {
		return pay_pw;
	}

	public void setPay_pw(String pay_pw) {
		this.pay_pw = pay_pw;
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

	@Override
	public String toString() {
		return "AcctInfo [user_id=" + user_id + ", balance=" + balance
				+ ", withdraw_amt=" + withdraw_amt + ", status=" + status
				+ ", pay_pw=" + pay_pw + ", create_time=" + create_time
				+ ", last_update_time=" + last_update_time + "]";
	}
	
}
