package com.example.mychingu;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.data.*;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;

import java.util.*;

public class ReportActivity extends AppCompatActivity {

    private PieChart genderChart;
    private BarChart birthdayChart;
    private DatabaseHelper db;
    private int userId = 1; // 🧠 Replace with actual logged-in user ID

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report);

        genderChart = findViewById(R.id.genderChart);
        birthdayChart = findViewById(R.id.birthdayChart);
        db = new DatabaseHelper(this);

        showGenderChart();
        showBirthdayChart();
    }

    private void showGenderChart() {
        Map<String, Integer> genderData = db.getGenderCounts(userId);
        List<PieEntry> entries = new ArrayList<>();

        for (Map.Entry<String, Integer> entry : genderData.entrySet()) {
            entries.add(new PieEntry(entry.getValue(), entry.getKey()));
        }

        PieDataSet dataSet = new PieDataSet(entries, "Gender");
        dataSet.setColors(ColorTemplate.MATERIAL_COLORS);
        PieData data = new PieData(dataSet);

        genderChart.setData(data);
        genderChart.setCenterText("Gender");
        genderChart.setEntryLabelColor(android.graphics.Color.BLACK);
        genderChart.animateY(1000);
        genderChart.invalidate();
    }

    private void showBirthdayChart() {
        int[] monthCounts = db.getBirthdayMonthCounts(userId);
        List<BarEntry> entries = new ArrayList<>();
        String[] months = {
                "Jan", "Feb", "Mar", "Apr", "May", "Jun",
                "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"
        };

        for (int i = 0; i < monthCounts.length; i++) {
            entries.add(new BarEntry(i, monthCounts[i]));
        }

        BarDataSet dataSet = new BarDataSet(entries, "Birthdays");
        dataSet.setColors(ColorTemplate.COLORFUL_COLORS);
        BarData data = new BarData(dataSet);

        birthdayChart.setData(data);
        birthdayChart.getDescription().setText("Birthdays by Month");
        birthdayChart.getXAxis().setValueFormatter(new IndexAxisValueFormatter(months));
        birthdayChart.getXAxis().setGranularity(1f);
        birthdayChart.getXAxis().setLabelCount(12);
        birthdayChart.getXAxis().setPosition(com.github.mikephil.charting.components.XAxis.XAxisPosition.BOTTOM);
        birthdayChart.animateY(1000);
        birthdayChart.invalidate();
    }
}