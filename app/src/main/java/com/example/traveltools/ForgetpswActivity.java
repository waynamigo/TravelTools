package com.example.traveltools;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.dx.dxloadingbutton.lib.*;
import com.example.traveltools.collector.ActivityCollector;
import com.example.traveltools.constant.APPConfig;
import com.squareup.okhttp.Call;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.FormEncodingBuilder;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import java.io.IOException;

import static com.example.traveltools.LoginActivity.user;


public class ForgetpswActivity extends Activity implements View.OnClickListener{
    Button ps_sure,sendconfirm;
    ImageView back;
    EditText newpswd,againpswd,cfmcode;
    String newpsw,againpsw,cfm;
    private int i = 60;//倒计时

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgetpsw);
        ps_sure=(Button) findViewById(R.id.ps_sure1);
        back=(ImageView)findViewById(R.id.rps_back2);
        sendconfirm=(Button)findViewById(R.id.btn_yanzhengma);
        newpswd=(EditText)findViewById(R.id.newpassword1);
        againpswd=(EditText)findViewById(R.id.newpsaaword_r1);
        cfmcode=(EditText)findViewById(R.id.yanzhengma);
        ps_sure.setOnClickListener(this);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }
    Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            if (msg.what == -1) {
                sendconfirm.setText(i + " s");
            } else if (msg.what == -2) {
                sendconfirm.setText("重新发送");
                sendconfirm.setClickable(true);
                i = 60;
            } else {
                int event = msg.arg1;
                int result = msg.arg2;
                Object data = msg.obj;
                Log.e("asd", "event=" + event + "  result=" + result + "  ---> result=-1 success , result=0 error");
            }
        }
    };
    private void getprove(String phonenum){
        OkHttpClient mOkHttpClient = new OkHttpClient();
        FormEncodingBuilder builder = new FormEncodingBuilder();
        builder.add("phonenumber",phonenum);
        final Request request = new Request.Builder()
                .url(APPConfig.SENDSMS)
                .post(builder.build())
                .build();
        Call call = mOkHttpClient.newCall(request);
        call.enqueue(new Callback() {

            @Override
            public void onFailure(Request request, IOException e) {
                Log.e("err:",e.toString());
            }

            @Override
            public void onResponse(Response response) throws IOException {
                System.out.println("response body:"+response.body().string());

            }
        });

    }
    private void changePsw(String phone,String newpass,String cfmcode){
        OkHttpClient mOkHttpClient = new OkHttpClient();
        FormEncodingBuilder builder = new FormEncodingBuilder();
        builder.add("phonenumber",phone);
        builder.add("provenum",cfmcode);
        builder.add("password",newpass);

        final Request request = new Request.Builder()
                .url(APPConfig.RESET_PSW)
                .post(builder.build())
                .build();
        Call call = mOkHttpClient.newCall(request);
        call.enqueue(new Callback() {

            @Override
            public void onFailure(Request request, IOException e) {

            }

            @Override
            public void onResponse(Response response) throws IOException {
                String str =response.body().string();
                Log.e("success :",str);
            }
        });
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        ActivityCollector.removeActivity(this);
    }

    @Override
    public void onClick(View v) {
        newpsw=newpswd.getText().toString().trim();
        againpsw=againpswd.getText().toString().trim();
        cfm = cfmcode.getText().toString().trim();
        switch (v.getId()) {
            case R.id.ps_sure1:
                //发送验证码之前的判断
                if (TextUtils.isEmpty(cfm)) {
                    Toast.makeText(getApplicationContext(), "验证码不能为空",
                            Toast.LENGTH_SHORT).show();
                    return;
                }
                if (TextUtils.isEmpty(newpsw) ||TextUtils.isEmpty(againpsw)) {
                    Toast.makeText(getApplicationContext(), "密码不能为空",
                            Toast.LENGTH_SHORT).show();
                    return;
                }
                if (!newpsw.equals(againpsw)) {
                    Toast.makeText(getApplicationContext(), "密码不一致",
                            Toast.LENGTH_SHORT).show();
                    return;
                }
                if (againpsw.length()<6) {
                    Toast.makeText(getApplicationContext(), "密码长度应大于等于6",
                            Toast.LENGTH_SHORT).show();
                    return;
                }
                sendconfirm.setClickable(false);
                changePsw(user.getPhonenumber(),newpsw,cfm);
                Toast.makeText(this, "密码设置成功", Toast.LENGTH_SHORT).show();
                finish();
                break;
            case R.id.btn_yanzhengma:
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        for (; i > 0; i--) {
                            handler.sendEmptyMessage(-1);
                            if (i <= 0) {
                                break;
                            }
                            try {
                                Thread.sleep(1000);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                        handler.sendEmptyMessage(-2);
                    }
                }).start();
                getprove(user.getPhonenumber());
                break;

        }
    }
}
