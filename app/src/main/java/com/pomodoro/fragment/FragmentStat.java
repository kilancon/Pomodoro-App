package com.pomodoro.fragment;

import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.pomodoro.R;
import com.pomodoro.Utils;
import com.pomodoro.adapter.ItemAdapter;
import com.pomodoro.model.AppInfo;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FragmentStat extends Fragment {

    private Spinner spinner;
    private RecyclerView rv;
    private TextView tvDate, tvTotaltime;
    private ImageButton btnPrev, btnNext;
    private Button btnViewmore;
    private LinearLayout container;
    private BarChart barChart;
    public FragmentStat() {
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initView(view);
        Calendar cal = Calendar.getInstance();//begin time - param for query day
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.SECOND, 0);
        long currentDateInMilis = cal.getTimeInMillis();
        UsageStatsManager usm = (UsageStatsManager) view.getContext().getSystemService(Context.USAGE_STATS_SERVICE);
        ItemAdapter adapter = new ItemAdapter(view.getContext(), new ArrayList<>());
        rv.setAdapter(adapter);
        rv.setLayoutManager(new LinearLayoutManager(view.getContext(), RecyclerView.VERTICAL, false));
        //spinner
        ArrayAdapter<String> aa = new ArrayAdapter<>(view.getContext(), R.layout.item_spinner, Arrays.asList(getResources().getStringArray(R.array.stat_option)));
        spinner.setAdapter(aa);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int pos, long l) {
                if(pos==0){ //select today
                    btnPrev.setVisibility(View.VISIBLE);
                    container.setVisibility(View.VISIBLE);
                    barChart.setVisibility(View.INVISIBLE);
                    cal.setTimeInMillis(currentDateInMilis);
                    tvDate.setText(new SimpleDateFormat("MMM dd").format(cal.getTime()));
                    //get installed app
                    ArrayList<AppInfo> dsApp = Utils.getAppInfoList(getContext());
                    //get time used in foreground
                    long total = 0;
                    Map<String, UsageStats> ds = usm.queryAndAggregateUsageStats(currentDateInMilis, currentDateInMilis+1);
                    for(AppInfo app: dsApp){
                        if(ds.get(app.getPackageName()) != null) {
                            app.setTimeUsage(ds.get(app.getPackageName()).getTotalTimeInForeground());
                            total += app.getTimeUsage();
                        }
                    }
                    int sec = (int) (total/1000);
                    int hh = sec/3600;
                    int mm = (sec%3600)/60;
                    tvTotaltime.setText(String.format("%02dh %02dm",hh,mm));
                    Collections.sort(dsApp);
                    adapter.setDs(get4app((dsApp)));
                    btnViewmore.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            adapter.setDs(dsApp);
                        }
                    });
                }
                else{ //select week
                    container.setVisibility(View.INVISIBLE);
                    barChart.setVisibility(View.VISIBLE);
                    btnNext.setVisibility(View.INVISIBLE); btnPrev.setVisibility(View.INVISIBLE);
                    Calendar begin = Calendar.getInstance();
//                    begin.add(Calendar.WEEK_OF_YEAR,-1); // last week
                    begin.set(Calendar.DAY_OF_WEEK, 1);
                    begin.set(Calendar.MINUTE, 0);
                    begin.set(Calendar.HOUR_OF_DAY, 0);
                    begin.set(Calendar.SECOND, 0);
                    String datefrom = new SimpleDateFormat("MMM dd").format(begin.getTime());
                    //set data for recycler view
                    long day = 24*3600000;
                    Map<String, UsageStats> ds = usm.queryAndAggregateUsageStats(begin.getTimeInMillis(), begin.getTimeInMillis()+day*7);
                    ArrayList<AppInfo> dsApp = Utils.getAppInfoList(getContext());
                    for(AppInfo app: dsApp){
                        if(ds.get(app.getPackageName()) != null)
                            app.setTimeUsage(ds.get(app.getPackageName()).getTotalTimeInForeground());
                    }
                    Collections.sort(dsApp);
                    adapter.setDs(get4app(dsApp));
                    btnViewmore.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            adapter.setDs(dsApp);
                        }
                    });
                    Map<Integer, Float> stats = new HashMap<>();
                    for(int ii = 1; ii <= 7; ii++) {
                        Map<String, UsageStats> xxx = usm.queryAndAggregateUsageStats((begin.getTimeInMillis()), begin.getTimeInMillis() + 1);
                        long sum = 0;
                        for (AppInfo app : dsApp) {
                            if (xxx.get(app.getPackageName()) != null)
                                sum += xxx.get(app.getPackageName()).getTotalTimeInForeground();
                        }
                        Log.e("Date", begin.getTime().toString() + "Sum: " +sum);
                        begin.add(Calendar.DAY_OF_WEEK, 1); //next day
                        stats.put(ii, (float)sum/3600000);
                        if(ii == 6) datefrom = datefrom + " - " +new SimpleDateFormat("MMM dd").format(begin.getTime()) + " (Week " +begin.get(Calendar.WEEK_OF_YEAR) +")";
                    }
                    drawChart(barChart, stats);
                    tvDate.setText(datefrom);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });
        //
        btnPrev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                btnNext.setVisibility(View.VISIBLE);
                cal.add(Calendar.DAY_OF_YEAR,-1);
                tvDate.setText(new SimpleDateFormat("MMM dd").format(cal.getTime()));
                ArrayList<AppInfo> dsApp = Utils.getAppInfoList(getContext());
                Map<String, UsageStats> ds = usm.queryAndAggregateUsageStats(cal.getTimeInMillis(), cal.getTimeInMillis()+ 1);
                for(AppInfo app: dsApp){
                    if(ds.get(app.getPackageName()) != null)
                        app.setTimeUsage(ds.get(app.getPackageName()).getTotalTimeInForeground());
                }
                long total = 0;
                for(AppInfo app: dsApp){
                    if(ds.get(app.getPackageName()) != null) {
                        app.setTimeUsage(ds.get(app.getPackageName()).getTotalTimeInForeground());
                        total += app.getTimeUsage();
                    }
                }
                int sec = (int) (total/1000);
                int hh = sec/3600;
                int mm = (sec%3600)/60;
                tvTotaltime.setText(String.format("%02dh %02dm",hh,mm));
                Collections.sort(dsApp);
                adapter.setDs(get4app((dsApp)));
                btnViewmore.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        adapter.setDs(dsApp);
                    }
                });
            }
        });
        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cal.add(Calendar.DAY_OF_YEAR,1);
                tvDate.setText(new SimpleDateFormat("MMM dd").format(cal.getTime()));
                if(cal.getTimeInMillis() > System.currentTimeMillis()-24*3600000) btnNext.setVisibility(View.INVISIBLE);

                Map<String, UsageStats> ds = usm.queryAndAggregateUsageStats(cal.getTimeInMillis(), cal.getTimeInMillis() + 1);
                //get installed app
                ArrayList<AppInfo> dsApp = Utils.getAppInfoList(getContext());
                //
                for(AppInfo app: dsApp){
                    if(ds.get(app.getPackageName()) != null)
                        app.setTimeUsage(ds.get(app.getPackageName()).getTotalTimeInForeground());
                }
                long total = 0;
                for(AppInfo app: dsApp){
                    if(ds.get(app.getPackageName()) != null) {
                        app.setTimeUsage(ds.get(app.getPackageName()).getTotalTimeInForeground());
                        total += app.getTimeUsage();
                    }
                }
                int sec = (int) (total/1000);
                int hh = sec/3600;
                int mm = (sec%3600)/60;
                tvTotaltime.setText(String.format("%02dh %02dm",hh,mm));
                Collections.sort(dsApp);
                adapter.setDs(get4app((dsApp)));
                btnViewmore.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        adapter.setDs(dsApp);
                    }
                });
            }
        });
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_stat, container, false);
    }

    private void initView(View view){
        tvDate = view.findViewById(R.id.tvDate);
        tvTotaltime = view.findViewById(R.id.tvTotaltime);
        rv = view.findViewById(R.id.recycleView);
        spinner = view.findViewById(R.id.spinner);
        btnPrev = view.findViewById(R.id.btnPrev);
        btnNext = view.findViewById(R.id.btnNext);
        container = view.findViewById(R.id.container);
        barChart = view.findViewById(R.id.idBarChart);
        btnViewmore = view.findViewById(R.id.btnViewmore);
    }

    private ArrayList<AppInfo> get4app(ArrayList<AppInfo> ds){
        ArrayList<AppInfo> apps = new ArrayList<>();
        for(int i = 0; i < 4; i++)
            if(ds.size() > i) apps.add(ds.get(i));
        return apps;
    }

    private void drawChart(BarChart bc, Map<Integer, Float> stats){
        ArrayList<BarEntry> ds = new ArrayList<>();
        for(Integer i: stats.keySet()){
            ds.add(new BarEntry((float)i, stats.get(i)>12?11:stats.get(i)));
        }
        ArrayList<String> labels = new ArrayList<>();
        labels.add("");labels.add("S");labels.add("M");labels.add("T");labels.add("W");
        labels.add("Th");labels.add("F");labels.add("Sa");

        BarDataSet bds = new BarDataSet(ds, null);
        bds.setColor(getResources().getColor(R.color.white));
        BarData barData = new BarData(bds);
        barData.setDrawValues(false);
        barData.setBarWidth(0.3f);
        bc.setData(barData);
        bc.setDescription(null);
        bc.setTouchEnabled(false);
        bc.getLegend().setEnabled(false);

        XAxis x = bc.getXAxis();
        x.setValueFormatter(new IndexAxisValueFormatter(labels));
        x.setPosition(XAxis.XAxisPosition.BOTTOM);
        x.setDrawGridLines(false);
        x.setTextColor(getResources().getColor(R.color.white));
        x.setAxisLineColor(getResources().getColor(R.color.brown2));

        YAxis y = bc.getAxisRight();
        y.setDrawAxisLine(false);
        y.setDrawLabels(false);
        y.setDrawGridLines(false);

        YAxis y2 = bc.getAxisLeft();
        y2.setDrawAxisLine(false);
        y2.setAxisMinimum(0f);
        y2.setGridColor(getResources().getColor(R.color.brown2));
        y2.setTextColor(getResources().getColor(R.color.white));
        y2.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                return ""+(int)value+"h";
            }
        });
        y2.setPosition(YAxis.YAxisLabelPosition.OUTSIDE_CHART);

    }
}