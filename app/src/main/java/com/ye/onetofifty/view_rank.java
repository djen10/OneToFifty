package com.ye.onetofifty;

import android.content.Context;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;

/**
 * Created by YE on 2017-04-06.
 */

public class view_rank extends AppCompatActivity {
    ListView listview;
    ArrayList<String> m_list;
    CustomAdapter adapter;
    MyAsyncTask mTask;
    Handler handler2 = new Handler();
    int count;
    int ranking;
    String temp;

    static String query = "select top(10) * from Ranking order by total asc;";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_rank);
        listview = (ListView) findViewById(R.id.listView);
        ranking=1;
        count=1;
        m_list = new ArrayList<>();

        adapter = new CustomAdapter(this,0,m_list);

        listview.setAdapter(adapter);
        listview.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                return true;
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

        handler.sendEmptyMessage(0);
    }


    class MyAsyncTask extends AsyncTask<String, Void, ArrayList<String>> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected ArrayList<String> doInBackground(String... params) {


            ArrayList<String> list = new ArrayList<String>();


            ResultSet reset = null;
            Connection conn = null;

            try {
                Class.forName("net.sourceforge.jtds.jdbc.Driver");

                conn = DriverManager.getConnection("jdbc:jtds:sqlserver://"+"223.194.44.21"+":1433;databaseName=OnetoFifty_DB", "sa", "test");

                Statement stmt = conn.createStatement();

                reset = stmt.executeQuery(query);

                list.add("순위");
                list.add("이름");
                list.add("시간");

                while (reset.next()) {

                    if (isCancelled()) break;

                    String rank = Integer.toString(ranking);
                    String name = reset.getString(1);
                    String time = reset.getString(2) + ":" +reset.getString(3) + ":" + reset.getString(4);
                    name = name.replace(" ","\n");
                    list.add(rank);
                    list.add(name);
                    list.add(time);
                    ranking++;

                }
                conn.close();
            } catch (Exception e) {
                Log.w("111Error connection", "" + e.getMessage());
            }

            return list;
        }

        @Override
        protected void onPostExecute(ArrayList<String> list) {

            m_list.clear();
            m_list.addAll(list);


            adapter.notifyDataSetChanged();
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
        }
    }

    public Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            mTask = new MyAsyncTask();

            mTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, "");
        }
    };


    private class CustomAdapter extends ArrayAdapter<String> {
        private ArrayList<String> items;

        public CustomAdapter(Context context, int textViewResourceId, ArrayList<String> objects) {
            super(context, textViewResourceId, objects);
            this.items = objects;
        }

        @Override
        public int getCount() {
            return super.getCount();
        }

        @Nullable
        @Override
        public String getItem(int position) {
            return super.getItem(position);
        }

        @Override
        public long getItemId(int position) {
            return super.getItemId(position);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View v = convertView;
            if(position % 3 == 0){
                if (v == null) {
                    LayoutInflater vi = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                    v = vi.inflate(R.layout.listview_layout, null);
                    v.setOnTouchListener(new View.OnTouchListener() {
                        @Override
                        public boolean onTouch(View view, MotionEvent motionEvent) {
                            return true;
                        }
                    });
                }
                TextView Text_Rank = (TextView) v.findViewById(R.id.list_rank);
                TextView Text_Name = (TextView) v.findViewById(R.id.list_name);
                TextView Text_Time = (TextView) v.findViewById(R.id.list_time);

                Text_Rank.setText(items.get(position));
                Text_Name.setText(items.get(position+1));
                Text_Time.setText(items.get(position+2));

                if(Text_Rank.getText() == "1"){
                    Text_Rank.setBackgroundResource(R.drawable.medal_gold);
                    Text_Rank.setText("");
                }
                else if(Text_Rank.getText() == "2"){
                    Text_Rank.setBackgroundResource(R.drawable.medal_silver);
                    Text_Rank.setText("");
                }
                else if(Text_Rank.getText() == "3"){
                    Text_Rank.setBackgroundResource(R.drawable.medal_bronze);
                    Text_Rank.setText("");
                }
            }
            if(position == 0){
                TextView Text_Rank = (TextView) v.findViewById(R.id.list_rank);
                Text_Rank.setBackgroundColor(Color.TRANSPARENT);
            }
            return v;
        }
    }


}
