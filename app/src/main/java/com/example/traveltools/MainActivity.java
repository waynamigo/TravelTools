package com.example.traveltools;
import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.media.Image;

import android.os.Message;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.os.Handler.Callback;
import android.widget.Toast;

import android.support.design.*;

import com.example.traveltools.adapter.AdapterVRPic;
import com.example.traveltools.bean.ShareModel;
import com.example.traveltools.bean.User;
import com.example.traveltools.bean.VRpicture;
import com.example.traveltools.collector.ActivityCollector;
import com.example.traveltools.control.SharePopUpWindow;
import com.example.traveltools.control.ShareStorage;
import com.iflytek.cloud.RecognizerResult;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.SpeechUtility;
import com.iflytek.cloud.ui.RecognizerDialog;
import com.iflytek.cloud.ui.RecognizerDialogListener;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.PlatformActionListener;
import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.framework.utils.UIHandler;

import static com.example.traveltools.LoginActivity.user;


/**
 * Created by waynamigo on 18-5-27.
 */
public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,PlatformActionListener,Callback {
    //
//    TextView a,b;
//
//
//    private ViewPager myViewPager;
//    private List<Fragment> list;
//    private TabFragmentPagerAdapter adapter;
    Button voice;
    private TextView search;
    private TextView community;
    private TextView global;
    private TextView positionText;
    private SharePopUpWindow share;
//    private User user;
    private String imageurl ="imageurl";
    private String url ="url";
    private String text ="text";
    private String title ="title";
    private String Path =null;
    private ImageView headpic;
    ImageView clicksearch;
    ListView recommend;
    EditText editText;
    TextView myname;
    int id[] = { R.drawable.daminghu,R.drawable.haibinsea,R.drawable.luxunpark,R.drawable.zhanqiao};
    String names[]={"大明湖","海滨城","鲁迅公园","栈桥"};
    private List<VRpicture> Vrpics;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Vrpics =new ArrayList<VRpicture>();
        VRpicture vr;
        for(int i=0;i<id.length;i++){
            vr=new VRpicture();
            vr.setDrawableid(id[i]);
            vr.setPlacename(names[i]);
            Vrpics.add(vr);
        }
        search = (TextView) findViewById(R.id.search);
        community = (TextView) findViewById(R.id.commmunity);
        global = (TextView) findViewById(R.id.global);

        recommend =findViewById(R.id.recommendvrbox);
        AdapterVRPic adapterVRPic =new AdapterVRPic(MainActivity.this,Vrpics);
        recommend.setAdapter(adapterVRPic);
        recommend.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent=new Intent(MainActivity.this,VRActivity.class);
                intent.putExtra("which",position);
                startActivity(intent);
            }
        });
        clicksearch =findViewById(R.id.clicksearch);
        clicksearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(MainActivity.this,SearchUserActivity.class);
                String keyword =editText.getText().toString().trim();
                if(keyword.equals("")){
                    Toast.makeText(MainActivity.this, "请输入关键字查询内容", Toast.LENGTH_SHORT).show();
                }else {
                    intent.putExtra("keyword",keyword);
                    startActivity(intent);
                }
            }
        });
        editText = (EditText)findViewById(R.id.result);
        voice =(Button)findViewById(R.id.pressvoice);
        ShareSDK.initSDK(this);
        SpeechUtility.createUtility(this, SpeechConstant.APPID +"=5b87dd69");

        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, SearchActivity.class);
                startActivity(intent);
            }
        });
        community.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, CommentActivity.class);
                startActivity(intent);
            }
        });
        global.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, GlobalActivity.class);
                startActivity(intent);
            }
        });
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        View headerView = navigationView.getHeaderView(0);
        headpic = (ImageView) headerView.findViewById(R.id.headpic);
        myname =(TextView)headerView.findViewById(R.id.myName);
        myname.setText(user.getName());
        voice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                btnVoice();
            }
        });
        headpic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(MainActivity.this,UserMessageActivity.class);
                startActivity(intent);

            }
        });
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();


    }
    //TODO 开始说话：
    private void btnVoice(){
        try{
            RecognizerDialog dialog = new RecognizerDialog(MainActivity.this,null);
            dialog.setParameter(SpeechConstant.LANGUAGE, "zh_cn");
            dialog.setParameter(SpeechConstant.ACCENT, "mandarin");
            dialog.setListener(new RecognizerDialogListener() {
                @Override
                public void onResult(RecognizerResult recognizerResult, boolean b) {
                    printResult(recognizerResult);
                }
                @Override
                public void onError(SpeechError speechError) {
                }
            });
            dialog.show();
            Toast.makeText(this, "请开始说话", Toast.LENGTH_SHORT).show();
        }catch (Exception e){
            e.printStackTrace();
        }

    }
    //回调结果：
    private void printResult(RecognizerResult results) {
        String text = parseIatResult(results.getResultString());
        // 自动填写地址
        text=text.substring(0,text.length()-1);
        editText.append(text);

    }
    public static String parseIatResult(String json) {
        StringBuffer ret = new StringBuffer();
        try {
            JSONTokener tokener = new JSONTokener(json);
            JSONObject joResult = new JSONObject(tokener);
            JSONArray words = joResult.getJSONArray("ws");
            for (int i = 0; i < words.length(); i++) {
                // 转写结果词，默认使用第一个结果
                JSONArray items = words.getJSONObject(i).getJSONArray("cw");
                JSONObject obj = items.getJSONObject(0);
                ret.append(obj.getString("w"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ret.toString();
    }

    //    private void InitView() {
//        tv_item_one = (TextView) findViewById(R.id.tv_item_one);
//        tv_item_two = (TextView) findViewById(R.id.tv_item_two);
//        tv_item_three = (TextView) findViewById(R.id.tv_item_three);
//        myViewPager = (ViewPager) findViewById(R.id.myViewPager);
//    }
    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement


        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.myfocus) {
            // Handle the camera action

        } else if (id == R.id.mypublished) {

        } else if (id == R.id.mytravel) {

        } else if (id == R.id.search_global) {

        } else if (id == R.id.nav_share) {
            share = new SharePopUpWindow(MainActivity.this);
            share.setPlatformActionListener(MainActivity.this);
            ShareModel model = new ShareModel();
            model.setImageUrl(imageurl);
            model.setText(text);
            model.setTitle(title);
            model.setUrl(url);
            share.initShareParams(model);
            share.showShareWindow();
            // 显示窗口 设置layout在PopupWindow中显示的位置
            share.showAtLocation(
                    MainActivity.this.findViewById(R.id.drawer_layout),
                    Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ActivityCollector.removeActivity(this);
        ShareSDK.stopSDK(this);
    }

    @Override
    public void onComplete(Platform platform, int i, HashMap<String, Object> hashMap) {
        Message msg = new Message();
        msg.arg1 = 1;
        msg.arg2 = i;
        msg.obj = platform;
        UIHandler.sendMessage(msg, this);
    }

    @Override
    public void onError(Platform platform, int i, Throwable throwable) {
        Message msg = new Message();
        msg.what = 1;
        UIHandler.sendMessage(msg, this);
    }

    @Override
    public void onCancel(Platform platform, int i) {
        Message msg = new Message();
        msg.what = 0;
        UIHandler.sendMessage(msg, this);
    }

    @Override
    public boolean handleMessage(Message msg) {
        int what = msg.what;
        if (what == 1) {
            Toast.makeText(this, "分享失败", Toast.LENGTH_SHORT).show();
        }
        if (share != null) {
            share.dismiss();
        }

        return false;
    }
    @Override
    protected void onResume() {
        super.onResume();
        if (share != null) {
            share.dismiss();
        }
    }
}

//    @Override
//    public void onClick(View v) {
//        switch (v.getId()) {
//            case R.id.tv_item_one:
//                myViewPager.setCurrentItem(0);
//                tv_item_one.setBackgroundColor(Color.RED);
//                tv_item_two.setBackgroundColor(Color.WHITE);
//                tv_item_three.setBackgroundColor(Color.WHITE);
//                break;
//            case R.id.tv_item_two:
//                myViewPager.setCurrentItem(1);
//                tv_item_one.setBackgroundColor(Color.WHITE);
//                tv_item_two.setBackgroundColor(Color.RED);
//                tv_item_three.setBackgroundColor(Color.WHITE);
//                break;
//            case R.id.tv_item_three:
//                myViewPager.setCurrentItem(2);
//                tv_item_one.setBackgroundColor(Color.WHITE);
//                tv_item_two.setBackgroundColor(Color.WHITE);
//                tv_item_three.setBackgroundColor(Color.RED);
//                break;
//        }
//    }
//    public class MyPagerChangeListener implements ViewPager.OnPageChangeListener {
//
//        @Override
//        public void onPageScrollStateChanged(int arg0) {
//        }
//
//        @Override
//        public void onPageScrolled(int arg0, float arg1, int arg2) {
//        }
//
//        @Override
//        public void onPageSelected(int arg0) {
//            switch (arg0) {
//                case 0:
//                    tv_item_one.setBackgroundColor(Color.RED);
//                    tv_item_two.setBackgroundColor(Color.WHITE);
//                    tv_item_three.setBackgroundColor(Color.WHITE);
//                    break;
//                case 1:
//                    tv_item_one.setBackgroundColor(Color.WHITE);
//                    tv_item_two.setBackgroundColor(Color.RED);
//                    tv_item_three.setBackgroundColor(Color.WHITE);
//                    break;
//                case 2:
//                    tv_item_one.setBackgroundColor(Color.WHITE);
//                    tv_item_two.setBackgroundColor(Color.WHITE);
//                    tv_item_three.setBackgroundColor(Color.RED);
//                    break;
//            }
//        }
//    }
//}
