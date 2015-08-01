package com.tata.imta.constant;

/**
 * Created by Will Zhang on 2015/6/30.
 * 后台服务错误码
 */
public class ServerErrorCode {

    public static final int OK = 0;

    public static final int FAIL_ALL = -1;

    //签名不合法
    public static final int FAIL_SIGN = -1001;

    //参数不合法
    public static final int FAIL_PARAM = -1002;

    //查询结果为空
    public static final int FAIL_RET_EMPTY = -1003;

    //业务处理异常
    public static final int FAIL_BIZ_ERROR = -1004;

    //系统异常
    public static final int FAIL_SYS_ERROR = -1005;
}
