package com.example.labGPS.Adapter;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.labGPS.Database.Memory;

import com.example.labGPS.MainActivity;
import com.example.labGPS.Map;
import com.example.labGPS.Memories_List;
import com.example.labGPS.R;
import com.example.labGPS.Show;
import com.example.labGPS.Update;
import com.example.labGPS.Display;

import java.util.ArrayList;
import java.util.List;

public class MemoriesAdapter extends RecyclerView.Adapter<MemoriesAdapter.Viewholder>{

    private List<Memories_List> MemoriesList;

    private Context context;
    private List<Memory> memories;



    public MemoriesAdapter(List<Memories_List> memoriesList, Context context) {
        MemoriesList = memoriesList;

        this.context = context;
    }

    @NonNull
    @Override
    public MemoriesAdapter.Viewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View v= LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_memories__list,parent,false);

        return new Viewholder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull final Viewholder holder, final int position) {

        final Memories_List memorylist= MemoriesList.get(position);

        holder.TextViewTitle.setText(memorylist.getT());
        holder.TextViewTime.setText(memorylist.getTime());
        holder.TextViewDescription.setText(memorylist.getDescription());

        if(memorylist.getImage()!=null){

            holder.imageView.setVisibility(View.VISIBLE);

        }else{
            holder.imageView.setVisibility(View.GONE);
        }

        holder.Deletebtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {


                /*Note note = new Note();
                note.setId(position);
                MainActivity.noteDatabase.noteDao().delete(note);*/

                new AlertDialog.Builder(context)
                        .setTitle("Delete Memory")
                        .setMessage("Are you sure you want to delete this memory?")

                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {

                                Show.memoryDatabase.memoryDao().deleteByItemId(memorylist.getId());

                                MemoriesList.remove(position);
                                notifyItemRemoved(position);
                                // эта строка ниже дает вам анимацию, а также обновляет
                                 // список элементов после удаленного элемента
                                notifyItemRangeChanged(position, getItemCount());

                                Toast.makeText(context,"Memory deleted successfully", Toast.LENGTH_SHORT).show();

                                Intent intent = new Intent(context, MainActivity.class);
                                context.startActivity(intent);

                            }
                        })

                        // Нулевой слушатель позволяет кнопке закрыть диалог и не предпринимать никаких дальнейших действий.
                        .setNegativeButton(android.R.string.no, null)
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .show();

            }
        });


        holder.Updatebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {



                Intent intent = new Intent(context, Update.class);
                intent.putExtra("Title",memorylist.getT());
                intent.putExtra("Description",memorylist.getDescription());
                intent.putExtra("Id",memorylist.getId());
                intent.putExtra("Image",memorylist.getImage());
                intent.putExtra("latitude",memorylist.getLatitude());
                intent.putExtra("longitude",memorylist.getLongitude());
                context.startActivity(intent);

            }
        });

        holder.Markerbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(context, Map.class);

                intent.putExtra("Id",memorylist.getId());

                context.startActivity(intent);


            }
        });















        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {



                if(memorylist.getImage()!=null){

                    final Bitmap imgbytes = BitmapFactory.decodeByteArray(memorylist.getImage(), 0, memorylist.getImage().length);

                    Display.data=imgbytes;
                }else{

                    Display.data=null;

                }



                Intent intent = new Intent(context, Display.class);
                intent.putExtra("Title",memorylist.getT());
                intent.putExtra("Description",memorylist.getDescription());
                intent.putExtra("Time",memorylist.getTime());
                intent.putExtra("Image",memorylist.getImage());






                context.startActivity(intent);

            }
        });



    }


    @Override
    public int getItemCount() {
        return MemoriesList.size();
    }



    public static class Viewholder extends RecyclerView.ViewHolder {

        public TextView TextViewTitle, TextViewTime, TextViewDescription;
        public Button Deletebtn,Updatebtn,Markerbtn;
        public ImageView imageView;
        public int camera;

        @SuppressLint("ResourceType")
        public Viewholder(@NonNull View itemView) {
            super(itemView);
            TextViewTitle=(TextView)itemView.findViewById(R.id.TextViewTitle);
            TextViewTime=(TextView)itemView.findViewById(R.id.TextViewTime);
            TextViewDescription=(TextView)itemView.findViewById(R.id.TextViewDescription);
            Deletebtn=(Button)itemView.findViewById(R.id.Deletebtn);
            Updatebtn=(Button)itemView.findViewById(R.id.Updatebtn);
            Markerbtn=(Button)itemView.findViewById(R.id.Markerbtn);
            camera = R.drawable.ic_camera;


            imageView = (ImageView)itemView.findViewById(R.id.imageviewshow);

        }




    }

    public void updateList(List<Memories_List> newList){


        MemoriesList = new ArrayList<>();
        MemoriesList.addAll(newList);
        notifyDataSetChanged();


    }












}
