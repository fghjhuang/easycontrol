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
import com.dave.smartbushome.database.Savecurtain;
import com.dave.smartbushome.database.Savelight;
import com.dave.smartbushome.database.Saveroom;
import com.dave.smartbushome.founction_command.curtaincontrol;
import com.dave.smartbushome.founction_command.lightcontrol;

import java.util.ArrayList;
import java.util.List;

public class AllCurtainActivity extends AppCompatActivity {
    TextView allcurtainselectroom;
    WheelView wva;
    curtaincontrol cc=new curtaincontrol();
    List<Saveroom> roomlist=new ArrayList<Saveroom>();
    List<String> roomnamelist=new ArrayList<String>();
    List<Savecurtain> allcurtainlist=new ArrayList<Savecurtain>();
    Handler getdatahandler=new Handler();
    Handler senthandler=new Handler();
    String selectroom="";
    String value="";
    boolean senting=false,allcurtain=false;
    int roomid=0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_curtain);
        Toolbar toolbar = (Toolbar) findViewById(R.id.allcurtain_toolbar);
        toolbar.setTitleTextColor(getResources().getColor(R.color.tab_bgcolor));
        toolbar.setTitle("All Curtain");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        SharedPreferences sharedcolorPre = getSharedPreferences("xxx", MODE_PRIVATE);
        int backgroudcolor = sharedcolorPre.getInt("xxx", 0xFF000000);
        RelativeLayout roomacbg=(RelativeLayout)findViewById(R.id.allcurtainout);
        roomacbg.setBackgroundColor(backgroudcolor);

        final Drawable upArrow = getResources().getDrawable(R.mipmap.abc_ic_ab_back_mtrl_am_alpha);
        upArrow.setColorFilter(getResources().getColor(R.color.tab_bgcolor), PorterDuff.Mode.SRC_ATOP);
        getSupportActionBar().setHomeAsUpIndicator(upArrow);

        wva = (WheelView) findViewById(R.id.curtainroomchoose);
        allcurtainselectroom=(TextView)findViewById(R.id.allcurtainselectroom);
        wva.setOffset(1);

        wva.setOnWheelViewListener(new WheelView.OnWheelViewListener() {
            @Override
            public void onSelected(int selectedIndex, String item) {
                allcurtainselectroom.setText("Select Room: " + item);
                selectroom=item;
            }
        });
        /*if(MainActivity.mydupsocket!=null){
            MainActivity.mydupsocket.initprocess();
        }else{
            finish();
        }*/
        getdatahandler.postDelayed(getdatarun,20);
    }
    @Override
    public void onDestroy(){
        super.onDestroy();
        //unregeisterReceiver();
      //  MainActivity.mydupsocket.StopAllThread();
    }
    Runnable getdatarun=new Runnable() {
        @Override
        public void run() {
            roomlist= MainActivity.mgr.queryroom();
            for(int i=0;i<roomlist.size();i++){
                roomnamelist.add(roomlist.get(i).room_name);
            }
            roomnamelist.add("All Curtain");
            wva.setItems(roomnamelist);
            selectroom=roomnamelist.get(0);
            allcurtainselectroom.setText("Select Room: " + selectroom);
            allcurtainlist=MainActivity.mgr.querycurtain();
        }
    };
    int count=0;
    Runnable senrun=new Runnable() {
        @Override
        public void run() {
            if(count>=allcurtainlist.size()){
                count=0;
                senting=false;
                allcurtain=false;
                Toast.makeText(AllCurtainActivity.this, "finished", Toast.LENGTH_SHORT).show();
                senthandler.removeCallbacks(senrun);
            }else{
                if(allcurtain){
                    cc.CurtainControl((byte)allcurtainlist.get(count).subnetID,(byte)allcurtainlist.get(count).deviceID,
                            allcurtainlist.get(count).channel_1, allcurtainlist.get(count).channel_2,value,MainActivity.mydupsocket);
                }else{
                    if(allcurtainlist.get(count).room_id==roomid){
                        cc.CurtainControl((byte)allcurtainlist.get(count).subnetID,(byte)allcurtainlist.get(count).deviceID,
                                allcurtainlist.get(count).channel_1, allcurtainlist.get(count).channel_2,value,MainActivity.mydupsocket);
                    }
                }
                count++;
                senthandler.postDelayed(senrun,70);
            }

        }
    };
    public void allopen(View v){
        for(int i=0;i<roomlist.size();i++){
            if(roomlist.get(i).room_name.equals(selectroom)){
                roomid=roomlist.get(i).room_id;
                break;
            }
        }
        if(selectroom.equals("All Curtain")){
            allcurtain=true;
        }
        count=0;
        senting=true;
        value="open";
        senthandler.postDelayed(senrun,30);
    }
    public void allclose(View v){
        for(int i=0;i<roomlist.size();i++){
            if(roomlist.get(i).room_name.equals(selectroom)){
                roomid=roomlist.get(i).room_id;
                break;
            }
        }
        if(selectroom.equals("All Curtain")){
            allcurtain=true;
        }
        count=0;
        senting=true;
        value="close";
        senthandler.postDelayed(senrun,30);
    }
    public void allstop(View v){
        for(int i=0;i<roomlist.size();i++){
            if(roomlist.get(i).room_name.equals(selectroom)){
                roomid=roomlist.get(i).room_id;
                break;
            }
        }
        if(selectroom.equals("All Curtain")){
            allcurtain=true;
        }
        count=0;
        senting=true;
        value="stop";
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
