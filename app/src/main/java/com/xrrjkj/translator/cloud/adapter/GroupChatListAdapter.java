package com.xrrjkj.translator.cloud.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.xrrjkj.translator.cloud.R;
import com.xrrjkj.translator.cloud.bean.ChatInfo;

import java.util.List;
import java.util.Random;

public class GroupChatListAdapter extends BaseAdapter {

    private List<ChatInfo> data;

    private Context mContext;
    private int [] imgs ={R.mipmap.head1, R.mipmap.head2, R.mipmap.head3, R.mipmap.head4, R.mipmap.head5, R.mipmap.head6, R.mipmap.head7, R.mipmap.head8};
    public GroupChatListAdapter(List<ChatInfo> items, Context context) {
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
            convertView = inflater.inflate(R.layout.list_group_chat_item, null);
            holder = new ViewHolder();
            holder.tv_content =(TextView)convertView.findViewById(R.id.tv_content);
            holder.iv_head = (ImageView)convertView.findViewById(R.id.iv_head);
            convertView.setFocusable(false);
            convertView.setTag(holder);
        }
        holder = (ViewHolder) convertView.getTag();
        holder.tv_content.setText(data.get(position).getContent());
        holder.iv_head.setImageResource(imgs[data.get(position).getType()]);
        return convertView;
    }

    class ViewHolder {
        public TextView tv_content;
        public ImageView iv_head;
    }
}
