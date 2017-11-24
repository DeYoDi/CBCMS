package com.dyd.cbcms;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private int Accuracy;
    private static final int HIGH_ACCURACY = 15;
    private static final int MEDIUM_ACCURACY = 20;
    private static final int LOW_ACCURACY = 30;
    private static final int PERMISSIONS = 555;
    private static final int MAP = 666;
    private static final int PHOTO_SELECT_GALLERY = 111;
    private static final int PHOTO_SELECT_CAMERA = 222;
    private Uri imagepath;
    LinearLayout layout;
    String mCurrentPhotoPath;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        try{
            // Get Preferences
            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
            // Default language is english
            String Language = preferences.getString(getResources().getString(R.string.Locale_preference),"en");
            // Default accuracy is HIGH
            Accuracy = preferences.getInt(getResources().getString(R.string.Accuracy_preference),HIGH_ACCURACY);

            // Setting the app language
            Locale locale = new Locale(Language);
            setLocale(locale);
        }catch (Exception ex)
        {
            Toast.makeText(getApplicationContext(),ex.toString(),Toast.LENGTH_LONG).show();
        }

        // Set content Layout after setting the language
        setContentView(R.layout.activity_main);

        // After setting the Layout
         ActivityCompat.requestPermissions(this,new String[]{
                 Manifest.permission.ACCESS_COARSE_LOCATION,
                 Manifest.permission.ACCESS_FINE_LOCATION,
                 Manifest.permission.READ_EXTERNAL_STORAGE,
                 Manifest.permission.WRITE_EXTERNAL_STORAGE,
                 Manifest.permission.CAMERA},PERMISSIONS);// Dummy code

        layout = (LinearLayout) findViewById(R.id.linear);

        Button imgView = (Button) findViewById(R.id.btnPhotos);
        Button LocationBtn = (Button) findViewById(R.id.btnLocation);
        Button CameraBtn = (Button) findViewById(R.id.btnSubmit);

        imgView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                GalleryCameraDialog();
            }
        });


        LocationBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent mapIntent = new Intent(MainActivity.this,MapsActivity.class);
                mapIntent.putExtra("Accuracy", Accuracy);
                startActivityForResult(mapIntent,MAP);
            }
            });

    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        TextView txtLoc = (TextView) findViewById(R.id.txtLocation);
        TextView txtAddress = (TextView) findViewById(R.id.txtAddress);

        if (requestCode == PHOTO_SELECT_CAMERA && resultCode == RESULT_OK) {

            if(data!=null)
            {

                imagepath = Uri.parse(data.getStringExtra("Path"))  ;
                for (int i = 0; i < 1; i++) {
                    final ImageView imageView = new ImageView(this);
                    imageView.setId(i);
                    imageView.setPadding(1, 8, 1, 8);
                    imageView.setImageURI(imagepath);
                    imageView.setScaleType(ImageView.ScaleType.FIT_XY);
                    imageView.setMaxHeight(250);
                    imageView.setMaxWidth(250);
                    imageView.setAdjustViewBounds(true);

                    layout.addView(imageView);

                    imageView.setOnLongClickListener(new View.OnLongClickListener() {
                        @Override
                        public boolean onLongClick(View view) {
                            layout.removeView(imageView);
                            return true;
                        }
                    });
                }
            }


        }

        if(requestCode == PHOTO_SELECT_GALLERY&&resultCode==RESULT_OK)
        {
            if(data!=null)
            {
                imagepath = data.getData();
                for (int i = 0; i < 1; i++) {
                    final ImageView imageView = new ImageView(this);
                    imageView.setId(i);
                    imageView.setPadding(1, 8, 1, 8);
                    imageView.setImageURI(imagepath);
                    imageView.setScaleType(ImageView.ScaleType.FIT_XY);
                    imageView.setMaxHeight(250);
                    imageView.setMaxWidth(250);
                    imageView.setAdjustViewBounds(true);

                    layout.addView(imageView);

                    imageView.setOnLongClickListener(new View.OnLongClickListener() {
                        @Override
                        public boolean onLongClick(View view) {
                            layout.removeView(imageView);
                            return true;
                        }
                    });
                }
            }
        }

        if (requestCode == MAP && resultCode == RESULT_OK ) {
            if(data != null) {
                // get String data from Intent
                Double Latitude = data.getDoubleExtra("Latitude", 13.0500);
                Double Longitude = data.getDoubleExtra("Longitude", 80.2824);
                Double Accuracy = data.getDoubleExtra("Accuracy", 20);
                txtLoc.setText(Latitude + " " + Longitude);


                try{
                    Geocoder geocoder;
                    List<Address> addresses;
                    geocoder = new Geocoder(this, Locale.getDefault());
                    addresses = geocoder.getFromLocation(Latitude, Longitude, 1);
                    txtAddress.setText(addresses.get(0).getAddressLine(0));

                }catch (Exception ex)
                {
                    txtAddress.setText("Address Not Available");
                }

            }else
            {
                txtLoc.setText("Something went wrong");

            }

        }
    }

    @SuppressWarnings("deprecation")
    private void setLocale(Locale locale){
        Resources resources = getResources();
        Configuration configuration = resources.getConfiguration();
        DisplayMetrics displayMetrics = resources.getDisplayMetrics();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1){
            configuration.setLocale(locale);
            getApplicationContext().createConfigurationContext(configuration);
        }
        else{
            configuration.locale=locale;
            resources.updateConfiguration(configuration,displayMetrics);
        }
        getBaseContext().getResources().updateConfiguration(configuration,
                getBaseContext().getResources().getDisplayMetrics());
    }

    public void GalleryCameraDialog() {
        final CharSequence[] options = { "Take Photo", "Choose from Gallery","Cancel" };

        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle("Add Photo!");
        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                if (options[item].equals("Take Photo"))
                {
                    Intent intent = new Intent(MainActivity.this,CameraActivty.class);
                    startActivityForResult(intent,PHOTO_SELECT_CAMERA);

                }
                else if (options[item].equals("Choose from Gallery"))
                {
                    Intent intent = new Intent();
                    intent.setType("image/*");
                    intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                    intent.setAction(Intent.ACTION_GET_CONTENT);//
                    startActivityForResult(Intent.createChooser(intent, "Select File"),PHOTO_SELECT_GALLERY);

                }
                else if (options[item].equals("Cancel")) {
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }



}
