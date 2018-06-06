package com.example.vlada.licenta.Views.Exercise;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.vlada.licenta.Base.BaseFragment;
import com.example.vlada.licenta.Domain.Lift;
import com.example.vlada.licenta.R;
import com.example.vlada.licenta.Utils.Utils;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

import io.realm.Sort;

public class ExerciseChartFragment extends BaseFragment {
    List<Lift> mLifts;
    private BarChart mBarChart;
    private List<String> mXAxis;
    private String mExerciseName;

    public ExerciseChartFragment() {

    }

    public static ExerciseChartFragment newInstance(String text) {

        ExerciseChartFragment f = new ExerciseChartFragment();
        Bundle b = new Bundle();
        b.putString("exercise_name", text);
        f.setArguments(b);
        return f;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getContext() == null) {
            Intent intent = new Intent(getActivity(), ExerciseListView.class);
            startActivity(intent);
        }

        if (getArguments() != null)
            mExerciseName = getArguments().getString("exercise_name");
        else {
            Intent intent = new Intent(getActivity(), ExerciseListView.class);
            startActivity(intent);
        }

        View rootView = inflater.inflate(R.layout.fragment_chart, container, false);
        mBarChart = rootView.findViewById(R.id.chart);
        Spinner spinner = rootView.findViewById(R.id.spinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getContext(),
                R.array.strength_chart_types, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        TextView mExerciseTV = rootView.findViewById(R.id.exercise_name_chart);
        mExerciseTV.setText(R.string.chart_type_selector);


        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                mLifts = mRealmHelper.findAllFilteredSorted(Lift.class, "exercise_name", mExerciseName, "setDate", Sort.ASCENDING);
                try {
                    setupChart(() -> {
                        switch (position) {
                            case 0:
                                return getStrengthDataSet();
                            case 1:
                                return getVolumeDataSet();
                            default:
                                return getStrengthDataSet();
                        }
                    });

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
        return rootView;
    }


    private ArrayList<BarDataSet> getStrengthDataSet() {
        ArrayList<BarDataSet> dataSets;
        ArrayList<BarEntry> valueSet = new ArrayList<>();

        mXAxis.forEach(liftDate -> {
            double highest1RM = mLifts.stream().filter(lift -> lift.date2PrettyString().equals(liftDate)).map(Utils::getEstimated1RM).max(Double::compare).orElse(0d);
            BarEntry barEntry = new BarEntry((long) highest1RM, mXAxis.indexOf(liftDate));
            valueSet.add(barEntry);
        });

        BarDataSet barDataSet = new BarDataSet(valueSet, "Highest theoretical strength");
        barDataSet.setColors(ColorTemplate.PASTEL_COLORS);

        dataSets = new ArrayList<>();
        dataSets.add(barDataSet);
        return dataSets;
    }

    private ArrayList<BarDataSet> getVolumeDataSet() {
        ArrayList<BarDataSet> dataSets;
        ArrayList<BarEntry> valueSet = new ArrayList<>();

        mXAxis.forEach(liftDate -> {
            long volume = 0;
            for (Lift lift : mLifts) {
                if (lift.date2PrettyString().equals(liftDate)) {
                    volume += lift.getWeight() * lift.getReps();
                }
            }
            BarEntry barEntry = new BarEntry(volume, mXAxis.indexOf(liftDate));
            valueSet.add(barEntry);
        });

        BarDataSet barDataSet = new BarDataSet(valueSet, "Work Volume");
        barDataSet.setColors(ColorTemplate.PASTEL_COLORS);

        dataSets = new ArrayList<>();
        dataSets.add(barDataSet);
        return dataSets;
    }


    void setupChart(Callable<ArrayList<BarDataSet>> barDataFunc) throws Exception {
        mXAxis = new ArrayList<>();
        mLifts.forEach(l -> {
            String date = l.date2PrettyString();
            if (!mXAxis.contains(date)) mXAxis.add(date);
        });
        if (getActivity() != null)
            getActivity().runOnUiThread(() -> {
                BarData data;
                try {
                    data = new BarData(mXAxis, barDataFunc.call());
                    if (getActivity() != null) {
                        getActivity().runOnUiThread(() -> {
                            mBarChart.setData(data);
                            mBarChart.setDescription("");
                            mBarChart.animateXY(750, 750);
                            mBarChart.invalidate();

                        });
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

            });
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }
}