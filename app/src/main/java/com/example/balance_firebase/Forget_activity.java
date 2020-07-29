package com.example.balance_firebase;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class Forget_activity extends AppCompatActivity {

    EditText editText_email_forget;
    Button button_forgetPass;
    FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forget_activity);

        editText_email_forget=findViewById(R.id.editText_email_forget);
        button_forgetPass=findViewById(R.id.button_forgetPass);
        firebaseAuth=FirebaseAuth.getInstance();

        button_forgetPass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email=editText_email_forget.getText().toString();
                if(!email.equalsIgnoreCase(""))
                {
                    firebaseAuth.sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful())
                            {
                                Toast.makeText(Forget_activity.this, "Please check your email for resetting Password", Toast.LENGTH_SHORT).show();
                                Intent intent=new Intent(Forget_activity.this,Login_activity.class);
                                startActivity(intent);
                                finish();
                            }else {
                                Toast.makeText(Forget_activity.this, "Error resetting password", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }else {
                    Toast.makeText(Forget_activity.this, "Enter a valid Email", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }


}
