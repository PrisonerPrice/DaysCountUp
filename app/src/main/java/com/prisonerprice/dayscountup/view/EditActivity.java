package com.prisonerprice.dayscountup.view;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.switchmaterial.SwitchMaterial;
import com.maltaisn.icondialog.IconDialog;
import com.maltaisn.icondialog.IconDialogSettings;
import com.maltaisn.icondialog.data.Icon;
import com.maltaisn.icondialog.pack.IconDrawableLoader;
import com.maltaisn.icondialog.pack.IconPack;
import com.prisonerprice.dayscountup.App;
import com.prisonerprice.dayscountup.R;
import com.prisonerprice.dayscountup.database.Task;
import com.prisonerprice.dayscountup.viewmodel.EditViewModel;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class EditActivity extends AppCompatActivity implements IconDialog.Callback{

    public final static String EXTRA_TASK_ID = "extrataskid";
    public final static String INSTANCE_TASK_ID = "instancetaskid";
    public final static Long A_DAY_IN_MILI = 24 * 3600 * 1000L;
    public final static int ICON_DEFAULT_ID = 1036;

    private final static String TAG = EditActivity.class.getSimpleName();
    private final static int DEFAULT_TASK_ID = -1;
    private final DateFormat format = new SimpleDateFormat("MM-dd-yyyy", Locale.getDefault());

    private EditText taskDescEditText;
    private EditText monthEditText;
    private EditText dayEditText;
    private EditText yearEditText;
    private SwitchMaterial hundredSwitch;
    private SwitchMaterial anniversarySwitch;
    private EditText customDaysEditText;
    private Button submitBtn;
    private ImageView iconImage;
    private final EditViewModel editViewModel = new EditViewModel(this);

    private int mTaskId = DEFAULT_TASK_ID;

    private final static String ICON_DIALOG_TAG = "icon-dialog";

    private int iconID = ICON_DEFAULT_ID;
    private IconPack iconPack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);

        iconPack = ((App) getApplication()).getIconPack();

        // initViews
        taskDescEditText = findViewById(R.id.et_task_desc);
        monthEditText = findViewById(R.id.et_month);
        dayEditText = findViewById(R.id.et_day);
        yearEditText = findViewById(R.id.et_year);
        hundredSwitch = findViewById(R.id.switch_100);
        anniversarySwitch = findViewById(R.id.switch_anniversary);
        customDaysEditText = findViewById(R.id.et_custom_days);
        submitBtn = findViewById(R.id.btn_edit_activity);
        iconImage = findViewById(R.id.iv_icon);

        iconImage.setImageDrawable(iconPack.getIcon(1036).getDrawable());

        // Read savedInstanceState
        if (savedInstanceState != null && savedInstanceState.containsKey(INSTANCE_TASK_ID)) {
            mTaskId = savedInstanceState.getInt(INSTANCE_TASK_ID, DEFAULT_TASK_ID);
        }

        // read intent from MainActivity
        Intent intent = getIntent();
        if (intent != null && intent.hasExtra(EXTRA_TASK_ID)) {
            submitBtn.setText(R.string.update_btn);
            if (mTaskId == DEFAULT_TASK_ID) {
                mTaskId = intent.getIntExtra(EXTRA_TASK_ID, DEFAULT_TASK_ID);
                editViewModel.getTask(mTaskId).observe(this, task -> {
                    editViewModel.getTask(mTaskId).removeObservers(this);
                    populateUI(task);
                });
            }
        }


        submitBtn.setOnClickListener(v -> onSaveButtonClicked());

        // If dialog is already added to fragment manager, get it. If not, create a new instance.
        IconDialog dialog = (IconDialog) getSupportFragmentManager().findFragmentByTag(ICON_DIALOG_TAG);
        IconDialog iconDialog = dialog != null ? dialog
                : IconDialog.newInstance(new IconDialogSettings.Builder().build());

        iconImage.setOnClickListener(v -> {
            // Open icon dialog
            iconDialog.show(getSupportFragmentManager(), ICON_DIALOG_TAG);
        });

    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        outState.putInt(INSTANCE_TASK_ID, mTaskId);
        super.onSaveInstanceState(outState);
    }

    public void populateUI(Task task) {
        if (task == null) return;

        taskDescEditText.setText(task.getDescription());

        Date date = task.getUpdatedAt();
        String dateStrings[] = format.format(date).split("-");
        monthEditText.setText(dateStrings[0]);
        dayEditText.setText(dateStrings[1]);
        yearEditText.setText(dateStrings[2]);

        ArrayList<Date> dates = task.getRemainderDates();
        for(Date d : dates) {
            if (d.getTime() - date.getTime() == A_DAY_IN_MILI * 100)
                hundredSwitch.setChecked(true);
            else if (format.format(d).substring(0, 5).equals(format.format(date).substring(0, 5)))
                anniversarySwitch.setChecked(true);
            else
                customDaysEditText.setText((d.getTime() - date.getTime()) / A_DAY_IN_MILI + "");
        }

        iconID = task.getIconID();
        if (iconID != ICON_DEFAULT_ID) {
            Icon icon = iconPack.getIcon(iconID);
            iconImage.setImageDrawable(icon.getDrawable());
        }

    }

    public void onSaveButtonClicked() {
        boolean dateFormatIsCorrect = true;
        String desc = taskDescEditText.getText().toString();

        String dateString = "";

        try {
            int month = Integer.parseInt(monthEditText.getText().toString());
            int day = Integer.parseInt(dayEditText.getText().toString());
            int year = Integer.parseInt(yearEditText.getText().toString());

            dateString = month + "-" + day + "-" + year;

            if (month < 0 || month > 12) dateFormatIsCorrect = false;
            if (day < 0 || day > 31) dateFormatIsCorrect = false;

        } catch (Exception e) {
            dateFormatIsCorrect = false;
            e.printStackTrace();
        }

        Date date;
        try {
            date = format.parse(dateString);
        } catch (ParseException e) {
            date = null;
            dateFormatIsCorrect = false;
            e.printStackTrace();
        }

        if (dateFormatIsCorrect) {
            ArrayList<Date> dates = new ArrayList<>();
            if (hundredSwitch.isChecked())
                dates.add(new Date(date.getTime() + 101 * A_DAY_IN_MILI));
            if (anniversarySwitch.isChecked()) {
                String oldYear = format.format(date).substring(6, 10);
                String newYear = (Integer.parseInt(oldYear) + 1) + "";
                try {
                    dates.add(format.parse(format.format(date).substring(0, 6) + newYear));
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
            if (customDaysEditText.getText() != null) {
                String day = customDaysEditText.getText().toString();
                try {
                    dates.add(new Date(date.getTime() + Integer.parseInt(day) * A_DAY_IN_MILI));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            final Task task = new Task(desc, date, dates, iconID);
            editViewModel.insertOrUpdateTask(this, task, mTaskId);
        } else {
            Snackbar.make(submitBtn, "Wrong Date format, please enter a valid date", Snackbar.LENGTH_SHORT)
                    .setAction("Action", null).show();
        }

    }

    @Nullable
    @Override
    public IconPack getIconDialogIconPack() {
        return iconPack;
    }

    @Override
    public void onIconDialogIconsSelected(@NonNull IconDialog dialog, @NonNull List<Icon> icons) {
        // Show a toast with the list of selected icon IDs.
        StringBuilder sb = new StringBuilder();
        for (Icon icon : icons) {
            iconImage.setImageDrawable(icon.getDrawable());
            iconID = icon.getId();
            sb.append(iconID);
            sb.append(", ");
        }
        sb.delete(sb.length() - 2, sb.length());
        Snackbar.make(iconImage, "Icon selected: " + sb, Snackbar.LENGTH_LONG)
                .setAction("Action", null).show();
    }

    @Override
    public void onIconDialogCancelled() {}
}
