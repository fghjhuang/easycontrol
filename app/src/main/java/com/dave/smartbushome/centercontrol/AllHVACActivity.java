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
import com.dave.smartbushome.database.Savehvac;
import com.dave.smartbushome.database.Savelight;
import com.dave.smartbushome.database.Saveroom;
import com.dave.smartbushome.founction_command.ACcontrol;
import com.dave.smartbushome.founction_command.lightcontrol;

import java.util.ArrayList;
import java.util.List;

public class AllHVACActivity extends AppCompatActivity {
    TextView allhvacselectroom;
    WheelView wva;
    ACcontrol ac=new ACcontrol();
    List<Saveroom> roomlist=new ArrayList<Saveroom>();
    List<String> roomnamelist=new ArrayList<String>();
    List<Savehvac> allhvaclist=new ArrayList<Savehvac>();
    Handler getdatahandler=new Handler();
    Handler senthandler=new Handler();
    String selectroom="";
    int controltype=0;
    boolean senting=false,allac=false;
    int roomid=0;

    //AC command type define
    private static final byte const_ac_cmd_type_onoff=3;
    private static final byte const_ac_cmd_type_set_cold_tmp=4;
    private static final byte const_ac_cmd_type_set_fan=5;
    private static final byte const_ac_cmd_type_set_mode=6;
    private static final byte const_ac_cmd_type_set_heat_tmp=7;
    private static final byte const_ac_cmd_type_set_auto_tmp=8;

    //ac mode
    private static final byte const_mode_cool=0;
    private static final byte const_mode_heat=1;
    private static final byte const_mode_fan=2;
    private static final byte const_mode_auto=3;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_hvac);
        Toolbar toolbar = (Toolbar) findViewById(R.id.allhvac_toolbar);
        toolbar.setTitleTextColor(getResources().getColor(R.color.tab_bgcolor));
        toolbar.setTitle("All HVAC");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        final Drawable upArrow = getResources().getDrawable(R.mipmap.abc_ic_ab_back_mtrl_am_alpha);
        upArrow.setColorFilter(getResources().getColor(R.color.tab_bgcolor), PorterDuff.Mode.SRC_ATOP);
        getSupportActionBar().setHomeAsUpIndicator(upArrow);

        SharedPreferences sharedcolorPre = getSharedPreferences("xxx", MODE_PRIVATE);
        int backgroudcolor = sharedcolorPre.getInt("xxx", 0xFF000000);
        RelativeLayout roomacbg=(RelativeLayout)findViewById(R.id.allhvacout);
        roomacbg.setBackgroundColor(backgroudcolor);

        wva = (WheelView) findViewById(R.id.hvacroomchoose);
        allhvacselectroom=(TextView)findViewById(R.id.allhvacselectroom);
        wva.setOffset(1);

        wva.setOnWheelViewListener(new WheelView.OnWheelViewListener() {
            @Override
            public void onSelected(int selectedIndex, String item) {
                allhvacselectroom.setText("Select Room: " + item);
                selectroom = item;
            }
        });
        if(MainActivity.mydupsocket!=null){
            MainActivity.mydupsocket.initprocess();
        }else{
            finish();
        }
        getdatahandler.postDelayed(getdatarun, 20);
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
            roomnamelist.add("All hvac");
            wva.setItems(roomnamelist);
            selectroom=roomnamelist.get(0);
            allhvacselectroom.setText("Select Room: " + selectroom);
            allhvaclist=MainActivity.mgr.queryhvac();
        }
    };

    int count=0;
    Runnable senrun=new Runnable() {
        @Override
        public void run() {
            if(count>=allhvaclist.size()){
                count=0;
                senting=false;
                allac=false;
                controltype=0;
                Toast.makeText(AllHVACActivity.this, "finished", Toast.LENGTH_SHORT).show();
                senthandler.removeCallbacks(senrun);
            }else{
                if(allac){
                   switch (controltype){
                       case 1:
                           ac.ACControl((byte)allhvaclist.get(count).subnetID,(byte)allhvaclist.get(count).deviceID,
                                   const_ac_cmd_type_onoff,1 ,MainActivity.mydupsocket);
                           break;
                       case 2:
                           ac.ACControl((byte)allhvaclist.get(count).subnetID,(byte)allhvaclist.get(count).deviceID,
                                   const_ac_cmd_type_onoff,0 ,MainActivity.mydupsocket);
                           break;
                       case 3:
                           ac.ACControl((byte)allhvaclist.get(count).subnetID,(byte)allhvaclist.get(count).deviceID,
                                   const_ac_cmd_type_set_mode,const_mode_cool,MainActivity.mydupsocket);
                           break;
                       case 4:
                           ac.ACControl((byte)allhvaclist.get(count).subnetID,(byte)allhvaclist.get(count).deviceID,
                                   const_ac_cmd_type_set_mode,const_mode_heat,MainActivity.mydupsocket);
                           break;
                       case 5:
                           ac.ACControl((byte)allhvaclist.get(count).subnetID,(byte)allhvaclist.get(count).deviceID,
                                   const_ac_cmd_type_set_mode,const_mode_fan ,MainActivity.mydupsocket);
                           break;
                       case 6:
                           ac.ACControl((byte)allhvaclist.get(count).subnetID,(byte)allhvaclist.get(count).deviceID,
                                   const_ac_cmd_type_set_mode,const_mode_auto,MainActivity.mydupsocket );
                           break;
                           default:break;
                   }
                }else{
                    if(allhvaclist.get(count).room_id==roomid){
                        switch (controltype){
                            case 1:
                                ac.ACControl((byte)allhvaclist.get(count).subnetID,(byte)allhvaclist.get(count).deviceID,
                                        const_ac_cmd_type_onoff,1 ,MainActivity.mydupsocket);
                                break;
                            case 2:
                                ac.ACControl((byte)allhvaclist.get(count).subnetID,(byte)allhvaclist.get(count).deviceID,
                                        const_ac_cmd_type_onoff,0 ,MainActivity.mydupsocket);
                                break;
                            case 3:
                                ac.ACControl((byte)allhvaclist.get(count).subnetID,(byte)allhvaclist.get(count).deviceID,
                                        const_ac_cmd_type_set_mode,const_mode_cool,MainActivity.mydupsocket);
                                break;
                            case 4:
                                ac.ACControl((byte)allhvaclist.get(count).subnetID,(byte)allhvaclist.get(count).deviceID,
                                        const_ac_cmd_type_set_mode,const_mode_heat,MainActivity.mydupsocket);
                                break;
                            case 5:
                                ac.ACControl((byte)allhvaclist.get(count).subnetID,(byte)allhvaclist.get(count).deviceID,
                                        const_ac_cmd_type_set_mode,const_mode_fan,MainActivity.mydupsocket );
                                break;
                            case 6:
                                ac.ACControl((byte)allhvaclist.get(count).subnetID,(byte)allhvaclist.get(count).deviceID,
                                        const_ac_cmd_type_set_mode,const_mode_auto ,MainActivity.mydupsocket);
                                break;
                            default:break;
                        }
                    }
                }
                count++;
                senthandler.postDelayed(senrun,70);
            }

        }
    };

    public void allacon(View v){
        for(int i=0;i<roomlist.size();i++){
            if(roomlist.get(i).room_name.equals(selectroom)){
                roomid=roomlist.get(i).room_id;
                break;
            }
        }
        if(selectroom.equals("All hvac")){
            allac=true;
        }
        count=0;
        senting=true;
        controltype=1;//on
        senthandler.postDelayed(senrun,30);
    }
    public void allacoff(View v){
        if(senting){
            Toast.makeText(AllHVACActivity.this, "senting command,please wait", Toast.LENGTH_SHORT).show();
        }else{
            for(int i=0;i<roomlist.size();i++){
                if(roomlist.get(i).room_name.equals(selectroom)){
                    roomid=roomlist.get(i).room_id;
                    break;
                }
            }
            if(selectroom.equals("All hvac")){
                allac=true;
            }
            count=0;
            senting=true;
            controltype=2;//off
            senthandler.postDelayed(senrun,30);
        }

    }
    public void allaccool(View v){
        if(senting){
            Toast.makeText(AllHVACActivity.this, "senting command,please wait", Toast.LENGTH_SHORT).show();
        }else{
            for(int i=0;i<roomlist.size();i++){
                if(roomlist.get(i).room_name.equals(selectroom)){
                    roomid=roomlist.get(i).room_id;
                    break;
                }
            }
            if(selectroom.equals("All hvac")){
                allac=true;
            }
            count=0;
            senting=true;
            controltype=3;//cool
            senthandler.postDelayed(senrun,30);
        }

    }
    public void allacheat(View v){
        if(senting){
            Toast.makeText(AllHVACActivity.this, "senting command,please wait", Toast.LENGTH_SHORT).show();
        }else{
            for(int i=0;i<roomlist.size();i++){
                if(roomlist.get(i).room_name.equals(selectroom)){
                    roomid=roomlist.get(i).room_id;
                    break;
                }
            }
            if(selectroom.equals("All hvac")){
                allac=true;
            }
            count=0;
            senting=true;
            controltype=4;//heat
            senthandler.postDelayed(senrun,30);
        }

    }
    public void allacfan(View v){
        if(senting){
            Toast.makeText(AllHVACActivity.this, "senting command,please wait", Toast.LENGTH_SHORT).show();
        }else{
            for(int i=0;i<roomlist.size();i++){
                if(roomlist.get(i).room_name.equals(selectroom)){
                    roomid=roomlist.get(i).room_id;
                    break;
                }
            }
            if(selectroom.equals("All hvac")){
                allac=true;
            }
            count=0;
            senting=true;
            controltype=5;//fan
            senthandler.postDelayed(senrun,30);
        }

    }
    public void allacauto(View v){
        if(senting){
            Toast.makeText(AllHVACActivity.this, "senting command,please wait", Toast.LENGTH_SHORT).show();
        }else{
            for(int i=0;i<roomlist.size();i++){
                if(roomlist.get(i).room_name.equals(selectroom)){
                    roomid=roomlist.get(i).room_id;
                    break;
                }
            }
            if(selectroom.equals("All hvac")){
                allac=true;
            }
            count=0;
            senting=true;
            controltype=6;//auto
            senthandler.postDelayed(senrun,30);
        }

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
