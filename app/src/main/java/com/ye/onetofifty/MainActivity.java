package com.ye.onetofifty;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;


public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


    }

    public void mClick(View v){
        switch(v.getId())
        {
            case R.id.btn_start:
            {
                Intent intent = new Intent(this,game_rank.class);
                startActivity(intent);
                break;
            }
            case R.id.btn_rank:
            {
                Intent intent = new Intent(this,view_rank.class);
                startActivity(intent);
                break;
            }
            case R.id.btn_exit:
            {
                finish();
                break;
            }

        }
    }
}
