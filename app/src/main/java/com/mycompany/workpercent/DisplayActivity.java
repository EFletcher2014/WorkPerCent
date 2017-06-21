package com.mycompany.workpercent;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Calendar;

public class DisplayActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display);
        Context context = getApplicationContext();
        SharedPreferences pref = getSharedPreferences("shiftDetails", MODE_PRIVATE);
        final ProgressBar progressBar;
        int progressBarStatus = 0;
        Handler progressBarHandler = new Handler();
        int begHour = pref.getInt("beginHour", 11);
        int begMin = pref.getInt("beginMinute", 11);
        int endHour = pref.getInt("endHour", 11);
        int endMin = pref.getInt("endMinute", 11);
        final float wage = pref.getFloat("hourlyWage", 1);
        if(begHour>endHour)
        {
            endHour+=24;
        }
        System.out.println("begHour: " + begHour + " endHour: " + endHour);
        double begTime = (double)begHour + ((double)begMin/60);
        double endTime = (double)endHour + ((double)endMin/60);
        final double totalHours = (endTime - begTime);
        final double totalMoney = totalHours* (double) wage;

        //retrieves current time
        Calendar c = Calendar.getInstance();
        int hour = c.get(Calendar.HOUR_OF_DAY);
        int min = c.get(Calendar.MINUTE);
        if(hour<begHour)
        {
            hour+=24;
        }
        double currentTime = (double)hour + ((double)min/60);

        //calculates percentage done
        double hoursWorked = (currentTime - begTime);

        if(hoursWorked>totalHours)
        {
            hoursWorked=totalHours;
        }
        long numMili = (long)(3600000*(totalHours-hoursWorked));
        System.out.println("hoursWorked: " + hoursWorked + " totalHours: " + totalHours);
        System.out.println("numMili: " + numMili);
        double percentDone =  hoursWorked/totalHours;
        percentDone*=100;
        int perc=(int)(percentDone + 0.5);
        final DecimalFormat df = new DecimalFormat("0.00");
        final DecimalFormat hourFormat = new DecimalFormat("00");
        String percentDoneString = "Your shift is " + Integer.toString(perc) + "% over";
        System.out.println(percentDoneString);
        final TextView mTextField = (TextView) findViewById(R.id.mTextView);

        progressBar = (ProgressBar)findViewById(R.id.progressBar);
        progressBar.setProgress(perc);
        progressBar.setMax(100);

        //creates an initializes a textview
        TextView text = (TextView) findViewById(R.id.percentWorked);
        text.setTextSize(24);
        text.setTextColor(Color.parseColor("#87C1B8"));

        //sets the text of that textview
        final TextView percent = (TextView) findViewById(R.id.percentWorked);
        percent.setText(percentDoneString);

        //creates an initializes a textview
        TextView hourText = (TextView) findViewById(R.id.hoursWorked);
        hourText.setTextSize(24);
        hourText.setTextColor(Color.parseColor("#87C1B8"));

        //sets the text of that textview
        final TextView hours = (TextView) findViewById(R.id.hoursWorked);
        hours.setText(df.format(hoursWorked) + " hours worked of " + df.format(totalHours) + " hours total");

        double moneyEarn=hoursWorked* (double) wage;
        final NumberFormat formatter = NumberFormat.getCurrencyInstance();

        //creates an initializes a textview
        final TextView wageText = (TextView) findViewById(R.id.moneyEarned);
        wageText.setTextSize(24);
        wageText.setTextColor(Color.parseColor("#87C1B8"));

        //sets the text of that textview
        final TextView wages = (TextView) findViewById(R.id.moneyEarned);
        wages.setText(formatter.format(moneyEarn) + " of " + formatter.format(totalMoney) + " earned");

        new CountDownTimer(numMili, 1000)
        {
            public void onTick(long millisUntilFinished) {
                long temp = millisUntilFinished;
                int hors = ((int) millisUntilFinished / 3600000);
                temp=temp%3600000;
                int mins = ((int) temp / 60000);
                temp=temp%60000;
                int secs = ((int) temp / 1000);
                double hurs= ((totalHours*3600000)-millisUntilFinished)/3600000;
                System.out.println("hurs: " + hurs + " totalHours: " + totalHours);
                int percnt = (int)((100*(hurs/totalHours))+0.5);
                System.out.println("hurs: " + hurs + " percnt: " + percnt);
                double totWage = hurs * wage;

                wages.setText(formatter.format(totWage) + " of " + formatter.format(totalMoney) + " earned");

                hours.setText(df.format(hurs) + " hours worked of " + df.format(totalHours) + " hours total");

                percent.setText("Your shift is " + Integer.toString(percnt) + "% over");

                progressBar.setProgress(percnt);

                mTextField.setText("Time Remaining: " + hourFormat.format(hors) + ":" + hourFormat.format(mins) + ":" + hourFormat.format(secs));

                //here you can have your logic to set text to edittext
            }

            public void onFinish() {
                mTextField.setText("Time Remaining: 00:00:00\n Good Job!");
                percent.setText("Your shift is 100% over!");
                progressBar.setProgress(100);
            }
        }.start();
    }

}
