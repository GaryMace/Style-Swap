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

import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.firebase.geofire.GeoQuery;
import com.firebase.geofire.GeoQueryEventListener;
import com.firebase.geofire.LocationCallback;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;

import jameshassmallarms.com.styleswap.R;
import jameshassmallarms.com.styleswap.impl.Match;
import jameshassmallarms.com.styleswap.impl.User;
import jameshassmallarms.com.styleswap.infrastructure.FireBaseQueries;
import jameshassmallarms.com.styleswap.infrastructure.Linker;
import jameshassmallarms.com.styleswap.infrastructure.QueryMaster;

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
    private Linker linker = (Linker)getActivity();
    private String userName = "haymakerStirrat@gmail.com";
    private FireBaseQueries fireBaseQueries = new FireBaseQueries();
    private ArrayList<Match> matchs = new ArrayList<>();


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
        getMatchs();

        return root;
    }

    public void onStart() {
        super.onStart();

        //System.out.println(linker.getLoggedInUser());
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
    //TODO dont match if matched recently
    private void getNewMatchs(){
        //users email
        DatabaseReference mUserRef = fireBaseQueries.getUserLocationReferenceByEmail(userName);
        final GeoFire geoFire = new GeoFire(mUserRef.getParent());


        geoFire.getLocation(mUserRef.getKey(), new LocationCallback() {
            @Override
            public void onLocationResult(String key, GeoLocation location) {
                if (location != null) {
                    GeoQuery geoQuery = geoFire.queryAtLocation(new GeoLocation(location.latitude, location.longitude), 10);//my search radius
                    geoQuery.addGeoQueryEventListener(new GeoQueryEventListener() {
                        @Override
                        public void onKeyEntered(String key, GeoLocation location) {
                            DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Users").child(key);
                            fireBaseQueries.executeIfExists(ref, new QueryMaster() {
                                @Override
                                public void run(DataSnapshot s) {

                                    User user = s.getValue(User.class);
                                    if (user.getDressSize() == 8)//my dress size
                                        matchs.add(user.toMatch());

                                    System.out.println(matchs);
                                }
                            });
                        }

                        @Override
                        public void onKeyExited(String key) {

                        }

                        @Override
                        public void onKeyMoved(String key, GeoLocation location) {

                        }

                        @Override
                        public void onGeoQueryReady() {

                        }

                        @Override
                        public void onGeoQueryError(DatabaseError error) {

                        }
                    });
                } else {
                    System.out.println(String.format("There is no location for key %s in GeoFire", key));
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.err.println("There was an error getting the GeoFire location: " + databaseError);
            }
        });

    }

    private void getMatchs(){

        final DatabaseReference matchedMe = fireBaseQueries.getMatchedme(userName);//users email
        fireBaseQueries.executeIfExists(matchedMe, new QueryMaster() {
            @Override
            public void run(DataSnapshot s) {
                matchs.clear();
                GenericTypeIndicator<ArrayList<Match>> t = new GenericTypeIndicator<ArrayList<Match>>() {
                };
                ArrayList<Match> update = s.getValue(t);
                if (update.size() > 1){
                    for (int i = 1; i < update.size() ; i++) {
                        matchs.add(update.get(i));
                        update.remove(i);
                    }
                    //matchedMe.setValue(update);//comment back in for vinal version just not removing so i can test
                }

                getNewMatchs();
            }
        });
    }

}
