package com.aphrome.gamebench.home.adapters;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.view.LayoutInflater;

import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.BaseAdapter;
import android.graphics.drawable.Drawable;
import android.util.Log;
import java.util.HashMap;
import java.util.ArrayList;

import com.aphrome.gamebench.R;

public class LinkIntroduceAdapter extends BaseAdapter{
    public ArrayList<LinkIntroduceInfo> mList;
    private Context mContext;
    private LayoutInflater mInflater;
    private static String TAG = "LinkIntroduceAdapter";
    public LinkIntroduceAdapter(Context context, ArrayList<LinkIntroduceInfo> list){         
        mList = list;
        mContext = context;
        mInflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        Log.d(TAG, "getCount:"+mList.size());
        return mList.size();
    }

    @Override
    public Object getItem(int position) {
        return mList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup viewGroup) {
        ViewHolder viewHolder = null;
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.content_list_item, null);
            viewHolder = new ViewHolder();
            viewHolder.mLinkIconImageView = (ImageView) convertView.findViewById(R.id.device_icon);
            viewHolder.mLinkTypeTextView = (TextView)convertView.findViewById(R.id.device_type);
            viewHolder.mArrowImageView = (ImageView)convertView.findViewById(R.id.show_arrow);
            viewHolder.mLinearLayout = (View)convertView.findViewById(R.id.link_introduce);      
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        Log.d(TAG, "getView"+convertView);
        LinkIntroduceInfo info = mList.get(position);
        String text = (String)info.mLinkType;
        Drawable icon = (Drawable)info.mLinkIcon;
        Drawable arrow = (Drawable)info.mArrow;
        viewHolder.mArrowImageView.setImageDrawable(arrow);
        viewHolder.mLinkIconImageView.setImageDrawable(icon);
        viewHolder.mLinkTypeTextView.setText(info.mLinkType);
        TextView text0 = (TextView)viewHolder.mLinearLayout.findViewById(R.id.bluetooth);
        text0.setText(mContext.getResources().getString(R.string.bluetooth_link_method));
        TextView text1 = (TextView)viewHolder.mLinearLayout.findViewById(R.id.bluetooth_introduce);
        text1.setText(mContext.getResources().getString(R.string.bluetooth_link_details));
        TextView text2 = (TextView)viewHolder.mLinearLayout.findViewById(R.id.wireless);
        text2.setText(mContext.getResources().getString(R.string.wired_link_method));
        TextView text3 = (TextView)viewHolder.mLinearLayout.findViewById(R.id.wireless_introduce);
        text3.setText(mContext.getResources().getString(R.string.wireless_link_details));
        TextView text4 = (TextView)viewHolder.mLinearLayout.findViewById(R.id.wired);
        text4.setText(mContext.getResources().getString(R.string.wired_link_method));
        TextView text5 = (TextView)viewHolder.mLinearLayout.findViewById(R.id.wired_introduce);
        text5.setText(mContext.getResources().getString(R.string.wired_link_details));              
        return convertView;
    }

    static class ViewHolder {
        public TextView mLinkTypeTextView;
        public ImageView mLinkIconImageView;
        public ImageView mArrowImageView;
        public View mLinearLayout;
        public ViewHolder(TextView type, ImageView icon, ImageView arrow){
            mLinkTypeTextView = type;
            mLinkIconImageView = icon;
            mArrowImageView = arrow;
        }

        public ViewHolder(){
        }
    }

    public static class LinkIntroduceInfo{
        public String mLinkType;
        public Drawable mLinkIcon;
        public Drawable mArrow;

        public LinkIntroduceInfo(String type, Drawable icon, Drawable arrow){
            mLinkType = type;
            mLinkIcon = icon;
            mArrow = arrow;
        }
    }
}
