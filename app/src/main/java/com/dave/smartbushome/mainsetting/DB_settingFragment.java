package com.dave.smartbushome.mainsetting;


import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.bigkoo.alertview.AlertView;
import com.dave.smartbushome.R;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class DB_settingFragment extends Fragment implements View.OnClickListener{

    Button bt_export,bt_import,bt_share,bt_delete;
    ProgressDialog searching;
    View view;
    public static DB_settingFragment newInstance() {
        DB_settingFragment pageFragment = new DB_settingFragment();
        return pageFragment;
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
    public DB_settingFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view=inflater.inflate(R.layout.fragment_db_setting_, container, false);
        bt_export=(Button)view.findViewById(R.id.ms_export);
        bt_import=(Button)view.findViewById(R.id.ms_import);
        bt_delete=(Button)view.findViewById(R.id.ms_delete);
        bt_share=(Button)view.findViewById(R.id.ms_share);
        bt_export.setOnClickListener(this);
        bt_import.setOnClickListener(this);
        bt_delete.setOnClickListener(this);
        bt_share.setOnClickListener(this);
        searching = new ProgressDialog(getContext());
        searching.setCancelable(true);
        searching.setCanceledOnTouchOutside(false);
        searching.setMessage("Searching Database...");
        searching.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        return view;
    }

    Handler closehandler=new Handler();
    Runnable run=new Runnable() {
        @Override
        public void run() {
            importalter.dismiss();
        }
    };
    AlertView importalter,importconfirmalter,importdeletealter,deletealter;
    String importpath="",deletepath="";
    public void onClick(View v){
        switch (v.getId()){
            case R.id.ms_export:
                SimpleDateFormat formatter    =   new SimpleDateFormat("yyyyMMdd_HHmmss");
                Date curDate    =   new    Date(System.currentTimeMillis());//获取当前时间
                String    str    =    formatter.format(curDate);
                String dbDir=android.os.Environment.getExternalStorageDirectory().getPath();
                dbDir += "/smartbus_backup/database";//数据库所在目录
                String database_source=android.os.Environment.getExternalStorageDirectory().getPath()+"/android/data/com.dave.smartbushome/database/sbus.db3";
               //String database_source=getActivity().getDatabasePath().getAbsolutePath();
                //String database_source=getActivity().getDatabasePath("com.dave.smartbushome").getPath() + "/database/sbus.db3" ;
                //Toast.makeText(getActivity(), database_source, Toast.LENGTH_SHORT).show();
                String target = android.os.Environment.getExternalStorageDirectory().getPath()+"/smartbus_backup/database/smartbus_dbbackup_"+str+".db3";//数据库路径
                //判断目录是否存在，不存在则创建该目录
                File dirFile = new File(dbDir);
                if(!dirFile.exists())
                    dirFile.mkdirs();
                copyFile(database_source, target);
                Toast.makeText(getActivity(), "export database to ../smartbus_backup/database/.. successfully", Toast.LENGTH_SHORT).show();

                break;
            case R.id.ms_import:
                if(searchresult.size()>0){searchresult.clear();}
                if(searchname.size()>0){searchname.clear();}
                new Thread(new scanfile()).start();
                searching.show();
                break;
            case R.id.ms_delete:
                deletealter = new AlertView("Warning", "Are you sure to delete the database ?", "CANCEL",
                        new String[]{"YES"}, null, getActivity(), AlertView.Style.Alert, exitclick);
                deletealter .setCancelable(false);
                deletealter .show();
                break;
            case R.id.ms_share:
                Toast.makeText(getActivity(), "still developing,please wait", Toast.LENGTH_SHORT).show();
                break;
        }
    }

    public com.bigkoo.alertview.OnItemClickListener exitclick=new com.bigkoo.alertview.OnItemClickListener(){
        public void onItemClick(Object o,int position) {
            if(position==-1){

            }else if(position==0){
                deleteFile();
            }
        }
    };

    public com.bigkoo.alertview.OnItemClickListener importclick=new com.bigkoo.alertview.OnItemClickListener(){
        public void onItemClick(Object o,int position) {
            if(position==-1){

            }else if(position==0){
                if(o==importdeletealter){
                    File file = new File(deletepath);
                    if (file.exists()) { // 判断文件是否存在
                        if (file.isFile()) { // 判断是否是文件
                            file.delete(); // delete()方法 你应该知道 是删除的意思;
                        }
                        file.delete();
                        Toast.makeText(getActivity(), "database delete succeed", Toast.LENGTH_SHORT).show();
                        closehandler.postDelayed(run,50);//貌似同时关闭会出错
                    } else {
                        Toast.makeText(getActivity(), "database not exist", Toast.LENGTH_SHORT).show();
                    }
                }else{
                    closehandler.postDelayed(run,50);//貌似同时关闭会出错
                    String targetpath=android.os.Environment.getExternalStorageDirectory().getPath()+"/android/data/com.dave.smartbushome/database/sbus.db3";
                    copyFile(importpath, targetpath);
                    Toast.makeText(getActivity(), "import succeed", Toast.LENGTH_SHORT).show();
                    Toast.makeText(getActivity(), "Please re-open this app again!!!", Toast.LENGTH_SHORT).show();
                }

            }
        }
    };

    public void copyFile(String oldPath, String newPath) {
        try {
            int bytesum = 0;
            int byteread = 0;
            File oldfile = new File(oldPath);
            if (oldfile.exists()) { //文件存在时
                InputStream inStream = new FileInputStream(oldPath); //读入原文件
                FileOutputStream fs = new FileOutputStream(newPath);
                byte[] buffer = new byte[1024];
                int length;
                while ( (byteread = inStream.read(buffer)) != -1) {
                    bytesum += byteread; //字节数 文件大小
                    System.out.println(bytesum);
                    fs.write(buffer, 0, byteread);
                }
                inStream.close();
            }
        }
        catch (Exception e) {
            System.out.println("复制单个文件操作出错");
            e.printStackTrace();

        }

    }
    public void deleteFile() {
        File file = new File(android.os.Environment.getExternalStorageDirectory().getPath()+"/android/data/com.dave.smartbushome/database/"+"sbus.db3");
        if (file.exists()) { // 判断文件是否存在
            if (file.isFile()) { // 判断是否是文件
                file.delete(); // delete()方法 你应该知道 是删除的意思;
            }
            file.delete();
            Toast.makeText(getActivity(), "database delete succeed", Toast.LENGTH_SHORT).show();
            getActivity().finish();
        } else {
            Toast.makeText(getActivity(), "database not exist", Toast.LENGTH_SHORT).show();
        }
    }
    public class scanfile implements Runnable {
        @Override
        public void run() {
            File file=new File(android.os.Environment.getExternalStorageDirectory().getPath());
            toSearchFiles(file);
            mHandler.obtainMessage(5, 1, -1, "finish")
                    .sendToTarget();
            searching.dismiss();
        }
    }
    List<HashMap<String,String>> searchresult=new ArrayList<>();
    List<String> searchname=new ArrayList<>();
    public void toSearchFiles(File file) {
        File[] files = file.listFiles();
        HashMap rowItem;

        for (File tf : files) {
            if (tf.isDirectory()) {
                if(tf.canRead()){
                    toSearchFiles(tf);  //如果是目录，递归查找
                }
            } else {
                try {
                    if (tf.getName().indexOf("smartbus_dbbackup")>-1) {

                        rowItem = new HashMap<String, String>();
                        rowItem.put("fileName", tf.getName());// 加入名称
                        rowItem.put("path", tf.getPath());  // 加入路径
                        searchresult.add(rowItem);
                    }
                } catch(Exception e) {
                    Toast.makeText(getActivity(), "error", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    private final Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            //Activity activity = getContext();
            switch (msg.what) {
                case 5:
                    for(int i=0;i<searchresult.size();i++){
                        searchname.add(searchresult.get(i).get("fileName"));
                    }
                    importalter = new AlertView("Database list", null, "CANCEL",  null, null, getActivity(), AlertView.Style.Alert,
                            null);
                    final ListView typelist=new ListView(getActivity());
                    typelist.setAdapter(new ArrayAdapter(getActivity(),
                            R.layout.simplelistitem, searchname));
                    typelist.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            importpath = searchresult.get(position).get("path");
                            importconfirmalter = new AlertView("Warning",
                                    "Are you sure to import " + searchname.get(position) + " as your database?", "CANCEL",
                                    new String[]{"YES"}, null, getActivity(), AlertView.Style.Alert, importclick);
                            importconfirmalter.setCancelable(false);
                            importconfirmalter.show();
                        }
                    });
                    typelist.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                        @Override
                        public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                            deletepath = searchresult.get(position).get("path");
                            importdeletealter = new AlertView("Warning",
                                    "Do you want to delete this file ? :" + searchname.get(position) , "CANCEL",
                                    new String[]{"YES"}, null, getActivity(), AlertView.Style.Alert, importclick);
                            importdeletealter.setCancelable(false);
                            //importdeletealter.setShoulddismiss(false);
                            importdeletealter.show();
                            return true;
                        }
                    });
                    importalter.addExtView(typelist);
                    importalter.show();
                    break;
            }
        }
    };
}
