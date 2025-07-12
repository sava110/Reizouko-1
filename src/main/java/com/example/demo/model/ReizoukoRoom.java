package com.example.demo.model;

import java.util.List;

public class ReizoukoRoom {
    private int id;
    private String roomName;
    private List<Shokuhin> foods;

    public ReizoukoRoom(int id, String roomName, List<Shokuhin> foods) {
        this.id = id;
        this.roomName = roomName;
        this.foods = foods;
    }

    public int getId() { return id; }
    public String getRoomName() { return roomName; }
    public List<Shokuhin> getFoods() { return foods; }
}
