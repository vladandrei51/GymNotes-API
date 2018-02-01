package com.example.vlada.licenta.Views;

import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.vlada.licenta.Domain.Exercise;
import com.example.vlada.licenta.Net.Client.ExerciseClient;
import com.example.vlada.licenta.R;

import java.util.List;

import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;
import io.realm.Realm;
import io.realm.RealmBaseAdapter;
import io.realm.RealmResults;

/**
 * Created by andrei-valentin.vlad on 1/30/2018.
 */

public class ExerciseActivity extends AppCompatActivity {

    private CompositeDisposable disposables = new CompositeDisposable();
    private ExerciseClient exerciseClient;

    private Realm realm;
    private RealmResults<Exercise> results;
    private RealmBaseAdapter<Exercise> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exercise);

        this.realm = Realm.getDefaultInstance();
        this.results = realm.where(Exercise.class).findAllAsync();
        this.adapter = new RealmBaseAdapter<Exercise>(results) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                ViewHolder viewHolder;
                if (convertView == null) {
                    convertView = LayoutInflater.from(parent.getContext())
                            .inflate(R.layout.list_view, parent, false);
                    viewHolder = new ViewHolder();
                    viewHolder.exerciseName = convertView.findViewById(R.id.label);
                    convertView.setTag(viewHolder);
                } else {
                    viewHolder = (ViewHolder) convertView.getTag();
                }

                if (adapterData != null) {
                    final Exercise item = adapterData.get(position);
                    viewHolder.exerciseName.setText(item.getName());
                }
                return convertView;

            }
        };
        exerciseClient = new ExerciseClient(this);
        populateExerciseList();

        ListView lvItems = findViewById(R.id.lvItems);
        lvItems.setAdapter(adapter);

        lvItems.setOnItemLongClickListener((arg0, arg1, pos, id) -> {
            Exercise clickedExercise = (Exercise) lvItems.getItemAtPosition(pos);
            showAlert(clickedExercise.getName(), clickedExercise.toPrettyString());
            return true;
        });

    }

    void populateExerciseList() {
        disposables.add(exerciseClient.getExercises()
                .subscribeOn(Schedulers.io())
                .subscribe(
                        this::getExercisesSuccess,
                        this::getExercisesError
                )
        );
    }

    private void getExercisesError(Throwable throwable) {
        runOnUiThread(() -> {
            displayToast("Could not connect to the server, cached data is being used");
        });
    }

    private void getExercisesSuccess(List<Exercise> exercises) {
        try (Realm r = Realm.getDefaultInstance()) {
            r.executeTransaction(realm -> {
                realm.where(Exercise.class).findAll()
                        .deleteAllFromRealm();
                for (Exercise exercise : exercises) {
                    realm.insertOrUpdate(exercise);
                }
            });
        }
    }

    private void displayToast(String message) {
        Toast toast = Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT);
        toast.show();
    }

    void showAlert(String title, String message) {
        new AlertDialog.Builder(ExerciseActivity.this)
                .setTitle(title)
                .setMessage(message)
                .show();
    }

    private static class ViewHolder {
        TextView exerciseName;
    }
}
