package com.travelerguide.riderhub;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentContainerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class location extends AppCompatActivity {

    EditText start,end,vehicleNo,stime,date;
    RadioGroup vehicle;
    RadioButton selectedVehicle;
    ImageButton back;
    Button proceed,search;
    TextView name,email;
    FragmentContainerView map;
    GoogleMap gmap;
    float[] distance =new float[10];
    String realdistance;
    FirebaseFirestore fstore=FirebaseFirestore.getInstance();
    String count;
    int realcount;

    String username,useremail,phoneno,userId;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location);
        start=findViewById(R.id.txt_start);
        end=findViewById(R.id.txt_end);
        proceed=findViewById(R.id.btn_proceed);
        search=findViewById(R.id.btn_search);
        back=findViewById(R.id.btn_back);
        name=findViewById(R.id.txt_name);
        email=findViewById(R.id.txt_email);
        map=findViewById(R.id.map);
        vehicleNo=findViewById(R.id.txt_vehicleNo);
        date=findViewById(R.id.txt_date);
        stime=findViewById(R.id.txt_time);
        vehicle=findViewById(R.id.vehicle);
        selectedVehicle=findViewById(R.id.rdb_car);
        selectedVehicle.setChecked(true);

        Intent intent=getIntent();
        username=intent.getStringExtra("username");
        useremail=intent.getStringExtra("useremail");
        phoneno=intent.getStringExtra("userphone");
        userId=intent.getStringExtra("userId");

        name.setText(username);
        email.setText(useremail);

        //readCount();

        proceed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String vehicletype,vehicleno,sdate,time,startlocation,endlocation;
                vehicleno=vehicleNo.getText().toString().trim();
                sdate=date.getText().toString().trim();
                time=stime.getText().toString().trim();
                selectedVehicle=findViewById(vehicle.getCheckedRadioButtonId());
                vehicletype=selectedVehicle.getText().toString().trim();
                startlocation=start.getText().toString().trim();
                endlocation=end.getText().toString().trim();

                AlertDialog.Builder builder = new AlertDialog.Builder(location.this);

                // Set the message show for the Alert time
                builder.setMessage("Do you wish to save this ride ?\n\nStart : "+startlocation+"\nEnd : "+endlocation+"\nTotal Distance : "+realdistance+" Km");

                // Set Alert Title
                builder.setTitle("Save");

                // Set Cancelable false for when the user clicks on the outside the Dialog Box then it will remain show
                builder.setCancelable(false);

                // Set the positive button with yes name Lambda OnClickListener method is use of DialogInterface interface.
                builder.setPositiveButton("yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        DocumentReference documentReference=fstore.collection("usersSaveRides/"+userId+"/details").document();
                        Map<String,Object> user=new HashMap<>();
                        user.put("rideUid",userId);
                        user.put("Name",username);
                        user.put("phoneNo",phoneno);
                        user.put("vehicleno",vehicleno);
                        user.put("date",sdate);
                        user.put("time",time);
                        user.put("vehicletype",vehicletype);
                        user.put("startlocation",startlocation);
                        user.put("endlocation",endlocation);
                        user.put("distance",realdistance);
                        documentReference.set(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                FirebaseFirestore.getInstance().terminate();
                                Toast.makeText(location.this, "Ride saved", Toast.LENGTH_SHORT).show();
                                Intent intent=new Intent(location.this,Home.class);
                                startActivity(intent);
                                finish();
                            }
                        });
                    }
                });

                builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                AlertDialog alertDialog = builder.create();
                alertDialog.show();
            }
        });

        start.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int i, KeyEvent keyEvent) {
                proceed.setVisibility(View.INVISIBLE);
                search.setVisibility(View.VISIBLE);
                return false;
            }
        });

        end.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int i, KeyEvent keyEvent) {
                proceed.setVisibility(View.INVISIBLE);
                search.setVisibility(View.VISIBLE);
                return false;
            }
        });

        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getroute();
            }
        });

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(location.this,Home.class);
                startActivity(intent);
                finish();
            }
        });

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(@NonNull GoogleMap googleMap) {

                gmap = googleMap;
            }
        });
    }

    private void getroute() {
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        if(start.getText().toString().equals("")){
            start.setError("Please enter a valid start city location");
        }
        else if (end.getText().toString().equals("")) {
            end.setError("Please enter a valid end city location");
        }
        else if (vehicleNo.getText().toString().equals("")) {
            vehicleNo.setError("Please enter a valid vehicle number");
        }
        else if (date.getText().toString().equals("")) {
            date.setError("Please enter a valid date");
        }
        else if (stime.getText().toString().equals("")) {
            stime.setError("Please enter a valid time");
        } else{
            String startSearch=start.getText().toString();
            String endSearch=end.getText().toString();
            Geocoder geocoder=new Geocoder(location.this);
            List<Address> startlist=new ArrayList<>();
            List<Address> endlist=new ArrayList<>();
            try {
                startlist=geocoder.getFromLocationName(startSearch,1);
                endlist=geocoder.getFromLocationName(endSearch,1);
            }catch (IOException e){
                Toast.makeText(this, "Failed To get Locations click on Search again", Toast.LENGTH_LONG).show();
            }

            if(startlist.size()>0 && endlist.size()>0){
                Address startaddress=startlist.get(0);
                Address endaddress=endlist.get(0);
                popmap(startaddress,endaddress);
            }
        }

    }


    private void popmap(Address start,Address end) {
        try {
            gmap.clear();
        }catch (Exception e){

        }

        Location.distanceBetween(
                start.getLatitude(),
                start.getLongitude(),
                end.getLatitude(),
                end.getLongitude(),
                distance
        );

        realdistance=String.valueOf(distance[0]/1000);
        polylineadd(start,end);
        moveCamera(new LatLng(start.getLatitude(),start.getLongitude()),10f,start.getAddressLine(0));
        moveCamera(new LatLng(end.getLatitude(),end.getLongitude()),10f,end.getAddressLine(0));
    }

    private void moveCamera(com.google.android.gms.maps.model.LatLng latLng,float zoom,String title){
        try {
            map.setVisibility(View.VISIBLE);
            gmap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng,zoom));
            MarkerOptions options=new MarkerOptions()
                    .position(latLng)
                    .title(title);
            gmap.addMarker(options);
        }catch (Exception e){

        }

    }

    private void polylineadd(Address start,Address end){
        try{
            PolylineOptions polylineOptions=new PolylineOptions()
                    .add(new LatLng(start.getLatitude(),start.getLongitude()))
                    .add(new LatLng(end.getLatitude(),end.getLongitude()));
            Polyline polyline=gmap.addPolyline(polylineOptions);
            polyline.setColor(0xFF0000FF);
            polyline.setWidth(10);

            proceed.setVisibility(View.VISIBLE);
            search.setVisibility(View.INVISIBLE);

        }catch (Exception e){
            Toast.makeText(this, "\"Failed to add the track please press on Search button again", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent=new Intent(location.this,Home.class);
        startActivity(intent);
        finish();
    }

}