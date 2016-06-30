package com.ytdinfo.keephealth.adapter;

 
 import java.util.List;

import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;
 
 public class ImageAdapter extends PagerAdapter {
     private List<View> list;
 
     public ImageAdapter(List<View> list) {// ���ù���������list���ϲ���
         this.list = list;
     }
 
     @Override
     public int getCount() {// ����ҳ������
         if (list.size() != 0) {
             return list.size();
         }
         return 0;
     }
 
     @Override
     public boolean isViewFromObject(View arg0, Object arg1) {//�ж��Ƿ�Ϊview����
        return arg0==arg1;//�ٷ�demo����Ľ���д��
     }
     
     @Override
     public Object instantiateItem(ViewGroup container, int position) {//ʵ��һ��ҳ����view��������ViewGroup��
         container.addView(list.get(position));
         return list.get(position);
     }
     
     @Override
     public void destroyItem(ViewGroup container, int position, Object object) {//���һ��ҳ��
         container.removeView(list.get(position));
     }
 
 }