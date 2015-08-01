package com.tata.imta.util;

import android.text.TextUtils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.tata.imta.helper.LogHelper;

/**
 * Created by Will Zhang on 2015/5/25.
 * 使用fastjson处理json
 */
public class JsonUtils {

    /**
     * 对象序列化成json
     */
    public static String toJson(Object target) {
        if(target != null) {
            try {
                return JSON.toJSONString(target, SerializerFeature.WriteNullStringAsEmpty);
            } catch (Exception e) {
                e.printStackTrace();
                LogHelper.error(JsonUtils.class, "toJson error:", e);
            }
        }

        return "";
    }

    /**
     * 反序例化成对象
     */
    public static <T> T json2Obj (String jsonStr, Class<T> target) {
        if(!TextUtils.isEmpty(jsonStr)) {
            try {
                return JSON.parseObject(jsonStr, target);
            } catch (Exception e) {
                e.printStackTrace();
                LogHelper.error(JsonUtils.class, "json2Obj error:", e);
            }
        }

        return null;
    }

    /**
     * 是否标准json,严格语法
     */
    public static boolean isValidJson(String jsonStr) {
        if(!TextUtils.isEmpty(jsonStr)) {
            try {
                JSONObject json = (JSONObject) JSON.parse(jsonStr);
            } catch (Exception e) {
                e.printStackTrace();
                LogHelper.error(JsonUtils.class, "isValidJson error:", e);
            }
        }

        return false;
    }

    public static JSONObject formatJson(String jsonStr) {
        if(!TextUtils.isEmpty(jsonStr)) {
            try {
                JSONObject json = (JSONObject) JSON.parse(jsonStr);
                return json;
            } catch (Exception e) {
                e.printStackTrace();
                LogHelper.error(JsonUtils.class, "formatJson error:", e);
            }
        }

        return null;
    }

    public static JSONObject formatDataObj(Object obj) {
        String json = toJson(obj);
        if(!TextUtils.isEmpty(json)) {
            return formatJson(json);
        }

        return null;
    }

    public static String getStringFromDataObj(Object obj, String key) {
        String value = "";
        if(obj != null && key != null) {
            JSONObject json = formatDataObj(obj);
            if(json != null) {
                value = json.getString(key);
            }
        }

        return value;
    }

    public static JSONArray getListFromDataObj(Object obj, String key) {
        JSONArray value = null;
        if(obj != null && key != null) {
            JSONObject json = formatDataObj(obj);
            if(json != null) {
                value = json.getJSONArray(key);
            }
        }

        return value;
    }
}
