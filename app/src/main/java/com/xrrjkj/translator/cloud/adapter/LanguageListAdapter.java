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

public class LanguageListAdapter extends BaseAdapter {

    private String [] data;

    private Context mContext;
    public LanguageListAdapter(String [] items, Context context) {
        this.data =items;
        this.mContext =context;
    }

    @Override
    public int getCount() {
        return data == null? 0: data.length;
    }

    @Override
    public Object getItem(int position) {
        return data == null? null: data.length > position? data[position]: null;
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
            convertView = inflater.inflate(R.layout.list_language_item, null);
            holder = new ViewHolder();
            holder.tv_lan =(TextView)convertView.findViewById(R.id.tv_lan);
            convertView.setFocusable(false);
            convertView.setTag(holder);
        }
        holder = (ViewHolder) convertView.getTag();
        holder.tv_lan.setText(data[position]);
        return convertView;
    }

    class ViewHolder {
        public TextView tv_lan;
    }
}
