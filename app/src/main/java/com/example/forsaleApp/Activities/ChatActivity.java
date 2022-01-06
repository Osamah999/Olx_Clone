package com.example.forsaleApp.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.forsaleApp.Adapters.AdapterProductSeller;
import com.example.forsaleApp.Adapters.MessageAdapter;
import com.example.forsaleApp.ModelProduct;
import com.example.forsaleApp.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatActivity extends AppCompatActivity {

    private CircleImageView Profile_Image;
    private TextView User_Name;
    private ImageButton Send_btn;
    private EditText Type_Massage;

    private String UserId, UserName;

    public MessageAdapter messageAdapter;
    public ArrayList<ModelProduct> productList;
    public RecyclerView recyclerView;

    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        Profile_Image = findViewById(R.id.profile_image);
        User_Name = findViewById(R.id.user_name);
        Type_Massage = findViewById(R.id.type_message);
        Send_btn = findViewById(R.id.send_btn);
        recyclerView = findViewById(R.id.recycler_view);

        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(linearLayoutManager);

        firebaseAuth = FirebaseAuth.getInstance();

        UserId = getIntent().getStringExtra("UserId");
        UserName = getIntent().getStringExtra("UserName");

        User_Name.setText(UserName);

        Send_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                String msg = Type_Massage.getText().toString();
                if (!msg.equals(""))
                {
                    sendMessage();
                }
                else
                {
                    Toast.makeText(ChatActivity.this, "You can't sent empty message!!", Toast.LENGTH_SHORT).show();
                }
                Type_Massage.setText("");
            }
        });
        readMessage();
    }

    private void sendMessage()
    {
        String Message = Type_Massage.getText().toString();
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("Message", "" + Message);
        hashMap.put("Sender", "" + firebaseAuth.getUid());
        hashMap.put("Receiver", "" + UserId);

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        reference.child("Chat").push().setValue(hashMap);

    }

    private void readMessage()
    {
        productList = new ArrayList<>();

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Chat");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                productList.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren())
                {
                    ModelProduct modelProduct = dataSnapshot.getValue(ModelProduct.class);
                    if (modelProduct.getReceiver().equals(firebaseAuth.getUid()) && modelProduct.getSender().equals(UserId) ||
                            modelProduct.getReceiver().equals(UserId) && modelProduct.getSender().equals(firebaseAuth.getUid()))
                    {
                        productList.add(modelProduct);
                    }
                }
                messageAdapter = new MessageAdapter(ChatActivity.this, productList);
                recyclerView.setAdapter(messageAdapter);
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });
    }
}