package com.example.kitchen_appliances_android_java.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.kitchen_appliances_android_java.R;
import com.example.kitchen_appliances_android_java.model.Category;
import com.example.kitchen_appliances_android_java.model.Product;

import java.util.ArrayList;

public class ListCategoryAdapter extends RecyclerView.Adapter<ListCategoryAdapter.ListCategoryViewHolder>{
    private static ArrayList<Category> categories;
    private OnCategoryListener mlistener;

    public ListCategoryAdapter(ArrayList<Category> categories, OnCategoryListener listener) {
        this.categories = categories;
        this.mlistener = listener;
    }

    @NonNull
    @Override
    public ListCategoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.category_item, parent, false);
            return new ListCategoryViewHolder(view, mlistener);
    }

    @Override
    public void onBindViewHolder(@NonNull ListCategoryViewHolder holder, int position) {
        Category category = categories.get(position);
        holder.bind(category);
    }

    @Override
    public int getItemCount() {
        return categories.size();
    }

    public interface OnCategoryListener{
        void onButtonEdit(String strName, int ctgrId);
        void onButtonDelete(int ctgrId);
    }


    static class ListCategoryViewHolder extends RecyclerView.ViewHolder {
        private EditText  ctgr_name;
        private ImageButton btn_edit_category, btn_delete_category;

        private OnCategoryListener mlistener;

        public ListCategoryViewHolder(@NonNull View itemView, OnCategoryListener listener) {
            super(itemView);
            mlistener = listener;

            ctgr_name = itemView.findViewById(R.id.ctgr_name);
            btn_edit_category = itemView.findViewById(R.id.btn_edit_category);
            btn_delete_category = itemView.findViewById(R.id.btn_delete_category);
            final boolean[] isEditing = {false};
            btn_edit_category.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (isEditing[0]) {
                        ctgr_name.setEnabled(false);
                        btn_edit_category.setImageResource(R.drawable.btn_edit);
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION) {
                            String ctgrName = ctgr_name.getText().toString();
                            int ctgrId = categories.get(position).getId();
                            mlistener.onButtonEdit(ctgrName, ctgrId);
                        }
                    } else {
                        ctgr_name.setEnabled(true);
                        btn_edit_category.setImageResource(R.drawable.btn_save);
                    }
                    isEditing[0] = !isEditing[0];
                }
            });

            btn_delete_category.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        int ctgrId = categories.get(position).getId();
                        mlistener.onButtonDelete(ctgrId);
                    }
                }
            });

        }

        public void bind(Category category) {
            ctgr_name.setText(category.getName());
        }
    }
}
