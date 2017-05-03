package com.dave.smartbushome.assist.Adapter;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.StateListDrawable;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.dave.smartbushome.R;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Administrator on 2016/9/8.
 */
public class RoomAdapter extends BaseAdapter{
    private Context context;
    private List<String> stringarray=new ArrayList<String>();
    private List<String> iconarray=new ArrayList<String>();
    private List<String> iconbgarray=new ArrayList<String>();
    private int size;
    private LayoutInflater inflater=null;
    private HashMap<Integer, View> mView ;

    public RoomAdapter(final Context context){
        this.context = context;
        size=0;
        inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mView = new HashMap<Integer, View>();
    }

    public void clear(){
        stringarray.clear();
        iconarray.clear();
        iconbgarray.clear();
        mView.clear();
        size=0;
    }
    public void add(String str,String icon,String icon_bg){
        stringarray.add(str);
        iconarray.add(icon);
        iconbgarray.add(icon_bg);
        size++;
    }
    @Override
    public int getCount() {
        return size;
    }

    @Override
    public Object getItem(int position) {
        return stringarray.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub
        View view = mView.get(position);
        if(view==null)
        {
            view = inflater.inflate(R.layout.adapter_draggable_grid_item, null);
            TextView txt = (TextView)view.findViewById(R.id.textView12);
            ImageView ceb = (ImageView)view.findViewById(R.id.imageView8);
            txt.setText(stringarray.get(position));
            ceb.setImageDrawable(context.getResources().getDrawable(getResourdIdByResourdName(context, iconarray.get(position))));

            if(Build.VERSION.SDK_INT >= 16){
                setdrawable(view,position);
            }else{
                view.setBackgroundColor(ToColor(iconbgarray.get(position)));
            }

            mView.put(position, view);
        }
        return view;
    }

    public void setdrawable(View v,int position){
        Drawable backgroundDrawable = context.getResources().getDrawable(R.drawable.mainitem);
        StateListDrawable sld = (StateListDrawable) backgroundDrawable;// 通过向下转型，转回原型，selector对应的Java类为：StateListDrawable

        Drawable.ConstantState cs = sld.getConstantState();

        try {
            Method method = cs.getClass().getMethod("getChildren", new Class[ 0 ]);// 通过反射调用getChildren方法获取xml文件中写的drawable数组
            method.setAccessible(true);
            Object obj = method.invoke(cs, new  Object[ 0 ]);
            Drawable[] drawables = (Drawable[]) obj;

            for (int i = 0; i < drawables.length; i++) {
                // 接下来我们要通过遍历的方式对每个drawable对象进行修改颜色值
                GradientDrawable gd = (GradientDrawable) drawables[i];
                if (gd == null) {
                    break;
                }
                if (i == 0) {
                    // 我们对按下的状态做浅色处理
                    gd.setColor(ToColor(iconbgarray.get(position)));
                } else {
                    // 对默认状态做深色处理
                    gd.setColor(ToColor(iconbgarray.get(position)));
                }
            }
            // 最后总结一下，为了实现这个效果，刚开始并没有看到setColor的方法，而是通过反射获取GradientDrawable对象的属性GradientState，
            // 再通过反射调用GradientState对象的setSolidColor方法去实现，效果不太理想。
            // 最后在仔仔细细一一看GradientDrawable对象的属性，发现属性Paint
            // mFillPaint，从名字就可以看出这个对象是用来绘制drawable的背景的，
            // 于是顺着往下找，发现setColor方法，于是bingo，这个过程也是挺曲折的。
            v.setBackground(backgroundDrawable);
        } catch (NoSuchMethodException e1) {
            e1.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
    }
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
}
