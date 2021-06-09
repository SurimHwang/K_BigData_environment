package com.example.naver_map_api;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.SpannableString;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.BarLineChartBase;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;

import java.util.ArrayList;
import java.util.HashMap;


// 서브 화면
public class SubActivity extends AppCompatActivity {
    private TextView tv_result;
    private TextView tv_detail;
    private TextView tv_address;

    // 클릭한 마커의 지역명
    private String str;

    private BarChart chart1;
    private LineChart chart2;
    private Button btn_help;
    // 그래프 그릴 때 MPAndroidChart 참고 바람
    // https://jeongupark-study-house.tistory.com/159

    // 막대 색깔을 위해 해쉬맵<수위, 결과> 생성
    private HashMap<Float, String> colorBar = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d("지건2", "6번째 실행");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sub);

        // 관측소 이름(key 값) 받아오기
        str = getIntent().getStringExtra("지하수");
        // 관측 결과 화면에 띄우기

        // 관측소 이름 띄우기
        tv_address = findViewById(R.id.tv_address);
        tv_address.setText(String.valueOf(str)+" 지하수 관측소");

        // 관측 결과 띄우기
        tv_result = findViewById(R.id.tv_result);
        String day_result = MainActivity.resultMap.get(str);

        // 세부사항
        tv_detail = findViewById(R.id.tv_detail);
        String detail;

        switch(day_result) {
            case "심각" :
                tv_result.setText(day_result + "\n(극심한 가뭄)");
                tv_result.setBackgroundColor(Color.BLACK);
                detail = "기상가뭄\n" +
                        "-최근 6개월 누적강수량이 이용한 표준강수지수 –2.0이하(평년대비 약 45%)이하가 20일 이상 기상가뭄이 지속되어 전국적인 가뭄 피해가 예상되는 경우로 하되, 지역별 강수 특성을 반영할 수 있음\n" +
                        "   * 표준강수지수 : 일정기간의 누적강수량과 과거 동일기간의 강수량을 비교하여\n"   +
                        "                        가뭄정도를 나타내는 지수\n" +
                        "\n"+
                        "농업용수\n" +
                        "-[논] 영농기 평년 저수율 40% 이하인 경우\n" +
                        "-[밭] 영농기 토양 유효수분율 15% 이하\n" +
                        "※ 위와 같은 상황에서 대규모 가뭄피해가 발생하였거나 예상 되는 경우 관계부처 협의를 통해 결정\n" +
                        "\n"+
                        "-생활 및 공업용수\n" +
                        "-하천 및 수자원시설에서 생활 및 공업용수 부족이 확대되어 하천 및 댐･저수지 등에서 생활 및 공업용수 공급 제한이 발생하였거나 필요한 경우";
                break;
            case "경계" :
                tv_result.setText(day_result + "\n(심한 가뭄)");
                tv_result.setBackgroundColor(Color.RED);
                detail = "기상가뭄\n" +
                        "-최근 6개월 누적강수량을 이용한 표준강수지수 -2.0이하(평년대비 약 45%)이하로 기상가뭄이 지속될 것으로 예상되는 경우로 하되, 지역별 강수 특성을 반영할 수 있음\n" +
                        "   * 표준강수지수 : 일정기간의 누적강수량과 과거 동일기간의 강수량을 비교하여\n"   +
                        "                        가뭄정도를 나타내는 지수\n" +
                        "\n"+
                        "농업용수\n" +
                        "-[논] 영농기 평년 저수율 50% 이하인 경우\n" +
                        "-[밭] 영농기 토양 유효 수분율 30% 이하\n" +
                        "  ※ 위와 같은 상황에서 가뭄피해가 발생하였 거나 예상되는 경우\n" +
                        "\n"+
                        "생활 및 공업용수\n" +
                        "-하천 및 수자원시설에서 생활 및 공업용수 부족이 일부 발생하였거나 발생이 우려되어 하천유지용수, 농업용수 공급의 제한이 필요한 경우";
                break;
            case "주의" :
                tv_result.setText(day_result + "\n(보통 가뭄)");
                tv_result.setBackgroundColor(-32985);
                detail = "기상가뭄\n" +
                        "-최근 6개월 누적강수량을 이용한 표준강수지수 -1.5이하(평년대비 약 55%)이하로 기상가뭄이 지속될 것으로 예상되는 경우로 하되, 지역별 강수 특성을 반영할 수 있음\n" +
                        "   * 표준강수지수 : 일정기간의 누적강수량과 과거 동일기간의 강수량을 비교하여\n"   +
                        "                        가뭄정도를 나타내는 지수\n" +
                        "\n"+
                        "농업용수\n" +
                        "-[논] 영농기 평년 저수율의 60% 이하, 비영농기 저수율이 다가오는 영농기 모내기 용수공급에 물 부족이 예상되는 경우\n" +
                        "-[밭] 영농기 토양 유효 수분율이 45% 이하\n" +
                        "\n"+
                        "생활 및 공업용수\n" +
                        "-하천 및 수자원시설의 수위가 낮아 하천의 하천유지유량이 부족하거나 댐･저수지에서 하천유지용수 공급 등의 제한이 필요한 경우";
                break;
            case "관심" :
                tv_result.setText(day_result + "\n(약한 가뭄)");
                tv_result.setBackgroundColor(-3584);
                detail = "기상가뭄\n" +
                        "-최근 6개월 누적강수량을 이용한 표준강수지수 -1.0이하(평년대비 약 65%)이하로 기상가뭄이 지속될 것으로 예상되는 경우로 하되, 지역별 강수 특성을 반영할 수 있음\n" +
                        "   * 표준강수지수 : 일정기간의 누적강수량과 과거 동일기간의 강수량을 비교하여\n"   +
                        "                        가뭄정도를 나타내는 지수\n" +
                        "\n"+
                        "농업용수\n" +
                        "-[논] 영농기 평년 저수율의 70% 이하인 경우\n" +
                        "-[밭] 영농기 토양 유효 수분율이 60% 이하\n" +
                        "\n"+
                        "생활 및 공업용수\n" +
                        "-하천 및 수자원시설의 수위가 평년에 비해 낮아 정상적인 용수공급을 위해 생활 및 공업용수의 여유량을 관리하는 등 가뭄대비가 필요한 경우";
                break;
            case "정상" :
                tv_result.setText(day_result + "\n");
                tv_result.setBackgroundColor(-15806139);
                detail = "기상가뭄\n" +
                        "-최근 6개월 누적강수량을 이용한 표준강수지수 -1.0이상(평년대비 약 65%)이상으로 기상가뭄이 지속될 것으로 예상되는 경우로 하되, 지역별 강수 특성을 반영할 수 있음\n" +
                        "   * 표준강수지수 : 일정기간의 누적강수량과 과거 동일기간의 강수량을 비교하여\n"   +
                        "                        가뭄정도를 나타내는 지수\n" +
                        "\n"+
                        "농업용수\n" +
                        "-[논] 영농기 평년 저수율의 70% 이상인 경우\n" +
                        "-[밭] 영농기 토양 유효 수분율이 60% 이상\n" +
                        "\n"+
                        "생활 및 공업용수\n" +
                        "-하천 및 수자원시설의 수위가 평년과 비슷해 가뭄대비가 필요하지 않은 경우";
                break;
            default:
                tv_result.setBackgroundColor(Color.BLUE);
                detail = "데이터가 없습니다.";
        }

        tv_detail.setText(detail);

        btn_help = findViewById(R.id.btn_help);
        btn_help.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SubActivity.this, HtdActivity.class);
                startActivity(intent);
            }
        });
        // ************** 최근 7일 그래프 그리기 **************
        // ***************************************************
        // 한달 그래프 띄우기 일주일마다 라벨
        chart1 = findViewById(R.id.Barchart_recent);       // 최근 일주일 그래프
        ArrayList<BarEntry> values1 = new ArrayList<>();       // 점 ArrayList 생성
        int day1 = 31;
        switch(MainActivity.recent_month) {
            case 1: case 3: case 5: case 7:
            case 8: case 10: case 12:
                day1 = 31;
                break;
            case 4: case 6: case 9: case 11:
                day1 = 30;
                break;
            case 2:
                day1 = 28;
        }
        float yesterday_height = 0;
        for (int i = 1; i <= day1; i++) {
            String key = str.concat("/")                                            // 지역
                    .concat(String.valueOf(MainActivity.recent_month)).concat("/")  // 최근 월
                    .concat(String.valueOf(i));
            String data = MainActivity.dataMap.get(key);                            // 수위 및 결과
            int seperator;
            float height;
            String result;
            if(data != null) {
                seperator = data.indexOf("/");                                      // 구분자 '/' 설정
                height = Float.valueOf(data.substring(0, seperator));               // 수위 추출
                result = data.substring(seperator + 1);                             // 결과
                yesterday_height = height;
            } else {
                height = yesterday_height;
                result = "데이터 없음";
            }
            colorBar.put(height, result);                                           // 막대 색깔
            values1.add(new BarEntry(i, height));
        }
        bar_chart_making(values1, chart1);
        chart1.getXAxis().setValueFormatter(new DayAxisValueFormatter(chart1));

        // ************** 월별 평균 그래프 그리기 **************
        // ***************************************************
        chart2 = findViewById(R.id.Linechart_past);         // 과거 그래프
        ArrayList<Entry> values2 = new ArrayList<>();       // 점 ArrayList 생성
        ArrayList<String> mlabel = new ArrayList<>();       // 월 라벨
        for (int i = 1; i <= MainActivity.recent_month; i++) {       // 월 단위
            float sum = 0;
            float average;
            int cnt =  0;
            mlabel.add(i+ "월");
            int day2 = 31;     // 월말 지정
            switch(i) {
                case 1: case 3: case 5: case 7:
                case 8: case 10: case 12:
                    day2 = 31;
                    break;
                case 4: case 6: case 9: case 11:
                    day2 = 30;
                    break;
                case 2:
                    day2 = 28;
            }
            for(int j = 1; j <= day2; j++) {      // 일 단위
                String key = str.concat("/")                                                // 지역
                        .concat(String.valueOf(i)).concat("/")                              // 매월
                        .concat(String.valueOf(j));                                         // 모든 날짜
                String data = MainActivity.dataMap.get(key);                                // 수위 및 결과
                int seperator;
                float height;
                if (data != null) {      // 데이터가 없는 경우도 있는 것 같음
                    seperator = data.indexOf("/");                                      // 구분자 '/' 설정
                    height = Float.valueOf(data.substring(0, seperator));               // 수위 추출
                    sum += height;
                    cnt++;
                } else {
                    continue;
                }
            }
            average = sum / cnt;
            Log.d("ㄹㅇ", String.valueOf(average));
            values2.add(new Entry(i, average));
        }
        line_chart_making(values2, chart2);
        chart2.getXAxis().setValueFormatter(new MyXAxisValueFormatter(mlabel));
    }

    // 관측 결과에 따라 막대 색깔 바꾸는 기능
    public class MyBarDataSet extends BarDataSet {
        public MyBarDataSet(ArrayList<BarEntry> yVals, String label) {
            super(yVals, label);
        }

        @Override
        public int getColor(int index) {
            Float height = getEntryForIndex(index).getY();
            if ("정상".equalsIgnoreCase(colorBar.get(height))) {           // "정상"일 때의 막대 색깔
                return mColors.get(0);
            } else if ("관심".equalsIgnoreCase(colorBar.get(height))) {    // "관심"일 때의 막대 색깔
                return mColors.get(1);
            } else if ("주의".equalsIgnoreCase(colorBar.get(height))) {    // "주의"일 때의 막대 색깔
                return mColors.get(2);
            } else if ("경계".equalsIgnoreCase(colorBar.get(height))) {    // "경계"일 때의 막대 색깔
                return mColors.get(3);
            } else if ("심각".equalsIgnoreCase(colorBar.get(height))) {    // "경계"일 때의 막대 색깔
                return mColors.get(4);
            } else {                                                       // "null" 값
                return mColors.get(5);
            }
        }
    }

    // 막대그래프 그리는 함수
    private void bar_chart_making(ArrayList<BarEntry> values, BarChart chart) {
        MyBarDataSet set = new MyBarDataSet(values, "");
        set.setColors(-15806139, -3584, -32985, Color.RED, Color.BLACK, Color.BLUE);
        BarData data = new BarData(set);
        chart.setData(data);

        data.setValueTextSize(8f);                                  // 막대 위 라벨 텍스트 크기
        chart.setVisibleXRangeMaximum(15);                  // 15개의 막대 보이게
        chart.moveViewToX(16);                      // 처음 보일 시작 값은 16일 부터에요

        XAxis xAxis = chart.getXAxis();
        xAxis.setGranularity(1f);
        xAxis.setLabelCount(4);
        xAxis.setYOffset(1f);
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);              // x축 아래쪽에 위치
        xAxis.setDrawGridLines(false);                              // x축 눈금선 제거
        xAxis.setTextSize(13f);                                     // x축 label 텍스트 크기

        chart.getLegend().setEnabled(false);                        // 범례 제거
        chart.getAxisLeft().setDrawGridLines(false);                // y축 눈금선 제거(둘 다 해야함)
        chart.getAxisRight().setDrawGridLines(false);               // y축 눈금선 제거(둘 다 해야함)
        chart.getAxisRight().setDrawAxisLine(false);                // 오른쪽 y축 제거
        chart.getAxisRight().setDrawLabels(false);                  // 오른쪽 축 제거
        chart.setExtraBottomOffset(5f);
        chart.animateXY(2000,2000);
        chart.getDescription().setEnabled(false);                   // 기본값 label 제거
    }

    // 꺾은선 그래프 그리는 함수
    private void line_chart_making(ArrayList<Entry> values, LineChart chart) {
        LineDataSet set;
        set = new LineDataSet(values, "");
        ArrayList<ILineDataSet> dataSets = new ArrayList<>();
        dataSets.add(set);

        String strColor = "#29b6f6";
        LineData data = new LineData(dataSets);
        data.setValueTextSize(12f);
        data.setValueTypeface(Typeface.DEFAULT_BOLD);

        set.setColor(Color.parseColor(strColor));                                  // 선 색깔
        set.setCircleColor(Color.parseColor(strColor));                            // 데이터마다 점
        set.setCircleHoleColor(Color.parseColor(strColor));
        chart.getXAxis().setSpaceMin(0.5f);
        chart.getXAxis().setSpaceMax(0.5f);
        chart.getLegend().setEnabled(false);                        // 범례 제거
        chart.getDescription().setEnabled(false);                   // 기본값 label 제거
        chart.getAxisRight().setDrawLabels(false);                  // 오른쪽 축 제거
        chart.getAxisRight().setDrawAxisLine(false);                // 오른쪽 y축 제거
        chart.getAxisLeft().setDrawGridLines(false);                // y축 눈금선 제거(둘 다 해야함)
        chart.getAxisRight().setDrawGridLines(false);               // y축 눈금선 제거(둘 다 해야함)
        chart.getXAxis().setTextSize(13f);                          // x축 label 텍스트 크기
        chart.getXAxis().setYOffset(1f);
        chart.setExtraBottomOffset(5f);
        chart.getXAxis().setDrawGridLines(false);                   // x축 눈금선 제거
        chart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);   // x축 아래쪽에 위치
        chart.setData(data);
    }

    // 최근 일주일 x축 label 사용자 정의하는 클래스
    private class DayAxisValueFormatter extends ValueFormatter {
        private final BarLineChartBase<?> chart;
        public DayAxisValueFormatter(BarLineChartBase<?> chart) {
            this.chart = chart;
        }
        @Override
        public String getFormattedValue(float value) {
            return ((int) value + "일");
        }
    }

    // 월별 평균 x축 label 사용자 정의하는 클래스
    public class MyXAxisValueFormatter extends ValueFormatter {
        private ArrayList<String> mValues;

        public MyXAxisValueFormatter(ArrayList<String> values) {
            this.mValues = values;
        }

        @Override
        public String getFormattedValue(float value) {
            // "value" represents the position of the label on the axis (x or y)
            return ((int) value + "월");
        }
    }
}