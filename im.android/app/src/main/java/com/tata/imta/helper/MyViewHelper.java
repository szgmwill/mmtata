package com.tata.imta.helper;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.style.ImageSpan;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.tata.imta.R;
import com.tata.imta.anim.AnimatedGifDrawable;
import com.tata.imta.anim.AnimatedImageSpan;
import com.tata.imta.bean.status.FeeUnit;
import com.tata.imta.bean.status.Sex;
import com.tata.imta.util.ShowUtils;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Will Zhang on 2015/7/1.
 * 一些公共视图处理工具类
 */
public class MyViewHelper {

    /**
     * 处理年龄性别视图的渲染
     */
    public static void showSexAgeView(View view, Sex sex, int age) {
        if(view != null && sex != null) {

            RelativeLayout rl = null;
            if(view.getId() == R.id.ta_item_sex_age_parent) {
                rl = (RelativeLayout) view;
            } else {
                rl = (RelativeLayout) view.findViewById(R.id.ta_item_sex_age_parent);
            }

            ImageView sexIV = (ImageView) rl.findViewById(R.id.ta_item_iv_icon_sex);
            TextView ageTV = (TextView) rl.findViewById(R.id.ta_item_tv_sex_age);

            if(sex == Sex.BOY) {
                rl.setBackgroundResource(R.drawable.ta_shape_yuanjiao_boy);//男性背景图
                sexIV.setImageResource(R.drawable.ta_icon_male);//男性icon
            } else {
                //默认展示女性
            }
            ageTV.setText(String.valueOf(age));
        }
    }

    /**
     * 处理用户聊天资费的展示
     */
    public static void showUnitPrice(View view, BigDecimal fee, FeeUnit unit, int duration) {
        if(view != null && fee != null && unit != null) {
            String showUnit = ShowUtils.showUnit(unit, duration);
            showUnitPrice(view, fee, showUnit);
        }
    }

    public static void showUnitPrice(View view, BigDecimal fee, String showUnit) {
        if(view != null && fee != null && !TextUtils.isEmpty(showUnit)) {
            RelativeLayout parent = null;
            if(view.getId() == R.id.ta_item_unit_price_parent) {
                parent = (RelativeLayout) view;
            } else {
                parent = (RelativeLayout) view.findViewById(R.id.ta_item_unit_price_parent);
            }

            parent.findViewById(R.id.ta_rl_show_free).setVisibility(View.GONE);
            parent.findViewById(R.id.ta_rl_show_price).setVisibility(View.VISIBLE);
            TextView tv_price = (TextView) parent.findViewById(R.id.ta_item_unit_price_tv_price);
            TextView tv_unit = (TextView) parent.findViewById(R.id.ta_item_unit_price_tv_unit);

            tv_price.setText(String.valueOf(fee));
            tv_unit.setText(showUnit);
        }
    }


    /**
     * 处理富文本文字
     * 中间夹杂了表情图标
     */
    public static void processTextMsg(Context context, final TextView gifTextView,String content) {
        SpannableStringBuilder sb = new SpannableStringBuilder(content);
        //表情的占位符
        String regex = "(\\#\\[face/png/f_static_)\\d{3}(.png\\]\\#)";
        Pattern p = Pattern.compile(regex);
        Matcher m = p.matcher(content);
        while (m.find()) {
            String tempText = m.group();
            try {
                String num = tempText.substring("#[face/png/f_static_".length(), tempText.length()- ".png]#".length());
                String gif = "face/gif/f" + num + ".gif";
                /**
                 * 如果open这里不抛异常说明存在gif，则显示对应的gif
                 * 否则说明gif找不到，则显示png
                 * */
                InputStream is = context.getAssets().open(gif);
                sb.setSpan(new AnimatedImageSpan(new AnimatedGifDrawable(is,new AnimatedGifDrawable.UpdateListener() {
                            @Override
                            public void update() {
                                gifTextView.postInvalidate();
                            }
                        })), m.start(), m.end(),
                        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                is.close();
            } catch (Exception e) {
                String png = tempText.substring("#[".length(),tempText.length() - "]#".length());
                try {
                    sb.setSpan(new ImageSpan(context, BitmapFactory.decodeStream(context.getAssets().open(png))), m.start(), m.end(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                } catch (IOException e1) {
                    // TODO Auto-generated catch block
                    e1.printStackTrace();
                }
                e.printStackTrace();
            }
        }
        //展示出来
        gifTextView.setText(sb);
    }

}
