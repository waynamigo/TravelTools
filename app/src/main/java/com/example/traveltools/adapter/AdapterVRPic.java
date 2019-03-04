package com.example.traveltools.adapter;

import android.content.Context;
import android.media.Image;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.traveltools.R;
import com.example.traveltools.bean.VRpicture;

import java.util.List;
import java.util.Map;

public class AdapterVRPic extends BaseAdapter{
    Context context;
    List<VRpicture> vRpictures;

    public AdapterVRPic(Context context, List<VRpicture> vRpictures) {
        this.context = context;
        this.vRpictures = vRpictures;
        System.out.println(vRpictures.get(1).getPlacename());
    }

    @Override
    public int getCount() {
        return vRpictures.size();
    }

    @Override
    public Object getItem(int position) {
        return vRpictures.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder1 holder;
        if(convertView == null){
            holder = new ViewHolder1();
            convertView = LayoutInflater.from(context).inflate(R.layout.vrpic_item, null);
            holder.itemImage = (ImageView) convertView.findViewById(R.id.itemImage1);
            holder.itemplace = (TextView) convertView.findViewById(R.id.itemName1);
            convertView.setTag(holder);
        }else{
            holder = (ViewHolder1) convertView.getTag();
        }
        if(vRpictures.get(position).getPlacename()==null){

            return convertView;
        }else{
            holder.itemImage.setImageResource(vRpictures.get(position).getDrawableid());
            holder.itemplace.setText(vRpictures.get(position).getPlacename());
        }
        return convertView;
    }
    public static class ViewHolder1{
        ImageView itemImage;
        TextView itemplace;
    }
}
