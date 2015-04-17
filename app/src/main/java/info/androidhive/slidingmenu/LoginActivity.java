package info.androidhive.slidingmenu;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by vijaykumars on 4/17/2015.
 */
public class LoginActivity extends ActionBarActivity {

    private int a;
        private EditText username;
        private EditText password;
        private Button login;
        //private TextView loginLockedTV;
        //private TextView attemptsLeftTV;
        //private TextView numberOfRemainingLoginAttemptsTV;
        int numberOfRemainingLoginAttempts = 3;
        //private ProgressBar pb;
        String status = "";
        String userId = "";
        String access_token = "";

        @Override
        protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        setupVariables();
    }

    public void authenticateLogin(View view) {
        // TODO - pass UID and PWD

        new MyAsyncTask().execute(username.getText() + "$" + password.getText());
        Toast.makeText(getApplicationContext(), "verifying login details ..",
                Toast.LENGTH_SHORT).show();

    }

    private void setupVariables() {
        username = (EditText) findViewById(R.id.usernameET);
        password = (EditText) findViewById(R.id.passwordET);
        login = (Button) findViewById(R.id.loginBtn);
        //	loginLockedTV = (TextView) findViewById(R.id.loginLockedTV);
//		attemptsLeftTV = (TextView) findViewById(R.id.attemptsLeftTV);
//		numberOfRemainingLoginAttemptsTV = (TextView) findViewById(R.id.numberOfRemainingLoginAttemptsTV);
//		numberOfRemainingLoginAttemptsTV.setText(Integer.toString(numberOfRemainingLoginAttempts));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    // Async task to make http post request for authentication

    private class MyAsyncTask extends AsyncTask<String, Integer, Double> {

        @Override
        protected Double doInBackground(String... params) {
            // TODO Auto-generated method stub

            String paramVal = params[0];
            int delemiterIndex = paramVal.indexOf("$");
            postData(paramVal.substring(0, delemiterIndex), paramVal.substring(delemiterIndex + 1, paramVal.length()));
            return null;
        }

        protected void onPostExecute(Double result) {
            //	pb.setVisibility(View.GONE);
            //Toast.makeText(getApplicationContext(), "connecting ..", Toast.LENGTH_LONG).show();

            if (status.toString().equals("SUCCESS")) {
                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                //Create the bundle
                Bundle bundle = new Bundle();
                bundle.putString("userId", userId);
                bundle.putString("access_token", access_token);
                intent.putExtras(bundle);
                LoginActivity.this.startActivity(intent);
            } else {
                Toast.makeText(getApplicationContext(), "Invalid credientials.", Toast.LENGTH_LONG).show();
            }

        }

        protected void onProgressUpdate(Integer... progress) {
            //pb.setProgress(progress[0]);
        }

        public void postData(String email, String pwd) {
            // Create a new HttpClient and Post Header
            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httppost = new HttpPost("http://10.9.8.107:8080/talent-wagon/api/v0/auth/login");
            httppost.setHeader("Content-Type", "application/json");
            try {
                /*
				// Add your data
				List<BasicNameValuePair> nameValuePairs = new ArrayList < BasicNameValuePair>();
				nameValuePairs.add(new BasicNameValuePair("email", "vijaykumar.siripuram@yahoo.com"));
				nameValuePairs.add(new BasicNameValuePair("password","CHANGE_ME"));
				httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

				*/

                JSONObject holder = new JSONObject();
//				holder.put("email", "vijaykumar.siripuram@yahoo.com");
//				holder.put("password","CHANGE_ME");

                holder.put("email", email);
                holder.put("password", pwd);

                StringEntity se = new StringEntity(holder.toString());
                httppost.setEntity(se);
                httppost.setHeader("Accept", "application/json");
                httppost.setHeader("Content-type", "application/json");


                // Execute HTTP Post Request
                HttpResponse response = httpclient.execute(httppost);
                HttpEntity rp = response.getEntity();
                String result = readContent(response);
                System.out.println("done...");

                if (result != "" || result != null) {
                    JSONObject json = new JSONObject(result);
                    status = (String) json.get("status");
                    if (status.toString().equals("SUCCESS")) {
                        JSONArray results = json.getJSONArray("results");
                        JSONObject resultObj = results.getJSONObject(0);
                        userId = "" + (Integer) resultObj.get("userId");
                        access_token = (String) resultObj.get("access_token");

                    }
                }

                // call second activity

            } catch (ClientProtocolException e) {
                // TODO Auto-generated catch block
            } catch (IOException e) {
                System.out.println("IOException ..." + e.getMessage());

                // TODO Auto-generated catch block
            } catch (JSONException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }


    }

    String readContent(HttpResponse response) {
        String text = "";
        InputStream in = null;

        try {
            in = response.getEntity().getContent();
            BufferedReader reader = new BufferedReader(new InputStreamReader(in));
            StringBuilder sb = new StringBuilder();
            String line = null;
            while ((line = reader.readLine()) != null) {
                sb.append(line + "\n");
            }
            text = sb.toString();
        } catch (IllegalStateException e) {
            e.printStackTrace();

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {

                in.close();
            } catch (Exception ex) {
            }
        }

        return text;
    }
}