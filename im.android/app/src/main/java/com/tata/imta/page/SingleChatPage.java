//package com.tata.imta.page;
//
//import android.annotation.SuppressLint;
//import android.content.Context;
//import android.content.Intent;
//import android.content.SharedPreferences;
//import android.graphics.BitmapFactory;
//import android.net.Uri;
//import android.os.Bundle;
//import android.os.Handler;
//import android.provider.MediaStore;
//import android.support.v4.view.ViewPager;
//import android.text.Editable;
//import android.text.Selection;
//import android.text.Spannable;
//import android.text.SpannableStringBuilder;
//import android.text.TextUtils;
//import android.text.style.ImageSpan;
//import android.view.ContextMenu;
//import android.view.ContextMenu.ContextMenuInfo;
//import android.view.KeyEvent;
//import android.view.MenuItem;
//import android.view.MenuItem.OnMenuItemClickListener;
//import android.view.MotionEvent;
//import android.view.View;
//import android.view.View.OnCreateContextMenuListener;
//import android.view.ViewGroup.LayoutParams;
//import android.view.Window;
//import android.view.inputmethod.InputMethodManager;
//import android.widget.AdapterView;
//import android.widget.Button;
//import android.widget.GridView;
//import android.widget.ImageView;
//import android.widget.LinearLayout;
//import android.widget.TextView;
//import android.widget.Toast;
//
//import com.gotye.api.GotyeChatTarget;
//import com.gotye.api.GotyeChatTargetType;
//import com.gotye.api.GotyeMedia;
//import com.gotye.api.GotyeMessage;
//import com.gotye.api.GotyeMessageType;
//import com.gotye.api.GotyeStatusCode;
//import com.gotye.api.GotyeUser;
//import com.gotye.api.WhineMode;
//import com.tata.imta.R;
//import com.tata.imta.adapter.FaceGridViewAdapter;
//import com.tata.imta.adapter.FaceViewPagerAdapter;
//import com.tata.imta.adapter.SingleChatAdapter;
//import com.tata.imta.app.BaseActivity;
//import com.tata.imta.app.BizInfoHolder;
//import com.tata.imta.bean.User;
//import com.tata.imta.helper.FragmentHelper;
//import com.tata.imta.helper.LogHelper;
//import com.tata.imta.listener.GotyeVoicePlayClickPlayListener;
//import com.tata.imta.task.SendImageMessageTask;
//import com.tata.imta.util.CommonUtils;
//import com.tata.imta.util.ToastUtils;
//import com.tata.imta.util.URIUtil;
//import com.tata.imta.view.MyEditText;
//import com.tata.imta.view.RTPullListView;
//import com.tata.imta.view.RTPullListView.OnRefreshListener;
//
//import java.io.File;
//import java.util.ArrayList;
//import java.util.List;
//import java.util.regex.Matcher;
//import java.util.regex.Pattern;
//
///**
// * Created by Will Zhang on 2015/5/6.
// *
// * 用户与用户之间的聊天页面
// * 单聊页面
// */
//public class SingleChatPage extends BaseActivity implements View.OnClickListener {
//
//    private static final int REQUEST_PIC = 1;
//    private static final int REQUEST_CAMERA = 2;
//
//    public static final int VOICE_MAX_TIME = 60 * 1000;//最长录音不超一分钟
//
//    //聊天消息填充器
//    public SingleChatAdapter adapter;
//
//    //聊天对象
//    private User targetUser;
//    //聊天对象的(亲加对象)
//    private GotyeUser gotyeUser;
//
//    //聊天消息列表
//    private List<GotyeMessage> messageList;
//
//
//    /**=================页面视图控件===========**/
//    //下拉刷新
//    private RTPullListView pullListView;
//
//    private ImageView changeMode;
//    private Button pressToSendVoice;
//    private Button pressToSendText;
//    private MyEditText textEditor;
//    private ImageView showMoreType;
//    private LinearLayout moreTypeLayout;
//
//    /**
//     * 按住语音显示录音效果视图
//     */
//    private LinearLayout voicePopUpLayout;
//
//    private LinearLayout voice_rcd_hint_rcding,del_re,voice_rcd_hint_loading,voice_rcd_hint_tooshort;
//    private ImageView volume;
//
//    private boolean moreTypeForSend = true;
//    public int onRealTimeTalkFrom = -1; // -1默认状态 ,0表示我在说话,1表示别人在实时语音
//
//    private File cameraFile;
//    public static final int IMAGE_MAX_SIZE_LIMIT = 1024 * 1024;
//    public static final int Voice_MAX_TIME_LIMIT = 60 * 1000;
//    private long playingId;
//    private TextView title;
//
//    public boolean makingVoiceMessage = false;
//
//    //emoji表情视图  start==================
//    private ViewPager mEmojiViewPager;
//    //表情页里底部的小圆点
//    private LinearLayout ta_ll_dots_container;
//    private LinearLayout ta_ll_emoji_container;
//    private ImageView ta_iv_emoticons_normal;//表情图标开关
//    //表情展示的表格大小
//    private int columns = 6;
//    private int rows = 4;
//    //表情视图列表页
//    private List<View> emojiViews = new ArrayList<View>();
//
//    //emoji表情视图  end==================
//
//    @Override
//    public void onCreate(Bundle savedInstanceState) {
//
//        super.onCreate(savedInstanceState);
//
//        //没有标题页面
//        requestWindowFeature(Window.FEATURE_NO_TITLE);
//
//        //聊天主面板
//        setContentView(R.layout.ta_activity_single_chat);
//
//        LogHelper.debug(this, "current ta_activity_mobile_login user:" + loginUser.getUserId());
//
//        //注册监听各种事件
//        gotyeAPI.addListener(this);
//
//        //当前的聊天对象是谁,从前一个activity带过来的参数
//        targetUser = (User) getIntent().getSerializableExtra("user");
//        if(targetUser != null && targetUser.getUserId() == 0) {
//            gotyeUser = new GotyeUser(targetUser.getNick());
//        } else {
//            gotyeUser = new GotyeUser(String.valueOf(targetUser.getUserId()));
//        }
//
//
//        //初始化各种视图
//        initViews();
//        //绑定事件
//        initEvents();
//
//        //一旦进入该聊天页面后,把收到的消息都标记为已读
//        gotyeAPI.activeSession(gotyeUser);
//
//        //从亲加服务端读取聊天记录
//        loadData();
//
//        //敏感词过滤 设置
//        SharedPreferences spf = getSharedPreferences("fifter_cfg", Context.MODE_PRIVATE);
//        boolean fifter = spf.getBoolean("fifter", false);
//        gotyeAPI.enableTextFilter(GotyeChatTargetType.GotyeChatTargetTypeUser, fifter);
//
//        FragmentHelper.refreshTopbarErrTips(this, null);
//    }
//
//    @Override
//    protected void initViews() {
//
//        //下拉刷新处理
//        pullListView = (RTPullListView) findViewById(R.id.ta_chat_msg_refresh_view);
//
//        //回退键
//        findViewById(R.id.ta_rl_topbar_back).setOnClickListener(this);
//
//        //标题
//        title = ((TextView) findViewById(R.id.ta_tv_topbar_title));
//        title.setText(targetUser.getNick());
//
//        //更多按钮
//        findViewById(R.id.ta_rl_topbar_more).setOnClickListener(this);
//
//        //处理语音视图
////        realTalkView = findViewById(R.id.real_time_talk_layout);
////        realTalkName = (TextView) realTalkView
////                .findViewById(R.id.real_talk_name);
////        Drawable[] anim = realTalkName.getCompoundDrawables();
////        realTimeAnim = (AnimationDrawable) anim[2];
////        stopRealTalk = (TextView) realTalkView
////                .findViewById(R.id.stop_real_talk);
////        stopRealTalk.setOnClickListener(this);
//
//        changeMode = (ImageView) findViewById(R.id.ta_iv_setting_mode);
//        //发送按钮
//        pressToSendVoice = (Button) findViewById(R.id.ta_btn_send_voice);
//        pressToSendText = (Button) findViewById(R.id.ta_btn_send_text);
//        textEditor = (MyEditText) findViewById(R.id.ta_text_msg_input);
//        showMoreType = (ImageView) findViewById(R.id.ta_iv_more_type);
//
//        //额外功能
//        moreTypeLayout = (LinearLayout) findViewById(R.id.ta_more_type_layout);
//
//        //录音层
//        voicePopUpLayout = (LinearLayout) findViewById(R.id.ta_rcChat_popup);
//        voice_rcd_hint_rcding = (LinearLayout) voicePopUpLayout.findViewById(R.id.voice_rcd_hint_rcding);
//        del_re = (LinearLayout) voicePopUpLayout.findViewById(R.id.del_re);
//        voice_rcd_hint_loading = (LinearLayout) voicePopUpLayout.findViewById(R.id.voice_rcd_hint_loading);
//        voice_rcd_hint_tooshort = (LinearLayout) voicePopUpLayout.findViewById(R.id.voice_rcd_hint_tooshort);
//        volume = (ImageView) voicePopUpLayout.findViewById(R.id.volume);
//
//        moreTypeLayout.findViewById(R.id.to_gallery).setOnClickListener(this);
//        moreTypeLayout.findViewById(R.id.to_camera).setOnClickListener(this);
//
//        changeMode.setOnClickListener(this);
//        showMoreType.setOnClickListener(this);
//
//        //emoji表情视图
//        mEmojiViewPager = (ViewPager) findViewById(R.id.ta_vp_emoji);
//        ta_ll_dots_container = (LinearLayout) findViewById(R.id.ta_ll_dots_container);
//        ta_ll_emoji_container = (LinearLayout) findViewById(R.id.ta_ll_emoji_container);
//        ta_iv_emoticons_normal = (ImageView) findViewById(R.id.ta_iv_emoticons_normal);
//
//        initEmojiPager();
//
//        //填充聊天记录
//        adapter = new SingleChatAdapter(this, new ArrayList<GotyeMessage>(), targetUser.getHead());
//        pullListView.setAdapter(adapter);
//        pullListView.setSelection(adapter.getCount());
//
//        setListViewInfo();
//    }
//
//    private void setListViewInfo() {
//        // 下拉刷新监听器
//        pullListView.setonRefreshListener(new OnRefreshListener() {
//
//            /**
//             * 刷新列表
//             */
//            @Override
//            public void onRefresh() {
//                messageList = gotyeAPI.getMessageList(gotyeUser, true);
//
//                if (messageList != null) {
//                    adapter.refreshData(messageList);
//                } else {
//                    ToastUtils.showShortToast(SingleChatPage.this, "没有更多历史消息");
//                }
//                adapter.notifyDataSetChanged();
//                pullListView.onRefreshComplete();
//            }
//        });
//        pullListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
//
//            @Override
//            public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
//                                           int position, long arg3) {
//                final GotyeMessage message = adapter.getItem(position - 1);
//                pullListView.setTag(message);
//                pullListView.showContextMenu();
//                return true;
//            }
//        });
//        pullListView.setOnCreateContextMenuListener(new OnCreateContextMenuListener() {
//
//            @Override
//            public void onCreateContextMenu(ContextMenu conMenu,
//                                            View arg1, ContextMenuInfo arg2) {
//                final GotyeMessage message = (GotyeMessage) pullListView
//                        .getTag();
//                if (message == null) {
//                    return;
//                }
//                MenuItem m = null;
////                        if (chatType == 1
////                                && !message.getSender().getName()
////                                .equals(currentLoginUser.getName())) {
////                            m = conMenu.add(0, 0, 0, "举报");
////                        }
//                // if(message.getType()==GotyeMessageType.GotyeMessageTypeAudio){
//                // m= conMenu.add(0, 1, 0, "转为文字(仅限普通话)");
//                // }
//                if (m == null) {
//                    return;
//                }
//                m.setOnMenuItemClickListener(new OnMenuItemClickListener() {
//
//                    @Override
//                    public boolean onMenuItemClick(MenuItem item) {
//                        switch (item.getItemId()) {
//                            case 0:
//                                gotyeAPI.report(0, "举报的说明", message);
//                                break;
//
//                            case 1:
//                                // api.decodeMessage(message);
//                                break;
//                        }
//                        return true;
//                    }
//                });
//            }
//        });
//
//        //定位到最后一条消息去
//        refreshToTail();
//
//    }
//
//    @Override
//    protected void initEvents() {
//
//        ta_iv_emoticons_normal.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                //展开收回表情页
//                if(ta_ll_emoji_container.getVisibility() == View.VISIBLE) {
//                    ta_ll_emoji_container.setVisibility(View.GONE);
//                } else {
//                    hideKeyboard();
//                    ta_ll_emoji_container.setVisibility(View.VISIBLE);
//                }
//            }
//        });
//
//        mEmojiViewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
//            @Override
//            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
//
//            }
//
//            //处理圆点
//            @Override
//            public void onPageSelected(int position) {
//                for (int i = 0; i < ta_ll_dots_container.getChildCount(); i++) {
//                    ta_ll_dots_container.getChildAt(i).setSelected(false);
//                }
//                ta_ll_dots_container.getChildAt(position).setSelected(true);
//            }
//
//            @Override
//            public void onPageScrollStateChanged(int state) {
//
//            }
//        });
//
//        textEditor.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                //如果点击文本框的话
//                //隐藏表情
//                if(ta_ll_emoji_container.getVisibility() == View.VISIBLE) {
//                    ta_ll_emoji_container.setVisibility(View.GONE);
//                }
//            }
//        });
//
//        //发送文本消息监听
//        textEditor.setOnEditorActionListener(new TextView.OnEditorActionListener() {
//            @Override
//            public boolean onEditorAction(TextView arg0, int arg1, KeyEvent arg2) {
//                String text = arg0.getText().toString();
//                // GotyeMessage message =new GotyeMessage();
//                // GotyeChatManager.getInstance().sendMessage(message);
//                sendTextMessage(text);
//                textEditor.setText("");
//                return true;
//            }
//        });
//
//        pressToSendText.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                //发送消息
//                hideKeyboard();
//                String str = textEditor.getText().toString();
//                sendTextMessage(str);
//                textEditor.setText("");
//            }
//        });
//
//        //发送语音消息监听
//        pressToSendVoice.setOnTouchListener(new View.OnTouchListener() {
//
//            /**
//             * 监测语音操作
//             * @param v
//             * @param event
//             * @return
//             */
//            @SuppressLint("ClickableViewAccessibility")
//            @Override
//            public boolean onTouch(View v, MotionEvent event) {
//                switch (event.getAction()) {
//                    case MotionEvent.ACTION_DOWN://按下动作
//
//                        if (onRealTimeTalkFrom == 0) {
//                            ToastUtils.showShortToast(SingleChatPage.this, "正在实时通话中...");
//                            return false;
//                        }
//
//                        if (GotyeVoicePlayClickPlayListener.isPlaying) {
//                            GotyeVoicePlayClickPlayListener.currentPlayListener
//                                    .stopPlayVoice(true);
//                        }
//                        //开始录音
//                        int code = gotyeAPI.startTalk(gotyeUser, WhineMode.DEFAULT, VOICE_MAX_TIME);
//
//                        pressToSendVoice.setText("松开 发送");
//                        break;
//                    case MotionEvent.ACTION_UP://松开动作
//
//                        if (onRealTimeTalkFrom == 0) {
//                            return false;
//                        }
//                        // if (onRealTimeTalkFrom > 0) {
//                        // return false;
//                        // }
//                        gotyeAPI.stopTalk();
//                        pressToSendVoice.setText("按住 说话");
//                        // mPopupWindow.dismiss();------------------------------------------
//                        break;
//                    case MotionEvent.ACTION_CANCEL://取消动作
//                        if (onRealTimeTalkFrom == 0) {
//                            return false;
//                        }
//                        // if (onRealTimeTalkFrom > 0) {
//                        // return false;
//                        // }
//                        gotyeAPI.stopTalk();
//                        pressToSendVoice.setText("按住 说话");
//                        // mPopupWindow.dismiss();---------------------------------------------
//                        break;
//                    default:
//                        break;
//                }
//                return false;
//            }
//        });
//    }
//
//    /**
//     * 初始化表情视图
//     */
//    private void initEmojiPager() {
//
//        /**
//         * 根据表情数量以及GridView设置的行数和列数计算Pager数量
//         */
//        int count = BizInfoHolder.getInstance().getStaticEmojiList().size();
//        int pagerCount = count % (columns * rows - 1) == 0 ? count / (columns * rows - 1)
//                : count / (columns * rows - 1) + 1;
//
//
//        // 获取页数
//        for (int i = 0; i < pagerCount; i++) {
//            emojiViews.add(getEmojiPageItem(i));
//            LayoutParams params = new LayoutParams(16, 16);
//            ta_ll_dots_container.addView(dotsItem(i), params);
//        }
//        FaceViewPagerAdapter mVpAdapter = new FaceViewPagerAdapter(emojiViews);
//
//        mEmojiViewPager.setAdapter(mVpAdapter);
//        ta_ll_dots_container.getChildAt(0).setSelected(true);
//    }
//
//    private View getEmojiPageItem(int position) {
//        View layout = getLayoutInflater().inflate(R.layout.emoji_gridview, null);//表情布局
//        GridView gridview = (GridView) layout.findViewById(R.id.chart_face_gv);
//
//        List<String> staticFacesList = BizInfoHolder.getInstance().getStaticEmojiList();
//
//        /**
//         * 注：因为每一页末尾都有一个删除图标，所以每一页的实际表情columns *　rows　－　1; 空出最后一个位置给删除图标
//         * */
//        List<String> subList = new ArrayList<String>();
//        subList.addAll(staticFacesList
//                .subList(position * (columns * rows - 1),
//                        (columns * rows - 1) * (position + 1) > staticFacesList
//                                .size() ? staticFacesList.size() : (columns
//                                * rows - 1)
//                                * (position + 1)));
//        /**
//         * 末尾添加删除图标
//         * */
//        subList.add("emotion_del_normal.png");
//        FaceGridViewAdapter mGvAdapter = new FaceGridViewAdapter(subList, this);
//        gridview.setAdapter(mGvAdapter);
//        gridview.setNumColumns(columns);
//        // 单击表情执行的操作
//        gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                try {
//                    String png = ((TextView) ((LinearLayout) view).getChildAt(1)).getText().toString();
//                    if (!png.contains("emotion_del_normal")) {// 如果不是删除图标
//                        insert(getFace(png));
//                    } else {
//                        delete();
//                    }
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//            }
//        });
//
//        return gridview;
//    }
//
//    private SpannableStringBuilder getFace(String png) {
//        SpannableStringBuilder sb = new SpannableStringBuilder();
//        try {
//            /**
//             * 经过测试，虽然这里tempText被替换为png显示，但是但我单击发送按钮时，获取到輸入框的内容是tempText的值而不是png
//             * 所以这里对这个tempText值做特殊处理
//             * 格式：#[face/png/f_static_000.png]#，以方便判斷當前圖片是哪一個
//             * */
//            String tempText = "#[" + png + "]#";
//            sb.append(tempText);
//            sb.setSpan(
//                    new ImageSpan(this, BitmapFactory
//                            .decodeStream(getAssets().open(png))), sb.length()
//                            - tempText.length(), sb.length(),
//                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
//
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//
//        return sb;
//    }
//
//    /**
//     * 生成表情页底点的小圆点视图
//     */
//    private ImageView dotsItem(int position) {
//        View layout = getLayoutInflater().inflate(R.layout.emoji_dot_image, null);
//        ImageView iv = (ImageView) layout.findViewById(R.id.face_dot);
//        iv.setId(position);
//        return iv;
//    }
//
//    /**
//     * 向输入框里添加表情
//     * */
//    private void insert(CharSequence text) {
//        int iCursorStart = Selection.getSelectionStart((textEditor.getText()));
//        int iCursorEnd = Selection.getSelectionEnd((textEditor.getText()));
//        if (iCursorStart != iCursorEnd) {
//            ((Editable) textEditor.getText()).replace(iCursorStart, iCursorEnd, "");
//        }
//        int iCursor = Selection.getSelectionEnd((textEditor.getText()));
//        ((Editable) textEditor.getText()).insert(iCursor, text);
//    }
//
//    /**
//     * 删除图标执行事件
//     * 注：如果删除的是表情，在删除时实际删除的是tempText即图片占位的字符串，所以必需一次性删除掉tempText，才能将图片删除
//     * */
//    private void delete() {
//        if (textEditor.getText().length() != 0) {
//            int iCursorEnd = Selection.getSelectionEnd(textEditor.getText());
//            int iCursorStart = Selection.getSelectionStart(textEditor.getText());
//            if (iCursorEnd > 0) {
//                if (iCursorEnd == iCursorStart) {
//                    if (isDeletePng(iCursorEnd)) {
//                        String st = "#[face/png/f_static_000.png]#";
//                        ((Editable) textEditor.getText()).delete(
//                                iCursorEnd - st.length(), iCursorEnd);
//                    } else {
//                        ((Editable) textEditor.getText()).delete(iCursorEnd - 1,
//                                iCursorEnd);
//                    }
//                } else {
//                    ((Editable) textEditor.getText()).delete(iCursorStart,
//                            iCursorEnd);
//                }
//            }
//        }
//    }
//
//    /**
//     * 判断即将删除的字符串是否是图片占位字符串tempText 如果是：则讲删除整个tempText
//     * **/
//    private boolean isDeletePng(int cursor) {
//        String st = "#[face/png/f_static_000.png]#";
//        String content = textEditor.getText().toString().substring(0, cursor);
//        if (content.length() >= st.length()) {
//            String checkStr = content.substring(content.length() - st.length(),
//                    content.length());
//            String regex = "(\\#\\[face/png/f_static_)\\d{3}(.png\\]\\#)";
//            Pattern p = Pattern.compile(regex);
//            Matcher m = p.matcher(checkStr);
//            return m.matches();
//        }
//        return false;
//    }
//
//
//    /**
//     * 发送文本消息
//     * @param text
//     */
//    private void sendTextMessage(String text) {
//        if (!TextUtils.isEmpty(text)) {
//            GotyeUser sender = new GotyeUser(String.valueOf(loginUser.getUserId()));
//            GotyeMessage toSend = GotyeMessage.createTextMessage(sender, gotyeUser, text);
//
//            String extraStr = null;
//            if (text.contains("#")) {
//                String[] temp = text.split("#");
//                if (temp.length > 1) {
//                    extraStr = temp[1];
//                }
//
//            }
//            if (extraStr != null) {
//                toSend.putExtraData(extraStr.getBytes());
//            }
//
//            // putExtre(toSend);
//            int code = gotyeAPI.sendMessage(toSend);
//            LogHelper.debug(this, String.valueOf(code));
//            adapter.addMsgToBottom(toSend);
//
//            refreshToTail();
//        }
//    }
//
//    /**
//     * 获取到异步图片发送后处理
//     * @param msg
//     */
//    public void callBackSendImageMessage(GotyeMessage msg) {
//        adapter.addMsgToBottom(msg);
//        refreshToTail();
//    }
//
//    /**
//     *
//     */
//    public void refreshToTail() {
//        if (adapter != null) {
//            if (pullListView.getLastVisiblePosition()
//                    - pullListView.getFirstVisiblePosition() <= pullListView
//                    .getCount())
//                pullListView.setStackFromBottom(false);
//            else
//                pullListView.setStackFromBottom(true);
//
//            handler.postDelayed(new Runnable() {
//                @Override
//                public void run() {
//
//                    pullListView.setSelection(pullListView.getAdapter()
//                            .getCount() - 1);
//
//                    // This seems to work
//                    handler.post(new Runnable() {
//                        @Override
//                        public void run() {
//                            pullListView.clearFocus();
//                            pullListView.setSelection(pullListView.getAdapter()
//                                    .getCount() - 1);
//                        }
//                    });
//                }
//            }, 300);
//            pullListView.setSelection(adapter.getCount()
//                    + pullListView.getHeaderViewsCount() - 1);
//        }
//    }
//
//    private Handler handler = new Handler();
//
//    public void setPlayingId(long playingId) {
//        this.playingId = playingId;
//        adapter.notifyDataSetChanged();
//    }
//
//    public long getPlayingId() {
//        return playingId;
//    }
//
//
//    private void loadData() {
//        if (gotyeUser != null) {
//            //从第三方服务器获取聊天记录
//            messageList = gotyeAPI.getMessageList(gotyeUser, true);
//        }
//        if (messageList == null) {
//            messageList = new ArrayList<GotyeMessage>();
//        }
//        //刷新UI
//        adapter.refreshData(messageList);
//    }
//
//    @Override
//    protected void onDestroy() {
//        //取消相关监听器
//        gotyeAPI.removeListener(this);
//        //取消激活会话
//        gotyeAPI.deactiveSession(gotyeUser);
//        super.onDestroy();
//    }
//
//    /**
//     *
//     * 当暂停的时候,不要播放语音
//     */
//    @Override
//    protected void onPause() {
//        if (GotyeVoicePlayClickPlayListener.isPlaying
//                && GotyeVoicePlayClickPlayListener.currentPlayListener != null) {
//            // 停止语音播放
//            GotyeVoicePlayClickPlayListener.currentPlayListener
//                    .stopPlayVoice(false);
//        }
//        super.onPause();
//    }
//
//    /**
//     * 回退键操作,取消相关语音
//     *
//     */
//    @Override
//    public void onBackPressed() {
//        gotyeAPI.stopTalk();
//        gotyeAPI.stopPlay();
//        super.onBackPressed();
//    }
//
//    /**
//     * 点击聊天页面的用户头像进入用户详情页
//     */
//    public void userinfo(View view) {
//        // TO DO...
//        showAlertDialog("用户详情页", "未完成");
//    }
//
//    /**
//     * 捕捉各种点击操作
//     * @param arg0
//     */
//    @Override
//    public void onClick(View arg0) {
//        switch (arg0.getId()) {
//            case R.id.ta_rl_topbar_back:
//                if (onRealTimeTalkFrom == 0) {
//                    ToastUtils.showShortToast(this, "正在实时语音,无法操作");
//                    return;
//                }
////			if(makingVoiceMessage){
////				ToastUtil.show(this, "正在语音短消息,无法操作");
////				return;
////			}
//                onBackPressed();
//                break;
//            case R.id.ta_iv_setting_mode:
//                ta_ll_emoji_container.setVisibility(View.GONE);
//                //语音切换成文本模式
//                if (pressToSendVoice.getVisibility() == View.VISIBLE) {//当前是语音模式
//                    pressToSendVoice.setVisibility(View.GONE);
//                    textEditor.setVisibility(View.VISIBLE);
//                    changeMode.setImageResource(R.drawable.ta_chat_footer_voice_icon);//显示语音图标
//                    pressToSendText.setVisibility(View.VISIBLE);
//                    showMoreType.setVisibility(View.GONE);
//                    moreTypeForSend = true;
//                    moreTypeLayout.setVisibility(View.GONE);
//
//                    //消除掉录音层
//                    voicePopUpLayout.setVisibility(View.GONE);
//                    volume.setImageResource(R.drawable.amp1);
//                } else {
//                    //文本切换成语音
//                    pressToSendVoice.setVisibility(View.VISIBLE);
//                    textEditor.setVisibility(View.GONE);
//
//                    changeMode.setImageResource(R.drawable.ta_chat_footer_text_icon);//显示文本图标
//
//                    pressToSendText.setVisibility(View.GONE);
//                    showMoreType.setVisibility(View.VISIBLE);
//                    moreTypeForSend = false;
//                    hideKeyboard();
//                }
//
//                break;
//            case R.id.ta_iv_more_type:
//                ta_ll_emoji_container.setVisibility(View.GONE);
//                if (moreTypeForSend) {
//                    hideKeyboard();
//                    String str = textEditor.getText().toString();
//                    sendTextMessage(str);
//                    textEditor.setText("");
//                } else {
//                    if (moreTypeLayout.getVisibility() == View.VISIBLE) {
//                        moreTypeLayout.setVisibility(View.GONE);
//                    } else {
//                        moreTypeLayout.setVisibility(View.VISIBLE);
//                    }
//                }
//                break;
//            case R.id.to_gallery:
//                takePic();
//                break;
//            case R.id.to_camera:
//                selectPicFromCamera();
//                break;
//            case R.id.voice_to_text:
//                // showTalkView();
//                break;
//            case R.id.real_time_voice_chat:
//                realTimeTalk();
//                break;
//            case R.id.stop_real_talk:
//                gotyeAPI.stopTalk();
//                break;
//            default:
//                break;
//        }
//    }
//
//    public void hideKeyboard() {
//        // 隐藏输入法
//        InputMethodManager imm = (InputMethodManager) getApplicationContext()
//                .getSystemService(Context.INPUT_METHOD_SERVICE);
//        // 显示或者隐藏输入法
//        imm.hideSoftInputFromWindow(textEditor.getWindowToken(),
//                InputMethodManager.HIDE_NOT_ALWAYS);
//    }
//
//    /**
//     * 打开手机图片相册
//     */
//    private void takePic() {
//        Intent intent;
//        intent = new Intent(Intent.ACTION_PICK,
//                MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
//        intent.setType("image/jpeg");
//        startActivityForResult(intent, REQUEST_PIC);
//
//        // Intent intent = new Intent(Intent.ACTION_GET_CONTENT, null);
//        // intent.setType("image/*");
//        // startActivityForResult(intent, REQUEST_PIC);
//    }
//
//    /**
//     * 从手机外存储选择图片
//     */
//    public void selectPicFromCamera() {
//        if (!CommonUtils.isExitsSdcard()) {
//
//            ToastUtils.showShortToast(getApplicationContext(), "SD卡不存在，不能拍照");
//            return;
//        }
//
//        cameraFile = new File(URIUtil.getAppFIlePath()
//                + +System.currentTimeMillis() + ".jpg");
//        cameraFile.getParentFile().mkdirs();
//        startActivityForResult(
//                new Intent(MediaStore.ACTION_IMAGE_CAPTURE).putExtra(
//                        MediaStore.EXTRA_OUTPUT, Uri.fromFile(cameraFile)),
//                REQUEST_CAMERA);
//    }
//
//    public void realTimeTalk() {
//        if (onRealTimeTalkFrom > 0) {
//            Toast.makeText(this, "请稍后...", Toast.LENGTH_SHORT).show();
//            return;
//        }
////        gotyeAPI.startTalk(room, WhineMode.DEFAULT, true, Voice_MAX_TIME_LIMIT);
////        moreTypeLayout.setVisibility(View.GONE);
//    }
//
//    /**
//     * 处理Activity的回调
//     */
//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        // 选取图片的返回值
//        if (requestCode == REQUEST_PIC) {
//            if (data != null) {
//                Uri selectedImage = data.getData();
//                if (selectedImage != null) {
//                    String path = URIUtil.uriToPath(this, selectedImage);
//                    sendPicture(path);
//                }
//            }
//
//        } else if (requestCode == REQUEST_CAMERA) {
//            if (resultCode == RESULT_OK) {
//
//                if (cameraFile != null && cameraFile.exists())
//                    sendPicture(cameraFile.getAbsolutePath());
//            }
//        }
//        // TODO 获取图片失败
//        super.onActivityResult(requestCode, resultCode, data);
//    }
//
//    /**
//     *
//     * 异步发送图片
//     */
//    private void sendPicture(String path) {
//        SendImageMessageTask task = new SendImageMessageTask(this, gotyeUser);
//
//        task.execute(path);
//    }
//
//    /**
//     * 重发消息
//     */
//    public void resendMessage(GotyeMessage message) {
//        //判断消息类型重发
//        gotyeAPI.sendMessage(message);
//    }
//
//    /**
//     * 发送消息后的回调
//     * @param code
//     * @param message
//     */
//    @Override
//    public void onSendMessage(int code, GotyeMessage message) {
//        LogHelper.info(this, "############## onSendMessage,code= " + code + " message = " + message);
//        // GotyeChatManager.getInstance().insertChatMessage(message);
//
//        adapter.updateMessage(message);
//        if (message.getType() == GotyeMessageType.GotyeMessageTypeAudio) {
//            // api.decodeMessage(message);
//        }
//        // message.senderUser =
//        // DBManager.getInstance().getUser(currentLoginName);
//        pullListView.setSelection(adapter.getCount());
//    }
//
//    /**
//     * 接收服务器端送过来的聊天消息推送
//     * @param message
//     */
//    @Override
//    public void onReceiveMessage(GotyeMessage message) {
//
//        LogHelper.info(this, ">>>>>>>>>>>>>>Receive msg:"+message.toString());
//
//        if (isMyMessage(message)) {
//            adapter.addMsgToBottom(message);
//            pullListView.setSelection(adapter.getCount());
//            //下载消息异步请求,处理在回调中
//            if(message.getType() == GotyeMessageType.GotyeMessageTypeImage) {
//                LogHelper.debug(this, "onReceiveMessage type is image:"+message.toString());
//            }
//            gotyeAPI.downloadMediaInMessage(message);
//        }
//    }
//
//    /**
//     * 判断一下接收到的消息是不是当前聊天的对象发送给我的
//     * @param message
//     * @return
//     */
//    private boolean isMyMessage(GotyeMessage message) {
//        if (message.getSender() != null
//                && gotyeUser.getName().equals(message.getSender().getName())
//                && String.valueOf(loginUser.getUserId()).equals(
//                message.getReceiver().getName())) {
//            return true;
//        } else {
//            return false;
//        }
//    }
//
//    /**
//     * 下载聊天消息后的回调处理
//     * @param code
//     * @param message
//     */
//    @Override
//    public void onDownloadMediaInMessage(int code, GotyeMessage message) {
//        LogHelper.debug(this, "onDownloadMediaInMessage code["+code+"], msg:"+message.toString());
//        //将收到的消息更新到当前聊天视图
//        adapter.updateChatMessage(message);
//    }
//
//    /**
//     * 获取历史消息回调
//     * @param code
//     * @param list
//     */
//    @Override
//    public void onGetMessageList(int code, List<GotyeMessage> list) {
//        List<GotyeMessage> listmessages = gotyeAPI.getMessageList(gotyeUser, false);
//        if (listmessages != null) {
//            adapter.refreshData(listmessages);
//        } else {
//            ToastUtils.showShortToast(this, "没有历史记录");
//        }
//
//        adapter.notifyDataSetInvalidated();
//        pullListView.onRefreshComplete();
//    }
//
//    boolean realTalk, realPlay;
//
//    /**
//     * 处理录音时声音的变化
//     */
//    private Handler volumeHandler = new Handler();
//
//    private static final int POLL_INTERVAL = 300;
//
//    private Runnable mVolumeTask = new Runnable() {
//        public void run() {
//            int amp = gotyeAPI.getTalkingPower();//amp取值范围:[0,255]
//            updateDisplay(amp);
//            volumeHandler.postDelayed(mVolumeTask, POLL_INTERVAL);
//
//        }
//    };
//
//    /**
//     *
//     * 实时更新音量的变化
//     */
//    private void updateDisplay(int signalFeedback) {
//        int choice = signalFeedback/30;
//        switch (choice) {
//            case 0:
//            case 1:
//                volume.setImageResource(R.drawable.amp1);
//                break;
//            case 2:
//                volume.setImageResource(R.drawable.amp2);
//                break;
//            case 3:
//                volume.setImageResource(R.drawable.amp3);
//                break;
//            case 4:
//                volume.setImageResource(R.drawable.amp4);
//                break;
//            case 5:
//                volume.setImageResource(R.drawable.amp5);
//                break;
//            case 6:
//                volume.setImageResource(R.drawable.amp6);
//                break;
//            case 7:
//                volume.setImageResource(R.drawable.amp7);
//                break;
//            default:
//                volume.setImageResource(R.drawable.amp7);
//                break;
//        }
//    }
//
//
//    @Override
//    public void onStartTalk(int code, boolean isRealTime, int targetType,
//                            GotyeChatTarget target) {
//
//        if(code == GotyeStatusCode.CodeOK) {
//            //开始显示录音视图层
//            voicePopUpLayout.setVisibility(View.VISIBLE);
//
//            //监听音量变化
//            volumeHandler.postDelayed(mVolumeTask, POLL_INTERVAL);
//        }
//
//        if (isRealTime) {
//            this.realTalk = true;
//            if (code != 0) {
//                ToastUtils.showShortToast(this, "抢麦失败，先听听别人说什么。");
//                return;
//            }
//            if (GotyeVoicePlayClickPlayListener.isPlaying) {
//                GotyeVoicePlayClickPlayListener.currentPlayListener
//                        .stopPlayVoice(true);
//            }
//            onRealTimeTalkFrom = 0;
////            realTimeAnim.start();
////            realTalkView.setVisibility(View.VISIBLE);
////            realTalkName.setText("您正在说话..");
////            stopRealTalk.setVisibility(View.VISIBLE);
//        } else {
//            makingVoiceMessage = true;
//        }
//
//
//    }
//
//    @Override
//    public void onStopTalk(int code, GotyeMessage message, boolean isVoiceReal) {
//
//        volumeHandler.removeCallbacks(mVolumeTask);
//
//        //关闭显示录音视图层
//        voicePopUpLayout.setVisibility(View.GONE);
//        volume.setImageResource(R.drawable.amp1);
//
//        if(code == GotyeStatusCode.CodeOK) {
//            //录音成功...
//        }
//
//        if (isVoiceReal) {
//            onRealTimeTalkFrom = -1;
////            realTimeAnim.stop();
////            realTalkView.setVisibility(View.GONE);
//        } else {
//            if (code != 0 || message == null) {
//
//                voice_rcd_hint_loading.setVisibility(View.GONE);
//                voice_rcd_hint_rcding.setVisibility(View.VISIBLE);
//
//                ToastUtils.showShortToast(this, "时间太短...");
//                return;
//            }
//
//
//
//            // api.decodeMessage(message);
//            gotyeAPI.sendMessage(message);
//            adapter.addMsgToBottom(message);
//            refreshToTail();
//            makingVoiceMessage = false;
//        }
//
//    }
//
//    @Override
//    public void onPlayStop(int code) {
//        setPlayingId(-1);
//
//        if (this.realPlay) {
//            onRealTimeTalkFrom = -1;
////            realTimeAnim.stop();
////            realTalkView.setVisibility(View.GONE);
//        }
//        if (this.realPlay) {
//            this.realPlay = false;
//        }
//        adapter.notifyDataSetChanged();
//    }
//
//    /**
//     * 查询用户详情回调
//     * @param code
//     * @param user
//     */
//    @Override
//    public void onGetUserDetail(int code, GotyeUser user) {
//        if (user.getName().equals(this.gotyeUser.getName())) {
//            this.gotyeUser = user;
//        }
//    }
//
//    @Override
//    public void onDownloadMedia(int code, GotyeMedia media) {
//        adapter.notifyDataSetChanged();
//    }
//
//    /**
//     * 举报回调
//     * @param code
//     * @param message
//     */
//    @Override
//    public void onReport(int code, GotyeMessage message) {
//        // TODO Auto-generated method stub
//        if (code == GotyeStatusCode.CodeOK) {
//            ToastUtils.showShortToast(this, "举报成功");
//        } else {
//            ToastUtils.showShortToast(this, "举报失败");
//        }
//        super.onReport(code, message);
//    }
//
////    @Override
////    public void onDecodeMessage(int code, GotyeMessage message) {
////        if (code == GotyeStatusCode.CodeOK) {
////            VoiceToTextUtil util = new VoiceToTextUtil(this, mASREngine);
////            util.toPress(message);
////        }
////        super.onDecodeMessage(code, message);
////    }
//
//    @Override
//    public void onLogin(int code, GotyeUser currentLoginUser) {
//        //如果从离线转为登录后,隐藏头部的异步提示
//        if(code == GotyeStatusCode.CodeOK || code == GotyeStatusCode.CodeOfflineLoginOK
//                || code == GotyeStatusCode.CodeReloginOK) {
//            FragmentHelper.refreshTopbarErrTips(this, null);
//        }
//
//    }
//
//    @Override
//    public void onLogout(int code) {
//        if(code == GotyeStatusCode.CodeOK) {
//            FragmentHelper.refreshTopbarErrTips(this, "您已登出");
//        } else if(code == GotyeStatusCode.CodeForceLogout) {
//            FragmentHelper.refreshTopbarErrTips(this, "账号在其它设备登录,您被强制下线");
//        } else {
//            FragmentHelper.refreshTopbarErrTips(this, "您已处于离线状态");
//        }
//    }
//
//    @Override
//    public void onReconnecting(int code, GotyeUser currentLoginUser) {
//        // TODO Auto-generated method stub
//        FragmentHelper.refreshTopbarErrTips(this, "重新登录中...");
//    }
//}
