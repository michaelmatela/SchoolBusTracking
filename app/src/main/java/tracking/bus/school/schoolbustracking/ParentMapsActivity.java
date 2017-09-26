package tracking.bus.school.schoolbustracking;

import android.content.Intent;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.firebase.client.Firebase;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.text.SimpleDateFormat;

import tracking.bus.school.schoolbustracking.Models.LatitudeLongtitude;
import tracking.bus.school.schoolbustracking.Models.SOS;

public class ParentMapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    Button btnSave;
    Button btnBack;

    private FirebaseUser user;
    private FirebaseAuth mAuth;

    String parentId;

    MarkerOptions marker;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_parent_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        parentId = user.getUid();

        Firebase.setAndroidContext(this);

        btnSave = (Button) findViewById(R.id.btnSave);
        btnBack = (Button) findViewById(R.id.btnBack);

        btnSave.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                updateLocation();
            }
        });

        btnBack.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Intent intent = new Intent(ParentMapsActivity.this, MainActivity.class);
                ParentMapsActivity.this.startActivity(intent);
            }
        });
    }

    private void updateLocation(){



        if (marker != null) {

            Firebase ref = new Firebase(Config.FIREBASE_URL);
            LatitudeLongtitude latitudeLongtitude = new LatitudeLongtitude();
            latitudeLongtitude.setLatitude(String.valueOf(marker.getPosition().latitude));
            latitudeLongtitude.setLongtitude(String.valueOf(marker.getPosition().longitude));
            ref.child("ParentLocation").child(parentId).setValue(latitudeLongtitude);

            Toast.makeText(this, "location successfully updated.", Toast.LENGTH_LONG).show();
        }
        else{
            Toast.makeText(this, "please pick location.", Toast.LENGTH_LONG).show();
        }



    }





    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Sydney and move the camera
        LatLng sydney = new LatLng(14.592743814113977, 120.979442037642);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(sydney,15));

        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {

            @Override
            public void onMapClick(LatLng point) {
                // TODO Auto-generated method stub
                mMap.clear();
                marker = new MarkerOptions().position(point);
                mMap.addMarker(marker);
            }
        });
    }
}
