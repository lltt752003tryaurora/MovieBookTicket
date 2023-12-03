package com.example.movieaapp.Adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.movieaapp.Domain.Showtime;
import com.example.movieaapp.R;

import java.util.List;

public class ShowtimeAdapter extends RecyclerView.Adapter<ShowtimeAdapter.ViewHolder> {

    private List<Showtime> showtimes;
    private OnShowtimeClickListener listener;
    private int selectedPosition = -1; // -1: any showtime (time movie) can not be choose to book ticket

    public ShowtimeAdapter(List<Showtime> showtimes, OnShowtimeClickListener onShowtimeClickListener) {
        this.showtimes = showtimes;
        this.listener = onShowtimeClickListener;
    }

    public List<Showtime> getShowtimes() {
        return showtimes;
    }

    public interface OnShowtimeClickListener {
        void onShowtimeClick(Showtime showtime);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.showtime_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Showtime showtime = showtimes.get(position);
        holder.dateTextView.setText(showtime.getTime());
        holder.dateTextView.setEnabled(!"unavailable".equals(showtime.getStatus()));
        holder.dateTextView.setSelected(showtime.isSelected());

        holder.dateTextView.setOnClickListener(view -> {
            // Xử lý chọn và bỏ chọn showtime
            if (selectedPosition == holder.getAdapterPosition()) {
                showtime.setSelected(!showtime.isSelected());
            } else {
                int previousPosition = selectedPosition;
                selectedPosition = holder.getAdapterPosition();
                if (previousPosition != RecyclerView.NO_POSITION) {
                    showtimes.get(previousPosition).setSelected(false);
                    notifyItemChanged(previousPosition);
                }
                showtime.setSelected(true);
            }
            notifyItemChanged(holder.getAdapterPosition());

            // Gọi listener với showtime được chọn
            if (listener != null) {
                listener.onShowtimeClick(showtime);
            }
        });
    }



    @Override
    public int getItemCount() {
        return showtimes.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView dateTextView;

        public ViewHolder(View itemView) {
            super(itemView);
            dateTextView = itemView.findViewById(R.id.itemTime);
        }
    }
}

