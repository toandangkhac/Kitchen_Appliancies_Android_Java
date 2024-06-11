package com.example.kitchen_appliances_android_java.adapter;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.kitchen_appliances_android_java.R;
import com.example.kitchen_appliances_android_java.activity.EditProductActivity;
import com.example.kitchen_appliances_android_java.activity.OrderInfo;
import com.example.kitchen_appliances_android_java.model.Order;
import com.example.kitchen_appliances_android_java.model.Product;

import java.util.ArrayList;

public class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.OrderViewHolder>{
    private static ArrayList<Order> orders;

    public OrderAdapter(ArrayList<Order> orders) {
        this.orders = orders;
    }

    @NonNull
    @Override
    public  OrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.order_admin, parent, false);
        return new OrderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderViewHolder holder, int position) {
        Order order = orders.get(position);
        holder.bind(order);
    }

    @Override
    public int getItemCount() {
        return orders.size();
    }

    static class OrderViewHolder extends RecyclerView.ViewHolder{
        private TextView order_time, payStatus, orderStatus;
        private ImageButton btn_check;

        public OrderViewHolder(@NonNull View itemView) {
            super(itemView);
            order_time = itemView.findViewById(R.id.order_time);
            payStatus = itemView.findViewById(R.id.payStatus);
            orderStatus = itemView.findViewById(R.id.orderStatus);
            btn_check = itemView.findViewById(R.id.btn_check);

            btn_check.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        Order clickedOrder = orders.get(position);
                        Intent intent = new Intent(v.getContext(), OrderInfo.class);
                        intent.putExtra("order", clickedOrder);
                        v.getContext().startActivity(intent);
                    }
                }
            });

        }

        public void bind(Order order) {
//            order_time.setText(String.valueOf(order.getCreateAt()));
            if (order.isPaymentStatus()) {
                payStatus.setText("Đã thanh toán");
            } else {
                payStatus.setText("Chưa thanh toán");
            }
            if (order.getStatus()==1) {
                orderStatus.setText("Chưa xác nhận");
            } else {
                orderStatus.setText("Đã xác nhận");
            }

        }
    }



}
