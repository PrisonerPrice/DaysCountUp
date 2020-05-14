package com.prisonerprice.dayscountup.view;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.prisonerprice.dayscountup.App;
import com.prisonerprice.dayscountup.R;
import com.prisonerprice.dayscountup.firestore.FireStoreDB;
import com.prisonerprice.dayscountup.viewmodel.EditViewModel;
import com.prisonerprice.dayscountup.viewmodel.MainViewModel;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;

import it.xabaras.android.recyclerview.swipedecorator.RecyclerViewSwipeDecorator;

public class MainActivity extends AppCompatActivity implements TaskAdapter.ItemClickListener{

    private final static String TAG = MainActivity.class.getSimpleName();

    private TaskAdapter taskAdapter;
    private RecyclerView recyclerView;
    private MainViewModel mainViewModel;

    private SharedPreferences sharedPreferences;
    private String uid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sharedPreferences = getSharedPreferences("USER", Context.MODE_PRIVATE);
        String uid = sharedPreferences.getString("USER", "NO_USER");

        Toolbar topAppBar = findViewById(R.id.topAppBar);
        setSupportActionBar(topAppBar);

        recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        taskAdapter = new TaskAdapter(this, this);
        recyclerView.setAdapter(taskAdapter);

        FloatingActionButton fab = findViewById(R.id.fab);
        final Intent intent = new Intent(this, EditActivity.class);
        fab.setOnClickListener(view -> {
            startActivity(intent);
            EditViewModel.clearBufferTask();
        });

        mainViewModel = new MainViewModel(getApplication(), this);
        mainViewModel.getTasks().observe(this, tasks -> {
            taskAdapter.setTasks(tasks);
            Log.d(TAG, "uid is: " + uid);
            if (!uid.equals("NO_USER")) {
                Log.d(TAG, "Update tasks to FireStore!");
                FireStoreDB.getInstance(this).uploadTasks(tasks);
            }
            Log.d(TAG, tasks.size() + "");
        });

        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {

            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                mainViewModel.deleteTask(viewHolder, taskAdapter);
            }

            @Override
            public void onChildDraw (Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder,float dX, float dY,int actionState, boolean isCurrentlyActive){
                new RecyclerViewSwipeDecorator.Builder(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
                        .addBackgroundColor(ContextCompat.getColor(MainActivity.this, R.color.red))
                        .setIconHorizontalMargin(TypedValue.COMPLEX_UNIT_DIP, 24)
                        .addActionIcon(R.drawable.ic_delete_black_18dp)
                        .create()
                        .decorate();

                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
            }

        }).attachToRecyclerView(recyclerView);


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
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
        }
        if(id == R.id.settings) {
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onItemClickListener(int itemId) {
        EditViewModel.setBufferTask(itemId);
        Intent intent = new Intent(MainActivity.this, EditActivity.class);
        intent.putExtra(EditActivity.EXTRA_TASK_ID, itemId);
        startActivity(intent);
    }
}
