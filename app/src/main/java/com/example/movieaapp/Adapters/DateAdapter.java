package com.example.movieaapp.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.movieaapp.R;

import java.util.List;

public class DateAdapter extends RecyclerView.Adapter<DateAdapter.DateViewHolder> {

    private Context context;
    private List<String> dates;
    private int selectedIndex = -1;
    private OnDateClickListener onDateClickListener;

    public DateAdapter(Context context, List<String> dates) {
        this.context = context;
        this.dates = dates;
    }

    public void setOnDateClickListener(OnDateClickListener onDateClickListener) {
        this.onDateClickListener = onDateClickListener;
    }

    public interface OnDateClickListener {
        void onDateClick(String date);
    }

    @Override
    public DateViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.date_item, parent, false);
        return new DateViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final DateViewHolder holder, int position) {
        String date = dates.get(position);
        holder.dateTextView.setText(date);

        final int adapterPosition = holder.getAdapterPosition();

        if (selectedIndex == adapterPosition) {
            holder.dateTextView.setBackgroundResource(R.drawable.selected_date_background);
            holder.dateTextView.setTextColor(context.getResources().getColor(R.color.white));
        } else {
            holder.dateTextView.setBackgroundResource(R.drawable.default_date_background);
            holder.dateTextView.setTextColor(context.getResources().getColor(R.color.white));
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (adapterPosition != RecyclerView.NO_POSITION) {
                    selectedIndex = adapterPosition;
                    notifyDataSetChanged();

                    if (onDateClickListener != null) {
                        onDateClickListener.onDateClick(date);
                    }
                }
            }
        });
    }


    @Override
    public int getItemCount() {
        return dates.size();
    }

    public class DateViewHolder extends RecyclerView.ViewHolder {
        TextView dateTextView;

        public DateViewHolder(View itemView) {
            super(itemView);
            dateTextView = itemView.findViewById(R.id.dateTextView);
        }
    }
}
