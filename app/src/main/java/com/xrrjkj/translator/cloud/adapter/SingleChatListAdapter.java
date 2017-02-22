package com.xrrjkj.translator.cloud.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.xrrjkj.translator.cloud.R;
import com.xrrjkj.translator.cloud.bean.ChatInfo;

import java.util.List;

public class SingleChatListAdapter extends BaseAdapter {

    private List<ChatInfo> data;

    private Context mContext;
    public SingleChatListAdapter(List<ChatInfo> items, Context context) {
        this.data =items;
        this.mContext =context;
    }

    @Override
    public int getCount() {
        return data == null? 0: data.size();
    }

    @Override
    public Object getItem(int position) {
        return data == null? null: data.size() > position? data.get(position): null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder=null;

        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(parent.getContext());
            convertView = inflater.inflate(R.layout.list_single_chat_item, null);
            holder = new ViewHolder();
            holder.tv_content_left =(TextView)convertView.findViewById(R.id.tv_content_left);
            holder.tv_content_right =(TextView)convertView.findViewById(R.id.tv_content_right);
            holder.llyt_left = (LinearLayout)convertView.findViewById(R.id.llyt_left);
            holder.llyt_right = (LinearLayout)convertView.findViewById(R.id.llyt_right);
            convertView.setFocusable(false);
            convertView.setTag(holder);
        }
        holder = (ViewHolder) convertView.getTag();
        if(data.get(position).getType() ==1){
            holder.tv_content_left.setText(data.get(position).getContent());
            holder.llyt_left.setVisibility(View.VISIBLE);
            holder.llyt_right.setVisibility(View.GONE);
        }else if(data.get(position).getType() ==2){
            holder.tv_content_right.setText(data.get(position).getContent());
            holder.llyt_left.setVisibility(View.GONE);
            holder.llyt_right.setVisibility(View.VISIBLE);
        }
        return convertView;
    }

    class ViewHolder {
        public TextView tv_content_left, tv_content_right;
        public LinearLayout llyt_left, llyt_right;
    }
}
