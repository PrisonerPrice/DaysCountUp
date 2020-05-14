package com.prisonerprice.dayscountup.firestore;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteConstraintException;
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
import com.prisonerprice.dayscountup.database.AppDatabase;
import com.prisonerprice.dayscountup.database.Task;
import com.prisonerprice.dayscountup.middleware.AppExecutors;
import com.prisonerprice.dayscountup.view.LoginActivity;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class FireStoreDB {

    private final static String TAG = FireStoreDB.class.getSimpleName();

    private static FireStoreDB fireStoreDB;
    private FirebaseFirestore db;
    private String uid;
    private SharedPreferences sharedPreferences;

    private FireStoreDB(Context context) {
        db = FirebaseFirestore.getInstance();
        sharedPreferences = context.getSharedPreferences("USER", Context.MODE_PRIVATE);
        uid = sharedPreferences.getString("USER", "NO_USER");
    }

    public static FireStoreDB getInstance(Context context) {
        if (fireStoreDB == null) {
            fireStoreDB = new FireStoreDB(context);
        }
        return fireStoreDB;
    }

    public void uploadTasks(List<Task> tasks) {
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
        if (!uid.equals("NO_USER")) {
            db.collection("users").document(uid)
                    .set(firebaseTask)
                    .addOnSuccessListener(documentReference ->
                            Log.d(TAG, "DocumentSnapshot successfully written!"))
                    .addOnFailureListener(e ->
                            Log.w(TAG, "Error writing document", e));
        }
    }

    public void fetchDataFromCloudAndSaveInLocal(AppDatabase appDatabase, AppExecutors appExecutors) {
        List<Task> tasks = new ArrayList<>();
        String uid = LoginActivity.getCurrentUser().getUid();
        Log.d(TAG, "currUser is: " + uid);

        DocumentReference docRef = db.collection("users").document(uid);

        Source source = Source.SERVER;

        docRef.get(source).addOnSuccessListener(documentSnapshot -> {
            FirebaseTask firebaseTask = documentSnapshot.toObject(FirebaseTask.class);
            for(Map<String, Object> map : firebaseTask.getTasks()) {
                tasks.add(new Task(
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
            Log.d(TAG, "Tasks size is: " + tasks.size());
            appExecutors.getDiskIO().execute(() -> {
                for (Task task : tasks) {
                    try {
                        appDatabase.taskDao().insertTask(task);
                    }
                    catch(SQLiteConstraintException e) {
                        e.printStackTrace();
                    }
                }
            });
        });

    }

}
