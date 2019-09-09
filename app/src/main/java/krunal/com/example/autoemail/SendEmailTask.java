package krunal.com.example.autoemail;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.util.Log;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.mail.MessagingException;

import androidx.work.Data;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import static android.content.Context.MODE_PRIVATE;

public class SendEmailTask extends Worker {

    static final String EXTRA_SUBJECT = "title";
    static final String EXTRA_BODY_MESSAGE = "text";
    private static final String mPreferncesEmail = "time";
    private SharedPreferences mPreferences;

    public SendEmailTask(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {

        Log.e("doWork","doWork call");

        String subject = getInputData().getString(EXTRA_SUBJECT);
        String Body_Message = getInputData().getString(EXTRA_BODY_MESSAGE);
        mPreferences = getApplicationContext().getSharedPreferences(mPreferncesEmail, MODE_PRIVATE);

        List<String> EmailList = new ArrayList<>();
        EmailList.add("abcd@gmail.com");
        EmailList.add("abcd2@gmail.com");
        EmailList.add("abcd3@gmail.com");

        int hours = mPreferences.getInt("Hours", 0);
        int mins  = mPreferences.getInt("Minutes",0);
        Log.e("hours",String.valueOf(hours));
        Log.e("min",String.valueOf(mins));

        Email androidEmail = new Email("testemail.id@gamil.com",
                "testpass", (List) EmailList, subject,
                Body_Message);

        try {
            androidEmail.createEmailMessage();
            androidEmail.sendEmail();
        } catch (MessagingException | UnsupportedEncodingException e) {
            e.printStackTrace();
        }

//        Calendar calendar = Calendar.getInstance();
//        calendar.set(Calendar.HOUR_OF_DAY, hours);
//        calendar.set(Calendar.MINUTE, mins);

        Data inputData = new Data.Builder()
                .putString(SendEmailTask.EXTRA_SUBJECT, subject)
                .putString(SendEmailTask.EXTRA_BODY_MESSAGE,Body_Message)
                .build();

        Log.e("subject",subject);
        Log.e("Body_Message",Body_Message);

        OneTimeWorkRequest oneTimeWorkRequest = new OneTimeWorkRequest.Builder(SendEmailTask.class)
                .setInputData(inputData)
                .setInitialDelay(24 ,TimeUnit.HOURS)
                .build();

        WorkManager.getInstance().enqueue(oneTimeWorkRequest);

        return Result.success();
    }
}
