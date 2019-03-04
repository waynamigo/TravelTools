package com.example.traveltools.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.traveltools.bean.Comment;
import com.example.traveltools.R;

import org.w3c.dom.Text;

import java.util.List;

/**
 * Created by waynamigo on 18-8-7.
 */

public class AdapterComment extends BaseAdapter {
    Context context;
    List<Comment> commentList;
    public AdapterComment(Context c, List<Comment> data){
        this.context = c;
        this.commentList = data;
    }
    @Override
    public int getCount() {
        return commentList.size();
    }

    @Override
    public Object getItem(int i) {
        return commentList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View convertView, ViewGroup viewGroup) {
        ViewHolder holder;
        if(convertView == null){
            holder = new ViewHolder();
            convertView = LayoutInflater.from(context).inflate(R.layout.timeline_item_layout, null);
            holder.comment_name = (TextView) convertView.findViewById(R.id.tl_nickname);
            holder.comment_content = (TextView) convertView.findViewById(R.id.tl_content);
            holder.cm_publishtime =(TextView)convertView.findViewById(R.id.tl_date);
            convertView.setTag(holder);
        }else{
            holder = (ViewHolder) convertView.getTag();
        }
        if(commentList.get(i).getUsername()==null){
            return convertView;
        }else{
            holder.comment_name.setText(commentList.get(i).getUsername());
            holder.comment_content.setText(commentList.get(i).getContent());
            holder.cm_publishtime.setText(commentList.get(i).getPublish_time());
        }
        return convertView;
    }
    /**
     * 静态类，便于GC回收
     */
    public void addcomment(Comment onecomment){
        commentList.add(onecomment);
    }
    public static class ViewHolder{
        TextView comment_name;
        TextView comment_content;
        TextView comment_count;
        TextView cm_publishtime;
    }
}