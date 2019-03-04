package com.example.traveltools;

import android.app.Activity;
import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;


import com.example.traveltools.adapter.AdapterComment;
import com.example.traveltools.adapter.AdapterVRPic;
import com.example.traveltools.bean.VRpicture;
import com.example.traveltools.collector.ActivityCollector;
import com.zph.glpanorama.GLPanorama;

import java.util.ArrayList;
import java.util.List;

public class GlobalActivity extends Activity {
    ListView vrbox;
    AdapterVRPic adapterVRPic;
    ImageView backtomain;
    int id[] = { R.drawable.daminghu,R.drawable.haibinsea,R.drawable.huiquansqare,R.drawable.huiquanwan,R.drawable.kontemp,R.drawable.luxunpark,
            R.drawable.penglaige,R.drawable.qianfoshan,R.drawable.zhanqiao};
    String names[]={"大明湖","海滨城","汇泉广场","汇泉湾","孔庙","鲁迅公园","蓬莱阁","千佛山","栈桥"};
    private List<VRpicture> Vrpics;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_global);
        backtomain= (ImageView)findViewById(R.id.globalback);
        Vrpics =new ArrayList<VRpicture>();
        VRpicture vr;
        for(int i=0;i<id.length;i++){
            vr=new VRpicture();
            vr.setDrawableid(id[i]);
            vr.setPlacename(names[i]);
            Vrpics.add(vr);
        }
        vrbox =(ListView)findViewById(R.id.vrbox);

        adapterVRPic = new AdapterVRPic(GlobalActivity.this,Vrpics);
        vrbox.setAdapter(adapterVRPic);
        try{
            vrbox.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Intent intent=new Intent(GlobalActivity.this,VRActivity.class);
                    intent.putExtra("which",position);
                    startActivity(intent);
                }
            });
        }catch (Exception e){
            e.printStackTrace();
        }

        backtomain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        ActivityCollector.removeActivity(this);
    }

}
