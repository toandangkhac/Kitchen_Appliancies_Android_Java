package com.example.kitchen_appliances_android_java.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.kitchen_appliances_android_java.R;
import com.example.kitchen_appliances_android_java.model.Account;
import com.example.kitchen_appliances_android_java.model.Order;

import java.util.ArrayList;

public class AccountsAdapter extends RecyclerView.Adapter<AccountsAdapter.AccountsViewHolder> {
    public static ArrayList<Account> accounts;

    public AccountsAdapter(ArrayList<Account> accounts){
        this.accounts = accounts;
    }

    @NonNull
    @Override
    public AccountsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.account_item, parent, false);
        return new AccountsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AccountsViewHolder holder, int position) {
        Account account = accounts.get(position);
        holder.bind(account);
    }

    @Override
    public int getItemCount() {
        return accounts.size();
    }

    static class AccountsViewHolder extends RecyclerView.ViewHolder {
        private TextView tv_email, tv_roleId, tv_status_account;

        public AccountsViewHolder(@NonNull View itemView) {
            super(itemView);
            tv_email = itemView.findViewById(R.id.tv_email);
            tv_roleId = itemView.findViewById(R.id.tv_roleId);
            tv_status_account = itemView.findViewById(R.id.tv_status_account);
        }

        public void bind(@NonNull Account account) {
            tv_email.setText(account.getEmail());
            tv_roleId.setText(String.valueOf(account.getRoleId()));
            if (account.isStatus()){
                tv_status_account.setText("Đang hoạt động");
            } else {
                tv_status_account.setText("Bị khóa");
            }
        }
    }
}