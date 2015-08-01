/**
 * 深圳市塔塔互动
 * 作者:张伟锐
 * 2015年5月17日
 */
package com.mmtata.im.server.util;

import org.apache.commons.lang.StringUtils;

/**
 * 参数校验工具类
 */
public class CheckHelper {
	
	
	/**
	 * 校验用户登录密码
	 * 本来这里加密应该以user_id比较安全,暂时以手机号
	 */
	public static boolean isValidUserPwd(String mobile, String correct, String check) {
		
		if(StringUtils.isNotBlank(check)) {
			//先用提交密码得到结果
			String checkMd5 = MD5Utils.MD5Encode(mobile + "-" + check);
			if(checkMd5.equals(correct)) {
				return true;
			}
		}
		
		return false;
	}
	
	/**
	 * 得到保存在DB里的用户登录密码
	 * 本来这里加密应该以user_id比较安全,暂时以手机号
	 */
	public static String genSavePassword(String mobile, String md5Pwd) {
		if(StringUtils.isNotBlank(mobile) && StringUtils.isNotBlank(md5Pwd)) {
			String ori = mobile + "-" + md5Pwd;
			
			String saveMd5 = MD5Utils.MD5Encode(ori);
			
			return saveMd5;
		}
		
		
		return "";
	}
}
