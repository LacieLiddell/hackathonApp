package com.example.lacie.hackathon;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

public class ViewLevel extends Activity {
    ListView levelList;
    ArrayAdapter<String> adapter;
    ProgressBar progressLevels;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_level);
        progressLevels = (ProgressBar)findViewById (R.id.progressLevels);
        levelList = (ListView) findViewById(R.id.levelList);
        new JTask().execute();
        levelList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getApplicationContext(), LevelActivity.class);
                intent.putExtra("LVLNAME", adapter.getItem(position));
                startActivity(intent);
            }
        });

    }//oncreate


    private class JTask  extends AsyncTask<Void, Void, Void>{
        HttpURLConnection connection;
        BufferedReader reader;
        String jsonString;
        JSONArray arrayResults;
        JSONObject objectResult;
        ArrayList<String> lvlList = new ArrayList<>();


        @Override
        protected Void doInBackground(Void... params) {
            //получаем данные с внешнего ресурса
            try {
                //строка соединения
                URL url = new URL("http://popop-database-server.herokuapp.com/get_all");
                //создаём и открываем соединение
                connection = (HttpURLConnection) url.openConnection();
                //отправляем запрос GET
                connection.setRequestMethod("GET");
                //открываем входной поток данных соединения
                InputStream inputStream = connection.getInputStream();
                //читатель потока
                reader = new BufferedReader(new InputStreamReader(inputStream));
                //буфер, в который будет проивзодится чтение и вспомогательная строка
                StringBuffer buffer = new StringBuffer();
                String line;
                //пока не конец json array в буфер читается строка, когда весь
                //JSON array считан, начинается парсинг
                //каждый распарсенный JSON object будет передаваться в publishProgress,
                //чтобы минимизировать задержку вывода данных
                while ((line = reader.readLine()) != null){
                    buffer.append(line);
                }
                jsonString = buffer.toString();
                arrayResults = new JSONArray(jsonString);
                for (int i = 0; i < arrayResults.length(); i++){
                    boolean flag = true;
                    objectResult = arrayResults.getJSONObject(i);
                    for (String lvl:lvlList) {
                        if(lvl.equals(objectResult.getString("level_name"))){
                            flag = false;
                        }
                    }
                    if (flag) {
                        lvlList.add(objectResult.getString("level_name"));
                    }
                }//getting JSON oblects
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
           // adapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_list_item_1, lvlList){

            //};
            return null;
        }//onBackground


        protected void onPostExecute(Void params){
            super.onPostExecute(params);
            adapter = new ArrayAdapter<String>(getApplicationContext(), R.layout.list_item, lvlList);
            levelList.setAdapter(adapter);
            progressLevels.setVisibility(View.INVISIBLE);
        }
    }//jtask
}//class
