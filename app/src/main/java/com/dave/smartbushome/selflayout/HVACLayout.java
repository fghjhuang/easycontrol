package com.dave.smartbushome.selflayout;

/**
 * Created by Administrator on 16-5-25.
 */
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.widget.RelativeLayout;
import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.Button;
import android.widget.TextView;

import com.dave.smartbushome.MainActivity;
import com.dave.smartbushome.R;
import com.dave.smartbushome.database.Savehvac;
import com.dave.smartbushome.founction_command.ACcontrol;

public class HVACLayout extends RelativeLayout implements View.OnClickListener{
    private Button on,off,autofan,highfan,mediumfan,
            lowfan,automode,coolmode,fanmode,heatmode,
            tempup,tempdowm;
    TextView fanstate,tempstate,modestate,uint,name,fanremark,moderemark;
    //ImageView fanimage,modeimage;
    private Savehvac thishvac=new Savehvac();
    Handler reflashuihandler=new Handler();
    ACcontrol acc;
    boolean powerstate=false;
    public byte CurrentMode=3,CoolTemp=20,HeatTemp=20,AutoTemp=20,CurrentFanMode=2,
    CurrentUnit=0;
    private byte[] fanArray,modeArray;
    //AC command type define
    private static final byte const_ac_cmd_type_onoff=3;
    private static final byte const_ac_cmd_type_set_cold_tmp=4;
    private static final byte const_ac_cmd_type_set_fan=5;
    private static final byte const_ac_cmd_type_set_mode=6;
    private static final byte const_ac_cmd_type_set_heat_tmp=7;
    private static final byte const_ac_cmd_type_set_auto_tmp=8;

    //fan speed
    private static final byte const_fan_speed_anto=0;
    private static final byte const_fan_speed_high=1;
    private static final byte const_fan_speed_medium=2;
    private static final byte const_fan_speed_low=3;

    //ac mode
    private static final byte const_mode_cool=0;
    private static final byte const_mode_heat=1;
    private static final byte const_mode_fan=2;
    private static final byte const_mode_auto=3;

    //ac temp min/max value
    private  byte const_cool_temp_min=0;
    private  byte const_cool_temp_max=30;
    private  byte const_heat_temp_min=20;
    private  byte const_heat_temp_max=30;
    private  byte const_auto_temp_min=0;
    private  byte const_auto_temp_max=30;

    public HVACLayout(Context context) {
        super(context);
        // 将自定义组合控件的布局渲染成View
        initview(context);
    }
    public HVACLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        initview(context);
    }
    public void initview(Context context){
        View view = View.inflate(context, R.layout.hvac_layout, this);
        on=(Button)view.findViewById(R.id.on);
        off=(Button)view.findViewById(R.id.off);
        autofan=(Button)view.findViewById(R.id.autofan);
        highfan=(Button)view.findViewById(R.id.highfan);
        mediumfan=(Button)view.findViewById(R.id.mediumfan);
        lowfan=(Button)view.findViewById(R.id.lowfan);
        automode=(Button)view.findViewById(R.id.automode);
        coolmode=(Button)view.findViewById(R.id.cool);
        fanmode=(Button)view.findViewById(R.id.fan);
        heatmode=(Button)view.findViewById(R.id.heat);
        tempup=(Button)view.findViewById(R.id.up);
        tempdowm=(Button)view.findViewById(R.id.down);

       // onoffstate=(TextView)view.findViewById(R.id.state);
        tempstate=(TextView)view.findViewById(R.id.temp);
        fanstate=(TextView)view.findViewById(R.id.fanstate);
        modestate=(TextView)view.findViewById(R.id.mode);
        uint=(TextView)view.findViewById(R.id.uint);
        name=(TextView)view.findViewById(R.id.hvacremark);
        fanremark=(TextView)view.findViewById(R.id.textView66);
        moderemark=(TextView)view.findViewById(R.id.textView68);
       // fanimage=(ImageView)view.findViewById(R.id.fanimg);
       // modeimage=(ImageView)view.findViewById(R.id.modeimg);
        on.setOnClickListener(this);
        off.setOnClickListener(this);
        autofan.setOnClickListener(this);
        highfan.setOnClickListener(this);
        mediumfan.setOnClickListener(this);
        lowfan.setOnClickListener(this);
        automode.setOnClickListener(this);
        coolmode.setOnClickListener(this);
        fanmode.setOnClickListener(this);
        heatmode.setOnClickListener(this);
        tempup.setOnClickListener(this);
        tempdowm.setOnClickListener(this);
        acc=new ACcontrol();

    }

    int reflashstate=0,uicount=0;
    boolean readCF=false,readTempRange=false,readCountfanAndMode=false,readcstate=false;
    Runnable reflashrun=new Runnable() {
        @Override
        public void run() {
            uicount++;
            if(uicount>50){
                reflashstate=0;
                uicount=0;
                readCF=false;
                readTempRange=false;
                readCountfanAndMode=false;
                readcstate=false;
                reflashuihandler.removeCallbacks(reflashrun);
            }else{
                switch(reflashstate){
                    case 0:
                        acc.ACReadCFFlag((byte) thishvac.subnetID, (byte) thishvac.deviceID, MainActivity.mydupsocket);
                        // reflashstate=1;
                        reflashuihandler.postDelayed(reflashrun,70);
                        break;
                    case 1:
                        acc.ACReadTempRange((byte) thishvac.subnetID, (byte) thishvac.deviceID,MainActivity.mydupsocket);
                        //reflashstate=2;
                        reflashuihandler.postDelayed(reflashrun,70);
                        break;
                    case 2:
                        acc.ACReadCountFanAndMode((byte) thishvac.subnetID, (byte) thishvac.deviceID,MainActivity.mydupsocket);
                        //reflashstate=3;
                        reflashuihandler.postDelayed(reflashrun,70);
                        break;
                    case 3:
                        acc.ACReadCState((byte) thishvac.subnetID, (byte) thishvac.deviceID,MainActivity.mydupsocket);
                        // reflashstate=4;
                        reflashuihandler.postDelayed(reflashrun, 70);
                        break;
                    case 4:
                        reflashstate=0;
                        uicount=0;
                        readCF=false;
                        readTempRange=false;
                        readCountfanAndMode=false;
                        readcstate=false;
                        reflashuihandler.removeCallbacks(reflashrun);
                        break;
                }
            }

        }
    };

    public void removetimer(){
        reflashuihandler.removeCallbacks(reflashrun);
    }
    public void ReflashUI(){
        reflashuihandler.postDelayed(reflashrun,0);
    }
    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.on:
                setvisable(VISIBLE);
                powerstate=true;
                SendCommandOfAC(const_ac_cmd_type_onoff, 1);
                //ReflashUI();
                //setOnoffstate("ON");
                break;
            case R.id.off:
                setvisable(GONE);
                powerstate=false;
                SendCommandOfAC(const_ac_cmd_type_onoff, 0);
                //setOnoffstate("OFF");
                break;
            case R.id.up:
                switch(CurrentMode){
                    case const_mode_cool:
                        CoolTemp++;
                        if(CoolTemp>=const_cool_temp_max){
                            CoolTemp=const_cool_temp_max;
                        }else if(CoolTemp<const_cool_temp_min){
                            CoolTemp=const_cool_temp_min;
                        }
                        SendCommandOfAC(const_ac_cmd_type_set_cold_tmp,CoolTemp);
                        setTempstate(String.valueOf(CoolTemp));
                        break;
                    case const_mode_heat:
                        HeatTemp++;
                        if(HeatTemp>=const_heat_temp_max){
                            HeatTemp=const_heat_temp_max;
                        }else if(HeatTemp<const_heat_temp_min){
                            HeatTemp=const_heat_temp_min;
                        }
                        SendCommandOfAC(const_ac_cmd_type_set_heat_tmp,HeatTemp);
                        setTempstate(String.valueOf(HeatTemp));
                        break;
                    case const_mode_auto:
                        AutoTemp++;
                        if(AutoTemp>=const_auto_temp_max){
                            AutoTemp=const_auto_temp_max;
                        }else if(AutoTemp<const_auto_temp_min){
                            AutoTemp=const_auto_temp_min;
                        }
                        SendCommandOfAC(const_ac_cmd_type_set_auto_tmp,AutoTemp);
                        setTempstate(String.valueOf(AutoTemp));
                        break;
                }
                break;
            case R.id.down:
                switch(CurrentMode){
                    case const_mode_cool:
                        CoolTemp--;
                        if(CoolTemp>=const_cool_temp_max){
                            CoolTemp=const_cool_temp_max;
                        }else if(CoolTemp<const_cool_temp_min){
                            CoolTemp=const_cool_temp_min;
                        }
                        SendCommandOfAC(const_ac_cmd_type_set_cold_tmp,CoolTemp);
                        setTempstate(String.valueOf(CoolTemp));
                        break;
                    case const_mode_heat:
                        HeatTemp--;
                        if(HeatTemp>=const_heat_temp_max){
                            HeatTemp=const_heat_temp_max;
                        }else if(HeatTemp<const_heat_temp_min){
                            HeatTemp=const_heat_temp_min;
                        }
                        SendCommandOfAC(const_ac_cmd_type_set_heat_tmp,HeatTemp);
                        setTempstate(String.valueOf(HeatTemp));
                        break;
                    case const_mode_auto:
                        AutoTemp--;
                        if(AutoTemp>=const_auto_temp_max){
                            AutoTemp=const_auto_temp_max;
                        }else if(AutoTemp<const_auto_temp_min){
                            AutoTemp=const_auto_temp_min;
                        }
                        SendCommandOfAC(const_ac_cmd_type_set_auto_tmp,AutoTemp);
                        setTempstate(String.valueOf(AutoTemp));
                        break;
                }
                break;
            case R.id.autofan:
                CurrentFanMode=const_fan_speed_anto;
                SendCommandOfAC(const_ac_cmd_type_set_fan,const_fan_speed_anto);
                setFanstate("AUTO");
                break;
            case R.id.highfan:
                CurrentFanMode=const_fan_speed_high;
                SendCommandOfAC(const_ac_cmd_type_set_fan,const_fan_speed_high);
                setFanstate("HIGH");
                break;
            case R.id.mediumfan:
                CurrentFanMode=const_fan_speed_medium;
                SendCommandOfAC(const_ac_cmd_type_set_fan,const_fan_speed_medium);
                setFanstate("MEDIUM");
                break;
            case R.id.lowfan:
                CurrentFanMode=const_fan_speed_low;
                SendCommandOfAC(const_ac_cmd_type_set_fan,const_fan_speed_low);
                setFanstate("LOW");
                break;
            case R.id.automode:
                CurrentMode=const_mode_auto;
                SendCommandOfAC(const_ac_cmd_type_set_mode,const_mode_auto);
                setModestate("AUTO");
                break;
            case R.id.cool:
                CurrentMode=const_mode_cool;
                SendCommandOfAC(const_ac_cmd_type_set_mode,const_mode_cool);
                setModestate("COOL");
                break;
            case R.id.fan:
                CurrentMode=const_mode_fan;
                SendCommandOfAC(const_ac_cmd_type_set_mode,const_mode_fan);
                setModestate("FAN");
                break;
            case R.id.heat:
                CurrentMode=const_mode_heat;
                SendCommandOfAC(const_ac_cmd_type_set_mode,const_mode_heat);
                setModestate("HEAT");
                break;
            default:break;
        }
    }
    public void setvisable(int vis){
        autofan.setVisibility(vis);
        highfan.setVisibility(vis);
        mediumfan.setVisibility(vis);
        lowfan.setVisibility(vis);
        automode.setVisibility(vis);
        coolmode.setVisibility(vis);
        fanmode.setVisibility(vis);
        tempup.setVisibility(vis);
        tempdowm.setVisibility(vis);
        heatmode.setVisibility(vis);
        tempstate.setVisibility(vis);
        uint.setVisibility(vis);
        fanstate.setVisibility(vis);
        modestate.setVisibility(vis);
        fanremark.setVisibility(vis);
        moderemark.setVisibility(vis);
       // fanimage.setVisibility(vis);
       // modeimage.setVisibility(vis);
    }
    /*public void setOnoffstate(String x){
        onoffstate.setText(x);
    }*/
    public void setTempstate(String x){
        tempstate.setText(x);
    }
    public void setFanstate(String x){
        fanstate.setText(x);
        //fanimage.setImageDrawable(img);
    }
    public void setModestate(String x){
        modestate.setText(x);
       // modeimage.setImageDrawable(img);
    }
    public void setcontant(Savehvac sh){
        thishvac=sh;
        name.setText("AC:  "+thishvac.hvac_remark);
        ReflashUI();
    }
    public int getsubnetid(){return thishvac.subnetID;}
    public int getdeviceid(){return thishvac.deviceID;}
    public void setReceiveChange(int command,byte value){
        switch (command){
            case 0:
                    setvisable(VISIBLE);
                    //setOnoffstate("ON");
                    powerstate=true;
                break;
            case 1:
                    setvisable(GONE);
                    //setOnoffstate("OFF");
                    powerstate=false;
                break;
            case 2:
                if(powerstate){
                    setFanstate("AUTO");
                }else{
                    setvisable(VISIBLE);
                    //setOnoffstate("ON");
                    setFanstate("AUTO");
                }
                break;
            case 3:
                if(powerstate){
                    setFanstate("HIGH");
                }else{
                    setvisable(VISIBLE);
                    //setOnoffstate("ON");
                    setFanstate("HIGH");
                }
                break;
            case 4:
                if(powerstate){
                    setFanstate("MEDIUM");
                }else{
                    setvisable(VISIBLE);
                    //setOnoffstate("ON");
                    setFanstate("MEDIUM");
                }
                break;
            case 5:
                if(powerstate){
                    setFanstate("LOW");
                }else{
                    setvisable(VISIBLE);
                    //setOnoffstate("ON");
                    setFanstate("LOW");
                }
                break;
            case 6:
                if(powerstate){
                    CurrentMode=const_mode_auto;
                    setTempstate(String.valueOf((AutoTemp&0xff)));
                    setModestate("AUTO");
                }else{
                    CurrentMode=const_mode_auto;
                    setvisable(VISIBLE);
                    //setOnoffstate("ON");
                    setTempstate(String.valueOf(AutoTemp&0xff));
                    setModestate("AUTO");
                }
                break;
            case 7:
                if(powerstate){
                    CurrentMode=const_mode_cool;
                    setTempstate(String.valueOf(CoolTemp&0xff));
                    setModestate("COOL");
                }else{
                    CurrentMode=const_mode_cool;
                    setvisable(VISIBLE);
                   // setOnoffstate("ON");
                    setTempstate(String.valueOf(CoolTemp&0xff));
                    setModestate("COOL");
                }
                break;
            case 8:
                if(powerstate){
                    CurrentMode=const_mode_heat;
                    setTempstate(String.valueOf(HeatTemp&0xff));
                    setModestate("HEAT");
                }else{
                    CurrentMode=const_mode_heat;
                    setvisable(VISIBLE);
                    //setOnoffstate("ON");
                    setTempstate(String.valueOf(HeatTemp&0xff));
                    setModestate("HEAT");
                }
                break;
            case 9:
                if(powerstate){
                    CurrentMode=const_mode_fan;
                    setModestate("FAN");
                }else{
                    CurrentMode=const_mode_fan;
                    setvisable(VISIBLE);
                   // setOnoffstate("ON");
                    setModestate("FAN");
                }
                break;
            case 10:
                if(powerstate){
                    switch(CurrentMode){
                        case const_mode_cool:
                            CoolTemp=value;
                            setTempstate(String.valueOf((int)(CoolTemp)&0xff));
                            break;
                        case const_mode_heat:
                            HeatTemp=value;
                            setTempstate(String.valueOf((int)(HeatTemp)&0xff));
                            break;
                        case const_mode_auto:
                            AutoTemp=value;
                            setTempstate(String.valueOf((int)(AutoTemp)&0xff));
                            break;
                    }
                }else{
                    setvisable(VISIBLE);
                   // setOnoffstate("ON");
                    switch(CurrentMode){
                        case const_mode_cool:
                            CoolTemp=value;
                            setTempstate(String.valueOf((int)(CoolTemp)&0xff));
                            break;
                        case const_mode_heat:
                            HeatTemp=value;
                            setTempstate(String.valueOf((int)(HeatTemp)&0xff));
                            break;
                        case const_mode_auto:
                            AutoTemp=value;
                            setTempstate(String.valueOf((int)(AutoTemp)&0xff));
                            break;
                    }
                }
                break;
                default:break;
        }
    }
    public void setCandF(byte value){
        if(!readCF){
            readCF=true;
            if(value==0){
                uint.setText("℃");
                CurrentUnit=0;
            }else if((value&0xff)==1){
                uint.setText("℉");
                CurrentUnit=1;
            }
            reflashstate=1;
        }
    }
    public void setACTempRange(byte[] value){
        if(!readTempRange){
            readTempRange=true;
            const_cool_temp_min=value[0];
            const_cool_temp_max=value[1];
            const_heat_temp_min=value[2];
            const_heat_temp_max=value[3];
            const_auto_temp_min=value[4];
            const_auto_temp_max=value[5];
            reflashstate=2;
        }
    }

    public void setFanSpeedAndModeCount(byte[] value){
        if(!readCountfanAndMode){
            readCountfanAndMode=true;
            fanArray=new byte[value[25]];
            for(int i=0;i<fanArray.length;i++){
                fanArray[i]=value[26+i];
            }
            int startmode=26+fanArray.length;
            for(int i=0;i<10;i++){
                if((value[startmode]>(byte)0x20)||(value[startmode]==(byte)0x00)){
                    startmode++;
                }else if(value[startmode]<(byte)0x06){
                    break;
                }
            }
            modeArray=new byte[value[startmode]];
            for(int i=0;i<modeArray.length;i++){
                modeArray[i]=value[startmode+1+i];
            }
            reflashstate=3;
        }

    }

    public void setACCurrentState(byte[] value){
        if(!readcstate){
            readcstate=true;
            if(value[25]==0){
                setvisable(GONE);
                //setOnoffstate("OFF");
                powerstate=false;
            }else{
                setvisable(VISIBLE);
                //setOnoffstate("ON");
                powerstate=true;
            }
            CoolTemp=value[26];
            HeatTemp=value[30];
            AutoTemp=value[32];
            if((value[27]&0x0f)==fanArray.length){
                CurrentFanMode=fanArray[(value[27]&0x0f)-1];
            }else{
                CurrentFanMode=fanArray[(value[27]&0x0f)];
            }

            switch(CurrentFanMode){
                case const_fan_speed_anto:
                    setFanstate("AUTO");
                    break;
                case const_fan_speed_high:
                    setFanstate("HIGH");
                    break;
                case const_fan_speed_medium:
                    setFanstate("MEDIUM");
                    break;
                case const_fan_speed_low:
                    setFanstate("LOW");
                    break;
                default:break;
            }
            if((value[27]>>4)==modeArray.length){
                CurrentMode=modeArray[(value[27]>>4)-1];
            }else{
                CurrentMode=modeArray[(value[27]>>4)];
            }

            switch(CurrentMode){
                case const_mode_auto:
                    setTempstate(String.valueOf(AutoTemp&0xff));
                    setModestate("AUTO");
                    break;
                case const_mode_cool:
                    setTempstate(String.valueOf(CoolTemp&0xff));
                    setModestate("COOL");
                    break;
                case const_mode_fan:
                    setModestate("FAN");
                    break;
                case const_mode_heat:
                    setTempstate(String.valueOf(HeatTemp&0xff));
                    setModestate("HEAT");
                    break;
                default:break;
            }
            //CurrentTemp=value[29];
            reflashstate=4;
        }

    }

    private boolean SendCommandOfAC(int intType,int intValue)
    {
        boolean blnSuccess=false;
        try
        {
            blnSuccess=acc.ACControl((byte)thishvac.subnetID, (byte)thishvac.deviceID, intType, intValue,MainActivity.mydupsocket);

        }catch(Exception e)
        {e.printStackTrace();}

        return blnSuccess;
    }
}
