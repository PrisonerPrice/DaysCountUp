package com.prisonerprice.dayscountup.firestore;

import java.util.List;
import java.util.Map;

public class FirebaseTask {
    private String uid;
    private List<Map<String, Object>> tasks;

    public FirebaseTask() {}

    public FirebaseTask(String uid, List<Map<String, Object>> tasks) {
        this.uid = uid;
        this.tasks = tasks;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public List<Map<String, Object>> getTasks() {
        return tasks;
    }

    public void setTasks(List<Map<String, Object>> tasks) {
        this.tasks = tasks;
    }
}