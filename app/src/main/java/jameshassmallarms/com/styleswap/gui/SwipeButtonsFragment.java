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
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;

import jameshassmallarms.com.styleswap.R;
import jameshassmallarms.com.styleswap.impl.Match;
import jameshassmallarms.com.styleswap.impl.User;
import jameshassmallarms.com.styleswap.infrastructure.FireBaseQueries;
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
    private String userName = "haymakerStirrat@gmail.com";
    private FireBaseQueries fireBaseQueries = new FireBaseQueries();
    private ArrayList<User> matchs = new ArrayList<>();


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
                getMatchs();
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
    private void getMatchs(){
        //users email
        DatabaseReference mUserRef = FirebaseDatabase.getInstance().getReference().child("UserLocation").child("haymakerStirrat%40gmail%2Ecom");
//        DatabaseReference mUserRef1 = FirebaseDatabase.getInstance().getReference().child("UserLocation").child("haymakerStirrat%40gmail%2Ecom");
//        DatabaseReference mUserRef2 = FirebaseDatabase.getInstance().getReference().child("UserLocation").child("jameshassmallarms%40gmail%2Ecom");
//        DatabaseReference mUserRef3 = FirebaseDatabase.getInstance().getReference().child("UserLocation").child("nerthandrake%40gmail%2Ecom");
//        matchs.clear();
        final GeoFire geoFire = new GeoFire(mUserRef.getParent());
//        final GeoFire geoFire1 = new GeoFire(mUserRef.getParent());
//        final GeoFire geoFire2 = new GeoFire(mUserRef2.getParent());
//        final GeoFire geoFire3 = new GeoFire(mUserRef3.getParent());
        //geoFire.setLocation(mUserRef.getKey(), new GeoLocation(38.7853889, -122.4056973));
//        geoFire1.setLocation(mUserRef1.getKey(), new GeoLocation(38.7853889, -122.4056973));
//        geoFire2.setLocation(mUserRef2.getKey(), new GeoLocation(38.7853889, -122.4056973));
//        geoFire3.setLocation(mUserRef3.getKey(), new GeoLocation(38.7853889, -122.4056973));

        //return;
        geoFire.getLocation(mUserRef.getKey(), new LocationCallback() {
            @Override
            public void onLocationResult(String key, GeoLocation location) {
                if (location != null) {
                    GeoQuery geoQuery = geoFire.queryAtLocation(new GeoLocation(location.latitude, location.longitude), 10);//my search radius
                    geoQuery.addGeoQueryEventListener(new GeoQueryEventListener() {
                        @Override
                        public void onKeyEntered(String key, GeoLocation location) {
                            DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Users").child(key);
                            System.out.println(key);
                            System.out.println(ref);
                            fireBaseQueries.executeIfExists(ref, new QueryMaster() {
                                @Override
                                public void run(DataSnapshot s) {

                                    User user = s.getValue(User.class);
                                    if (user.getDressSize() == 8)//my dress size
                                        matchs.add(user);

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

}
