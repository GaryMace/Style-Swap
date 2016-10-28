package jameshassmallarms.com.styleswap.base;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import jameshassmallarms.com.styleswap.R;

public class Register extends AppCompatActivity implements View.OnClickListener {

    Button buttonRegister;
    EditText etName, etLocation, etAge, etUsername, etPassword, etDressSize, etEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        etName = (EditText) findViewById(R.id.etName);
        etAge = (EditText) findViewById(R.id.etAge);
        etEmail = (EditText) findViewById(R.id.etEmail);
        etLocation = (EditText) findViewById(R.id.etLocation);
        etUsername = (EditText) findViewById(R.id.etUsername);
        etPassword = (EditText) findViewById(R.id.etPassword);
        etDressSize = (EditText) findViewById(R.id.etDressSize);

        buttonRegister = (Button) findViewById(R.id.ButtonRegister);

        buttonRegister.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.ButtonRegister:

            break;
        }
    }
}
