package com.example.ecommerce.fragment.loja;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.ecommerce.R;
import com.example.ecommerce.activity.loja.LojaFormProdutoActivity;
import com.example.ecommerce.databinding.FragmentProdutosLojaBinding;

public class ProdutosLojaFragment extends Fragment {

    FragmentProdutosLojaBinding binding;

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
    }

    private void configCliques() {
        binding.toolbar.imbAdd.setOnClickListener(v -> startActivity(new Intent(requireActivity(), LojaFormProdutoActivity.class)));
    }
}