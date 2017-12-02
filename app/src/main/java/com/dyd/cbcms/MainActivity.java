package com.dyd.cbcms;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.database.Cursor;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;


public class MainActivity extends AppCompatActivity {

    private int Accuracy;
    private static final int HIGH_ACCURACY = 15;
    private static final int MEDIUM_ACCURACY = 20;
    private static final int LOW_ACCURACY = 30;
    private static final int PERMISSIONS = 555;
    private static final int MAP = 666;
    private static final int REQUEST_FINE_LOCATION = 777;
    private static final int PHOTO_SELECT_GALLERY = 111;
    private static final int PHOTO_SELECT_CAMERA = 222;
    private TextView txtLoc;
    private TextView txtAddress;
    private Double Latitude;
    private Double Longitude;
    private Uri imagepath;
    private String imagepath_str;
    private String State;
    private String District;
    private String Violation;
    private String Address;
    private String Received_Accuracy;
    private LinearLayout layout;
    private int imageId =1;
    private HashMap<String,String> imagemap;
    private ArrayList<String> pathString;
    private long PhotosSize=0;
    private int count=0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        String Language="null";
        try{
                // Get Preferences
                SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
                // Default language is english
                Language = preferences.getString(getResources().getString(R.string.Locale_preference),"en");
                // Default accuracy is HIGH
                Accuracy = Integer.valueOf(preferences.getString(getResources().getString(R.string.Accuracy_preference),"10"));

                // Setting the app language
                Locale locale = new Locale(Language);
                setLocale(locale);
            }
        catch (Exception ex)
            {
                Toast.makeText(getApplicationContext(),ex.toString(),Toast.LENGTH_LONG).show();
                ex.printStackTrace();
            }
      //  Toast.makeText(getApplicationContext(),Language + ","+Accuracy,Toast.LENGTH_LONG).show();
        // Set content Layout after setting the language
        setContentView(R.layout.activity_main);

        imagemap  = new HashMap<String, String>();

        // After setting the Layout
         ActivityCompat.requestPermissions(this,new String[]{
                 Manifest.permission.ACCESS_COARSE_LOCATION,
                 Manifest.permission.ACCESS_FINE_LOCATION,
                 Manifest.permission.READ_EXTERNAL_STORAGE,
                 Manifest.permission.WRITE_EXTERNAL_STORAGE,
                 Manifest.permission.CAMERA},PERMISSIONS);// Dummy code

        layout = (LinearLayout) findViewById(R.id.linear);

        final Button photoBtn = (Button) findViewById(R.id.btnPhotos);
        final Button LocationBtn = (Button) findViewById(R.id.btnLocation);
        final Button submitBtn = (Button) findViewById(R.id.btnSubmit);
        final Spinner spinner = (Spinner) findViewById(R.id.spinner);

        txtLoc     = (TextView) findViewById(R.id.txtLocation);
        txtAddress = (TextView) findViewById(R.id.txtAddress);
        pathString = new ArrayList<>();

        submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Animation myAnim = AnimationUtils.loadAnimation(MainActivity.this, R.anim.bounce);
                submitBtn.startAnimation(myAnim);
                Violation = spinner.getSelectedItem().toString();
              SendEmail();
              //  ShareActivity share = new ShareActivity();
              //  share.send(MainActivity.this);
            }
        });


        photoBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                for(String pa : pathString)
                {
                    if (pa != null)
                    {
                        File fileIn = new File(pa);
                        PhotosSize = fileIn.length();
                        PhotosSize = PhotosSize/(1024*1024);
                    }
                }
//                    if(PhotosSize<(long)20)
                   if(layout.getChildCount()<(long)3)
                    {
                        // GalleryCameraDialog();
                        Intent intent = new Intent(MainActivity.this,CameraActivty.class);
                        startActivityForResult(intent,PHOTO_SELECT_CAMERA);
                    }
                    else
                    {
                        Toast.makeText(getApplicationContext(),getResources().getText(R.string.Memory_full_Photo),Toast.LENGTH_LONG).show();
                    }

            }
        });


        LocationBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if ((ActivityCompat.checkSelfPermission(MainActivity.this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED)&&
                        (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION)== PackageManager.PERMISSION_GRANTED))
                {
                    Intent mapIntent = new Intent(MainActivity.this,MapsActivity.class);
                    mapIntent.putExtra("Accuracy", Accuracy);
                    startActivityForResult(mapIntent,MAP);

                }
                else
                {
                    ActivityCompat.requestPermissions(MainActivity.this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                            REQUEST_FINE_LOCATION);
                }

            }
            });

    }

    protected void SendEmail(){

        String subject = "Complaint from " + " , " + State + " , " + District;
        StringBuilder Body = new StringBuilder();

        Intent intent = new Intent(Intent.ACTION_SEND_MULTIPLE);

        Body.append("Complaint Details:");

        Body.append("\n");
        Body.append("State              :" + State);
        Body.append("\n");
        Body.append("District:" + District);
        Body.append("\n");
        Body.append("Violation\t\t:" + Violation);
        Body.append("\n");
        Body.append("Location\t\t:" + "https://www.google.com/maps/?q="+Latitude+","+Longitude);
        Body.append("\n");
        Body.append("Accuracy in meters\t\t:" + Received_Accuracy);
        Body.append("\n");
        Body.append("Address Line\t\t: "+Address );

        ArrayList<Uri> uris = new ArrayList<>();

        uris.clear();

        for(String pa : pathString)
        {
            if (pa != null)
            {
                File fileIn = new File(pa);
                Uri u = FileProvider.getUriForFile(this, "com.example.android.fileprovider", fileIn);
                uris.add(u);
            }
        }

        if (uris.size()>0)
        {
            intent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, uris);
        }
        intent.putExtra(Intent.EXTRA_EMAIL, new String[]{"thecoastalresourcecentre@gmail.com"});
        intent.putExtra(Intent.EXTRA_SUBJECT, subject);
        intent.putExtra(Intent.EXTRA_TEXT, Body.toString());
        intent.setType("message/rfc822");
        intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION | Intent.FLAG_ACTIVITY_NEW_TASK);

        try{startActivityForResult(intent,777);}catch (Exception ex){Toast.makeText(getApplicationContext(),ex.toString(),Toast.LENGTH_LONG).show();}


    }

    /**
     * helper to retrieve the path of an image URI
     */
    public String getPath(Uri uri) {
        // just some safety built in
        if (uri == null) {
            // TODO perform some logging or show user feedback
            return null;
        }
        // try to retrieve the image from the media store first
        // this will only work for images selected from gallery
        String[] projection = {MediaStore.Images.Media.DATA};

        Cursor cursor = getContentResolver().query(uri, projection, null, null, null);
        if (cursor != null) {
            int column_index = cursor
                    .getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            String path = cursor.getString(column_index);
            cursor.close();
            return path;
        }
        // this is our fallback here
        return uri.getPath();
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.action_settings:
            Intent settings = new Intent(MainActivity.this,PreferencesActivity.class);
                try{
                startActivity(settings);

                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }

                return true;
            case R.id.action_policy:
              //  Intent policy = new Intent(MainActivity.this,PreferencesActivity.class);
                try{
                  //  startActivity(policy);
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }

                return true;
            case R.id.action_exit:
                System.exit(0);

                return true;
            case R.id.reset:

                clearContent();

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void clearContent()
    {
        txtAddress.setText("");
        txtAddress.setEnabled(true);
        txtLoc.setText("");
        txtLoc.setEnabled(true);
        if(layout.getChildCount()>0)
        {
            layout.removeAllViews();
        }

    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PHOTO_SELECT_CAMERA && resultCode == RESULT_OK) {

            if(data!=null)
            {
                imagepath = Uri.parse(data.getStringExtra("Path"))  ;
                imagepath_str = data.getStringExtra("Path");
                for (int i = 0; i < 1; i++) {
                    final ImageView imageView = new ImageView(this);
                    imageView.setId(imageId);
                    imageView.setPadding(1, 8, 1, 8);
                    imageView.setImageURI(imagepath);
                    imageView.setScaleType(ImageView.ScaleType.FIT_XY);
                    imageView.setMaxHeight(250);
                    imageView.setMaxWidth(250);
                    imageView.setAdjustViewBounds(true);
                    layout.addView(imageView);
                    imageId++;
                    imagemap.put(String.valueOf(imageId),imagepath_str);
                    pathString.add(imagepath_str);
                    layout.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if(layout.getChildCount() > 0)
                            {
                                DeletePictures();

                                //if(DeletePictures())
                                //{
                                //    layout.removeAllViews();
                                //    pathString.clear();
                                //    PhotosSize = 0;
                                //
                                //    Toast.makeText(getApplicationContext(),
                                //            getResources().getString(R.string.Photos_delete_success),
                                //            Toast.LENGTH_SHORT).show();
                                //}
                                //else
                                //{
                                //    Toast.makeText(getApplicationContext(),
                                //            getResources().getString(R.string.Photos_delete_failed),
                                //            Toast.LENGTH_SHORT).show();
                                //}

                            }

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
                imagepath_str = data.getData().toString();
                for (int i = 0; i < 1; i++) {
                    final ImageView imageView = new ImageView(this);
                    imageView.setId(imageId);
                    imageView.setPadding(1, 8, 1, 8);
                    imageView.setImageURI(imagepath);
                    imageView.setScaleType(ImageView.ScaleType.FIT_XY);
                    imageView.setMaxHeight(250);
                    imageView.setMaxWidth(250);
                    imageView.setAdjustViewBounds(true);

                    layout.addView(imageView);
                    imageId++;
                    imagemap.put(String.valueOf(imageId),imagepath_str);
                //    pathString.add(getRealPathFromURI(imagepath,this));

                    Uri selectedImageUri = data.getData();
                 //   imagepath = ;
                //    File imageFile = new File(imagepath);

                   // pathString.add(getPath(selectedImageUri));
                    pathString.add(selectedImageUri.toString());


                    layout.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if(layout.getChildCount() > 0)
                            {
                                layout.removeAllViews();
                                pathString.clear();
                            }
                        }
                    });
                }
            }
        }

        if (requestCode == MAP && resultCode == RESULT_OK ) {
            if(data != null) {
                // get String data from Intent
                Latitude     = data.getDoubleExtra("Latitude", 13.0500);
                Longitude    = data.getDoubleExtra("Longitude", 80.2824);
                Received_Accuracy = data.getStringExtra("Accuracy_Received");

                txtLoc.setText(Latitude + " " + Longitude);
                txtLoc.setEnabled(false);

                try{
                    Geocoder geocoder;
                    List<Address> addresses;
                    geocoder = new Geocoder(this, Locale.getDefault());
                    addresses = geocoder.getFromLocation(Latitude, Longitude, 1);
                    txtAddress.setText(addresses.get(0).getAddressLine(0));
                    Address =addresses.get(0).getAddressLine(0);
                    State = addresses.get(0).getAdminArea();
                    District = addresses.get(0).getLocality();
                    txtAddress.setEnabled(false);

                }catch (Exception ex)
                {
                    ex.printStackTrace();
                    txtAddress.setText("Address Not Available");
                    txtAddress.setEnabled(true);
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

    public void DeletePictures()
    {
        final CharSequence[] options = { getResources().getString(R.string.Ok), getResources().getString(R.string.Cancel)};
        count =0;
        if(layout.getChildCount()>0)
        {
            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
            builder.setTitle(getResources().getString(R.string.Photos_delete));
            builder.setItems(options, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int item) {
                    if (options[item].equals(getResources().getString(R.string.Ok)))
                    {
                        for(String pa : pathString)
                        {
                            if (pa != null)
                            {
                                File file= new File(pa);
                                file.delete();
                                if(!file.exists())
                                {
                                    count++;
                                }

                            }
                        }
                        if(count == pathString.size())
                        {
                            layout.removeAllViews();
                            pathString.clear();
                            PhotosSize = 0;

                            Toast.makeText(getApplicationContext(),
                                    getResources().getString(R.string.Photos_delete_success),
                                    Toast.LENGTH_SHORT).show();
                        }
                        else
                        {
                            Toast.makeText(getApplicationContext(),
                                    getResources().getString(R.string.Photos_delete_failed),
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                    else if (options[item].equals(getResources().getString(R.string.Cancel)))
                    {
                        //Do Nothing

                    }
                }
            });
            builder.show();
        }

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
                    intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, false);
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
