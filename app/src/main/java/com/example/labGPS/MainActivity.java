package com.example.labGPS;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;
import androidx.room.Room;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;

import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.labGPS.Database.Memory;
import com.example.labGPS.Database.MemoryDatabase;
import com.example.labGPS.R;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.Date;
import java.util.List;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback {

    GoogleMap mGoogleMap;
    SupportMapFragment mapFrag;
    LocationRequest mLocationRequest;
    Location mLastLocation;
    Marker mCurrLocationMarker;
    FusedLocationProviderClient mFusedLocationClient; //Основная точка входа для взаимодействия с провайдером локации.

    public static MemoryDatabase memoryDatabase;

    private String CHANNEL_ID = "labGPS";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this); //определяем FusedLocationProviderClient

        mapFrag = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFrag.getMapAsync(this); //Компонент карты в приложении, размещаем карту

//////////////////////////

        FloatingActionButton addMemory = findViewById(R.id.addMemory);

        addMemory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(MainActivity.this, Add.class);

                intent.putExtra("latitude", mLastLocation.getLatitude()); //Получите широту, в градусах.
                intent.putExtra("longitude", mLastLocation.getLongitude()); //Получите долготу в градусах.

                startActivity(intent);

            }
        });

        FloatingActionButton showMemories = findViewById(R.id.showMemories);

        showMemories.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, Show.class);
                startActivity(intent);
            }
        });
    }
//Используется для получения уведомлений
// FusedLocationProviderApi когда местоположение устройства
// изменилось или больше не может быть определено
    LocationCallback mLocationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(LocationResult locationResult) {
            List<Location> locationList = locationResult.getLocations();
            if (locationList.size() > 0) {
                //Последнее место в списке новое
                Location location = locationList.get(locationList.size() - 1);
                Log.i("MapsActivity", "Location: " + location.getLatitude() + " " + location.getLongitude());
                mLastLocation = location;
                if (mCurrLocationMarker != null) {
                    mCurrLocationMarker.remove();
                }

                //Разместить маркер текущего местоположения

                LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
                MarkerOptions markerOptions = new MarkerOptions();
                markerOptions.position(latLng);
                markerOptions.title("Current Position");
                markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA));
                mCurrLocationMarker = mGoogleMap.addMarker(markerOptions);

                //переместить камеру карты

                memoryDatabase = Room.databaseBuilder(getApplicationContext(),
                        MemoryDatabase.class, "memorydb").allowMainThreadQueries().build(); //Отключает проверку запроса основного потока для Room.

                ////

                List<Memory> memories = MainActivity.memoryDatabase.memoryDao().getAll();

                for (Memory memory : memories) {

                    //
                    LatLng latLngo = new LatLng(memory.getLatitude(), memory.getLongitude());
                    markerOptions = new MarkerOptions();
                    markerOptions.position(latLngo);
                    markerOptions.title(memory.getTitle() + "    Memory's time : " + memory.getTime());
                    //markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA));
                    markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_marker));
                    mCurrLocationMarker = mGoogleMap.addMarker(markerOptions);
                    mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLngo, 11));

                    if (Math.ceil(latLngo.latitude) == Math.ceil(latLng.latitude)) {

                        // Создать интент для действия, которое нужно начать
                        Intent resultIntent = new Intent(MainActivity.this, Show.class);
                        // Создать TaskStackBuilder и добавить интент, который добавляется в стек (переходы по кнопке назад)
                        TaskStackBuilder stackBuilder = TaskStackBuilder.create(MainActivity.this);
                        stackBuilder.addNextIntentWithParentStack(resultIntent);
                        // Получить PendingIntent, содержащий весь стек (для других приложений)

                        Intent intent;

                        intent = new Intent(getApplicationContext(), Display.class);
                        intent.putExtra("Title", memory.getTitle());
                        intent.putExtra("Description", memory.getDescription());
                        intent.putExtra("Time", memory.getTime());
                        intent.putExtra("Image", memory.getImage());

                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                        PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), 0, intent, PendingIntent.FLAG_ONE_SHOT);

                        Uri defaultSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

                        NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext())
                                .setSmallIcon(R.drawable.ic_marker)
                                .setLargeIcon(BitmapFactory.decodeResource(getApplicationContext().getResources(),
                                        R.mipmap.ic_launcher))
                                .setContentTitle("You have a memory here")
                                .setContentText("Have a look to bring memories back")
                                .setAutoCancel(true)
                                .setSound(defaultSound)
                                .setContentIntent(pendingIntent);

                        NotificationManager noti = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

                        int m = (int) ((new Date().getTime() / 1000L) % Integer.MAX_VALUE);

                        assert noti != null;
                        noti.notify(m, builder.build());
                    }
                }
                mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 11)); //зум камеры
            }
        }
    };


    @Override
    public void onPause() {
        super.onPause();

        //остановить обновление местоположения, когда активность больше не активна
        if (mFusedLocationClient != null) {
            mFusedLocationClient.removeLocationUpdates(mLocationCallback);
        }
    }

    //Интерфейс обратного вызова, когда карта готова к использованию.
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mGoogleMap = googleMap;
        mGoogleMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);

        mLocationRequest = new LocationRequest(); //запроса качества обслуживания для обновлений местоположения от FusedLocationProviderApi.
        mLocationRequest.setInterval(120000); // интервал две мин
        mLocationRequest.setFastestInterval(120000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);

        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {
                //Location Permission уже предоставлен
                mFusedLocationClient.requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.myLooper());
                mGoogleMap.setMyLocationEnabled(true);
            } else {
                //Запрос разрешения местоположения
                checkLocationPermission();
            }
        } else {
            mFusedLocationClient.requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.myLooper());
            mGoogleMap.setMyLocationEnabled(true);
        }
    }

    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;

    private void checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            // Следует ли показывать обоснование для разрешения?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {

                // Показать объяснение пользователю * асинхронно * - не блокировать
                // этот поток ждет ответа от пользователя
                // после того, как пользователь увидит объяснение, попробовать еще раз запросить разрешение.
                new AlertDialog.Builder(this)
                        .setTitle("Location Permission Needed")
                        .setMessage("This app needs the Location permission, please accept to use location functionality")
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                //Prompt the user once explanation has been shown
                                ActivityCompat.requestPermissions(MainActivity.this,
                                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                                        MY_PERMISSIONS_REQUEST_LOCATION);
                            }
                        })
                        .create()
                        .show();


            } else {
                // Никаких объяснений не требуется, мы можем запросить разрешение.
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_LOCATION: {
                // Если запрос отменен, результирующие массивы пусты.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // разрешение было предоставлено
                    // выполняется задача с местоположением
                    if (ContextCompat.checkSelfPermission(this,
                            Manifest.permission.ACCESS_FINE_LOCATION)
                            == PackageManager.PERMISSION_GRANTED) {

                        mFusedLocationClient.requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.myLooper());
                        mGoogleMap.setMyLocationEnabled(true);
                    }

                } else {

                    // разрешение отказано
                    // откл функц-ость которая зависит от этого разрешения
                    Toast.makeText(this, "permission denied", Toast.LENGTH_LONG).show();
                }
                return;
            }
        }
    }
}

