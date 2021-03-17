package com.example.reminder;

import android.content.Context;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class RecyclerCustom extends RecyclerView.Adapter<RecyclerCustom.ReminderViewHolder>{

    private List<Reminder> reminderList;
    private Context context;
    private ItemClickListener itemClickListener;


    public RecyclerCustom(List<Reminder> reminderList, Context context){
        this.reminderList = reminderList;
        this.context = context;
    }

    @NonNull
    @NotNull
    @Override
    public RecyclerCustom.ReminderViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(context).inflate(R.layout.recycler_view, parent, false);
        return new ReminderViewHolder(itemView);

    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onBindViewHolder(@NonNull @NotNull RecyclerCustom.ReminderViewHolder holder, int position) {
        holder.nameOfTheReminder.setText(reminderList.get(position).getNameOfTheReminder());
        holder.dateOfTheReminder.setText(reminderList.get(position).getDateOfTheReminder());
        holder.timeOfTheReminder.setText(reminderList.get(position).getTimeOfTheReminder());
        if (reminderList.get(position).isLocationFeatureActive()){
            if (reminderList.get(position).getTypeOfTransfer().equals("Walking")){
                holder.transitIcon.setImageResource(R.drawable.walk_directions_icon);
            }else {
                holder.transitIcon.setImageResource(R.drawable.car_direction_icon);
            }
            holder.trafficModel.setText(reminderList.get(position).getModelAverageOrWorse());
            if (reminderList.get(position).getTypeOfTransfer().equals("Walking")){
                holder.timeToDestination.setText(reminderList.get(position).getTimeToGetToDestination().get(0));
            }else if (reminderList.get(position).getModelAverageOrWorse().equals("Average")){
                holder.timeToDestination.setText(reminderList.get(position).getTimeToGetToDestination().get(1));
            }else {
                holder.timeToDestination.setText(reminderList.get(position).getTimeToGetToDestination().get(2));
            }
        }else {
            holder.transitIcon.setVisibility(View.GONE);
            holder.trafficModel.setVisibility(View.GONE);
            holder.timeToDestination.setVisibility(View.GONE);
        }


    }

    public void setClickListener(ItemClickListener itemClickListener){
        this.itemClickListener = itemClickListener;
    }

    @Override
    public int getItemCount() {
        return reminderList.size();
    }

    public class ReminderViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        public TextView nameOfTheReminder, timeOfTheReminder, dateOfTheReminder, trafficModel, timeToDestination;
        public ImageView transitIcon;

        public ReminderViewHolder(View view){
            super(view);
            nameOfTheReminder = view.findViewById(R.id.nameOfTheReminder);
            timeOfTheReminder = view.findViewById(R.id.recyclerTime);
            dateOfTheReminder = view.findViewById(R.id.dateOfTheReminder);
            trafficModel = view.findViewById(R.id.modelType);
            timeToDestination = view.findViewById(R.id.timeToDestination);
            transitIcon = view.findViewById(R.id.transitIcon);
        }

        @Override
        public void onClick(View v) {
            if (itemClickListener != null){
                itemClickListener.onClick(v, getAdapterPosition());
            }
        }
    }
}
