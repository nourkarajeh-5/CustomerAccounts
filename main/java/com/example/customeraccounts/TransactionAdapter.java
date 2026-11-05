package com.example.customeraccounts;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.customeraccounts.databinding.ItemPurchasesBinding;

import java.util.ArrayList;

public class TransactionAdapter extends RecyclerView.Adapter<TransactionAdapter.TransactionViewHolder> {
    private Context context;
    private ArrayList<Transaction> transactionList;

    public TransactionAdapter(Context context, ArrayList<Transaction> transactionList) {
        this.context = context;
        this.transactionList = transactionList;
    }

    @NonNull
    @Override
    public TransactionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemPurchasesBinding binding = ItemPurchasesBinding.inflate(LayoutInflater.from(context), parent, false);
        return new TransactionViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull TransactionViewHolder holder, int position) {
        Transaction transaction = transactionList.get(position);

        holder.binding.txtDate.setText(transaction.getDate());
        holder.binding.productPrice.setText("₪ " + transaction.getAmount());

        int redColor = Color.parseColor("#B8271D");
        int greenColor = Color.parseColor("#4B9B94");

        if (transaction.getType() != null && transaction.getType().equalsIgnoreCase("شراء")) {
            holder.binding.txtProduct.setText(transaction.getName());

            holder.binding.txtProduct.setTextColor(redColor);
            holder.binding.productPrice.setTextColor(redColor);
        } else {
            if (transaction.getName() != null && !transaction.getName().trim().isEmpty()) {
                holder.binding.txtProduct.setText("دفعة: " + transaction.getName());
            } else {
                holder.binding.txtProduct.setText("تسديد دفعة نقدية");
            }

            holder.binding.txtProduct.setTextColor(greenColor);
            holder.binding.productPrice.setTextColor(greenColor);
        }
    }

    @Override
    public int getItemCount() {
        return transactionList.size();
    }

    public static class TransactionViewHolder extends RecyclerView.ViewHolder {
        ItemPurchasesBinding binding;

        public TransactionViewHolder(@NonNull ItemPurchasesBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
