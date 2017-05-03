package com.dave.smartbushome;


import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.PopupMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bigkoo.alertview.AlertView;
import com.dave.smartbushome.about.AboutActivity;
import com.dave.smartbushome.assist.Adapter.RoomAdapter;
import com.dave.smartbushome.assist.Adapter.RoomIconAdapter;
import com.dave.smartbushome.assist.DraggableGridViewPager;
import com.dave.smartbushome.assist.MusicNotification;
import com.dave.smartbushome.assist.MusicNotifyReceiver;
import com.dave.smartbushome.assist.holocolorpicker.ColorPicker;
import com.dave.smartbushome.assist.holocolorpicker.SVBar;
import com.dave.smartbushome.database.Saveroom;
import com.dave.smartbushome.mainsetting.MainSettingActivity;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
/**
 * A simple {@link Fragment} subclass.
 */
public class MainFragment extends Fragment {
    private DraggableGridViewPager mDraggableGridViewPager;
    android.os.Handler handle =new android.os.Handler();
    private RoomAdapter mAdapter;
    public List<Saveroom> roomdata=new ArrayList<Saveroom>();
    public static List<HashMap<String,String>> netdeviceList=new ArrayList<HashMap<String, String>>();
    private boolean deletechoose=false;
    public static Context maincontext;
    String roomicon="room_type1",settingicon="",roombgcolor="";
    private AlertView maddingAlertViewExt,miconAlertViewExt,msettingAlertViewExt,mpcolorAlertViewExt;//窗口拓展例子
    int settingid=0;
    private List<String> iconarray=new ArrayList<String>(){
        {
            add("room_type1");add("room_type2");add("room_type3");add("room_type4");
            add("room_type5");add("room_type6");add("room_type7");add("room_type8");
            add("room_type9");add("room_type10");add("room_type11");add("room_type12");
            add("room_type13");add("room_type14");add("room_type15");
        }
    };
    MusicNotifyReceiver mReceiver;
    View view;
    AlertView mAlertView;
    boolean exitdialogshow=false;

    public MainFragment() {
        // Required empty public constructor
    }

    public static MainFragment newInstance(String param1) {
        MainFragment fragment = new MainFragment();
        return fragment;
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view= inflater.inflate(R.layout.fragment_main, container, false);
        setHasOptionsMenu(true);
        mDraggableGridViewPager = (DraggableGridViewPager) view.findViewById(R.id.draggable_grid_view_pager);
        mAdapter =new RoomAdapter(getActivity());
        maincontext=getActivity();
        handle.postDelayed(getroomrun, 20);
        mDraggableGridViewPager.setOnPageChangeListener(new DraggableGridViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        mDraggableGridViewPager.setOnItemClickListener(itemclick);//点击
        mDraggableGridViewPager.setOnItemLongClickListener(itemlongclick);
        return view;
    }
    @Override
    public void onResume(){
        super.onResume();
        getActivity().registerReceiver(mGattUpdateReceiver, makeUpdateIntentFilter());
    }
    @Override
    public void onPause(){
        super.onPause();
        getActivity().unregisterReceiver(mGattUpdateReceiver);
    }
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.main_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);

    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // TODO Auto-generated method stub
        switch(item.getItemId()){
            case R.id.room_add:
                maddingAlertViewExt = new AlertView("Enter Room info", null, "CANCEL",
                        null, new String[]{"SAVE"}, getActivity(), AlertView.Style.Alert, alterclick);
                ViewGroup addingextView = (ViewGroup) LayoutInflater.from(getActivity()).inflate(R.layout.setting_maininfo, null);
                final ImageView iconchoose = (ImageView) addingextView.findViewById(R.id.mainroomicon);
                name = (EditText) addingextView.findViewById(R.id.mainnameeditText);
                setmainbgcolor=(TextView)addingextView.findViewById(R.id.mainbgcolor);
                if(roomdata.size()<=0){
                    name.setText("room1");
                }else{
                    name.setText("room"+(roomdata.get(roomdata.size() - 1).room_id + 1));
                }
                iconchoose.setImageDrawable(getResources().getDrawable(getResourdIdByResourdName(getActivity(), roomicon)));
                settingicon=roomicon;
                iconchoose.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        miconAlertViewExt = new AlertView("Icon Selection", null, "CANCEL",
                                null, null, getActivity(), AlertView.Style.Alert, alterclick);
                        miconAlertViewExt.setCancelable(false);
                        ViewGroup exticonView = (ViewGroup) LayoutInflater.from(getActivity()).inflate(R.layout.mood_icon_select, null);
                        GridView icongrid = (GridView) exticonView.findViewById(R.id.gridView2);
                        icongrid.setAdapter(new RoomIconAdapter(getActivity()));
                        icongrid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                settingicon = iconarray.get(position);
                                iconchoose.setImageDrawable(getResources().getDrawable(getResourdIdByResourdName(getActivity(), iconarray.get(position))));
                                miconAlertViewExt.dismiss();
                            }
                        });
                        miconAlertViewExt.addExtView(exticonView);
                        miconAlertViewExt.show();
                    }
                });
                setmainbgcolor.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mpcolorAlertViewExt = new AlertView("Color Selection", null, "CANCEL",
                                null,  new String[]{"SAVE"}, getActivity(), AlertView.Style.Alert, alterclick);
                        mpcolorAlertViewExt.setCancelable(false);
                        ViewGroup extcolorView = (ViewGroup) LayoutInflater.from(getActivity()).inflate(R.layout.view_pickcolor, null);
                        picker = (ColorPicker) extcolorView.findViewById(R.id.view5);
                        SVBar svBar = (SVBar)extcolorView. findViewById(R.id.view7);
                        picker.addSVBar(svBar);
                        ColorDrawable dr = (ColorDrawable) setmainbgcolor.getBackground();
                        if(dr!=null){
                            int col_int = dr.getColor();
                            picker.setColor(col_int);
                        }
                        mpcolorAlertViewExt.addExtView(extcolorView);
                        mpcolorAlertViewExt.show();
                    }
                });
                maddingAlertViewExt.addExtView(addingextView);
                maddingAlertViewExt.show();
                break;
            case R.id.room_delete:
                if (mAdapter.getCount() > 0) {
                    showToast("pls click one item to delete");
                    deletechoose=true;
                }
                break;
            case R.id.search:
                startActivity(new Intent(getActivity(),NetDaviceListActivity.class));
                break;
            case R.id.main_setting:
                startActivity(new Intent(getActivity(),MainSettingActivity.class));
                break;
            case R.id.main_about:
                Intent about=new Intent(getActivity(),AboutActivity.class);
                startActivity(about);
                break;
            case R.id.main_exit:
                if(exitdialogshow){
                    mAlertView.dismiss();
                    exitdialogshow=false;
                }else{
                    mAlertView = new AlertView("Warning", "Are you sure to exit?", "CANCEL",
                            new String[]{"YES"}, null, getActivity(), AlertView.Style.Alert, exitclick);
                    mAlertView .setCancelable(false);
                    mAlertView .show();
                    exitdialogshow=true;
                }
                break;
            default:
                break;
        }

        return super.onOptionsItemSelected(item);
    }
    private void showToast(String msg) {
        Toast.makeText(getActivity(), msg, Toast.LENGTH_SHORT).show();
    }
    private final BroadcastReceiver mGattUpdateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
             if(MainActivity.ACTION_BACKPRESS.equals(action)){
                 if(exitdialogshow){
                     if (MusicNotification.manager != null) {
                         MusicNotification.manager.cancel(MusicNotification.NOTICE_ID_TYPE_0);
                     }
                     int currentVersion = android.os.Build.VERSION.SDK_INT;
                     if (currentVersion > android.os.Build.VERSION_CODES.ECLAIR_MR1) {
                         Intent startMain = new Intent(Intent.ACTION_MAIN);
                         startMain.addCategory(Intent.CATEGORY_HOME);
                         startMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                         startActivity(startMain);
                         System.exit(0);
                     } else {// android2.1
                         ActivityManager am = (ActivityManager)getActivity(). getSystemService(getActivity().ACTIVITY_SERVICE);
                         am.restartPackage(getActivity().getPackageName());
                     }
                 }else{
                     mAlertView = new AlertView("Warning", "Are you sure to exit?", "CANCEL",
                             new String[]{"YES"}, null, getActivity(), AlertView.Style.Alert, exitclick);
                     mAlertView .setCancelable(false);
                     mAlertView .show();
                     exitdialogshow=true;
                 }
            }
        }
    };
    public com.bigkoo.alertview.OnItemClickListener exitclick=new com.bigkoo.alertview.OnItemClickListener(){
        public void onItemClick(Object o,int position) {
            if(position==-1){
                mAlertView.dismiss();
                exitdialogshow=false;
            }else if(position==0){
                if (MusicNotification.manager != null) {
                    MusicNotification.manager.cancel(MusicNotification.NOTICE_ID_TYPE_0);
                }
                int currentVersion = android.os.Build.VERSION.SDK_INT;
                if (currentVersion > android.os.Build.VERSION_CODES.ECLAIR_MR1) {
                    Intent startMain = new Intent(Intent.ACTION_MAIN);
                    startMain.addCategory(Intent.CATEGORY_HOME);
                    startMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(startMain);
                    System.exit(0);
                } else {// android2.1
                    ActivityManager am = (ActivityManager) getActivity().getSystemService(getActivity().ACTIVITY_SERVICE);
                    am.restartPackage(getActivity().getPackageName());
                }
            }
        }
    };
    Runnable getroomrun=new Runnable() {
        @Override
        public void run() {
            //roomid.clear();
            if(roomdata.size()>0){
                roomdata.clear();
            }
            mAdapter.clear();
            try{
                roomdata=MainActivity.mgr.queryroom();
            }catch (Exception e){
                Toast.makeText(getActivity(), "your database is boarken", Toast.LENGTH_SHORT).show();
            }

            for(int i=0;i<roomdata.size();i++){
                mAdapter.add(roomdata.get(i).room_name,roomdata.get(i).room_icon,roomdata.get(i).room_icon_bg);
            }
            mDraggableGridViewPager.setAdapter(mAdapter);

            handle.removeCallbacks(getroomrun);
        }
    };
    public AdapterView.OnItemLongClickListener itemlongclick=new AdapterView.OnItemLongClickListener() {
        @Override
        public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
            showPopupMenu(view);
            return true;
        }
    };
    ColorPicker picker;
    TextView setmainbgcolor;
    EditText name;
    private void showPopupMenu(final View popview) {
        // View当前PopupMenu显示的相对View的位置
        PopupMenu popupMenu = new PopupMenu(getActivity(), popview);
        // menu布局
        popupMenu.getMenuInflater().inflate(R.menu.main_popup_menu, popupMenu.getMenu());

        // menu的item点击事件
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch(item.getItemId()){
                    case R.id.main_setting:
                        for(int i=0;i<roomdata.size();i++){
                            if(roomdata.get(i).room_name.equals(((TextView)popview.findViewById(R.id.textView12)).getText().toString().trim())){
                                settingid=i;
                                settingicon=roomdata.get(i).room_icon;
                                break;
                            }
                        }
                        msettingAlertViewExt = new AlertView("Settings", null, "CANCEL",
                                null, new String[]{"SAVE"}, getActivity(), AlertView.Style.Alert, alterclick);
                        ViewGroup extView = (ViewGroup) LayoutInflater.from(getActivity()).inflate(R.layout.setting_maininfo, null);
                        final ImageView iconchoose = (ImageView) extView.findViewById(R.id.mainroomicon);
                        name = (EditText) extView.findViewById(R.id.mainnameeditText);
                        setmainbgcolor=(TextView)extView.findViewById(R.id.mainbgcolor);
                        name.setText(roomdata.get(settingid).room_name);
                        setmainbgcolor.setBackgroundColor(ToColor(roomdata.get(settingid).room_icon_bg));
                        iconchoose.setImageDrawable(getResources().getDrawable(getResourdIdByResourdName(getActivity(), settingicon)));
                        iconchoose.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                miconAlertViewExt = new AlertView("Icon Selection", null, "CANCEL",
                                        null, null, getActivity(), AlertView.Style.Alert, alterclick);
                                miconAlertViewExt.setCancelable(false);
                                ViewGroup exticonView = (ViewGroup) LayoutInflater.from(getActivity()).inflate(R.layout.mood_icon_select, null);
                                GridView icongrid = (GridView) exticonView.findViewById(R.id.gridView2);
                                icongrid.setAdapter(new RoomIconAdapter(getActivity()));
                                icongrid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                    @Override
                                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                        settingicon = iconarray.get(position);
                                        iconchoose.setImageDrawable(getResources().getDrawable(getResourdIdByResourdName(getActivity(), iconarray.get(position))));
                                        miconAlertViewExt.dismiss();
                                    }
                                });
                                miconAlertViewExt.addExtView(exticonView);
                                miconAlertViewExt.show();
                            }
                        });
                        setmainbgcolor.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                mpcolorAlertViewExt = new AlertView("Color Selection", null, "CANCEL",
                                        null,  new String[]{"SAVE"}, getActivity(), AlertView.Style.Alert, alterclick);
                                mpcolorAlertViewExt.setCancelable(false);
                                ViewGroup extcolorView = (ViewGroup) LayoutInflater.from(getActivity()).inflate(R.layout.view_pickcolor, null);
                                picker = (ColorPicker) extcolorView.findViewById(R.id.view5);
                                SVBar svBar = (SVBar)extcolorView. findViewById(R.id.view7);
                                picker.addSVBar(svBar);
                                ColorDrawable dr = (ColorDrawable) setmainbgcolor.getBackground();
                                if(dr!=null){
                                    int col_int = dr.getColor();
                                    picker.setColor(col_int);
                                }
                                mpcolorAlertViewExt.addExtView(extcolorView);
                                mpcolorAlertViewExt.show();

                            }
                        });
                        msettingAlertViewExt.addExtView(extView);
                        msettingAlertViewExt.show();
                        break;
                }
                return false;

            }
        });
        popupMenu.show();
    }
    public com.bigkoo.alertview.OnItemClickListener alterclick=new com.bigkoo.alertview.OnItemClickListener(){
        public void onItemClick(Object o,int position) {
            if(o==mpcolorAlertViewExt){
                if(position==-1){
                    mpcolorAlertViewExt.dismiss();
                }else if(position==0){
                    setmainbgcolor.setBackgroundColor(picker.getColor());
                }
            }else if(o==miconAlertViewExt){
                if(position==-1){
                    miconAlertViewExt.dismiss();
                }
            }else if(o==msettingAlertViewExt){
                if(position==-1){
                    msettingAlertViewExt.dismiss();
                }else if(position==0){
                    ColorDrawable dr = (ColorDrawable) setmainbgcolor.getBackground();
                    int col_int = dr.getColor();
                    Saveroom updateroom = new Saveroom();
                    updateroom.room_id = roomdata.get(settingid).room_id;
                    updateroom.room_name = name.getText().toString().trim();
                    updateroom.room_icon = settingicon;
                    updateroom.room_icon_bg = Integer.toHexString(col_int);
                    MainActivity.mgr.updataroominfo(updateroom);
                    handle.postDelayed(getroomrun, 20);
                }
            }else if(o==maddingAlertViewExt){
                if(position==-1){
                    maddingAlertViewExt.dismiss();
                }else if(position==0){
                    String x = name.getText().toString().trim();
                    // mAdapter.add(x);
                    ArrayList<Saveroom> tips = new ArrayList<Saveroom>();
                    int room_id;
                    if (roomdata.size() == 0) {
                        room_id = 1;
                    } else {
                        room_id = roomdata.get(roomdata.size() - 1).room_id + 1;
                    }
                    ColorDrawable dr = (ColorDrawable) setmainbgcolor.getBackground();
                    int tvcolor = dr.getColor();
                    //Toast.makeText(MainActivity.this, Integer.toHexString(tvcolor), Toast.LENGTH_SHORT).show();
                    Saveroom savenews = new Saveroom(room_id, x, 0, 0, 0, 0, 0, 0, settingicon, Integer.toHexString(tvcolor),0,0,0);
                    tips.add(savenews);
                    MainActivity.mgr.addroom(tips);
                    handle.postDelayed(getroomrun, 20);
                }
            }

        }
    };

    public AdapterView.OnItemClickListener itemclick=new AdapterView.OnItemClickListener(){
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            if (deletechoose) {
                int select=0;
                for(int i=0;i<roomdata.size();i++){
                    if(((TextView)view.findViewById(R.id.textView12)).getText().toString().trim().equals(roomdata.get(i).room_name)){
                        select=i;
                        break;
                    }
                }
                //mAdapter.remove(mAdapter.getItem(position));
                MainActivity.mgr.deletefounction("room", roomdata.get(select).room_id);
                MainActivity.mgr.deletefc("light", roomdata.get(select).room_id);
                MainActivity.mgr.deletefc("hvac", roomdata.get(select).room_id);
                MainActivity.mgr.deletefc("mood", roomdata.get(select).room_id);
                MainActivity.mgr.deletefc("moodbutton", roomdata.get(select).room_id);
                //mgr.deletefc("fan", roomdata.get(select).room_id);
                MainActivity.mgr.deletefc("curtain", roomdata.get(select).room_id);
                MainActivity.mgr.deletefc("music", roomdata.get(select).room_id);
                MainActivity.mgr.deletefc("song", roomdata.get(select).room_id);
                MainActivity.mgr.deletefc("radio", roomdata.get(select).room_id);
                deletechoose = false;
                handle.postDelayed(getroomrun, 20);
            } else {
                int select=0;
                for(int i=0;i<roomdata.size();i++){
                    if(((TextView)view.findViewById(R.id.textView12)).getText().toString().trim().equals(roomdata.get(i).room_name)){
                        select=i;
                        break;
                    }
                }
                Intent gotoroom = new Intent();
                gotoroom.putExtra("roomname", roomdata.get(select).room_name);
                gotoroom.putExtra("roomid", roomdata.get(select).room_id);
                gotoroom.setClass(getActivity(), RoomActivity.class);
                startActivity(gotoroom);
                //showToast(new String(Integer.toString(position)));
            }

        }
    };

    public static int getResourdIdByResourdName(Context context, String ResName){
        int resourceId = 0;
        try {
            Field field = R.mipmap.class.getField(ResName);
            field.setAccessible(true);

            try {
                resourceId = field.getInt(null);
            } catch (IllegalArgumentException e) {
                // log.showLogDebug("IllegalArgumentException:" + e.toString());
            } catch (IllegalAccessException e) {
                // log.showLogDebug("IllegalAccessException:" + e.toString());
            }
        } catch (NoSuchFieldException e) {
            //log.showLogDebug("NoSuchFieldException:" + e.toString());
        }
        return resourceId;
    }

    public int ToColor(String data){
        int color=0;
        int rin,gin,bin,ain;
        ain=Integer.parseInt(data.substring(0,2),16);
        rin=Integer.parseInt(data.substring(2,4),16);
        gin=Integer.parseInt(data.substring(4,6),16);
        bin=Integer.parseInt(data.substring(6,8),16);
        color= Color.argb(ain, rin, gin, bin);
        return color;
    }

    private static IntentFilter makeUpdateIntentFilter() {
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.setPriority(2147483647);
        intentFilter.addAction(MainActivity.ACTION_BACKPRESS);
        return intentFilter;
    }
}
