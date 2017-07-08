package com.yioks.lzclib.Adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by asus-pc on 2016/3/2.
 */
public class CommonFragmentPagerAdapter extends FragmentStatePagerAdapter {

    private List<TabData>tabDataList=new ArrayList<>();
    private List<Fragment>fragmentList;

    public static class TabData
    {
        String name;
        String id;

        public TabData() {
        }

        public TabData(String name, String id) {
            this.name = name;
            this.id = id;
        }
    }
    public CommonFragmentPagerAdapter(FragmentManager fm)
    {
        super(fm);
    }
    public CommonFragmentPagerAdapter(FragmentManager fm,  List<TabData>tabDataList, List<Fragment> fragmentList) {
        super(fm);
        this.tabDataList=tabDataList;
        this.fragmentList=fragmentList;
    }
    @Override
    public Fragment getItem(int position) {
        return fragmentList.get(position);
    }

    @Override
    public int getCount() {
        return fragmentList.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return tabDataList.get(position).name;
    }

    public List<TabData> getTabDataList() {
        return tabDataList;
    }

    public void setTabDataList(List<TabData> tabDataList) {
        this.tabDataList = tabDataList;
    }

    public List<Fragment> getFragmentList() {
        return fragmentList;
    }

    public void setFragmentList(List<Fragment> fragmentList) {
        this.fragmentList = fragmentList;
    }
}
