package tracking.bus.school.schoolbustracking;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.client.Firebase;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;

import tracking.bus.school.schoolbustracking.Adapter.ChildAdapter;
import tracking.bus.school.schoolbustracking.Adapter.DriverAdapter;
import tracking.bus.school.schoolbustracking.Models.Child;
import tracking.bus.school.schoolbustracking.Models.Profile;


public class DriverListFragment extends Fragment implements PopupMenu.OnMenuItemClickListener {
    View view;
    Button btnBack;

    DriverAdapter driverAdapter;
    ArrayList<Profile> drivers;

    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference myRef;

    private FirebaseUser user;
    private FirebaseAuth mAuth;
    PopupMenu popup;

    String driverId;

    FloatingActionButton fab_menu;

    public DriverListFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_driver_list, container, false);
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        fab_menu = (FloatingActionButton) getActivity().findViewById(R.id.fab_menu);

        Firebase.setAndroidContext(getActivity());

        btnBack = (Button) view.findViewById(R.id.btnBack);


        btnBack.setOnClickListener(new View.OnClickListener() {
                                       @Override
                                       public void onClick(View v) {
                                           removeFragment();
                                           fab_menu.setVisibility(View.VISIBLE);
                                       }
                                   });

        getChildrenList();
        return view;
    }

    private void getChildrenList(){
        drivers = new ArrayList<Profile>();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        myRef = mFirebaseDatabase.getReference().child("Profile");
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {

                drivers.clear();
                ArrayList<StorageReference> storageReferences = new ArrayList<StorageReference>();
                for (DataSnapshot ds : snapshot.getChildren()) {
                    FirebaseStorage storage = FirebaseStorage.getInstance();
                    Profile driver = new Profile();
                    if (ds.child("type").getValue().toString().equals("1")) {
                        driver.setFullName(ds.child("fullName").getValue().toString());
                        driver.setEmail(ds.child("email").getValue().toString());
                        driver.setId(ds.getKey().toString());
                        driver.setPassword(ds.child("email").getValue().toString());
                        driver.setType(ds.child("type").getValue().toString());

                        try {
                            driver.setCapacity(ds.child("capacity").getValue().toString());
                            driver.setCurrent(ds.child("current").getValue().toString());
                        } catch (NullPointerException e) {
                            driver.setCapacity("0");
                            driver.setCurrent("0");
                        }
                        drivers.add(driver);
                    }


                }

                driverAdapter = new DriverAdapter(drivers);
                driverAdapter.setContext(getContext());
                driverAdapter.notifyDataSetChanged();

                driverAdapter.setStorageReferences(storageReferences);

                RecyclerView rv = (RecyclerView) view.findViewById(R.id.lvDriver);

                rv.setAdapter(driverAdapter);
                LinearLayoutManager llm = new LinearLayoutManager(getActivity());
                llm.setOrientation(LinearLayoutManager.VERTICAL);
                rv.setLayoutManager(llm);

                rv.addOnItemTouchListener(new RecyclerItemListener(getContext(), rv,
                        new RecyclerItemListener.RecyclerTouchListener() {
                            public void onClickItem(View v, int position) {
                                final int samplePosition = position;
                                System.out.println(Config.APP_TYPE);
                                if (Config.APP_TYPE == "3"){
                                    AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                                    builder.setTitle("Input Capacity");
                                   View viewInflated = LayoutInflater.from(getContext()).inflate(R.layout.input_box_layout, (ViewGroup) getView(), false);
                                    final EditText input = (EditText) viewInflated.findViewById(R.id.input);
                                    builder.setView(viewInflated);

                                    builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            Profile profile = new Profile();
                                            profile = drivers.get(samplePosition);
                                            profile.setCapacity(input.getText().toString());
                                            addChild(profile);
                                            dialog.dismiss();
                                        }
                                    });
                                    builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.cancel();
                                        }
                                    });

                                    builder.show();
                                }
                                else if (Config.APP_TYPE == "2"){

                                }
                            }

                            public void onLongClickItem(View v, int position) {

                            }
                        }));
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    private void addChild(Profile profile){
                Firebase ref = new Firebase(Config.FIREBASE_URL);
                profile.setCurrent("0");
                ref.child("Profile").child(profile.getId()).setValue(profile);
                Toast.makeText(getActivity(), "Edit Capacity successful.", Toast.LENGTH_LONG).show();


    }

    public void removeFragment(){
        List<Fragment> fragments = getActivity().getSupportFragmentManager().getFragments();
        if (fragments != null) {
            for (Fragment fragment : fragments) {
                if (fragment != null)
                getActivity().getSupportFragmentManager().beginTransaction().remove(fragment).commit();
            }
        }
    }


    @Override
    public boolean onMenuItemClick(MenuItem item) {
        Firebase ref = new Firebase(Config.FIREBASE_URL);
        Child destination = new Child();
        switch (item.getItemId()) {
            case R.id.mnuFollowChild:

                return true;
            default:
                return false;
        }
    }


}
