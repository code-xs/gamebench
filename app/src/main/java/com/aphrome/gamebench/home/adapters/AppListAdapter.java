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

import java.util.HashMap;
import java.util.ArrayList;

import com.aphrome.gamebench.R;

public class AppListAdapter extends BaseAdapter{
        public ArrayList<HashMap<String, Object>> mList;
        private Context mContext;
        private LayoutInflater mInflater;
        public AppListAdapter(Context context, ArrayList<HashMap<String, Object>> list){         
            mList = list;
            mContext = context;
            mInflater = LayoutInflater.from(context);
        }

        @Override
        public int getCount() {
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
                convertView = mInflater.inflate(R.layout.item_app, null);
                viewHolder = new ViewHolder();  
                viewHolder.mIcon = (ImageView) convertView.findViewById(R.id.item_app_icon);  
                viewHolder.mText = (TextView)convertView.findViewById(R.id.item_app_name);
                convertView.setTag(viewHolder);  
            } else {
                viewHolder = (ViewHolder) convertView.getTag();  
            }

            HashMap<String, Object> map = mList.get(position);
            String text = (String)map.get("name");
            Drawable icon = (Drawable)map.get("icon");
            viewHolder.mIcon.setImageDrawable(icon);
            viewHolder.mText.setText(text);
            return convertView;
        }

        static class ViewHolder {

            public ViewHolder(ImageView icon, TextView text){
                mText = text;
                mIcon = icon;
            }

            public ViewHolder(){

            }

            public TextView mText;
            public ImageView mIcon;
        }
}
