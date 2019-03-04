package com.example.traveltools;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.traveltools.adapter.AdapterSubComment;
import com.example.traveltools.bean.Comment;
import com.example.traveltools.bean.SubComment;
import com.example.traveltools.constant.APPConfig;
import com.google.gson.JsonObject;
import com.squareup.okhttp.Call;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.FormEncodingBuilder;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static com.example.traveltools.LoginActivity.jwt;

public class subCommentActivity extends AppCompatActivity {
    private SubComment subComment;
    private Button cmsend;
    private EditText cmedit;
    private AdapterSubComment adapterSubComment;
    private List<SubComment> subCommentList;
    private TextView subname;
    private TextView cmcomment;
    private ListView subcom_listview;
    Comment thiscomment;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sub_comment);
        cmsend=findViewById(R.id.cm_send);
        cmedit=findViewById(R.id.cm_edit);
        subCommentList =new ArrayList<SubComment>();
        thiscomment =new Comment();
        try{
            final Intent intent= getIntent();
            thiscomment = (Comment) intent.getSerializableExtra("headcomment");
            subname=findViewById(R.id.sub_cm_nickname);
            cmcomment=findViewById(R.id.cm_comment);
            subname.setText(thiscomment.getUsername());
            cmcomment.setText(thiscomment.getContent());
            subcom_listview=findViewById(R.id.subcomment_list);


            adapterSubComment = new AdapterSubComment(subCommentActivity.this,subCommentList);
            getallsubComment(thiscomment.getWeiboId());
            subcom_listview.setAdapter(adapterSubComment);

            cmsend.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String content = cmedit.getText().toString().trim();
                    if(content.equals("")){
                        Toast.makeText(subCommentActivity.this, "评论内容不能为空", Toast.LENGTH_SHORT).show();
                    }else{
                        publishsubComment(thiscomment.getWeiboId(),content);
                        Toast.makeText(subCommentActivity.this, "评论成功", Toast.LENGTH_SHORT).show();
                    }

                }
            });
        }catch (Exception e){
            e.printStackTrace();
        }

    }
    private void publishsubComment(Integer weiboId,String content){
        OkHttpClient okHttpClient= new OkHttpClient();
        FormEncodingBuilder builder = new FormEncodingBuilder();
        builder.add("jwt",jwt);
        builder.add("weiboId",weiboId.toString());
        builder.add("content",content);

        final Request request = new Request.Builder()
                .url(APPConfig.ADD_COMMENT)
                .post(builder.build())
                .build();
        Call call = okHttpClient.newCall(request);
        call.enqueue(new Callback() {

            @Override
            public void onFailure(Request request, IOException e) {
                Log.e("tag","request failed");
            }

            @Override
            public void onResponse(Response response) throws IOException {
                Log.e("tag",response.body().string());
            }
        });
    }
    private void getallsubComment(Integer weiboId){
        OkHttpClient okHttpClient= new OkHttpClient();
        String jwt1=jwt;
        FormEncodingBuilder builder = new FormEncodingBuilder();
        String subcmturl ="http://jd.han777.win:8086/comment/getall/?jwt="+jwt1+"&weiboId="+weiboId;

        final Request request = new Request.Builder()
                .url(subcmturl)
                .build();
        Call call = okHttpClient.newCall(request);
        call.enqueue(new Callback() {

            @Override
            public void onFailure(Request request, IOException e) {
                Log.e("tag","request failed");
            }
            @Override
            public void onResponse(Response response) throws IOException {
                try{
                    String str =response.body().string();
                    Log.e("body",str);
                    JSONObject jsonObject = new JSONObject(str);
                    JSONArray jsonArray = jsonObject.getJSONArray("data");
                    JSONObject subjson;
                    for (int i = 0; i < jsonArray.length(); i++) {
                        subComment =new SubComment();
                        subjson = new JSONObject(jsonArray.get(i).toString());
                        if (subjson.getString("content")!=null) {
                            subComment.setUserId(subjson.getInt("userId"));
                            subComment.setUsername(subjson.getString("userName"));
                            subComment.setOwnerId(subjson.getInt("ownerId"));
                            subComment.setWieboId(subjson.getInt("weiboId"));
                            subComment.setContent(subjson.getString("content"));
                            Log.e("output",subComment.getContent());
                            adapterSubComment.addsubComment(subComment);
                        }
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        });
    }
}
