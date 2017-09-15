package com.htetznaing.droidxbg;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.Toast;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;

import java.io.File;

public class MainActivity extends AppCompatActivity {
    SeekBar seek;
    Spinner spinner;
    ImageView iv;
    Switch onOff;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    private static final int REQUEST_CODE = 102;
    private static final int Req_Code = 1;
    final int Req = 1;
    AdView B;
    AdRequest adRequest;
    InterstitialAd IAd;
    int check;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        sharedPreferences = getSharedPreferences("myFile",MODE_PRIVATE);
        editor = sharedPreferences.edit();

        onOff = (Switch) findViewById(R.id.onOff);
        iv= (ImageView) findViewById(R.id.iv);
        seek = (SeekBar) findViewById(R.id.seek);

        adRequest = new AdRequest.Builder().build();
        B = (AdView) findViewById(R.id.adView);
        B.loadAd(adRequest);
        IAd = new InterstitialAd(this);
        IAd.setAdUnitId("ca-app-pub-4173348573252986/3376652516");
        IAd.loadAd(adRequest);

        IAd.setAdListener(new AdListener() {
            @Override
            public void onAdClosed() {
                if(!IAd.isLoaded()){
                    IAd.loadAd(adRequest);
                }
            }

            @Override
            public void onAdFailedToLoad(int i) {
                if(!IAd.isLoaded()){
                    IAd.loadAd(adRequest);
                }
            }

            @Override
            public void onAdOpened() {
                if(!IAd.isLoaded()){
                    IAd.loadAd(adRequest);
                }
            }

            @Override
            public void onAdLeftApplication() {
                if(!IAd.isLoaded()){
                    IAd.loadAd(adRequest);
                }
            }
        });

        spinner = (Spinner) findViewById(R.id.spinner);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position!=0){
                    editor.putInt("style",position);
                    editor.commit();
                    if (!onOff.isChecked()){
                        onOff.setChecked(true);
                    }
                    showAd();
                    startLOL();
                }
                checkPermission();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        check = sharedPreferences.getInt("check",0);
        if (check!=0){
            onOff.setChecked(true);
            checkPermission();
            startLOL();
        }else{
            onOff.setChecked(false);
        }

        onOff.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                showAd();
                checkPermission();
                if (isChecked!=false){
                    startService(new Intent(MainActivity.this,Floating.class));
                    editor.putInt("check",1);
                    editor.commit();
                    startLOL();
                }else{
                    stopService(new Intent(MainActivity.this,Floating.class));
                    editor.putInt("check",0);
                    editor.commit();
                }
            }
        });

        seek.setProgress(sharedPreferences.getInt("alpha",30));
        seek.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                editor.putInt("alpha",progress);
                editor.commit();
                if (!onOff.isChecked()){
                    onOff.setChecked(true);
                }
                startLOL();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setType("text/plain");
                intent.putExtra(Intent.EXTRA_TEXT,"You can change your phone background with this app easily!\n Download free at Google Play Store : http://play.google.com/store/apps/details?id=com.htetznaing.droidxbg");
                startActivity(new Intent(Intent.createChooser(intent,"Android Background Changer")));
            }
        });
    }

    public void showAd(){
        if (IAd.isLoaded()){
            IAd.show();
        }else{
            IAd.loadAd(adRequest);
        }
    }

    public void chooseImage(View view){
        checkPermission();
        startActivityForResult(new Intent(Intent.ACTION_PICK,MediaStore.Images.Media.EXTERNAL_CONTENT_URI),Req);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        showAd();
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Req && resultCode == RESULT_OK && null != data) {
            Uri selectedImage = data.getData();
            String[] filePathColumn = { MediaStore.Images.Media.DATA };

            Cursor cursor = getContentResolver().query(selectedImage,
                    filePathColumn, null, null, null);
            cursor.moveToFirst();

            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            String picturePath = cursor.getString(columnIndex);
            cursor.close();
            String file = Environment.getExternalStorageDirectory()+"/.AndXBackground.bg";
            new myFiles().cpr(picturePath,file);
            if (!onOff.isChecked()){
                onOff.setChecked(true);
            }
            startLOL();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    public void startLOL(){
        File file = new File(Environment.getExternalStorageDirectory()+"/.AndXBackground.bg");
        if (file.exists()) {
            stopService(new Intent(MainActivity.this, Floating.class));
            startService(new Intent(MainActivity.this, Floating.class));
        }else{
            onOff.setChecked(false);
            Toast.makeText(this, "Please Choose Image First!", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            startActivity(new Intent(MainActivity.this,About.class));
        }

        return super.onOptionsItemSelected(item);
    }

    public void checkPermission(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!Settings.canDrawOverlays(this)) {
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("IMPORTANT!!");
                builder.setMessage("your need to grant Permission for\nPermit drawing over other apps");
                builder.setPositiveButton("Grant it", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + getPackageName()));
                        startActivityForResult(intent, Req_Code);
                    }
                });
                AlertDialog alertDialog = builder.create();
                alertDialog.show();
            }
        }

        if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_CODE);
        }
    }
}
