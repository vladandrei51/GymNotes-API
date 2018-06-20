package com.example.vlada.licenta.Views.Calendar;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.applandeo.materialcalendarview.CalendarView;
import com.applandeo.materialcalendarview.EventDay;
import com.applandeo.materialcalendarview.exceptions.OutOfDateRangeException;
import com.example.vlada.licenta.Domain.Cardio;
import com.example.vlada.licenta.Domain.Lift;
import com.example.vlada.licenta.R;
import com.example.vlada.licenta.Utils.Utils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

import io.realm.Realm;


public class CalendarActivity extends AppCompatActivity {

    public static final String EVENT_DATE = "event";
    CalendarView mCalendar;
    ArrayList<Date> lifts;
    ArrayList<Date> cardio;
    Realm realm;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        realm = Realm.getDefaultInstance();
        lifts = realm.where(Lift.class).findAll().stream().map(Lift::getSetDate).distinct().collect(Collectors.toCollection(ArrayList::new));
        cardio = realm.where(Cardio.class).findAll().stream().map(Cardio::getSetDate).distinct().collect(Collectors.toCollection(ArrayList::new));
        List<EventDay> events = new ArrayList<>();
        for (Date lift_date : lifts) {
            events.add(new EventDay(Utils.toCalendar(lift_date), R.drawable.black_barebell));
        }
        for (Date cardio_date : cardio) {
            events.add(new EventDay(Utils.toCalendar(cardio_date), R.drawable.black_bike));
        }
        setContentView(R.layout.activity_calendar);
        setTitle("Activity history");
        mCalendar = findViewById(R.id.calendarView);
        mCalendar.setOnDayClickListener(this::previewNote);
        try {
            mCalendar.setDate(new Date());
        } catch (OutOfDateRangeException ignored) {
        }
        mCalendar.setEvents(events);
//        calendar.setTime(lifts.stream().map(Lift::getSetDate).max(Date::compareTo).orElse(new Date()));
//        calendar.add(Calendar.MONTH, 1);
//        mCalendar.setMaximumDate(calendar);
//        calendar.setTime(lifts.stream().map(Lift::getSetDate).min(Date::compareTo).orElse(new Date()));
//        calendar.add(Calendar.MONTH, -1);
//        mCalendar.setMinimumDate(calendar);
        realm.close();
        super.onCreate(savedInstanceState);
    }

    private void previewNote(EventDay eventDay) {
        if (eventDay.getImageResource() == 0) {
            Utils.displayToast(getApplicationContext(), "No activity for " + new SimpleDateFormat("dd MMM. yyyy", Locale.US).format(eventDay.getCalendar().getTime()));
        } else {
            Intent intent = new Intent(this, LiftPreviewActivity.class);
            intent.putExtra(EVENT_DATE, eventDay.getCalendar().getTime());
            startActivity(intent);
        }
    }

}
