package jameshassmallarms.com.styleswap.base;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;

import jameshassmallarms.com.styleswap.R;
import jameshassmallarms.com.styleswap.impl.User;
import jameshassmallarms.com.styleswap.infrastructure.FireBaseQueries;

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
    FireBaseQueries fireBaseQueries = new FireBaseQueries();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_register);

            mName = (EditText) findViewById(R.id.registerName);
            mAge = (EditText) findViewById(R.id.registerAge);
            mEmail = (EditText) findViewById(R.id.registerEmail);
            mPassword = (EditText) findViewById(R.id.registerPassword);
            mDressSize = (EditText) findViewById(R.id.registerDressSize);
            mPhoneNumber = (EditText) findViewById(R.id.registerPhoneNumber);
            buttonRegister = (Button) findViewById(R.id.ButtonRegister);

            buttonRegister.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                        if (validateRegister());
                            registerIfNew();

                }
            });
    }


    public void launchLogin(){
        Intent intent = new Intent();
        intent.putExtra(MainActivity.GET_LOGIN_STATE, REGISTER_NEW_USER);
        intent.putExtra(REGISTER_EMAIL, mEmail.getText().toString());
        intent.putExtra(REGISTER_NAME, mName.getText().toString());
        intent.putExtra(REGISTER_AGE, Integer.valueOf(mAge.getText().toString()));
        intent.putExtra(REGISTER_PASSWORD, mPassword.getText().toString());
        intent.putExtra(REGISTER_PHONE, Integer.valueOf(mPhoneNumber.getText().toString()));
        intent.putExtra(REGISTER_SIZE, Integer.valueOf(mDressSize.getText().toString()));
        setResult(Activity.RESULT_OK, intent);
        finish();
    }

    
    private void registerIfNew(){
        DatabaseReference userRef = fireBaseQueries.getUserReferenceByEmail(mEmail.getText().toString());
        userRef.addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    //sorry user already exsists
                    Toast.makeText(getBaseContext(), "User with that email already exists.", Toast.LENGTH_SHORT).show();
                }
                else {
                    //register
                    User newUser = new User(mEmail.getText().toString(), mPassword.getText().toString(),
                            mName.getText().toString(), Integer.valueOf(mAge.getText().toString()),
                            Integer.valueOf(mDressSize.getText().toString()), Integer.valueOf(mPhoneNumber.getText().toString()));

                    fireBaseQueries.pushNewUserDetails(newUser);
                    //Drawable myDrawable = getResources().getDrawable(R.drawable.stock_img);

                    StorageReference picRef = FirebaseStorage.getInstance().getReferenceFromUrl("gs://styleswap-f3aa9.appspot.com").child(mEmail + "/" + "Dress");
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    Bitmap mybit = BitmapFactory.decodeResource(getResources(), R.drawable.profilepicexample);
                    mybit.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                    byte[] data = baos.toByteArray();

                    UploadTask uploadTask = picRef.putBytes(data);
                    uploadTask.addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {
                            // Handle unsuccessful uploads
                        }
                    }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            // taskSnapshot.getMetadata() contains file metadata such as size, content-type, and download URL.
                            Uri downloadUrl = taskSnapshot.getDownloadUrl();
                        }
                    });
                    //tock.setImageDrawable(myDrawable);

                    Toast.makeText(getBaseContext(), "Register complete, logging in!", Toast.LENGTH_SHORT).show();
                    launchLogin();
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private boolean validateRegister(){
        if(mName.getText().toString().isEmpty() || mPassword.getText().toString().isEmpty() ||
               mAge.getText().toString().isEmpty() || mEmail.getText().toString().isEmpty() ||
               mPhoneNumber.getText().toString().isEmpty() || mDressSize.getText().toString().isEmpty()){

            Toast.makeText(getBaseContext(), "Please fill all fields to complete register", Toast.LENGTH_SHORT).show();
            return false;
        }
        int dressSize = Integer.valueOf(mDressSize.getText().toString());

        if (dressSize % 2 != 0 || dressSize < 0 || dressSize > 40){
            Toast.makeText(getBaseContext(), "Invalid dress size", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (mPhoneNumber.getText().toString().length() != 10){
            Toast.makeText(getBaseContext(), "Invaild phone number", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

}
