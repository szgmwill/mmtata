package com.tata.imta.adapter;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.TextView;

import com.gotye.api.GotyeAPI;
import com.gotye.api.GotyeMessage;
import com.gotye.api.GotyeMessageStatus;
import com.gotye.api.GotyeMessageType;
import com.tata.imta.R;
import com.tata.imta.activity.SingleChatActivity;
import com.tata.imta.activity.UserDetailActivity;
import com.tata.imta.app.BizInfoHolder;
import com.tata.imta.bean.ResultObject;
import com.tata.imta.bean.User;
import com.tata.imta.bean.status.ServerAPI;
import com.tata.imta.helper.ChatHelper;
import com.tata.imta.helper.ImageCache;
import com.tata.imta.helper.LoadDataFromServer;
import com.tata.imta.helper.LogHelper;
import com.tata.imta.helper.MyViewHelper;
import com.tata.imta.helper.ServerAPIHelper;
import com.tata.imta.helper.img.LoadUserImg;
import com.tata.imta.listener.MyVoicePlayClickPlayListener;
import com.tata.imta.page.DisplayBigImageActivity;
import com.tata.imta.task.GetUserDetailTask;
import com.tata.imta.util.BitmapUtil;
import com.tata.imta.util.ShowUtils;
import com.tata.imta.util.TimeUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 单聊页面
 * 聊天消息填充器
 */
public class SingleChatAdapter extends MyAdapter {

	public static final int TYPE_RECEIVE_TEXT = 0;
	public static final int TYPE_RECEIVE_IMAGE = 1;
	public static final int TYPE_RECEIVE_VOICE = 2;
	public static final int TYPE_RECEIVE_USER_DATA = 3;

	public static final int TYPE_SEND_TEXT = 4;
	public static final int TYPE_SEND_IMAGE = 5;
	public static final int TYPE_SEND_VOICE = 6;
	public static final int TYPE_SEND_USER_DATA = 7;

	//消息发送接收类型
	public static final int MESSAGE_DIRECT_RECEIVE = 1;//聊天对象发送的
	public static final int MESSAGE_DIRECT_SEND = 0;//我发送的
	public static final int MESSAGE_DIRECT_SYS = 2;//系统消息

	private SingleChatActivity activity;

	/**
	 * 亲加保存的与当前对象的聊天记录
	 */
	private List<GotyeMessage> messageList;

	private LayoutInflater inflater;
	/**
	 * 当前登录用户,即我
	 */
	private User loginUser;

	/**
	 * 对方
	 */
	private User targetUser;

	/**
	 * 自己的头像
	 */
	private Bitmap loginUserBitmap;
	/**
	 * 当前聊天对象的头像
	 */
	private Bitmap targetUserBitmap;

	private GotyeAPI api = GotyeAPI.getInstance();

    /** 弹出的更多选择框 针对每一条消息内容 */
    private PopupWindow popupWindow;

    /** 复制，删除 */
    private TextView copy, delete;

    /**
     * 执行动画的时间
     */
    protected long mAnimationTime = 150;

	//图片加载器
	private LoadUserImg loadUserImg;

	/**
	 * 图片缓存特殊处理部分
	 */
	private ImageCache cache = ImageCache.getInstance();

	public SingleChatAdapter(SingleChatActivity activity, List<GotyeMessage> messageList, User targetUser) {
		this.activity = activity;
		this.messageList = messageList;
		inflater = activity.getLayoutInflater();
		this.targetUser = targetUser;
		loadUserImg = new LoadUserImg();

		//初始化双方用户信息
		loginUser = BizInfoHolder.getInstance().getLoginUser();
		loginUserBitmap = loadUserImg.getBitMapFromHead(loginUser.getHead());
		targetUserBitmap = loadUserImg.getBitMapFromHead(targetUser.getHead());

		//初始化弹出功能选择器
//        initPopWindow();
	}

    /**
     * 初始化弹出的pop
     * */
    private void initPopWindow() {
        View popView = inflater.inflate(R.layout.ta_chat_row_popup_menu, null);
        //复制和删除功能
		copy = (TextView) popView.findViewById(R.id.chat_copy_menu);
        delete = (TextView) popView.findViewById(R.id.chat_delete_menu);
        popupWindow = new PopupWindow(popView, LayoutParams.WRAP_CONTENT,
                LayoutParams.WRAP_CONTENT);
        popupWindow.setBackgroundDrawable(new ColorDrawable(0));
        // 设置popwindow出现和消失动画
//		popupWindow.setAnimationStyle(R.style.PopMenuAnimation);
    }

	/**
	 * 将消息插入到最后一条
	 */
	public void addMsgToBottom(GotyeMessage msg) {
		//先判断一下该条消息是否已经存在,如果存在则先删除,再插入到最后,如果没有则直接加到最后
		int position = messageList.indexOf(msg);
		if (position < 0) {
			messageList.add(msg);
		} else {
			messageList.remove(position);
			messageList.add(position, msg);
		}
		notifyDataSetChanged();
	}

	/**
	 * 更新消息的同时更新UI
	 */
	public void updateMessage(GotyeMessage msg) {
		int position = messageList.indexOf(msg);
		if (position < 0) {
			return;
		}
		messageList.remove(position);
		//直接原记录保持位置还是加入到最尾?
//		messageList.add(position, msg);
		messageList.add(msg);//放到最后一条
		//更新UI
		notifyDataSetChanged();
	}

	/**
	 * 更新消息
	 */
	public void updateChatMessage(GotyeMessage msg) {
		if (messageList.contains(msg)) {
			int index = messageList.indexOf(msg);
			messageList.remove(index);
			messageList.add(index, msg);
			//notifyDataSetChanged();
		}
	}

	/**
	 * 将历史消息置顶
	 */
	public void addMessagesToTop(List<GotyeMessage> histMessages) {
		messageList.addAll(0, histMessages);
	}

	/**
	 * 将某条消息置顶
	 */
	public void addMessageToTop(GotyeMessage msg) {
		messageList.add(0, msg);
	}

	@Override
	public int getCount() {
		return messageList.size();
	}

	@Override
	public GotyeMessage getItem(int position) {
		if (position < 0 || position >= messageList.size()) {
			return null;
		} else {
			return messageList.get(position);
		}

	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	/**
	 * 获取消息的类别
	 */
	public int getItemViewType(int position) {
		GotyeMessage message = getItem(position);
		if (message.getType() == GotyeMessageType.GotyeMessageTypeText) {
			return getDirect(message) == MESSAGE_DIRECT_RECEIVE ? TYPE_RECEIVE_TEXT
					: TYPE_SEND_TEXT;
		}
		if (message.getType() == GotyeMessageType.GotyeMessageTypeImage) {
			return getDirect(message) == MESSAGE_DIRECT_RECEIVE ? TYPE_RECEIVE_IMAGE
					: TYPE_SEND_IMAGE;

		}
		if (message.getType() == GotyeMessageType.GotyeMessageTypeAudio) {
			return getDirect(message) == MESSAGE_DIRECT_RECEIVE ? TYPE_RECEIVE_VOICE
					: TYPE_SEND_VOICE;
		}
		if (message.getType() == GotyeMessageType.GotyeMessageTypeUserData) {
			return getDirect(message) == MESSAGE_DIRECT_RECEIVE ? TYPE_RECEIVE_USER_DATA
					: TYPE_SEND_USER_DATA;
		}
		return -1;// invalid
	}

	public int getViewTypeCount() {
		return 8;
	}

    /**
     * 获得聊天记录视图生成器
     * @param position
     * @param convertView
     * @param parent
     * @return
     */
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		final GotyeMessage message = getItem(position);
		final ViewHolder holder;
		//消息的发送方,对方或者是我
		int directType = getDirect(message);
		//消息类型
		GotyeMessageType msgType = message.getType();

		if (convertView == null) {
			holder = new ViewHolder();

			if(msgType == GotyeMessageType.GotyeMessageTypeUserData) {//如果是自定义消息

				long orderId = ChatHelper.getOrderIdFromUserExtraMessage(message);

				if(orderId > 0) {//是服务确认消息
					if(directType == MESSAGE_DIRECT_RECEIVE) {//对方发送给我的确认消息

						//生成确认服务视图
						convertView = inflater.inflate(R.layout.ta_chat_row_recv_confirm, null);

					}
				}

				if(convertView == null) {
					//如果没有匹配到则默认文字消息
					//普通文本
					convertView = inflater.inflate(R.layout.ta_chat_row_send_msg, null);
				}
			} else {
				//亲加消息类型
				//先得到一条聊天记录行视图
				convertView = createViewByMessage(msgType, directType);
			}

			//初始化各聊天行视图控件
            if (msgType == GotyeMessageType.GotyeMessageTypeImage) {

				if(directType == MESSAGE_DIRECT_SEND) {//我发送的
					holder.timestamp = (TextView) convertView.findViewById(R.id.ta_chat_row_send_timestamp);
					holder.iv = ((ImageView) convertView
							.findViewById(R.id.ta_chat_row_send_iv_pic));
					holder.head = (ImageView) convertView
							.findViewById(R.id.ta_chat_row_send_iv_avatar);
					holder.percentage = (TextView) convertView
							.findViewById(R.id.ta_chat_row_send_percentage);
					holder.pb = (ProgressBar) convertView
							.findViewById(R.id.ta_chat_row_send_progressBar);
					holder.staus_iv = (ImageView) convertView
							.findViewById(R.id.ta_chat_row_send_iv_status);
					holder.tv_ack = (TextView) convertView.findViewById(R.id.ta_chat_row_send_tv_ack);

					holder.tv_delivered = (TextView) convertView
							.findViewById(R.id.ta_chat_row_send_tv_delivered);
				} else {//我收到的
					holder.timestamp = (TextView) convertView.findViewById(R.id.ta_chat_row_recv_timestamp);
					holder.iv = ((ImageView) convertView
							.findViewById(R.id.ta_chat_row_recv_iv_pic));
					holder.head = (ImageView) convertView
							.findViewById(R.id.ta_chat_row_recv_iv_avatar);

					holder.pb = (ProgressBar) convertView
							.findViewById(R.id.ta_chat_row_recv_progressBar);

					holder.nick = (TextView) convertView.findViewById(R.id.ta_chat_row_recv_tv_nick);
				}


			} else if (msgType == GotyeMessageType.GotyeMessageTypeAudio) {
				//语音
				if(directType == MESSAGE_DIRECT_SEND) {//我发送的
					holder.timestamp = (TextView) convertView.findViewById(R.id.ta_chat_row_send_timestamp);
					holder.head = (ImageView) convertView
							.findViewById(R.id.ta_chat_row_send_iv_avatar);
					//语音图标
					holder.iv = (ImageView) convertView.findViewById(R.id.ta_chat_row_send_iv_voice);
					//语音时长
					holder.tv = (TextView) convertView.findViewById(R.id.ta_chat_row_send_tv_voice_length);

					//以下为各种状态值
					holder.pb = (ProgressBar) convertView
							.findViewById(R.id.ta_chat_row_send_pb_sending);
					holder.staus_iv = (ImageView) convertView
							.findViewById(R.id.ta_chat_row_send_iv_status);
					holder.tv_ack = (TextView) convertView.findViewById(R.id.ta_chat_row_send_tv_ack);

					holder.tv_delivered = (TextView) convertView
							.findViewById(R.id.ta_chat_row_send_tv_delivered);

				} else {//我收到的
					holder.timestamp = (TextView) convertView.findViewById(R.id.ta_chat_row_recv_timestamp);
					holder.head = (ImageView) convertView
							.findViewById(R.id.ta_chat_row_recv_iv_avatar);
					//语音图标
					holder.iv = (ImageView) convertView.findViewById(R.id.ta_chat_row_recv_iv_voice);
					//语音时长
					holder.tv = (TextView) convertView.findViewById(R.id.ta_chat_row_recv_tv_voice_length);

					//未读标记
					holder.iv_read_status = (ImageView) convertView.findViewById(R.id.ta_chat_row_recv_iv_unread_voice);

					holder.nick = (TextView) convertView.findViewById(R.id.ta_chat_row_recv_tv_nick);
				}

			} else if (msgType == GotyeMessageType.GotyeMessageTypeUserData && directType == MESSAGE_DIRECT_RECEIVE) {
				//我收到的对方给发的服务确认消息

				//时间戳
				holder.timestamp = (TextView) convertView.findViewById(R.id.ta_chat_row_recv_timestamp);

				//头像
				holder.head = (ImageView) convertView.findViewById(R.id.ta_chat_row_confirm_iv_avatar);

				//确认按钮
				holder.tv = (TextView) convertView.findViewById(R.id.ta_chat_row_recv_tv_confirm);

				//评价
				holder.rb_pingjia = (RatingBar) convertView.findViewById(R.id.ta_chat_row_confirm_ratingbar);

			} else {
				//文本聊天
				if(directType == MESSAGE_DIRECT_SEND) {//我发送的
					holder.timestamp = (TextView) convertView.findViewById(R.id.ta_chat_row_send_timestamp);
					holder.head = (ImageView) convertView
							.findViewById(R.id.ta_chat_row_send_iv_avatar);

					//聊天内容
					holder.tv = (TextView) convertView.findViewById(R.id.ta_chat_row_send_tv_text);

					//以下为各种状态值
					holder.pb = (ProgressBar) convertView
							.findViewById(R.id.ta_chat_row_send_pb_sending);
					holder.staus_iv = (ImageView) convertView
							.findViewById(R.id.ta_chat_row_send_iv_status);
					holder.tv_ack = (TextView) convertView.findViewById(R.id.ta_chat_row_send_tv_ack);

					holder.tv_delivered = (TextView) convertView
							.findViewById(R.id.ta_chat_row_send_tv_delivered);

				} else {//我收到的
					holder.timestamp = (TextView) convertView.findViewById(R.id.ta_chat_row_recv_timestamp);
					holder.head = (ImageView) convertView
							.findViewById(R.id.ta_chat_row_recv_iv_avatar);
					//聊天内容
					holder.tv = (TextView) convertView.findViewById(R.id.ta_chat_row_recv_tv_text);

					//未读标记
					holder.iv_read_status = (ImageView) convertView.findViewById(R.id.ta_chat_row_recv_iv_unread_icon);

					holder.nick = (TextView) convertView.findViewById(R.id.ta_chat_row_recv_tv_nick);
				}
			}
            //保存视图
			convertView.setTag(holder);
		} else {
            //得到上次初始化好的视图
			holder = (ViewHolder) convertView.getTag();
		}

        //以下为视图填充数据内容
		switch (msgType) {
		// 根据消息type显示item
		case GotyeMessageTypeImage: // 图片
			handleImageMessage(message, holder);
			break;
		case GotyeMessageTypeAudio: // 语音
			handleVoiceMessage(message, holder);
			break;
		case GotyeMessageTypeUserData://自定义的服务确认消息

			handleConfirmMessage(message, holder);

			break;
		default:                    // 文字
			handleTextMessage(message, holder);
			break;
		}

        //统一显示消息的时间戳
		if(position == 0) {
			holder.timestamp.setText(TimeUtil.dateToMessageTime(message.getDate() * 1000));
		} else {
			//除了第一条消息外,其它消息判断时间间隔
			// 两条消息时间离得如果稍长，显示时间
			if (TimeUtil.needShowTime(message.getDate(), messageList.get(position - 1).getDate())) {
				holder.timestamp.setText(TimeUtil.dateToMessageTime(message.getDate() * 1000));
				holder.timestamp.setVisibility(View.VISIBLE);
			} else {
				holder.timestamp.setVisibility(View.INVISIBLE);
			}
		}


		//统一显示用户头像
		if(directType == MESSAGE_DIRECT_SEND) {//我发送的
			holder.head.setImageBitmap(loginUserBitmap);
		} else {
			if(targetUserBitmap != null) {
				holder.head.setImageBitmap(targetUserBitmap);
			}
		}

        //点击头像跳转到用户的个人详情页去
		holder.head.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				if(getDirect(message) == MESSAGE_DIRECT_SEND) {//我自己
					Intent i = new Intent(activity, UserDetailActivity.class);
					i.putExtra("user", loginUser);
					activity.startActivity(i);
				} else {
					//对方
					final long senderId = ShowUtils.parseLong(message.getSender().getName());

					if(senderId == 0) {
						showAlertDialog(activity, "错误提示", "没有该用户详情");
					} else {
						GetUserDetailTask task = new GetUserDetailTask(new GetUserDetailTask.GetUserDetailCallBack() {
							@Override
							public void onCallBack(Map<Long, User> resultMap) {
								User senderUser = resultMap.get(senderId);
								if(senderUser == null) {
									showAlertDialog(activity, "错误提示", "找不到用户信息");
								} else {
									Intent i = new Intent(activity, UserDetailActivity.class);
									i.putExtra("user", senderUser);
									activity.startActivity(i);
								}
							}
						});
						List<Long> userIdList = new ArrayList<Long>();
						userIdList.add(senderId);
						task.execute(userIdList);
					}
				}
			}
		});

		//是否要重发消息
		if(holder.staus_iv != null) {
			holder.staus_iv.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					activity.showConfirmDialog("要重发消息?", new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							activity.resendMessage(message);
						}
					});
				}
			});
		}

		return convertView;
	}

    /**
     * 填充图片视图
     */
	private void handleImageMessage(final GotyeMessage message, final ViewHolder holder) {

		//处理图片
		setImageMessage(holder, message);

		//处理发送状态
		if (getDirect(message) == MESSAGE_DIRECT_SEND) {
			switch (message.getStatus()) {
			case GotyeMessageStatusSent: // 发送成功
				holder.pb.setVisibility(View.GONE);
				holder.percentage.setVisibility(View.GONE);
				holder.staus_iv.setVisibility(View.GONE);
				break;
			case GotyeMessageStatusSendingFailed: // 发送失败
				holder.pb.setVisibility(View.GONE);
				holder.percentage.setVisibility(View.GONE);
				holder.staus_iv.setVisibility(View.VISIBLE);
				if (holder.tv_delivered != null) {
					holder.tv_delivered.setVisibility(View.GONE);
				}
				break;
			case GotyeMessageStatusSending: // 发送中
				holder.pb.setVisibility(View.VISIBLE);
				holder.staus_iv.setVisibility(View.GONE);
				if (holder.tv_delivered != null) {
					holder.tv_delivered.setVisibility(View.GONE);
				}
				break;
			default:
				holder.pb.setVisibility(View.GONE);
				holder.percentage.setVisibility(View.GONE);
				holder.staus_iv.setVisibility(View.GONE);
			}
		} else {
			holder.nick.setText(targetUser.getNick());
		}
	}

    /**
     * 填充普通文本内容
     */
	private void handleTextMessage(GotyeMessage message, ViewHolder holder) {

        //处理文字,同时兼容表情等
        MyViewHelper.processTextMsg(activity, holder.tv, message.getText());

		//文本消息发送结果
		if (getDirect(message) == MESSAGE_DIRECT_SEND) {
			switch (message.getStatus()) {
			case GotyeMessageStatusSent: // 发送成功
				holder.pb.setVisibility(View.GONE);
				holder.staus_iv.setVisibility(View.GONE);
				break;
			case GotyeMessageStatusSendingFailed: // 发送失败
				holder.pb.setVisibility(View.GONE);
				holder.staus_iv.setVisibility(View.VISIBLE);
				if (holder.tv_delivered != null) {
					holder.tv_delivered.setVisibility(View.GONE);
				}
				break;
			case GotyeMessageStatusSending: // 发送中
				holder.pb.setVisibility(View.VISIBLE);
				holder.staus_iv.setVisibility(View.GONE);
				if (holder.tv_delivered != null) {
					holder.tv_delivered.setVisibility(View.GONE);
				}
				break;
			default:
				holder.pb.setVisibility(View.GONE);
				holder.staus_iv.setVisibility(View.GONE);
			}
		} else {
			holder.nick.setText(targetUser.getNick());
		}
	}

    /**
     * 填充语音内容
     */
	private void handleVoiceMessage(final GotyeMessage message, final ViewHolder holder) {

		//时长
		holder.tv.setText(TimeUtil.getVoiceTime(message.getMedia().getDuration()));

		//加入点击将播放语音事件
		holder.iv.setOnClickListener(new MyVoicePlayClickPlayListener(
				message, holder.iv, this, activity));
		//当前这条语音消息是否在播放中
		boolean isPlaying = isPlaying(message);
		if (isPlaying) {
			//如果正在播放中的话,开启播放动画
			AnimationDrawable voiceAnimation;
			if (getDirect(message) == MESSAGE_DIRECT_RECEIVE) {
				holder.iv.setImageResource(R.drawable.ta_voice_from_playing);
			} else {
				holder.iv.setImageResource(R.drawable.ta_voice_to_playing);
			}
			voiceAnimation = (AnimationDrawable) holder.iv.getDrawable();
			voiceAnimation.start();//播放动画
		} else {
			//默认语音图标
			if (getDirect(message) == MESSAGE_DIRECT_RECEIVE) {
				holder.iv.setImageResource(R.drawable.ta_chat_recv_voice_playing);
			} else {
				holder.iv.setImageResource(R.drawable.ta_chat_send_voice_playing);
			}
		}

		if (getDirect(message) == MESSAGE_DIRECT_RECEIVE) {
			if (message.getStatus() == GotyeMessageStatus.GotyeMessageStatusUnread) {// if
				holder.iv_read_status.setVisibility(View.VISIBLE);
			} else {
				holder.iv_read_status.setVisibility(View.INVISIBLE);
			}

			holder.nick.setText(targetUser.getNick());
			return;
		}

		// until here, deal with send voice msg status
		switch (message.getStatus()) {
		case GotyeMessageStatusSent:
			holder.pb.setVisibility(View.GONE);
			holder.staus_iv.setVisibility(View.GONE);
			break;
		case GotyeMessageStatusSendingFailed:
			holder.pb.setVisibility(View.GONE);
			holder.staus_iv.setVisibility(View.VISIBLE);
			if (holder.tv_delivered != null) {
				holder.tv_delivered.setVisibility(View.GONE);
			}
			break;
		case GotyeMessageStatusSending:
			holder.pb.setVisibility(View.VISIBLE);
			holder.staus_iv.setVisibility(View.GONE);
			if (holder.tv_delivered != null) {
				holder.tv_delivered.setVisibility(View.GONE);
			}
			break;
		default:
			holder.pb.setVisibility(View.GONE);
			holder.staus_iv.setVisibility(View.GONE);
		}

		//如果语音正在下载
		switch (message.getMedia().getStatus()) {
		case MEDIA_STATUS_DOWNLOADING:
			holder.pb.setVisibility(View.VISIBLE);
			break;
		default:
			holder.pb.setVisibility(View.GONE);
			break;
		}
	}

	/**
	 * 处理自定义消息(服务确认)
	 */
	private void handleConfirmMessage(final GotyeMessage message, final ViewHolder holder) {
		final float[] currRating = {0};//当前的评分分数

		final long orderId = ChatHelper.getOrderIdFromUserExtraMessage(message);

		if(orderId > 0) {
			LogHelper.debug(SingleChatAdapter.class, "handleConfirmMessage ==> order_id[" + orderId + "]");

			if(getDirect(message) == MESSAGE_DIRECT_RECEIVE) {
				holder.rb_pingjia.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
					@Override
					public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
						currRating[0] = rating;
						LogHelper.debug(SingleChatAdapter.class, "onRatingChanged ==> rating["+rating+"]");
					}
				});

				holder.tv.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {

						//先判断一下有没有评分
						if (currRating[0] > 0) {
							activity.showConfirmDialog("确认服务完成?", new DialogInterface.OnClickListener() {
										@Override
										public void onClick(DialogInterface dialog, int which) {

											activity.showProgressDialog("正在处理中...");

											//确认完后把所有未处理的确认消息都给干掉
											removeAllConfirmRecv();

											//同时调用后台确认订单完成,同时钱会充值进收款方个人账户里
											Map<String, Object> paramMap = new HashMap<String, Object>();
											paramMap.put("order_id", orderId);
											paramMap.put("feedback_rate", currRating[0]);
											LoadDataFromServer confirmOrderTask = new LoadDataFromServer(activity, ServerAPI.SERVER_API_CONFIRM_ORDER_DONE,
													paramMap);

											confirmOrderTask.getData(new LoadDataFromServer.DataCallBack() {
												@Override
												public void onDataCallBack(ResultObject result) {
													activity.dismissProgressDialog();
													if (result != null && result.getCode() == ServerAPIHelper.ServerOK) {
														//自动发送确认回复
														String text = "我已确认完毕,请注意查收您账户余额的变化哦";
														ChatHelper.sendTextMessage(loginUser, targetUser, text);
													} else {
														activity.showAlertDialog("操作提示", "后台确认失败");
													}
												}
											});


										}
									}
							);
						} else {
							activity.showAlertDialog("确认服务完成", "请先给个评分,不要吝啬你的分数哦");
						}


					}
				});
			} else {
				//是我发的确认服务消息
				message.setText("我发起了聊天服务完成确认,等待对方的确认完成");
				handleTextMessage(message, holder);
			}


		}
	}

	/**
	 * 找出所有收到的服务确认消息删除掉
	 * 一旦确认完服务后,这类消息都全干掉,以防多次确认
	 */
	private void removeAllConfirmRecv() {
		List<GotyeMessage> copyList = new ArrayList<>();
		copyList.addAll(messageList);
		for(GotyeMessage msg : copyList) {
			if(msg.getType() == GotyeMessageType.GotyeMessageTypeUserData) {//只针对自定义确认消息
				api.deleteMessage(msg);
			}
		}
	}

	/**
	 * 判断当前语音消息是否正在播放当中
	 */
	private boolean isPlaying(GotyeMessage msg) {
		long id = msg.getDbId();
		long pid = activity.getPlayingId();
		if (id == pid) {
			return true;
		} else {
			return false;
		}

	}

	/**
	 * 生成一条聊天记录行视图
	 */
	private View createViewByMessage(GotyeMessageType msgType, int directType) {
		switch (msgType) {
		case GotyeMessageTypeImage://图片聊天布局
			return directType == MESSAGE_DIRECT_RECEIVE ? inflater
					.inflate(R.layout.ta_chat_row_recv_pic, null)
					: inflater.inflate(R.layout.ta_chat_row_send_pic, null);

		case GotyeMessageTypeAudio://语音聊天布局
			return directType == MESSAGE_DIRECT_RECEIVE ? inflater
					.inflate(R.layout.ta_chat_row_recv_voice, null)
					: inflater.inflate(R.layout.ta_chat_row_send_voice, null);

		default://默认文字聊天布局
			return directType == MESSAGE_DIRECT_RECEIVE ? inflater
					.inflate(R.layout.ta_chat_row_recv_msg, null)
					: inflater.inflate(R.layout.ta_chat_row_send_msg, null);
		}
	}

	/**
	 * 处理聊天图片展示
	 */
	private void setImageMessage(ViewHolder holder, final GotyeMessage msg) {
        //先从本地缓存中查找图片
		String ivPath = msg.getMedia().getPath();//缩略图地址
		final String ivPathEx = msg.getMedia().getPathEx();//大图地址

		if(getDirect(msg) == MESSAGE_DIRECT_SEND) {
			//如果是发送的图片,直接读本地大图显示
			ivPath = ivPathEx;
		}

		//从缓存和文件里去找这个图片
		Bitmap cacheBm = cache.get(ivPath);
		if (cacheBm != null) {
			holder.iv.setImageBitmap(cacheBm);
			holder.pb.setVisibility(View.GONE);
		} else if (ivPath != null) {
			//如果缓存找不到则从文件中查找
			Bitmap bm = BitmapUtil.getBitmap(ivPath);
			if (bm != null) {
				holder.iv.setImageBitmap(bm);
				cache.put(ivPath, bm);//同时加入缓存
			}
			holder.pb.setVisibility(View.GONE);
		}

        //单击图片时显示大图
		holder.iv.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {

				//处理大图的过程:先看下本地是否已经存在该大图,如果是,直接显示出来
				//如果本地没有,先调用亲加API下载,然后直接跳转显示大图活动页,并且显示正在加载中.
				//待后台将该图片下载完成后,再到大图活动页更新相关UI

//				NSString* path;    ///<保存语音/图片缩略图/用户数据的本地文件路径
//				NSString* pathEx; ///< 保存语音PCM元数据(需解码)/图片大图(需下载)的本地文件路径

                if(TextUtils.isEmpty(ivPathEx) || !new File(ivPathEx).exists()) {

					LogHelper.debug(activity, "Chat Image [" + ivPathEx + "] can not find local,downloading...");

                    //亲加API下载图片,注意在回调结果里处理展示和更新UI
					api.downloadMediaInMessage(msg);

					//先用缩略图替代显示
					String tempPath = msg.getMedia().getPath();
					if(tempPath != null) {
						showBigImage(tempPath, true);
					}

				} else {
					//本地有图片,直接展示
                    showBigImage(ivPathEx, false);
                }

			}
		});
	}

    /**
     * 大图查看器
     * 显示大图
     */
    private void showBigImage(String path, boolean isTemp) {
        if (!TextUtils.isEmpty(path) && new File(path).exists()) {
            Intent intent = new Intent(activity, DisplayBigImageActivity.class);
            Uri uri = Uri.fromFile(new File(path));
            intent.putExtra("uri", uri);
			intent.putExtra("isTemp", isTemp);//是否缩略图
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);//查看时如果当前处于显示中,则直接复用视图并且更新UI
			activity.startActivity(intent);
        }
    }

	private int getDirect(GotyeMessage message) {
		if (message.getSender().getName().equals(String.valueOf(loginUser.getUserId()))) {
			return MESSAGE_DIRECT_SEND;
		} else {
			return MESSAGE_DIRECT_RECEIVE;
		}
	}

	/**
	 * 聊天消息的数据载体
	 */
	public static class ViewHolder {
		/**
		 * 消息的时间
		 */
		TextView timestamp;

		/**
		 * 用户昵称
		 */
		TextView nick;

		/**
		 * 用户头像
		 */
		ImageView head;

		/**
		 * 通用图片组件
		 */
		ImageView iv;
		/**
		 * 通用文字组件
		 */
		TextView tv;
		/**
		 * 发送进度
		 */
		ProgressBar pb;

		/**
		 * 发送百分比
		 */
		TextView percentage;
		/**
		 * 发送状态
		 */
		ImageView staus_iv;

		/**
		 * 已读状态
		 */
		ImageView iv_read_status;
		// 显示已读回执状态
		TextView tv_ack;
		// 显示送达回执状态
		TextView tv_delivered;
		/**
		 * 附加信息
		 */
		TextView extra_data;

		/**
		 * 评分组件,只有自定义的服务确认消息才有
		 */
		RatingBar rb_pingjia;
	}

	public void refreshData(List<GotyeMessage> list) {
		// TODO Auto-generated method stub
		this.messageList = list;
		notifyDataSetChanged();
	}
}
