package tracking.bus.school.schoolbustracking;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import tracking.bus.school.schoolbustracking.Adapter.DriverAdapter;
import tracking.bus.school.schoolbustracking.Models.Profile;
import tracking.bus.school.schoolbustracking.Models.SOS;

public class UnauthorizedDriversActivity extends AppCompatActivity {

    Profile profile;
    DriverAdapter driverAdapter;
    ArrayList<Profile> drivers;

    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference myRef;

    private FirebaseUser user;
    private FirebaseAuth mAuth;
    Button btnBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_unauthorized_drivers);
        btnBack = (Button) findViewById(R.id.btnBack);
        Firebase.setAndroidContext(UnauthorizedDriversActivity.this);

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(UnauthorizedDriversActivity.this, MainActivity.class);
                UnauthorizedDriversActivity.this.startActivity(intent);
            }
        });

        getChildrenList();

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
                        driver.setPhoneNumber(ds.child("phoneNumber").getValue().toString());
                        driver.setArea(ds.child("area").getValue().toString());
                        driver.setStatus(ds.child("status").getValue().toString());

                        try {
                            driver.setCapacity(ds.child("capacity").getValue().toString());
                            driver.setCurrent(ds.child("current").getValue().toString());
                        } catch (NullPointerException e) {
                            driver.setCapacity("0");
                            driver.setCurrent("0");
                        }
                        if (driver.getStatus().equals("0"))
                            drivers.add(driver);
                    }


                }

                driverAdapter = new DriverAdapter(drivers);
                driverAdapter.setContext(UnauthorizedDriversActivity.this);
                driverAdapter.notifyDataSetChanged();

                driverAdapter.setStorageReferences(storageReferences);

                RecyclerView rv = (RecyclerView) findViewById(R.id.lvDriver);

                rv.setAdapter(driverAdapter);
                LinearLayoutManager llm = new LinearLayoutManager(UnauthorizedDriversActivity.this);
                llm.setOrientation(LinearLayoutManager.VERTICAL);
                rv.setLayoutManager(llm);

                rv.addOnItemTouchListener(new RecyclerItemListener(UnauthorizedDriversActivity.this, rv,
                        new RecyclerItemListener.RecyclerTouchListener() {
                            public void onClickItem(View v, int position) {
                                profile = new Profile();
                                profile = drivers.get(position);
                                DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        switch (which){
                                            case DialogInterface.BUTTON_POSITIVE:

                                                Firebase ref = new Firebase(Config.FIREBASE_URL);
                                                profile.setStatus("1");
                                                ref.child("Profile").child(profile.getId()).setValue(profile);
                                                Toast.makeText(UnauthorizedDriversActivity.this, "Driver can now be set to guardian.", Toast.LENGTH_LONG).show();


                                                break;

                                            case DialogInterface.BUTTON_NEGATIVE:
                                                //No button clicked
                                                break;
                                        }
                                    }
                                };

                                AlertDialog.Builder builder = new AlertDialog.Builder(UnauthorizedDriversActivity.this);
                                builder.setMessage("Are you sure you want to authorize this driver to be a school service driver?").setPositiveButton("Yes", dialogClickListener)
                                        .setNegativeButton("No", dialogClickListener).show();
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
}
