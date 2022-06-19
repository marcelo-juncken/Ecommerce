package com.example.ecommerce.activity.app;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.core.view.WindowInsetsControllerCompat;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.example.ecommerce.activity.loja.MainActivityLoja;
import com.example.ecommerce.activity.usuario.MainActivityUsuario;
import com.example.ecommerce.R;
import com.example.ecommerce.autenticacao.LoginActivity;
import com.example.ecommerce.helper.FirebaseHelper;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        hideSystemBars();

        //FirebaseHelper.getAuth().signOut();
        new Handler(getMainLooper()).postDelayed(this::verificaAcesso, 3000);
    }

    private void verificaAcesso() {
        if (FirebaseHelper.getAutenticado()) {
            recuperaAcesso();
        } else {
            finish();
            startActivity(new Intent(this, LoginActivity.class));
        }

    }

    private void recuperaAcesso(){
        DatabaseReference usuarioRef = FirebaseHelper.getDatabaseReference()
                .child("usuarios")
                .child(FirebaseHelper.getIdFirebase());
        usuarioRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    startActivity(new Intent(getBaseContext(), MainActivityUsuario.class));
                }else{
                    startActivity(new Intent(getBaseContext(), MainActivityLoja.class));
                }
                finish();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void hideSystemBars() {
        WindowInsetsControllerCompat windowInsetsController =
                ViewCompat.getWindowInsetsController(getWindow().getDecorView());
        if (windowInsetsController == null) {
            return;
        }
        // Configure the behavior of the hidden system bars
        windowInsetsController.setSystemBarsBehavior(
                WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        );
        // Hide both the status bar and the navigation bar
        windowInsetsController.hide(WindowInsetsCompat.Type.systemBars());
    }


}