package com.google.st_apk_install;


import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

/**
 * Created by appchina on 2016/11/4.
 */

public class MyPagerAdapter extends PagerAdapter {

    List<View> viewList;
    List titleList;

    public MyPagerAdapter(List<View> viewList, List titleList) {
        this.viewList = viewList;
        this.titleList = titleList;
    }

    @Override
    public boolean isViewFromObject(View arg0, Object arg1) {
        // TODO Auto-generated method stub
        //根据传来的key，找到view,判断与传来的参数View arg0是不是同一个视图
        return arg0 == viewList.get((int)Integer.parseInt(arg1.toString()));
    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return viewList.size();
    }

    @Override
    public void destroyItem(ViewGroup container, int position,
                            Object object) {
        // TODO Auto-generated method stub
        container.removeView(viewList.get(position));
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        // TODO Auto-generated method stub
        container.addView(viewList.get(position));

        //把当前新增视图的位置（position）作为Key传过去
        return position;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        // TODO Auto-generated method stub
        return (CharSequence) titleList.get(position);
    }
}


