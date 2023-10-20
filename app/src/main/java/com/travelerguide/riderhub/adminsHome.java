package com.travelerguide.riderhub;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class adminsHome extends AppCompatActivity {

    String useremail, phoneNo, username,userID;
    FirebaseAuth auth = FirebaseAuth.getInstance();
    FirebaseFirestore fstore = FirebaseFirestore.getInstance();
    TextView name, email,totalEarnings;
    private List<String> usersIds =new ArrayList<>();
    private List<String> ridersIds =new ArrayList<>();
    float totalData=0.0f;
    ImageButton logout;
    String count;
    ArrayList<String> rideId,price,riderUid,dates;
    listAdapter listAdapter;
    ListView onging;
    String vehicletype;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admins_home);

        userID = auth.getCurrentUser().getUid();
        logout = findViewById(R.id.btn_back);
        name = findViewById(R.id.txt_name);
        email = findViewById(R.id.txt_email);
        totalEarnings=findViewById(R.id.txt_totalEarnings);
        onging=findViewById(R.id.ongoingRides);

        try {
            DocumentReference documentReference = fstore.collection("users").document(userID);
            documentReference.addSnapshotListener(this, new EventListener<DocumentSnapshot>() {

                @Override
                public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                    username = value.getString("Name");
                    useremail = value.getString("email");
                    phoneNo = value.getString("phoneNo");

                    name.setText(username);
                    email.setText(useremail);
                }
            });
        } catch (Exception e) {
            Toast.makeText(this, e.toString(), Toast.LENGTH_SHORT).show();
        }

        getusersIds();
        getridersIds();


        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                logout();
            }
        });
    }

    private void getusersIds(){
        try{
            fstore.collection("riderEarnings").addSnapshotListener(new EventListener<QuerySnapshot>() {
                @Override
                public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                    for(QueryDocumentSnapshot documentSnapshot:value){
                        String documentID=documentSnapshot.getId();
                        usersIds.add(documentID);
                    }
                    calculateTotal(totalEarnings);
                }
            });
        }catch (Exception e){
            Toast.makeText(this, "Something went wrong", Toast.LENGTH_SHORT).show();
        }
    }

    private void logout() {
        AlertDialog.Builder builder = new AlertDialog.Builder(adminsHome.this);

        // Set the message show for the Alert time
        builder.setMessage("Do you want to logout ?");

        // Set Alert Title
        builder.setTitle("Log Out !");

        // Set Cancelable false for when the user clicks on the outside the Dialog Box then it will remain show
        builder.setCancelable(false);

        // Set the positive button with yes name Lambda OnClickListener method is use of DialogInterface interface.
        builder.setPositiveButton("yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                FirebaseFirestore.getInstance().terminate();
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(adminsHome.this, Login.class);
                startActivity(intent);
                finish();
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

    private void calculateTotal(TextView totalEarnings){
        for (int x=0;x<usersIds.size();x++){
            try {
                DocumentReference documentReference = fstore.collection("riderEarnings").document(usersIds.get(x));
                documentReference.addSnapshotListener(this, new EventListener<DocumentSnapshot>() {

                    @Override
                    public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                        String temp= value.getString("ammount")==null?"0":value.getString("ammount");
                        totalData += Float.valueOf(temp);
                        totalEarnings.setText("Total Earnings :- "+String.valueOf(totalData)+" LKR");
                    }
                });
            } catch (Exception e) {
                Toast.makeText(this, e.toString(), Toast.LENGTH_SHORT).show();
            }

        }
    }

    private void getridersIds(){
        try{
            fstore.collection("usersSaveRides").addSnapshotListener(new EventListener<QuerySnapshot>() {
                @Override
                public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                    for(QueryDocumentSnapshot documentSnapshot:value){
                        String documentID=documentSnapshot.getId();
                        ridersIds.add(documentID);
                    }
                    loadrideData();
                }
            });
        }catch (Exception e){
            Toast.makeText(this, "Something went wrong", Toast.LENGTH_SHORT).show();
        }
    }

    private void loadrideData(){

        ArrayList<String> names=new ArrayList<String>(),liststart=new ArrayList<String>(),listend=new ArrayList<String>(),listtime=new ArrayList<String>();
        price=new ArrayList<String>();
        rideId=new ArrayList<String>();
        riderUid=new ArrayList<String>();
        dates=new ArrayList<String>();
        ArrayList<Integer> image=new ArrayList<Integer>();

        for (int x = 0; x < ridersIds.size(); x++) {
            try {
                DocumentReference documentReference = fstore.collection("usersSaveRides").document(ridersIds.get(x));
                int finalX = x;
                documentReference.addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
                    @Override
                    public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                        count = value.getString("count");
                        for (int y = 1; y <= Integer.parseInt(count); y++) {
                            try {
                                fstore.collection("usersSaveRides/"+ridersIds.get(finalX)+"/details").document(String.valueOf(y)).addSnapshotListener(new EventListener<DocumentSnapshot>() {
                                    @Override
                                    public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                                        rideId.add(value.getString("rideid"));
                                        riderUid.add(value.getString("rideUid"));
                                        names.add(value.getString("Name"));
                                        dates.add(value.getString("date"));
                                        liststart.add(value.getString("startlocation"));
                                        listend.add(value.getString("endlocation"));
                                        listtime.add(value.getString("time"));
                                        vehicletype=value.getString("vehicletype");
                                        price.add(value.getString("vehicleno"));
                                        if (vehicletype.equals("Motor Bike")) {
                                            image.add(R.drawable.scooter);
                                        } else if (vehicletype.equals("Car")) {
                                            image.add(R.drawable.car);
                                        } else if (vehicletype.equals("Three Wheeler")) {
                                            image.add(R.drawable.threewheel);
                                        }
                                        setAdapter(names, dates, liststart, listend, listtime, price, image);
                                    }
                                });
                            }catch (Exception e){
                                Log.e("riderSearch2",e.toString());
                                Toast.makeText(adminsHome.this, "Something went wrong", Toast.LENGTH_SHORT).show();
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
        listAdapter = new listAdapter(adminsHome.this, names, dates, liststart, listend, listtime, price, image);
        onging.setAdapter(listAdapter);
    }
}