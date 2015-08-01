package com.tata.imta.bean;

/**
 * Created by Will Zhang on 2015/6/15.
 * 用户与用户之间的各种关联关系
 */
public class UserRelation {

    /**
     * 当前用户
     */
    private long userId;

    /**
     * 目标用户
     */
    private long targetUserId;

    /**
     * 是否已关注(即好友)
     */
    private boolean isFriend = false;

    /**
     * 是否已拉黑
     */
    private boolean isBlacklist = false;

    /**
     * 是否已举报
     */
    private boolean isReported = false;

    /**
     * 是否已评价
     */
    private boolean isFeedback = false;

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public long getTargetUserId() {
        return targetUserId;
    }

    public void setTargetUserId(long targetUserId) {
        this.targetUserId = targetUserId;
    }

    public boolean isFriend() {
        return isFriend;
    }

    public void setIsFriend(boolean isFriend) {
        this.isFriend = isFriend;
    }

    public boolean isBlacklist() {
        return isBlacklist;
    }

    public void setIsBlacklist(boolean isBlacklist) {
        this.isBlacklist = isBlacklist;
    }

    public boolean isReported() {
        return isReported;
    }

    public void setIsReported(boolean isReported) {
        this.isReported = isReported;
    }

    public boolean isFeedback() {
        return isFeedback;
    }

    public void setIsFeedback(boolean isFeedback) {
        this.isFeedback = isFeedback;
    }
}
