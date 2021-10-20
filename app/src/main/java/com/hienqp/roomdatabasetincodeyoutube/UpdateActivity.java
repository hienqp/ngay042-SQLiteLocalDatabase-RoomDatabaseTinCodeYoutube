package com.hienqp.roomdatabasetincodeyoutube;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.hienqp.roomdatabasetincodeyoutube.database.UserDatabase;

public class UpdateActivity extends AppCompatActivity {
    private EditText edtUsername;
    private EditText edtAddress;
    private Button btnUpdateUser;

    private User mUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update);

        edtUsername = (EditText) findViewById(R.id.edt_username);
        edtAddress = (EditText) findViewById(R.id.edt_address);
        btnUpdateUser = (Button) findViewById(R.id.btn_update_user);

        mUser = (User) getIntent().getExtras().get("object_user");

        if (mUser != null) {
            edtUsername.setText(mUser.getUsername());
            edtAddress.setText(mUser.getAddress());
        }

        btnUpdateUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateUser();
            }
        });
    }

    private void updateUser() {
        String strUsername = edtUsername.getText().toString().trim();
        String strAddress = edtAddress.getText().toString().trim();

        if (TextUtils.isEmpty(strUsername) || TextUtils.isEmpty(strAddress)) {
            return;
        }

        // Update User
        mUser.setUsername(strUsername);
        mUser.setAddress(strAddress);

        UserDatabase.getInstance(UpdateActivity.this).userDAO().updateUser(mUser);
        notifyToast(UpdateActivity.this, "Update user successfully");

        Intent intentResult = new Intent();
        setResult(Activity.RESULT_OK, intentResult);
        finish();
    }

    private void notifyToast(Context context, String message) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }
}