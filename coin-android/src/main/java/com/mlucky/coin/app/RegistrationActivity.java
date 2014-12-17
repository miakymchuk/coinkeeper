package com.mlucky.coin.app;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import com.mlucky.coin.app.gui.R;
import com.mlucky.coin.app.model.User;
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

    private class HttpRequestTask extends AsyncTask<Void, Void, User> {
        @Override
        protected User doInBackground(Void... voids) {
            try {
                final String url = "http://5.231.82.114//login";
                RestTemplate restTemplate = new RestTemplate();
                restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
                User user = restTemplate.getForObject(url, User.class);
                return user;
            } catch (Exception e) {
                Log.e("Login ", e.getMessage(), e);
            }
            return null;
        }
    }

}
