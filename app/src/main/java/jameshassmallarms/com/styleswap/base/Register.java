package jameshassmallarms.com.styleswap.base;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.firebase.database.DatabaseReference;

import jameshassmallarms.com.styleswap.R;
import jameshassmallarms.com.styleswap.infrastructure.FireBaseQueries;

public class Register extends AppCompatActivity{

    Button buttonRegister;
    EditText etName, etLocation, etAge, etUsername, etPassword, etDressSize, etEmail;
    FireBaseQueries fireBaseQueries = new FireBaseQueries();

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

        buttonRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validRegistrationInfo()){
                    pushUserDetails();
                }
            }
        });


    }

    private boolean validRegistrationInfo(){
        return true;
    }

    public void pushUserDetails(){
        DatabaseReference mUserRef = fireBaseQueries.getUserReferenceByEmail(etEmail.getText().toString());
        mUserRef.child("email").setValue(etEmail.getText());//check its unique
        mUserRef.child("password").setValue(etPassword.getText());//check its a decent password
        mUserRef.child("name").setValue(etName.getText());
        mUserRef.child("location").setValue(etLocation.getText());//should be gotten from phone prob not entered
        mUserRef.child("dressSize").setValue(etDressSize.getText());//should be entered using spinner maybe?
        mUserRef.child("phoneNumber").setValue("");//need value for this
    }
}
