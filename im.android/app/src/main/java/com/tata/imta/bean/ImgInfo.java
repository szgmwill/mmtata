package com.tata.imta.bean;

import java.io.Serializable;

/**
 * Created by Will Zhang on 2015/5/24.
 * 用户相册信息
 */
public class ImgInfo implements Serializable {
    private long userId;

//    private int index;

    private String url;

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }



    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    /**
     * 生成用户的头像图片存储文件名:固定名称
     * user_id - index - time
     */
    public String getUserImgFileName(int index) {

        StringBuffer sb = new StringBuffer();
        sb.append(String.valueOf(userId)).append("-");
        sb.append(String.valueOf(index));
        sb.append("-").append(String.valueOf(System.currentTimeMillis()));
        sb.append(".png");

        return sb.toString();
    }
}
