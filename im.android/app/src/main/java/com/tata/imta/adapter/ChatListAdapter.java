package com.tata.imta.adapter;

import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.gotye.api.GotyeAPI;
import com.gotye.api.GotyeChatTarget;
import com.gotye.api.GotyeUser;
import com.tata.imta.R;
import com.tata.imta.app.BaseActivity;
import com.tata.imta.bean.ChatListItem;
import com.tata.imta.fragment.FragmentChat;
import com.tata.imta.helper.LogHelper;
import com.tata.imta.helper.MyViewHelper;
import com.tata.imta.helper.UserHelper;
import com.tata.imta.helper.img.LoadUserImg;
import com.tata.imta.util.ShowUtils;
import com.tata.imta.util.ToastUtils;

import java.util.List;

/**
 * Created by Will Zhang on 2015/5/3.
 * 聊天记录填充
 * 我的对话列表
 */
public class ChatListAdapter extends MyAdapter {

    private List<ChatListItem> chatList;
    //当前所在的面板
    private FragmentChat fragmentChatPage;
    //当前所在的活动页
    private BaseActivity context;

    private LayoutInflater inflater;

    /**
     * 图片缓存器
     */
    private LoadUserImg loadImage;

    public ChatListAdapter(FragmentChat fragmentChatPage, List<ChatListItem> list) {
        this.fragmentChatPage = fragmentChatPage;
        this.context = (BaseActivity) fragmentChatPage.getActivity();

        this.chatList = list;

        //init inflater
        inflater = LayoutInflater.from(context);

        //init img cache loader
        loadImage = new LoadUserImg();
    }

    @Override
    public int getCount() {

        if(chatList != null) {
//            LogHelper.debug(this, "ChatAdapter-->getCount()======="+chatList.size());
            return chatList.size();
        }

        return 0;
    }

    @Override
    public ChatListItem getItem(int position) {
        if(chatList != null) {
            return chatList.get(position);
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
        final ChatListItem chatItem = chatList.get(position);

        //当前选中的用户名

        final long userId = chatItem.getUserId();
        final String userIdStr = String.valueOf(userId);

        //每一条展示记录的承载
        //保存视图信息
        ChatHolder holder = null;

        if(convertView == null) {
            //先得到一条聊天记录的布局信息
            convertView = inflater.inflate(R.layout.ta_item_chat, null, false);

            holder = new ChatHolder();

            //最后填充布局页面各组件内容
            ImageView head = (ImageView) convertView.findViewById(R.id.ta_item_iv_avatar);//头像
            holder.iv_head = head;

            // 昵称
            holder.tv_nick = (TextView) convertView.findViewById(R.id.ta_item_chat_tv_name);
            // 未读消息
            holder.tv_unread = (TextView) convertView.findViewById(R.id.ta_item_chat_tv_unread);
            // 最近一条消息
            holder.tv_content = (TextView) convertView.findViewById(R.id.ta_item_chat_tv_content);
            // 时间
            holder.tv_time = (TextView) convertView.findViewById(R.id.ta_item_chat_tv_time);

            //缓存视图
            convertView.setTag(holder);
        } else {
            holder = (ChatHolder) convertView.getTag();
        }

        //处理头像展示
        final String headUrl = chatItem.getHeadUrl();
        if(headUrl == null) {
            //头像如果为空,展示系统管理员头像
            holder.iv_head.setImageResource(R.drawable.ta_sys_avatar);
        } else {
            loadImage.showImgView(holder.iv_head, headUrl);
        }

        //给视图填充值
        holder.tv_nick.setText(ShowUtils.showTextLimit(chatItem.getNick(), 10));
        //setText的时候,要注意数字和字符串的区别,设置内容只能用字符串
        //如果未读消息数为0,则不展示
        if(chatItem.getUnreadNum() == 0) {
            holder.tv_unread.setVisibility(View.GONE);
        } else {
            holder.tv_unread.setText(String.valueOf(chatItem.getUnreadNum()));//未读消息条数
            holder.tv_unread.setVisibility(View.VISIBLE);
        }

        holder.tv_time.setText(chatItem.getLastMsgTime());
        //聊天内容,要处理富文本
        String chatContent = chatItem.getContent();
        MyViewHelper.processTextMsg(context, holder.tv_content, chatContent);
//        holder.tv_content.setText(ShowUtils.showTextLimit(chatContent, 25));

        //给每一条记录添加点击事件
        RelativeLayout re_parent = (RelativeLayout) convertView.findViewById(R.id.ta_item_chat_re_parent);

        //点击
        re_parent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(userIdStr.equals(GotyeAPI.getInstance().getLoginUser().getName())) {
                    ToastUtils.showShortToast(context, "不能和自己聊天!");
                } else {
                    if (FragmentChat.fixName.equals(chatItem.getNick())) {
                        //点开通知页
                        ToastUtils.showShortToast(context, "now you click NotifyListPage");
//                    Intent i = new Intent(getActivity(), NotifyListPage.class);
//                    startActivity(i);
                    } else {//进入聊天页面
                        //将该点开的会话所在目录用户未读消息记为全部已读
                        GotyeChatTarget gotyeTarget = new GotyeUser(userIdStr);
                        GotyeAPI.getInstance().markMessagesAsRead(gotyeTarget, true);

                        UserHelper.goChat(context, userId);
                    }
                }

            }
        });

        //长按的情况
        re_parent.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {

                (context).showConfirmDialog("删除该用户的所有会话", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //先干掉当前的会话,然后调用亲加删除会话,最后在删除会话成功回调后更新UI
                        chatList.remove(chatItem);
                        //删除该会话:删除会话的同时是否删除这个聊天对象的所有本地记录，取决于第二个参数alsoRemoveMessages
                        GotyeAPI.getInstance().deleteSession(new GotyeUser(userIdStr), true);
                        //后面在回调处理更新UI
                        fragmentChatPage.refreshUI();
                    }
                });
                return false;
            }
        });
        return convertView;
    }

    /**
     * 重置展示列表
     */
    public void setChatList(List<ChatListItem> list) {
        if(list != null) {
            LogHelper.debug(this, "setChatList size:"+list.size());
            chatList = list;
            //列表更新了,通知当前活动页进行UI刷新
            notifyDataSetChanged();
        }
    }

    /**
     * 聊天视图
     */
    private static class ChatHolder {

        /**
         * 消息对象昵称
         */
        TextView tv_nick;
        /**
         * 消息未读数
         */
        TextView tv_unread;
        /**
         * 最后一条消息的内容
         */
        TextView tv_content;
        /**
         * 最后一条消息的时间
         */
        TextView tv_time;
        /**
         * 用户头像
         */
        ImageView iv_head;
    }
}
