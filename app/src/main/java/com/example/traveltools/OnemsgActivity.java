package com.example.traveltools;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.traveltools.bean.Weibo;
import com.example.traveltools.collector.ActivityCollector;
import com.example.traveltools.constant.APPConfig;
import com.example.traveltools.control.PictureTool;
import com.squareup.okhttp.Call;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.FormEncodingBuilder;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.MultipartBuilder;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

import org.json.JSONException;
import org.json.JSONObject;
import java.io.File;
import java.io.IOException;
import static com.example.traveltools.LoginActivity.jwt;


public class OnemsgActivity extends Activity {
    private EditText onemsg;
    private ImageView choosepic,back;
    private String UrlPath;
    public static final int CHOOSE_PHOTO = 12;
    private Button publish;
    Weibo weibo;
    private String filename;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_onemsg);
        onemsg =(EditText)findViewById(R.id.oneMessage);
        choosepic =(ImageView)findViewById(R.id.choose1);
        final Dialog dialog = new Dialog(this, R.style.transparentFrameWindowStyle);
        publish =(Button) findViewById(R.id.publishmessage);
        back =(ImageView)findViewById(R.id.release_back);
        choosepic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ContextCompat.checkSelfPermission(OnemsgActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(OnemsgActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                } else {
                    openAlbum();
                }
                dialog.dismiss();

            }
        });
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        publish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Createweibo();
                Toast.makeText(OnemsgActivity.this, "FA♂贴成功", Toast.LENGTH_SHORT).show();
                finish();
            }
        });

    }
    private void Createweibo(){
        OkHttpClient mOkHttpClient = new OkHttpClient();
        FormEncodingBuilder builder = new FormEncodingBuilder();
        builder.add("jwt",jwt);
        final Request request = new Request.Builder()
                .url(APPConfig.WEIBO_CREATE)
                .post(builder.build())
                .build();
        Call call = mOkHttpClient.newCall(request);
        call.enqueue(new Callback() {

            @Override
            public void onFailure(Request request, IOException e) {
                Log.e("err:",e.toString()+"创建失败");
            }
            @Override
            public void onResponse(Response response) throws IOException {
                try{
                    JSONObject jsonObject = new JSONObject(response.body().string());

                    JSONObject jsonObject1 = jsonObject.getJSONObject("data");
                    Log.e("json : data: ",jsonObject1.toString());

                    Integer userId = jsonObject1.getInt("userId");
                    String content =onemsg.getText().toString().trim();
                    String username= jsonObject1.getString("userName");
                    Integer weiboId=jsonObject1.getInt("weiboId");
                    String date =jsonObject1.getString("date");
                    weibo = new Weibo(content,date,userId,username,weiboId);

                    Intent backData = new Intent();
                    if(weibo!=null){
                        backData.putExtra("weibo",weibo);
                        setResult(RESULT_OK,backData);
//                        Toast.makeText(OnemsgActivity.this,"上传成功",Toast.LENGTH_SHORT).show();
                        if(UrlPath!=null){

                            System.out.println(UrlPath);
                            uploadfile(UrlPath);

                        }
                        setContent(weiboId,content);

                        Intent intent = new Intent(OnemsgActivity.this,CommentActivity.class);

                        startActivity(intent);
                    }else{
                        Toast.makeText(OnemsgActivity.this,"帖子不能为空",Toast.LENGTH_LONG).show();
                    }

                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        });
    }
    private void setContent(Integer weiboId,String content){
        OkHttpClient mOkHttpClient = new OkHttpClient();
        FormEncodingBuilder builder = new FormEncodingBuilder();
        builder.add("jwt",jwt);
        builder.add("weiboId",weiboId.toString());
        builder.add("content",content);
        final Request request = new Request.Builder()
                .url(APPConfig.WEIBO_SET_CONTENT)
                .post(builder.build())
                .build();
        Call call = mOkHttpClient.newCall(request);
        call.enqueue(new Callback() {

            @Override
            public void onFailure(Request request, IOException e) {

            }

            @Override
            public void onResponse(Response response) throws IOException {
                    Log.e(" set content "," success ");
                    String str =response.body().string();
                    System.out.println(str);
            }
        });
    }
    private void uploadfile(String filepath){
        File file =new File(filepath);
        if (!file.exists())
        {
            Toast.makeText(OnemsgActivity.this, "文件不存在，请修改文件路径", Toast.LENGTH_SHORT).show();
            return;
        }
        OkHttpClient mOkHttpClient = new OkHttpClient();
        RequestBody body = new MultipartBuilder()
                .addFormDataPart("file",filepath , RequestBody.create(MediaType.parse("media/type"), new File(filepath)))
                .addFormDataPart("jwt",jwt)
                .type(MultipartBuilder.FORM)
                .build();
        final Request request = new Request.Builder()
                .url(APPConfig.FILE_UPLOAD)
                .post(body)
                .build();
        Call call = mOkHttpClient.newCall(request);
        call.enqueue(new Callback() {

            @Override
            public void onFailure(Request request, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Response response) throws IOException {
                Log.e("fileupload:","success");
                String str =response.body().string();
                System.out.println(str);
                try {
                    JSONObject jsonObject = new JSONObject(str);
                    weibo.setFilename(jsonObject.getString("data"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });


    }
    private void openAlbum(){
        Intent intent = new Intent("android.intent.action.GET_CONTENT");
        intent.setType("image/*");
        startActivityForResult(intent, CHOOSE_PHOTO);
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case 1:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    openAlbum();
                } else {
                    Toast.makeText(this, "You denied the permission", Toast.LENGTH_SHORT).show();
                }
                break;
            default:
        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case CHOOSE_PHOTO:
                if (resultCode == RESULT_OK) {
                    PictureTool tool = new PictureTool();

                    UrlPath = tool.getPicFromUri(this,data.getData());

                    choosepic.setImageBitmap(PictureTool.decodeSampledBitmapFromResource(UrlPath,120,120));
                }
                break;

            default:
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ActivityCollector.removeActivity(this);
    }
}
