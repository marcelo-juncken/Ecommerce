package com.example.ecommerce.activity.usuario;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.ecommerce.R;
import com.example.ecommerce.adapter.AdapterLojaPagamento;
import com.example.ecommerce.databinding.ActivityUsuarioSelecionaPagamentoBinding;
import com.example.ecommerce.helper.FirebaseHelper;
import com.example.ecommerce.model.FormaPagamento;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class UsuarioSelecionaPagamentoActivity extends AppCompatActivity implements AdapterLojaPagamento.onClickListener {

    private ActivityUsuarioSelecionaPagamentoBinding binding;
    private List<FormaPagamento> formaPagamentoList = new ArrayList<>();

    private FormaPagamento formaPagamentoEscolhida = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityUsuarioSelecionaPagamentoBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        configCliques();
        configRv();
        recuperaPagamentos();
    }

    private AdapterLojaPagamento adapterLojaPagamento;


    private void recuperaPagamentos() {
        formaPagamentoList.clear();
        if (FirebaseHelper.getAutenticado()) {
            DatabaseReference enderecosRef = FirebaseHelper.getDatabaseReference()
                    .child("formapagamento");
            enderecosRef.orderByChild("posicao").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        for (DataSnapshot ds : snapshot.getChildren()) {
                            FormaPagamento formaPagamento = ds.getValue(FormaPagamento.class);
                            if (formaPagamento != null) {
                                formaPagamentoList.add(0, formaPagamento);
                            }
                        }
                        binding.txtInfo.setText("");
                    } else {
                        binding.txtInfo.setText("Nenhuma forma de pagamento cadastrada");
                    }
                    binding.progressBar.setVisibility(View.GONE);
                    adapterLojaPagamento.notifyDataSetChanged();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }
    }

    private void configRv() {
        binding.rvPagamentos.setLayoutManager(new LinearLayoutManager(getBaseContext()));
        binding.rvPagamentos.setHasFixedSize(true);
        adapterLojaPagamento = new AdapterLojaPagamento(R.layout.item_pagamento_pedido_adapter,formaPagamentoList, this, getBaseContext());

        binding.rvPagamentos.setAdapter(adapterLojaPagamento);


    }

    private void configCliques() {
        binding.include7.txtTitulo.setText("Escolha a forma de pagamento");
        binding.include7.include.ibVoltar.setOnClickListener(v -> finish());

        binding.btnContinuar.setOnClickListener(v -> {
            if(formaPagamentoEscolhida!=null){
                Intent intent = new Intent(this, UsuarioResumoPedidoActivity.class);
                intent.putExtra("formaPagamentoSelecionada", formaPagamentoEscolhida);
                startActivity(intent);
            } else {
                Toast.makeText(this, "Selecione a forma de pagamento.", Toast.LENGTH_SHORT).show();
            }
        });
    }


    @Override
    public void onClick(FormaPagamento formaPagamento) {
        formaPagamentoEscolhida = formaPagamento;
    }
}