package com.example.ecommerce.activity.usuario;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.example.ecommerce.databinding.ActivityUsuarioPagamentoPedidoBinding;

public class UsuarioPagamentoPedidoActivity extends AppCompatActivity {

    private ActivityUsuarioPagamentoPedidoBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityUsuarioPagamentoPedidoBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
    }
}