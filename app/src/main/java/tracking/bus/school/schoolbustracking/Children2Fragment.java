package tracking.bus.school.schoolbustracking;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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
import tracking.bus.school.schoolbustracking.Models.Child;


public class Children2Fragment extends Fragment implements PopupMenu.OnMenuItemClickListener {
    View view;
    Button btnBack;
    Button btnAdd;

    ChildAdapter childAdapter;
    ArrayList<Child> children;

    String parentId;

    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference myRef;

    private FirebaseUser user;
    private FirebaseAuth mAuth;
    PopupMenu popup;

    String driverId;
    String childName;
    FloatingActionButton fab_menu;

    public Children2Fragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_children, container, false);
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        parentId = getArguments().getString("parentId");
        fab_menu = (FloatingActionButton) getActivity().findViewById(R.id.fab_menu);

        Firebase.setAndroidContext(getActivity());

        btnBack = (Button) view.findViewById(R.id.btnBack);
        btnAdd = (Button) view.findViewById(R.id.btnAdd);


        mFirebaseDatabase = FirebaseDatabase.getInstance();
        myRef = mFirebaseDatabase.getReference().child("Profile").child(user.getUid()).child("type");
        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                Config.APP_TYPE = snapshot.getValue().toString();
                if(Config.APP_TYPE.equals("3"))
                    btnAdd.setVisibility(View.GONE);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });

        btnBack.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                removeFragment();
                getActivity().findViewById(R.id.fragment_container).setVisibility(View.GONE);
                getActivity().findViewById(R.id.llButtons).setVisibility(View.VISIBLE);
            }
        });

        getChildrenList();
        return view;
    }

    private void getChildrenList(){
        children = new ArrayList<Child>();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        myRef = mFirebaseDatabase.getReference().child("Children");
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {

                children.clear();
                ArrayList<StorageReference> storageReferences = new ArrayList<StorageReference>();
                for (DataSnapshot ds : snapshot.getChildren()) {
                    FirebaseStorage storage = FirebaseStorage.getInstance();
                    StorageReference storageRef = storage.getReference().child("Children").child(ds.child("name").getValue().toString());
                    Child destination = new Child();
                    destination.setName(ds.child("name").getValue().toString());
                    destination.setParent(ds.child("parent").getValue().toString());
                    destination.setStatus(ds.child("status").getValue().toString());
                    destination.setAge(ds.child("age").getValue().toString());
                    destination.setGender(ds.child("gender").getValue().toString());

                    try{
                        destination.setDriver(ds.child("driver").getValue().toString());
                    }
                    catch(NullPointerException e){}

                    storageReferences.add(storageRef);

                    if(parentId.equals(destination.getParent()))
                        children.add(destination);
                }

                childAdapter = new ChildAdapter(children);
                childAdapter.setContext(getContext());
                childAdapter.notifyDataSetChanged();

                childAdapter.setStorageReferences(storageReferences);

                RecyclerView rv = (RecyclerView) view.findViewById(R.id.lvChildren);

                rv.setAdapter(childAdapter);
                LinearLayoutManager llm = new LinearLayoutManager(getActivity());
                llm.setOrientation(LinearLayoutManager.VERTICAL);
                rv.setLayoutManager(llm);

                rv.addOnItemTouchListener(new RecyclerItemListener(getContext(), rv,
                        new RecyclerItemListener.RecyclerTouchListener() {
                            public void onClickItem(View v, int position) {

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
                Intent intentQueue = new Intent(getActivity(), ChildMapsActivity.class);
                intentQueue.putExtra("parentId", parentId);
                getActivity().startActivity(intentQueue);
                return true;
            case R.id.mnuRide:


                destination.setName(childName);
                destination.setParent(parentId);
                destination.setDriver(driverId);
                destination.setStatus("School Bus");

                ref.child("Children").child(childName).setValue(destination);

                Toast.makeText(getActivity(), childName +" is now in school bus.", Toast.LENGTH_LONG).show();

                return true;
            case R.id.mnuDropH:
                destination.setName(childName);
                destination.setParent(parentId);
                destination.setDriver(driverId);
                destination.setStatus("Home");

                ref.child("Children").child(childName).setValue(destination);

                Toast.makeText(getActivity(), childName +" is now at home.", Toast.LENGTH_LONG).show();
                return true;
            case R.id.mnuDropS:
                destination.setName(childName);
                destination.setParent(parentId);
                destination.setDriver(driverId);
                destination.setStatus("School");

                ref.child("Children").child(childName).setValue(destination);

                Toast.makeText(getActivity(), childName +" is now in School.", Toast.LENGTH_LONG).show();
                return true;
            default:
                return false;
        }
    }


}
