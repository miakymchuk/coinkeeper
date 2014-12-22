package com.mlucky.coin.app.gui;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import com.mlucky.coin.app.model.User;
import org.springframework.http.*;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

/**
 * Created by m.iakymchuk on 17.12.2014.
 */
public class LoginActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_login);
    }
    private class HttpLoginTask extends AsyncTask<Void, Void, User> {
        private final String TAG = HttpLoginTask.class.getSimpleName();
        private String username;
        private String password;

        @Override
        protected void onPreExecute() {
            EditText mUsername = (EditText) findViewById(R.id.editText_login);
            this.username = mUsername.getText().toString();
            EditText mPassword = (EditText) findViewById(R.id.editText_password);
            this.password = mPassword.getText().toString();
        }

        @Override
        protected User doInBackground(Void... strings) {
            final String url = "http://5.231.82.114//login";
           // Set the username and password for creating a Basic Auth request
            HttpAuthentication authHeader = new HttpBasicAuthentication(username, password);
            HttpHeaders requestHeaders = new HttpHeaders();
            requestHeaders.setAuthorization(authHeader);
            HttpEntity<?> requestEntity = new HttpEntity<Object>(requestHeaders);

            // Create a new RestTemplate instance
            RestTemplate restTemplate = new RestTemplate();

            // Add the String message converter
            restTemplate.getMessageConverters().add(new StringHttpMessageConverter());

            try {
                // Make the HTTP GET request to the Basic Auth protected URL
                ResponseEntity<User> response = restTemplate.exchange(url, HttpMethod.GET, requestEntity, User.class);
                return response.getBody();
            } catch (HttpClientErrorException e) {
                Log.e(TAG, e.getLocalizedMessage(), e);
                //TODO Handle 401 Unauthorized response
            }

            return null;
        }
    }
}
