package com.winservices.wingoods.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.TextView;

import com.winservices.wingoods.R;
import com.winservices.wingoods.dbhelpers.UsersDataManager;
import com.winservices.wingoods.models.User;

public class ProfileActivity extends AppCompatActivity {

    private TextView txtPhone;
    private EditText editUserName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        setTitle(getString(R.string.my_profile));
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        txtPhone = findViewById(R.id.txtPhone);
        editUserName = findViewById(R.id.editUserName);

        UsersDataManager usersDataManager = new UsersDataManager(this);
        final User currentUser = usersDataManager.getCurrentUser();

        txtPhone.setText(currentUser.getUserPhone());
        if (currentUser.getUserName().equals("null")) currentUser.setUserName("");
        editUserName.setText(currentUser.getUserName());

        editUserName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.length() > 0) {
                    currentUser.setUserName(charSequence.toString());
                    UsersDataManager.UpdateUser updateUser = new UsersDataManager.UpdateUser(getApplicationContext(), currentUser);
                    Thread t = new Thread(updateUser);
                    t.start();
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case android.R.id.home:
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

}
