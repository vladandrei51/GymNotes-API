package com.example.vlada.licenta.Views.ExerciseGenerator;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.ListView;

import com.example.vlada.licenta.Adapter.AdapterItems.Header;
import com.example.vlada.licenta.Adapter.AdapterItems.Item;
import com.example.vlada.licenta.Adapter.AdapterItems.ListItem;
import com.example.vlada.licenta.Adapter.HeaderItemListAdapter;
import com.example.vlada.licenta.Domain.Exercise;
import com.example.vlada.licenta.Domain.GeneratedExercises;
import com.example.vlada.licenta.R;
import com.example.vlada.licenta.Utils.Utils;
import com.example.vlada.licenta.Views.ExerciseView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

import io.realm.Case;
import io.realm.Realm;
import io.realm.RealmList;
import io.realm.Sort;

import static com.example.vlada.licenta.Views.HomeActivity.WEAK_BODY_PARTS_INTENT;

public class ExerciseGeneratorView extends AppCompatActivity {
    private final static String PREF_EXERCISE_NUMBER_KEY = "exercises_number_key";
    ListView list;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        HashMap<String, String> muscleGroup2Compound = new HashMap<String, String>() {{
            put("Chest", getResources().getString(R.string.benchpress_strength_exercise));
            put("Lower Back", getResources().getString(R.string.pull_strength_exercise));
            put("Shoulders", getResources().getString(R.string.ohp_strength_exercise));
            put("Legs", getResources().getString(R.string.squat_strength_exercise));
            put("Middle Back", getResources().getString(R.string.row_strength_exercise));
        }};
//exercises_number_key
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        int numberOfExercises = prefs.getInt(PREF_EXERCISE_NUMBER_KEY, 8);

        setTitle("Personalized workout");
        setContentView(R.layout.activity_exercise_generator);
        ArrayList<String> weakMuscleGroups;

        Realm realm = Realm.getDefaultInstance();
        weakMuscleGroups = new ArrayList<>(Arrays.asList((getIntent().getStringExtra(WEAK_BODY_PARTS_INTENT)).split("\\s*,\\s*")));
        RealmList<String> rr = new RealmList<>();
        rr.addAll(weakMuscleGroups);
        try (Realm r = Realm.getDefaultInstance()) {
            r.executeTransaction(realm1 -> realm1.insertOrUpdate(new GeneratedExercises(rr)));
        }

        GeneratedExercises generatedExercises = realm.where(GeneratedExercises.class).findAll().stream()
                .filter(g -> Utils.datesAreSameDay(g.getmPrettyDate(), new Date()))
                .filter(g -> {
                    boolean found = false;
                    for (String s : muscleGroup2Compound.keySet()) {
                        if (g.getArrWeakBodyParts().contains(s))
                            found = true;
                    }
                    return found;
                })
                .sorted(Comparator.comparing(GeneratedExercises::getmPrettyDate).reversed())
                .findFirst().orElse(null);
        if (generatedExercises != null)
            weakMuscleGroups = generatedExercises.getArrWeakBodyParts();

        if (weakMuscleGroups.size() == 0 || weakMuscleGroups.get(0) == null || weakMuscleGroups.contains(getResources().getString(R.string.not_enough_data)) || weakMuscleGroups.contains(getResources().getString(R.string.symmetrical_strength))) {
            Utils.displayToast(getApplicationContext(), "Not enough data yet");
            finish();
        }

        String ms;
        list = findViewById(R.id.generated_list);
        List<Item> items = new ArrayList<>();
        for (String s : weakMuscleGroups) {
            items.add(new Header(s));
            items.add(new ListItem(muscleGroup2Compound.get(s)));

            int x = getLimit(numberOfExercises, weakMuscleGroups.size(), weakMuscleGroups.indexOf(s)) - 1;
            if (s.equals("Legs"))
                ms = "Quads";
            else {
                ms = s;
            }
            List<Exercise> exercisesToAdd = realm.where(Exercise.class)
                    .equalTo("musclegroup", ms, Case.INSENSITIVE)
                    .and().not().equalTo("type", "Cardio", Case.INSENSITIVE)
                    .and().not().equalTo("type", "Plyometrics", Case.INSENSITIVE)
                    .and().not().equalTo("type", "Stretching", Case.INSENSITIVE)
                    .and().not().equalTo("name", muscleGroup2Compound.get(s))
                    .sort("rating", Sort.DESCENDING)
                    .findAll()
                    .stream().limit(x).collect(Collectors.toCollection(ArrayList::new));

            for (Exercise exercise : exercisesToAdd) {
                items.add(new ListItem(exercise.getName()));
            }
        }


        HeaderItemListAdapter adapter = new HeaderItemListAdapter(this, items);
        list.setAdapter(adapter);
        list.setOnItemClickListener((adapterView, view, i, l) ->

        {
            if (list.getItemAtPosition(i) instanceof ListItem) {
                String s = ((ListItem) list.getItemAtPosition(i)).getStr1();
                Intent intent = new Intent(getApplicationContext(), ExerciseView.class);
                intent.putExtra("exercise_name", s);
                startActivity(intent);

            }
        });
        realm.close();
    }

    private int getLimit(int maxExercises, int numberOfTotalMuscleGroups, int numberOfMuscleGroup) {
        if (maxExercises == 0) maxExercises = 8;
        @SuppressLint("UseSparseArrays") HashMap<Integer, Integer> valuesMap = new HashMap<>();
        for (int i = 0; i < numberOfTotalMuscleGroups; i++) {
            valuesMap.put(i, 0);
        }
        while (maxExercises > 0) {
            for (int i = 0; i < numberOfTotalMuscleGroups; i++) {
                if (maxExercises > 0) {
                    maxExercises--;
                    valuesMap.put(i, valuesMap.get(i) + 1);
                }
            }
        }
        return valuesMap.get(numberOfMuscleGroup);
    }


}
