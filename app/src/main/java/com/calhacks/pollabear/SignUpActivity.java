package com.calhacks.pollabear;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.calhacks.pollabear.models.UserModel;


public class SignUpActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        Button signinBtn = (Button)findViewById(R.id.signin_btn);
        final EditText emailTxt = (EditText) findViewById(R.id.email);
        final EditText passwordTxt = (EditText) findViewById(R.id.password);
        final EditText firstNameTxt = (EditText) findViewById(R.id.firstName);
        final EditText lastNameTxt = (EditText) findViewById(R.id.lastName);

        signinBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = emailTxt.getText().toString();
                String password = passwordTxt.getText().toString();
                String firstName = firstNameTxt.getText().toString();
                String lastName = lastNameTxt.getText().toString();

                if(firstName.isEmpty()){firstNameTxt.setError("You forgot your First Name?!"); return;}
                if(lastName.isEmpty()){lastNameTxt.setError("You forgot your Last Name?!");return;}
                if(email.isEmpty()){emailTxt.setError("You forgot the email!"); return;}
                if(password.isEmpty()){passwordTxt.setError("You forgot the password!"); return;}

                if(UserModel.findUserInEmail(email) == null) {
                    //TODO:Add some loading icon here?
                    PollApplication.loggedInUser = UserModel.newUser(firstName,lastName,email,password);
                    Toast.makeText(getApplicationContext(),"Success!", Toast.LENGTH_LONG).show();

                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                    startActivity(intent);
                    //TODO: Put info to the data base
                }
                else{
                    emailTxt.setError("Sorry! Someone is using this email already.");
                    return;
                }
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.sign_up, menu);
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
