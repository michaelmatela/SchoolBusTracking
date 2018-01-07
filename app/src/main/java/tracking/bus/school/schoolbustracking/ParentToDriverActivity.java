package tracking.bus.school.schoolbustracking;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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

import tracking.bus.school.schoolbustracking.Adapter.ParentAdapter;
import tracking.bus.school.schoolbustracking.Models.Child;
import tracking.bus.school.schoolbustracking.Models.Profile;

public class ParentToDriverActivity extends AppCompatActivity implements PopupMenu.OnMenuItemClickListener {
    Button btnBack;

    ParentAdapter parentAdapter;
    ArrayList<Profile> parents;

    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference myRef;

    private FirebaseUser user;
    private FirebaseAuth mAuth;
    PopupMenu popup;

    String parentId;
    String count;

    FloatingActionButton fab_menu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_parent_to_driver);
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        fab_menu = (FloatingActionButton) findViewById(R.id.fab_menu);

        Firebase.setAndroidContext(ParentToDriverActivity.this);

        btnBack = (Button) findViewById(R.id.btnBack);


        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ParentToDriverActivity.this, MainActivity.class);
                ParentToDriverActivity.this.startActivity(intent);
            }
        });

        getChildrenList();
    }

    private void getChildrenList(){
        parents = new ArrayList<Profile>();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        myRef = mFirebaseDatabase.getReference().child("Profile");
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {

                parents.clear();
                ArrayList<StorageReference> storageReferences = new ArrayList<StorageReference>();
                for (DataSnapshot ds : snapshot.getChildren()) {
                    FirebaseStorage storage = FirebaseStorage.getInstance();
                    Profile driver = new Profile();
                    if (ds.child("type").getValue().toString().equals("2")) {
                        driver.setFullName(ds.child("fullName").getValue().toString());
                        driver.setEmail(ds.child("email").getValue().toString());
                        driver.setId(ds.getKey().toString());
                        driver.setPassword(ds.child("email").getValue().toString());
                        driver.setType(ds.child("type").getValue().toString());
                        try{
                            driver.setAssignee(ds.child("assignee").getValue().toString());
                        }
                        catch (NullPointerException e){
                            driver.setAssignee("Not assigned.");
                        }

                        try {
                            driver.setNumber_of_child(ds.child("number_of_child").getValue().toString());
                        } catch (NullPointerException e) {
                            driver.setNumber_of_child("0");
                        }
                        parents.add(driver);
                    }
                }

                parentAdapter = new ParentAdapter(parents);
                parentAdapter.setContext(ParentToDriverActivity.this);
                parentAdapter.notifyDataSetChanged();

                parentAdapter.setStorageReferences(storageReferences);

                RecyclerView rv = (RecyclerView) findViewById(R.id.lvParent);

                rv.setAdapter(parentAdapter);
                LinearLayoutManager llm = new LinearLayoutManager(ParentToDriverActivity.this);
                llm.setOrientation(LinearLayoutManager.VERTICAL);
                rv.setLayoutManager(llm);

                rv.addOnItemTouchListener(new RecyclerItemListener(ParentToDriverActivity.this, rv,
                        new RecyclerItemListener.RecyclerTouchListener() {
                            public void onClickItem(View v, int position) {
                                Profile parent = new Profile();
                                parent = parents.get(position);

                                if (Integer.parseInt(parent.getNumber_of_child()) <= 0){
                                    Toast.makeText(ParentToDriverActivity.this, "Please set number of child for this parent.", Toast.LENGTH_LONG).show();
                                }
                                else{
                                    parentId = parent.getId();
                                    count = parent.getNumber_of_child();
                                    popup = new PopupMenu(ParentToDriverActivity.this, v);
                                    MenuInflater inflater2 = popup.getMenuInflater();
                                    inflater2.inflate(R.menu.driver_list_menu, popup.getMenu());

                                    popup.setOnMenuItemClickListener(ParentToDriverActivity.this);
                                    popup.show();
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

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        Firebase ref = new Firebase(Config.FIREBASE_URL);
        Child destination = new Child();
        switch (item.getItemId()) {
            case R.id.mnuChildren:
                Bundle bundle1 = new Bundle();

                bundle1.putString("parentId", parentId );

                FragmentManager fragmentManager1 = getSupportFragmentManager();
                FragmentTransaction fragmentTransaction1 = fragmentManager1.beginTransaction();
                Children2Fragment children2Fragment = new Children2Fragment();
                children2Fragment.setArguments(bundle1);
                fragmentTransaction1.replace(R.id.fragment_container, children2Fragment);
                fragmentTransaction1.commit();

                findViewById(R.id.fragment_container).setVisibility(View.VISIBLE);
                findViewById(R.id.llButtons).setVisibility(View.GONE);
                return true;
            case R.id.mnuDriver:
                Bundle bundle = new Bundle();

                bundle.putString("parentId", parentId );
                bundle.putString("count",count);

                FragmentManager fragmentManager = getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                DriverList2Fragment driverListFragment = new DriverList2Fragment();
                driverListFragment.setArguments(bundle);
                fragmentTransaction.replace(R.id.fragment_container, driverListFragment);
                fragmentTransaction.commit();

                findViewById(R.id.fragment_container).setVisibility(View.VISIBLE);
                findViewById(R.id.llButtons).setVisibility(View.GONE);
                return true;

            default:
                return false;
        }
    }



}
