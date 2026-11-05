package com.example.customeraccounts;

import android.os.Bundle;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.customeraccounts.databinding.ActivityAddAccountsBinding;

public class AddAccountsActivity extends AppCompatActivity {

    private ActivityAddAccountsBinding binding;
    private DBHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAddAccountsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        dbHelper = new DBHelper(this);

        binding.icBack.setOnClickListener(v -> finish());


        int selectedGreen = android.graphics.Color.parseColor("#2E7D32");
        int defaultWhite = android.graphics.Color.parseColor("#FFFFFF");

        binding.btnBuy.setOnClickListener(v -> {
            binding.btnBuy.setSelected(true);
            binding.btnPaid.setSelected(false);

            binding.btnBuy.setBackgroundTintList(android.content.res.ColorStateList.valueOf(selectedGreen));
            binding.btnPaid.setBackgroundTintList(android.content.res.ColorStateList.valueOf(defaultWhite));

            if (binding.checkAccount.isChecked()) {
                binding.checkAccount.setChecked(false);
                Toast.makeText(this, "تم اختيار عليه مبلغ وإلغاء وضع التصفير", Toast.LENGTH_SHORT).show();
            }
            binding.editAmount.setEnabled(true);
            if (binding.editAmount.getText().toString().equals("0")) {
                binding.editAmount.setText("");
            }
        });

        binding.btnPaid.setOnClickListener(v -> {
            binding.btnPaid.setSelected(true);
            binding.btnBuy.setSelected(false);

            binding.btnPaid.setBackgroundTintList(android.content.res.ColorStateList.valueOf(selectedGreen));
            binding.btnBuy.setBackgroundTintList(android.content.res.ColorStateList.valueOf(defaultWhite));

            if (binding.checkAccount.isChecked()) {
                binding.checkAccount.setChecked(false);
                Toast.makeText(this, "تم اختيار له رصيد وإلغاء وضع التصفير", Toast.LENGTH_SHORT).show();
            }
            binding.editAmount.setEnabled(true);
            if (binding.editAmount.getText().toString().equals("0")) {
                binding.editAmount.setText("");
            }
        });

        binding.checkAccount.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                binding.btnBuy.setSelected(false);
                binding.btnPaid.setSelected(false);

                binding.btnBuy.setBackgroundTintList(android.content.res.ColorStateList.valueOf(defaultWhite));
                binding.btnPaid.setBackgroundTintList(android.content.res.ColorStateList.valueOf(defaultWhite));

                binding.editAmount.setText("0");
                binding.editAmount.setEnabled(false);
                Toast.makeText(this, "تم اختيار حساب مصفّر وإلغاء خيارات الأرصدة الأخرى", Toast.LENGTH_SHORT).show();
            } else {
                if (!binding.btnBuy.isSelected() && !binding.btnPaid.isSelected()) {
                    binding.editAmount.setEnabled(true);
                    binding.editAmount.setText("");
                }
            }
        });

        binding.save.setOnClickListener(v -> {
            binding.save.setBackgroundTintList(android.content.res.ColorStateList.valueOf(selectedGreen));
            new android.os.Handler().postDelayed(() -> binding.save.setBackgroundTintList(null), 150);
            saveCustomer();
        });

        binding.save.setOnClickListener(v -> saveCustomer());
    }

    private void saveCustomer() {
        String name = binding.editName.getText().toString().trim();
        String phone = binding.editPhone.getText().toString().trim();
        String amountStr = binding.editAmount.getText().toString().trim();
        boolean isZeroChecked = binding.checkAccount.isChecked();

        if (name.isEmpty()) {
            binding.editName.setError("الرجاء إدخال اسم الزبون أولاً");
            binding.editName.requestFocus();
            return;
        }
        if (name.length() < 3) {
            binding.editName.setError("يجب أن يتكون الاسم من 3 حروف على الأقل");
            binding.editName.requestFocus();
            return;
        }

        for (int i = 0; i < name.length(); i++) {
            if (Character.isDigit(name.charAt(i))) {
                binding.editName.setError("يجب أن يحتوي الاسم على حروف فقط بدون أرقام أو رموز");
                binding.editName.requestFocus();
                return;
            }
        }

        if (phone.isEmpty()) {
            binding.editPhone.setError("الرجاء إدخال رقم الجوال");
            binding.editPhone.requestFocus();
            return;
        }

        if (phone.length() != 10 || (!phone.startsWith("059") && !phone.startsWith("056"))) {
            binding.editPhone.setError("الرجاء إدخال رقم جوال صحيح يتكون من 10 أرقام ويبدأ بـ 059 أو 056");
            binding.editPhone.requestFocus();
            return;
        }
        if (dbHelper.isPhoneExists(phone)) {
            binding.editPhone.setError("رقم الجوال هذا مسجل مسبقاً لزبون آخر!");
            binding.editPhone.requestFocus();
            return;
        }

        if (!isZeroChecked && !binding.btnBuy.isSelected() && !binding.btnPaid.isSelected()) {
            Toast.makeText(this, "يجب تحديد خيار واحد فقط (مصفّر / عليه مبلغ / له رصيد)", Toast.LENGTH_LONG).show();
            return;
        }

        double balance = 0.0;

        if (!isZeroChecked) {
            if (amountStr.isEmpty() || amountStr.equals("0")) {
                binding.editAmount.setError("الرجاء إدخال مبلغ الحساب الحالي");
                binding.editAmount.setEnabled(true);
                binding.editAmount.requestFocus();
                return;
            }

            try {
                balance = Double.parseDouble(amountStr);
                if (balance <= 0) {
                    binding.editAmount.setError("يجب أن يكون المبلغ أكبر من صفر");
                    binding.editAmount.requestFocus();
                    return;
                }
            } catch (NumberFormatException e) {
                binding.editAmount.setError("الرجاء إدخال قيمة رقمية صحيحة");
                binding.editAmount.requestFocus();
                return;
            }

            if (binding.btnPaid.isSelected()) {
                balance = -balance;
            }
        } else {
            balance = 0.0;
        }

        if (balance == 0.0) {
            isZeroChecked = true;
        }

        Customer newCustomer = new Customer(0, name, phone, balance, isZeroChecked);
        boolean isInserted = dbHelper.insertCustomer(newCustomer);

        if (isInserted) {
            Toast.makeText(this, "تم حفظ حساب الزبون بنجاح", Toast.LENGTH_SHORT).show();
            finish();
        } else {
            Toast.makeText(this, "عذراً، فشل حفظ البيانات", Toast.LENGTH_SHORT).show();
        }
    }
}
