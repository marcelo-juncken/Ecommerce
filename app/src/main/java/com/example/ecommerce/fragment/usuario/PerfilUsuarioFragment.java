package com.example.ecommerce.fragment.usuario;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.ecommerce.R;
import com.example.ecommerce.activity.usuario.UsuarioEnderecoActivity;
import com.example.ecommerce.autenticacao.CadastroActivity;
import com.example.ecommerce.autenticacao.LoginActivity;
import com.example.ecommerce.databinding.FragmentPerfilUsuarioBinding;
import com.example.ecommerce.helper.FirebaseHelper;


public class PerfilUsuarioFragment extends Fragment {

    private FragmentPerfilUsuarioBinding binding;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentPerfilUsuarioBinding.inflate(inflater, container, false);

        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        configDados();
        configCliques();
    }

    private void configDados() {
        if (FirebaseHelper.getAutenticado()) {
            binding.llLogin.setVisibility(View.GONE);
            binding.lLSair.setVisibility(View.VISIBLE);
        } else {
            binding.llLogin.setVisibility(View.VISIBLE);
            binding.lLSair.setVisibility(View.GONE);

        }
    }

    private void configCliques() {
        binding.btnEntrar.setOnClickListener(v -> startActivity(new Intent(requireActivity(),LoginActivity.class)));
        binding.btnCadastrar.setOnClickListener(v -> startActivity(new Intent(requireActivity(),CadastroActivity.class)));

        binding.lLEnderecos.setOnClickListener(v -> startActivity(UsuarioEnderecoActivity.class));
        binding.lLPerfil.setOnClickListener(v -> startActivity(UsuarioEnderecoActivity.class));

        binding.lLSair.setOnClickListener(v -> {
            binding.llLogin.setVisibility(View.VISIBLE);
            FirebaseHelper.getAuth().signOut();
            binding.lLSair.setVisibility(View.GONE);
        });

    }

    private void startActivity(Class<?> classe) {
        if(FirebaseHelper.getAutenticado()){
            startActivity(new Intent(requireActivity(), classe));
        }else{
            startActivity(new Intent(requireActivity(), LoginActivity.class));
        }

    }
}