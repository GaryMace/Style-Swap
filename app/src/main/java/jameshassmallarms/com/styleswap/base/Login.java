package jameshassmallarms.com.styleswap.base;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;

import jameshassmallarms.com.styleswap.R;
import jameshassmallarms.com.styleswap.infrastructure.FireBaseQueries;

import static jameshassmallarms.com.styleswap.base.Register.REGISTER_NAME;


public class Login extends AppCompatActivity{
    public static final String LOGIN_USER_EMAIL = "login_email";
    public static final String LOGIN_EXISTING_USER = "login_existing";
    Button ButtonLogin;
    EditText etUsername, etPassword;
    TextView LinkWithRegister;
    boolean flag;
    FireBaseQueries newQuery = new FireBaseQueries();
    AppStartupActivtiy test = new AppStartupActivtiy();
    Intent loginIntent = new Intent();


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        while(flag = false) {

            etUsername = (EditText) findViewById(R.id.etEmail);
            etPassword = (EditText) findViewById(R.id.etPassword);
            ButtonLogin = (Button) findViewById(R.id.ButtonLogin);
            LinkWithRegister = (TextView) findViewById(R.id.LinkWithRegister);
            ButtonLogin.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    if(etUsername.getText().toString().isEmpty()|| etPassword.getText().toString().isEmpty()){
                        Toast.makeText(getBaseContext(), "Username or password left empty", Toast.LENGTH_SHORT).show();
                    }else {
                        flag = true;
                        //boolean loginSuccess;
                        //loginSuccess = attemptLogin();
                        //if (loginSuccess){
                            String userName = etUsername.getText().toString();
                            String userPassword = etPassword.getText().toString();
                            Intent i = new Intent();
                            i.putExtra(MainActivity.GET_LOGIN_STATE, LOGIN_EXISTING_USER);
                            i.putExtra(LOGIN_USER_EMAIL, userName);
                            setResult(Activity.RESULT_OK, i);
                        //}

                    }
                }
            });


            LinkWithRegister.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getBaseContext(), Register.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_FORWARD_RESULT);
                    startActivity(intent);
                }
            });
        }
    }

    public boolean attemptLogin(){
        DatabaseReference testString;
        boolean localFLag;
        testString = newQuery.getUserName(etUsername.getText().toString());
        if(testString.equals(null) == false){
            localFLag = true;
        }

        else{
            Log.d("The user doesnt exist", "");
            localFLag = false;
        }
        return localFLag;
    }
}



