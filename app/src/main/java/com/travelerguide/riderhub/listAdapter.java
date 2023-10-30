package com.travelerguide.riderhub;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;

public class listAdapter extends ArrayAdapter<String> {
    Context context;
    ArrayList<String> listnames=new ArrayList<String>(),listdates=new ArrayList<String>(),liststart=new ArrayList<String>(),listend=new ArrayList<String>(),listtime=new ArrayList<String>(),price=new ArrayList<String>();
    ArrayList<Integer> image=new ArrayList<Integer>();

    public listAdapter(Context c, ArrayList<String> names,ArrayList<String> dates,ArrayList<String> liststart,ArrayList<String> listend,ArrayList<String> listtime,ArrayList<String> price,ArrayList<Integer> image) {
        super(c, R.layout.list_item,names);
        this.context = c;
        this.listnames = names;
        this.listdates = dates;
        this.liststart = liststart;
        this.listend = listend;
        this.listtime = listtime;
        this.price = price;
        this.image = image;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        LayoutInflater layoutInflater=LayoutInflater.from(context);
        convertView =layoutInflater.inflate(R.layout.list_item,parent,false);
        ImageView images=convertView.findViewById(R.id.listImage);
        TextView names=convertView.findViewById(R.id.listName);
        TextView dates=convertView.findViewById(R.id.listDate);
        TextView start=convertView.findViewById(R.id.listStart);
        TextView end=convertView.findViewById(R.id.lisetEnd);
        TextView time=convertView.findViewById(R.id.listTime);
        TextView listprice=convertView.findViewById(R.id.listPrice);
        images.setImageResource(image.get(position));
        names.setText(listnames.get(position));
        dates.setText(listdates.get(position));
        start.setText(liststart.get(position));
        end.setText(listend.get(position));
        time.setText(listtime.get(position));
        listprice.setText(price.get(position));
        return convertView;
    }
}
