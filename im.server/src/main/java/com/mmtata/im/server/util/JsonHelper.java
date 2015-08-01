/**
 * 深圳市塔塔互动
 * 作者:张伟锐
 * 2015年5月17日
 */
package com.mmtata.im.server.util;

import java.util.Date;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.mmtata.im.server.bean.UserBase;
import com.mmtata.im.server.bean.UserFind;
import com.mmtata.im.server.status.Sex;

public class JsonHelper {
	
	private static final Logger logger = Logger.getLogger(JsonHelper.class);
	
	public static <T> T jsonStr2Obj (String jsonStr, Class<T> target) {
		if(StringUtils.isNotBlank(jsonStr)) {
			try {
				return JSON.parseObject(jsonStr, target);
			} catch (Exception e) {
				logger.error("json 2 obj error:"+e);
			}
		}
		return null;
	}
	
	public static JSONObject genJsonObj(String jsonStr) {
		if(StringUtils.isNotBlank(jsonStr)) {
			try {
				return JSON.parseObject(jsonStr);
			} catch (Exception e) {
				logger.error("genJsonObj error:"+e);
			}
		}
		return null;
	}
	
	public static <T> T json2Obj(String jsonStr, Class<T> target) {
		if(StringUtils.isNotBlank(jsonStr)) {
			try {
				return JSON.parseObject(jsonStr, target);
			} catch (Exception e) {
				logger.error("json2Obj error:"+e);
			}
		}
		return null;
	}
	
	/**
     * 对象序列化成json
     */
    public static String toJson(Object target) {
        if(target != null) {
            try {
                return JSON.toJSONString(target, SerializerFeature.WriteNullStringAsEmpty);
            } catch (Exception e) {
                e.printStackTrace();
                logger.error("toJson error:", e);
            }
        }

        return "";
    }
	
	/**
     * 将序列化的用户信息反序例化成对象
     */
    public static UserBase transferUserJson(String jsonStr) {
        if(!StringUtils.isEmpty(jsonStr)) {
            try {
                JSONObject jsonObj = (JSONObject)JSON.parse(jsonStr);
                UserBase user = JsonHelper.json2Obj(jsonStr, UserBase.class);

                if(user != null) {
                    Sex sex = Sex.valueOf(jsonObj.getString("sex"));
                    if(sex != null) {
                        user.setSex(sex);
                    }

                    return user;
                }

            } catch (Exception e) {

            }


        }

        return null;
    }
	
	public static void main(String[] args) {
		UserBase base = new UserBase();
		
		base.setUser_id(10199);
		base.setNick("张妮娅");
		base.setBirth(new Date());
		base.setCareer("公关");
		base.setLocation("深圳");
		base.setSex(Sex.GIRL);
		base.setSign("人生座佑铭:我选择，我喜欢");
		base.setHead(null);
		
		String json = "{\"birth\",\"career\":\"公关\",\"location\":\"深圳\",\"nick\":\"张妮娅\",\"sex\":\"GIRL\",\"sign\":\"人生座佑铭:我选择，我喜欢\",\"user_id\":10199}";//JSON.toJSONString(base);
		
		System.out.println("json string:"+json);
		String test = "{\"nick\":\"测试用户\",\"head\":\"http://img.qiniu.com/001.jpg\",\"tab_list\":[{\"tab_type\":1,\"tab_name\":\"我是大哥\"},{\"tab_type\":3,\"tab_name\":\"我是你妹\"}],\"openid\":\"abc123\",\"sex\":\"BOY\"}";
		UserBase transfer = JsonHelper.jsonStr2Obj(test, UserBase.class);
		
		System.out.println("transfer:" + (transfer != null ? transfer.toString() : "空"));
		
		
		String testUser = "{\"birth\":\"1985-01-02 00:00:00\",\"career\":\"程序狗\",\"head\":\"sfjkfdljsfkdlsfj\",\"location\":\"深圳\",\"mobile\":null,\"nick\":\"张木头\",\"pwd\":null,\"sex\":\"BOY\",\"sign\":\"hello world\",\"tabList\":\"[]\",\"wxOpenId\":\"wx_openid_101\",\"userId\":\"10106\"}";
		UserBase user = JsonHelper.transferUserJson(testUser);
		
		System.out.println("User:"+user.toString());
		
		String testJson = "{\"birth\":\"2015-06-19\",\"fee\":20.00,\"fee_price\":\"20/天\",\"head\":\"http://7xjkrr.com1.z0.glb.clouddn.com/10108-1-1433925385994.png\",\"last_login_time\":1434785774000,\"nick\":\"张伟锐\",\"sex\":\"BOY\",\"show_unit\":\"/天\",\"sign\":\"我是一只产品狗\",\"user_id\":10107}";
		
		UserFind userFind = JsonHelper.json2Obj(testJson, UserFind.class);
		if(userFind != null) {
			System.out.println("userFind:"+userFind.toString());
		}
	}
}
