package com.travelerguide.riderhub;/
import androidx.annotation.NonNull;/
import androidx.annotation.Nullable;/
import androidx.appcompat.app.AlertDialog;/
import androidx.appcompat.app.AppCompatActivity;/
import androidx.core.app.ActivityCompat;/
import androidx.fragment.app.FragmentContainerView;/
/
import android.content.DialogInterface;/
import android.content.Intent;/
import android.content.pm.PackageManager;/
import android.location.Location;/
import android.location.LocationManager;/
import android.os.Bundle;/
import android.view.View;/
import android.widget.Button;/
import android.widget.ImageButton;/
import android.widget.TextView;/
import android.widget.Toast;/
/
import com.google.android.gms.maps.CameraUpdateFactory;/
import com.google.android.gms.maps.GoogleMap;/
import com.google.android.gms.maps.OnMapReadyCallback;/
import com.google.android.gms.maps.SupportMapFragment;/
import com.google.firebase.auth.FirebaseAuth;/
import com.google.firebase.firestore.DocumentReference;/
import com.google.firebase.firestore.DocumentSnapshot;/
import com.google.firebase.firestore.EventListener;/
import com.google.firebase.firestore.FirebaseFirestore;/
import com.google.firebase.firestore.FirebaseFirestoreException;/
/
public class Home extends AppCompatActivity {/
/
/
    FirebaseAuth auth = FirebaseAuth.getInstance();/
    FirebaseFirestore fstore = FirebaseFirestore.getInstance();/
    ImageButton logout;/
    TextView name, email;/
    Button destination,saveRide,reserve,orders,rides;/
    GoogleMap gmap;/
    String useremail, phoneNo, username,userID;/
    LocationManager locationManager;/
    android.location.LocationListener locationListener;/
    boolean isGPSEnabled;/
    FragmentContainerView map;/
/
    @Override/
    protected void onCreate(Bundle savedInstanceState) {/
        super.onCreate(savedInstanceState);/
        setContentView(R.layout.activity_home);/
        logout = findViewById(R.id.btn_back);/
        userID = auth.getCurrentUser().getUid();/
        name = findViewById(R.id.txt_name);/
        email = findViewById(R.id.txt_email);/
        destination = findViewById(R.id.btn_home);/
        saveRide=findViewById(R.id.btn_Rides);/
        reserve=findViewById(R.id.btn_reserves);/
        map=findViewById(R.id.map);/
        orders=findViewById(R.id.btn_orders);/
        rides=findViewById(R.id.btn_savedRides);/
/
        getDeviceLocation();/
/
        try {/
            /DocumentReference documentReference = fstore.collection("users").document(userID);
           / documentReference.addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
/

                @Override/

               / public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                    username = value.getString("Name");/
                    useremail = value.getString("email");/
                    phoneNo = value.getString("phoneNo");/
                    name.setText(username);/
                    email.setText(useremail);/
                }/
            });/
        } catch (Exception e) {/
            Toast.makeText(this, e.toString(), Toast.LENGTH_SHORT).show();/
        }/
/
        logout.setOnClickListener(new View.OnClickListener() {/
            @Override/
            public void onClick(View v) {/
                logout();/
            }/
        });/
/
        saveRide.setOnClickListener(new View.OnClickListener() {/
            @Override/
            public void onClick(View v) {/

                Intent destination = new Intent(Home.this, location.class);/
                destination.putExtra("username", username);/
                destination.putExtra("useremail", useremail);/
                destination.putExtra("userphone", phoneNo);/
                destination.putExtra("userId", userID);/
                startActivity(destination);/
                finish();/
            }/
        });/
        destination.setOnClickListener(new View.OnClickListener() {/
            @Override/
            public void onClick(View view) {/
                Intent destination = new Intent(Home.this, whereTo.class);/
                destination.putExtra("username", username);/
                destination.putExtra("useremail", useremail);/
                destination.putExtra("userphone", phoneNo);/
                destination.putExtra("userId", userID);/
                startActivity(destination);/
                finish();/
            }/
        });/
        reserve.setOnClickListener(new View.OnClickListener() {/
            @Override/
            public void onClick(View view) {/
                Intent intent=new Intent(Home.this, reserves.class);/
                intent.putExtra("username", username);/
                intent.putExtra("useremail", useremail);/
                intent.putExtra("userphone", phoneNo);/
                intent.putExtra("userId", userID);/
                startActivity(intent);/
                finish();/
            }/
        });/
        orders.setOnClickListener(new View.OnClickListener() {/
            @Override/
            public void onClick(View view) {/
                Intent intent=new Intent(Home.this, orders.class);/
                intent.putExtra("username", username);/
                intent.putExtra("useremail", useremail);/
                intent.putExtra("userphone", phoneNo);/
                intent.putExtra("userId", userID);/
                startActivity(intent);/
                finish();/
            }/
        });/
        rides.setOnClickListener(new View.OnClickListener() {/
            @Override/
            public void onClick(View view) {/
                Intent intent=new Intent(Home.this, saveRides.class);/
                intent.putExtra("username", username);/
                intent.putExtra("useremail", useremail);/
                intent.putExtra("userphone", phoneNo);/
                intent.putExtra("userId", userID);/
                startActivity(intent);/
                finish();/
            }/
        });/
    }/

    private void getDeviceLocation() {/
        /
       / SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(new OnMapReadyCallback() {/
            @Override/
            public void onMapReady(@NonNull GoogleMap googleMap) {/
/
                gmap = googleMap;/
            }/
        });/
/
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);/
        locationListener = new android.location.LocationListener() {/
            @Override/
/
            public void onLocationChanged(@NonNull Location location) {/
                try {/
                   / moveCamera(new com.google.android.gms.maps.model.LatLng(location.getLatitude(), location.getLongitude()), 15f);
                   / if (ActivityCompat.checkSelfPermission(Home.this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(Home.this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                   /     Toast.makeText(Home.this, "Permission denied", Toast.LENGTH_SHORT).show();
                        return;/
                    }/
                    gmap.setMyLocationEnabled(true);/
                    gmap.setBuildingsEnabled(true);/
                    gmap.setTrafficEnabled(true);/
                    gmap.setIndoorEnabled(true);/
                    map.setVisibility(View.VISIBLE);/
                    locationManager.removeUpdates(locationListener);/
                }catch (Exception e){/
                    /Toast.makeText(Home.this, "Loading your location please wait...", Toast.LENGTH_LONG).show();
               / }
           / }
/
          /  @Override

           / public void onProviderEnabled(@NonNull String provider) {
             /   Toast.makeText(Home.this, "Location service enabled", Toast.LENGTH_SHORT).show();
           / }
/
           / @Override
           / public void onProviderDisabled(@NonNull String provider) {
         /       Toast.makeText(Home.this, "\"Location service disabled", Toast.LENGTH_SHORT).show();
         /   }
      /  };
      /
      /  try {
      /      locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,
       /             1000,(float)0.01,locationListener);
      /  } catch (SecurityException e) {
        /    e.printStackTrace();
      / }
/
    /}
/
   / private void moveCamera(com.google.android.gms.maps.model.LatLng latLng,float zoom){
    /        gmap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng,zoom));
  /  }
/
   / @Override
   / public void onBackPressed() {
   /     super.onBackPressed();
   / }
/
   / private void logout(){
    /    AlertDialog.Builder builder = new AlertDialog.Builder(Home.this);
/
    /    // Set the message show for the Alert time
     /   builder.setMessage("Do you want to logout ?");
     /   // Set Alert Title
      /  builder.setTitle("Log Out !");
     /   // Set Cancelable false for when the user clicks on the outside the Dialog Box then it will remain show
     /   builder.setCancelable(false);
      /  // Set the positive button with yes name Lambda OnClickListener method is use of DialogInterface interface.
     /   builder.setPositiveButton("yes", new DialogInterface.OnClickListener() {
     /       @Override
     /       public void onClick(DialogInterface dialog, int which) {
      /          FirebaseFirestore.getInstance().terminate();
      /          FirebaseAuth.getInstance().signOut();
      /          Intent intent=new Intent(Home.this,Login.class);
       /         startActivity(intent);
        /        finish();
       /     }
       / });
/
       / builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
       /     @Override
       /     public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
      /  });
       / AlertDialog alertDialog = builder.create();
       / alertDialog.show();
   / }
/}