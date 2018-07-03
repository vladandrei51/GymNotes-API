package com.example.vlada.licenta.Views.ExerciseGenerator;

import android.content.Intent;
import android.os.Bundle;
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
import com.example.vlada.licenta.Views.ExerciseView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import io.realm.Case;
import io.realm.Realm;
import io.realm.RealmList;
import io.realm.Sort;

import static com.example.vlada.licenta.Views.HomeActivity.WEAK_BODY_PARTS_INTENT;

public class ExerciseGeneratorView extends AppCompatActivity {
    public final static String DATE_FORMAT = "yyyymmddHHmmss";
    private final static int NO_EXERCISES = 8;
    ListView list;
    ArrayList<String> weakMuscleGroups;

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

        setTitle("Personalised workout");
        setContentView(R.layout.activity_exercise_generator);

        Realm realm = Realm.getDefaultInstance();
        long exercisesGeneratedToday = realm.where(GeneratedExercises.class).findAll().stream().filter(g -> g.getmPrettyDate().equals(new SimpleDateFormat(DATE_FORMAT, Locale.US).format(new Date()))).count();
        if (exercisesGeneratedToday == 0) {
            weakMuscleGroups = new ArrayList<>(Arrays.asList((getIntent().getStringExtra(WEAK_BODY_PARTS_INTENT)).split("\\s*,\\s*")));
            RealmList<String> rr = new RealmList<>();
            rr.addAll(weakMuscleGroups);
            GeneratedExercises generatedExercises = new GeneratedExercises(rr);
            try (Realm r = Realm.getDefaultInstance()) {
                r.executeTransaction(realm1 -> {
                    realm1.insertOrUpdate(generatedExercises);
                });
            }
        } else {
            weakMuscleGroups = realm.where(GeneratedExercises.class).findAll().stream().filter(g -> g.getmPrettyDate().equals(new SimpleDateFormat(DATE_FORMAT, Locale.US).format(new Date()))).sorted().findFirst().orElse(null).getArrWeakBodyParts();
        }
        list = findViewById(R.id.generated_list);
        List<Item> items = new ArrayList<>();
        int exercisesLeft = NO_EXERCISES;
        for (String s : weakMuscleGroups) {
            items.add(new Header(s));
            items.add(new ListItem(muscleGroup2Compound.get(s)));
            exercisesLeft--;
            if (s.equals("Legs"))
                s = "Quads";
            List<Exercise> exercisesToAdd = realm.where(Exercise.class).equalTo("musclegroup", s)
                    .and().not().contains("type", "Cardio", Case.INSENSITIVE)
                    .and().not().contains("type", "Plyometrics", Case.INSENSITIVE)
                    .and().not().contains("type", "Stretching", Case.INSENSITIVE)
                    .and().notEqualTo("name", muscleGroup2Compound.get(s)).sort("rating", Sort.DESCENDING)
                    .findAll();
            if (exercisesLeft >= weakMuscleGroups.size()) {
                exercisesToAdd = exercisesToAdd.subList(0, exercisesLeft % weakMuscleGroups.size());
            } else {
                exercisesToAdd = exercisesToAdd.subList(0, exercisesLeft);
            }
            for (Exercise exercise : exercisesToAdd) {
                items.add(new ListItem(exercise.getName()));
                exercisesLeft--;
            }
        }
        HeaderItemListAdapter adapter = new HeaderItemListAdapter(this, items);
        list.setAdapter(adapter);
        list.setOnItemClickListener((adapterView, view, i, l) -> {
            if (list.getItemAtPosition(i) instanceof ListItem) {
                String s = ((ListItem) list.getItemAtPosition(i)).getStr1();
                Intent intent = new Intent(getApplicationContext(), ExerciseView.class);
                intent.putExtra("exercise_name", s);
                startActivity(intent);

            }
        });
        realm.close();
    }


}
