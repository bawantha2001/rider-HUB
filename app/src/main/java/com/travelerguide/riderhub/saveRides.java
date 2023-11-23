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

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

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
    ArrayList<String> price,dates,rideid=new ArrayList<String>();
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

        ridelist.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                AlertDialog.Builder builder = new AlertDialog.Builder(saveRides.this);
                // Set the message show for the Alert time
                builder.setMessage("Do you want to delete this ride ?");

                // Set Alert Title
                builder.setTitle("Delete Ride");

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
                        try {
                            fstore.collection("usersSaveRides/"+userId+"/details").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                    if (task.isSuccessful()) {
                                        for (QueryDocumentSnapshot value : task.getResult()) {
                                            try {
                                                names.add(value.getString("vehicleno") + "/ " + value.getString("seatCount") + " Seats Available");
                                                dates.add(value.getString("date"));
                                                liststart.add(value.getString("startlocation"));
                                                listend.add(value.getString("endlocation"));
                                                listtime.add(value.getString("time"));
                                                vehicletype.add(value.getString("vehicletype"));
                                                price.add("Trip ID:- " + value.getString("rideid"));
                                                rideid.add(value.getString("rideid"));
                                                if (value.getString("vehicletype").equals("Motor Bike")) {
                                                    image.add(R.drawable.scooter);
                                                } else if (value.getString("vehicletype").equals("Car")) {
                                                    image.add(R.drawable.car);
                                                } else if (value.getString("vehicletype").equals("Three Wheeler")) {
                                                    image.add(R.drawable.threewheel);
                                                }
                                                setAdapter(names, dates, liststart, listend, listtime, price, image);
                                            } catch (Exception e) {
                                                Log.e("riderSearch2", e.toString());
                                            }
                                        }
                                    }
                                }
                            });

                        }catch (Exception e){
                            Log.e("riderSearch2",e.toString());
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

    private void delete(int index) {
        DocumentReference documentReference = fstore.collection("usersSaveRides/"+userId+"/details").document(rideid.get(index));
        documentReference.delete();
        Intent intent=new Intent(saveRides.this,Home.class);
        startActivity(intent);
        finish();
        Toast.makeText(this, "Ride deleted", Toast.LENGTH_SHORT).show();
    }
}