package com.tata.imta.bean.status;

/**
 * Created by Will Zhang on 2015/6/21.
 */
public enum ServerAPI {

    SERVER_API_TEST("/login/test", "GET"),
    SERVER_API_REGISTER("/user/register", "POST"),

    SERVER_API_MOBILE_LOGIN("/login/mobile_login", "POST"),//手机用户登录校验

    SERVER_API_USER_DETAIL("/user/query_info", "GET"),
    SERVER_API_TAB_LIST("/admin/tablist", "GET"),
    SERVER_API_EDIT_BASE("/user/edit_base", "POST"),
    SERVER_API_QINIU_UPTOKEN("/image/uptoken", "GET"),
    SERVER_API_DO_LOGIN("/login/action", "POST"),
    SERVER_API_ADD_REPORT("/extend/add_report", "POST"),//用户举报
    SERVER_API_FIND_USER_LIST("/user/query_userlist", "GET"),//查询在线用户
    SERVER_API_UPDATE_USER_EXTEND("/user/edit_extend", "POST"),//更新用户扩展信息

    SERVER_API_ADD_ORDER("/account/add_order", "POST"),//生成一笔新支付订单
    SERVER_API_GEN_CHARGE("/account/gen_charge", "POST"),//生成支付凭证
    SERVER_API_SYNC_PAY_RESULT("/account/edit_order", "POST"),//支付单付款结果同步

    SERVER_API_QUERY_MY_ACCT("/account/query_info", "GET"),//查询我的账户信息
    SERVER_API_QUERY_MY_ORDER_LIST("/account/order_list", "GET"),//查看我的消费明细
    SERVER_API_QUERY_MY_WITHDRAW_LIST("/account/withdraw_list", "GET"),//查看我的提现记录

    SERVER_API_CONFIRM_ORDER_DONE("/account/confirm_order", "POST"),//确认订单完成

    SERVER_API_APPLY_WITHDRAW("/account/add_withdraw", "POST"),//用户申请提现操作

    SERVER_API_QUERY_TARGET_ORDER_LIST("/account/query_target_order", "GET"),//查询用户与聊天对象之间的有效订单列表

    SERVER_API_VERSION_INFO("/admin/query_version", "GET"),//服务器端最新的版本号
    ;
    private String uri;

    private String method;

    private ServerAPI(String uri, String method) {
        this.uri = uri;
        this.method = method;
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }
}
