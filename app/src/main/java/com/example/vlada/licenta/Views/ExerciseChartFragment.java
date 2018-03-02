package com.example.vlada.licenta.Views;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.vlada.licenta.Base.BaseFragment;
import com.example.vlada.licenta.Domain.Lift;
import com.example.vlada.licenta.R;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.util.ArrayList;
import java.util.List;

import io.realm.Sort;

import static com.example.vlada.licenta.Utils.Utils.getEstimated1RM;

/**
 * Created by andrei-valentin.vlad on 2/12/2018.
 */

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
        View rootView = inflater.inflate(R.layout.fragment_chart, container, false);
        mBarChart = rootView.findViewById(R.id.chart);
        if (getArguments() != null)
            mExerciseName = getArguments().getString("exercise_name");

        populateLiftList();
        setupChart();


        return rootView;
    }

    private void populateLiftList() {
        mLifts = mRealmHelper.findAllFilteredSorted(Lift.class, "exercise_name", mExerciseName, "setDate", Sort.ASCENDING);
    }

    private void getXAxisValues() {
        mXAxis = new ArrayList<>();
        mLifts.forEach(l -> {
            String date = l.date2PrettyString();
            if (!mXAxis.contains(date)) mXAxis.add(date);
        });
    }

    private ArrayList<BarDataSet> getDataSet() {
        ArrayList<BarDataSet> dataSets;
        ArrayList<BarEntry> valueSet = new ArrayList<>();

        mXAxis.forEach(liftDate -> {
            float highest1RM = 0;
            for (Lift lift : mLifts) {
                if (lift.date2PrettyString().equals(liftDate)) {
                    float current1RM = getEstimated1RM(lift);
                    if (current1RM >= highest1RM)
                        highest1RM = current1RM;
                }
            }
            BarEntry barEntry = new BarEntry(highest1RM, mXAxis.indexOf(liftDate));
            valueSet.add(barEntry);
        });

        BarDataSet barDataSet = new BarDataSet(valueSet, "Strength/day");
        barDataSet.setColors(ColorTemplate.COLORFUL_COLORS);

        dataSets = new ArrayList<>();
        dataSets.add(barDataSet);
        return dataSets;
    }

    void setupChart() {
        getXAxisValues();
        BarData data = new BarData(mXAxis, getDataSet());
        if (getActivity() != null) {
            getActivity().runOnUiThread(() -> {
                mBarChart.setData(data);
                mBarChart.setDescription("Estimated maximum strength");
                mBarChart.animateXY(1000, 1000);
                mBarChart.invalidate();

            });
        }
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }
}