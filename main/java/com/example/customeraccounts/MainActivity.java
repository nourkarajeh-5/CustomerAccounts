package com.example.customeraccounts;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;

import com.example.customeraccounts.databinding.ActivityMainBinding;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private DBHelper dbHelper;
    private CustomerAdapter adapter;
    private ArrayList<Customer> customerList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        dbHelper = new DBHelper(this);
        customerList = new ArrayList<>();

        binding.rvAccounts.setLayoutManager(new LinearLayoutManager(this));
        loadCustomerData();

        binding.floatingAddCustomer.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, AddAccountsActivity.class);
            startActivity(intent);
        });
        binding.editSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String query = s.toString().trim();
                if (query.isEmpty()) {
                    loadCustomerData();
                } else {
                    filterCustomers(query);
                }
            }
            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    public void calculateSummary() {
        double totalDebit = 0;
        double totalCredit = 0;

        ArrayList<Customer> allCustomers = dbHelper.getAllCustomers();

        for (Customer c : allCustomers) {
            if (c.getBalance() > 0) {
                totalDebit += c.getBalance();
            } else if (c.getBalance() < 0) {
                totalCredit += Math.abs(c.getBalance());
            }
        }

        binding.totalPrice.setText("₪ " + totalDebit);
        binding.totalPrice2.setText("₪ " + totalCredit);
        binding.txtCount.setText("عدد الزبائن: " + allCustomers.size());

        if (allCustomers.isEmpty()) {
            binding.tvEmptyState.setVisibility(android.view.View.VISIBLE);
        } else {
            binding.tvEmptyState.setVisibility(android.view.View.GONE);
        }
    }


    private void loadCustomerData() {
        customerList = dbHelper.getAllCustomers();
        adapter = new CustomerAdapter(this, customerList);
        binding.rvAccounts.setAdapter(adapter);

        if (customerList.isEmpty()) {
            binding.tvEmptyState.setVisibility(android.view.View.VISIBLE);
        } else {
            binding.tvEmptyState.setVisibility(android.view.View.GONE);
        }

        calculateSummary();
    }

    private void filterCustomers(String query) {
        customerList = dbHelper.searchCustomers(query);
        adapter = new CustomerAdapter(this, customerList);
        binding.rvAccounts.setAdapter(adapter);

        if (customerList.isEmpty()) {
            binding.tvEmptyState.setVisibility(android.view.View.VISIBLE);
        } else {
            binding.tvEmptyState.setVisibility(android.view.View.GONE);
        }

        calculateSummary();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadCustomerData();
        if (adapter != null) {
            adapter.notifyDataSetChanged();
        }
    }

}