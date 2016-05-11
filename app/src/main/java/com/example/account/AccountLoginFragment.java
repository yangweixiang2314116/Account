package com.example.account;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.account.Constants;
import com.example.module.AccountRestClient;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;

public class AccountLoginFragment extends Fragment {
    private Button m_LoginButton = null;
    private EditText m_UserName = null;
    private EditText m_Password = null;
    private SharedPreferences mSharedPreferences = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.i(Constants.TAG, "-------LoginFragment----onCreate--------");
        super.onCreate(savedInstanceState);

        mSharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(getContext());

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View content = inflater.inflate(R.layout.login_fragment_layout, container, false);

        m_UserName = (EditText)content.findViewById(R.id.username_content);
        m_Password = (EditText)content.findViewById(R.id.password_content);
        m_LoginButton = (Button)content.findViewById(R.id.account_register);

        m_UserName.clearFocus();
        m_Password.clearFocus();


        m_LoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                        String userName = m_UserName.getText().toString();
                        String password = m_Password.getText().toString();
                        m_TryLogin(userName,password);
            }
        });

        return content;
    }

    private void m_TryLogin(String userName, String password)
    {
        Log.i(Constants.TAG, "-------m_TryLogin----userName--------"+userName+"--password---"+password);
            //TODO check username
            final String checkUserName = userName;
            //TODO check password
        getActivity().getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        AccountApiConnector.instance().getToken(userName, password, new JsonHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                // If the response is JSONObject instead of expected JSONArray
                Log.i(Constants.TAG, "---getToken--onSuccess--response---" + response);

                String token  = null;
                try {
                    token = response.getString("message");
                    AccountRestClient.SetClientToken(token);

                    mSharedPreferences.edit().putBoolean("is_login", true)
                            .apply();

                    mSharedPreferences.edit().putString("user_name", checkUserName)
                            .apply();

                    Toast.makeText(getActivity(), R.string.account_login_success, Toast.LENGTH_SHORT).show();
                    getActivity().finish();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override

            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject response) {
                super.onFailure(statusCode, headers, throwable, response);
                Log.i(Constants.TAG, "---getToken--onFailure--statusCode---" + statusCode);
                Log.i(Constants.TAG, "---getToken--onFailure--responseString---" + response);

                Toast.makeText(getActivity(), R.string.account_login_failed, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFinish() {
                super.onFinish();
            }

        });
    }
}