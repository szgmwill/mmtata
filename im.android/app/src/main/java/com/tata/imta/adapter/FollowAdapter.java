package com.tata.imta.adapter;

import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.gotye.api.GotyeAPI;
import com.gotye.api.GotyeUser;
import com.tata.imta.R;
import com.tata.imta.app.BaseActivity;
import com.tata.imta.bean.User;
import com.tata.imta.bean.UserExtend;
import com.tata.imta.fragment.FragmentFollow;
import com.tata.imta.helper.MyViewHelper;
import com.tata.imta.helper.UserHelper;
import com.tata.imta.helper.img.LoadUserImg;
import com.tata.imta.util.DateUtils;
import com.tata.imta.util.ShowUtils;

import java.util.List;

/**
 * Created by Will Zhang on 2015/6/21.
 * 我的关注列表
 * 即我的好友列表
 */
public class FollowAdapter extends MyAdapter {

    private FragmentFollow fragment;

    private LayoutInflater inflater;

    private BaseActivity context;

    /*
     * 图片加载器
     */
    private LoadUserImg imgLoader;

    private List<User> followList;

    public FollowAdapter (FragmentFollow fragment, List<User> followList) {
        this.fragment = fragment;
        this.followList = followList;

        //找出当前模块所在上下文
        context = (BaseActivity) fragment.getActivity();

        inflater = LayoutInflater.from(context);

        imgLoader = new LoadUserImg();
    }

    @Override
    public int getCount() {
        if(followList != null) {
           return followList.size();
        }
        return 0;
    }

    @Override
    public Object getItem(int position) {
        if(followList != null) {
            followList.get(position);
        }
        return null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        FollowHolder holder = null;

        final User user = followList.get(position);

        if(convertView == null) {
            convertView = inflater.inflate(R.layout.ta_item_follow, null, false);

            holder = new FollowHolder();

            //初始化视图
            holder.iv_head = (ImageView) convertView.findViewById(R.id.ta_item_iv_avatar);
            holder.tv_nick = (TextView) convertView.findViewById(R.id.ta_item_follow_tv_name);
            holder.tv_sign = (TextView) convertView.findViewById(R.id.ta_item_follow_tv_sign);

            //性别年龄
            holder.rl_sex_age = (RelativeLayout) convertView.findViewById(R.id.ta_item_sex_age_parent);

            //聊天资费
            holder.rl_unit_price = (RelativeLayout) convertView.findViewById(R.id.ta_item_unit_price_parent);


            //缓存视图
            convertView.setTag(holder);
        } else {
            holder = (FollowHolder) convertView.getTag();
        }

        //用当前用户信息给视图填充内容
        imgLoader.showImgView(holder.iv_head, user.getHead());

        holder.tv_nick.setText(ShowUtils.showTextLimit(user.getNick(), 10));

        //展示年龄性别
        MyViewHelper.showSexAgeView(holder.rl_sex_age, user.getSex(), DateUtils.getAgeFromDate(user.getBirth()));

        holder.tv_sign.setText(ShowUtils.showTextLimit(user.getSign(), 10));

        UserExtend extend = user.getExtend();
        if(extend != null) {
            //展示聊天资费
            MyViewHelper.showUnitPrice(holder.rl_unit_price, extend.getFee(), extend.getFee_unit(), extend.getFee_duration());
        }

        //给每一条记录添加点击事件
        RelativeLayout re_parent = (RelativeLayout) convertView.findViewById(R.id.ta_item_follow_re_parent);
        re_parent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //进入个人主页
                UserHelper.goUserDetail(context, user.getUserId());
            }
        });

        //长按取消关注
        re_parent.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {

                context.showConfirmDialog("取消关注", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        //一般删除关注后,首先将本地显示列表移除该元素,同时调用亲加服务取消关注用户
                        //最后在亲加回调删除结果时更新UI
                        followList.remove(user);

                        ///< 对应回调GotyeDelegate onRemoveFriend，同时会更新本地好友列表,回调时顺道更新当前UI
                        GotyeAPI.getInstance().reqRemoveFriend(new GotyeUser(String.valueOf(user.getUserId())));
                    }
                });
                return false;
            }
        });

        return convertView;
    }

    /**
     * 刷新列表
     */
    public void resetList(List<User> followList) {
        if(followList != null && followList.size() > 0) {
            this.followList = followList;

            notifyDataSetChanged();
        }
    }

    /**
     * 聊天视图
     */
    private static class FollowHolder {

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
         * 聊天资费
         */
        RelativeLayout rl_unit_price;

    }
}
