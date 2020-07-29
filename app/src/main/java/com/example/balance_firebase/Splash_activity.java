package com.example.balance_firebase;

import android.app.ActionBar;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.dynamiclinks.DynamicLink;
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks;
import com.google.firebase.dynamiclinks.PendingDynamicLinkData;
import com.google.firebase.dynamiclinks.ShortDynamicLink;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.Calendar;
import java.util.Date;
import java.util.Random;
import java.util.TimeZone;

public class Splash_activity extends AppCompatActivity {

    SharedPreferences sharedPreferences;
    FirebaseAuth firebaseAuth;
    static Uri InviteLink;
    String uid;
    static boolean userAdd=false;
    static String addUid;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        getWindow().requestFeature(Window.FEATURE_NO_TITLE);
//        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_splash_activity);

        firebaseAuth=FirebaseAuth.getInstance();
        sharedPreferences=getSharedPreferences("FIRENOTESDATA",MODE_PRIVATE);


// Remember that you should never show the action bar if the
// status bar is hidden, so hide that too if necessary.
//        ActionBar actionBar = getActionBar();
//        actionBar.hide();


        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                if (firebaseAuth.getCurrentUser() != null) {
                    Intent intent = new Intent(Splash_activity.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                } else {
                    Intent intent = new Intent(Splash_activity.this, Login_activity.class);
                    startActivity(intent);
                    finish();
                }
            }
        },1500);


//        Log.i("CURRENT",firebaseAuth.getCurrentUser().getUid());

        if ((InviteLink == null && firebaseAuth.getCurrentUser() != null) ){
            Task<ShortDynamicLink> shortLinkTask = FirebaseDynamicLinks.getInstance().createDynamicLink()
                    .setLink(Uri.parse("https://www.balancefirebase.com/"+"?invitedby="+firebaseAuth.getCurrentUser().getUid()))
////                .setLink(Uri.parse("https://www.google.com/"));
                    .setDomainUriPrefix("https://balancefirebase.page.link")
                    // Set parameters
                    // ...
                    .setAndroidParameters(new DynamicLink.AndroidParameters.Builder().build())
                    // Open links with com.example.ios on iOS
                    .setIosParameters(new DynamicLink.IosParameters.Builder("com.example.ios").build())
                    .buildShortDynamicLink()
                    .addOnCompleteListener(this, new OnCompleteListener<ShortDynamicLink>() {
                        @Override
                        public void onComplete(@NonNull Task<ShortDynamicLink> task) {
                            if (task.isSuccessful()) {
                                // Short link created
                                Uri shortLink = task.getResult().getShortLink();
                                Uri flowchartLink = task.getResult().getPreviewLink();
                                InviteLink=shortLink;
                                Log.i("Dynamic",shortLink.toString());
                                Log.i("Dynamic2",flowchartLink.toString());
                            } else {
                                // Error
                                // ...
                            }

                        }
                    });
        }






        FirebaseDynamicLinks.getInstance()
                .getDynamicLink(getIntent())
                .addOnSuccessListener(this, new OnSuccessListener<PendingDynamicLinkData>() {
                    @Override
                    public void onSuccess(PendingDynamicLinkData pendingDynamicLinkData) {
                        // Get deep link from result (may be null if no link is found)
                        Uri deepLink = null;
                        if (pendingDynamicLinkData != null) {
                            deepLink = pendingDynamicLinkData.getLink();
                            Log.i("LINK",deepLink.getQueryParameter("invitedby"));
                        }

                        //
                        // If the user isn't signed in and the pending Dynamic Link is
                        // an invitation, sign in the user anonymously, and record the
                        // referrer's UID.
                        //
                        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                        if (user != null
                                && deepLink != null
                                && deepLink.getBooleanQueryParameter("invitedby", false)) {
                            String referrerUid = deepLink.getQueryParameter("invitedby");
                            if (!firebaseAuth.getCurrentUser().getUid().equals(referrerUid)){
                                addUid=referrerUid;
                                userAdd=true;
                            }
//                            createAnonymousAccountWithReferrerInfo(referrerUid);
                        }
                    }
                });



}



//    public void invites=FirebaseDynamicLinks.getInstance()
//            .getDynamicLink(getIntent())
//            .addOnSuccessListener(MainActivity.this, new OnSuccessListener<PendingDynamicLinkData>() {
//                @Override
//                public void onSuccess(PendingDynamicLinkData pendingDynamicLinkData) {
//                    // Get deep link from result (may be null if no link is found)
//                    Uri deepLink = null;
//                    if (pendingDynamicLinkData != null) {
//                        deepLink = pendingDynamicLinkData.getLink();
//                    }
//
//                    Log.i("LINK",deepLink.toString());
//                    // Handle the deep link. For example, open the linked
//                    // content, or apply promotional credit to the user's
//                    // account.
//                    // ...
//
//                    // ...
//                }
//            })
//            .addOnFailureListener(MainActivity.this, new OnFailureListener() {
//                @Override
//                public void onFailure(@NonNull Exception e) {
//                    Log.w("tag", "getDynamicLink:onFailure", e);
//                }
//            });




    public static final SimpleDateFormat DATE_FORMAT_1 = new SimpleDateFormat("dd/M/yyyy hh:mm:ss");

    public static Date getCurrentDate() {
//        SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT_1);
        DATE_FORMAT_1.setTimeZone(TimeZone.getTimeZone("Asia/Kolkata"));
        Date today = Calendar.getInstance().getTime();
//        Calendar.getInstance().getTime()
//        LocalDateTime date=LocalDateTime.now();
//        Date date = new Date();
//        Log.i("DATE",dateFormat.format(today));
//        Date date = null;
//        date
//        DATE_FORMAT_1.parse(date);
//        try {
//            date = DATE_FORMAT_1.parse(today);
            Log.i("DATESP",today.toString());
//        } catch (ParseException e) {
//            e.printStackTrace();
//        }
//        Log.i("DATE",dateFormat.format(today));
        return today;
    }

    public static String getCurrentDate3() {
//        SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT_1);
        DATE_FORMAT_1.setTimeZone(TimeZone.getTimeZone("Asia/Kolkata"));
        Date today = Calendar.getInstance().getTime();
//        Calendar.getInstance().getTime()
//        LocalDateTime date=LocalDateTime.now();
//        Date date = new Date();
//        Log.i("DATE",dateFormat.format(today));
//        Date date = null;
//        date
//        DATE_FORMAT_1.parse(date);
//        try {
//            date = DATE_FORMAT_1.parse(today);
        Log.i("DATESP",today.toString());
//        } catch (ParseException e) {
//            e.printStackTrace();
//        }
//        Log.i("DATE",dateFormat.format(today));
        return today.toString();
    }

    public static final String DATE_FORMAT_2 = "dd-M-yyyy hh:mm:ss";

    public static String getCurrentDate2() {
        SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT_2);
        dateFormat.setTimeZone(TimeZone.getTimeZone("Asia/Kolkata"));
        Date today = Calendar.getInstance().getTime();
        Log.i("DATE",dateFormat.format(today));

        Log.i("DATE",dateFormat.format(today));
        return dateFormat.format(today);
    }




}
