package com.example.forsaleApp.Fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.forsaleApp.Activities.MainActivity;
import com.example.forsaleApp.Activities.forgot_password;
import com.example.forsaleApp.R;
import com.example.forsaleApp.User;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.jetbrains.annotations.NotNull;


public class AccountFragment extends Fragment {

    private TextInputEditText userNameD,userEmailD;
    private TextView userNameU,userEmailU, logout, resetPassword;
    private FirebaseUser user;
    private DatabaseReference reference;
    private String userID;
    private ProgressBar progressBar;

    public AccountFragment(){

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        final View view =  inflater.inflate(R.layout.fragment_account, container, false);

        logout = view.findViewById(R.id.logout_txt);
        resetPassword = view.findViewById(R.id.reset_txt);
        userNameD = view.findViewById(R.id.user_name_down);
        userEmailD = view.findViewById(R.id.user_email_down);
        userNameU = view.findViewById(R.id.user_name_up);
        userEmailU = view.findViewById(R.id.user_email_up);
        progressBar = view.findViewById(R.id.progressBar);


        user = FirebaseAuth.getInstance().getCurrentUser();
        reference = FirebaseDatabase.getInstance().getReference("users");
        userID = user.getUid();

        progressBar.setVisibility(View.VISIBLE);
        reference.child(userID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                User userprofile = snapshot.getValue(User.class);

                if (userprofile != null)
                {
                    String fullName = userprofile.name;
                    String email = userprofile.email;
                    userNameU.setText(fullName);
                    userEmailU.setText(email);
                    userNameD.setText(fullName);
                    userEmailD.setText(email);
                    progressBar.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });

        resetPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), forgot_password.class);
                startActivity(intent);
            }
        });


        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(getActivity(), MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }
        });


        return view;
    }
}