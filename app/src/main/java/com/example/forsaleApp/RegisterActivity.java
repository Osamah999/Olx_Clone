package com.example.forsaleApp;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;
import org.jetbrains.annotations.NotNull;
import java.util.Objects;

public class RegisterActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private Button CreateAccountButton;
    private EditText InputName, InputEmail, InputPassword;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mAuth = FirebaseAuth.getInstance();

        CreateAccountButton = (Button) findViewById(R.id.register_btn);
        InputName = (EditText) findViewById(R.id.register_username_input);
        InputEmail = (EditText) findViewById(R.id.register_Email_input);
        InputPassword = (EditText) findViewById(R.id.register_password_input);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);


        CreateAccountButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CreateAccount();
            }
        });
    }
    private void CreateAccount()
    {
        String name = InputName.getText().toString();
        String email = InputEmail.getText().toString();
        String password = InputPassword.getText().toString();

        if (TextUtils.isEmpty(name))
        {
            InputName.setError("Please write your name!");
            InputName.requestFocus();
        }
        else if (TextUtils.isEmpty(email))
        {
            InputEmail.setError("Please write your Email address!");
            InputEmail.requestFocus();
        }
        else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches())
        {
            InputEmail.setError("Please provide valid Email Address!");
            InputEmail.requestFocus();
        }
        else if (TextUtils.isEmpty(password))
        {
            InputPassword.setError("Please write your password!");
            InputPassword.requestFocus();
        }
        else if (password.length() < 6)
        {
            InputPassword.setError("Min password length should be 6 characters!");
            InputPassword.requestFocus();
        }
        else
        {

            progressBar.setVisibility(View.VISIBLE);
            mAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NotNull  Task<AuthResult> task) {

                            if (task.isSuccessful())
                            {
                                User user = new User(name,email,password);

                                FirebaseDatabase.getInstance().getReference("users")
                                        .child(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid())
                                        .setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NotNull Task<Void> task) {

                                        if (task.isSuccessful())
                                        {
                                            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                                            user.sendEmailVerification();
                                            Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
                                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                            startActivity(intent);
                                            Toast.makeText(RegisterActivity.this, "You have been registered successfully, check your email to verify your account", Toast.LENGTH_LONG).show();
                                        }
                                        else
                                        {
                                            Toast.makeText(RegisterActivity.this, "something wrong happened!", Toast.LENGTH_LONG).show();
                                            progressBar.setVisibility(View.GONE);
                                        }

                                    }
                                });
                            }

                            else
                            {
                                Toast.makeText(RegisterActivity.this, "This email already exists!", Toast.LENGTH_LONG).show();
                                progressBar.setVisibility(View.GONE);
                            }

                        }
                    });
        }
    }
}
