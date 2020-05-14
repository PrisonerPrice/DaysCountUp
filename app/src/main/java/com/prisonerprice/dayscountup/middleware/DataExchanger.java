package com.prisonerprice.dayscountup.middleware;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.widget.TextView;

import androidx.lifecycle.LiveData;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseUser;
import com.prisonerprice.dayscountup.database.AppDatabase;
import com.prisonerprice.dayscountup.database.Task;
import com.prisonerprice.dayscountup.firestore.FireStoreDB;
import com.prisonerprice.dayscountup.view.TaskAdapter;

import java.util.List;

public class DataExchanger {

    private static final String LOG_TAG = DataExchanger.class.getSimpleName();
    private static final Object LOCK = new Object();

    private final AppDatabase appDatabase;
    private final AppExecutors appExecutors = AppExecutors.getInstance();
    private static DataExchanger dataExchanger;
    private static FireStoreDB fireStoreDB = FireStoreDB.getInstance();

    private final static int DEFAULT_TASK_ID = -1;

    public static DataExchanger getInstance(Context context) {
        if (dataExchanger == null) {
            synchronized (LOCK) {
                dataExchanger = new DataExchanger(context);
            }
        }
        return dataExchanger;
    }

    private DataExchanger(Context context) {
        this.appDatabase = AppDatabase.getInstance(context);
    }

    public LiveData<Task> getTaskById(int id) {
        return appDatabase.taskDao().getTaskById(id);
    }

    public LiveData<List<Task>> getTaskList() {
        return appDatabase.taskDao().getTaskList();
    }

    public void deleteTask(final RecyclerView.ViewHolder viewHolder, final TaskAdapter mAdapter) {
        appExecutors.getDiskIO().execute(() -> {
            int position = viewHolder.getAdapterPosition();
            List<Task> tasks = mAdapter.getTasks();
            appDatabase.taskDao().deleteTask(tasks.get(position));
        });
    }

    public void insertTask(Task task) {
        appExecutors.getDiskIO().execute(() -> {
            appDatabase.taskDao().insertTask(task);
        });
    }

    public void insertOrUpdateTask(Activity activity, Task task, int mTaskId) {
        appExecutors.getDiskIO().execute(() -> {
            if(mTaskId == DEFAULT_TASK_ID) {
                Log.d(LOG_TAG, "INSERT NEW TASK");
                appDatabase.taskDao().insertTask(task);
            } else {
                //task.setId(mTaskId);
                Log.d(LOG_TAG, "UPDATE TASK");
                appDatabase.taskDao().updateTask(task);
            }
            activity.finish();
        });
    }

    public void truncateDatabase() {
        appExecutors.getDiskIO().execute(() -> {
            appDatabase.taskDao().truncate();
        });
    }

    public void uploadDataToFirebase(FirebaseUser firebaseUser, List<Task> tasks) {
        fireStoreDB.uploadTasks(firebaseUser, tasks);
    }

    public List<Task> downloadDataFromFirebase(FirebaseUser firebaseUser) {
        return fireStoreDB.downloadTasks(firebaseUser);
    }

    public void downloadDataFromFirebase2(FirebaseUser firebaseUser, TextView tv) {
        fireStoreDB.downloadTasks2(firebaseUser, tv);
    }
}
