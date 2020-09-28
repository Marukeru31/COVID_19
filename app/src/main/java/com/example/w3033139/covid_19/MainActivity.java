package com.example.w3033139.covid_19;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Spinner;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //決定ボタンを押すとenterClickEventが発生
        findViewById(R.id.btn_enter).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                enterClickEvent(v);
            }
        });


    }


    //決定ボタンクリック時の処理
    private void enterClickEvent(View v){
        //インテントを作成
        Intent intent = new Intent(getApplication(),MPChart.class);
        //選択されたデータの取得
        Spinner spinner = (Spinner)this.findViewById(R.id.spinner);
        String prefecture = (String)spinner.getSelectedItem();
        //名前と共にデータをセット
        intent.putExtra("Prefecture", prefecture);
        //画面遷移
        startActivity(intent);
    }

}
