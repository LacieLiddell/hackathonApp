package com.example.lacie.hackathon;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

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
import java.util.HashMap;

public class LevelActivity extends Activity {
    String lvlName;
    String levelArray;
    String[] arraylevel;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();
        lvlName = intent.getStringExtra("LVLNAME");
        new JTask().execute();
    }

    class DrawView extends View {
        Paint paint;
        Rect rect;
                public DrawView(Context context) {
            super(context);
            paint = new Paint();
            rect = new Rect();
        }

        protected void onDraw(Canvas canvas){
            for(int i = 0; i < arraylevel.length; i++){
                int x, y;
                x = ((i)/20)*50;
                y = ((i) % 20 )*50;
                switch (Integer.parseInt(arraylevel[i])){
                    case 0:
                        paint.setARGB(255, 242, 243, 244);
                        break;
                    case 1:
                        paint.setARGB(255, 220, 118, 51);
                        break;
                    case 2:
                        paint.setARGB(255, 46, 134, 193);
                        break;
                    case 3:
                        paint.setARGB(255, 40, 180, 99);
                        break;
                    case 4:
                        paint.setARGB(255, 0, 0, 0);
                        break;
                    case 5:
                        paint.setARGB(255, 0, 0, 0);
                        break;
                }
                rect.set(x+25, y+200, x+75, y+250);
                canvas.drawRect(rect, paint);
            }
        }
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
                boolean flag = false;
                int i = 0;
                while(!flag){
                    objectResult = arrayResults.getJSONObject(i);
                    if (objectResult.getString("level_name").equals(lvlName)){
                        flag = true;
                        levelArray = objectResult.getString("level_array");
                    } else {
                        i++;
                    }
                }
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            arraylevel = levelArray.split(",");
            return null;
        }

        protected void onPostExecute(Void result){
            super.onPostExecute(result);
            setContentView(new DrawView(getApplicationContext()));
        }
    }//jtask
}//class
