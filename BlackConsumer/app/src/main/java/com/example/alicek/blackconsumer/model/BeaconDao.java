package com.example.alicek.blackconsumer.model;

/**
 * Created by Kwak on 2016-06-28.
 */
public class BeaconDao {
    private String name;
    private String content;

    public BeaconDao(String name, String content){
        this.name = name;
        this.content = content;
    }

    public String getName(){
        return name;
    }

    public void setName(String name){
        this.name = name;
    }

    public String getContent(){
        return content;
    }

    public void setContent(String content){
        this.content = content;
    }
}
