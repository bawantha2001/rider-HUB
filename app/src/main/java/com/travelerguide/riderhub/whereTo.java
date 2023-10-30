package com.travelerguide.riderhub;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class whereTo extends AppCompatActivity {


    ImageButton back;
    RadioGroup vehicle;
    RadioButton selected_vehicle;
    EditText start,end;
    Button search;
    TextView name,email;
    String username,useremail,phoneno,userId,startcity,endcity,vehicletype;
    FirebaseFirestore fstore = FirebaseFirestore.getInstance();
    private List<String> documentIds =new ArrayList<>();
    String count;
    ListView list;
    listAdapter listAdapter;
    ArrayList<String> rideId,price,riderUid,dates;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_where_to);

        name=findViewById(R.id.txt_name);
        email=findViewById(R.id.txt_email);
        back=findViewById(R.id.btn_back);
        vehicle=findViewById(R.id.vehicle);
        start=findViewById(R.id.txt_start);
        end=findViewById(R.id.txt_end);
        search=findViewById(R.id.btn_search);
        selected_vehicle=findViewById(R.id.rdb_car);
        selected_vehicle.setChecked(true);
        list=findViewById(R.id.orderlist);

        Intent intent=getIntent();
        username=intent.getStringExtra("username");
        useremail=intent.getStringExtra("useremail");
        phoneno=intent.getStringExtra("userphone");
        userId=intent.getStringExtra("userId");

        name.setText(username);
        email.setText(useremail);
        retriveRiders();

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(whereTo.this,Home.class);
                startActivity(intent);
                finish();
            }
        });

        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                list.setVisibility(View.INVISIBLE);
                startcity=start.getText().toString().trim();
                endcity=end.getText().toString().trim();
                selected_vehicle=findViewById(vehicle.getCheckedRadioButtonId());
                vehicletype=selected_vehicle.getText().toString();

                if(startcity.equals("")){
                    start.setError("Please enter a valid start location");
                    return;
                }
                if(endcity.equals("")){
                    end.setError("Please enter a valid end location");
                    return;
                }
                try {
                    listAdapter.clear();
                    getdistance(startcity,endcity);
                }catch (Exception e){
                    getdistance(startcity,endcity);
                }

            }
        });

        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                reserveride(i);
            }
        });
    }

    private void searchRiders(float distance) {

        ArrayList<String> names=new ArrayList<String>(),liststart=new ArrayList<String>(),listend=new ArrayList<String>(),listtime=new ArrayList<String>();
        price=new ArrayList<String>();
        rideId=new ArrayList<String>();
        riderUid=new ArrayList<String>();
        dates=new ArrayList<String>();
        ArrayList<Integer> image=new ArrayList<Integer>();

        for (int x = 0; x < documentIds.size(); x++) {
            try {
                DocumentReference documentReference = fstore.collection("usersSaveRides").document(documentIds.get(x));
                int finalX = x;
                documentReference.addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
                    @Override
                    public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                        count = value.getString("count");
                        for (int y = 1; y <= Integer.parseInt(count); y++) {
                            try {
                                fstore.collection("usersSaveRides/"+documentIds.get(finalX)+"/details").document(String.valueOf(y)).addSnapshotListener(new EventListener<DocumentSnapshot>() {
                                    @Override
                                    public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                                        if(value.getString("startlocation").equals(startcity) && value.getString("endlocation").equals(endcity) && value.getString("vehicletype").equals(vehicletype)&&!documentIds.get(finalX).equals(userId)){
                                            rideId.add(value.getString("rideid"));
                                            riderUid.add(value.getString("rideUid"));
                                            names.add(value.getString("Name"));
                                            dates.add(value.getString("date"));
                                            liststart.add(value.getString("startlocation"));
                                            listend.add(value.getString("endlocation"));
                                            listtime.add(value.getString("time"));
                                            if(vehicletype.equals("Motor Bike")){
                                                price.add(String.valueOf(500*distance));
                                                image.add(R.drawable.scooter);
                                            } else if (vehicletype.equals("Car")) {
                                                price.add(String.valueOf(1500*distance));
                                                image.add(R.drawable.car);
                                            } else if (vehicletype.equals("Three Wheeler")) {
                                                price.add(String.valueOf(900*distance));
                                                image.add(R.drawable.threewheel);
                                            }

                                            setAdapter(names,dates,liststart,listend,listtime,price,image);
                                        }
                                    }
                                });
                            }catch (Exception e){
                                Log.e("riderSearch2",e.toString());
                                Toast.makeText(whereTo.this, "Something went wrong", Toast.LENGTH_SHORT).show();
                                return;
                            }
                        }
                    }
                });
            } catch (Exception e) {
                Log.e("riderSearch",e.toString());
                Toast.makeText(this, "Something went wrong", Toast.LENGTH_SHORT).show();
                return;
            }
        }
    }

    private void setAdapter(ArrayList<String> names,ArrayList<String> dates,ArrayList<String> liststart,ArrayList<String> listend,ArrayList<String> listtime,ArrayList<String> price,ArrayList<Integer> image) {
        listAdapter = new listAdapter(whereTo.this, names, dates, liststart, listend, listtime, price, image);
        list.setAdapter(listAdapter);
        list.setVisibility(View.VISIBLE);
    }
    private void retriveRiders(){
        try{
            fstore.collection("usersSaveRides").addSnapshotListener(new EventListener<QuerySnapshot>() {
                @Override
                public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                    for(QueryDocumentSnapshot documentSnapshot:value){
                        String documentID=documentSnapshot.getId();
                        documentIds.add(documentID);
                    }
                }
            });
        }catch (Exception e){
            Toast.makeText(this, "Something went wrong", Toast.LENGTH_SHORT).show();
            return;
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent=new Intent(whereTo.this,Home.class);
        startActivity(intent);
        finish();
    }

    public void getdistance(String startSearch,String endSearch){
        String realdistance;
        float[] distance =new float[10];
        Geocoder geocoder=new Geocoder(whereTo.this);
        List<Address> startlist=new ArrayList<>();
        List<Address> endlist=new ArrayList<>();
        try {
            startlist=geocoder.getFromLocationName(startSearch,1);
            endlist=geocoder.getFromLocationName(endSearch,1);
        }catch (IOException e){
            Toast.makeText(this, "Failed To get Locations Please re enter", Toast.LENGTH_SHORT).show();
        }
        if(startlist.size()>0 && endlist.size()>0){
            Address startaddress=startlist.get(0);
            Address endaddress=endlist.get(0);
            Location.distanceBetween(
                    startaddress.getLatitude(),
                    startaddress.getLongitude(),
                    endaddress.getLatitude(),
                    endaddress.getLongitude(),
                    distance
            );
            realdistance=String.valueOf(distance[0]/1000);
            searchRiders(Float.parseFloat(realdistance));
        }
    }

    private void reserveride(int index){
        AlertDialog.Builder builder = new AlertDialog.Builder(whereTo.this);

        // Set the message show for the Alert time
        builder.setMessage("Do you wish to join this ride ?\n\nPayment Method :- Cash");

        // Set Alert Title
        builder.setTitle("Reserve Ride");

        // Set Cancelable false for when the user clicks on the outside the Dialog Box then it will remain show
        builder.setCancelable(false);

        // Set the positive button with yes name Lambda OnClickListener method is use of DialogInterface interface.
        builder.setPositiveButton("yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                DocumentReference documentReference=fstore.collection("userReserveRides").document(riderUid.get(index));
                Map<String,Object> user=new HashMap<>();
                user.put("Name",username);
                user.put("phoneNo",phoneno);
                user.put("rideId",rideId.get(index));
                user.put("price",price.get(index));
                user.put("date",dates.get(index));
                user.put("vehicletype",vehicletype);
                user.put("userid",userId);
                documentReference.set(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {

                        saveridereservation(index);
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

    private void saveridereservation(int index){
        DocumentReference documentReference=fstore.collection("userReservation").document(userId);
        Map<String,Object> user=new HashMap<>();
        user.put("userid",riderUid.get(index));
        user.put("rideId",rideId.get(index));
        user.put("price",price.get(index));
        documentReference.set(user).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                Toast.makeText(whereTo.this, "Reserved", Toast.LENGTH_SHORT).show();
                Intent intent=new Intent(whereTo.this,Home.class);
                startActivity(intent);
                finish();
            }
        });
    }
}