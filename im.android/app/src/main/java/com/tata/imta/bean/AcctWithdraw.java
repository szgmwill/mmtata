package com.tata.imta.bean;

import com.tata.imta.bean.status.AuditStatus;
import com.tata.imta.bean.status.PayChannelType;
import com.tata.imta.bean.status.WithdrawResult;

import java.math.BigDecimal;
import java.util.Date;


/**
 * 提现单详情
 * @author 张伟锐
 *
 */
public class AcctWithdraw {
	/**
	 * 主键,提现单id
	 */
	private long withdraw_id;
	
	/**
	 * 用户ID，即提现方
	 */
	private long user_id;
	
	/**
	 * 提现前的账户余额(快照)
	 */
	private BigDecimal balance;
	
	/**
	 * 提现金额
	 */
	private BigDecimal amount;
	
	/**
	 * 提现通道：1-微信；2-支付宝； 即希望把提现的钱打款到哪类账户上，目前仅支持第三方支付
	 */
	private PayChannelType channel;
	
	/**
	 * 审核状态：0-未审核；1-审核通过；2-不通过
	 */
	private AuditStatus status;
	
	/**
	 * 审核意见,如果审核不通过都要填写意见
	 */
	private String comment;
	
	/**
	 * 提现结果：0-未处理；1-提现成功；2-提现失败,真正提现操作时第三方支付的返回结果
	 */
	private WithdrawResult result;
	
	/**
	 * 提现成功后,第三方的操作流水号,交易流水号
	 */
	private String trade_no;
	
	/**
	 * 创建时间
	 */
	private Date create_time;
	
	/**
	 * 最后修改时间
	 */
	private Date last_update_time;

	public long getWithdraw_id() {
		return withdraw_id;
	}

	public void setWithdraw_id(long withdraw_id) {
		this.withdraw_id = withdraw_id;
	}

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

	public BigDecimal getAmount() {
		return amount;
	}

	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}

	
	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}


	public String getTrade_no() {
		return trade_no;
	}

	public void setTrade_no(String trade_no) {
		this.trade_no = trade_no;
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
	
	public PayChannelType getChannel() {
		return channel;
	}

	public void setChannel(PayChannelType channel) {
		this.channel = channel;
	}

	public AuditStatus getStatus() {
		return status;
	}

	public void setStatus(AuditStatus status) {
		this.status = status;
	}

	public WithdrawResult getResult() {
		return result;
	}

	public void setResult(WithdrawResult result) {
		this.result = result;
	}

	@Override
	public String toString() {
		return "AcctWithdraw [withdraw_id=" + withdraw_id + ", user_id="
				+ user_id + ", balance=" + balance + ", amount=" + amount
				+ ", channel=" + channel + ", status=" + status + ", comment="
				+ comment + ", result=" + result + ", trade_no=" + trade_no
				+ ", create_time=" + create_time + ", last_update_time="
				+ last_update_time + "]";
	}
	
	
}
