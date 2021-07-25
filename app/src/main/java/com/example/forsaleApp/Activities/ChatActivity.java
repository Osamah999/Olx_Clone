package com.example.forsaleApp.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.forsaleApp.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatActivity extends AppCompatActivity {

    private CircleImageView Profile_Image;
    private TextView User_Name;
    private ImageButton Send_btn;
    private EditText Type_Massage;

    private String UserId, UserName;

    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        Profile_Image = findViewById(R.id.profile_image);
        User_Name = findViewById(R.id.user_name);
        Type_Massage = findViewById(R.id.type_message);
        Send_btn = findViewById(R.id.send_btn);

        firebaseAuth = FirebaseAuth.getInstance();

        UserId = getIntent().getStringExtra("UserId");
        UserName = getIntent().getStringExtra("UserName");

        User_Name.setText(UserName);

        Send_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {



                    sendMessage();

            }
        });
    }

    private void sendMessage()
    {

        String Message = Type_Massage.getText().toString();
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("Message", "" + Message);

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("users");
        reference.child(firebaseAuth.getUid()).child("Chat").child(UserId).setValue(hashMap);

    }
}