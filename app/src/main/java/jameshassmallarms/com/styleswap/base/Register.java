package jameshassmallarms.com.styleswap.base;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import jameshassmallarms.com.styleswap.R;

public class Register extends AppCompatActivity{
    public static final String REGISTER_NEW_USER = "register_new";

    public static final String REGISTER_EMAIL  ="reg_email";
    public static final String REGISTER_NAME  ="reg_name";
    public static final String REGISTER_PHONE  ="reg_phone";
    public static final String REGISTER_PASSWORD  ="reg_pass";
    public static final String REGISTER_SIZE  ="reg_size";
    public static final String REGISTER_AGE  ="reg_age";       //Why we need an age?
    public static final String REGISTER_LOCATION  ="reg_loc"; //Is this needed?


    Button buttonRegister;
    EditText etName, etLocation, etAge, etUsername, etPassword, etDressSize, etEmail;
    //FireBaseQueries fireBaseQueries = new FireBaseQueries();

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
                    //pushUserDetails();
                }
            }
        });

        /**
         * If the Register button is clicked, check if user email already exists.
         *
         * If they dont "resultIntent.putString(MainActivity.GET_LOGIN_STATE, REGISTER_NEW_USER);
         * and pass all relevent info back with resultIntent.putString(REGISTER_NAME, "gary); or
         * whatever
         */
    }

    private boolean validRegistrationInfo(){
        return true;
    }

    //should use pushNewUserDetails instead
    /*public void pushUserDetails(){
        DatabaseReference mUserRef = fireBaseQueries.getUserReferenceByEmail(etEmail.getText().toString());
        mUserRef.child("email").setValue(etEmail.getText());//check its unique
        mUserRef.child("password").setValue(etPassword.getText());//check its a decent password
        mUserRef.child("name").setValue(etName.getText());
        mUserRef.child("location").setValue(etLocation.getText());//should be gotten from phone prob not entered
        mUserRef.child("dressSize").setValue(etDressSize.getText());//should be entered using spinner maybe?
        mUserRef.child("phoneNumber").setValue("");//need value for this
    }*/
}
