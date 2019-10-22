package com.example.unifriends.events;

import java.util.ArrayList;

class HomeCollection {
    public String date;
    public String name;
    public String subject;
    public String description;
    public String time;


    public static ArrayList<HomeCollection> date_collection_arr;
    public HomeCollection(String date, String name, String subject, String description, String time){

        this.date=date;
        this.name=name;
        this.subject=subject;
        this.description= description;
        this.time = time;

    }
}
