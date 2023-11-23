package com.travelerguide.riderhub;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class saveRides extends AppCompatActivity {


    String username,useremail,phoneno,userId;
    TextView name,email;
    ImageButton back;
    FirebaseFirestore fstore = FirebaseFirestore.getInstance();
    ListView ridelist;
    listAdapter listAdapter;
    ArrayList<String> price,dates;
    String count;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_save_rides);

        name=findViewById(R.id.txt_name);
        email=findViewById(R.id.txt_email);
        back=findViewById(R.id.btn_back);
        ridelist=findViewById(R.id.saverideslist);

        Intent intent=getIntent();
        username=intent.getStringExtra("username");
        useremail=intent.getStringExtra("useremail");
        phoneno=intent.getStringExtra("userphone");
        userId=intent.getStringExtra("userId");

        name.setText(username);
        email.setText(useremail);

        fetchall();

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(saveRides.this,Home.class);
                startActivity(intent);
                finish();
            }
        });

    }

    public void onBackPressed() {
        Intent intent=new Intent(saveRides.this,Home.class);
        startActivity(intent);
        finish();
        super.onBackPressed();
    }

    private void fetchall(){
        ArrayList<String> names=new ArrayList<String>(),liststart=new ArrayList<String>(),listend=new ArrayList<String>(),listtime=new ArrayList<String>(),vehicletype=new ArrayList<String>();
        dates=new ArrayList<String>();
        price=new ArrayList<String>();
        ArrayList<Integer> image=new ArrayList<Integer>();

        try {
            DocumentReference documentReference = fstore.collection("usersSaveRides").document(userId);
            documentReference.addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
                @Override
                public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                    count = value.getString("count");
                    for (int y = 1; y <= Integer.parseInt(count); y++) {
                        try {
                            int finalY = y;
                            fstore.collection("usersSaveRides/"+userId+"/details").document(String.valueOf(y)).addSnapshotListener(new EventListener<DocumentSnapshot>() {
                                @Override
                                public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                                    try {
                                        names.add(value.getString("vehicleno")+"/ "+value.getString("seatCount")+" Seats");
                                        dates.add(value.getString("date"));
                                        liststart.add(value.getString("startlocation"));
                                        listend.add(value.getString("endlocation"));
                                        listtime.add(value.getString("time"));
                                        vehicletype.add(value.getString("vehicletype"));
                                        price.add("Trip No:- "+value.getString("rideid"));
                                        if(vehicletype.get(finalY-1).equals("Motor Bike")){
                                            image.add(R.drawable.scooter);
                                        } else if (vehicletype.get(finalY-1).equals("Car")) {
                                            image.add(R.drawable.car);
                                        } else if (vehicletype.get(finalY-1).equals("Three Wheeler")) {
                                            image.add(R.drawable.threewheel);
                                        }
                                        setAdapter(names,dates,liststart,listend,listtime,price,image);
                                    }catch (Exception e){
                                        Log.e("riderSearch2",e.toString());
                                    }
                                }
                            });
                        }catch (Exception e){
                            Log.e("riderSearch2",e.toString());
                            return;
                        }
                    }
                }
            });
        }catch (Exception e){

        }

    }

    private void setAdapter(ArrayList<String> names,ArrayList<String> dates,ArrayList<String> liststart,ArrayList<String> listend,ArrayList<String> listtime,ArrayList<String> price,ArrayList<Integer> image) {
        listAdapter = new listAdapter(saveRides.this, names, dates, liststart, listend, listtime, price, image);
        ridelist.setAdapter(listAdapter);
        ridelist.setVisibility(View.VISIBLE);
    }
}