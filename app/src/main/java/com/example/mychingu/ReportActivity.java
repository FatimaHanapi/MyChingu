package com.example.mychingu;

import android.os.Bundle;
import android.widget.Toast;
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
    private int userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report);

        // Initialize views and database
        genderChart = findViewById(R.id.genderChart);
        birthdayChart = findViewById(R.id.birthdayChart);
        db = new DatabaseHelper(this);

        // Get user ID from intent
        userId = getIntent().getIntExtra("user_id", -1);

        if (userId == -1) {
            Toast.makeText(this, "Invalid user ID", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        showGenderChart();
        showBirthdayChart();
    }

    private void showGenderChart() {
        Map<String, Integer> genderData = db.getGenderCounts(userId);
        List<PieEntry> entries = new ArrayList<>();

        for (Map.Entry<String, Integer> entry : genderData.entrySet()) {
            if (entry.getValue() > 0) {
                entries.add(new PieEntry(entry.getValue(), entry.getKey()));
            }
        }

        if (entries.isEmpty()) {
            genderChart.clear();
            genderChart.setNoDataText("No gender data available.");
            return;
        }

        PieDataSet dataSet = new PieDataSet(entries, "Gender");
        dataSet.setColors(ColorTemplate.MATERIAL_COLORS);

        PieData data = new PieData(dataSet);
        data.setValueTextColor(android.graphics.Color.BLACK);
        data.setValueTextSize(12f);

        genderChart.setData(data);
        genderChart.setCenterText("Gender");
        genderChart.setEntryLabelColor(android.graphics.Color.BLACK);
        genderChart.animateY(1000);
        genderChart.invalidate();
    }

    private void showBirthdayChart() {
        int[] monthCounts = db.getBirthdayMonthCounts(userId);

        if (monthCounts == null || monthCounts.length == 0) {
            birthdayChart.clear();
            birthdayChart.setNoDataText("No birthday data available.");
            return;
        }

        boolean hasData = false;
        for (int count : monthCounts) {
            if (count > 0) {
                hasData = true;
                break;
            }
        }

        if (!hasData) {
            birthdayChart.clear();
            birthdayChart.setNoDataText("No birthday data available.");
            return;
        }

        List<BarEntry> entries = new ArrayList<>();
        String[] months = {
                "Jan", "Feb", "Mar", "Apr", "May", "Jun",
                "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"
        };

        for (int i = 0; i < 12; i++) {
            entries.add(new BarEntry(i, monthCounts[i]));
        }

        BarDataSet dataSet = new BarDataSet(entries, "Birthdays");
        dataSet.setColors(ColorTemplate.COLORFUL_COLORS);
        BarData data = new BarData(dataSet);
        data.setBarWidth(0.9f);

        birthdayChart.setData(data);
        birthdayChart.setFitBars(true);
        birthdayChart.getDescription().setText("Birthdays by Month");

        birthdayChart.getXAxis().setValueFormatter(new IndexAxisValueFormatter(months));
        birthdayChart.getXAxis().setGranularity(1f);
        birthdayChart.getXAxis().setLabelCount(12);
        birthdayChart.getXAxis().setPosition(
                com.github.mikephil.charting.components.XAxis.XAxisPosition.BOTTOM
        );

        birthdayChart.getAxisRight().setEnabled(false);
        birthdayChart.animateY(1000);
        birthdayChart.invalidate();
    }

}