package com.prisonerprice.dayscountup.database;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import java.util.ArrayList;
import java.util.Date;

@Entity(tableName = "tasks")
public class Task {

    @PrimaryKey(autoGenerate = true)
    private int id;

    private String description;

    @ColumnInfo(name = "updated_at")
    private Date updatedAt;

    @ColumnInfo(name = "remainderDates")
    private ArrayList<Date> remainderDates;

    @Ignore
    public Task(String description, Date updatedAt, ArrayList<Date> remainderDates) {
        this.description = description;
        this.updatedAt = updatedAt;
        this.remainderDates = remainderDates;
    }

    public Task(int id, String description, Date updatedAt, ArrayList<Date> remainderDates) {
        this.id = id;
        this.description = description;
        this.updatedAt = updatedAt;
        this.remainderDates = remainderDates;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }

    public ArrayList<Date> getRemainderDates() {
        return remainderDates;
    }

    public void setRemainderDates(ArrayList<Date> remainderDates) {
        this.remainderDates = remainderDates;
    }
}
