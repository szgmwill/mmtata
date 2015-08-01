package com.tata.imta.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.tata.imta.R;
import com.tata.imta.app.BaseActivity;
import com.tata.imta.bean.UserFind;
import com.tata.imta.helper.MyViewHelper;
import com.tata.imta.helper.UserHelper;
import com.tata.imta.helper.img.LoadUserImg;
import com.tata.imta.util.DateUtils;
import com.tata.imta.util.ShowUtils;
import com.tata.imta.util.TimeUtil;

import java.util.List;

/**
 * Created by Will Zhang on 2015/6/2.
 */
public class FindAdapter extends MyAdapter {

    private List<UserFind> userList;

    private BaseActivity context;

    private LayoutInflater inflater;

    /*
     * 图片加载器
     */
    private LoadUserImg imgLoader;

    public FindAdapter (BaseActivity context, List<UserFind> userList) {
        this.context = context;
        this.userList = userList;

        //init inflater
        inflater = LayoutInflater.from(context);

        imgLoader = new LoadUserImg();
    }

    @Override
    public int getCount() {
        if(userList != null) {
            return userList.size();
        }

        return 0;
    }

    @Override
    public UserFind getItem(int position) {
        if(userList != null) {
            return userList.get(position);
        }

        return null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        //再得到当前索引下的聊天明细
        final UserFind userinfo = userList.get(position);

        final String nick = userinfo.getNick();

        FindHolder holder = null;

        if(convertView == null) {
            //先得到一条聊天记录的布局信息
            convertView = inflater.inflate(R.layout.ta_item_find, null, false);

            holder = new FindHolder();

            //组建视图
            ImageView head = (ImageView) convertView.findViewById(R.id.ta_item_iv_avatar);//头像
            holder.iv_head = head;

            // 昵称
            holder.tv_nick = (TextView) convertView.findViewById(R.id.ta_item_find_tv_name);
            holder.tv_sign = (TextView) convertView.findViewById(R.id.ta_item_find_tv_sign);

            //性别年龄
            holder.rl_sex_age = (RelativeLayout) convertView.findViewById(R.id.ta_item_find_rl_sexage);

            //聊天资费
            holder.rl_unit_price = (RelativeLayout) convertView.findViewById(R.id.ta_item_find_rl_unitprice);

            // 时间
            holder.tv_time = (TextView) convertView.findViewById(R.id.ta_item_find_tv_time);

            //缓存视图
            convertView.setTag(holder);
        } else {
            holder = (FindHolder) convertView.getTag();
        }

        //用当前用户信息给视图填充内容

        imgLoader.showImgView(holder.iv_head, userinfo.getHead());

        holder.tv_nick.setText(ShowUtils.showTextLimit(nick, 10));

        //展示年龄性别
        MyViewHelper.showSexAgeView(holder.rl_sex_age, userinfo.getSex(), DateUtils.getAgeFromDate(userinfo.getBirth()));
        //个性签名
        holder.tv_sign.setText(ShowUtils.showTextLimit(userinfo.getSign(), 10));

        //展示聊天资费
        MyViewHelper.showUnitPrice(holder.rl_unit_price, userinfo.getFee(), userinfo.getShowUnit());

        //展示最近一次登录时间
        holder.tv_time.setText(TimeUtil.dateToMessageTime(userinfo.getLastLoginTime().getTime()));

        //给每一条记录添加点击事件
        RelativeLayout re_parent = (RelativeLayout) convertView.findViewById(R.id.ta_item_find_re_parent);
        re_parent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //进入个人主页
                UserHelper.goUserDetail(context, userinfo.getUserId());
            }
        });

        //长按的情况
//        re_parent.setOnLongClickListener(new View.OnLongClickListener() {
//            @Override
//            public boolean onLongClick(View v) {
//                showAlertDialog(context, "你的选择", "未完成");
//                return false;
//            }
//        });


        return convertView;
    }

    /**
     * 重置展示列表
     */
    public void setUserList(List<UserFind> list) {
        if(list != null) {
            userList = list;
            //列表更新了,通知当前活动页进行UI刷新
            notifyDataSetChanged();
        }

    }

    /**
     * 聊天视图
     */
    private static class FindHolder {

        /**
         * 用户头像
         */
        ImageView iv_head;

        /**
         * 用户名称
         */
        TextView tv_nick;

        /**
         * 性别年龄
         */
        RelativeLayout rl_sex_age;

        /**
         * 用户签名
         */
        TextView tv_sign;
        /**
         * 最近登录时间
         */
        TextView tv_time;

        /**
         * 聊天资费
         */
        RelativeLayout rl_unit_price;
    }
}
