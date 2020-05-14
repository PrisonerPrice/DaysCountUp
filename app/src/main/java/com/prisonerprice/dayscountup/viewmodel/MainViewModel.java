package com.prisonerprice.dayscountup.viewmodel;

import android.app.Application;
import android.content.Context;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;
import androidx.recyclerview.widget.RecyclerView;

import com.prisonerprice.dayscountup.database.Task;
import com.prisonerprice.dayscountup.middleware.DataExchanger;
import com.prisonerprice.dayscountup.view.TaskAdapter;

import java.util.List;

public class MainViewModel extends AndroidViewModel {

    private static final String TAG = MainViewModel.class.getSimpleName();
    private DataExchanger dataExchanger;

    public MainViewModel(@NonNull Application application, Context context) {
        super(application);
        dataExchanger = DataExchanger.getInstance(context);
    }

    public LiveData<List<Task>> getTasks() {
        return dataExchanger.getTaskList();
    }

    public void deleteTask(RecyclerView.ViewHolder viewHolder, TaskAdapter mAdapter) {
        dataExchanger.deleteTask(viewHolder, mAdapter);
    }
}
