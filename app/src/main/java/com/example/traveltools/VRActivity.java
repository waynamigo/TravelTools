package com.example.traveltools;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.traveltools.collector.ActivityCollector;
import com.example.traveltools.constant.APPConfig;
import com.squareup.okhttp.Call;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;
import com.zph.glpanorama.GLPanorama;

import java.io.File;
import java.io.IOException;

import static com.example.traveltools.LoginActivity.jwt;

public class VRActivity extends AppCompatActivity{

    private GLPanorama mGLPanorama;
    private ImageView back;
    int id[] = { R.drawable.daminghu,R.drawable.haibinsea,R.drawable.huiquansqare,R.drawable.huiquanwan,R.drawable.kontemp,R.drawable.luxunpark,
            R.drawable.penglaige,R.drawable.qianfoshan,R.drawable.zhanqiao};
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vr);
        try{
            Intent intent = getIntent();
            int which = intent.getIntExtra("which",R.drawable.imgwall);
            System.out.println(which);
            mGLPanorama = (GLPanorama) findViewById(R.id.mGLPanorama);
            //传入你的全景图
            mGLPanorama.setGLPanorama(id[which]);
            back=(ImageView)findViewById(R.id.vrback);
            back.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    finish();
                }
            });
        }catch(Exception e){
            e.printStackTrace();
        }
    }
    private void setFilepath(String vrfileurl){
        OkHttpClient mOkHttpClient = new OkHttpClient();
        final Request request = new Request.Builder()
                .url(vrfileurl)
                .build();
        Call call = mOkHttpClient.newCall(request);
        call.enqueue(new Callback() {

            @Override
            public void onFailure(Request request, IOException e) {

            }

            @Override
            public void onResponse(Response response) throws IOException {

            }
        });

    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        ActivityCollector.removeActivity(this);
    }
}
