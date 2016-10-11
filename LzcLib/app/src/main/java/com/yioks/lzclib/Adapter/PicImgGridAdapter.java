package com.yioks.lzclib.Adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;

import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.Picasso;
import com.yioks.lzclib.R;
import com.yioks.lzclib.View.NumberCheckView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2016/7/15 0015.
 */
public class PicImgGridAdapter extends BaseAdapter {
    private List<Uri>uriList=new ArrayList<>();
    private List<Uri>choiceUriList=new ArrayList<>();
    private Context context;

    public PicImgGridAdapter(Context context) {
        this.context = context;
    }

    @Override
    public int getCount() {
        return uriList.size();
    }

    @Override
    public Object getItem(int position) {
        return uriList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        if(convertView==null)
        {
            ViewHoler viewHoler=new ViewHoler();
            convertView= LayoutInflater.from(context).inflate(R.layout.item_pick_img_layout,parent,false);
            viewHoler.pic_img= (ImageView) convertView.findViewById(R.id.pic_img);
            viewHoler.checkBox= (CheckBox) convertView.findViewById(R.id.pic_check_box);
            convertView.setTag(viewHoler);
        }
        ViewHoler viewHoler= (ViewHoler) convertView.getTag();
        final Uri uri=uriList.get(position);
        Picasso.with(context).load(uri).fit().centerCrop().placeholder(R.drawable.holder).memoryPolicy(MemoryPolicy.NO_STORE, MemoryPolicy.NO_CACHE).tag("picimg" + position).config(Bitmap.Config.RGB_565).into(viewHoler.pic_img);
        NumberCheckView numberCheckView= (NumberCheckView) viewHoler.checkBox;
        if(choiceUriList.contains(uri))
        {
            numberCheckView.setNumber(choiceUriList.indexOf(uri)+1);
            viewHoler.checkBox.setChecked(true);
        }
        else
        {
            viewHoler.checkBox.setChecked(false);
        }
        return convertView;
    }



    public class ViewHoler
    {
        public ImageView pic_img;
        public CheckBox checkBox;
    }

    public List<Uri> getUriList() {
        return uriList;
    }

    public void setUriList(List<Uri> uriList) {
        this.uriList = uriList;
    }

    public List<Uri> getChoiceUriList() {
        return choiceUriList;
    }

    public void setChoiceUriList(List<Uri> choiceUriList) {
        this.choiceUriList = choiceUriList;
    }
}
