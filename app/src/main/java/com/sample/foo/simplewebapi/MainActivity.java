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


public class MainActivity extends AppCompatActivity {

    EditText emailText;
    TextView responseView;
    ProgressBar progressBar;
    int PAGINATION = 0;
    static final String API_URL = "https://en.wikipedia.org/w/api.php?";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        responseView = (TextView) findViewById(R.id.responseView);
        emailText = (EditText) findViewById(R.id.emailText);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);

        Button queryButton = (Button) findViewById(R.id.queryButton);
        queryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = emailText.getText().toString();
                new RetrieveFeedTask().execute(email);
            }
        });
    }

    class RetrieveFeedTask extends AsyncTask<String, String, String> {

        private Exception exception;

        protected void onPreExecute() {
            progressBar.setVisibility(View.VISIBLE);
            responseView.setText("");
        }

        protected String doInBackground(String... urls) {
            String email = urls[0];
            // Do some validation here

            try {
                StringBuilder stringBuilder = new StringBuilder();
                JSONObject out = new JSONObject() {{
                    put("continue", new JSONObject() {{
                        put("sroffset", PAGINATION);
                    }});
                }};

                while (out.has("continue") && out.getJSONObject("continue").getInt("sroffset") < 4) {
                    URL url = new URL(API_URL +
                            "srsearch=morelike%3A" + email +
                            "&sroffset=" + PAGINATION +
                            "&srlimit=2" +
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
                            stringBuilder.append(line).append("\n");
                            currResponse.append(line).append("\n");
                        }
                        bufferedReader.close();
                        out = new JSONObject(currResponse.toString());
                        if (out.has("continue")) {
                            PAGINATION = out.getJSONObject("continue").getInt("sroffset");
                        }
                    } finally {
                        urlConnection.disconnect();
                    }
                }
                return stringBuilder.toString();
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
            // TODO: check this.exception
            // TODO: do something with the feed

//            try {
//                JSONObject object = (JSONObject) new JSONTokener(response).nextValue();
//                String requestID = object.getString("requestId");
//                int likelihood = object.getInt("likelihood");
//                JSONArray photos = object.getJSONArray("photos");
//                .
//                .
//                .
//                .
//            } catch (JSONException e) {
//                e.printStackTrace();
//            }
        }
    }
}
