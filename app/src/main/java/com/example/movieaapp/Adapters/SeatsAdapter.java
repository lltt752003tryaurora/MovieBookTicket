package com.example.movieaapp.Adapters;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.example.movieaapp.Domain.Seat;
import com.example.movieaapp.R;

import java.util.List;

public class SeatsAdapter extends BaseAdapter {
    private Context context;
    private List<Seat> seatList;
    private LayoutInflater inflater;
    private SeatClickListener seatClickListener;
    private int selectedSeatsCount = 0;
    private int totalPrice = 0;

    public int getSelectedSeatsCount() {
        return selectedSeatsCount;
    }

    public int getTotalPrice() {
        return totalPrice;
    }

    public SeatsAdapter(Context context, List<Seat> seatList) {
        this.context = context;
        this.seatList = seatList;
        inflater = LayoutInflater.from(context);
    }

    public void setSeatClickListener(SeatClickListener listener) {
        this.seatClickListener = listener;
    }

    public interface SeatClickListener {
        void onSeatClicked(int totalTickets, int totalPrice);
    }

    @Override
    public int getCount() {
        return seatList.size();
    }

    @Override
    public Object getItem(int position) {
        return seatList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.item_seat, parent, false);
        }

        ImageView imageViewSeat = convertView.findViewById(R.id.imageViewSeat);
        Seat seat = seatList.get(position);

        if ("booked".equals(seat.getStatus())) {
            imageViewSeat.setBackgroundColor(Color.GRAY);
        } else if ("selected".equals(seat.getStatus())) {
            imageViewSeat.setBackgroundColor(Color.BLUE);
        } else {
            imageViewSeat.setBackgroundColor(Color.WHITE);
        }

        imageViewSeat.setOnClickListener(v -> {
            if ("available".equals(seat.getStatus())) {
                seat.setStatus("selected");
                imageViewSeat.setBackgroundColor(Color.BLUE);
                selectedSeatsCount++; // Tăng số lượng ghế đã chọn
                totalPrice += 25; // Tăng tổng giá
            } else if ("selected".equals(seat.getStatus())) {
                seat.setStatus("available");
                imageViewSeat.setBackgroundColor(Color.WHITE);
                selectedSeatsCount--; // Giảm số lượng ghế đã chọn
                totalPrice -= 25; // Giảm tổng giá
            }

            // Gọi callback để cập nhật thông tin vé
            if (seatClickListener != null) {
                seatClickListener.onSeatClicked(selectedSeatsCount, totalPrice);
            }

            notifyDataSetChanged();
        });

        return convertView;
    }
}

