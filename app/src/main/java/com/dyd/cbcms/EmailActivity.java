package com.dyd.cbcms;

import android.app.Activity;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.content.FileProvider;
import android.text.Html;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Created by A_KIRA on 11/25/2017.
 */

public class EmailActivity extends MainActivity {

    private List<Address> addresses;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent_receiver = getIntent();
        Double Latitude = intent_receiver.getDoubleExtra("Latitude", 13.0);
        Double Longitude = intent_receiver.getDoubleExtra("Longitude", 80.0);
        String Address = intent_receiver.getStringExtra("Address");
        String Accuracy = intent_receiver.getStringExtra("Accuracy");
        String Violation = intent_receiver.getStringExtra("Violation");
        HashMap<String, Uri> hashMap = (HashMap<String, Uri>)intent_receiver.getSerializableExtra("Images");

        Geocoder myLocation = new Geocoder(getApplicationContext(), Locale.getDefault());

        try{addresses = myLocation.getFromLocation(Latitude, Longitude, 1);}
        catch (Exception ex){
            Toast.makeText(getApplicationContext(),ex.toString(),Toast.LENGTH_LONG).show();
        }

        String State = addresses.get(0).getAdminArea();
        String District = addresses.get(0).getLocality();

        String subject = "Complaint from " + " , " + State + " , " + District;
        StringBuilder Body = new StringBuilder();

        Intent intent = new Intent(Intent.ACTION_SEND);

        Body.append("Complaint Number 1");
        Body.append("\n");
        Body.append("State :" + State);
        Body.append("\n");
        Body.append("District :" + District);
        Body.append("\n");
        Body.append("Violation :" + Violation);
        Body.append("\n");
        Body.append("Location :" + Html.fromHtml("<a href='https://www.google.com/maps/?q="+Latitude+","+Longitude+"'>"+ Latitude +","+Longitude +"</a>"));
        Body.append("\n");
        Body.append("Accuracy in meters :" + Accuracy);
        Body.append("\n");
        Body.append("Address Line : "+Address );

        ArrayList<Uri> uris = new ArrayList<>();
        // For each hashmap, iterate over it
        for (Map.Entry<String, Uri> entry  : hashMap.entrySet())
        {
            File fileIn = new File(entry.getValue().toString());
            Uri u = FileProvider.getUriForFile(this, "com.example.android.fileprovider", fileIn);
            uris.add(u);
        }

        intent.putExtra(Intent.EXTRA_STREAM, uris);
        String uriText = "mailto:" + Uri.encode("krrvarma@gmail.com") +
                "?subject=" + Uri.encode(subject) +
                "&body=" + Uri.encode(Body.toString());
        Uri uri = Uri.parse(uriText);

        intent.putExtra(Intent.EXTRA_EMAIL, new String[]{"thecoastalresourcecentre@gmail.com"});


        intent.putExtra(Intent.EXTRA_SUBJECT, "CBCMS Complaint");
        intent.putExtra(Intent.EXTRA_TEXT, Body.toString());
        intent.setData(uri);
        intent.setType("message/rfc822");
        intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION | Intent.FLAG_ACTIVITY_NEW_TASK);

        startActivity(intent);
    }


}
