package com.example.traveltools;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.traveltools.collector.ActivityCollector;
import com.example.traveltools.constant.APPConfig;
import com.squareup.okhttp.Call;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.FormEncodingBuilder;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import java.io.IOException;



/**
 * Created by waynamigo on 18-5-27.
 */
public class RegisterActivity extends AppCompatActivity implements View.OnClickListener {
    private Button btnSendMsg, btnSubmitCode;
    private EditText etPhoneNumber, etCode,password,againPass;
    Toolbar toolbar;
    ImageView back;
    private int i = 60;//倒计时
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        toolbar = (Toolbar)findViewById(R.id.registerToolbar);
        back=(ImageView)findViewById(R.id.backtologin);
        etPhoneNumber = (EditText) findViewById(R.id.etPhoneNumber);
        etCode = (EditText) findViewById(R.id.etCode);
        btnSendMsg = (Button) findViewById(R.id.btnSendMsg);
        btnSubmitCode = (Button) findViewById(R.id.btnSubmitCode);
        password = (EditText)findViewById(R.id.mima);
        againPass = (EditText)findViewById(R.id.againassword);
//        EventHandler eventHandler = new EventHandler() {
//            @Override
//            public void afterEvent(int event, int result, Object data) {
//                Message msg = new Message();
//                msg.arg1 = event;
//                msg.arg2 = result;
//                msg.obj = data;
//                handler.sendMessage(msg);
//            }
//        };
        //注册回调监听接口
       //
        back.setOnClickListener(this);
        btnSendMsg.setOnClickListener(this);
        btnSubmitCode.setOnClickListener(this);
    }
    //发送验证码之后
    Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            if (msg.what == -1) {
                btnSendMsg.setText(i + " s");
            } else if (msg.what == -2) {
                btnSendMsg.setText("重新发送");
                btnSendMsg.setClickable(true);
                i = 60;
            } else {
                int event = msg.arg1;
                int result = msg.arg2;
                Object data = msg.obj;
                Log.e("asd", "event=" + event + "  result=" + result + "  ---> result=-1 success , result=0 error");
//                if (result == SMSSDK.RESULT_COMPLETE) {
//                    // 短信注册成功后，返回MainActivity,然后提示
//                    if (event == SMSSDK.EVENT_SUBMIT_VERIFICATION_CODE) {//验证成功
//                       //手机号存在  结束任务
//                        //返回到空text
//
//                    } else if (event == SMSSDK.EVENT_GET_VERIFICATION_CODE) {
//                        Toast.makeText(getApplicationContext(), "验证码已经发送",
//                                Toast.LENGTH_SHORT).show();
//                    } else {
//                        ((Throwable) data).printStackTrace();
//                    }
//                } else if (result == SMSSDK.RESULT_ERROR) {
//                    try {
//                        Throwable throwable = (Throwable) data;
//                        throwable.printStackTrace();
//                        JSONObject object = new JSONObject(throwable.getMessage());
//                        String des = object.optString("detail");//错误描述
//                        int status = object.optInt("status");//错误代码
//                        if (status > 0 && !TextUtils.isEmpty(des)) {
//                            Log.e("asd", "des: " + des);
//                            Toast.makeText(RegisterActivity.this, des, Toast.LENGTH_SHORT).show();
//                            return;
//                        }
//                    } catch (Exception e) {
//                        Toast.makeText(getApplicationContext(), e.toString(),
//                                Toast.LENGTH_SHORT).show();
//                    }
//                }
            }
        }
    };

    @Override
    public void onClick(View v) {
        String phoneNum = etPhoneNumber.getText().toString().trim();
        String pass = password.getText().toString().trim();
        String againPassword = againPass.getText().toString().trim();
        String etcode = etCode.getText().toString().trim();
        switch (v.getId()) {
            case R.id.btnSendMsg:
                //发送验证码之前的判断
                if (TextUtils.isEmpty(phoneNum)) {
                    Toast.makeText(getApplicationContext(), "手机号码不能为空",
                            Toast.LENGTH_SHORT).show();
                    return;
                }
                if (TextUtils.isEmpty(pass) ||TextUtils.isEmpty(againPassword)) {
                    Toast.makeText(getApplicationContext(), "密码不能为空",
                            Toast.LENGTH_SHORT).show();
                    return;
                }
                if (!pass.equals(againPassword)) {
                    Toast.makeText(getApplicationContext(), "密码不一致",
                            Toast.LENGTH_SHORT).show();
                    return;
                }
                if (pass.length()<6) {
                    Toast.makeText(getApplicationContext(), "密码长度应大于等于6",
                            Toast.LENGTH_SHORT).show();
                    return;
                }
                //send sms
                getprove(phoneNum);
                btnSendMsg.setClickable(false);
                //开始倒计时
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
                break;
            case R.id.btnSubmitCode:
                String code = etCode.getText().toString().trim();
                if (TextUtils.isEmpty(phoneNum)) {
                    Toast.makeText(getApplicationContext(), "手机号码不能为空",
                            Toast.LENGTH_SHORT).show();
                    return;
                }
                if (TextUtils.isEmpty(code)) {
                    Toast.makeText(getApplicationContext(), "验证码不能为空",
                            Toast.LENGTH_SHORT).show();
                    return;
                }
                signup(phoneNum,pass,code);
                Intent intent=new Intent(RegisterActivity.this,LoginActivity.class);
                startActivity(intent);
                finish();
                break;
            case R.id.backtologin:
//                Intent intent1 =new Intent(RegisterActivity.this,LoginActivity.class);
//                startActivity(intent1);
                finish();
                break;
            default:
                break;
        }
    }

    private void signup(String phonenum,String password,String provenum){
        OkHttpClient mOkHttpClient = new OkHttpClient();
        FormEncodingBuilder builder = new FormEncodingBuilder();
        String name ="新用户";
        builder.add("phonenumber",phonenum);
        builder.add("provenum",provenum);
        builder.add("password",password);
        builder.add("userName","defaultname");
        final Request request = new Request.Builder()
                .url(APPConfig.SIGN_UP)
                .post(builder.build())
                .build();
        Call call = mOkHttpClient.newCall(request);
        call.enqueue(new Callback() {

            @Override
            public void onFailure(Request request, IOException e) {
                Log.e("err:",e.toString());
                Toast.makeText(RegisterActivity.this, "由于未知错误，注册失败", Toast.LENGTH_SHORT).show();

            }

            @Override
            public void onResponse(Response response) throws IOException {
                String res =response.body().string();
                Log.e("succ",res);
            }
        });
    }
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
    @Override
    protected void onDestroy() {
        super.onDestroy();
        // 销毁回调监听接口
       // SMSSDK.unregisterAllEventHandler();
        ActivityCollector.removeActivity(this);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
