package com.example.customeraccounts;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.customeraccounts.databinding.ActivityCustomerDetailsBinding;
import com.example.customeraccounts.databinding.DialogBuyBinding;
import com.example.customeraccounts.databinding.DialogPaymentBinding;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class CustomerDetailsActivity extends AppCompatActivity {

    private ActivityCustomerDetailsBinding binding;
    private DBHelper dbHelper;
    private TransactionAdapter transactionAdapter;
    private ArrayList<Transaction> transactionList;

    private int customerId;
    private String customerName;
    private String customerPhone;
    private double customerBalance;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCustomerDetailsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        dbHelper = new DBHelper(this);
        binding.icBack.setOnClickListener(v -> finish());

        customerId = getIntent().getIntExtra("CUSTOMER_ID", -1);
        customerName = getIntent().getStringExtra("CUSTOMER_NAME");
        customerPhone = getIntent().getStringExtra("CUSTOMER_PHONE");
        customerBalance = getIntent().getDoubleExtra("CUSTOMER_BALANCE", 0.0);

        binding.rvPurchases.setLayoutManager(new LinearLayoutManager(this));
        loadTransactions();
        updateUI();

        binding.btnBuy.setOnClickListener(v -> {
            binding.btnBuy.setBackgroundColor(Color.GREEN);
            new Handler().postDelayed(() -> binding.btnBuy.setBackgroundColor(Color.parseColor("#FFFFFF")), 150);
            showBuyDialog();
        });
        binding.btnBuy.setOnClickListener(v -> {
            binding.btnBuy.setBackgroundTintList(android.content.res.ColorStateList.valueOf(android.graphics.Color.parseColor("#2E7D32")));
            new android.os.Handler().postDelayed(() -> binding.btnBuy.setBackgroundTintList(null), 150);
            showBuyDialog();
        });

        binding.btnPaid.setOnClickListener(v -> {
            binding.btnPaid.setBackgroundTintList(android.content.res.ColorStateList.valueOf(android.graphics.Color.parseColor("#2E7D32")));
            new android.os.Handler().postDelayed(() -> binding.btnPaid.setBackgroundTintList(null), 150);
            showPaymentDialog();
        });

        binding.message.setOnClickListener(v -> {
            binding.message.setBackgroundTintList(android.content.res.ColorStateList.valueOf(android.graphics.Color.parseColor("#2E7D32")));
            new android.os.Handler().postDelayed(() -> binding.message.setBackgroundTintList(null), 150);

            if (customerBalance == 0) {
                Toast.makeText(this, "الحساب مصفّر، لا داعي لإرسال رسالة تذكيرية", Toast.LENGTH_LONG).show();
                return;
            }

            String currentDate = new java.text.SimpleDateFormat("dd/MM/yyyy", java.util.Locale.getDefault()).format(new java.util.Date());
            String messageText = "";

            if (customerBalance > 0) {
                messageText = "مرحباً " + customerName + "، نود تذكيرك بأن الرصيد المستحق لحسابك هو (" + Math.abs(customerBalance) + " ₪). " +
                        " \n يرجى تسديد المبلغ في أقرب وقت ممكن.\n شكراً لتعاملكم. " +
                        "الرصيد حسب سجلاتنا حتى " + currentDate;
            } else {
                messageText = "مرحباً " + customerName + "، نود إعلامك بوجود رصيد لحسابك هو (" + Math.abs(customerBalance) + " ₪). " +
                        "\n شكراً لتعاملكم.\n " +
                        "الرصيد حسب سجلاتنا حتى " + currentDate;
            }

            Intent intent = new Intent(Intent.ACTION_SENDTO);
            intent.setData(Uri.parse("smsto:" + customerPhone));
            intent.putExtra("sms_body", messageText);

            try {
                startActivity(intent);
            } catch (Exception e) {
                Toast.makeText(this, "عذراً، لا يوجد تطبيق رسائل متوفر", Toast.LENGTH_SHORT).show();
            }
        });



    }

    private void updateUI() {
        binding.txtName.setText(customerName);
        binding.txtPhone.setText(customerPhone);
        binding.customerTotal.setText(Math.abs(customerBalance) + " ₪");

        int greenColor = Color.parseColor("#4CAF50");
        int redColor = Color.parseColor("#B8271D");
        int blueColor = Color.parseColor("#3F51B5");

        if (customerBalance == 0) {
            binding.isZero.setText("مصفّر");
            binding.isZero.setTextColor(greenColor);
            binding.customerTotal.setTextColor(greenColor);
            binding.isZero.setBackgroundResource(R.drawable.zero_shape);
        } else if (customerBalance > 0) {
            binding.isZero.setText("عليه رصيد");
            binding.customerTotal.setTextColor(redColor);
            binding.isZero.setTextColor(redColor);
            binding.isZero.setBackgroundResource(R.drawable.plus_shape);
        } else {
            binding.isZero.setText("له رصيد");
            binding.isZero.setTextColor(blueColor);
            binding.customerTotal.setTextColor(blueColor);
            binding.isZero.setBackgroundResource(R.drawable.minus_shape);
        }

    }

    private void loadTransactions() {
        transactionList = dbHelper.getTransactionsForCustomer(customerId);
        transactionAdapter = new TransactionAdapter(this, transactionList);
        binding.rvPurchases.setAdapter(transactionAdapter);

        if (transactionList.isEmpty()) {
            binding.tvEmptyState.setVisibility(android.view.View.VISIBLE);
        } else {
            binding.tvEmptyState.setVisibility(android.view.View.GONE);
        }

        transactionAdapter.notifyDataSetChanged();
    }


    private void showBuyDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        DialogBuyBinding dialogBinding = DialogBuyBinding.inflate(getLayoutInflater());
        builder.setView(dialogBinding.getRoot());
        AlertDialog dialog = builder.create();

        dialogBinding.saveProduct.setOnClickListener(v -> {
            String productName = dialogBinding.editNamePro.getText().toString().trim();
            String amountStr = dialogBinding.editPricePro.getText().toString().trim();

            if (productName.isEmpty()) {
                dialogBinding.editNamePro.setError("الرجاء إدخال اسم المنتج");
                dialogBinding.editNamePro.requestFocus();
                return;
            }
            if (amountStr.isEmpty()) {
                dialogBinding.editPricePro.setError("الرجاء إدخال السعر");
                dialogBinding.editPricePro.requestFocus();
                return;
            }

            try {
                double amount = Double.parseDouble(amountStr);
                if (amount <= 0) {
                    dialogBinding.editPricePro.setError("يجب أن يكون السعر أكبر من صفر");
                    dialogBinding.editPricePro.requestFocus();
                    return;
                }

                String currentDate = new java.text.SimpleDateFormat("dd/MM/yyyy", java.util.Locale.getDefault()).format(new java.util.Date());
                Transaction transaction = new Transaction(0, customerId, productName, amount, currentDate, "شراء");
                dbHelper.insertTransaction(transaction);

                customerBalance += amount;
                dbHelper.updateCustomerBalance(customerId, customerBalance, customerBalance == 0);

                loadTransactions();
                updateUI();
                dialog.dismiss();
                Toast.makeText(this, "تم تسجيل عملية الشراء بنجاح", Toast.LENGTH_SHORT).show();

            } catch (NumberFormatException e) {
                dialogBinding.editPricePro.setError("الرجاء إدخال قيمة رقمية صحيحة");
                dialogBinding.editPricePro.requestFocus();
            }
        });

        dialogBinding.cancelProduct.setOnClickListener(v -> dialog.dismiss());
        dialog.show();
    }


    private void showPaymentDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        DialogPaymentBinding dialogBinding = DialogPaymentBinding.inflate(getLayoutInflater());
        builder.setView(dialogBinding.getRoot());
        AlertDialog dialog = builder.create();

        dialogBinding.saveTrans.setOnClickListener(v -> {
            String methodText = dialogBinding.editTrans.getText().toString().trim();
            String amountStr = dialogBinding.editAmount.getText().toString().trim();

            if (amountStr.isEmpty()) {
                dialogBinding.editAmount.setError("الرجاء إدخال قيمة الدفعة");
                dialogBinding.editAmount.requestFocus();
                return;
            }

            try {
                double amount = Double.parseDouble(amountStr);
                if (amount <= 0) {
                    dialogBinding.editAmount.setError("يجب أن تكون الدفعة أكبر من صفر");
                    dialogBinding.editAmount.requestFocus();
                    return;
                }

                String currentDate = new java.text.SimpleDateFormat("dd/MM/yyyy", java.util.Locale.getDefault()).format(new java.util.Date());
                String transName = methodText.isEmpty() ? "تسديد دفعة نقدية" : methodText;

                Transaction transaction = new Transaction(0, customerId, transName, amount, currentDate, "دفعة");
                dbHelper.insertTransaction(transaction);

                customerBalance -= amount;
                dbHelper.updateCustomerBalance(customerId, customerBalance, customerBalance == 0);

                loadTransactions();
                updateUI();
                dialog.dismiss();
                Toast.makeText(this, "تم تسجيل الدفعة بنجاح", Toast.LENGTH_SHORT).show();

            } catch (NumberFormatException e) {
                dialogBinding.editAmount.setError("الرجاء إدخال قيمة رقمية صحيحة");
                dialogBinding.editAmount.requestFocus();
            }
        });

        dialogBinding.cancelTrans.setOnClickListener(v -> dialog.dismiss());
        dialog.show();
    }

}
