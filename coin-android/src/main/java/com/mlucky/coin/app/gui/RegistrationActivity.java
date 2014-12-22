package com.mlucky.coin.app.gui;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import com.mlucky.coin.app.gui.R;
import com.mlucky.coin.app.model.User;

import com.mlucky.coin.app.model.ValidUser;
import org.springframework.http.*;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

/**
 * Created by m.iakymchuk on 17.12.2014.
 */
public class RegistrationActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.registration);
    }

    private class HttpRequestTask extends AsyncTask<String, Void, String> {
		private final String TAG = HttpRequestTask.class.getSimpleName();

		private boolean isDataCorrect = true;
		private String errorCode;
        private User user;

        @Override
		protected void onPreExecute() {
			EditText mPassword = (EditText) findViewById(R.id.editText_new_password);
            String password = mPassword.getText().toString();
			EditText mConfirmPassword = (EditText) findViewById(R.id.editText_new_confirm_password);
            String confirmPassword = mConfirmPassword.getText().toString();

            if (!password.equals(confirmPassword)
                    || password.isEmpty() || confirmPassword.isEmpty()) {
				isDataCorrect = false;
				errorCode = "Passwords is not equals or empty!";
                Log.d(TAG, errorCode);
                return;
			}
            EditText mUsername = (EditText) findViewById(R.id.editText_new_login);
            String name = mUsername.getText().toString();
            if (name.isEmpty()) {
                errorCode = "Name is empty";
                Log.d(TAG, errorCode);
                return;
            }
            this.user = new User();
            this.user.setName(name);
            this.user.setPassword(password);
		}
		
        @Override
        protected String doInBackground(String... strings) {
            String response = new String();
            if (!isDataCorrect) {
				//TODO need notify user about kind of error
				return "";
			}
            try {
                final String URL = "http://5.231.82.114/registration";
                HttpHeaders headers = new HttpHeaders();
                headers.setContentType(MediaType.APPLICATION_JSON);
                HttpEntity<User> entity = new HttpEntity<User>(user , headers);
                RestTemplate restTemplate = new RestTemplate();
                restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
                restTemplate.getMessageConverters().add(new StringHttpMessageConverter());
                //response = restTemplate.postForObject(URL, user, String.class);
//                ResponseEntity<ValidUser> responseEntity = restTemplate.exchange(URL, HttpMethod.POST, entity, ValidUser.class);
//                ValidUser validUserResponse = responseEntity.getBody();
                ResponseEntity<String> responseEntity = restTemplate.exchange(URL, HttpMethod.POST, entity, String.class);
                response = responseEntity.getBody();
                //restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
                //User user = restTemplate.getForObject(url, User.class);

            } catch (Exception e) {
                Log.e("Login ", e.getMessage(), e);
            }
            return response;
        }
    }
}
