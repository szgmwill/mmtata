package com.mmtata.im.server.bean;
/**
 * 支付凭证
 * @author 张伟锐
 *
 */
/**
 *  order_no: required
	商户订单号，适配每个渠道对此参数的要求，必须在商户系统内唯一。(alipay: 1-64 位， wx: 1-32 位，bfb: 1-20 位，upacp: 8-40 位，yeepay_wap:1-50 位，jdpay_wap:1-30 位，推荐使用 8-20 位，要求数字或字母，不允许特殊字符)。
	app[id]: required
	支付使用的 app 对象的 id。
	channel: required
	支付使用的第三方支付渠道，取值范围
	amount: required
	订单总金额, 单位为对应币种的最小货币单位，例如：人民币为分（如订单总金额为 1 元，此处请填 100）。
	client_ip: required
	发起支付请求终端的 IP 地址，格式为 IPV4，如: 127.0.0.1。
	currency: required
	三位 ISO 货币代码，目前仅支持人民币 cny。
	subject: required
	商品的标题，该参数最长为 32 个 Unicode 字符，银联限制在 32 个字节。
	body: required
	商品的描述信息，该参数最长为 128 个 Unicode 字符，yeepay_wap 对于该参数长度限制为 100 个 Unicode 字符。
	extra: optional
	特定渠道发起交易时需要的额外参数。
	time_expire: optional
	订单失效时间，用 UTC 时间表示。时间范围在订单创建后的 1 分钟到 15 天，默认为 1 天，创建时间以 Ping++ 服务器时间为准。该参数不适用于微信支付渠道。
	metadata: optional
	参考 Metadata 元数据。
	description: optional
	订单附加说明，最多 255 个 Unicode 字符。*/
public class PingCharge {
	private long order_no;
	
	private String app_id;
	
	/**
	 * 支付渠道
	 * wx:微信支付
	 * wx_pub:微信公众账号支付
	 * wx_pub_qr:微信公众账号扫码支付
	 * alipay:支付宝手机支付
	 * alipay_qr:支付宝扫码支付
	 */
	private String channel;
	
	private long amount;
	
	private String client_ip;
	
	private String currency = "cny";
	
	private String subject;
	
	private String body;

	public long getOrder_no() {
		return order_no;
	}

	public void setOrder_no(long order_no) {
		this.order_no = order_no;
	}

	public String getApp_id() {
		return app_id;
	}

	public void setApp_id(String app_id) {
		this.app_id = app_id;
	}

	public String getChannel() {
		return channel;
	}

	public void setChannel(String channel) {
		this.channel = channel;
	}

	public long getAmount() {
		return amount;
	}

	public void setAmount(long amount) {
		this.amount = amount;
	}

	public String getClient_ip() {
		return client_ip;
	}

	public void setClient_ip(String client_ip) {
		this.client_ip = client_ip;
	}

	public String getCurrency() {
		return currency;
	}

	public void setCurrency(String currency) {
		this.currency = currency;
	}

	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	public String getBody() {
		return body;
	}

	public void setBody(String body) {
		this.body = body;
	}

	@Override
	public String toString() {
		return "PingCharge [order_no=" + order_no + ", app_id=" + app_id
				+ ", channel=" + channel + ", amount=" + amount
				+ ", client_ip=" + client_ip + ", currency=" + currency
				+ ", subject=" + subject + ", body=" + body + "]";
	}
	
}
