package com.example.traveltools;

import android.app.Activity;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.traveltools.adapter.AdapterComment;
import com.example.traveltools.bean.Comment;
import com.example.traveltools.bean.Weibo;
import com.example.traveltools.collector.ActivityCollector;
import com.example.traveltools.constant.APPConfig;
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
import java.util.List;
import java.util.Map;
import java.util.logging.Handler;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

import de.hdodenhof.circleimageview.CircleImageView;

import static com.example.traveltools.LoginActivity.jwt;

/**
 * Created by waynamigo on 18-5-27.
 */
public class CommentActivity extends AppCompatActivity {

    private ImageView pic;
    private FloatingActionButton comment;
    private ListView comment_list_view;
    private List<Comment> commentList;
    private AdapterComment adapterComment;
    TextView publish;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment);
        comment_list_view = findViewById(R.id.comment_list);
        pic = findViewById(R.id.searchback2);
        pic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        publish =findViewById(R.id.publishnew);
        publish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(CommentActivity.this,OnemsgActivity.class));
                finish();
            }
        });
        try{
            commentList = new ArrayList<Comment>();
            adapterComment=new AdapterComment(CommentActivity.this,commentList);
            getCommentList();
            comment_list_view.setAdapter(adapterComment);
            comment_list_view.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Intent intent = new Intent(CommentActivity.this, subCommentActivity.class);
                    intent.putExtra("headcomment", commentList.get(position));
                    Log.e("details",commentList.get(position).getContent());
                    startActivity(intent);
                }
            });
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void getCommentList() {
        String jwt1=jwt;
        String url = APPConfig.WEIBO_ALL + "?jwt=" + jwt1;
        OkHttpClient mOkHttpClient = new OkHttpClient();
        adapterComment = new AdapterComment(CommentActivity.this, commentList);
        final Request request = new Request.Builder()
                .url(url)
                .build();
        Call call = mOkHttpClient.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Request request, IOException e) {
            }
            @Override
            public void onResponse(Response response) throws IOException {
                Comment onecomment;
                try {
                    String str = response.body().string();
                    JSONObject jsonObject = new JSONObject(str);
                    JSONArray jsonArray = jsonObject.getJSONArray("data");
                    JSONObject subjson;
                    for (int i = jsonArray.length()-1; i >=0; i--) {
                        onecomment = new Comment();
                        subjson = new JSONObject(jsonArray.get(i).toString());
                        if (subjson.getString("content") != null) {
                            onecomment.setWeiboId(subjson.getInt("weiboId"));
                            onecomment.setUserId(subjson.getInt("userId"));
                            onecomment.setUsername(subjson.getString("userName"));
                            onecomment.setContent(subjson.getString("content"));
                            onecomment.setPublish_time(subjson.getString("date").substring(0,10));
                            adapterComment.addcomment(onecomment);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }
}