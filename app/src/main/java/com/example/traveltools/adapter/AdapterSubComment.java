package com.example.traveltools.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.traveltools.R;
import com.example.traveltools.bean.Comment;
import com.example.traveltools.bean.SubComment;

import java.util.List;

public class AdapterSubComment extends BaseAdapter {
    Context context;
    List<SubComment> subCommentList;

    public AdapterSubComment(Context c, List<SubComment> data){
        this.context = c;
        this.subCommentList = data;
    }

    @Override
    public int getCount() {
        return subCommentList.size();
    }

    @Override
    public Object getItem(int i) {
        return subCommentList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View convertView, ViewGroup viewGroup) {
        AdapterSubComment.ViewHolder2 holder;
        // 重用convertView
        if(convertView == null){
            holder = new AdapterSubComment.ViewHolder2();
            convertView = LayoutInflater.from(context).inflate(R.layout.subcomment_item, null);
            holder.subcomment_name = (TextView) convertView.findViewById(R.id.cm_second_nick);
            holder.subcomment_content = (TextView) convertView.findViewById(R.id.cm_second_content);

            convertView.setTag(holder);
        }else{
            holder = (AdapterSubComment.ViewHolder2) convertView.getTag();
        }
        // 适配数据
        if(subCommentList.get(i).getContent()==null){
            return convertView;
        }else{
            holder.subcomment_name.setText(subCommentList.get(i).getUsername());
            holder.subcomment_content.setText(subCommentList.get(i).getContent());
        }
        return convertView;
    }

    /**
     * 添加一条评论,刷新列表
     * @param subComment
     */
    public void addsubComment(SubComment subComment){
        subCommentList.add(subComment);
    }

    /**
     * 静态类，便于GC回收
     */
    public static class ViewHolder2{
        TextView subcomment_name;
        TextView subcomment_content;
    }
}
