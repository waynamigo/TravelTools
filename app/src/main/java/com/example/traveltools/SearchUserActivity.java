package com.example.traveltools;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.example.traveltools.adapter.AdapterComment;
import com.example.traveltools.bean.Comment;
import com.example.traveltools.bean.User;
import com.example.traveltools.constant.APPConfig;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.squareup.okhttp.Call;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.FormEncodingBuilder;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static com.example.traveltools.LoginActivity.jwt;

public class SearchUserActivity extends AppCompatActivity {
    private ListView resultlist_view;
    private List<Comment> resultList;
    private AdapterComment adapterComment;
    String keyword;
    ImageView back;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_user);
        resultlist_view =findViewById(R.id.resultuser_list);
        Intent intent=getIntent();
        keyword=intent.getStringExtra("keyword");
        resultList =new ArrayList<Comment>();
        getResultUser(keyword);
        adapterComment=new AdapterComment(SearchUserActivity.this,resultList);
        resultlist_view.setAdapter(adapterComment);
        back =findViewById(R.id.searchback3);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
    private void getResultUser(String keyword){
        OkHttpClient mOkHttpClient = new OkHttpClient();
        FormEncodingBuilder builder = new FormEncodingBuilder();
        String get ="http://jd.han777.win:8086/weibo/search/userName/?jwt="+jwt+"&keyword="+keyword;
        final Request request = new Request.Builder()
                .url(get)
                .build();
        Call call = mOkHttpClient.newCall(request);
        call.enqueue(new Callback() {

            @Override
            public void onFailure(Request request, IOException e) {
                Log.e("err:",e.toString());
            }

            @Override
            public void onResponse(Response response) throws IOException {

                String jsonarray;
                Gson gson =new Gson();
                Comment onecomment;
                try {
                    String str = response.body().string();
                    JSONObject jsonObject = new JSONObject(str);


                    JSONArray jsonArray = jsonObject.getJSONArray("data");
                    for (int i = jsonArray.length()-1; i >=0; i--) {
                        onecomment = new Comment();
                        jsonarray =jsonArray.get(i).toString();
                        String []strarray =jsonarray.split(",");
                        if(strarray[3].equals("null")){
                            continue;
                        }else{
                            onecomment.setUsername(strarray[2].substring(1,strarray[2].length()-2));
                            onecomment.setContent(strarray[3]);
                            resultList.add(onecomment);
                        }


                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }
}
