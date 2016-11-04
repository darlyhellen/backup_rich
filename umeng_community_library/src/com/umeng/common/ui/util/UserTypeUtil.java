package com.umeng.common.ui.util;

import android.R;
import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Typeface;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.umeng.comm.core.beans.CommConfig;
import com.umeng.comm.core.beans.CommUser;
import com.umeng.comm.core.utils.CommonUtils;
import com.umeng.comm.core.utils.ResFinder;
import com.umeng.common.ui.widgets.NetworkImageView;


/**
 * Created by wangfei on 16/1/28.
 */
public class UserTypeUtil {
    @SuppressLint("ResourceAsColor") public static void SetUserType(Context context, CommUser user,LinearLayout layout){
        if (layout!=null) {
            layout.removeAllViews();
        }
        //To-DO
//        if (user!=null&&user.medals!=null&&user.medals.size()!=0){

//            NetworkImageView img= new NetworkImageView(context);
//            img.setImageUrl(user.medals.get(0).imgUrl);
        if(user!=null){
//            ImageView level=new ImageView(context);
//          if(user.score<=200&&user.score>=0){
//        	  level.setImageDrawable(ResFinder.getDrawable("level1"));
//          }else if(user.score>200&&user.score<=1000)
//          {
//        	  level.setImageDrawable(ResFinder.getDrawable("level2"));
//          }else if(user.score>1000&&user.score<=4000)
//          {
//        	  level.setImageDrawable(ResFinder.getDrawable("level3"));
//          }else if(user.score>4000&&user.score<=15000)
//          {
//        	  level.setImageDrawable(ResFinder.getDrawable("level4"));
//          }else if(user.score>15000&&user.score<=20000)
//          {
//        	  level.setImageDrawable(ResFinder.getDrawable("level5"));
//          }else if(user.score>20000&&user.score<=60000)
//          {
//        	  level.setImageDrawable(ResFinder.getDrawable("level6"));
//          }else if(user.score>60000)
//          {
//        	  level.setImageDrawable(ResFinder.getDrawable("level7"));
//          }
        	if("Admin".equalsIgnoreCase(user.customField)
        			||"Doctor".equalsIgnoreCase(user.customField)
        			  ||"Assistant".equalsIgnoreCase(user.customField)){
        		 ImageView expert=new ImageView(context);
        		 expert.setImageDrawable(ResFinder.getDrawable("official_cert"));
                 int height = CommonUtils.dip2px(context,15);
                 LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, height);
                 layout.setVisibility(View.VISIBLE);
                 layout.addView(expert,lp);
                 
        	}
        }
//        }
    }
}
