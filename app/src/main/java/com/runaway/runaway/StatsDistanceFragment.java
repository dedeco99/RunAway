package com.runaway.runaway;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.anychart.AnyChart;
import com.anychart.AnyChartView;
import com.anychart.chart.common.dataentry.DataEntry;
import com.anychart.chart.common.dataentry.ValueDataEntry;
import com.anychart.charts.Cartesian;
import com.anychart.core.cartesian.series.Column;
import com.anychart.enums.Anchor;
import com.anychart.enums.HoverMode;
import com.anychart.enums.Position;
import com.anychart.enums.TooltipPositionMode;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

public class StatsDistanceFragment extends Fragment implements RequestGetHandler{
    private Context context;
    private TextView distanceValue;
    private AnyChartView anyChartView;
    private ProgressBar distanceProgress;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_stats_distance, container, false);
        context=this.getContext();

        distanceValue = view.findViewById(R.id.distanceValue);
        anyChartView = view.findViewById(R.id.distanceChart);
        distanceProgress = view.findViewById(R.id.distanceProgress);

        //getStats();

        return view;
    }

    public void getStats(){
        String url = "https://api.mlab.com/api/1/databases/runaway/collections/tracks?apiKey=gMqDeofsYMBMCO6RJBydS59weP9OCJZf";
        RequestGetHandler requestGetHandler = this;
        RequestSingleton.getInstance().getRequest(url, requestGetHandler,context);
    }

    @Override
    public void handleGetRequest(JSONArray response) {
        Integer distance=0;

        for(int i=0;i<response.length();i++){
            try {
                distance+=response.getJSONObject(i).getInt("distance");
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        String responseText = "Response: " + distance;
        distanceValue.setText(responseText);

        makeChart();
    }

    public void makeChart(){
        anyChartView.setProgressBar(distanceProgress);
        Cartesian cartesian = AnyChart.column();

        List<DataEntry> data = new ArrayList<>();
        data.add(new ValueDataEntry("Monday", 1372));
        data.add(new ValueDataEntry("Tuesday", 3213));
        data.add(new ValueDataEntry("Wednesday", 2112));
        data.add(new ValueDataEntry("Thursday", 3245));
        data.add(new ValueDataEntry("Friday", 224));
        data.add(new ValueDataEntry("Saturday", 1234));
        data.add(new ValueDataEntry("Sunday", 1576));

        Column column = cartesian.column(data);

        column.tooltip()
                .titleFormat("{%X}")
                .position(Position.CENTER_BOTTOM)
                .anchor(Anchor.CENTER_BOTTOM)
                .offsetX(0d)
                .offsetY(5d)
                .format("{%Value} m");

        cartesian.animation(true);

        cartesian.tooltip().positionMode(TooltipPositionMode.POINT);
        cartesian.interactivity().hoverMode(HoverMode.BY_X);

        cartesian.xAxis(0).title("Day");
        cartesian.yAxis(0).title("Meters");

        anyChartView.setChart(cartesian);
    }
}
