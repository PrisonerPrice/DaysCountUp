package com.prisonerprice.dayscountup.database;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import java.util.ArrayList;
import java.util.Date;
import java.util.Objects;

@Entity(tableName = "tasks")
public class Task {

    @PrimaryKey(autoGenerate = true)
    private int id;

    private String description;

    @ColumnInfo(name = "updated_at")
    private Date updatedAt;

    @ColumnInfo(name = "icon_id")
    private int iconID;

    @ColumnInfo(name = "allow_notification")
    private int allowNotification;

    private Long timestamp;

    @ColumnInfo(name = "celebrate_100_days")
    private int celebrate100Days;

    @ColumnInfo(name = "celebrate_anniversary")
    private int celebrateAnniversary;

    @ColumnInfo(name = "custom_celebrate_day")
    private int customCelebrateDay;

    @Ignore
    public Task(String description, Date updatedAt, int iconID, int allowNotification, Long timestamp, int celebrate100Days, int celebrateAnniversary, int customCelebrateDay) {
        this.description = description;
        this.updatedAt = updatedAt;
        this.iconID = iconID;
        this.allowNotification = allowNotification;
        this.timestamp = timestamp;
        this.celebrate100Days = celebrate100Days;
        this.celebrateAnniversary = celebrateAnniversary;
        this.customCelebrateDay = customCelebrateDay;
    }

    public Task(int id, String description, Date updatedAt, int iconID, int allowNotification, Long timestamp, int celebrate100Days, int celebrateAnniversary, int customCelebrateDay) {
        this.id = id;
        this.description = description;
        this.updatedAt = updatedAt;
        this.iconID = iconID;
        this.allowNotification = allowNotification;
        this.timestamp = timestamp;
        this.celebrate100Days = celebrate100Days;
        this.celebrateAnniversary = celebrateAnniversary;
        this.customCelebrateDay = customCelebrateDay;
    }

    @Ignore
    public Task(Task task) {
        if (task != null) {
            this.id = task.id;
            this.description = task.description;
            this.updatedAt = task.updatedAt;
            this.iconID = task.iconID;
            this.allowNotification = task.allowNotification;
            this.timestamp = task.timestamp;
            this.celebrate100Days = task.celebrate100Days;
            this.celebrateAnniversary = task.celebrateAnniversary;
            this.customCelebrateDay = task.customCelebrateDay;
        }
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

    public int getIconID() {
        return iconID;
    }

    public void setIconID(int iconID) {
        this.iconID = iconID;
    }

    public int getAllowNotification() {
        return allowNotification;
    }

    public void setAllowNotification(int allowNotification) {
        this.allowNotification = allowNotification;
    }

    public Long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }

    public int getCelebrate100Days() {
        return celebrate100Days;
    }

    public void setCelebrate100Days(int celebrate100Days) {
        this.celebrate100Days = celebrate100Days;
    }

    public int getCelebrateAnniversary() {
        return celebrateAnniversary;
    }

    public void setCelebrateAnniversary(int celebrateAnniversary) {
        this.celebrateAnniversary = celebrateAnniversary;
    }

    public int getCustomCelebrateDay() {
        return customCelebrateDay;
    }

    public void setCustomCelebrateDay(int customCelebrateDay) {
        this.customCelebrateDay = customCelebrateDay;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Task task = (Task) o;
        return id == task.id &&
                iconID == task.iconID &&
                allowNotification == task.allowNotification &&
                celebrate100Days == task.celebrate100Days &&
                celebrateAnniversary == task.celebrateAnniversary &&
                customCelebrateDay == task.customCelebrateDay &&
                Objects.equals(description, task.description) &&
                Objects.equals(updatedAt, task.updatedAt) &&
                Objects.equals(timestamp, task.timestamp);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, description, updatedAt, iconID, allowNotification, timestamp, celebrate100Days, celebrateAnniversary, customCelebrateDay);
    }

    @Override
    public String toString() {
        return "Task{" +
                "id=" + id +
                ", description='" + description + '\'' +
                ", updatedAt=" + updatedAt +
                ", iconID=" + iconID +
                ", allowNotification=" + allowNotification +
                ", timestamp=" + timestamp +
                ", celebrate100Days=" + celebrate100Days +
                ", celebrateAnniversary=" + celebrateAnniversary +
                ", customCelebrateDay=" + customCelebrateDay +
                '}';
    }
}
