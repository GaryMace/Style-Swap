package jameshassmallarms.com.styleswap.gui.discover;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
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
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;

import jameshassmallarms.com.styleswap.R;
import jameshassmallarms.com.styleswap.base.MainActivity;
import jameshassmallarms.com.styleswap.gui.BlankFragment;
import jameshassmallarms.com.styleswap.gui.im.ChatMessage;
import jameshassmallarms.com.styleswap.impl.Match;
import jameshassmallarms.com.styleswap.impl.User;
import jameshassmallarms.com.styleswap.infrastructure.DatabaseHandler;
import jameshassmallarms.com.styleswap.infrastructure.FireBaseQueries;
import jameshassmallarms.com.styleswap.infrastructure.Linker;
import jameshassmallarms.com.styleswap.infrastructure.QueryMaster;

/**
 * Created by Alan on 25/10/2016.
 */

public class SwipeButtonsFragment extends Fragment {

    public static final int LOADING_SIZE = 10;
    private BlankFragment blank;
    private ImageButton likeObject;
    private ImageButton dislikeObject;
    private String description;
    private TextView userNameView;
    private Fragment nestedCard;
    private FragmentTransaction transaction;
    private Queue<NestedInfoCard> nestedQueue;
    private String userName;
    private int dressSize = 8;
    private int searchRadius = 10;
    private boolean isBlank;
    private Linker linker;
    private FireBaseQueries fireBaseQueries = new FireBaseQueries();
    private ArrayList<Match> matchs = new ArrayList<>();
    private DatabaseHandler convert = new DatabaseHandler(getContext());

    QueryMaster q = new QueryMaster() {
        @Override
        public void run(DataSnapshot s) {
            description = s.getValue().toString();
        }
    };

    QueryMaster P = new QueryMaster() {
        @Override
        public void run(DataSnapshot s) {
            userNameView.setText(s.getValue().toString());
        }
    };


    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // set the view

        View root = inflater.inflate(R.layout.fragment_swipe_buttons, container, false);
        blank = new BlankFragment();
        linker = (Linker) getActivity();
        nestedCard = new NestedInfoCard();
        nestedQueue = new LinkedList<NestedInfoCard>();

        userName = linker.getLoggedInUser();
        Log.d("TAG", "prafff: " + linker.getLoggedInUser());


        //System.out.println(linker.getLoggedInUser());
        likeObject = (ImageButton) root.findViewById(R.id.fragment_yes_button);
        dislikeObject = (ImageButton) root.findViewById(R.id.fragment_no_button);


        loadBlankFragment();
//        } else {
//            NestedInfoCard card = loadFragment(matchs.get(0));
//            nestedQueue.add(card);
//            matchs.remove(0);
//
//            replaceFragment(nestedQueue.poll());
//
//        }


        likeObject.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (nestedQueue.isEmpty()) {
                    loadBlankFragment();
                    if (userName != null)
                        getMatchs();
                } else {
                    if (!isBlank) {
                        executeIfExists(fireBaseQueries.getMatchedme(userName), new QueryMaster() {
                            @Override
                            public void run(DataSnapshot s) {
                                GenericTypeIndicator<ArrayList<Match>> t = new GenericTypeIndicator<ArrayList<Match>>() {
                                };
                                ArrayList<Match> update = s.getValue(t);
                                for (int i = 1; i < update.size(); i++) {
                                    Match m = update.get(i);
                                    if (m.getMatchMail().equals(matchs.get(0).getMatchMail())) {
                                        Log.d("Email for encoding", userName);
                                        Log.d("Other Email", matchs.get(0).getMatchMail());
                                       final String chatKey = FireBaseQueries.encodeKey(userName)
                                                + FireBaseQueries.encodeKey(matchs.get(0).getMatchMail());
                                        fireBaseQueries.executeIfExists(fireBaseQueries.getBothMatched(userName), new QueryMaster() {
                                            @Override
                                            public void run(DataSnapshot s) {
                                                Match nMatch = new Match();
                                                nMatch.setMatchMail(matchs.get(0).getMatchMail());
                                                nMatch.setMatchNumber(matchs.get(0).getMatchNumber());
                                                nMatch.setMatchName(matchs.get(0).getMatchName());
                                                nMatch.setChatKey(chatKey);
                                                fireBaseQueries.addMatch(userName, MainActivity.FIREBASE_BOTH_MATCHED,nMatch);
                                            }
                                        });

                                        fireBaseQueries.executeIfExists(fireBaseQueries.getBothMatched(matchs.get(0).getMatchMail()), new QueryMaster() {
                                            @Override
                                            public void run(DataSnapshot s) {
                                                Match nMatch = new Match();
                                                nMatch.setMatchMail(userName);
                                                nMatch.setMatchNumber(linker.getPhoneNumber());
                                                nMatch.setMatchName(linker.getUserName());
                                                nMatch.setChatKey(chatKey);
                                                fireBaseQueries.addMatch(matchs.get(0).getMatchMail(), MainActivity.FIREBASE_BOTH_MATCHED,nMatch);
                                            }
                                        });
                                        ChatMessage message = new ChatMessage("Hello,I matched you", userName);
                                        fireBaseQueries.createChatRoom(chatKey).push().setValue(message);
                                        fireBaseQueries.removeMatch(userName, MainActivity.FIREBASE_MATCHED_ME,i);


                                    }
                                }
                            }
                        });
                        //Run Queries
                    }

                    replaceFragment(nestedQueue.poll());
                    matchs.remove(0);
                }
            }
        });

        dislikeObject.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (nestedQueue.isEmpty()) {
                    loadBlankFragment();
                    if (userName != null)
                        getMatchs();
                } else {
                    replaceFragment(nestedQueue.poll());
                }
            }
        });

        return root;
    }

    public void onStart() {
        super.onStart();
        userName = linker.getLoggedInUser();
        if (userName != null && nestedQueue.size() == 0) {
            getMatchs();

            System.out.println("here");
        }

    }

    private NestedInfoCard loadFragment(Match match) {
        //Need to add in a query to get name description and picture
        String u = match.getMatchName();
        String desc = match.getMatchBio();
        String email = match.getMatchMail();
        String number = match.getMatchNumber();
        if ((match.getByteArray()) == null) {
            Log.d("Debug_swipe", "Empty match byte array");
        }
        byte[] image = match.getByteArray();
        if (image == null) {
            Log.d("IMAGE DOESn't Exist", "LAMEEEE");
        }
        Bundle b = new Bundle();
        b.putString("UserName", u);
        b.putString("Description", desc);
        b.putString("Email", email);
        b.putByteArray("IMG", image);
        b.putString("Num", number);
        NestedInfoCard nest = new NestedInfoCard();
        nest.setArguments(b);
        Log.d("tag", "Fragment added to stack");
        return nest;
    }

    private void replaceFragment(NestedInfoCard nest) {
        transaction = getChildFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_match_frame, nest, "TAG").commit();
        Log.d("Fragment", "Replaced");
    }

    //    private void fillFragments(){
//        count = 0;
//
//        nestedCards.clear();
//        for(int i = 0; i < LOADING_SIZE; i++){
//            NestedInfoCard card = loadFragment();
//            nestedCards.add(card);
//        }
//    }
//    //TODO dont match if matched recently
    private void getNewMatchs() {
        //users email

        final DatabaseReference mUserRef = fireBaseQueries.getUserLocationReferenceByEmail(userName);
        final GeoFire geoFire = new GeoFire(mUserRef.getParent());

        geoFire.setLocation(mUserRef.getKey(), new GeoLocation(linker.getDeviceLat(), linker.getDeviceLon()), new GeoFire.CompletionListener() {
            @Override
            public void onComplete(String key, DatabaseError error) {
                if (error == null) {
                    geoFire.getLocation(mUserRef.getKey(), new LocationCallback() {
                        @Override
                        public void onLocationResult(String key, GeoLocation location) {
                            if (location != null) {
                                GeoQuery geoQuery = geoFire.queryAtLocation(new GeoLocation(location.latitude, location.longitude), searchRadius);//my search radius
                                geoQuery.addGeoQueryEventListener(new GeoQueryEventListener() {
                                    @Override
                                    public void onKeyEntered(String key, GeoLocation location) {
                                        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Users").child(key);
                                        fireBaseQueries.executeIfExists(ref, new QueryMaster() {
                                            @Override
                                            public void run(DataSnapshot s) {
                                                User user = s.getValue(User.class);
                                                if (user.getDressSize() == dressSize)//my dress size
                                                    addToQueue(user.toMatch());
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

        });


    }

    private void getMatchs() {
        final DatabaseReference matchedMe = fireBaseQueries.getMatchedme(userName);//users email
        fireBaseQueries.executeIfExists(matchedMe, new QueryMaster() {
            @Override
            public void run(DataSnapshot s) {
                GenericTypeIndicator<ArrayList<Match>> t = new GenericTypeIndicator<ArrayList<Match>>() {
                };
                ArrayList<Match> update = s.getValue(t);
                boolean replaceFlag = true;
                for (int i = 1; i < update.size(); ) {
                    addToQueue(update.get(i));
                    update.remove(i);
                }
                //matchedMe.setValue(update);//comment back in for vinal version just not removing so i can test

                getNewMatchs();

            }
        });
    }


    private void loadBlankFragment() {
        transaction = getChildFragmentManager().beginTransaction();
        transaction.add(R.id.fragment_match_frame, blank, "TAG").commit();
        isBlank = true;
    }


    public void addToQueue(final Match match) {
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference picRef = storage.getReferenceFromUrl("gs://styleswap-f3aa9.appspot.com").child(match.getMatchMail() + "/" + "Dress");
        Log.d("Username Check", match.getMatchName());

        final long ONE_MEGABYTE = 1024 * 1024;
        picRef.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
            @Override
            public void onSuccess(byte[] bytes) {
                Log.d("User", match.getMatchMail());
                match.setByteArray(bytes);
                nestedQueue.add(loadFragment(match));
                if (isBlank) {
                    replaceFragment(nestedQueue.poll());
                    isBlank = false;
                }
                matchs.add(match);

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                Log.d("Pic load Failed", "WHy");
                // Handle any errors
            }
        });
    }

    public void executeIfExists(DatabaseReference databaseReference, final QueryMaster q) {
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists())
                    q.run(dataSnapshot);

                else{
                    Match match = new Match();
                    match.setMatchMail(userName);
                    match.setMatchNumber(linker.getPhoneNumber());
                    match.setMatchName(linker.getUserName());
                    fireBaseQueries.addMatch(matchs.get(0).getMatchMail(),MainActivity.FIREBASE_MATCHED_ME,match);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

}