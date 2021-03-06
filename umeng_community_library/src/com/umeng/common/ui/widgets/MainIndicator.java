package com.umeng.common.ui.widgets;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.umeng.comm.core.constants.Constants;
import com.umeng.comm.core.utils.CommonUtils;
import com.umeng.comm.core.utils.DeviceUtils;
import com.umeng.comm.core.utils.ResFinder;

/**
 * Created by wangfei on 15/12/4.
 */
public class MainIndicator extends LinearLayout{
   
    /**
     * tab上的内容
     */
    private String[] mTabTitles;
    /**
     * 与之绑定的ViewPager
     */
    public ViewPager mViewPager;


    /**
     * 标题正常时的颜色
     */
    private static final int COLOR_TEXT_NORMAL = ResFinder
            .getColor("umeng_comm_indicator_default");
    /**
     * 标题选中时的颜色
     */
    protected static final int COLOR_TEXT_HIGHLIGHTCOLOR = ResFinder
            .getColor("umeng_comm_text_topic_light_color");

    public MainIndicator(Context context) {
        this(context, null);
    }

    public MainIndicator(Context context, AttributeSet attrs) {
        super(context, attrs);
      
    }
 
    /**
     * 设置tab的标题内容 可选，可以自己在布局文件中写死
     *
     * @param datas
     */
    public void setTabItemTitles(String[] datas) {
        // 如果传入的list有值，则移除布局文件中设置的view
        if (datas != null && datas.length > 0) {
            this.removeAllViews();
            this.mTabTitles = datas;

            for (int i = 0; i <mTabTitles.length;i++) {
                // 添加view
                addView(generateView(i,mTabTitles[i]));
                if(i!=3)
                {
                    View view =new View(getContext());
                    view.setBackgroundColor(ResFinder.getColor("umeng_comm_feed_detail_divider"));
                    LayoutParams lps = new LayoutParams(
                            2,CommonUtils.dip2px(getContext(), 30));
                    view.setLayoutParams(lps);
                    addView(view);
                }
            }
        }

    }

    /**
     * 对外的ViewPager的回调接口
     *
     * @author zhy
     */
    public interface PageChangeListener {
        public void onPageScrolled(int position, float positionOffset,
                                   int positionOffsetPixels);

        public void onPageSelected(int position);

        public void onPageScrollStateChanged(int state);
    }
    /**
     *
     * 每个item的click事件
     * @author wf
     */
    public interface IndicatorListener {
        public void SetItemClick();
    }
    public void SetIndictorClick(IndicatorListener listener){
        listener.SetItemClick();
    }
    // 设置关联的ViewPager
    public void setViewPager(ViewPager mViewPager, int pos) {
        this.mViewPager = mViewPager;

        mViewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                // 设置字体颜色高亮
                resetTextViewColor();
                highLightTextView(position*2);

            }

            @Override
            public void onPageScrolled(int position, float positionOffset,
                                       int positionOffsetPixels) {
               
            }

            @Override
            public void onPageScrollStateChanged(int state) {
              
            }
        });
        // 设置当前页
        mViewPager.setCurrentItem(pos);
        // 高亮
        highLightTextView(pos);
        mViewPager.setCurrentItem(pos);
    }

    /**
     * 高亮文本
     *
     * @param position
     */
    protected void highLightTextView(int position) {
        View view = getChildAt(position);
        if (view instanceof TextView) {
            ((TextView) view).setTextColor(COLOR_TEXT_HIGHLIGHTCOLOR);
            Drawable drawableLine = ResFinder.getDrawable("blue_line_for_bottom");
                drawableLine.setBounds(0,0,getScreenWidth()/4,DeviceUtils.dp2px(getContext(), 2));
                ((TextView) view).setCompoundDrawables(null,null,null,drawableLine);
        }
   
    }

    /**
     * 重置文本颜色
     */
    private void resetTextViewColor() {
        for (int i = 0; i < getChildCount(); i++) {
            View view = getChildAt(i);
            if (view instanceof TextView) {
                ((TextView) view).setTextColor(COLOR_TEXT_NORMAL);
                    ((TextView) view).setCompoundDrawables(null,null,null,null);
            }
        }
    }
 
    /**
     * 根据标题生成我们的TextView
     *
     * @param text
     * @return
     */
    private TextView generateView(int index,String text) {
        TextView tv = new TextView(getContext());
        LayoutParams lp = new LayoutParams(
                0, LayoutParams.MATCH_PARENT);
        lp.weight = 1;

        tv.setGravity(Gravity.CENTER_HORIZONTAL);
        tv.setTextColor(COLOR_TEXT_NORMAL);
        tv.setSingleLine();
        tv.setText(text);
        tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
        tv.setLayoutParams(lp);
        tv.setPadding(0,CommonUtils.dip2px(getContext(), 11),0,0);
        tv.setTag(index);
        tv.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mViewPager.setCurrentItem((Integer)v.getTag());
            }
        });
        return tv;
    }
 

    
    /**
     * 获得屏幕的宽度
     *
     * @return
     */
    public int getScreenWidth() {
        WindowManager wm = (WindowManager) getContext().getSystemService(
                Context.WINDOW_SERVICE);
        DisplayMetrics outMetrics = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(outMetrics);
        return outMetrics.widthPixels;
    }
}
