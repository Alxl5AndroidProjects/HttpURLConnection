package com.alxl5.asynctaskproject;

import android.content.Context;
import android.os.AsyncTask;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.alxl5.asynctaskproject.models.BlogModel;

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
import java.util.List;

public class MainActivity extends AppCompatActivity {

    Button button;
    ListView blogListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        button = (Button) findViewById(R.id.button);

        blogListView = (ListView) findViewById(R.id.blogListView);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                JSONTask jsonTask = new JSONTask();
                jsonTask.execute("http://alxl5-domain.esy.es/index.php/blog", null, null);
            }
        });
    }

    private class JSONTask extends AsyncTask<String, String, List<BlogModel>> {
        @Override
        protected List<BlogModel> doInBackground(String[] params) {

            HttpURLConnection connection = null;
            BufferedReader reader = null;

            try {
                URL url = new URL(params[0]);
                connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("POST");
                connection.setDoOutput(true);
                connection.setDoInput(true);
                connection.setConnectTimeout(1000);
                connection.setReadTimeout(1000);

                connection.connect();

                InputStream stream = connection.getInputStream();

                reader = new BufferedReader(new InputStreamReader(stream));

                StringBuilder sb = new StringBuilder();

                String line;

                while ((line = reader.readLine()) != null) {
                    sb.append(line);
                }

                String stringJSON = sb.toString();

                JSONObject jsonObject = new JSONObject(stringJSON);
                JSONArray jsonArray = jsonObject.getJSONArray("blog");

                StringBuffer stringBufferData = new StringBuffer();

                List<BlogModel> blogModelList = new ArrayList<>();

                for (int i = 0; i < jsonArray.length(); i++) {

                    JSONObject resultObject = jsonArray.getJSONObject(i);
                    BlogModel blogModel = new BlogModel();
                    blogModel.setId(resultObject.getInt("id"));
                    blogModel.setTitle(resultObject.getString("title"));
                    blogModel.setText(resultObject.getString("text"));
                    blogModel.setCreate_date(resultObject.getString("create_date"));

                    String id = resultObject.getString("id");
                    stringBufferData.append("ID: " + id + "\n");

                    blogModelList.add(blogModel);
                }

                return blogModelList;

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            } finally {
                assert connection != null;
                connection.disconnect();
            }

            return null;
        }

        @Override
        protected void onPostExecute(List<BlogModel> result) {
            super.onPostExecute(result);
            BlogAdapter adapter = new BlogAdapter(getApplicationContext(), R.layout.row, result);
            blogListView.setAdapter(adapter);
        }
    }

    public class BlogAdapter extends ArrayAdapter {

        private List<BlogModel> blogModelList;
        private int resourse;
        private LayoutInflater inflater;

        public BlogAdapter(@NonNull Context context, @LayoutRes int resource, @NonNull List objects) {
            super(context, resource, objects);
            blogModelList = objects;
            this.resourse = resource;
            inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = inflater.inflate(R.layout.row, null);
            }

            TextView titleView;
            TextView textView;
            TextView dateView;

            titleView = (TextView) convertView.findViewById(R.id.titleView);
            textView = (TextView) convertView.findViewById(R.id.textView);
            dateView = (TextView) convertView.findViewById(R.id.dateView);

            titleView.setText(blogModelList.get(position).getTitle());
            textView.setText(Html.fromHtml(blogModelList.get(position).getText()));
            dateView.setText(blogModelList.get(position).getCreate_date());

            return convertView;
        }
    }
}
