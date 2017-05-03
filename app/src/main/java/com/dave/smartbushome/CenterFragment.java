package com.dave.smartbushome;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;

import com.dave.smartbushome.assist.Adapter.CentralControlAdapter;
import com.dave.smartbushome.centercontrol.AllCurtainActivity;
import com.dave.smartbushome.centercontrol.AllFanActivity;
import com.dave.smartbushome.centercontrol.AllHVACActivity;
import com.dave.smartbushome.centercontrol.AllLightActivity;
import com.dave.smartbushome.centercontrol.AllMusicActivity;
import com.dave.smartbushome.centercontrol.AllOtherActivity;
import com.dave.smartbushome.centercontrol.EnergyActivity;
import com.dave.smartbushome.centercontrol.MarcoActivity;
import com.dave.smartbushome.centercontrol.NFCActivity;
import com.dave.smartbushome.centercontrol.ScheduleActivity;
import com.dave.smartbushome.centercontrol.Security;
import com.dave.smartbushome.centercontrol.StatusActivity;


/**
 * A simple {@link Fragment} subclass.
 */
public class CenterFragment extends Fragment {

    private GridView centralcontrol;
    View view;
    public CenterFragment() {
        // Required empty public constructor
    }

    public static CenterFragment newInstance(String param1) {
        CenterFragment fragment = new CenterFragment();
        return fragment;
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view=inflater.inflate(R.layout.fragment_center, container, false);
        centralcontrol=(GridView)view.findViewById(R.id.centercontrol);
        centralcontrol.setAdapter(new CentralControlAdapter(getActivity()));
        centralcontrol.setOnItemClickListener(centralitemclick);
        return view;
    }
    public AdapterView.OnItemClickListener centralitemclick=new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            switch (position){
                case 0:
                    startActivity(new Intent(getActivity(), MarcoActivity.class));
                    break;
                case 1:
                    startActivity(new Intent(getActivity(), AllLightActivity.class));
                    break;
                case 2:
                    startActivity(new Intent(getActivity(), AllHVACActivity.class));
                    break;
                case 3:
                    startActivity(new Intent(getActivity(), AllMusicActivity.class));
                    break;
                case 4:
                    startActivity(new Intent(getActivity(), AllCurtainActivity.class));
                    break;
                case 5:
                    startActivity(new Intent(getActivity(), AllOtherActivity.class));
                    break;
                case 6:
                    startActivity(new Intent(getActivity(), AllFanActivity.class));
                    break;
                case 7:
                    startActivity(new Intent(getActivity(), Security.class));
                    break;
                case 8:
                    startActivity(new Intent(getActivity(), EnergyActivity.class));
                    break;
                case 9:
                    startActivity(new Intent(getActivity(), StatusActivity.class));
                    break;
                case 10:
                    startActivity(new Intent(getActivity(),ScheduleActivity.class));
                    break;
                case 11:
                    startActivity(new Intent(getActivity(), NFCActivity.class));
                    break;
            }
        }
    };
}
