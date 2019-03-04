package com.example.traveltools;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.traveltools.constant.APPConfig;
import com.example.traveltools.control.ToastUtil;
import com.squareup.okhttp.Call;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.FormEncodingBuilder;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import org.json.JSONObject;

import java.io.IOException;

import static com.example.traveltools.LoginActivity.jwt;
import static com.example.traveltools.LoginActivity.user;

public class RenameActivity extends AppCompatActivity {
    private Button sure;
    private EditText editText;
    ImageView back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rename);
        sure =findViewById(R.id.ps_sure2);
        editText=findViewById(R.id.newname);
        back=findViewById(R.id.rps_back3);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        sure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resetName();
            }
        });

    }
    private void resetName(){
        String name=editText.getText().toString().trim();
        if(name==""){
            Toast.makeText(this, "用户昵称不能为空", Toast.LENGTH_SHORT).show();

        }else {
            user.setName(name);
            OkHttpClient mOkHttpClient = new OkHttpClient();
            FormEncodingBuilder builder = new FormEncodingBuilder();
            builder.add("jwt", jwt);
            builder.add("userName", name);
            try {
                final Request request = new Request.Builder()
                        .url(APPConfig.SIGN_RENAME)
                        .put(builder.build())
                        .build();
                Call call = mOkHttpClient.newCall(request);
                call.enqueue(new Callback() {
                    @Override
                    public void onFailure(Request request, IOException e) {
                        Log.e("err:", e.toString());
                    }
                    @Override
                    public void onResponse(Response response) throws IOException {
                        Log.e("succ:", "sign in success");
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                ToastUtil.show(RenameActivity.this, "修改成功");
                            }
                        });
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
            startActivity(new Intent(RenameActivity.this,UserMessageActivity.class));
            finish();
        }

    }
}
