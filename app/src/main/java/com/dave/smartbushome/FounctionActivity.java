package com.dave.smartbushome;

import android.content.Intent;
import android.os.Handler;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.content.Context;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.dave.smartbushome.assist.MusicNotifyReceiver;
import com.dave.smartbushome.assist.ShakeEventManager;
import com.dave.smartbushome.founction_view.Curtain;
import com.dave.smartbushome.founction_view.Fan;
import com.dave.smartbushome.founction_view.HAVC;
import com.dave.smartbushome.founction_view.Light;
import com.dave.smartbushome.founction_view.Media;
import com.dave.smartbushome.founction_view.Mood;
import com.dave.smartbushome.founction_view.Music;
import com.dave.smartbushome.founction_view.NineInOne;
import com.dave.smartbushome.founction_view.Other;

import java.util.ArrayList;
import java.util.List;

public class FounctionActivity extends AppCompatActivity implements ShakeEventManager.ShakeListener {
    private android.support.v4.app.Fragment contentFragment;
    private FragmentManager fragmentManager;
    public static int roomidfc;
    public static Context fcontext;
    public final static String ACTION_BACKPRESS = "com.example.founction.BACKPRESS";
    public final static String ACTION_DELETELIGHT = "com.example.founction.DELETELIGHT";
    public final static String ACTION_DELETEMOOD = "com.example.founction.DELETEMOOD";
    public final static String ACTION_DELETECURTAIN = "com.example.founction.DELETECURTAIN";
    public final static String ACTION_DELETEOTHER = "com.example.founction.DELETEOTHER";
    public final static String ACTION_DELETEFAN = "com.example.founction.DELETEFAN";
    public final static String ACTION_SHAKE = "com.example.founction.SHAKE";
    String x;
    ActionBar actionBar;
    ArrayAdapter<String> adapter;
    boolean initfinish=false;
    String[] fc=null;
    List<String> spinnerlist=new ArrayList<String>();
    Handler startwork=new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_founction);
        Toolbar toolbar = (Toolbar) findViewById(R.id.founction_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setTitleTextColor(getResources().getColor(R.color.tab_bgcolor));


        Intent getdata=getIntent();
        roomidfc=getdata.getIntExtra("roomid", 0);
        x=getdata.getStringExtra("founction_type");
        Bundle b=this.getIntent().getExtras();
        fc = b.getStringArray("founction_count");


        adapter = getadapter(fc);
        actionBar = getSupportActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);
        actionBar.setListNavigationCallbacks(adapter, new DropDownListenser());
        fragmentManager = getSupportFragmentManager();
        fcontext=FounctionActivity.this;
        sd = new ShakeEventManager();
        sd.setListener(this);
        sd.init(this);
       /* if(MainActivity.mydupsocket!=null){
            MainActivity.mydupsocket.initprocess();
        }else{
            finish();
        }*/
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        if(x.equals("Light")){
            actionBar.setSelectedNavigationItem(getspinnerindex("Light"));
            contentFragment = new Light();
            transaction.replace(R.id.founctionlayout, contentFragment).commit();
        }else if(x.equals("HVAC")){
            actionBar.setSelectedNavigationItem(getspinnerindex("HVAC"));
            contentFragment = new HAVC();
            transaction.replace(R.id.founctionlayout, contentFragment).commit();
        }else if(x.equals("Mood")){
            actionBar.setSelectedNavigationItem(getspinnerindex("Mood"));
            contentFragment = new Mood();
            transaction.replace(R.id.founctionlayout, contentFragment).commit();
        }else if(x.equals("Fan")){
            actionBar.setSelectedNavigationItem(getspinnerindex("Fan"));
            contentFragment = new Fan();
            transaction.replace(R.id.founctionlayout, contentFragment).commit();
        }else if(x.equals("Curtain")){
            actionBar.setSelectedNavigationItem(getspinnerindex("Curtain"));
            contentFragment = new Curtain();
            transaction.replace(R.id.founctionlayout, contentFragment).commit();
        }else if(x.equals("Music")){
            actionBar.setSelectedNavigationItem(getspinnerindex("Music"));
            contentFragment = new Music();
            transaction.replace(R.id.founctionlayout, contentFragment).commit();
        }else if(x.equals("Other")){
            actionBar.setSelectedNavigationItem(getspinnerindex("Other"));
            contentFragment = new Other();
            transaction.replace(R.id.founctionlayout, contentFragment).commit();
        }else if(x.equals("Media")){
            actionBar.setSelectedNavigationItem(getspinnerindex("Media"));
            contentFragment = new Media();
            transaction.replace(R.id.founctionlayout, contentFragment).commit();
        }else if(x.equals("9 in 1")){
            actionBar.setSelectedNavigationItem(getspinnerindex("9 in 1"));
            contentFragment = new NineInOne();
            transaction.replace(R.id.founctionlayout, contentFragment).commit();
        }
    }
    class DropDownListenser implements ActionBar.OnNavigationListener
    {
        /* 当选择下拉菜单项的时候，将Activity中的内容置换为对应的Fragment */
        public boolean onNavigationItemSelected(int itemPosition, long itemId)
        {
            if(initfinish){
                FragmentTransaction transactionx = fragmentManager.beginTransaction();
                if(spinnerlist.get(itemPosition).equals("Light")){
                    android.support.v4.app.Fragment contentFragmentx1=new Light();
                    transactionx.replace(R.id.founctionlayout, contentFragmentx1).commit();
                }else if(spinnerlist.get(itemPosition).equals("HVAC")){
                    android.support.v4.app.Fragment contentFragmentx2=new HAVC();
                    transactionx.replace(R.id.founctionlayout, contentFragmentx2).commit();
                }else if(spinnerlist.get(itemPosition).equals("Mood")){
                    android.support.v4.app.Fragment contentFragmentx3=new Mood();
                    transactionx.replace(R.id.founctionlayout, contentFragmentx3).commit();
                }else if(spinnerlist.get(itemPosition).equals("Curtain")){
                    android.support.v4.app.Fragment contentFragmentx4=new Curtain();
                    transactionx.replace(R.id.founctionlayout, contentFragmentx4).commit();
                }else if(spinnerlist.get(itemPosition).equals("Music")){
                    android.support.v4.app.Fragment contentFragmentx5=new Music();
                    transactionx.replace(R.id.founctionlayout, contentFragmentx5).commit();
                }else if(spinnerlist.get(itemPosition).equals("Other")){
                    android.support.v4.app.Fragment contentFragmentx6=new Other();
                    transactionx.replace(R.id.founctionlayout, contentFragmentx6).commit();
                }else if(spinnerlist.get(itemPosition).equals("Fan")){
                    android.support.v4.app.Fragment contentFragmentx7=new Fan();
                    transactionx.replace(R.id.founctionlayout, contentFragmentx7).commit();
                }else if(spinnerlist.get(itemPosition).equals("Media")){
                    android.support.v4.app.Fragment contentFragmentx8=new Media();
                    transactionx.replace(R.id.founctionlayout, contentFragmentx8).commit();
                }else if(spinnerlist.get(itemPosition).equals("9 in 1")){
                    android.support.v4.app.Fragment contentFragmentx8=new NineInOne();
                    transactionx.replace(R.id.founctionlayout, contentFragmentx8).commit();
                }
            }else{
                initfinish=true;
            }
            return false;
        }
    }
    private ShakeEventManager sd;

    @Override
    public void onShake(int type) {
        final Intent intent = new Intent(ACTION_SHAKE);
        intent.putExtra("shake_type", type);
        fcontext.sendBroadcast(intent);
       /* switch (type){
            case 1:Toast.makeText(this, "左边", Toast.LENGTH_SHORT).show();
                break;
            case 2:Toast.makeText(this, "右边", Toast.LENGTH_SHORT).show();
                break;
            case 5:Toast.makeText(this, "上面", Toast.LENGTH_SHORT).show();
                break;
            case 6:Toast.makeText(this, "下面", Toast.LENGTH_SHORT).show();
                break;
        }*/

    }
    @Override
    public void onRestart(){
        super.onRestart();
    }
    @Override
    public void onResume(){
        super.onResume();
        sd.register();
        //initfinish=true;
       // intiReceiver();
    }
    MusicNotifyReceiver mReceiver;
    @Override
    public void onPause(){
        super.onPause();
        sd.deregister();

    }
    @Override
    public void onDestroy(){
        super.onDestroy();
        initfinish=false;
        spinnerlist.clear();
        //unregeisterReceiver();
      //  MainActivity.mydupsocket.StopAllThread();
    }

    private void broadcastUpdate(final String action) {
        final Intent intent = new Intent(action);
      //  intent.putExtra(ACTION_BACKPRESS, 1);
        fcontext.sendBroadcast(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.founction_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // TODO Auto-generated method stub
        switch(item.getItemId()){
            case android.R.id.home:
                broadcastUpdate(ACTION_BACKPRESS);
                break;
            default:
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        broadcastUpdate(ACTION_BACKPRESS);
        return;
    }

    public ArrayAdapter getadapter(String[] fc){
        ArrayAdapter<String> resultadapter= new ArrayAdapter<String>(this,R.layout.spinner_dropdown_item);
        if(fc[0].equals("1")){
            spinnerlist.add("Light");
            resultadapter.add("Light");
        }
        if(fc[1].equals("1")){
            spinnerlist.add("HVAC");
            resultadapter.add("HVAC");
        }
        if(fc[2].equals("1")){
            spinnerlist.add("Fan");
            resultadapter.add("Fan");
        }
        if(fc[3].equals("1")){
            spinnerlist.add("Curtain");
            resultadapter.add("Curtain");
        }
        if(fc[4].equals("1")){
            spinnerlist.add("Music");
            resultadapter.add("Music");
        }
        if(fc[5].equals("1")){
            spinnerlist.add("Mood");
            resultadapter.add("Mood");
        }
        if(fc[6].equals("1")){
            spinnerlist.add("Other");
            resultadapter.add("Other");
        }
        if(fc[7].equals("1")){
            spinnerlist.add("Media");
            resultadapter.add("Media");
        }
        if(fc[8].equals("1")){
            spinnerlist.add("9 in 1");
            resultadapter.add("9 in 1");
        }
        return resultadapter;
    }

    public int getspinnerindex(String name){
        int result=0;
        for(int i=0;i<spinnerlist.size();i++){
            if(spinnerlist.get(i).equals(name)){
                result=i;
            }
        }
        return result;
    }
}
