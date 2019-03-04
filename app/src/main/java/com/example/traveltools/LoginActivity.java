package com.example.traveltools;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.traveltools.bean.User;
import com.example.traveltools.collector.ActivityCollector;
import com.example.traveltools.constant.APPConfig;
import com.example.traveltools.control.LoadingButton;
import com.google.gson.Gson;
import com.squareup.okhttp.Call;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.FormEncodingBuilder;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import org.json.JSONObject;

import java.io.IOException;

import site.gemus.openingstartanimation.RedYellowBlueDrawStrategy;
import site.gemus.openingstartanimation.OpeningStartAnimation;


/**
 * Created by waynamigo on 18-5-27.
 */
public class LoginActivity extends Activity {
    Button login;
    TextView forget;
    Button signup;
    EditText account;
    EditText password;
    CheckBox remember;
    private SharedPreferences pref;
    private SharedPreferences.Editor  editor;
    Gson gson;
    public static String jwt;
    public static User user;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        OpeningStartAnimation openingStartAnimation = new OpeningStartAnimation.Builder(this)
                .setDrawStategy(new RedYellowBlueDrawStrategy())
                .setAppStatement("开始你的旅程")
                .setAnimationInterval(1000)
                .setAnimationFinishTime(800)
                .create();
        openingStartAnimation.show(this);
        user = new User();
        pref= getSharedPreferences("data", Context.MODE_PRIVATE);
        ActivityCollector.addActivty(this);

        login =(Button)findViewById(R.id.login);
        forget=(TextView)findViewById(R.id.forget);
        signup=(Button)findViewById(R.id.signup);
        account=(EditText)findViewById(R.id.account);
        password=(EditText)findViewById(R.id.password);
        remember=(CheckBox)findViewById(R.id.remember);
        boolean isRemember=pref.getBoolean("remember",false);
        if(isRemember){
            String acc= pref.getString("account","");
            String pass= pref.getString("password","");
            account.setText(acc);
            password.setText(pass);
            user.setPhonenumber(acc);
            user.setPassword(pass);
            Log.e("account:",user.getPhonenumber()+' '+user.getPassword());

            remember.setChecked(true);
        }
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try{
                    String acc = account.getText().toString().trim();
                    String psw = password.getText().toString().trim();
                    if (TextUtils.isEmpty(acc) || TextUtils.isEmpty(psw)) {
                        Toast.makeText(LoginActivity.this, "用户名或密码不能为空",
                                Toast.LENGTH_SHORT).show();
                    } else {
                        account.setText(acc);
                        password.setText(psw);
                        user.setPhonenumber(acc);
                        user.setPassword(psw);
                        user.setName("userName");
                        signin(acc, psw);
                        finish();
                    }
                }catch (Exception e){
                    Toast.makeText(LoginActivity.this, "网络异常", Toast.LENGTH_SHORT).show();
                }


            }
        });
        forget.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent =new Intent(LoginActivity.this,ForgetpswActivity.class);
                startActivity(intent);

            }
        });

        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(LoginActivity.this,RegisterActivity.class);
                startActivity(intent);
            }
        });
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        ActivityCollector.removeActivity(this);
    }
    private void signin(String phonenum,String password){
        OkHttpClient mOkHttpClient = new OkHttpClient();
        FormEncodingBuilder builder = new FormEncodingBuilder();
        builder.add("phonenumber",phonenum);
        builder.add("password",password);
        try{
            final Request request = new Request.Builder()
                    .url(APPConfig.SIGN_IN)
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
                    Log.e("succ:","sign in success");
                    try{
                        JSONObject jsonObject = new JSONObject(response.body().string());
                        jwt = jsonObject.getString("data");
                        final String msg=jsonObject.getString("message");

                        if(msg.equals("success")){
                            editor=pref.edit();
                            editor.putBoolean("remember", true);
                            editor.putString("account", user.getPhonenumber());
                            editor.putString("password", user.getPassword());
                            editor.commit();
                            Intent intent = new Intent(LoginActivity.this,MainActivity.class);
                            startActivity(intent);
                        }else{
                            new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    // TODO Auto-generated method stub
                                    Toast.makeText(LoginActivity.this, msg, Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                        System.out.println(jwt);
                        System.out.println(jsonObject.toString());
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }
            });
        }catch (Exception e){
            Toast.makeText(LoginActivity.this, "网络错误，登录失败", Toast.LENGTH_SHORT).show();
        }

    }
}
