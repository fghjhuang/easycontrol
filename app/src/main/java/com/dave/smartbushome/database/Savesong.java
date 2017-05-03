package com.dave.smartbushome.database;

/**
 * Created by Administrator on 16-6-27.
 */
public class Savesong {
    public int room_id,album_num,song_num,like;
    public String album_name,song_name;
    public Savesong(){

    }

    public Savesong(int room_id,int album_num,String album_name
            ,int song_num,String song_name,int like){
        this.room_id=room_id;
        this.album_num=album_num;
        this.album_name=album_name;
        this.song_num=song_num;
        this.song_name=song_name;
        this.like=like;
    }
}
