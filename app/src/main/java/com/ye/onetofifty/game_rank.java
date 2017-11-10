package com.ye.onetofifty;

import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.TextView;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by YE on 2017-04-03.
 */

public class game_rank extends AppCompatActivity{
    MyAsyncTask mTask;
    static String query ;
    Animation anim;

    int NumberList[] = new int[50]; // 중복되지않는 1~50까지의 수
    Button btn[] = new Button[25]; // 버튼의 배열
    int NumberCount=25; // 24까지는 한번에 생성하니까 25부터 넣는다.
    int Globaltemp; // 쓰레드 돌릴려고 만든 변수
    int CheckNumber=1; // 몇번까지 통과했는지 체크
    int [] IdList= {R.id.game_btn_1, R.id.game_btn_2,R.id.game_btn_3,R.id.game_btn_4,R.id.game_btn_5,
            R.id.game_btn_6,R.id.game_btn_7,R.id.game_btn_8,R.id.game_btn_9,R.id.game_btn_10,
            R.id.game_btn_11,R.id.game_btn_12,R.id.game_btn_13,R.id.game_btn_14,R.id.game_btn_15,
            R.id.game_btn_16,R.id.game_btn_17,R.id.game_btn_18,R.id.game_btn_19,R.id.game_btn_20,
            R.id.game_btn_21,R.id.game_btn_22,R.id.game_btn_23,R.id.game_btn_24,R.id.game_btn_25}; // id 를 저장하는 배열
    TextView Tv_Time;
    TextView Tv_FindNumber;
    TimerTask timerTask;
    Timer timer;
    long BaseTime;
    long CurrentTime;
    long milis;
    int seconds;
    int minutes;
    String total;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.game_rank);
        Tv_Time = (TextView)findViewById(R.id.Tv_Time);
        Tv_FindNumber = (TextView)findViewById(R.id.Tv_FindNumber);
        for(int i=0;i<(NumberList.length);i++){
            if(i<25){ // 먼저 25까지 중복되지않는 수를 넣는다. 처음 화면이 25개 이기 때문에.
                NumberList[i]=(int)(Math.random()*25)+1;
                for(int j=0;j<i;j++){
                    if(NumberList[i]==NumberList[j]){
                        i--;
                        break;
                    }
                }
            }
            else{ // 그다음 26~50 까지의 중복되지 않는 수를 넣는다.
                NumberList[i]=(int)(Math.random()*25)+26;
                for(int j=25;j<i;j++){
                    if(NumberList[i]==NumberList[j]){
                        i--;
                        break;
                    }
                }
            }
        }

        new Thread(new Runnable() {
            @Override
            public void run() {
                for(int i=0;i<25;i++) {
                    btn[i] = (Button) findViewById(IdList[i]);
                    btn[i].setText(""+NumberList[i]);
                    btn[i].setTextSize(15);
                } // 쓰레드를 통하여 초기 25까지의 수와 사이즈를 조정.
            }
        }).start();
        BaseTime = System.currentTimeMillis();
        timerTask = new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        CurrentTime = System.currentTimeMillis();
                        milis = CurrentTime - BaseTime;
                        int now_seconds = (int)milis / 1000;
                        minutes = (now_seconds % 3600) / 60;  // 분
                        seconds = now_seconds% 60;// 초
                        milis = (milis / 10) % 100; // 밀리초

                        total = String.format("%02d%02d%02d", minutes, seconds, milis);
                        String time = String.format("%02d : %02d : %02d", minutes, seconds, milis);
                        Tv_Time.setText("" + time);
                    }
                }) ;
            }
        };
        timer = new Timer();
        timer.schedule(timerTask,0,1);
    }

    public void mCilck(View v){
        anim = AnimationUtils.loadAnimation(this, R.anim.alpha);
        v.startAnimation(anim);
        for(int i=0;i<25;i++){ // 버튼이 25개이기 때문에 25까지만 돔.
            if(v.getId() == IdList[i]){ // 몇번쨰 버튼을 클릭했는지 확인
                if(CheckNumber == Integer.parseInt(btn[i].getText().toString())) { // 클릭한게 현재 눌러야 하는 숫자와 일치한다면
                    Globaltemp=i;
                    CheckNumber++;
                    if(CheckNumber > 50){
                        AlertDialog.Builder alert = new AlertDialog.Builder(game_rank.this);
                        alert.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                new MyAsyncTask().execute("");
                                dialog.dismiss();
                                finish();
                            }

                        });
                        alert.setMessage("끝!");
                        alert.setCancelable(false);
                        alert.show();
                        timer.cancel();
                        break;
                    }
                    else if(NumberCount >= 50){ // 25번째를 넘어가면 넣을 수가 없기에 처리하는 부분
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        btn[Globaltemp].setText(" ");
                                    }
                                });
                            }
                        }).start();
                    }
                    else{
                        new Thread(new Runnable() { // 수를 넣는 부분
                            @Override
                            public void run() {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        btn[Globaltemp].setText(""+NumberList[NumberCount++]);
                                    }
                                });
                            }
                        }).start();
                    }
                    Tv_FindNumber.setText("Find Number : " + CheckNumber);
                    break;
                }
                else
                    break;
            }
        }
    }
    public Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            mTask = new MyAsyncTask();

            mTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, "");
        }
    };

    class MyAsyncTask extends AsyncTask<String, Void, Integer> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }


        @Override
        protected Integer doInBackground(String... params) {

            Connection conn = null;

            try {
                Class.forName("net.sourceforge.jtds.jdbc.Driver");

                conn = DriverManager.getConnection("jdbc:jtds:sqlserver://" + "223.194.44.21" + ":1433;databaseName=OnetoFifty_DB", "sa", "test");

                Statement stmt = conn.createStatement();
                query = "insert into Ranking values('" + Build.ID + "'"+ " , " + minutes + "," + seconds + "," + milis + ",'" + total + "')";
                stmt.executeQuery(query);
                conn.close();
            } catch (Exception e) {
                Log.w("111Error connection", "" + e.getMessage());
            }

            return 0;
        }
    }
}
