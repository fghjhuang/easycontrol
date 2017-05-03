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
import com.dave.smartbushome.database.Savelight;
import com.dave.smartbushome.database.Savemusic;
import com.dave.smartbushome.database.Saveroom;
import com.dave.smartbushome.founction_command.lightcontrol;
import com.dave.smartbushome.founction_command.musiccontrol;

import java.util.ArrayList;
import java.util.List;

public class AllMusicActivity extends AppCompatActivity {
    TextView allmusicselectroom;
    WheelView wva;
    musiccontrol mc =new musiccontrol();
    List<Saveroom> roomlist=new ArrayList<Saveroom>();
    List<String> roomnamelist=new ArrayList<String>();
    List<Savemusic> allmusiclist=new ArrayList<Savemusic>();
    Handler getdatahandler=new Handler();
    Handler senthandler=new Handler();
    String selectroom="";
    int value1=0,value2=0,value3=0,value4=0;
    boolean senting=false,allmusic=false;
    int roomid=0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_music);
        Toolbar toolbar = (Toolbar) findViewById(R.id.allmusic_toolbar);
        toolbar.setTitleTextColor(getResources().getColor(R.color.tab_bgcolor));
        toolbar.setTitle("All Music");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        final Drawable upArrow = getResources().getDrawable(R.mipmap.abc_ic_ab_back_mtrl_am_alpha);
        upArrow.setColorFilter(getResources().getColor(R.color.tab_bgcolor), PorterDuff.Mode.SRC_ATOP);
        getSupportActionBar().setHomeAsUpIndicator(upArrow);

        SharedPreferences sharedcolorPre = getSharedPreferences("xxx", MODE_PRIVATE);
        int backgroudcolor = sharedcolorPre.getInt("xxx", 0xFF000000);
        RelativeLayout roomacbg=(RelativeLayout)findViewById(R.id.allmusicout);
        roomacbg.setBackgroundColor(backgroudcolor);

        wva = (WheelView) findViewById(R.id.musicroomchoose);
        allmusicselectroom=(TextView)findViewById(R.id.allmusicselectroom);
        wva.setOffset(1);

        wva.setOnWheelViewListener(new WheelView.OnWheelViewListener() {
            @Override
            public void onSelected(int selectedIndex, String item) {
                allmusicselectroom.setText("Select Room: " + item);
                selectroom=item;
            }
        });
        if(MainActivity.mydupsocket!=null){
            MainActivity.mydupsocket.initprocess();
        }else{
            finish();
        }
        getdatahandler.postDelayed(getdatarun,20);
    }
    @Override
    public void onDestroy(){
        super.onDestroy();
        //unregeisterReceiver();
        MainActivity.mydupsocket.StopAllThread();
    }
    Runnable getdatarun=new Runnable() {
        @Override
        public void run() {
            roomlist= MainActivity.mgr.queryroom();
            for(int i=0;i<roomlist.size();i++){
                roomnamelist.add(roomlist.get(i).room_name);
            }
            roomnamelist.add("All Music");
            wva.setItems(roomnamelist);
            selectroom=roomnamelist.get(0);
            allmusicselectroom.setText("Select Room: " + selectroom);
            allmusiclist=MainActivity.mgr.querymusic();
        }
    };
    int count=0;
    Runnable senrun=new Runnable() {
        @Override
        public void run() {
            if(count>=allmusiclist.size()){
                count=0;
                value1=value2=value3=value4=0;
                senting=false;
                allmusic=false;
                Toast.makeText(AllMusicActivity.this, "finished", Toast.LENGTH_SHORT).show();
                senthandler.removeCallbacks(senrun);
            }else{
                if(allmusic){
                    mc.MusicControl((byte)value1,(byte)value2,(byte)value3,(byte)value4,
                            (byte) allmusiclist.get(count).subnetID, (byte) allmusiclist.get(count).deviceID,MainActivity.mydupsocket);
                }else{
                    if(allmusiclist.get(count).room_id==roomid){
                        mc.MusicControl((byte)value1,(byte)value2,(byte)value3,(byte)value4,
                                (byte) allmusiclist.get(count).subnetID, (byte) allmusiclist.get(count).deviceID,MainActivity.mydupsocket);
                    }
                }
                count++;
                senthandler.postDelayed(senrun,70);
            }

        }
    };
    public void playall(View v){
        for(int i=0;i<roomlist.size();i++){
            if(roomlist.get(i).room_name.equals(selectroom)){
                roomid=roomlist.get(i).room_id;
                break;
            }
        }
        if(selectroom.equals("All Music")){
            allmusic=true;
        }
        count=0;
        senting=true;
        value1=4;
        value2=3;
        value3=0;
        value4=0;
        senthandler.postDelayed(senrun,30);
    }
    public void pauseall(View v){
        for(int i=0;i<roomlist.size();i++){
            if(roomlist.get(i).room_name.equals(selectroom)){
                roomid=roomlist.get(i).room_id;
                break;
            }
        }
        if(selectroom.equals("All Music")){
            allmusic=true;
        }
        count=0;
        senting=true;
        value1=4;
        value2=4;
        value3=0;
        value4=0;
        senthandler.postDelayed(senrun,30);
    }
    public void backall(View v){
        for(int i=0;i<roomlist.size();i++){
            if(roomlist.get(i).room_name.equals(selectroom)){
                roomid=roomlist.get(i).room_id;
                break;
            }
        }
        if(selectroom.equals("All Music")){
            allmusic=true;
        }
        count=0;
        senting=true;
        value1=4;
        value2=1;
        value3=0;
        value4=0;
        senthandler.postDelayed(senrun,30);
    }
    public void nextall(View v){
        for(int i=0;i<roomlist.size();i++){
            if(roomlist.get(i).room_name.equals(selectroom)){
                roomid=roomlist.get(i).room_id;
                break;
            }
        }
        if(selectroom.equals("All Music")){
            allmusic=true;
        }
        count=0;
        senting=true;
        value1=4;
        value2=2;
        value3=0;
        value4=0;
        senthandler.postDelayed(senrun,30);
    }
    public void all0(View v){
        for(int i=0;i<roomlist.size();i++){
            if(roomlist.get(i).room_name.equals(selectroom)){
                roomid=roomlist.get(i).room_id;
                break;
            }
        }
        if(selectroom.equals("All Music")){
            allmusic=true;
        }
        count=0;
        senting=true;
        value1=5;
        value2=1;
        value3=3;
        value4=79;
        senthandler.postDelayed(senrun,30);
    }
    public void all25(View v){
        for(int i=0;i<roomlist.size();i++){
            if(roomlist.get(i).room_name.equals(selectroom)){
                roomid=roomlist.get(i).room_id;
                break;
            }
        }
        if(selectroom.equals("All Music")){
            allmusic=true;
        }
        count=0;
        senting=true;
        value1=5;
        value2=1;
        value3=3;
        value4=60;
        senthandler.postDelayed(senrun,30);
    }
    public void all50(View v){
        for(int i=0;i<roomlist.size();i++){
            if(roomlist.get(i).room_name.equals(selectroom)){
                roomid=roomlist.get(i).room_id;
                break;
            }
        }
        if(selectroom.equals("All Music")){
            allmusic=true;
        }
        count=0;
        senting=true;
        value1=5;
        value2=1;
        value3=3;
        value4=40;
        senthandler.postDelayed(senrun,30);
    }
    public void all100(View v){
        for(int i=0;i<roomlist.size();i++){
            if(roomlist.get(i).room_name.equals(selectroom)){
                roomid=roomlist.get(i).room_id;
                break;
            }
        }
        if(selectroom.equals("All Music")){
            allmusic=true;
        }
        count=0;
        senting=true;
        value1=5;
        value2=1;
        value3=3;
        value4=0;
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
