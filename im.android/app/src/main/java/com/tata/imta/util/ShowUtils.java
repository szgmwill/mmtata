package com.tata.imta.util;

import com.tata.imta.bean.UserExtend;
import com.tata.imta.bean.status.FeeUnit;
import com.tata.imta.helper.LogHelper;

import java.math.BigDecimal;

/**
 * Created by Will Zhang on 2015/6/20.
 * 处理展示时内容的工具
 */
public class ShowUtils {

    /**
     * 处理当字符串超过指定长度时,截断显示
     */
    public static String showTextLimit(String content, int limit) {
        if(content != null && content.length() > limit) {
            content = content.substring(0, limit);
            return content+"...";
        }

        return content;
    }

    /**
     * 判断是否数字
     */
    public static long parseLong(String name) {
        if(name != null) {
            try {
                return Long.parseLong(name);
            } catch (Exception e) {
                LogHelper.debug(ShowUtils.class, "name["+name+"] parse not num");
            }

        }
        return 0;
    }

    /**
     * 展示资费单位
     */
    public static String showUnit(UserExtend extend) {
        if(extend != null) {
            BigDecimal fee = extend.getFee();
            FeeUnit fee_unit = extend.getFee_unit();
            int fee_duration = extend.getFee_duration();

            if(fee != null && fee_unit != null && fee_duration > 0) {
                if(fee_duration > 1) {
                    return "/"+fee_duration+fee_unit.getDesc();
                } else {
                    return "/"+fee_unit.getDesc();
                }
            }
        }


        return "";
    }

    /**
     * 展示资费单位
     */
    public static String showUnit(FeeUnit unit, int duration) {
        if(unit != null) {
            if(duration > 1) {
                return "/"+duration+unit.getDesc();
            } else {
                return "/"+unit.getDesc();
            }
        }
        return "";
    }

    /**
     * 展示订单聊天时长
     */
    public static String showOrderServiceTime(FeeUnit fee_unit, int duration, int buyNum) {
        if(fee_unit != null && duration > 0) {

            return ""+duration*buyNum+fee_unit.getDesc();
        }


        return "";
    }
}
