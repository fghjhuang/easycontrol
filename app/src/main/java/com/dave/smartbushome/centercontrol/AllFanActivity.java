package com.dave.smartbushome.centercontrol;

import android.content.SharedPreferences;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.dave.smartbushome.MainActivity;
import com.dave.smartbushome.R;
import com.dave.smartbushome.assist.WheelView;
import com.dave.smartbushome.database.Savefan;
import com.dave.smartbushome.database.Saveother;
import com.dave.smartbushome.database.Saveroom;
import com.dave.smartbushome.founction_command.fancontrol;
import com.dave.smartbushome.founction_command.othercontrol;

import java.util.ArrayList;
import java.util.List;

public class AllFanActivity extends AppCompatActivity {
    TextView allfanselectroom;
    WheelView wva;
    fancontrol fc=new fancontrol();
    List<Saveroom> roomlist=new ArrayList<Saveroom>();
    List<String> roomnamelist=new ArrayList<String>();
    List<Savefan> allfanlist=new ArrayList<Savefan>();
    Handler getdatahandler=new Handler();
    Handler senthandler=new Handler();
    String selectroom="";
    int value=0;
    boolean senting=false,allfan=false;
    int roomid=0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_fan);
        Toolbar toolbar = (Toolbar) findViewById(R.id.allfan_toolbar);
        toolbar.setTitleTextColor(getResources().getColor(R.color.tab_bgcolor));
        toolbar.setTitle("All Fan");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        SharedPreferences sharedcolorPre = getSharedPreferences("xxx", MODE_PRIVATE);
        int backgroudcolor = sharedcolorPre.getInt("xxx", 0xFF000000);
        RelativeLayout roomacbg=(RelativeLayout)findViewById(R.id.allfanout);
        roomacbg.setBackgroundColor(backgroudcolor);

        final Drawable upArrow = getResources().getDrawable(R.mipmap.abc_ic_ab_back_mtrl_am_alpha);
        upArrow.setColorFilter(getResources().getColor(R.color.tab_bgcolor), PorterDuff.Mode.SRC_ATOP);
        getSupportActionBar().setHomeAsUpIndicator(upArrow);

        wva = (WheelView) findViewById(R.id.fanroomchoose);
        allfanselectroom=(TextView)findViewById(R.id.allfanselectroom);
        wva.setOffset(1);

        wva.setOnWheelViewListener(new WheelView.OnWheelViewListener() {
            @Override
            public void onSelected(int selectedIndex, String item) {
                allfanselectroom.setText("Select Room: " + item);
                selectroom = item;
            }
        });
        /*if(MainActivity.mydupsocket!=null){
            MainActivity.mydupsocket.initprocess();
        }else{
            finish();
        }*/
        getdatahandler.postDelayed(getdatarun, 20);
    }
    @Override
    public void onDestroy(){
        super.onDestroy();
        //unregeisterReceiver();
       // MainActivity.mydupsocket.StopAllThread();
    }
    Runnable getdatarun=new Runnable() {
        @Override
        public void run() {
            roomlist= MainActivity.mgr.queryroom();
            for(int i=0;i<roomlist.size();i++){
                roomnamelist.add(roomlist.get(i).room_name);
            }
            roomnamelist.add("All Fan");
            wva.setItems(roomnamelist);
            selectroom=roomnamelist.get(0);
            allfanselectroom.setText("Select Room: " + selectroom);
            allfanlist=MainActivity.mgr.queryfan();
        }
    };
    int count=0;
    Runnable senrun=new Runnable() {
        @Override
        public void run() {
            if(count>=allfanlist.size()){
                count=0;
                senting=false;
                allfan=false;
                Toast.makeText(AllFanActivity.this, "finished", Toast.LENGTH_SHORT).show();
                senthandler.removeCallbacks(senrun);
            }else{
                if(allfan){
                   fc.FanChannelControl((byte)allfanlist.get(count).subnetID,(byte)allfanlist.get(count).deviceID,
                           (byte)allfanlist.get(count).channel,value,MainActivity.mydupsocket);
                }else{
                    if(allfanlist.get(count).room_id==roomid){
                        fc.FanChannelControl((byte)allfanlist.get(count).subnetID,(byte)allfanlist.get(count).deviceID,
                                (byte)allfanlist.get(count).channel,value,MainActivity.mydupsocket);
                    }
                }
                count++;
                senthandler.postDelayed(senrun,70);
            }

        }
    };
    public void allfanoff(View v){
        for(int i=0;i<roomlist.size();i++){
            if(roomlist.get(i).room_name.equals(selectroom)){
                roomid=roomlist.get(i).room_id;
                break;
            }
        }
        if(selectroom.equals("All Fan")){
            allfan=true;
        }
        count=0;
        senting=true;
        value=0;
        senthandler.postDelayed(senrun,30);
    }

    public void allfanfull(View v){
        for(int i=0;i<roomlist.size();i++){
            if(roomlist.get(i).room_name.equals(selectroom)){
                roomid=roomlist.get(i).room_id;
                break;
            }
        }
        if(selectroom.equals("All Fan")){
            allfan=true;
        }
        count=0;
        senting=true;
        value=4;
        senthandler.postDelayed(senrun,30);
    }

    public void allfanlow(View v){
        for(int i=0;i<roomlist.size();i++){
            if(roomlist.get(i).room_name.equals(selectroom)){
                roomid=roomlist.get(i).room_id;
                break;
            }
        }
        if(selectroom.equals("All Fan")){
            allfan=true;
        }
        count=0;
        senting=true;
        value=1;
        senthandler.postDelayed(senrun,30);
    }

    public void allfanmid(View v){
        for(int i=0;i<roomlist.size();i++){
            if(roomlist.get(i).room_name.equals(selectroom)){
                roomid=roomlist.get(i).room_id;
                break;
            }
        }
        if(selectroom.equals("All Fan")){
            allfan=true;
        }
        count=0;
        senting=true;
        value=2;
        senthandler.postDelayed(senrun,30);
    }

    public void allfanhigh(View v){
        for(int i=0;i<roomlist.size();i++){
            if(roomlist.get(i).room_name.equals(selectroom)){
                roomid=roomlist.get(i).room_id;
                break;
            }
        }
        if(selectroom.equals("All Fan")){
            allfan=true;
        }
        count=0;
        senting=true;
        value=3;
        senthandler.postDelayed(senrun,30);
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        // getMenuInflater().inflate(R.menu.marco_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // TODO Auto-generated method stub
        switch(item.getItemId()){
            case android.R.id.home:
                finish();
                break;
            default:
                break;
        }

        return super.onOptionsItemSelected(item);
    }
}
