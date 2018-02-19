package com.example.vlada.licenta.Views;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.vlada.licenta.Domain.Lift;
import com.example.vlada.licenta.R;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import io.realm.Case;
import io.realm.Realm;
import io.realm.Sort;

import static com.example.vlada.licenta.Utils.Utils.getEstimated1RM;

/**
 * Created by andrei-valentin.vlad on 2/12/2018.
 */

public class ExerciseChartFragment extends Fragment {
    List<Lift> lifts;
    private BarChart barChart;
    private Realm realm;
    private List<String> xAxis;
    private String exercise_name;

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
        this.realm = Realm.getDefaultInstance();
        barChart = rootView.findViewById(R.id.chart);

        exercise_name = getArguments().getString("exercise_name");

        populateLiftList();

        return rootView;
    }

    private void populateLiftList() {
        lifts = realm.where(Lift.class)
                .contains("exercise_name", exercise_name, Case.INSENSITIVE)
                .sort("setDate", Sort.ASCENDING)
                .findAll();
        setupChart();
    }

    private void getXAxisValues() {
        xAxis = new ArrayList<>();
        for (Lift lift : lifts) {
            String date = new SimpleDateFormat("dd MMM. yyyy ", Locale.US).format(lift.getSetDate());
            if (!xAxis.contains(date)) xAxis.add(date);
        }
    }

    private ArrayList<BarDataSet> getDataSet() {
        ArrayList<BarDataSet> dataSets;
        ArrayList<BarEntry> valueSet = new ArrayList<>();

        for (String liftDate : xAxis) {
            float highest1RM = 0;
            for (Lift lift : lifts) {
                if (new SimpleDateFormat("dd MMM. yyyy ", Locale.US).format(lift.getSetDate()).equals(liftDate)) {
                    float current1RM = getEstimated1RM(lift);
                    if (current1RM >= highest1RM)
                        highest1RM = current1RM;
                }
            }
            BarEntry barEntry = new BarEntry(highest1RM, xAxis.indexOf(liftDate));
            valueSet.add(barEntry);
        }

        BarDataSet barDataSet = new BarDataSet(valueSet, "1 rep max per date");
        barDataSet.setColors(ColorTemplate.COLORFUL_COLORS);

        dataSets = new ArrayList<>();
        dataSets.add(barDataSet);
        return dataSets;
    }

    void setupChart() {
        getXAxisValues();
        BarData data = new BarData(xAxis, getDataSet());
        barChart.setData(data);
        barChart.setDescription("Estimated 1 rep max");
        barChart.animateXY(1000, 1000);
        barChart.invalidate();
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        realm.close();
    }
}