package com.example.ecommerce.activity.loja;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.InputType;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import com.example.ecommerce.databinding.ActivityLojaRecebimentosBinding;
import com.example.ecommerce.helper.FirebaseHelper;
import com.example.ecommerce.model.Loja;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

public class LojaRecebimentosActivity extends AppCompatActivity {

    private ActivityLojaRecebimentosBinding binding;

    private Loja loja;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLojaRecebimentosBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        recuperaLoja();
        configCliques();
    }

    private void recuperaLoja() {
        DatabaseReference lojaRef = FirebaseHelper.getDatabaseReference()
                .child("loja");
        lojaRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    loja = snapshot.getValue(Loja.class);
                    configLoja();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void configLoja() {
        binding.edtPublicKey.setText(loja.getPublicKey());
        binding.edtAccessToken.setText(loja.getAccessToken());
        if (loja.getParcelas() != 0) {
            binding.edtParcelas.setText(String.valueOf(loja.getParcelas()));
        }
        statusButton(true);
    }

    private void validaDados() {
        String publicKey = binding.edtPublicKey.getText().toString().trim();
        String accessToken = binding.edtAccessToken.getText().toString().trim();

        String parcelasStr = binding.edtParcelas.getText().toString().trim();
        int parcelas = 0;
        if (!parcelasStr.isEmpty()) {
            parcelas = Integer.parseInt(parcelasStr);
        }

        if (!publicKey.isEmpty()) {
            if (!accessToken.isEmpty()) {
                if (parcelas >= 1 && parcelas <= 12) {
                    ocultarTeclado();
                    statusButton(false);
                    loja.setPublicKey(publicKey);
                    loja.setAccessToken(accessToken);
                    loja.setParcelas(parcelas);
                    loja.salvar();
                    statusButton(true);

                } else {
                    binding.edtParcelas.requestFocus();
                    binding.edtParcelas.setError("A parcela deve ser um número inteiro entre 1 e 12");
                }
            } else {
                binding.edtAccessToken.requestFocus();
                binding.edtAccessToken.setError("Informe o Access Token");
            }
        } else {
            binding.edtPublicKey.requestFocus();
            binding.edtPublicKey.setError("Informe a Public Key");
        }
    }

    private void statusButton(boolean isEnabled) {
        binding.btnSalvar.setEnabled(isEnabled);
        if (isEnabled) {
            binding.progressBar.setVisibility(View.GONE);
        } else {
            binding.progressBar.setVisibility(View.VISIBLE);
        }
    }

    private void configCliques() {
        binding.btnSalvar.setOnClickListener(v -> {
            if (loja != null) {
                validaDados();
            } else {
                Toast.makeText(this, "Espere o carregamento da página", Toast.LENGTH_SHORT).show();
            }
        });

        binding.include2.include.ibVoltar.setOnClickListener(v -> finish());
        binding.include2.txtTitulo.setText("Recebimentos");


        binding.imgShow.setOnClickListener(v -> {
            binding.edtAccessToken.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
            binding.edtAccessToken.setSelection(binding.edtAccessToken.getText().length());
            binding.imgShow.setVisibility(View.GONE);
            binding.imgHide.setVisibility(View.VISIBLE);
        });


        binding.imgHide.setOnClickListener(v -> {
            binding.edtAccessToken.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
            binding.edtAccessToken.setSelection(binding.edtAccessToken.getText().length());
            binding.imgShow.setVisibility(View.VISIBLE);
            binding.imgHide.setVisibility(View.GONE);
        });

    }

    private void ocultarTeclado() {
        ((InputMethodManager) getSystemService(INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(
                binding.edtParcelas.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS
        );
    }
}