package com.example.customeraccounts;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import java.util.ArrayList;

public class DBHelper extends SQLiteOpenHelper {

    public DBHelper(Context context) {
        super(context, "CustomerAccounts.db", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String customersTable = "CREATE TABLE customers (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "name TEXT NOT NULL," +
                "phone TEXT," +
                "balance REAL," +
                "is_zero_balance INTEGER)";
        db.execSQL(customersTable);

        String transactionsTable = "CREATE TABLE transactions (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "customer_id INTEGER, " +
                "name TEXT, " +
                "amount REAL, " +
                "date TEXT, " +
                "type TEXT," +
                "FOREIGN KEY(customer_id) REFERENCES customers(id) ON DELETE CASCADE)";
        db.execSQL(transactionsTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS transactions");
        db.execSQL("DROP TABLE IF EXISTS customers");
        onCreate(db);
    }

    @Override
    public void onConfigure(SQLiteDatabase db) {
        super.onConfigure(db);
        db.setForeignKeyConstraintsEnabled(true);
    }

    public boolean insertCustomer(Customer customer) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("name", customer.getName());
        values.put("phone", customer.getPhone());
        values.put("balance", customer.getBalance());
        values.put("is_zero_balance", customer.isZeroBalance() ? 1 : 0);

        long result = db.insert("customers", null, values);
        db.close();
        return result != -1;
    }

    public ArrayList<Customer> getAllCustomers() {
        ArrayList<Customer> customerList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM customers ORDER BY name ASC";
        Cursor cursor = db.rawQuery(query, null);
        if (cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(0);
                String name = cursor.getString(1);
                String phone = cursor.getString(2);
                double balance = cursor.getDouble(3);
                boolean isZero = cursor.getInt(4) == 1;
                Customer customer = new Customer(id, name, phone, balance, isZero);
                customerList.add(customer);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return customerList;
    }

    public ArrayList<Customer> searchCustomers(String search) {
        ArrayList<Customer> filteredList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM customers WHERE name LIKE ? OR phone LIKE ? ORDER BY name ASC";
        String searchPattern = "%" + search + "%";
        Cursor cursor = db.rawQuery(query, new String[]{searchPattern, searchPattern});
        if (cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(0);
                String name = cursor.getString(1);
                String phone = cursor.getString(2);
                double balance = cursor.getDouble(3);
                boolean isZero = cursor.getInt(4) == 1;
                Customer customer = new Customer(id, name, phone, balance, isZero);
                filteredList.add(customer);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return filteredList;
    }

    public boolean deleteCustomer(int customerId) {
        SQLiteDatabase db = this.getWritableDatabase();
        int result = db.delete("customers", "id = ?", new String[]{String.valueOf(customerId)});
        db.close();
        return result > 0;
    }

    public boolean updateCustomerBalance(int customerId, double newBalance, boolean isZero) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("balance", newBalance);
        values.put("is_zero_balance", isZero ? 1 : 0);

        int result = db.update("customers", values, "id = ?", new String[]{String.valueOf(customerId)});
        db.close();
        return result > 0;
    }
    public boolean updateCustomerCheckBoxStatus(int customerId, boolean isZero) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("is_zero_balance", isZero ? 1 : 0);
        int result = db.update("customers", values, "id = ?", new String[]{String.valueOf(customerId)});
        db.close();
        return result > 0;
    }
    public boolean insertTransaction(Transaction transaction) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("customer_id", transaction.getCustomerId());
        values.put("name", transaction.getName());
        values.put("amount", transaction.getAmount());
        values.put("date", transaction.getDate());
        values.put("type", transaction.getType());
        long result = db.insert("transactions", null, values);
        db.close();
        return result != -1;
    }
    public ArrayList<Transaction> getTransactionsForCustomer(int customerId) {
        ArrayList<Transaction> list = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM transactions WHERE customer_id = ? ORDER BY id DESC";
        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(customerId)});
        if (cursor.moveToFirst()) {
            do {
                Transaction transaction = new Transaction(
                        cursor.getInt(0),
                        cursor.getInt(1),
                        cursor.getString(2),
                        cursor.getDouble(3),
                        cursor.getString(4),
                        cursor.getString(5)
                );
                list.add(transaction);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return list;
    }
    public boolean isPhoneExists(String phone) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT 1 FROM customers WHERE phone = ?";
        Cursor cursor = db.rawQuery(query, new String[]{phone});
        boolean exists = cursor.getCount() > 0;
        cursor.close();
        db.close();
        return exists;
    }

}
