package com.example.w3033139.covid_19;

//https://github.com/PhilJay/MPAndroidChart
//https://blog.anysense.co.jp/app/mpandroidchart/
//https://moneyforward.com/engineers_blog/2015/10/20/mpandroidchart/
//https://akira-watson.com/android/achartengine.html
//https://qiita.com/iKimishima/items/7fd192a074739cf5290b
//https://www.stopcovid19.jp/data/covid19japan-all.json
//http://www.tohoho-web.com/ex/json.html
//https://tkm0on.hatenablog.com/entry/2015/05/21/183608
//https://moewe-net.com/android/webapi-get-json
//https://qiita.com/iwacchi/items/94e5745a2c8e8376b983
//https://www.youtube.com/watch?v=h71Ia9iFWfI&feature=share



import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;

import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IValueFormatter;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONArray;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class MPChart extends AppCompatActivity{
    private BarChart barChart;
    private LineChart lineChart;
    BarDataSet barDataSet;
    int npat, Max = 0, length = 0;
    String lDate, prefecture, labels[] = new String[62];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mpchart);


        //getJson();


        //-----JSONのデータを取得----------------------------------------------
        String json;
        Intent intent = this.getIntent();
        prefecture = intent.getStringExtra("Prefecture");
        int Npat[] = new int[62];

        try{
            InputStream is = getAssets().open("COVID_19.json");
            //推定バイト数を返す
            int size = is.available();
            //データの大きさを取得
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();

            json = new String(buffer,"UTF-8");
            //JSONの全データ
            JSONArray jsonArray = new JSONArray(json);

            System.out.println("データの日数" + jsonArray.length());
            length = jsonArray.length();



            for (int i=0; i<jsonArray.length(); i++) {

                //i番目の日のデータ
                JSONObject obj = jsonArray.getJSONObject(i);
                //日付のデータ
                labels[i] = obj.getString("lastUpdate");

                //areaの中身
                JSONArray dateArray = obj.getJSONArray("area");

                for (int j = 0; j < dateArray.length(); j++) {
                    //j番目の県のデータ
                    JSONObject dateobj = dateArray.getJSONObject(j);
                    //System.out.println("選択した県 : " + prefecture);
                    //選択した県のデータを取り出す
                    if (dateobj.getString("name_jp").equals(prefecture)) {
                        npat = dateobj.getInt("ncurrentpatients");
                        //配列に格納
                        Npat[i] = npat;
                        if (Max < npat){
                            Max = npat;
                        }

                    }

                }

            }
/*
            System.out.println("Numberlistの中身 : " + lDate);
            for(int i = 0; i < length; i++){
                System.out.println("チャートのデータ : " + Npat[i]);
            }
*/

        }catch (IOException e){
            e.printStackTrace();
        }catch (JSONException e){
            e.printStackTrace();
        }





        //-----折れ線グラフ------------------------------------------

        lineChart = findViewById(R.id.line_chart);


        // Chartの説明書き
        lineChart.getDescription().setEnabled(true);


        //X軸
        // 背景縦軸を破線
        XAxis xAxis = lineChart.getXAxis();
        xAxis.enableGridDashedLine(10f, 10f, 0f);
        //ラベルをつける
        xAxis.setValueFormatter(new IndexAxisValueFormatter(labels));
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);


        //Y軸
        YAxis leftAxis = lineChart.getAxisLeft();
        // Y軸０始まりから最大
        leftAxis.setAxisMaximum(Max);
        leftAxis.setAxisMinimum(0f);
        // 背景横軸を破線
        leftAxis.enableGridDashedLine(10f, 10f, 0f);
        leftAxis.setDrawZeroLine(true);
        // Y軸右側の目盛りなし
        lineChart.getAxisRight().setEnabled(false);

        //チャートにデータをセット
        //setData();


        //-----LineChartのデータ----------------------------------------------
        ArrayList<Entry> values = new ArrayList<>();

        //データの要素数を数えて、ラベリングする
        for (int i = 0; i < Npat.length; i++) {
            values.add(new Entry(i, Npat[i], null, null));
        }

        LineDataSet set1;

        //データセット
        if (lineChart.getData() != null &&
                lineChart.getData().getDataSetCount() > 0) {

            set1 = (LineDataSet) lineChart.getData().getDataSetByIndex(0);
            set1.setValues(values);
            lineChart.getData().notifyDataChanged();
            lineChart.notifyDataSetChanged();
        } else {
            //set1を作ってタイプを決める
            set1 = new LineDataSet(values, "感染者数");

            set1.setDrawIcons(false);
            //set1の色
            set1.setColor(Color.BLACK);
            //点の色
            set1.setCircleColor(Color.BLACK);
            //線の太さ
            set1.setLineWidth(1f);
            //点の大きさ
            set1.setCircleRadius(3f);
            //値の字の大きさ
            set1.setValueTextSize(10f);
            //塗りつぶし
            set1.setDrawFilled(true);
            set1.setFillColor(Color.BLUE);
            set1.setFormLineWidth(1f);
            set1.setFormLineDashEffect(new DashPathEffect(new float[]{10f, 5f}, 0f));
            set1.setFormSize(15.f);


            //sataSetsにset1を追加
            ArrayList<ILineDataSet> dataSets = new ArrayList<ILineDataSet>();
            dataSets.add(set1);

            LineData lineData = new LineData(dataSets);

            lineChart.setData(lineData);
        }

        //アニメーション
        lineChart.animateX(2500);




        //System.out.println(prefecture);

        //textViewの書き換え
        TextView textView = (TextView)this.findViewById(R.id.textView2);
        textView.setText(prefecture + "の感染者数");


        //-----JSONデータの取得----------------------------------------------
        /*
        String jsonData;
        try{
            BufferedReader br = new BufferedReader(new InputStreamReader(getResources().getAssets().open("COVID_19.json")));
            jsonData = br.readLine();
        }catch (IOException e){
            e.printStackTrace();
        }
        try{
            JSONObject rootObject = new JSONObject(jsonData);
            System.out.println(rootObject.getString());
        }

        */




        //-----戻るボタンの画面遷移----------------------------------------------
        findViewById(R.id.btn_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }



    /*
    //-----LineChartのデータ----------------------------------------------
    private  void setData() {
        // Entry()を使ってLineDataSetに設定できる形に変更してarrayを新しく作成
        int data[] = {116, 111, 112, 121, 102, 83,
                99, 101, 74, 105, 120, 112,
                109, 102, 107, 93, 82, 99, 110,
        };

        ArrayList<Entry> values = new ArrayList<>();

        //データの要素数を数えて、ラベリングする
        for (int i = 0; i < data.length; i++) {
            values.add(new Entry(i, data[i], null, null));
        }

        LineDataSet set1;

        //データセット
        if (lineChart.getData() != null &&
                lineChart.getData().getDataSetCount() > 0) {

            set1 = (LineDataSet) lineChart.getData().getDataSetByIndex(0);
            set1.setValues(values);
            lineChart.getData().notifyDataChanged();
            lineChart.notifyDataSetChanged();
        } else {
            //set1を作ってタイプを決める
            set1 = new LineDataSet(values, "DataSet");

            set1.setDrawIcons(false);
            //set1の色
            set1.setColor(Color.BLACK);
            //点の色
            set1.setCircleColor(Color.BLACK);
            //線の太さ
            set1.setLineWidth(1f);
            //点の大きさ
            set1.setCircleRadius(3f);
            //値の字の大きさ
            set1.setValueTextSize(10f);
            //塗りつぶし
            set1.setDrawFilled(true);
            set1.setFillColor(Color.BLUE);
            set1.setFormLineWidth(1f);
            set1.setFormLineDashEffect(new DashPathEffect(new float[]{10f, 5f}, 0f));
            set1.setFormSize(15.f);


            //sataSetsにset1を追加
            ArrayList<ILineDataSet> dataSets = new ArrayList<ILineDataSet>();
            dataSets.add(set1);

            LineData lineData = new LineData(dataSets);

            // set data
            lineChart.setData(lineData);
        }
    }

    */

/*
    //-----JSONのデータを取得----------------------------------------------
    public void getJson(){
        String json;
        Intent intent = this.getIntent();
        prefecture = intent.getStringExtra("Prefecture");
        int Npat[] = new int[6];

        try{
            InputStream is = getAssets().open("COVID_19.json");
            //推定バイト数を返す
            int size = is.available();
            //データの大きさを取得
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();

            json = new String(buffer,"UTF-8");
            //JSONの全データ
            JSONArray jsonArray = new JSONArray(json);

            System.out.println("データの日数" + jsonArray.length());



            for (int i=0; i<jsonArray.length(); i++) {

                //i番目の日のデータ
                JSONObject obj = jsonArray.getJSONObject(i);
                /*
                if(obj.getString("lastUpdate").equals("2020-09-19")){
                    //numberlist.add(obj.getInt("npatients"));
                    //こっち残す
                    //Numberlist = obj.getInt("npatients");
                    lDate = obj.getString("lastUpdate");
                    */
/*
                //areaの中身
                JSONArray dateArray = obj.getJSONArray("area");

                for (int j = 0; j < dateArray.length(); j++) {
                    //j番目の県のデータ
                    JSONObject dateobj = dateArray.getJSONObject(j);
                    //System.out.println("選択した県 : " + prefecture);
                    //選択した県のデータを取り出す
                    if (dateobj.getString("name_jp").equals(prefecture)) {
                        npat = dateobj.getInt("ncurrentpatients");
                        //配列に格納
                        Npat[i] = npat;


                    }


                }

                /*

            }
            */

/*

            }

            System.out.println("Numberlistの中身 : " + lDate);
            for(int i = 0; i < 6; i++){
                System.out.println("チャートのデータ : " + Npat[i]);
            }


        }catch (IOException e){
            e.printStackTrace();
        }catch (JSONException e){
            e.printStackTrace();
        }
    }
*/

}
