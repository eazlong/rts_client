package com.xrrjkj.translator.cloud.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.xrrjkj.translator.cloud.R;
import com.xrrjkj.translator.cloud.bean.ChatInfo;
import com.xrrjkj.translator.cloud.bean.RoomInfo;

import java.util.List;
import java.util.Random;

public class RoomListAdapter extends BaseAdapter {

    private List<RoomInfo> data;

    private Context mContext;
    private int [] imgs ={R.mipmap.head1, R.mipmap.head2, R.mipmap.head3, R.mipmap.head4, R.mipmap.head5, R.mipmap.head6, R.mipmap.head7, R.mipmap.head8};
    public RoomListAdapter(List<RoomInfo> items, Context context) {
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
            convertView = inflater.inflate(R.layout.list_room_item, null);
            holder = new ViewHolder();
            holder.tv_room =(TextView)convertView.findViewById(R.id.tv_room_id);
            holder.tv_datetime =(TextView)convertView.findViewById(R.id.tv_datetime);
            holder.tv_num =(TextView)convertView.findViewById(R.id.tv_num);
            holder.iv_head =(ImageView) convertView.findViewById(R.id.iv_head);
            convertView.setFocusable(false);
            convertView.setTag(holder);
        }
        holder = (ViewHolder) convertView.getTag();
        holder.tv_room.setText("ID："+data.get(position).getRoomId());
        holder.tv_datetime.setText(data.get(position).getDatetime().substring(0,10).replace(":","-"));
        holder.tv_num.setText(String.valueOf(data.get(position).getPepNum()));
        int index = new Random().nextInt(8);
        holder.iv_head.setImageResource(imgs[index]);
        return convertView;
    }

    class ViewHolder {
        public TextView tv_room, tv_datetime, tv_num;
        public ImageView iv_head;
    }
}
