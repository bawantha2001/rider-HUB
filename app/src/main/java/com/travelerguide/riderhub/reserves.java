package com.travelerguide.riderhub;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import java.util.ArrayList;

public class  reserves extends AppCompatActivity {

    String username,useremail,phoneno,userId,rideruId,rideId;
    TextView name,email;
    ImageButton back;
    FirebaseFirestore fstore = FirebaseFirestore.getInstance();
    ListView ridelist;
    listAdapter listAdapter;
    ArrayList<String> price=new ArrayList<String>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reserves);

        name=findViewById(R.id.txt_name);
        email=findViewById(R.id.txt_email);
        back=findViewById(R.id.btn_back);
        ridelist=findViewById(R.id.riderlist);

        Intent intent=getIntent();
        username=intent.getStringExtra("username");
        useremail=intent.getStringExtra("useremail");
        phoneno=intent.getStringExtra("userphone");
        userId=intent.getStringExtra("userId");

        name.setText(username);
        email.setText(useremail);

        getreservations();

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(reserves.this,Home.class);
                startActivity(intent);
                finish();
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent=new Intent(reserves.this,Home.class);
        startActivity(intent);
        finish();
    }

    private void getreservations(){
        try {
            DocumentReference documentReference = fstore.collection("userReservation").document(userId);
            documentReference.addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
                @Override/
                public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                    rideruId = value.getString("userid");
                    rideId = value.getString("rideId");
                    price.add(value.getString("price")+" LKR");
                    loadData();
                }
            });
        } catch (Exception e) {

        }
    }

    private void loadData(){
        ArrayList<String> names=new ArrayList<String>(),dates=new ArrayList<String>(),liststart=new ArrayList<String>(),listend=new ArrayList<String>(),listtime=new ArrayList<String>();
        ArrayList<Integer> image=new ArrayList<Integer>();
        try {
            DocumentReference documentReference = fstore.collection("usersSaveRides/"+rideruId+"/details").document(rideId);
            documentReference.addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
                @Override
                public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                    names.add(value.getString("Name"));
                    dates.add(value.getString("date"));
                    liststart.add(value.getString("startlocation"));
                    listend.add(value.getString("endlocation"));
                    listtime.add(value.getString("time"));
                    if(value.getString("vehicletype").equals("Motor Bike")){
                        image.add(R.drawable.scooter);
                    } else if (value.getString("vehicletype").equals("Car")) {
                        image.add(R.drawable.car);
                    } else if (value.getString("vehicletype").equals("Three Wheeler")) {
                        image.add(R.drawable.threewheel);
                    }
                    setAdapter(names,dates,liststart,listend,listtime,price,image);
                }
            });
        } catch (Exception e) {

        }
    }

    private void setAdapter(ArrayList<String> names,ArrayList<String> dates,ArrayList<String> liststart,ArrayList<String> listend,ArrayList<String> listtime,ArrayList<String> price,ArrayList<Integer> image) {
        listAdapter = new listAdapter(reserves.this, names, dates, liststart, listend, listtime, price, image);
        ridelist.setAdapter(listAdapter);
        ridelist.setVisibility(View.VISIBLE);
    }
}