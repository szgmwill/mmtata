package com.tata.imta.bean;

/**
 * Created by Will Zhang on 2015/5/3.
 *
 * 聊天对象信息
 */
public class SingleChatItem {

    /**
     * 自己平台的用户id
     */
    private String userId;

    /** 用户尼称 */
    private String nick;
    /** 消息未读数 */
    private int unreadNum;
    /** 最后一条消息的内容 */
    private String content;
    /** 最后一条消息的时间 */
    private String lastMsgTime;
    /** 用户头像 */
    private String headUrl;

    public SingleChatItem(String userId, String nick) {
        this.userId = userId;
        this.nick = nick;
    }

    public String getNick() {
        return nick;
    }

    public void setNick(String nick) {
        this.nick = nick;
    }

    public int getUnreadNum() {
        return unreadNum;
    }

    public void setUnreadNum(int unreadNum) {
        this.unreadNum = unreadNum;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getLastMsgTime() {
        return lastMsgTime;
    }

    public void setLastMsgTime(String lastMsgTime) {
        this.lastMsgTime = lastMsgTime;
    }

    public String getHeadUrl() {
        return headUrl;
    }

    public void setHeadUrl(String headUrl) {
        this.headUrl = headUrl;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}
