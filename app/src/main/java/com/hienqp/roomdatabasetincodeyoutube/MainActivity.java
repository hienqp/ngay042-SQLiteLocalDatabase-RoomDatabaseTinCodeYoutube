package com.hienqp.roomdatabasetincodeyoutube;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.hienqp.roomdatabasetincodeyoutube.database.UserDatabase;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private static final int MY_REQUEST_CODE = 10;
    private EditText edtUsername;
    private EditText edtAddress;
    private Button btnAddUser;
    private RecyclerView rcvUser;
    private TextView tvDeleteAll;
    private EditText edtSearch;
    private EditText edtYear;

    private UserAdapter userAdapter;
    private List<User> mListUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initUi();

        userAdapter = new UserAdapter(new UserAdapter.IClickItemUser() {
            @Override
            public void updateUser(User user) {
                clickUpdateUser(user);
            }

            @Override
            public void deleteUser(User user) {
                clickDeleteUser(user);
            }
        });
        mListUser = new ArrayList<>();
        userAdapter.setData(mListUser);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(MainActivity.this);
        rcvUser.setLayoutManager(linearLayoutManager);

        rcvUser.setAdapter(userAdapter);
        loadData();

        btnAddUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addUser();
            }
        });

        tvDeleteAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickDeleteAllUser();
            }
        });

        edtSearch.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    // logic search
                    clickSearchUser();
                }
                return false;
            }
        });
    }

    private void clickSearchUser() {
        String strKeyword = edtSearch.getText().toString().trim();
        mListUser = new ArrayList<>();
        mListUser = UserDatabase.getInstance(MainActivity.this).userDAO().searchUser(strKeyword);
        userAdapter.setData(mListUser);
        hideSoftKeyboard();
    }

    private void clickDeleteAllUser() {
        new AlertDialog.Builder(MainActivity.this)
                .setTitle("Confirm Delete All User")
                .setMessage("Are you sure ?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // logic delete user trong database
                        UserDatabase.getInstance(MainActivity.this).userDAO().deleteAllUser();
                        notifyToast(MainActivity.this, "Delete all user successfully");

                        loadData();
                    }
                })
                .setNegativeButton("No", null)
                .show();
    }

    private void clickDeleteUser(User user) {
        new AlertDialog.Builder(MainActivity.this)
                .setTitle("Confirm Delete User")
                .setMessage("Are you sure ?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // logic delete user trong database
                        UserDatabase.getInstance(MainActivity.this).userDAO().deleteUser(user);
                        notifyToast(MainActivity.this, "Delete user successfully");

                        loadData();
                    }
                })
                .setNegativeButton("No", null)
                .show();
    }

    private void clickUpdateUser(User user) {
        Intent intent = new Intent(MainActivity.this, UpdateActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable("object_user", user);
        intent.putExtras(bundle);
        startActivityForResult(intent, MY_REQUEST_CODE);
    }

    private void initUi() {
        edtUsername = (EditText) findViewById(R.id.edt_username);
        edtAddress = (EditText) findViewById(R.id.edt_address);
        btnAddUser = (Button) findViewById(R.id.btn_add_user);
        rcvUser = (RecyclerView) findViewById(R.id.rcv_user);
        tvDeleteAll = (TextView) findViewById(R.id.tv_delete_all);
        edtSearch = (EditText) findViewById(R.id.edt_search);
        edtYear = (EditText) findViewById(R.id.edt_year);
    }

    private void addUser() {
        String strUsername = edtUsername.getText().toString().trim();
        String strAddress = edtAddress.getText().toString().trim();
        String strYear = edtYear.getText().toString().trim();

        if (TextUtils.isEmpty(strUsername) || TextUtils.isEmpty(strAddress)) {
            return;
        }

        User user = new User(strUsername, strAddress, strYear);

        if (isUserExist(user)) {
            notifyToast(MainActivity.this, "User exist");
            return;
        }

        UserDatabase.getInstance(MainActivity.this).userDAO().insertUser(user);
        notifyToast(MainActivity.this, "Add user successfully");

        edtUsername.setText("");
        edtAddress.setText("");
        edtYear.setText("");

        hideSoftKeyboard();

        loadData();
    }

    private void loadData() {
        mListUser =  UserDatabase.getInstance(MainActivity.this).userDAO().getListUser();
        userAdapter.setData(mListUser);
    }

    private boolean isUserExist(User user) {
        List<User> list = UserDatabase.getInstance(MainActivity.this).userDAO().checkUser(user.getUsername());
        return list != null && !list.isEmpty();
    }

    private void hideSoftKeyboard() {
        try {
            InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        } catch (NullPointerException ex) {
            ex.printStackTrace();
        }
    }

    private void notifyToast(Context context, String message) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == MY_REQUEST_CODE && resultCode == Activity.RESULT_OK ) {
            loadData();
        }
    }
}