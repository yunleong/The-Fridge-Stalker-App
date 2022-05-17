package com.example.thestockers;

import android.content.ContentValues;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.anychart.APIlib;
import com.anychart.AnyChart;
import com.anychart.AnyChartView;
import com.anychart.chart.common.dataentry.DataEntry;
import com.anychart.chart.common.dataentry.ValueDataEntry;
import com.anychart.charts.Cartesian;
import com.anychart.charts.Pie;
import com.anychart.core.annotations.Line;

import java.util.ArrayList;
import java.util.List;


public class SummaryFragment extends Fragment {

    AnyChartView chartView,graphView;
    String[] type = {"Waste", "Consumption", "Neither"};
    int[] qty = {20, 35, 5};

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_summary,container,false);
        chartView = view.findViewById(R.id.pie_chart_view);
        graphView = view.findViewById(R.id.line_graph_view);
        drawGraphs();
        return view;
    }


    public void drawGraphs(){
        APIlib.getInstance().setActiveAnyChartView(chartView);
        setupPieChart();
        APIlib.getInstance().setActiveAnyChartView(graphView);
        setupLineGraph();
    }

    public void setupPieChart(){
        /*HomeFragment home = new HomeFragment();
        int[] qty = home.getWasteConsumptionData();*/
        Pie pie = AnyChart.pie();
        List<DataEntry> dataEntries = new ArrayList<>();

        for (int i = 0; i < type.length; i++){
            dataEntries.add(new ValueDataEntry(type[i],qty[i]));
        }

        pie.data(dataEntries);
        //dark mode background
        //pie.background().fill("black");
        chartView.setChart(pie);
    }

    public void setupLineGraph(){
        Cartesian bar = AnyChart.column();
        String[] month = {"Nov", "Dec", "Jan", "Feb", "Mar", "Apr"};
        double[] spending = {245.80, 302.20, 270.40, 332.51, 262.00, 292.19};

        List<DataEntry> spendingData = new ArrayList<>();

        for (int i = 0; i < month.length; i++){
            spendingData.add(new ValueDataEntry(month[i],spending[i]));
        }

        bar.data(spendingData);
        //dark mode background
        //bar.background().fill("black");
        graphView.setChart(bar);

    }

}