package com.sample.foo.simplewebapi;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

// TODO: cache results across searches
public class MainActivity extends AppCompatActivity {

    EditText wikiTitleText;
    EditText numSubset;
    TextView responseView;
    ProgressBar progressBar;
    int PAGINATION = 0;
    static final String API_URL = "https://en.wikipedia.org/w/api.php?";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        responseView = (TextView) findViewById(R.id.responseView);
        wikiTitleText = (EditText) findViewById(R.id.wikiTitleText);
        numSubset = (EditText) findViewById(R.id.numSubset);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);

        Button queryButton = (Button) findViewById(R.id.queryButton);
        queryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String wiki_title = wikiTitleText.getText().toString();
                String num_subset = numSubset.getText().toString();
                new RetrieveFeedTask().execute(wiki_title, num_subset);
            }
        });
    }

    class RetrieveFeedTask extends AsyncTask<String, String, String> {

        private Exception exception;

        protected void onPreExecute() {
            progressBar.setVisibility(View.VISIBLE);
            responseView.setText("");
        }

        protected String doInBackground(String... inputs) {
            String wiki_title = inputs[0];
            Integer num_subset = Integer.parseInt(inputs[1]);
            // Do some validation here

            try {
                // articles accumulates the the results from Wikipedia during pagination
                JSONObject articles = new JSONObject() {{
                    put("titles", new JSONArray());
                }};
                JSONObject out = new JSONObject() {{
                    put("continue", new JSONObject() {{
                        put("sroffset", PAGINATION);
                    }});
                }};

                while (out.has("continue") && out.getJSONObject("continue").getInt("sroffset") < 25000) {
                    URL url = new URL(API_URL +
                            "srsearch=morelike%3A" + wiki_title +
                            "&sroffset=" + PAGINATION +
                            "&srlimit=500" +
                            "&format=json" +
                            "&list=search" +
                            "&srprop=timestamp" +
                            "&action=query"
                    );
                    HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                    try {
                        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                        StringBuilder currResponse = new StringBuilder();
                        String line;
                        while ((line = bufferedReader.readLine()) != null) {
                            currResponse.append(line).append("\n");
                        }
                        bufferedReader.close();
                        out = new JSONObject(currResponse.toString());
                        if (out.has("continue")) {
                            PAGINATION = out.getJSONObject("continue").getInt("sroffset");
                        }
                        // loop through [query][search] JSONArray and accumulate to articles[titles]
                        JSONArray results = out.getJSONObject("query").getJSONArray("search");
                        for (int i = 0; i < results.length(); ++i) {
                            JSONObject result = results.getJSONObject(i);
                            String title = result.getString("title");
                            articles.accumulate("titles", title);
                        }
                    } finally {
                        urlConnection.disconnect();
                    }
                }
                PAGINATION = 0;  // reset pagination

                JSONArray titles = articles.getJSONArray("titles");
                JSONArray subset = Util.pickNRandomWeighted(titles, num_subset);
                return subset.toString();
            }
            catch(Exception e) {
                Log.e("ERROR", e.getMessage(), e);
                return null;
            }
        }

        protected void onPostExecute(String response) {
            if(response == null) {
                response = "THERE WAS AN ERROR";
            }
            progressBar.setVisibility(View.GONE);
            Log.i("INFO", response);
            responseView.setText(response);
        }
    }
}
