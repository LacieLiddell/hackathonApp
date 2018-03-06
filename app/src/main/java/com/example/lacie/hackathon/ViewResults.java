package com.example.lacie.hackathon;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SimpleAdapter;

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
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class ViewResults extends Activity {
    ListView resultList;
    ProgressBar resProgressBar;
    ArrayList<Map<String, Object>> data;
    Map<String, Object> m;
    final String ATTR_RESULT_ID = "resultID";
    final String ATTR_LVL_NAME = "levelName";
    final String ATTR_USER = "userName";
    final String ATTR_GAME_RESULT = "gameResult";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_results);
        resultList = (ListView) findViewById(R.id.resultList);
        resProgressBar = (ProgressBar) findViewById(R.id.resProgressBar);
        data = new ArrayList<Map<String, Object>>();
        new JTask().execute();
    }

    private class JTask extends AsyncTask<Void, Void, Void>{
        HttpURLConnection connection;
        BufferedReader reader;
        String jsonString;
        JSONArray arrayResults;
        JSONObject objectResult;

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
                    objectResult = arrayResults.getJSONObject(i);
                    m = new HashMap<String, Object>();
                    m.put(ATTR_RESULT_ID, objectResult.getString("result_id"));
                    m.put(ATTR_LVL_NAME, objectResult.getString("level_name"));
                    m.put(ATTR_USER, objectResult.getString("user_name"));
                    m.put(ATTR_GAME_RESULT, objectResult.getString("game_result"));
                    data.add(m);
                    publishProgress();
                }
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }//doInBackground

        protected void onProgressUpdate(Void... value){
            super.onProgressUpdate(value);
            resProgressBar.setVisibility(View.INVISIBLE);
            String[] from = {ATTR_RESULT_ID, ATTR_LVL_NAME, ATTR_USER, ATTR_GAME_RESULT };
            int [] to = {R.id.resultID, R.id.lvlName, R.id.userName, R.id.gameResult};
            MyAdapter adapter = new MyAdapter(getApplicationContext(), data, R.layout.result_view, from, to);
            resultList.setAdapter(adapter);
        }
    }//jtask

    public class MyAdapter extends SimpleAdapter{

        /**
         * Constructor
         *
         * @param context  The context where the View associated with this SimpleAdapter is running
         * @param data     A List of Maps. Each entry in the List corresponds to one row in the list. The
         *                 Maps contain the data for each row, and should include all the entries specified in
         *                 "from"
         * @param resource Resource identifier of a view layout that defines the views for this list
         *                 item. The layout file should include at least those named views defined in "to"
         * @param from     A list of column names that will be added to the Map associated with each
         *                 item.
         * @param to       The views that should display column in the "from" parameter. These should all be
         *                 TextViews. The first N views in this list are given the values of the first N columns
         */
        public MyAdapter(Context context, List<? extends Map<String, ?>> data, int resource, String[] from, int[] to) {
            super(context, data, resource, from, to);
        }
    }//adapter

    public void onRestart(){
        super.onRestart();
        resProgressBar.setVisibility(View.VISIBLE);
    }
}//activity
