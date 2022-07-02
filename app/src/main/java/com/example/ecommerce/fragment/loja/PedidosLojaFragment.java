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

import com.example.ecommerce.activity.app.DetalhesPedidoActivity;
import com.example.ecommerce.adapter.AdapterLojaPedidos;
import com.example.ecommerce.databinding.FragmentPedidosLojaBinding;
import com.example.ecommerce.helper.FirebaseHelper;
import com.example.ecommerce.model.Pedido;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class PedidosLojaFragment extends Fragment implements AdapterLojaPedidos.onClick {

    private FragmentPedidosLojaBinding binding;
    private AdapterLojaPedidos adapterLojaPedidos;
    private List<Pedido> pedidoList = new ArrayList<>();

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentPedidosLojaBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        configRv();
        recuperaPedidos();
    }

    private void recuperaPedidos() {
        if (FirebaseHelper.getAutenticado()) {
            DatabaseReference pedidosRef = FirebaseHelper.getDatabaseReference()
                    .child("lojaPedidos");
            pedidosRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    pedidoList.clear();
                    if (snapshot.exists()) {
                        for (DataSnapshot ds : snapshot.getChildren()) {
                            Pedido pedido = ds.getValue(Pedido.class);
                            if (pedido != null) {
                                pedidoList.add(0, pedido);
                            }
                        }
                        binding.txtInfo.setText("");
                    } else {
                        binding.txtInfo.setText("Nenhum pedido cadastrado");
                    }
                    binding.progressBar.setVisibility(View.GONE);
                    adapterLojaPedidos.notifyDataSetChanged();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                }
            });
        } else {
            binding.progressBar.setVisibility(View.GONE);
            binding.txtInfo.setText("Você não está autenticado");
            adapterLojaPedidos.notifyDataSetChanged();
        }
    }

    private void configRv() {
        binding.rvPedidos.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.rvPedidos.setHasFixedSize(true);
        adapterLojaPedidos = new AdapterLojaPedidos(pedidoList, this, requireContext());
        binding.rvPedidos.setAdapter(adapterLojaPedidos);
    }


    @Override
    public void onClicK(Pedido pedido, String operacao) {
        switch (operacao) {
            case "detalhes":
                Intent intent = new Intent(requireContext(), DetalhesPedidoActivity.class);
                intent.putExtra("pedidoSelecionado", pedido);
                startActivity(intent);
                break;
            case "status":
                break;
        }
    }
}