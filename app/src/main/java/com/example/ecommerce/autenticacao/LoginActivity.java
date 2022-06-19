package com.example.ecommerce.autenticacao;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.ecommerce.R;
import com.example.ecommerce.activity.loja.MainActivityLoja;
import com.example.ecommerce.activity.usuario.MainActivityUsuario;
import com.example.ecommerce.databinding.ActivityLoginBinding;
import com.example.ecommerce.helper.FirebaseHelper;
import com.example.ecommerce.model.Usuario;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

public class LoginActivity extends AppCompatActivity {

    private ActivityLoginBinding binding;

    private final ActivityResultLauncher<Intent> resultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK) {
                    if (result.getData() != null) {
                        String email = result.getData().getStringExtra("email");
                        binding.edtEmail.setText(email);
                    }
                }
            }
    );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        configCliques();
    }


    private void validaDados() {
        String email = binding.edtEmail.getText().toString().trim();
        String senha = binding.edtSenha.getText().toString().trim();

        if (!email.isEmpty()) {
            if (!senha.isEmpty()) {

                ocultarTeclado();
                binding.progressBar.setVisibility(View.VISIBLE);
                binding.btnLogar.setEnabled(false);

                login(email, senha);

            } else {
                binding.edtSenha.requestFocus();
                binding.edtSenha.setError("Informe sua senha.");
            }
        } else {
            binding.edtEmail.requestFocus();
            binding.edtEmail.setError("Informe seu email.");
        }


    }

    private void login(String email, String senha) {
        FirebaseHelper.getAuth().signInWithEmailAndPassword(
                email, senha
        ).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                recuperaUsuario(task.getResult().getUser().getUid());
            } else {
                Toast.makeText(this, FirebaseHelper.validaErros(task.getException().getMessage()), Toast.LENGTH_LONG).show();
                binding.btnLogar.setEnabled(true);
                binding.progressBar.setVisibility(View.GONE);
            }
        });
    }

    private void recuperaUsuario(String id) {
        DatabaseReference usuarioRef = FirebaseHelper.getDatabaseReference()
                .child("usuarios")
                .child(id);
        usuarioRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    startActivity(new Intent(getBaseContext(), MainActivityUsuario.class));
                } else {
                    startActivity(new Intent(getBaseContext(), MainActivityLoja.class));
                }
                finish();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void configCliques() {
        binding.imbVoltar.ibVoltar.setOnClickListener(v -> finish());

        binding.txtRecuperaSenha.setOnClickListener(v -> {
            ocultarTeclado();
            startActivity(new Intent(this, RecuperaContaActivity.class));
        });

        binding.txtCadastro.setOnClickListener(v -> {
            ocultarTeclado();
            Intent intent = new Intent(this, CadastroActivity.class);
            resultLauncher.launch(intent);
        });

        binding.btnLogar.setOnClickListener(v -> {
            validaDados();
        });
    }

    private void ocultarTeclado() {
        ((InputMethodManager) getSystemService(INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(
                binding.edtEmail.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS
        );
    }
}