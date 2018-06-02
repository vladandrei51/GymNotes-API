package com.example.vlada.licenta.Adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.vlada.licenta.Domain.Exercise;
import com.example.vlada.licenta.R;

import io.realm.RealmRecyclerViewAdapter;
import io.realm.RealmResults;

public class ExerciseListRecyclerAdapter extends RealmRecyclerViewAdapter<Exercise, ExerciseListRecyclerAdapter.ExerciseViewHolder> {

    private View.OnClickListener mListener;

    public ExerciseListRecyclerAdapter(RealmResults<Exercise> exercises, View.OnClickListener listener) {
        super(exercises, true);
        this.mListener = listener;
    }

    @Override
    public ExerciseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.exercise_list_adapter_layout, parent, false);
        view.setOnClickListener(mListener);
        return new ExerciseViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ExerciseViewHolder holder, final int position) {
        final Exercise exercise = getItem(position);

        if (exercise != null) {
            holder.bind(exercise);
        }
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
