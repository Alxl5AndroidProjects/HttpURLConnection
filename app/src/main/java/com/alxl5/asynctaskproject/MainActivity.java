package com.alxl5.asynctaskproject;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    public Button button;
    public TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        button = (Button) findViewById(R.id.button);
        textView = (TextView) findViewById(R.id.textView);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                JSONTask jsonTask = new JSONTask();
                jsonTask.execute("Ok! Add JSONTask class!", null, null);
            }
        });
    }

    private class JSONTask extends AsyncTask<String, String, String> {
        @Override
        protected String doInBackground(String[] params) {
            return params[0];
        }

        @Override
        protected void onPostExecute(String result) {
            textView.setText(result);
        }
    }
}
