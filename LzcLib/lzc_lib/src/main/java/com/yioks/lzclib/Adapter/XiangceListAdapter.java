package com.yioks.lzclib.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.yioks.lzclib.Data.XiangceData;
import com.yioks.lzclib.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lzc on 2016/7/18 0018.
 * 相册选取的适配器类
 */
public class XiangceListAdapter extends BaseAdapter {
    private List<XiangceData>xiangceDataList=new ArrayList<>();
    Context context;
    private int SelectPostion=0;

    public XiangceListAdapter(Context context) {
        this.context = context;
    }

    @Override
    public int getCount() {
        return xiangceDataList.size();
    }

    @Override
    public Object getItem(int position) {
        return xiangceDataList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if(convertView==null)
        {
            convertView= LayoutInflater.from(context).inflate(R.layout.item_xiangce_layout,null);
            ViewHolder viewHolder=new ViewHolder();
            viewHolder.yulan_pic= (ImageView) convertView.findViewById(R.id.xiangce_yulan);
            viewHolder.xiangce_name= (TextView) convertView.findViewById(R.id.xiangce_name);
            viewHolder.pic_count= (TextView) convertView.findViewById(R.id.pic_count);
            viewHolder.radioButton= (RadioButton) convertView.findViewById(R.id.choice_radio_button);
            convertView.setTag(viewHolder);
        }
        ViewHolder viewHolder= (ViewHolder) convertView.getTag();
        XiangceData xiangceData=xiangceDataList.get(position);
        Picasso.with(context).load(xiangceData.getUri()).fit().centerCrop().into(viewHolder.yulan_pic);
        viewHolder.xiangce_name.setText(xiangceData.getName());
        viewHolder.pic_count.setText("" + xiangceData.getCount());
        if(SelectPostion==position)
        {
            viewHolder.radioButton.setChecked(true);
        }
        else
        {
            viewHolder.radioButton.setChecked(false);
        }
        return convertView;
    }

    public class ViewHolder
    {
        ImageView yulan_pic;
        TextView xiangce_name;
        TextView pic_count;
        RadioButton radioButton;
    }

    public List<XiangceData> getXiangceDataList() {
        return xiangceDataList;
    }

    public void setXiangceDataList(List<XiangceData> xiangceDataList) {
        this.xiangceDataList = xiangceDataList;
    }

    public int getSelectPostion() {
        return SelectPostion;
    }

    public void setSelectPostion(int selectPostion) {
        SelectPostion = selectPostion;
    }
}
