package com.example.reminder;

import android.annotation.SuppressLint;
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
import java.util.ArrayList;
import java.util.List;

public class RecyclerCustom extends RecyclerView.Adapter<RecyclerCustom.ReminderViewHolder> {

    private List<Reminder> reminderList = new ArrayList<>();
    private onItemClickListener listener;


    public RecyclerCustom() {
    }

    @NonNull
    @NotNull
    @Override
    public RecyclerCustom.ReminderViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_view, parent, false);
        return new ReminderViewHolder(itemView);

    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onBindViewHolder(@NonNull @NotNull RecyclerCustom.ReminderViewHolder holder, int position) {
        holder.nameOfTheReminder.setText(reminderList.get(position).getNameOfTheReminder());
        holder.dateOfTheReminder.setText(reminderList.get(position).getDateOfTheReminder());
        holder.timeOfTheReminder.setText(reminderList.get(position).getTimeOfTheReminder());
        if (reminderList.get(position).isLocationFeatureActive()) {
            if (reminderList.get(position).getTypeOfTransfer().equals("Walking")) {
                holder.transitIcon.setImageResource(R.drawable.walk_directions_icon);
                holder.timeToDestination.setText(reminderList.get(position).getTimeToGetToDestination().get(0));
            } else {
                holder.transitIcon.setImageResource(R.drawable.car_direction_icon);
                if (reminderList.get(position).getModelAverageOrWorse().equals("Average")){
                    holder.timeToDestination.setText(reminderList.get(position).getTimeToGetToDestination().get(1));
                }else {
                    holder.timeToDestination.setText(reminderList.get(position).getTimeToGetToDestination().get(2));
                }
            }
            holder.trafficModel.setText(reminderList.get(position).getModelAverageOrWorse());
        } else {
            holder.transitIcon.setVisibility(View.GONE);
            holder.trafficModel.setVisibility(View.GONE);
            holder.timeToDestination.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return reminderList.size();
    }


    @SuppressLint("NotifyDataSetChanged")
    public void setReminderList(List<Reminder> reminders) {
        this.reminderList = reminders;
        notifyDataSetChanged();
    }

    public class ReminderViewHolder extends RecyclerView.ViewHolder {

        public TextView nameOfTheReminder, timeOfTheReminder, dateOfTheReminder, trafficModel, timeToDestination;
        public ImageView transitIcon;

        public ReminderViewHolder(View view) {
            super(view);
            nameOfTheReminder = view.findViewById(R.id.nameOfTheReminder);
            timeOfTheReminder = view.findViewById(R.id.recyclerTime);
            dateOfTheReminder = view.findViewById(R.id.dateOfTheReminder);
            trafficModel = view.findViewById(R.id.modelType);
            timeToDestination = view.findViewById(R.id.timeToDestination);
            transitIcon = view.findViewById(R.id.transitIcon);
            view.setOnClickListener(v -> listener.onItemClick(reminderList.get(getAdapterPosition())));

        }
    }

    public interface onItemClickListener {
        void onItemClick(Reminder reminder);
    }

    public void setOnItemClickListener(onItemClickListener listener) {
        this.listener = listener;
    }
}
