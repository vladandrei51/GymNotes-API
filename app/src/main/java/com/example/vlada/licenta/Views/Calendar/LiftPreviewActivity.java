package com.example.vlada.licenta.Views.Calendar;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ListView;

import com.example.vlada.licenta.Adapter.AdapterItems.Header;
import com.example.vlada.licenta.Adapter.AdapterItems.Item;
import com.example.vlada.licenta.Adapter.AdapterItems.ListItem;
import com.example.vlada.licenta.Adapter.HeaderItemListAdapter;
import com.example.vlada.licenta.Domain.Cardio;
import com.example.vlada.licenta.Domain.Exercise;
import com.example.vlada.licenta.Domain.Lift;
import com.example.vlada.licenta.R;
import com.example.vlada.licenta.Utils.Utils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

import io.realm.Realm;

import static com.example.vlada.licenta.Views.Calendar.CalendarActivity.EVENT_DATE;

public class LiftPreviewActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        if (intent == null) finish();
        Realm realm;
        realm = Realm.getDefaultInstance();

        setContentView(R.layout.activity_lift_preview);

        Date d = (Date) intent.getSerializableExtra(EVENT_DATE);
        String prettyString = new SimpleDateFormat("dd MMM. yyyy", Locale.US).format(d);
        setTitle("Activity history from " + prettyString);

        List<Item> items = new ArrayList<>();


        ArrayList<String> allExercises = realm.where(Lift.class)
                .findAll().stream().filter(lift -> lift.date2PrettyString().equals(prettyString)).map(Lift::getExercise_name).distinct().collect(Collectors.toCollection(ArrayList::new));

        ArrayList<String> allCardioExercises = realm.where(Cardio.class)
                .findAll().stream().filter(cardio -> cardio.date2PrettyString().equals(prettyString)).map(Cardio::getExercise_name).distinct().collect(Collectors.toCollection(ArrayList::new));
        allExercises.addAll(allCardioExercises);


        for (String exercise_name : allExercises) {
            Exercise exercise = realm.where(Exercise.class).equalTo("name", exercise_name).findFirst();
            items.add(new Header(exercise_name));
            if (exercise.getType().equals("Cardio") || exercise.getType().equals("Plyometrics") || exercise.getType().equals("Stretching")) {
                ArrayList<Cardio> cardioArrayList = realm.where(Cardio.class).equalTo("exercise_name", exercise_name)
                        .findAll().stream().filter(cardio -> cardio.date2PrettyString().equals(prettyString)).sorted(Comparator.comparing(Cardio::getDate_ms).reversed()).collect(Collectors.toCollection(ArrayList::new));

                for (Cardio cardio : cardioArrayList) {
                    items.add(new ListItem(Utils.getPrettySetFromCardio(cardio.getTime_spent())));
                }
            } else {
                ArrayList<Lift> lifts = realm.where(Lift.class).equalTo("exercise_name", exercise_name)
                        .findAll().stream().filter(lift -> lift.date2PrettyString().equals(prettyString)).sorted(Comparator.comparing(Lift::getDate_ms).reversed()).collect(Collectors.toCollection(ArrayList::new));

                for (Lift lift : lifts) {
                    items.add(new ListItem(Utils.getPrettySetFromLift(lift.getWeight(), lift.getReps())));
                }

            }
        }


        HeaderItemListAdapter adapter = new HeaderItemListAdapter(this, items);
        ListView list = findViewById(R.id.preview_list);
        list.setAdapter(adapter);

        realm.close();
    }

}
