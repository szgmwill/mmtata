package com.tata.imta.helper;

import android.app.Activity;
import android.content.Context;
import android.text.TextUtils;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.tata.imta.app.BaseActivity;
import com.tata.imta.app.BizInfoHolder;
import com.tata.imta.bean.ImgInfo;
import com.tata.imta.bean.ResultObject;
import com.tata.imta.bean.User;
import com.tata.imta.bean.UserExtend;
import com.tata.imta.bean.UserFind;
import com.tata.imta.bean.status.ServerAPI;
import com.tata.imta.bean.status.TabInfo;
import com.tata.imta.constant.ServerErrorCode;
import com.tata.imta.util.HttpUtils;
import com.tata.imta.util.JsonUtils;
import com.tata.imta.util.SignUtils;
import com.tata.imta.util.ToastUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Will Zhang on 2015/6/3.
 * 后台API工具类
 */
public class ServerAPIHelper {

    //生产环境
    public static final String BASE_URI  = "http://www.mmtata.com/api";

    //本地测试
//    public static final String BASE_URI  = "http://192.168.2.107:8080/im.server";
//    public static final String BASE_URI  = "http://192.168.43.157:8080/im.server";

//    public static final String BASE_URI  = "http://192.168.0.103:8080/im.server";
//    public static final String BASE_URI  = "http://192.168.0.106:8080/im.server";
    /**
     * 后台成功返回码
     */
    public static final int ServerOK = 0;

    /**
     * 与后台接口必填固定参数,同时参与签名校验
     * app平台类型
     */
    private static final String clientPF = "android";

    public static Map<String,String> genServerReq(Object params, String uri) {
        Map<String,String> reqMap = new HashMap<>();
        //业务请求参数
        reqMap.put("reqData", JsonUtils.toJson(params));

        //如果存在登录用户的话,要加入身份认证
        String token = "";
        User user = (BizInfoHolder.getInstance() == null ? null : BizInfoHolder.getInstance().getLoginUser());
        if(user != null) {
            token = user.getWxOpenId();
        }

        //调用接口必填参数
        String timestamp = String.valueOf(System.currentTimeMillis());
        reqMap.put("client", clientPF);
        reqMap.put("timestamp", timestamp);
//        String sign = SignUtils.makeSign(uri, clientPF, timestamp);
        //just for local test
        String sign = SignUtils.makeSign("/im.server" + uri, clientPF, timestamp, token);
        reqMap.put("signkey", sign);//安全签名值

        return reqMap;
    }

    /**
     * 处理与后台API交互
     */
    public static ResultObject invokeAPI(Object params, ServerAPI api) {
        if(api != null) {
            Map<String,String> reqMap = genServerReq(params, api.getUri());//生成总的请求参数
            LogHelper.debug(ServerAPIHelper.class, "Server Req:API["+api+"], DATA["+JsonUtils.toJson(reqMap)+"]");
            try {
                String result = null;
                String reqUrl = BASE_URI+api.getUri();
                if(HttpUtils.METHOD_GET.equalsIgnoreCase(api.getMethod())) {
                    result = HttpUtils.doGet(reqUrl, reqMap);
                } else {
                    result = HttpUtils.doPost(reqUrl, reqMap);
                }

                if(!TextUtils.isEmpty(result)) {
                    ResultObject resultObject = JsonUtils.json2Obj(result, ResultObject.class);
                    if(resultObject != null) {
                        int retCode = resultObject.getCode();
                        if (retCode != ServerOK) {
                            LogHelper.info(ServerAPIHelper.class, "uri[" + api.getUri() + "],return error:code[" + retCode + "]" + ",msg[" + resultObject.getMsg() + "]");
                        } else {
                            //成功
                        }

                        return resultObject;
                    }
                }

            } catch (IOException ioe) {
                ioe.printStackTrace();
                LogHelper.error(ServerAPIHelper.class, "调用后台API["+api.getUri()+"]IO异常", ioe);
            } catch (Exception e) {
                e.printStackTrace();
                LogHelper.error(ServerAPIHelper.class, "调用后台API[" + api.getUri() + "]异常", e);
            }
        }

        return null;
    }

    public static User getUserDetailFromResult(JSONObject result) {
        //开始保存相关用户信息
        JSONObject baseJson = result.getJSONObject("base");
        User user = null;
        if(baseJson != null) {
            user = UserHelper.transferUserJson(baseJson.toJSONString());
        }
        if(user == null) {
            user = new User();
        }
        //tab list
        List<TabInfo> tablist = UserHelper.getTabList(result.getJSONArray("tab_list"));


        if(tablist != null && tablist.size() > 0) {
            user.getTabList().addAll(tablist);
        }

        //读取其它信息保存
        //相册信息
        List<ImgInfo> imgList = UserHelper.getImgList(user.getUserId(), result.getJSONArray("img_list"));
        if(imgList.size() == 0) {
            ImgInfo headImg = new ImgInfo();
            headImg.setUrl(user.getHead());
            headImg.setUserId(user.getUserId());
            imgList.add(headImg);
        }
        user.getImgList().addAll(imgList);

        //扩展信息
        //用户扩展信息
        JSONObject extendJson = result.getJSONObject("extend");
        if(extendJson != null) {
            LogHelper.debug(LoginHelper.class, "查询到用户[" + user.getUserId() + "]扩展信息:" + extendJson.toJSONString());

            UserExtend extend = JsonUtils.json2Obj(extendJson.toString(), UserExtend.class);
            if(extend != null) {
                user.setExtend(extend);
            }
        }


        LogHelper.debug(ServerAPIHelper.class, "User Detail From Server:" + JsonUtils.toJson(user));
        return user;
    }

    public static User getUserDetailFromServer(long userId) {
        if(userId > 0) {
            Map<String,String> paramMap = new HashMap<>();
            paramMap.put("user_id", String.valueOf(userId));

            ResultObject serverRO = invokeAPI(paramMap, ServerAPI.SERVER_API_USER_DETAIL);
            if(serverRO != null && serverRO.getCode() == ServerOK) {

                //成功
                JSONObject dataObj = JsonUtils.formatDataObj(serverRO.getData());
                if (dataObj != null) {
                    return getUserDetailFromResult(dataObj);
                }
            }
        }

        return null;
    }

    /**
     * 获取七牛上传凭证
     */
    public static String getQiniuUpToken(long userId) {
        //先从后台获取上传凭证
        Map<String, String> paramMap = new HashMap<>();
        paramMap.put("user_id", String.valueOf(userId));

        ResultObject serverRO = invokeAPI(paramMap, ServerAPI.SERVER_API_QINIU_UPTOKEN);
        if(serverRO != null && serverRO.getCode() == ServerOK) {

            //成功
            return JsonUtils.getStringFromDataObj(serverRO.getData(), "uptoken");
        }
        return null;
    }

    /**
     * 查询在线用户列表
     */
    public static List<UserFind> findUserList(int pageIndex, int sex) {
        List<UserFind> findList = new ArrayList<>();
        Map<String, Object> paramMap = new HashMap<>();
//        paramMap.put("user_id", BizInfoHolder.getInstance().getLoginUser().getUserId());
        paramMap.put("page_index", pageIndex);
        paramMap.put("page_size", 10);//暂时固定每页10个
        paramMap.put("sex", sex);
        ResultObject serverRO = invokeAPI(paramMap, ServerAPI.SERVER_API_FIND_USER_LIST);

        if(serverRO != null && serverRO.getCode() == ServerOK) {

            JSONArray array = JsonUtils.getListFromDataObj(serverRO.getData(), "user_list");
            if(array != null && array.size() > 0) {
                List<UserFind> tempList = new ArrayList<>();
                LogHelper.debug(ServerAPIHelper.class, "onDataCallBack find userlist:" + array.size());

                for(int i=0;i<array.size();i++) {
                    JSONObject json = (JSONObject) array.get(i);
                    UserFind find = JsonUtils.json2Obj(json.toString(), UserFind.class);
                    if(find != null && !UserHelper.isLoginUser(find.getUserId())) {
                        tempList.add(find);
                    }
                }

                if(tempList.size() > 0) {
                    findList.addAll(tempList);
                }
            }
        }

        return findList;
    }

    /**
     * 统一处理后台返回结果
     */
    public static <T> T handleServerResult(Context context, ResultObject ro, Class<T> target) {
        if(ro != null && target != null) {
            JSONObject dataJson = null;
            if(ro.getCode() == 0 && ro.getData() != null) {//成功
                dataJson = JsonUtils.formatDataObj(ro.getData());
            } else if(ro.getCode() == 0 && ro.getData() == null) {
                LogHelper.debug(context, "后台返回成功但没有data");
            } else if (ro.getCode() != 0) {
                if(context instanceof BaseActivity) {
                    //直接弹出提示
                    ((BaseActivity) context).showAlertDialog("操作提示", "后台处理数据失败");
                } else {
                    LogHelper.debug(context, "后台返回失败:"+ro.getCode()+",msg:"+ro.getMsg());
                }
            }
            if(dataJson != null) {
                if(target == ArrayList.class) {
                    //转换成列表
                    JSONArray array = dataJson.getJSONArray("data_list");
                    if(array != null) {
                        return JsonUtils.json2Obj(array.toString(), target);
                    }
                } else {
                    //一般的对象
                    return JsonUtils.json2Obj(dataJson.toString(), target);
                }

            }
        }

        return null;
    }

    public static JSONObject handleServerResult(Context context, ResultObject ro) {
        JSONObject dataJson = null;

        if(ro != null) {
            if(ro.getCode() == 0 && ro.getData() != null) {//成功
                dataJson = JsonUtils.formatDataObj(ro.getData());
            } else if(ro.getCode() == 0 && ro.getData() == null) {
                LogHelper.debug(context, "后台返回成功但没有data");
            } else if (ro.getCode() != 0) {
                LogHelper.debug(context, "后台返回失败:"+ro.getCode()+",msg:"+ro.getMsg());
//                if(context instanceof BaseActivity) {
//                    //直接弹出提示
//                    ((BaseActivity) context).showAlertDialog("操作提示", "后台处理数据失败");
//                } else {
//                    LogHelper.debug(context, "后台返回失败:"+ro.getCode()+",msg:"+ro.getMsg());
//                }

            }
        }

        return dataJson;
    }

    /**
     * 展示后台业务失败
     */
    public static boolean showErrorMsg(Activity context, ResultObject ro) {
        boolean isShowErr = false;

        if(context != null && ro != null) {
            if(ro.getCode() == ServerErrorCode.OK) {
                return false;
            } else {
                isShowErr = true;

                if(ro.getCode() == ServerErrorCode.FAIL_BIZ_ERROR) {
                    if(context instanceof BaseActivity) {
                        BaseActivity activity = ((BaseActivity) context);
                        activity.dismissProgressDialog();
                        activity.showAlertDialog("操作提示", ro.getMsg());
                    } else {
                        ToastUtils.showShortToast(context, ro.getMsg());
                    }
                } else {
                    LogHelper.debug(context, "后台返回错误:"+ro.getMsg());
                }
            }
        }

        return isShowErr;
    }
}
