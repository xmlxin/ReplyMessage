package com.xiaoxin.jhang;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.xiaoxin.jhang.activity.SendMsgWx;
import com.xiaoxin.jhang.activity.SendMsgqq;
import com.xiaoxin.jhang.activity.WxReplyActivity;
import com.xiaoxin.jhang.util.SystemUtil;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,View.OnClickListener {

    private static final String TAG = "MainActivity";
    private Button mOpenAeccess,mWxName,mSendMsg,mOutoReply;
    private Toolbar mToolbar;
    private Button mBt_qq;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, mToolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        initView();

    }

    private void initView() {

        mOpenAeccess = (Button) findViewById(R.id.bt_open_access);
        mWxName = (Button) findViewById(R.id.bt_wxName);
        mSendMsg = (Button) findViewById(R.id.bt_sendMsg);
        mOutoReply = (Button) findViewById(R.id.bt_outoReply);
        mBt_qq = (Button) findViewById(R.id.bt_qq);

        mOpenAeccess.setOnClickListener(this);
        mWxName.setOnClickListener(this);
        mSendMsg.setOnClickListener(this);
        mOutoReply.setOnClickListener(this);
        mBt_qq.setOnClickListener(this);

    }

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
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.bt_open_access:
                SystemUtil.openService(this);
                Toast.makeText(this,"开启WxReply",Toast.LENGTH_SHORT).show();
                break;
            case R.id.bt_wxName:
                Toast.makeText(this,"iv2",Toast.LENGTH_SHORT).show();
                break;
            case R.id.bt_sendMsg:
                startActivity(new Intent(this, SendMsgWx.class));
                Toast.makeText(this,"iv3",Toast.LENGTH_SHORT).show();
                break;
            case R.id.bt_outoReply:
                //SharedPreferencesUtils.init(this).putBoolean("wxReply",true);
                startActivity(new Intent(this, WxReplyActivity.class));
                Toast.makeText(this,"iv4",Toast.LENGTH_SHORT).show();
                break;
            case R.id.bt_qq:
                startActivity(new Intent(this, SendMsgqq.class));
                Toast.makeText(this,"iv3",Toast.LENGTH_SHORT).show();
                break;

        }
    }


}
