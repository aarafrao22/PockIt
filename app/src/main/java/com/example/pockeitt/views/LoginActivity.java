package com.example.pockeitt.views;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.pockeitt.R;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

public class LoginActivity extends AppCompatActivity {
    TextView create_acc;
    FirebaseAuth mauth;
    FirebaseDatabase database;
    Button google_btn;
    GoogleSignInClient gsc;
    GoogleSignInOptions gso;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);
        create_acc = findViewById(R.id.create_acc);
        google_btn = findViewById(R.id.button);

        mauth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail().build();
        gsc = GoogleSignIn.getClient(this, gso);

//        Intent i;
//        if(isFirstTime()){
//            i = new Intent(LoginActivity.this, SecuritypinActivity.class);
//
//        }else{
//            i = new Intent(LoginActivity.this, EnteredPinActivity.class);
//
//        }
//        startActivity(i);
//        finish();

        GoogleSignInAccount acct = GoogleSignIn.getLastSignedInAccount(this);
        if (acct != null) {
            navigateToSecondActivity();
        }

        google_btn.setOnClickListener(v -> signIn());


        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        create_acc.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, SignUpActivity.class);
            startActivity(intent);
            finish();
        });
    }


//    private boolean isFirstTime() {
//
//        return false;
//    }

    private void signIn() {

//        TODO
//        Intent signInIntent = gsc.getSignInIntent();
//        startActivityForResult(signInIntent, 1000);
        startActivity(new Intent(getApplicationContext(), HomeActivity.class));
        finish();


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1000) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);

            try {
                task.getResult(ApiException.class);
                navigateToSecondActivity();
            } catch (ApiException e) {
                Toast.makeText(getApplicationContext(), "Something went wrong", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void navigateToSecondActivity() {
        finish();
        Intent intent = new Intent(LoginActivity.this, BudgetCurrencyActivity.class);
        startActivity(intent);
    }

}
