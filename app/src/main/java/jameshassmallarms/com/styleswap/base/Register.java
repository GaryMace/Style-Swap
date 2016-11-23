package jameshassmallarms.com.styleswap.base;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import jameshassmallarms.com.styleswap.R;
import jameshassmallarms.com.styleswap.gui.UserExistsFragment;

import static java.lang.Integer.parseInt;

public class Register extends AppCompatActivity{
    public static final String REGISTER_NEW_USER = "register_new";

    public static final String REGISTER_EMAIL  ="reg_email";
    public static final String REGISTER_NAME  ="reg_name";
    public static final String REGISTER_PHONE  ="reg_phone";
    public static final String REGISTER_PASSWORD  ="reg_pass";
    public static final String REGISTER_SIZE  ="reg_size";
    public static final String REGISTER_AGE  ="reg_age";       //Why we need an age?


    private Button buttonRegister;
    private EditText mName, mAge, mUsername, mPassword, mDressSize, mEmail, mPhoneNumber;
   // FireBaseQueries fireBaseQueries = new FireBaseQueries();
    boolean userExists, detailsOkay;
    UserExistsFragment user = new UserExistsFragment();
    boolean overallFlag = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_register);

            mName = (EditText) findViewById(R.id.activity_register_username);
            mAge = (EditText) findViewById(R.id.activity_register_age);
            mEmail = (EditText) findViewById(R.id.activity_register_email);
            mUsername = (EditText) findViewById(R.id.activity_register_email);
            mPassword = (EditText) findViewById(R.id.activity_register_password);
            mDressSize = (EditText) findViewById(R.id.activity_register_size);
            mPhoneNumber = (EditText) findViewById(R.id.activity_register_number);
            buttonRegister = (Button) findViewById(R.id.ButtonRegister);
            buttonRegister.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                        //detailsOkay = avoidEmptyDetails();
                        //userExists = checkIfUserExists();

                        //if (userExists && detailsOkay) {
                            overallFlag = true;
                            int age = parseInt(mAge.getText().toString());
                            //int dressSize = parseInt(mDressSize.getText().toString());
                            //User newUser = new User(mName.getText().toString(), age, mEmail.getText().toString(), mUserPassword.getText().toString(), dressSize);
                            //fireBaseQueries.pushNewUserDetails(newUser);

                            Intent intent = new Intent();
                            intent.putExtra(MainActivity.GET_LOGIN_STATE, REGISTER_NEW_USER);
                            intent.putExtra(REGISTER_EMAIL, mEmail.getText().toString());
                            intent.putExtra(REGISTER_NAME, mName.getText().toString());
                            intent.putExtra(REGISTER_AGE, age);
                            intent.putExtra(REGISTER_PASSWORD, mPassword.getText().toString());
                            intent.putExtra(REGISTER_PHONE, mPhoneNumber.getText().toString());
                            intent.putExtra(REGISTER_SIZE, mDressSize.getText().toString());
                            setResult(Activity.RESULT_OK, intent);
                        //} else {
                           // popUp("User Already exists or you have left out some details");
                            //launchRegister();
                        //}
                        ;

                }
            });
    }

    private void popUp(String message){
        AlertDialog.Builder dlgAlert = new AlertDialog.Builder(this);
        dlgAlert.setMessage(message);
        dlgAlert.setTitle("PROBLEM");
        dlgAlert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        dlgAlert.setCancelable(true);
        dlgAlert.create().show();
    }

    public void launchLogin(){startActivity(new Intent(this, Login.class));}

    
//    private boolean checkIfUserExists(){
//        DatabaseReference checkUser = fireBaseQueries.getUserReferenceByEmail(mEmail.getText().toString());
//        boolean check;
//        if(checkUser == null){
//            check = true; //if user exists doesnt exist we know we have a new user so want to push the details
//
//        }
//        else{
//            check = false; //otherwise we want to create a pop up saying account already exists and then giving another login attempt
//            popUp("That email already exists. Try logging in");
//            launchLogin();
//        }
//        return check;
//    }
//
//    public boolean avoidEmptyDetails(){
//        boolean flag;
//        if(mName.getText().toString().isEmpty() || mUserPassword.getText().toString().isEmpty() || mEmail.getText().toString().isEmpty() || mDressSize.getText().toString().isEmpty()){
//            popUp("Problem with registration. Make sure all fields are filled");
//            popUp("Please start again");
//           flag = false;
//        }
//        else{
//            flag = true;
//        }
//        return flag;
//    }
}

