package com.example.labGPS;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.example.labGPS.R;

public class Memories_List extends AppCompatActivity {

    private String T,Time,Description;
    private int Id;
    private byte[] Imageview;
    private double latitude;
    private double longitude;

    public Memories_List(String t, String time, String description, int id, byte[] imageview, double latitude, double longitude) {
        T = t;
        Time = time;
        Description = description;
        Id = id;
        Imageview = imageview;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public byte[] getImage() {

        return Imageview;
    }


    public int getId() {
        return Id;
    }

    public String getT() {
        return T;
    }

    public String getTime() {
        return Time;
    }

    public String getDescription() {
        return Description;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_memories__list);
    }
}
