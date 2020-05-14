package com.prisonerprice.dayscountup.viewmodel;

import android.app.Activity;
import android.content.Context;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.prisonerprice.dayscountup.database.Task;
import com.prisonerprice.dayscountup.middleware.DataExchanger;
import com.prisonerprice.dayscountup.view.TaskAdapter;

public class EditViewModel extends ViewModel {

    private final static String TAG = EditViewModel.class.getSimpleName();
    private DataExchanger dataExchanger;
    private static Task bufferTask;

    public EditViewModel(Context context) {
        this.dataExchanger = DataExchanger.getInstance(context);
    }

    public LiveData<Task> getTask(int taskId) {
        return dataExchanger.getTaskById(taskId);
    }

    public void insertTask(Task task) {
        dataExchanger.insertTask(task);
    }

    public void insertOrUpdateTask(Activity activity, Task task, int taskId) {
        dataExchanger.insertOrUpdateTask(activity, task, taskId);
    }

    public Task getBufferTask() {
        return bufferTask;
    }

    public static void setBufferTask(int position) {
        bufferTask = TaskAdapter.getTask(position);
        Log.d(TAG, bufferTask.toString());
    }

    public static void clearBufferTask() {
        bufferTask = null;
    }
}
