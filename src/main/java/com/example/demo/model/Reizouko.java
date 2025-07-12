package com.example.demo.model;

import java.util.List;

public class Reizouko {
    private int id;
    private String name;
    private List<ReizoukoRoom> rooms;

    public Reizouko(int id, String name, List<ReizoukoRoom> rooms) {
        this.id = id;
        this.name = name;
        this.rooms = rooms;
    }

    public int getId() { return id; }
    public String getName() { return name; }
    public List<ReizoukoRoom> getRooms() { return rooms; }
}
