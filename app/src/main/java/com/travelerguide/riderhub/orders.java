package com.travelerguide.riderhub;

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

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class orders extends AppCompatActivity {

    String username,useremail,phoneno,userId;
    TextView name,email;
    ImageButton back;
    FirebaseFirestore fstore = FirebaseFirestore.getInstance();
    ListView ridelist;
    listAdapter listAdapter;
    ArrayList<String> price=new ArrayList<String>();
    ArrayList<String> totalCountprice=new ArrayList<String>();
    ArrayList<String> pasengerID=new ArrayList<String>();
    float total = 0.0f;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_orders);
        name=findViewById(R.id.txt_name);
        email=findViewById(R.id.txt_email);
        back=findViewById(R.id.btn_back);
        ridelist=findViewById(R.id.orderlist);

        Intent intent=getIntent();
        username=intent.getStringExtra("username");
        useremail=intent.getStringExtra("useremail");
        phoneno=intent.getStringExtra("userphone");
        userId=intent.getStringExtra("userId");
        name.setText(username);
        email.setText(useremail);

        loadData();

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(orders.this,Home.class);
                startActivity(intent);
                finish();
            }
        });

        ridelist.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                AlertDialog.Builder builder = new AlertDialog.Builder(orders.this);
                // Set the message show for the Alert time
                builder.setMessage("Did you got the payment ?");
                // Set Alert Title
                builder.setTitle("Payment verify");
                // Set Cancelable false for when the user clicks on the outside the Dialog Box then it will remain show
                builder.setCancelable(false);
                // Set the positive button with yes name Lambda OnClickListener method is use of DialogInterface interface.
                builder.setPositiveButton("yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        delete(i);
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

    }

    @Override
    public void onBackPressed() {
        Intent intent=new Intent(orders.this,Home.class);
        startActivity(intent);
        finish();
        super.onBackPressed();
    }

    private void loadData(){
        ArrayList<String> names=new ArrayList<String>(),dates=new ArrayList<String>(),liststart=new ArrayList<String>(),listend=new ArrayList<String>(),listtime=new ArrayList<String>();
        ArrayList<Integer> image=new ArrayList<Integer>();
        try {
            DocumentReference documentReference = fstore.collection("userReserveRides").document(userId);
            documentReference.addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
                @Override
                public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                    try {
                        names.add(value.getString("Name"));
                        dates.add(value.getString("date"));
                        liststart.add("Trip No ");
                        listend.add(value.getString("rideId"));
                        listtime.add(value.getString("phoneNo"));
                        pasengerID.add(value.getString("userid"));
                        totalCountprice.add(value.getString("price"));
                        price.add(value.getString("price")+" LKR");
                        if(value.getString("vehicletype").equals("Motor Bike")){
                            image.add(R.drawable.scooter);
                        } else if (value.getString("vehicletype").equals("Car")) {
                            image.add(R.drawable.car);
                        } else if (value.getString("vehicletype").equals("Three Wheeler")) {
                            image.add(R.drawable.threewheel);
                        }
                        setAdapter(names,dates,liststart,listend,listtime,price,image);
                    }catch (Exception e){

                    }
                }
            });
        } catch (Exception e) {
        }
    }

    private void setAdapter(ArrayList<String> names,ArrayList<String> dates,ArrayList<String> liststart,ArrayList<String> listend,ArrayList<String> listtime,ArrayList<String> price,ArrayList<Integer> image) {
        listAdapter = new listAdapter(orders.this, names, dates, liststart, listend, listtime, price, image);
        ridelist.setAdapter(listAdapter);
        ridelist.setVisibility(View.VISIBLE);
    }

    private void delete(int index) {
        DocumentReference documentReference = fstore.collection("userReservation").document(pasengerID.get(index));
        documentReference.delete();
        documentReference = fstore.collection("userReserveRides").document(userId);
        documentReference.delete();

        CollectionReference collectionReference =fstore.collection("riderEarnings");
        Map<String,Object> user=new HashMap<>();
        user.put("ammount",totalCountprice.get(index));
        collectionReference.add(user).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
            @Override
            public void onSuccess(DocumentReference documentReference) {
                Intent intent=new Intent(orders.this,orders.class);
                intent.putExtra("username", username);
                intent.putExtra("useremail", useremail);
                intent.putExtra("userphone", phoneno);
                intent.putExtra("userId", userId);
                Toast.makeText(orders.this, "Thank You", Toast.LENGTH_SHORT).show();
                startActivity(intent);
                finish();
            }
        });

    }

}