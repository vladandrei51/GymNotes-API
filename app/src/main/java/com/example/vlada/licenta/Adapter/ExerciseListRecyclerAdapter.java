package com.example.vlada.licenta.Adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.vlada.licenta.Domain.Exercise;
import com.example.vlada.licenta.R;

import io.realm.Case;
import io.realm.Realm;
import io.realm.RealmQuery;
import io.realm.RealmRecyclerViewAdapter;
import io.realm.RealmResults;
import io.realm.Sort;

public class ExerciseListRecyclerAdapter extends RealmRecyclerViewAdapter<Exercise, ExerciseListRecyclerAdapter.ExerciseViewHolder> {

    private View.OnClickListener mListener;
    private Realm realm;
    private String mSelectedMuscleGroup;

    public ExerciseListRecyclerAdapter(RealmResults<Exercise> exercises, View.OnClickListener listener, Realm realm) {
        super(exercises, true);
        this.mListener = listener;
        this.realm = realm;
    }

    @NonNull
    @Override
    public ExerciseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.exercise_list_adapter_layout, parent, false);
        view.setOnClickListener(mListener);
        return new ExerciseViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ExerciseViewHolder holder, final int position) {
        final Exercise exercise = getItem(position);

        if (exercise != null) {
            holder.bind(exercise);
        }
    }

    public void filterStrengthExercises(String text, String selectedMuscleGroup) {
        text = text == null ? null : text.toLowerCase().trim();
        RealmQuery<Exercise> query = realm.where(Exercise.class)
                .and().not().contains("type", "Cardio", Case.INSENSITIVE)
                .and().not().contains("type", "Plyometrics", Case.INSENSITIVE)
                .and().not().contains("type", "Stretching", Case.INSENSITIVE)
                .sort("rating", Sort.DESCENDING);

        if (!(text == null || "".equals(text))) {
            query.contains("name", text, Case.INSENSITIVE)
                    .and().contains("musclegroup", selectedMuscleGroup, Case.INSENSITIVE);

        }
        updateData(query.findAll());
    }

    public void filterCardioExercises(String text) {
        text = text == null ? null : text.toLowerCase().trim();

        RealmQuery<Exercise> query = realm.where(Exercise.class)
                .and().not().contains("type", "Olympic Weightlifting", Case.INSENSITIVE)
                .and().not().contains("type", "Powerlifting", Case.INSENSITIVE)
                .and().not().contains("type", "Strength", Case.INSENSITIVE)
                .and().not().contains("type", "Strongman", Case.INSENSITIVE)
                .sort("rating", Sort.DESCENDING);

        if (!(text == null || "".equals(text))) {
            query.contains("name", text, Case.INSENSITIVE);

        }
        updateData(query.findAll());
    }





    class ExerciseViewHolder extends RecyclerView.ViewHolder {
        final Context context;
        TextView textTitle;

        ExerciseViewHolder(View itemView) {
            super(itemView);
            textTitle = itemView.findViewById(R.id.adapter_TV);
            this.context = itemView.getContext();
        }

        void bind(final Exercise exercise) {
            textTitle.setText(exercise.getName());

        }
    }
}
