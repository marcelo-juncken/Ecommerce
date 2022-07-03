package com.example.ecommerce.fragment.loja;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.ecommerce.R;
import com.example.ecommerce.activity.app.DetalhesPedidoActivity;
import com.example.ecommerce.adapter.AdapterLojaPedidos;
import com.example.ecommerce.databinding.DialogCategoriaFormBinding;
import com.example.ecommerce.databinding.DialogStatusPedidoBinding;
import com.example.ecommerce.databinding.FragmentPedidosLojaBinding;
import com.example.ecommerce.helper.FirebaseHelper;
import com.example.ecommerce.model.Pedido;
import com.example.ecommerce.model.StatusPedido;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class PedidosLojaFragment extends Fragment implements AdapterLojaPedidos.onClick {

    private FragmentPedidosLojaBinding binding;
    private AdapterLojaPedidos adapterLojaPedidos;
    private List<Pedido> pedidoList = new ArrayList<>();

    private DialogStatusPedidoBinding dialogBinding;
    private AlertDialog dialog;

    private StatusPedido statusPedido;

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

    private void showDialog(Pedido pedido) {

        AlertDialog.Builder builder = new AlertDialog.Builder(
                getContext(), R.style.AlertDialog);

        dialogBinding = DialogStatusPedidoBinding
                .inflate(LayoutInflater.from(getContext()));
        statusPedido = pedido.getStatusPedido();

        switch (pedido.getStatusPedido()) {
            case APROVADO:
                dialogBinding.rbAprovado.setChecked(true);
                dialogBinding.rbCancelado.setEnabled(true);
                dialogBinding.rbPendente.setEnabled(false);
                break;
            case CANCELADO:
                dialogBinding.rbCancelado.setChecked(true);
                dialogBinding.rbAprovado.setEnabled(false);
                dialogBinding.rbPendente.setEnabled(false);
                break;
            default:
                dialogBinding.rbPendente.setChecked(true);
                break;

        }

        dialogBinding.btnFechar.setOnClickListener(v -> {
            dialog.dismiss();
        });

        dialogBinding.btnConfirmar.setOnClickListener(v -> {
            validaDados(pedido);
        });

        builder.setView(dialogBinding.getRoot());
        dialog = builder.create();
        dialog.show();
    }

    private void validaDados(Pedido pedido) {
        if (dialogBinding.rgStatus.getCheckedRadioButtonId() == -1) {
            Toast.makeText(requireContext(), "Selecione um item", Toast.LENGTH_SHORT).show();
        } else {
            if (dialogBinding.rgStatus.getCheckedRadioButtonId() == R.id.rbAprovado) {
                pedido.setStatusPedido(StatusPedido.APROVADO);
            } else if (dialogBinding.rgStatus.getCheckedRadioButtonId() == R.id.rbPendente) {
                pedido.setStatusPedido(StatusPedido.PENDENTE);
            } else if (dialogBinding.rgStatus.getCheckedRadioButtonId() == R.id.rbCancelado) {
                pedido.setStatusPedido(StatusPedido.CANCELADO);
            }
            if(statusPedido != pedido.getStatusPedido()){
                pedido.salvar(false);
            }
            dialog.dismiss();
        }
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
                showDialog(pedido);
                break;
        }
    }
}