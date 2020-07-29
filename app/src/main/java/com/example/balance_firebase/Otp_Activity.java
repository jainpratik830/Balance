package com.example.balance_firebase;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.arch.core.executor.TaskExecutor;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskExecutors;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.concurrent.TimeUnit;

public class Otp_Activity extends AppCompatActivity {

    EditText editText_mobile, editText_otp;
    Button button_verify, button_otp;
    FirebaseAuth mAuth;
    String code;
    PhoneAuthProvider.ForceResendingToken token;
    ProgressDialog progressDialog;
    String mobileNumber,verificationID;
    PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;
    TextInputLayout input3;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_otp_);

        input3= findViewById(R.id.textinput3);
        editText_mobile = findViewById(R.id.editText_mobile);
        editText_otp=findViewById(R.id.editText_otp);
        button_otp=findViewById(R.id.button_otp);
        button_verify = findViewById(R.id.button_verify);
        mAuth = FirebaseAuth.getInstance();
        progressDialog = new ProgressDialog(this);

        button_verify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                code = editText_otp.getText().toString().trim();
                if (code.isEmpty() || code.length() < 6) {
                    editText_otp.setError("Enter valid code");
                    editText_otp.requestFocus();
                    return;
                }

                //verifying the code entered manually
                verifyVerificationCode(code);
            }
        });

        mCallbacks=new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {

                code=phoneAuthCredential.getSmsCode();

                if (code != null) {
                    editText_otp.setText(code);
                    //verifying the code
                }else {
                    code=editText_otp.getText().toString();
                }
                Log.i("CODE",code);
                verifyVerificationCode( code);

            }

            @Override
            public void onVerificationFailed(FirebaseException e) {
                Toast.makeText(Otp_Activity.this, e.toString(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCodeSent(String s, PhoneAuthProvider.ForceResendingToken forceResendingToken) {

                super.onCodeSent(s, forceResendingToken);
                verificationID=s;
                token=forceResendingToken;
            }
        };

        button_otp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                button_otp.setVisibility(View.GONE);
                button_verify.setVisibility(View.VISIBLE);
                editText_otp.setVisibility(View.VISIBLE);
                input3.setVisibility(View.VISIBLE);
                mobileNumber= "+91"+editText_mobile.getText();
                Log.i("Except",mobileNumber);
                sendVerificationCode(mobileNumber);
            }
        });

    }



    public void sendVerificationCode(String mobile)
    {
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                mobile,60,TimeUnit.SECONDS, Otp_Activity.this,mCallbacks,token);
    }

    public void verifyVerificationCode( String code)
    {
        try {
            PhoneAuthCredential credential=PhoneAuthProvider.getCredential(verificationID,code);

            signInWithPhoneAuthCredential(credential);
        }catch (Exception e)
        {
            Log.i("Except",e.toString());
        }

    }

    public void signInWithPhoneAuthCredential(PhoneAuthCredential credential)
    {
        mAuth.signInWithCredential(credential).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful())
                {
                    Toast.makeText(Otp_Activity.this, "Log In Succesfull", Toast.LENGTH_SHORT).show();
                    Intent intent=new Intent(Otp_Activity.this,MainActivity.class);
                    startActivity(intent);
                    finish();
                }
                else {
                    Toast.makeText(Otp_Activity.this, "Error"+task.getException(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

}