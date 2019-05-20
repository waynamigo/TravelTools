package com.example.traveltools;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.traveltools.collector.ActivityCollector;

import static com.example.traveltools.LoginActivity.user;


public class UserMessageActivity extends AppCompatActivity {
//    private User user;
    private TextView username;
    private TextView userPhonenum;
    private ImageView USERIMAGE;
    private RelativeLayout GoName;
    private Button button;
    public static final int TAKE_PHOTO = 11;
    public static final int CHOOSE_PHOTO = 12;
    private Uri imageUri;
    private String UriPath;//图片路径
    ImageView back;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_message);
        button =(Button)findViewById(R.id.exit_user_message);

        username=findViewById(R.id.name);
        username.setText(user.getName());
        username.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(UserMessageActivity.this,RenameActivity.class));
                finish();
            }
        });
        button.setOnClickListener(new View.OnClickListener() {//注销登陆
            @Override
            public void onClick(View v) {
                startActivity(new Intent(UserMessageActivity.this,LoginActivity.class));
                finish();
            }
        });
        back =findViewById(R.id.msgback);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

}
