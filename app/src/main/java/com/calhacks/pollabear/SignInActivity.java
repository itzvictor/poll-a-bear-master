package com.calhacks.pollabear;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.calhacks.pollabear.models.UserModel;

import org.w3c.dom.Text;


public class SignInActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);
        final EditText emailTxt = (EditText) findViewById(R.id.email);
        final  EditText passwordTxt = (EditText) findViewById(R.id.password);
        final Button signinBtn = (Button)findViewById(R.id.signin_btn);
        final TextView errorMessage = (TextView) findViewById(R.id.errorMessage);
        signinBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //TODO:FInd way to clear the error
                errorMessage.setVisibility(View.INVISIBLE);
                String email = emailTxt.getText().toString();
                String password = passwordTxt.getText().toString();

                //String username = userNameTxt.getText().toString();
                if (email.isEmpty()) {
                    emailTxt.setError("This is empty!");
                    return;
                }
                if (password.isEmpty()) {
                    passwordTxt.setError("This is empty!");
                    return;
                }
                UserModel user = UserModel.findUserInEmail(email);
                if (user != null) {
                    if (user.checkPassword(password)) {
                        PollApplication.loggedInUser=user;
                        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                        startActivity(intent);
                    }
                }
                signinBtn.setError("");
                errorMessage.setVisibility(View.VISIBLE);
                return;
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.sign_in, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
