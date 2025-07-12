package com.example.demo.model;

import java.time.LocalDate;

public class Shokuhin {
	private static int idCounter = 0;
	private int id;
    private String name;
    private String genre;
    private int amount;     // ← 数量（数値）
    private String unit;       // ← 単位（個、本、gなど）
    private String expiryDate;

    public Shokuhin() {
    	this.id = idCounter++;
    }

    public Shokuhin(String name, String genre, int amount, String unit, String expiryDate) {
        this.id = idCounter++;
        this.name = name;
        this.genre = genre;
        this.amount = amount;
        this.unit = unit;
        this.expiryDate = expiryDate;
    }
    
    public int getId() {
        return id;
    }

    // getter & setter
    public String getName() { return name; }
    public String getGenre() { return genre; }
    public int getAmount() { return amount; }
    public String getUnit() { return unit; }
    public String getExpiryDate() { return expiryDate; }

    public void setName(String name) { this.name = name; }
    public void setGenre(String genre) { this.genre = genre; }
    public void setAmount(int amount) { this.amount = amount; }
    public void setUnit(String unit) { this.unit = unit; }
    public void setExpiryDate(String expiryDate) { this.expiryDate = expiryDate; }

    public boolean isExpireToday() {
        try {
            return LocalDate.now().toString().equals(this.expiryDate);
        } catch (Exception e) {
            return false;
        }
    }
}
