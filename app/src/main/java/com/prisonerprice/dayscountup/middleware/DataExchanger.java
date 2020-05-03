package com.prisonerprice.dayscountup.middleware;

import android.app.Activity;
import android.content.Context;

import androidx.lifecycle.LiveData;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.PrimaryKey;

import com.prisonerprice.dayscountup.database.AppDatabase;
import com.prisonerprice.dayscountup.database.Task;
import com.prisonerprice.dayscountup.view.TaskAdapter;

import java.util.List;

public class DataExchanger {

    private static final Object LOCK = new Object();
    private final AppDatabase appDatabase;
    private final AppExecutors appExecutors = AppExecutors.getInstance();
    private static DataExchanger dataExchanger;
    private static int DEFAULT_TASK_ID = -1;

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
        appDatabase.taskDao().insertTask(task);
    }

    public void insertOrUpdateTask(Activity activity, Task task, int mTaskId) {
        appExecutors.getDiskIO().execute(() -> {
            if(mTaskId == DEFAULT_TASK_ID) {
                appDatabase.taskDao().insertTask(task);
            } else {
                task.setId(mTaskId);
                appDatabase.taskDao().updateTask(task);
            }
            activity.finish();
        });
    }
}
