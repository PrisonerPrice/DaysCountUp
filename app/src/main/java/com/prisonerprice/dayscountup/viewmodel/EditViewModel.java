package com.prisonerprice.dayscountup.viewmodel;

import android.app.Activity;
import android.content.Context;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.prisonerprice.dayscountup.database.Task;
import com.prisonerprice.dayscountup.middleware.DataExchanger;

public class EditViewModel extends ViewModel {

    private DataExchanger dataExchanger;

    public EditViewModel(Context context) {
        this.dataExchanger = DataExchanger.getInstance(context);
    }

    public LiveData<Task> getTask(int taskId) {
        return dataExchanger.getTaskById(taskId);
    }

    public void insertOrUpdateTask(Activity activity, Task task, int taskId) {
        dataExchanger.insertOrUpdateTask(activity, task, taskId);
    }
}
