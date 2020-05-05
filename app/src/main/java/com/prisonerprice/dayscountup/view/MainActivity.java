package com.prisonerprice.dayscountup.view;

import android.content.Intent;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.prisonerprice.dayscountup.R;
import com.prisonerprice.dayscountup.viewmodel.MainViewModel;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

public class MainActivity extends AppCompatActivity implements TaskAdapter.ItemClickListener{

    private final static String TAG = MainActivity.class.getSimpleName();

    private TaskAdapter taskAdapter;
    private RecyclerView recyclerView;
    private MainViewModel mainViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar topAppBar = findViewById(R.id.topAppBar);
        setSupportActionBar(topAppBar);

        recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        taskAdapter = new TaskAdapter(this, this);
        recyclerView.setAdapter(taskAdapter);

        FloatingActionButton fab = findViewById(R.id.fab);
        final Intent intent = new Intent(this, EditActivity.class);
        fab.setOnClickListener(view -> startActivity(intent));

        mainViewModel = new MainViewModel(getApplication(), this);
        mainViewModel.getTasks().observe(this, tasks -> {
            taskAdapter.setTasks(tasks);
            Log.d(TAG, tasks.size() + "");
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.top_app_bar, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if(id == R.id.sync) {

        }
        if(id == R.id.settings) {
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onItemClickListener(int itemId) {
        Intent intent = new Intent(MainActivity.this, EditActivity.class);
        intent.putExtra(EditActivity.EXTRA_TASK_ID, itemId);
        startActivity(intent);
    }
}
