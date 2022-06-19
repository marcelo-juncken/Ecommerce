package com.example.ecommerce.autenticacao;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.ecommerce.R;
import com.example.ecommerce.databinding.ActivityRecuperaContaBinding;
import com.example.ecommerce.helper.FirebaseHelper;

public class RecuperaContaActivity extends AppCompatActivity {

    private ActivityRecuperaContaBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        binding = ActivityRecuperaContaBinding.inflate(getLayoutInflater());
        super.onCreate(savedInstanceState);

        setContentView(binding.getRoot());

        configCliques();
    }

    private void validaDados() {
        String email = binding.edtEmail.getText().toString().trim();

        if (!email.isEmpty()) {

                ocultarTeclado();
                binding.progressBar.setVisibility(View.VISIBLE);
                binding.btnRecuperaConta.setEnabled(false);

                recuperaConta(email);
        } else {
            binding.edtEmail.requestFocus();
            binding.edtEmail.setError("Informe seu email.");
        }
    }

    private void recuperaConta(String email) {
        FirebaseHelper.getAuth().sendPasswordResetEmail(
                email
        ).addOnCompleteListener(task -> {
           if(task.isSuccessful()){
               Toast.makeText(this, "Email enviado", Toast.LENGTH_LONG).show();
           }else{
               Toast.makeText(this, FirebaseHelper.validaErros(task.getException().getMessage()), Toast.LENGTH_LONG).show();
           }
            binding.btnRecuperaConta.setEnabled(true);
            binding.progressBar.setVisibility(View.GONE);
        });
    }

    private void configCliques() {
        binding.btnRecuperaConta.setOnClickListener(v -> validaDados());
        binding.imbVoltar.ibVoltar.setOnClickListener(v -> finish());
    }

    private void ocultarTeclado() {
        ((InputMethodManager) getSystemService(INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(
                binding.edtEmail.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS
        );
    }
}