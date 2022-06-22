package com.example.ecommerce.fragment.loja;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.ecommerce.R;
import com.example.ecommerce.activity.loja.LojaFormProdutoActivity;
import com.example.ecommerce.adapter.AdapterProduto;
import com.example.ecommerce.databinding.FragmentProdutosLojaBinding;
import com.example.ecommerce.helper.FirebaseHelper;
import com.example.ecommerce.model.Categoria;
import com.example.ecommerce.model.Produto;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class ProdutosLojaFragment extends Fragment implements AdapterProduto.onClickListener {

    private FragmentProdutosLojaBinding binding;

    private AdapterProduto adapterProduto;
    private final List<Produto> produtoList = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentProdutosLojaBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        configCliques();
        configRV();
        recuperaProdutos();
    }

    private void recuperaProdutos() {
        DatabaseReference produtosRef = FirebaseHelper.getDatabaseReference()
                .child("produtos");
        produtosRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                produtoList.clear();
                if (snapshot.exists()) {
                    for (DataSnapshot ds : snapshot.getChildren()) {
                        produtoList.add(ds.getValue(Produto.class));
                    }
                    binding.txtInfo.setText("");
                } else {
                    binding.txtInfo.setText("Nenhum produto cadastrado.");
                }
                binding.progressBar.setVisibility(View.GONE);
                adapterProduto.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                binding.progressBar.setVisibility(View.GONE);
                binding.txtInfo.setText("Erro ao carregar página");
            }
        });
    }

    private void configRV() {
        binding.rvProdutos.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.rvProdutos.setHasFixedSize(true);
        adapterProduto = new AdapterProduto(produtoList, getContext(), this);
        binding.rvProdutos.setAdapter(adapterProduto);
    }

    private void configCliques() {
        binding.toolbar.imbAdd.setOnClickListener(v -> startActivity(new Intent(requireActivity(), LojaFormProdutoActivity.class)));
    }

    @Override
    public void onClicK(Produto produto) {
        Intent intent = new Intent(requireActivity(), LojaFormProdutoActivity.class);
        intent.putExtra("produtoSelecionado", produto.getId());
        startActivity(intent);
    }
}