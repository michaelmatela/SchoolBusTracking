package tracking.bus.school.schoolbustracking;

import android.content.DialogInterface;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.FragmentManager;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Toast;

import com.firebase.client.Firebase;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.util.Date;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private FirebaseUser user;
    private FirebaseAuth mAuth;
    FloatingActionButton fab_menu, fab_sos, fab_profile, fab_login, fab_register, fab_logout, fab_children, fab_follow, fab_admin_map, fab_driver_lists, fab_parent_lists, fab_parent_to_driver;
    Animation fabOpen, fabClose, rotateForward, rotateBackward;
    boolean isOpen = false;
    String login = "";

    FragmentManager fragmentManager;
    FragmentTransaction fragmentTransaction;
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference myRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        try{
            if (Config.APP_TYPE.equals("3"))
                setTitle("Servisibility (Admin)");
            else if (Config.APP_TYPE.equals("2"))
                setTitle("Servisibility (Guardian)");
            else if (Config.APP_TYPE.equals("1"))
                setTitle("Servisibility (Driver)");
        }
        catch (NullPointerException e){setTitle("Servisibility");}

        fab_menu = (FloatingActionButton) findViewById(R.id.fab_menu);
        fab_profile = (FloatingActionButton) findViewById(R.id.fab_profile);
        fab_sos = (FloatingActionButton) findViewById(R.id.fab_sos);
        fab_login = (FloatingActionButton) findViewById(R.id.fab_login);
        fab_logout = (FloatingActionButton) findViewById(R.id.fab_logout);
        fab_register = (FloatingActionButton) findViewById(R.id.fab_register);
        fab_children = (FloatingActionButton) findViewById(R.id.fab_children);
        fab_follow = (FloatingActionButton) findViewById(R.id.fab_follow);
        fab_admin_map = (FloatingActionButton) findViewById(R.id.fab_admin_map);
        fab_driver_lists = (FloatingActionButton) findViewById(R.id.fab_driver_lists);
        fab_parent_lists = (FloatingActionButton) findViewById(R.id.fab_parent_lists);
        fab_parent_to_driver = (FloatingActionButton) findViewById(R.id.fab_parent_to_driver);


        fabOpen = AnimationUtils.loadAnimation(this,R.anim.fab_open);
        fabClose = AnimationUtils.loadAnimation(this,R.anim.fab_close);

        rotateForward = AnimationUtils.loadAnimation(this,R.anim.rotate_forward);
        rotateBackward = AnimationUtils.loadAnimation(this,R.anim.rotate_backward);

        fab_menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                animateFab();
            }
        });

        fab_follow.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){

                mFirebaseDatabase = FirebaseDatabase.getInstance();
                myRef = mFirebaseDatabase.getReference().child("ParentDriver").child(user.getUid());
                myRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot snapshot) {
                        try {
                            Intent intent = new Intent(MainActivity.this, DriverMapsActivity.class);
                            intent.putExtra("driverId", snapshot.getValue().toString());
                            MainActivity.this.startActivity(intent);
                        }
                        catch(NullPointerException e){ Toast.makeText(MainActivity.this, "Please contact admin to use this feature.", Toast.LENGTH_LONG).show();}
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                    }
                });
            }
        });

        fab_profile.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                animateFab();
                Intent intent = new Intent(MainActivity.this, ParentMapsActivity.class);
                MainActivity.this.startActivity(intent);

            }
        });

        fab_admin_map.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                animateFab();
                Intent intent = new Intent(MainActivity.this, AdminMapsActivity.class);
                MainActivity.this.startActivity(intent);

            }
        });

        fab_parent_to_driver.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                animateFab();
                Intent intent = new Intent(MainActivity.this, ParentToDriverActivity.class);
                MainActivity.this.startActivity(intent);

            }
        });

        fab_sos.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){

                mFirebaseDatabase = FirebaseDatabase.getInstance();
                myRef = mFirebaseDatabase.getReference().child("Profile").child(user.getUid()).child("type");
                myRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot snapshot) {

                        animateFab();
                        Config.APP_TYPE = snapshot.getValue().toString();
                        FragmentManager fragmentManager = getSupportFragmentManager();
                        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                        SOSFragment sosFragment = new SOSFragment();
                        AddSOSFragment addSOSFragment = new AddSOSFragment();

                        if (Config.APP_TYPE.equals("1"))
                            fragmentTransaction.replace(R.id.fragment_container, addSOSFragment);
                        else
                            fragmentTransaction.replace(R.id.fragment_container, sosFragment);

                        fragmentTransaction.commit();
                        mainGone();

                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                    }
                });


            }
        });



        fab_children.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                animateFab();
                FragmentManager fragmentManager = getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                ChildrenFragment childrenFragment = new ChildrenFragment();
                fragmentTransaction.replace(R.id.fragment_container, childrenFragment);
                fragmentTransaction.commit();
                mainGone();
            }
        });

        fab_driver_lists.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                animateFab();
                FragmentManager fragmentManager = getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                DriverListFragment driverListFragment = new DriverListFragment();
                fragmentTransaction.replace(R.id.fragment_container, driverListFragment);
                fragmentTransaction.commit();
                mainGone();
            }
        });

        fab_parent_lists.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                animateFab();
                FragmentManager fragmentManager = getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                ParentListFragment parentListFragment = new ParentListFragment();
                fragmentTransaction.replace(R.id.fragment_container, parentListFragment);
                fragmentTransaction.commit();
                mainGone();
            }
        });

        fab_register.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                animateFab();
                FragmentManager fragmentManager = getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                RegisterFragment registerFragment = new RegisterFragment();
                fragmentTransaction.replace(R.id.fragment_container, registerFragment);
                fragmentTransaction.commit();
                mainGone();
            }
        });

        fab_logout.setOnClickListener(new View.OnClickListener(){
           @Override
           public void onClick(View v){
               animateFab();
               AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
               builder.setTitle("Logout");
               builder.setMessage("Are you sure you want to logout?");

               builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {

                   public void onClick(DialogInterface dialog, int which) {
                       // Do nothing but close the dialog
                       FirebaseAuth.getInstance().signOut();
                       setTitle("Servisibility");
                       Toast.makeText(getBaseContext(), "You are now Logged out.", Toast.LENGTH_LONG).show();
                       dialog.dismiss();
                   }
               });

               builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {

                   @Override
                   public void onClick(DialogInterface dialog, int which) {

                       // Do nothing
                       dialog.dismiss();
                   }
               });

               AlertDialog alert = builder.create();
               alert.show();
           }
        });

        fab_login.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                animateFab();
                FragmentManager fragmentManager = getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                LoginFragment loginFragment = new LoginFragment();
                fragmentTransaction.replace(R.id.fragment_container, loginFragment);
                fragmentTransaction.commit();
                mainGone();
            }
        });
    }

    private void animateFab(){
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();

        if(isOpen){
            if(user == null){
                fab_register.startAnimation(rotateForward);
                fab_login.startAnimation(fabClose);
                fab_register.startAnimation(fabClose);
                fab_login.setVisibility(View.INVISIBLE);
                fab_register.setVisibility(View.INVISIBLE);
            }
            else {
                mFirebaseDatabase = FirebaseDatabase.getInstance();
                myRef = mFirebaseDatabase.getReference().child("Profile").child(user.getUid()).child("type");
                myRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot snapshot) {
                        Config.APP_TYPE = snapshot.getValue().toString();
                        fab_menu.startAnimation(rotateForward);
                        fab_sos.startAnimation(fabClose);
                        fab_logout.startAnimation(fabClose);
                        fab_logout.setVisibility(View.INVISIBLE);
                        fab_sos.setVisibility(View.INVISIBLE);
                        if (Config.APP_TYPE.equals("1")){
                            fab_children.setVisibility(View.INVISIBLE);
                            fab_children.startAnimation(fabClose);
                        }
                        else if (Config.APP_TYPE.equals("2")){
                            fab_profile.setVisibility(View.INVISIBLE);
                            fab_profile.startAnimation(fabClose);
                            fab_follow.setVisibility(View.INVISIBLE);
                            fab_follow.startAnimation(fabClose);

                            fab_children.setVisibility(View.INVISIBLE);
                            fab_children.startAnimation(fabClose);
                        }
                        else {
                            fab_admin_map.setVisibility(View.INVISIBLE);
                            fab_admin_map.startAnimation(fabClose);
                            fab_driver_lists.startAnimation(fabClose);
                            fab_driver_lists.setVisibility(View.INVISIBLE);
                            fab_parent_lists.startAnimation(fabClose);
                            fab_parent_lists.setVisibility(View.INVISIBLE);
                            fab_parent_to_driver.startAnimation(fabClose);
                            fab_parent_to_driver.setVisibility(View.INVISIBLE);
                        }

                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                    }
                });
            }
            isOpen = false;
        }
        else{
            if(user == null){
                fab_register.startAnimation(rotateBackward);
                fab_login.startAnimation(fabOpen);
                fab_register.startAnimation(fabOpen);
                fab_login.setVisibility(View.VISIBLE);
                fab_register.setVisibility(View.VISIBLE);
            }
            else {
                mFirebaseDatabase = FirebaseDatabase.getInstance();
                myRef = mFirebaseDatabase.getReference().child("Profile").child(user.getUid()).child("type");
                myRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot snapshot) {
                        Config.APP_TYPE = snapshot.getValue().toString();
                        fab_menu.startAnimation(rotateBackward);

                        fab_sos.startAnimation(fabOpen);
                        fab_logout.startAnimation(fabOpen);
                        fab_logout.setVisibility(View.VISIBLE);
                        fab_sos.setVisibility(View.VISIBLE);
                        if (Config.APP_TYPE.equals("1")){
                            fab_children.setVisibility(View.VISIBLE);
                            fab_children.startAnimation(fabOpen);
                        }

                        else if (Config.APP_TYPE.equals("2")){
                            fab_profile.startAnimation(fabOpen);
                            fab_profile.setVisibility(View.VISIBLE);
                            fab_follow.startAnimation(fabOpen);
                            fab_follow.setVisibility(View.VISIBLE);

                            fab_children.setVisibility(View.VISIBLE);
                            fab_children.startAnimation(fabOpen);
                        }
                        else{
                            fab_admin_map.startAnimation(fabOpen);
                            fab_admin_map.setVisibility(View.VISIBLE);
                            fab_driver_lists.startAnimation(fabOpen);
                            fab_driver_lists.setVisibility(View.VISIBLE);
                            fab_parent_lists.startAnimation(fabOpen);
                            fab_parent_lists.setVisibility(View.VISIBLE);
                            fab_parent_to_driver.startAnimation(fabOpen);
                            fab_parent_to_driver.setVisibility(View.VISIBLE);
                        }




                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                    }
                });

            }
            isOpen = true;
        }
    }

    public void removeFragment(){
        try {
            List<Fragment> fragments = getSupportFragmentManager().getFragments();
            if (fragments != null) {
                for (Fragment fragment : fragments) {
                    getSupportFragmentManager().beginTransaction().remove(fragment).commit();
                }
            }
        }
        catch (NullPointerException e){}
    }

    public void mainGone(){
        fab_menu.setVisibility(View.GONE);
    }

}
