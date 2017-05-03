package com.dave.smartbushome.database;

/**
 * Created by Administrator on 16-6-16.
 */
public class Savemusic {
    public int room_id,music_id,deviceID,subnetID;
    public Savemusic(){

    }

    public Savemusic(int room_id,int music_id,int subnetID,int deviceID){
        this.room_id=room_id;
        this.music_id=music_id;
        this.deviceID=deviceID;
        this.subnetID=subnetID;
    }
}
