package com.example.lacie.hackathon;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class MainActivity extends Activity{
    ListView linearMain;
    ArrayAdapter <CharSequence> arrayAdapter;
    Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        linearMain = (ListView) findViewById(R.id.linearMain);
        arrayAdapter = ArrayAdapter.createFromResource(this, R.array.menu_array, android.R.layout.simple_list_item_1);
        linearMain.setAdapter(arrayAdapter);
        linearMain.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch (position){
                    case 0:
                        intent = new Intent(getApplicationContext(), ViewResults.class);
                        startActivity(intent);
                        break;
                    case 1:
                        intent = new Intent(getApplicationContext(), ViewLevel.class);
                        startActivity(intent);
                        break;
                }

            }
        });


    }

}
