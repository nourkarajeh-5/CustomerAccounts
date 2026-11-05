package com.example.customeraccounts;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.customeraccounts.databinding.ItemAccountBinding;

import java.util.ArrayList;

public class CustomerAdapter extends RecyclerView.Adapter<CustomerAdapter.CustomerViewHolder> {
    private Context context;
    private ArrayList<Customer> customerList;
    private DBHelper dbHelper;

    public CustomerAdapter(Context context, ArrayList<Customer> customerList) {
        this.context = context;
        this.customerList = customerList;
        this.dbHelper = new DBHelper(context);
    }

    @NonNull
    @Override
    public CustomerAdapter.CustomerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemAccountBinding binding = ItemAccountBinding.inflate(LayoutInflater.from(context), parent, false);
        return new CustomerViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull CustomerAdapter.CustomerViewHolder holder, int position) {
        Customer customer = customerList.get(position);
        holder.binding.txtName.setText(customer.getName());
        holder.binding.txtPhone.setText(customer.getPhone());
        holder.binding.CustomerTotal.setText("₪ " + Math.abs(customer.getBalance()));

        double balance = customer.getBalance();

        int greenColor = Color.parseColor("#4CAF50");
        int redColor = Color.parseColor("#B8271D");
        int blueColor = Color.parseColor("#3F51B5");

        if (balance == 0) {
            holder.binding.isZero.setText("مصفّر");
            holder.binding.isZero.setTextColor(greenColor);
            holder.binding.CustomerTotal.setTextColor(greenColor);
            holder.binding.isZero.setBackgroundResource(R.drawable.zero_shape);

            if (!customer.isZeroBalance()) {
                customer.setZeroBalance(true);
                dbHelper.updateCustomerCheckBoxStatus(customer.getId(), true);
            }
        }
        else if (balance > 0) {
            holder.binding.isZero.setText("عليه رصيد");
            holder.binding.isZero.setTextColor(redColor);
            holder.binding.CustomerTotal.setTextColor(redColor);
            holder.binding.isZero.setBackgroundResource(R.drawable.plus_shape);

            if (customer.isZeroBalance()) {
                customer.setZeroBalance(false);
                dbHelper.updateCustomerCheckBoxStatus(customer.getId(), false);
            }
        }
        else {
            holder.binding.isZero.setText("له رصيد");
            holder.binding.isZero.setTextColor(blueColor);
            holder.binding.CustomerTotal.setTextColor(blueColor);
            holder.binding.isZero.setBackgroundResource(R.drawable.minus_shape);

            if (customer.isZeroBalance()) {
                customer.setZeroBalance(false);
                dbHelper.updateCustomerCheckBoxStatus(customer.getId(), false);
            }
        }

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, CustomerDetailsActivity.class);

            intent.putExtra("CUSTOMER_ID", customer.getId());
            intent.putExtra("CUSTOMER_NAME", customer.getName());
            intent.putExtra("CUSTOMER_PHONE", customer.getPhone());
            intent.putExtra("CUSTOMER_BALANCE", customer.getBalance());

            context.startActivity(intent);
        });


        holder.itemView.setOnLongClickListener(v -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setTitle("تأكيد حذف الزبون");
            builder.setMessage("هل أنت متأكد من حذف الزبون (" + customer.getName() + ") وجميع سجلات مشترياته نهائياً؟");
            builder.setPositiveButton("نعم، احذف", (dialog, which) -> {
                boolean isDeleted = dbHelper.deleteCustomer(customer.getId());
                if (isDeleted) {
                    customerList.remove(position);
                    notifyItemRemoved(position);
                    notifyItemRangeChanged(position, customerList.size());
                    if (context instanceof MainActivity) {
                        ((MainActivity) context).calculateSummary();
                    }
                    Toast.makeText(context, "تم حذف حساب الزبون بنجاح", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(context, "عذراً، فشل حذف حساب الزبون", Toast.LENGTH_SHORT).show();
                }
            });
            builder.setNegativeButton("إلغاء", (dialog, which) -> dialog.dismiss());
            builder.show();
            return true;
        });
    }

    @Override
    public int getItemCount() {
        return customerList.size();
    }

    public static class CustomerViewHolder extends RecyclerView.ViewHolder {
        ItemAccountBinding binding;

        public CustomerViewHolder(@NonNull ItemAccountBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
