package jameshassmallarms.com.styleswap.gui;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;

import jameshassmallarms.com.styleswap.R;

/**
 * Created by Alan on 25/10/2016.
 */

public class SwipeButtonsFragment extends Fragment {

    public static final int LOADING_SIZE  = 10;
    private ImageButton likeObject;
    private  ImageButton dislikeObject;
    private Fragment nestedCard;
    private FragmentTransaction transaction;
    private ArrayList<NestedInfoCard> nestedCards;
    private int count;
    private String userName = "haymakerStirrat@gmail.com";



    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // set the view
        View root = inflater.inflate(R.layout.fragment_swipe_buttons, container, false);
        count = 0;
        nestedCards = new ArrayList<NestedInfoCard>();
         nestedCard = new NestedInfoCard();
         transaction = getChildFragmentManager().beginTransaction();
        transaction.add(R.id.fragment_match_frame, nestedCard, "TAG").commit();


        likeObject = (ImageButton) root.findViewById(R.id.fragment_yes_button);
        dislikeObject = (ImageButton) root.findViewById(R.id.fragment_no_button);


        return root;
    }

    public void onStart() {
        super.onStart();


        for(int i = 0; i < LOADING_SIZE; i++){
            NestedInfoCard card = loadFragment();
            nestedCards.add(card);
        }


        likeObject.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                count++;
                if(count == LOADING_SIZE ){
                    fillFragments();
                }
                replaceFragment(nestedCards.get(count));

            }
        });

        dislikeObject.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                count++;
                if(count == LOADING_SIZE){
                    fillFragments();
                }
                replaceFragment(nestedCards.get(count));


            }
        });

    }

    private NestedInfoCard loadFragment(){
        //Need to add in a query to get name description and picture



        Bundle b = new Bundle();
        b.putString("UserEmail", userName);
        NestedInfoCard nest = new NestedInfoCard();
        nest.setArguments(b);
        Log.d("tag","Fragment added to stack");
        return nest;
    }

    private void replaceFragment(NestedInfoCard nest){
        transaction = getChildFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_match_frame,nest,"TAG").commit();
        Log.d("Fragment","Replaced");
    }

    private void fillFragments(){
        count = 0;

        nestedCards.clear();
        for(int i = 0; i < LOADING_SIZE; i++){
            NestedInfoCard card = loadFragment();
            nestedCards.add(card);
        }
    }





}
