package com.example.forsaleApp.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.forsaleApp.R;
import com.example.forsaleApp.Utility.NetworkChangeListener;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import org.jetbrains.annotations.NotNull;



public class LoginActivity extends AppCompatActivity {

    private EditText email_input,password_input;
    private Button login_btn;
    private FirebaseAuth mAuth;
    private ProgressBar progressBar;
    private TextView forgotpassword;

    NetworkChangeListener networkChangeListener = new NetworkChangeListener();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);


        login_btn = (Button) findViewById(R.id.login_btn);
        email_input = (EditText) findViewById(R.id.login_Email_input);
        password_input = (EditText) findViewById(R.id.login_password_input);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        forgotpassword = (TextView) findViewById(R.id.forgot_password);

        mAuth = FirebaseAuth.getInstance();

        forgotpassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity.this, forgot_password.class);
                startActivity(intent);
            }
        });

        login_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Login();

            }
        });
    }

    public void Login()
    {
        String email = email_input.getText().toString();
        String password = password_input.getText().toString();

        if (TextUtils.isEmpty(email))
        {
            email_input.setError("Please write your Email address!");
            email_input.requestFocus();
        }
        else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches())
        {
            email_input.setError("Please provide valid Email Address!");
            email_input.requestFocus();
        }
        else if (TextUtils.isEmpty(password))
        {
            password_input.setError("Please write your password!");
            password_input.requestFocus();
        }
        else
        {
            progressBar.setVisibility(View.VISIBLE);
            mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull @NotNull Task<AuthResult> task) {

                    if (task.isSuccessful())
                    {
                        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                        if (user.isEmailVerified())
                        {
                            Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);

                        }
                        else
                        {
                            user.sendEmailVerification();
                            Toast.makeText(LoginActivity.this, "Check your email to verify your account!", Toast.LENGTH_LONG).show();
                        }


                    }
                    else
                    {
                        Toast.makeText(LoginActivity.this, "Failed to login! please check your credentials", Toast.LENGTH_SHORT).show();
                    }
                    progressBar.setVisibility(View.GONE);
                }
            });
        }


    }

    @Override
    protected void onStart() {
        IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(networkChangeListener, filter);
        super.onStart();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (mAuth.getCurrentUser() != null) {
            assert user != null;
            if (user.isEmailVerified())
            {
                startActivity(new Intent(this, HomeActivity.class));
                finish();

            }
        }
    }

    @Override
    protected void onStop() {
        unregisterReceiver(networkChangeListener);
        super.onStop();
    }
}