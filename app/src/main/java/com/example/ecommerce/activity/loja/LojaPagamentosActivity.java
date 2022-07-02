package com.example.ecommerce.activity.loja;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.ecommerce.R;
import com.example.ecommerce.adapter.AdapterLojaPagamento;
import com.example.ecommerce.databinding.ActivityLojaPagamentosBinding;
import com.example.ecommerce.databinding.SnackbarDeleteFormaPagamentoBinding;
import com.example.ecommerce.helper.FirebaseHelper;
import com.example.ecommerce.helper.RecyclerRowMoveCallback;
import com.example.ecommerce.model.FormaPagamento;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.tsuryo.swipeablerv.SwipeLeftRightCallback;

import java.util.ArrayList;
import java.util.List;

public class LojaPagamentosActivity extends AppCompatActivity implements AdapterLojaPagamento.onClickListener {

    private ActivityLojaPagamentosBinding binding;
    private List<FormaPagamento> formaPagamentoList = new ArrayList<>();
    private AdapterLojaPagamento adapterLojaPagamento;
    private Snackbar snackbar;
    private SnackbarDeleteFormaPagamentoBinding snackbarBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLojaPagamentosBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        iniciaComponentes();
        configCliques();
        configRv();

    }

    @Override
    protected void onStart() {
        super.onStart();
        recuperaFormaPagamentos();
    }

    private void configRv() {
        binding.rvPagamentos.setLayoutManager(new LinearLayoutManager(getBaseContext()));
        binding.rvPagamentos.setHasFixedSize(true);
        adapterLojaPagamento = new AdapterLojaPagamento(R.layout.item_forma_pagamento_adapter,formaPagamentoList,this,getBaseContext());

        ItemTouchHelper.Callback callback = new RecyclerRowMoveCallback(adapterLojaPagamento);
        ItemTouchHelper touchHelper = new ItemTouchHelper(callback);
        touchHelper.attachToRecyclerView(binding.rvPagamentos);


        binding.rvPagamentos.setListener(new SwipeLeftRightCallback.Listener() {
            @Override
            public void onSwipedLeft(int position) {
            }

            @Override
            public void onSwipedRight(int position) {
                snackbarDeletaItem(position);
            }
        });


        binding.rvPagamentos.setAdapter(adapterLojaPagamento);


    }

    public void snackbarDeletaItem(int posicao) {
        LinearLayout.LayoutParams objLayoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        snackbar = Snackbar.make(binding.getRoot(), "", Snackbar.LENGTH_INDEFINITE).setAnimationMode(BaseTransientBottomBar.ANIMATION_MODE_SLIDE);
        // Get the Snackbar's layout view

        Snackbar.SnackbarLayout layout = (Snackbar.SnackbarLayout) snackbar.getView();

        // Set snackbar layout params

        layout.setPadding(0, 0, 0, 0);


        // Inflate our custom view
        snackbarBinding = SnackbarDeleteFormaPagamentoBinding.inflate(LayoutInflater.from(this));


        objLayoutParams.setMargins(0, 0, 0, 0);

        snackbarBinding.txtNome.setText(formaPagamentoList.get(posicao).getNome());
        // Configure our custom view
        snackbarBinding.btnNao.setOnClickListener(v -> {
            adapterLojaPagamento.notifyDataSetChanged();
            snackbar.dismiss();
        });

        snackbarBinding.btnSim.setOnClickListener(v -> {

            snackbarBinding.progressBar.setVisibility(View.VISIBLE);
            formaPagamentoList.get(posicao).remover();
            formaPagamentoList.remove(posicao);
            adapterLojaPagamento.notifyItemRemoved(posicao);
            snackbar.dismiss();
            if (formaPagamentoList.isEmpty()) binding.txtInfo.setText("Nenhuma forma de pagamento cadastrada.");
        });

        // Add the view to the Snackbar's layout
        layout.addView(snackbarBinding.getRoot(), objLayoutParams);
        // Show the Snackbar
        snackbar.show();
    }

    private void recuperaFormaPagamentos() {
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

    private void configCliques() {
        binding.include8.imbAdd.setOnClickListener(v -> startActivity(new Intent(this, LojaFormPagamentoActivity.class)));
    }

    private void iniciaComponentes() {
        binding.include8.txtTitulo.setText("Formas de pagamento");
        binding.include8.include.ibVoltar.setOnClickListener(v -> finish());

    }

    @Override
    public void onClick(FormaPagamento formaPagamento) {
        Intent intent = new Intent(this, LojaFormPagamentoActivity.class);
        intent.putExtra("formaPagamentoSelecionada", formaPagamento);
        startActivity(intent);
    }
}