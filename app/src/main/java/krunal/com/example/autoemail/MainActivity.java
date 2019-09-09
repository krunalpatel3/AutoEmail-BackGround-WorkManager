package krunal.com.example.autoemail;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.TimePickerDialog;
import android.content.SharedPreferences;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


import java.time.Duration;
import java.util.Calendar;
import java.util.concurrent.TimeUnit;

import androidx.work.Data;
import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.ExistingWorkPolicy;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;

public class MainActivity extends AppCompatActivity {

    private SharedPreferences mPreferences;
    private TextView textView_Set_Time;
    private EditText editText_Subject, editText_Message_Body;
    private Button button_Schedule;
    private int mHour, mMinte;
    private static final String mPreferncesEmail = "time";
    private int hours, mins;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        textView_Set_Time = findViewById(R.id.textView_Set_Time);
        editText_Subject = findViewById(R.id.editText_Subject);
        editText_Message_Body = findViewById(R.id.editText_Message_Body);
        button_Schedule = findViewById(R.id.button_Schedule);

        mPreferences = getSharedPreferences(mPreferncesEmail, MODE_PRIVATE);
        textView_Set_Time.setOnClickListener(v -> {
            final Calendar calendar1 = Calendar.getInstance();

            mHour = calendar1.get(Calendar.HOUR_OF_DAY);
            mMinte = calendar1.get(Calendar.MINUTE);
            TimePickerDialog timePicker = new TimePickerDialog(this, (view, hourOfDay, minute) -> {

                final Calendar calendar = Calendar.getInstance();
                calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                calendar.set(Calendar.MINUTE, minute);

                updateTime(hourOfDay, minute);

            }, mHour, mMinte, false);
            timePicker.show();

        });


        button_Schedule.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (editText_Subject.getText() == null || editText_Subject.getText().toString().trim().isEmpty() ||
                        editText_Message_Body.getText() == null || editText_Message_Body.getText().toString().trim().isEmpty()
                        || textView_Set_Time.getText() == null || textView_Set_Time.getText().toString().trim().isEmpty()
                        || textView_Set_Time.getText().toString().contains("Set Time")) {

                    return;
                }

                Data inputData = new Data.Builder()
                        .putString(SendEmailTask.EXTRA_SUBJECT, editText_Subject.getText().toString())
                        .putString(SendEmailTask.EXTRA_BODY_MESSAGE, editText_Message_Body.getText().toString())
                        .build();

                Calendar calendar2 = Calendar.getInstance();

                Log.e("hours", "Inside hours != 0 || mins != 0 call");
            //    calendar2.setTimeInMillis(System.currentTimeMillis());
                calendar2.set(Calendar.HOUR_OF_DAY, hours);
                calendar2.set(Calendar.MINUTE, mins);

                long currentTime = System.currentTimeMillis();
                long specificTimeToTrigger = calendar2.getTimeInMillis();
                long delayToPass = specificTimeToTrigger - currentTime;

                Log.e("delayToPass", String.valueOf(delayToPass));
                Log.e("getTimeInMillis", String.valueOf(calendar2.getTimeInMillis()));

                Log.e("duration", String.valueOf(hours) + String.valueOf(mins));

                OneTimeWorkRequest oneTimeWorkRequest = new OneTimeWorkRequest.Builder(SendEmailTask.class)
                        .setInputData(inputData)
                        .setInitialDelay(delayToPass,TimeUnit.MILLISECONDS)
                        .build();

                WorkManager.getInstance().enqueue(oneTimeWorkRequest);
            }
        });


    }


    private void updateTime(int hours, int mins) {

        this.hours = hours;
        this.mins = mins;

        SaveToSharedPreferencesFile("Hours", hours, "Minutes", mins);

        String timeSet = "";
        if (hours > 12) {
            hours -= 12;
            timeSet = "PM";
        } else if (hours == 0) {
            hours += 12;
            timeSet = "AM";
        } else if (hours == 12)
            timeSet = "PM";
        else
            timeSet = "AM";

        String minutes = "";
        if (mins < 10)
            minutes = "0" + mins;
        else
            minutes = String.valueOf(mins);

        // Append in a StringBuilder
        String aTime = new StringBuilder().append(hours).append(':')
                .append(minutes).append(" ").append(timeSet).toString();

        textView_Set_Time.setText(aTime);
    }

    private void SaveToSharedPreferencesFile(String key1_House, int value1, String key2_mins, int value2) {
        SharedPreferences.Editor preferencesEditor = mPreferences.edit();
        preferencesEditor.putInt(key1_House, value1);
        preferencesEditor.putInt(key2_mins, value2);
        preferencesEditor.apply();
    }
}
