package com.example.labGPS;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.room.Room;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.icu.text.SimpleDateFormat;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.labGPS.Database.Memory;
import com.example.labGPS.Database.MemoryDatabase;
import com.example.labGPS.R;

import java.io.ByteArrayOutputStream;
import java.util.Date;
import java.util.Locale;

public class Add extends AppCompatActivity {

    private EditText TitleEditText, DescriptionEditText;
    private Button saveNote;

    ImageView imageView;
    Integer Request_Camera = 1, Select_File = 0;

    private byte[] imageview;

    public static MemoryDatabase memoryDatabase;

    Button cameraicon, deletecameraiconadd;

//MediaRecorder mediaRecorder;
//private static String fileName = null;

    public byte[] imageInByte = null;

    double latitude, longitude;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);

        TitleEditText = findViewById(R.id.EditTextTitle);
        DescriptionEditText = findViewById(R.id.EditTextDescription);

        imageView = (ImageView) findViewById(R.id.imageviewadd);

        cameraicon = (Button) findViewById(R.id.cameraiconadd);


        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            latitude = extras.getDouble("latitude");
            longitude = extras.getDouble("longitude");
        }

        cameraicon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                SelectImage();
            }
        });


        saveNote = findViewById(R.id.saveNote);

        saveNote.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onClick(View view) {

                String TitleString = TitleEditText.getText().toString();

                String DescriptionString = DescriptionEditText.getText().toString();

                if (imageView.getDrawable() != null) {

                    Bitmap bitmap = ((BitmapDrawable) imageView.getDrawable()).getBitmap();
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    if (bitmap != null) {
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                        imageInByte = baos.toByteArray();
                    }
                }

                if (TitleString.matches("") || DescriptionString.matches("")) {

                    Toast.makeText(getApplicationContext(), "Please fill out the blank fields", Toast.LENGTH_SHORT).show();

                } else {

                    //Date currentTime = Calendar.getInstance().getTime();

                    String currentDate = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date());
                    String currentTime = new SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(new Date());

                    Memory memory = new Memory();

                    memory.setTitle(TitleString);
                    memory.setTime(currentDate + " " + currentTime);
                    memory.setDescription(DescriptionString);


                    memory.setImage(imageInByte);

                    memory.setLatitude(latitude);
                    memory.setLongitude(longitude);


                    memoryDatabase = Room.databaseBuilder(getApplicationContext(),
                            MemoryDatabase.class, "memorydb").allowMainThreadQueries().build();

                    Add.memoryDatabase.memoryDao().insert(memory);

                    Toast.makeText(getApplicationContext(), "Memory added successfully", Toast.LENGTH_SHORT).show();


                    TitleEditText.setText("");
                    DescriptionEditText.setText("");


                    Intent intent = new Intent(Add.this, MainActivity.class);

                    startActivity(intent);

                    finish();
                }
            }
        });
    }

    private void SelectImage() {

        final CharSequence[] items = {"Camera", "Gallery", "Cancel"};

        AlertDialog.Builder builder = new AlertDialog.Builder(Add.this);
        builder.setTitle("Add Image");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int i) {

                if (items[i].equals("Camera")) {
                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(intent, Request_Camera);

                } else if (items[i].equals("Gallery")) {
                    Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(intent.createChooser(intent, "Select File"), Select_File);

                } else if (items[i].equals("Cancel")) {
                    dialog.dismiss();
                }

            }
        });

        builder.show();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        final ProgressDialog progressDialog;
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Uploading");
        progressDialog.show();

        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK) {

            if (requestCode == Request_Camera) {


                Bundle bundle = data.getExtras();
                final Bitmap bitmap = (Bitmap) bundle.get("data");
                imageView.setImageBitmap(bitmap);
                progressDialog.dismiss();

            } else if (requestCode == Select_File) {

                Uri SelectedImageUri = data.getData();

                imageView.setImageURI(SelectedImageUri);

                progressDialog.dismiss();
            }
        }
    }
}


