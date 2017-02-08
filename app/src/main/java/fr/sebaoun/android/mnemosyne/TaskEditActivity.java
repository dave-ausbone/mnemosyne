package fr.sebaoun.android.mnemosyne;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.util.Calendar;

import io.realm.Realm;
import io.realm.RealmResults;

/**
 * Created by sebaou_d on 17/01/17.
 */

public class TaskEditActivity extends AppCompatActivity {
    private Task        task;
    private Realm       realm ;
    private EditText    name, comment, content;
    private TextView    fromDate, toDate, fromTime, toTime;
    private CheckBox    isDone;
    private int         dialogId = -1;

    /**
     * override of the Activity onCreate method that initialise Realm for the activity
     * and calls a method to fetch a Task and an other one to fill the UI with the data
     * from that Task
     * @param savedInstanceState Saved instance of the activity
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_edit);
        int id = getIntent().getIntExtra("id", -1);

        Realm.init(getApplicationContext());
        realm = Realm.getDefaultInstance();

        initTask(id);
        initForms();
    }

    /**
     * Override of Activity onCreateOptionsMenu method that inflates the AppBar menu
     * @param menu AppBar object
     * @return true
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        menu.findItem(R.id.show_all).setVisible(false);
        menu.findItem(R.id.show_done).setVisible(false);
        menu.findItem(R.id.show_not_done).setVisible(false);

        return true;
    }

    /**
     * Override of Activity onCreateDialog method that handles the save and delete actions
     * from the AppBar.
     * @param item AppBar element that as been selected by the user
     * @return true
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_save) {
            realm.beginTransaction();
            task.setName(name.getText().toString().equals("") ?
                    "Task " + task.getId() : name.getText().toString());
            task.setContent(content.getText().toString());
            task.setComment(comment.getText().toString());
            task.setState(isDone.isChecked());
            realm.commitTransaction();
            finish();
        } else if (item.getItemId() == R.id.action_delete) {
            realm.beginTransaction();
            task.deleteFromRealm();
            realm.commitTransaction();
            finish();
        }
        return true;
    }

    /**
     * Override of Activity onCreateDialog method that launches a DatePicker or TimePicker
     * depending on what field of the UI has been clicked
     * @param id Id of the field that has been selected by user
     * @return Dialog | null
     */
    @Override
    @SuppressWarnings("deprecation")
    protected Dialog onCreateDialog(int id) {
        dialogId = id;
        Calendar start = task.getStart();
        Calendar end = task.getEnd();
        switch (id) {
            case 900 :
                return new DatePickerDialog(this, dateListener, start.get(Calendar.YEAR),
                        start.get(Calendar.MONTH), start.get(Calendar.DAY_OF_MONTH));
            case 901 :
                return new TimePickerDialog(this, timeListener, start.get(Calendar.HOUR_OF_DAY),
                        start.get(Calendar.MINUTE), DateFormat.is24HourFormat(this));
            case 902 :
                return new DatePickerDialog(this, dateListener, end.get(Calendar.YEAR),
                        end.get(Calendar.MONTH), end.get(Calendar.DAY_OF_MONTH));
            case 903 :
                return new TimePickerDialog(this, timeListener, end.get(Calendar.HOUR_OF_DAY),
                        end.get(Calendar.MINUTE), DateFormat.is24HourFormat(this));
        }
        return null;
    }

    /**
     * Listener that saves the date when a DatePicker returns
     */
    private DatePickerDialog.OnDateSetListener dateListener = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
            Calendar calendar = task.getStart();
            calendar.set(Calendar.YEAR, year);
            calendar.set(Calendar.MONTH, month);
            calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            realm.beginTransaction();
            if (dialogId == 900) {
                task.setStart(calendar);
                checkIfDateAfter(calendar);
            } else if (dialogId == 902) {
                task.setEnd(calendar);
                checkIfDateBefore(calendar);
            }
            realm.commitTransaction();
            setDateTime();
        }
    };

    /**
     * Listener that saves the time when a TimePicker returns
     */
    private TimePickerDialog.OnTimeSetListener timeListener = new TimePickerDialog.OnTimeSetListener() {
        @Override
        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            Calendar calendar = task.getStart();
            calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
            calendar.set(Calendar.MINUTE, minute);
            realm.beginTransaction();
            if (dialogId == 901) {
                task.setStart(calendar);
                checkIfDateAfter(calendar);
            } else if (dialogId == 903) {
                task.setEnd(calendar);
                checkIfDateBefore(calendar);
            }
            realm.commitTransaction();
            setDateTime();
        }
    };

    @SuppressWarnings("deprecation")
    public void pickFromDate(View view) {
        showDialog(900);
    }

    @SuppressWarnings("deprecation")
    public void pickFromTime(View view) {
        showDialog(901);
    }

    @SuppressWarnings("deprecation")
    public void pickToDate(View view) {
        showDialog(902);
    }

    @SuppressWarnings("deprecation")
    public void pickToTime(View view) {
        showDialog(903);
    }

    /**
     * check if the id matches with an existing object. If so, fetches it from Realm
     * if not (-1), creates a new Task
     * @param id Id of the field that has been selected by user
     */
    private void initTask(int id) {
        if (id == -1) {
            int maxId = -1;
            if (!realm.isEmpty()) {
                maxId = realm.where(Task.class).max("id").intValue();
            }
            realm.beginTransaction();
            task = realm.createObject(Task.class);
            task.setId(maxId + 1);
            task.setName("");
            task.setComment("");
            task.setContent("");
            task.setState(false);
            task.setStart(Calendar.getInstance());
            Calendar calendar = task.getStart();
            calendar.add(Calendar.DATE, 1);
            task.setEnd(calendar);
            realm.commitTransaction();
        } else {
            RealmResults<Task> tasks = realm.where(Task.class).findAll();
            task = tasks.get(0);
        }
    }

    /**
     * Binds the Activity with the UI components and updates them with the data from the Task
     */
    private void initForms() {
        name = (EditText) findViewById(R.id.edit_name);
        comment = (EditText) findViewById(R.id.edit_comment);
        content = (EditText) findViewById(R.id.edit_content);
        isDone = (CheckBox) findViewById(R.id.status_box);

        fromDate = (TextView) findViewById(R.id.from_date);
        toDate = (TextView) findViewById(R.id.to_date);
        fromTime = (TextView) findViewById(R.id.from_time);
        toTime = (TextView) findViewById(R.id.to_time);

        name.setText(task.getName(), TextView.BufferType.EDITABLE);
        comment.setText(task.getComment(), TextView.BufferType.EDITABLE);
        content.setText(task.getContent(), TextView.BufferType.EDITABLE);
        isDone.setChecked(task.getState());

        setDateTime();
        name.requestFocus();
    }

    /**
     * Sets the date and time in the UI according to the data in the Task
     */
    private void setDateTime() {
        Calendar c = task.getStart();
        fromDate.setText(Integer.toString(c.get(Calendar.DAY_OF_MONTH)) +
                "/" + Integer.toString(c.get(Calendar.MONTH) + 1) +
                "/" + Integer.toString(c.get(Calendar.YEAR)));
        fromTime.setText(Integer.toString(c.get(Calendar.HOUR_OF_DAY)) +
                ":" + Integer.toString(c.get(Calendar.MINUTE)));
        c = task.getEnd();
        toDate.setText(Integer.toString(c.get(Calendar.DAY_OF_MONTH)) +
                "/" + Integer.toString(c.get(Calendar.MONTH) + 1) +
                "/" + Integer.toString(c.get(Calendar.YEAR)));
        toTime.setText(Integer.toString(c.get(Calendar.HOUR_OF_DAY)) +
                ":" + Integer.toString(c.get(Calendar.MINUTE)));
    }

    /**
     * Checks if the "TO" Date is not before the "FROM" Date.
     * If so, changes the "FROM" Date to be one day before the "TO" Date
     * @param calendar Calendar object for the "FROM" Date of the Task
     */
    private void checkIfDateBefore(Calendar calendar) {
        if (calendar.before(task.getStart())) {
            Toast.makeText(getApplicationContext(), "BEFORE", Toast.LENGTH_SHORT).show();
  //          calendar.add(Calendar.DATE, -1);
            task.setStart(calendar);
        }
    }

    /**
     * Checks if the "FROM" Date is not after the "TO" Date.
     * If so, changes the "TO" Date to be one day after the "FROM" Date
     * @param calendar Calendar object for the "TO" Date of the Task
     */
    private void checkIfDateAfter(Calendar calendar) {
        if (calendar.after(task.getEnd())) {
            Toast.makeText(getApplicationContext(), "AFTER", Toast.LENGTH_SHORT).show();
//            calendar.add(Calendar.DATE, 1);
            task.setEnd(calendar);
        }
    }
}
