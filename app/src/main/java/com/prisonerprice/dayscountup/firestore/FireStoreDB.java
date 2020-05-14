package com.prisonerprice.dayscountup.firestore;

import android.util.Log;
import android.widget.TextView;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Source;
import com.prisonerprice.dayscountup.database.Task;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class FireStoreDB {

    private static FireStoreDB fireStoreDB;
    private final static String TAG = FireStoreDB.class.getSimpleName();
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private static MutableLiveData<List<Task>> tasks = new MutableLiveData<>();

    public static FireStoreDB getInstance() {
        if (fireStoreDB == null) {
            fireStoreDB = new FireStoreDB();
            tasks.setValue(new ArrayList<>());
        }
        return fireStoreDB;
    }

    public void uploadTasks(FirebaseUser firebaseUser, List<Task> tasks) {
        String uid = firebaseUser.getUid();
        List<Map<String, Object>> list = new ArrayList<>();
        for(Task task : tasks) {
            Map<String, Object> record = new HashMap<>();
            record.put("ID", task.getId());
            record.put("updatedAt", task.getUpdatedAt().getTime());
            record.put("description",task.getDescription());
            record.put("celebrate100Days", task.getCelebrate100Days());
            record.put("celebrateAnniversary", task.getCelebrateAnniversary());
            record.put("customCelebrateDay", task.getCustomCelebrateDay());
            record.put("allowNotification", task.getAllowNotification());
            record.put("iconID", task.getIconID());
            record.put("timestamp", task.getTimestamp());
            list.add(record);
        }
        FirebaseTask firebaseTask = new FirebaseTask(uid, list);
        db.collection("users").document(uid)
                .set(firebaseTask)
                .addOnSuccessListener(documentReference ->
                        Log.d(TAG, "DocumentSnapshot successfully written!"))
                .addOnFailureListener(e ->
                        Log.w(TAG, "Error writing document", e));
    }

    public List<Task> downloadTasks(FirebaseUser firebaseUser) {
        List<Task> t = new ArrayList<>();
        String uid = firebaseUser.getUid();
        DocumentReference docRef = db.collection("users").document(uid);

        Source source = Source.SERVER;

        docRef.get(source).addOnSuccessListener(documentSnapshot -> {
            FirebaseTask firebaseTask = documentSnapshot.toObject(FirebaseTask.class);
            for(Map<String, Object> map : firebaseTask.getTasks()) {
                t.add(new Task(
                        (int) (long) map.get("ID"),
                        (String) map.get("description"),
                        new Date((Long) map.get("updatedAt")),
                        (int) (long) map.get("iconID"),
                        (int) (long) map.get("allowNotification"),
                        (long) map.get("timestamp"),
                        (int) (long) map.get("celebrate100Days"),
                        (int) (long) map.get("celebrateAnniversary"),
                        (int) (long) map.get("customCelebrateDay"))
                );
            }
        });
        return t;
    }

    public void downloadTasks2(FirebaseUser firebaseUser, TextView tv) {
        List<Task> t = new ArrayList<>();
        String uid = firebaseUser.getUid();
        DocumentReference docRef = db.collection("users").document(uid);

        Source source = Source.SERVER;

        docRef.get(source).addOnSuccessListener(documentSnapshot -> {
            FirebaseTask firebaseTask = documentSnapshot.toObject(FirebaseTask.class);
            for(Map<String, Object> map : firebaseTask.getTasks()) {
                t.add(new Task(
                        (int) (long) map.get("ID"),
                        (String) map.get("description"),
                        new Date((Long) map.get("updatedAt")),
                        (int) (long) map.get("iconID"),
                        (int) (long) map.get("allowNotification"),
                        (long) map.get("timestamp"),
                        (int) (long) map.get("celebrate100Days"),
                        (int) (long) map.get("celebrateAnniversary"),
                        (int) (long) map.get("customCelebrateDay"))
                );
            }
            String output = "default text";
            for (com.prisonerprice.dayscountup.database.Task task : t) {
                output += (task.toString() + "\n");
            }
            tv.setText(output);
        });
    }
}
