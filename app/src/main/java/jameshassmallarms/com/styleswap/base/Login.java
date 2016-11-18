package jameshassmallarms.com.styleswap.base;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import jameshassmallarms.com.styleswap.R;


public class Login extends AppCompatActivity implements View.OnClickListener {
    public static final String LOGIN_EXISTING_USER = "login_existing";
    public static final String LOGIN_USER_EMAIL = "login_email";

    Button ButtonLogin;
    EditText etUsername, etPassword;
    TextView LinkWithRegister;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        etUsername = (EditText) findViewById(R.id.etUsername);
        etUsername.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        etPassword = (EditText) findViewById(R.id.etPassword);
        ButtonLogin = (Button) findViewById(R.id.ButtonLogin);
        LinkWithRegister = (TextView) findViewById(R.id.LinkWithRegister);
        ButtonLogin.setOnClickListener(this);
        LinkWithRegister.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){ //need a switch statement to see which button was clicked in login
            case R.id.ButtonLogin:


                break;

            case R.id.LinkWithRegister:
                startActivity(new Intent(this, Register.class));
                break;
        }

    }

}
