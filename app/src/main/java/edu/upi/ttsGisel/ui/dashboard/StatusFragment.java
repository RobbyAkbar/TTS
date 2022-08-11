package edu.upi.ttsGisel.ui.dashboard;

import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;

import java.util.HashMap;
import java.util.Map;

import edu.upi.ttsGisel.R;
import edu.upi.ttsGisel.utils.AppUtils;
import edu.upi.ttsGisel.utils.Config;

/**
 * A simple {@link Fragment} subclass.
 */
public class StatusFragment extends SimpleFragment {

    static final String ARG_OBJECT = "object";
    private ProgressBar progressBar;
    private String level;

    @NonNull
    static Fragment newInstance() {
        return new StatusFragment();
    }

    @SuppressWarnings("FieldCanBeLocal")
    private PieChart chart;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_status, container, false);

        chart = root.findViewById(R.id.pieChart1);
        progressBar = root.findViewById(R.id.progressBar);

        chart.getDescription().setEnabled(false);

        Typeface tf = Typeface.createFromAsset(context.getAssets(), "OpenSans-Light.ttf");

        chart.setCenterTextTypeface(tf);
        chart.setCenterText(generateCenterText());
        chart.setCenterTextSize(10f);
        chart.setCenterTextTypeface(tf);

        // radius of the center hole in percent of maximum radius
        chart.setHoleRadius(45f);
        chart.setTransparentCircleRadius(50f);

        Legend l = chart.getLegend();
        l.setVerticalAlignment(Legend.LegendVerticalAlignment.TOP);
        l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.RIGHT);
        l.setOrientation(Legend.LegendOrientation.VERTICAL);
        l.setDrawInside(false);

        chart.setData(generatePieData(""));

        level = getArguments() != null ? getArguments().getString(Config.LEVEL, "") : null;
        if (AppUtils.Companion.isNetworkStatusAvialable(requireContext())) getProgressGuess();

        return root;
    }

    private void getProgressGuess(){
        progressBar.setVisibility(View.VISIBLE);
        String url = Config.GET_PROGRESS_GUESS_URL;
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                chart.setData(generatePieData(response));
                chart.invalidate();
                progressBar.setVisibility(View.INVISIBLE);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(context, error.getMessage(), Toast.LENGTH_LONG).show();
                progressBar.setVisibility(View.INVISIBLE);
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put(Config.ID_USER_SHARED_PREF, AppUtils.Companion.getIdUser(context));
                params.put(Config.LEVEL, level);
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(context);
        requestQueue.add(stringRequest);
    }

    private SpannableString generateCenterText() {
        SpannableString s = new SpannableString("Answer \nStatistics Report");
        s.setSpan(new RelativeSizeSpan(2f), 0, 8, 0);
        s.setSpan(new ForegroundColorSpan(Color.GRAY), 8, s.length(), 0);
        return s;
    }
}
