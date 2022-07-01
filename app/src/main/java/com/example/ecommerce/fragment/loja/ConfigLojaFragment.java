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
import com.example.ecommerce.activity.loja.LojaConfigActivity;
import com.example.ecommerce.activity.loja.LojaPagamentosActivity;
import com.example.ecommerce.activity.loja.LojaRecebimentosActivity;
import com.example.ecommerce.activity.usuario.MainActivityUsuario;
import com.example.ecommerce.databinding.FragmentConfigLojaBinding;
import com.example.ecommerce.helper.FirebaseHelper;

public class ConfigLojaFragment extends Fragment {

    private FragmentConfigLojaBinding binding;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentConfigLojaBinding.inflate(inflater,container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        configCliques();
    }

    private void configCliques() {
        binding.lLConfig.setOnClickListener(v -> startActivity(new Intent(requireActivity(), LojaConfigActivity.class)));
        binding.lLPagamentos.setOnClickListener(v -> startActivity(new Intent(requireActivity(), LojaPagamentosActivity.class)));

        binding.lLSair.setOnClickListener(v -> {
            FirebaseHelper.getAuth().signOut();
            requireActivity().finish();
            startActivity(new Intent(requireActivity(), MainActivityUsuario.class));
        });
    }
}