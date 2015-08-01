package com.tata.imta.helper;

import com.alibaba.fastjson.JSONObject;
import com.gotye.api.GotyeAPI;
import com.gotye.api.GotyeMessage;
import com.gotye.api.GotyeMessageType;
import com.gotye.api.GotyeUser;
import com.tata.imta.bean.AcctOrder;
import com.tata.imta.bean.User;
import com.tata.imta.util.DateUtils;
import com.tata.imta.util.JsonUtils;
import com.tata.imta.util.ShowUtils;

/**
 * Created by Will Zhang on 2015/7/5.
 * 聊天相关操作帮助工具类
 */
public class ChatHelper {

    /**
     * 根据我接收到我消息展示消息内容
     */
    public static String showChatMessage(GotyeMessage message) {
        String showMsg = "";
        if(message != null) {
            //图片和语音没有文本消息
            if(message.getType() == GotyeMessageType.GotyeMessageTypeImage) {
                //图片
                showMsg = "[图片]";
            } else if(message.getType() == GotyeMessageType.GotyeMessageTypeAudio) {
                //语音
                showMsg = "[语音]";
            } else if(message.getType() == GotyeMessageType.GotyeMessageTypeText) {
                //文本,注意带表情的富文本
                showMsg = ShowUtils.showTextLimit(message.getText(), 20);
            } else {
                showMsg = "[其它]";
            }
        }

        return showMsg;
    }

    /**
     * 通知栏里显示收到的消息
     */
    public static String showNotifyMsg(GotyeMessage message) {
        String showMsg = "你有新未知消息";
        if(message != null) {
            //图片和语音没有文本消息
            if(message.getType() == GotyeMessageType.GotyeMessageTypeImage) {
                //图片
                showMsg = "你有新图片消息";
            } else if(message.getType() == GotyeMessageType.GotyeMessageTypeAudio) {
                //语音
                showMsg = "你有新语音消息";
            } else if(message.getType() == GotyeMessageType.GotyeMessageTypeText) {
                //文本
                showMsg = "你有新文本消息";
            } else {
                showMsg = "[其它]";
            }
        }

        return showMsg;
    }

    /**
     * 判断收到我推送消息是我的吗
     */
    public static boolean isMyPushMessage(GotyeMessage message) {
        boolean ret = false;
        GotyeUser me = GotyeAPI.getInstance().getLoginUser();
        if(me != null && message != null) {
            if(me.getName().equals(message.getReceiver().getName())) {
                ret = true;
            }
        }

        return ret;
    }

    /**
     * 发送一条自定义关注或付款消息
     */
    public static void sendAddFollowMsg(User send, User recv) {
        if(send != null && recv != null) {
            GotyeUser sender = new GotyeUser(String.valueOf(send.getUserId()));
            GotyeUser recver = new GotyeUser(String.valueOf(recv.getUserId()));

            String text = "["+send.getNick()+"]已经关注了["+recv.getNick()+"],在关注列表里可以找到Ta哦~";

            GotyeMessage toSend = GotyeMessage.createTextMessage(sender, recver, text);

            //消息附加信息
            String extraStr = "follow";
            if (extraStr != null) {
                toSend.putExtraData(extraStr.getBytes());
            }

            // putExtre(toSend);
            int code = GotyeAPI.getInstance().sendMessage(toSend);
            LogHelper.debug(ChatHelper.class, "sendMessage for text, code["+code+"]");
        }


    }

    /**
     * 发送一条已经付款并且关注了对方的消息
     */
    public static void sendPayMsg(User send, User recv, AcctOrder order) {
        if(send != null && recv != null) {
            GotyeUser sender = new GotyeUser(String.valueOf(send.getUserId()));
            GotyeUser recver = new GotyeUser(String.valueOf(recv.getUserId()));

            String text = "["+send.getNick()+"]已经为["+recv.getNick()+"]聊天服务" +
                    "付款["+order.getTotal_amt()+"],有效聊天时间将在["+ DateUtils.getTimeStrFromDate(order.getExpire_time())+"]过期,请注意查看哦~";

            GotyeMessage toSend = GotyeMessage.createTextMessage(sender, recver, text);

            //消息附加信息
            String extraStr = "pay";
            if (extraStr != null) {
                toSend.putExtraData(extraStr.getBytes());
            }

            // putExtre(toSend);
            int code = GotyeAPI.getInstance().sendMessage(toSend);
            LogHelper.debug(ChatHelper.class, "sendMessage for text, code["+code+"]");
        }
    }

    /**
     * 发送自定义附加消息
     */
    public static GotyeMessage sendUserDIYMsg(User send, User recv, String extra) {
        if(send != null && recv != null && extra != null) {
            GotyeUser sender = new GotyeUser(String.valueOf(send.getUserId()));
            GotyeUser recver = new GotyeUser(String.valueOf(recv.getUserId()));

            GotyeMessage toSend = GotyeMessage.createUserDataMessage(sender, recver, extra.getBytes(), extra.length());

            // putExtre(toSend);
            int code = GotyeAPI.getInstance().sendMessage(toSend);
            LogHelper.debug(ChatHelper.class, "sendUserDIYMsg, code["+code+"], extra["+extra+"]");

            return toSend;
        }
        return null;
    }

    /**
     * 发送一条文本消息
     */
    public static void sendTextMessage(User send, User recv, String text) {
        if(send != null && recv != null && text != null) {
            GotyeUser sender = new GotyeUser(String.valueOf(send.getUserId()));
            GotyeUser recver = new GotyeUser(String.valueOf(recv.getUserId()));

            GotyeMessage toSend = GotyeMessage.createTextMessage(sender, recver, text);

            // putExtre(toSend);
            int code = GotyeAPI.getInstance().sendMessage(toSend);
            LogHelper.debug(ChatHelper.class, "sendMessage for text, code["+code+"]");
        }
    }

    /**
     * 处理自定义消息内容
     */
   public static long getOrderIdFromUserExtraMessage(GotyeMessage message) {
       if(message != null) {
           String extra = new String(message.getUserData());
           JSONObject json = JsonUtils.formatJson(extra);
           if(json != null) {
               String diyType = json.getString("type");
               if ("confirm".equals(diyType)) {

                   Long orderId = json.getLong("order_id");
                   if (orderId != null && orderId > 0) {
                       return orderId;
                   }
               }
           }
       }

       return 0;
   }
}
