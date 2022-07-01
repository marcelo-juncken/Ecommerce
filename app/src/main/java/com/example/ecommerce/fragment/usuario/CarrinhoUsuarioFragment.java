package com.example.ecommerce.fragment.usuario;

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

import com.example.ecommerce.DAO.ItemDAO;
import com.example.ecommerce.DAO.ItemPedidoDAO;
import com.example.ecommerce.R;
import com.example.ecommerce.activity.usuario.UsuarioResumoPedidoActivity;
import com.example.ecommerce.adapter.AdapterCarrinho;
import com.example.ecommerce.autenticacao.LoginActivity;
import com.example.ecommerce.databinding.DialogItemCarrinhoBinding;
import com.example.ecommerce.databinding.FragmentCarrinhoUsuarioBinding;
import com.example.ecommerce.helper.FirebaseHelper;
import com.example.ecommerce.helper.GetMask;
import com.example.ecommerce.model.Favorito;
import com.example.ecommerce.model.ImagemUpload;
import com.example.ecommerce.model.ItemPedido;
import com.example.ecommerce.model.Produto;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.like.LikeButton;
import com.like.OnLikeListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class CarrinhoUsuarioFragment extends Fragment implements AdapterCarrinho.onClick {

    private FragmentCarrinhoUsuarioBinding binding;
    private AdapterCarrinho adapterCarrinho;
    private final List<ItemPedido> itemPedidoList = new ArrayList<>();
    private ItemDAO itemDAO;
    private ItemPedidoDAO itemPedidoDAO;

    private final List<String> idsFavoritosList = new ArrayList<>();

    private AlertDialog dialog;
    private DialogItemCarrinhoBinding bindingDialog;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentCarrinhoUsuarioBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        itemPedidoDAO = new ItemPedidoDAO(requireContext());
        itemDAO = new ItemDAO(requireContext());
        itemPedidoList.addAll(itemPedidoDAO.getList());
        configCliques();
        configRv();
        configTotalCarrinho();
    }

    private void configCliques() {
        binding.btnContinuar.setOnClickListener(v -> {
            if (FirebaseHelper.getAutenticado()){
                startActivity(new Intent(getContext(), UsuarioResumoPedidoActivity.class));
            }else{
                startActivity(new Intent(getContext(), LoginActivity.class));
            }
        });
    }


    private void recuperaFavoritos(Produto produto) {
        idsFavoritosList.clear();
        if (FirebaseHelper.getAutenticado()) {
            DatabaseReference favoritosRef = FirebaseHelper.getDatabaseReference()
                    .child("favoritos")
                    .child(FirebaseHelper.getIdFirebase());
            favoritosRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        for (DataSnapshot ds : snapshot.getChildren()) {
                            idsFavoritosList.add(ds.getValue(String.class));
                        }
                        bindingDialog.btnLike.setLiked(idsFavoritosList.contains(produto.getId()));
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                }
            });
        }
    }

    private void configTotalCarrinho() {
        binding.textValor.setText(getString(R.string.total_produto, GetMask.getValor(itemPedidoDAO.getTotalCarrinho())));
        if(itemPedidoList.isEmpty()) {
            binding.textInfo.setVisibility(View.VISIBLE);
        }else{
            binding.textInfo.setVisibility(View.GONE);
        }
    }

    private void configRv() {
        Collections.reverse(itemPedidoList);
        binding.rvProdutos.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.rvProdutos.setHasFixedSize(true);
        adapterCarrinho = new AdapterCarrinho(itemPedidoList, itemPedidoDAO, getContext(), this);
        binding.rvProdutos.setAdapter(adapterCarrinho);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onClick(int position, String operacao) {
        ItemPedido itemPedido = itemPedidoList.get(position);

        switch (operacao) {
            case "remover":
            case "adicionar":
                if (operacao.equals("remover")) {
                    if (itemPedido.getQuantidade() > 1) {
                        itemPedido.setQuantidade(itemPedido.getQuantidade() - 1);
                    }
                } else {
                    itemPedido.setQuantidade(itemPedido.getQuantidade() + 1);
                }
                itemPedidoDAO.editarProduto(itemPedido);
                itemPedidoList.set(position, itemPedido);
                break;
            case "deletar":
                Produto produto = itemPedidoDAO.getProduto(itemPedido.getId());
                showDialog(produto, position);
                break;
        }
        configTotalCarrinho();
        adapterCarrinho.notifyDataSetChanged();

    }

    private void showDialog(Produto produto, int position) {
        recuperaFavoritos(produto);

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());

        bindingDialog = DialogItemCarrinhoBinding.inflate(getLayoutInflater());
        builder.setView(bindingDialog.getRoot());

        bindingDialog.txtNomeProduto.setText(produto.getTitulo());
        List<ImagemUpload> imagemUploadList = new ArrayList<>(produto.getImagemUploadMap().values());
        Picasso.get().load(imagemUploadList.get(0).getCaminhoImagem()).into(bindingDialog.imgProduto);

        bindingDialog.cvCancelar.setOnClickListener(v -> {
            bindingDialog = null;
            dialog.dismiss();
        });

        bindingDialog.btnLike.setOnLikeListener(new OnLikeListener() {
            @Override
            public void liked(LikeButton likeButton) {
                if (FirebaseHelper.getAutenticado()) {
                    if (!idsFavoritosList.contains(produto.getId())) {
                        idsFavoritosList.add(produto.getId());
                    }
                    Favorito.salvar(idsFavoritosList);
                } else {
                    bindingDialog.btnLike.setLiked(false);
                    Toast.makeText(requireContext(), "Você não está logado", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void unLiked(LikeButton likeButton) {
                if (FirebaseHelper.getAutenticado()) {
                    idsFavoritosList.remove(produto.getId());
                    Favorito.salvar(idsFavoritosList);
                } else {
                    bindingDialog.btnLike.setLiked(false);
                    Toast.makeText(requireContext(), "Você não está logado", Toast.LENGTH_SHORT).show();
                }
            }
        });

        bindingDialog.cvRemover.setOnClickListener(v -> {
            itemPedidoDAO.removerProduto(produto);
            itemDAO.removerProduto(produto);
            itemPedidoList.remove(position);
            dialog.dismiss();
            adapterCarrinho.notifyDataSetChanged();
            bindingDialog = null;
            configTotalCarrinho();
        });

        dialog = builder.create();

        dialog.show();
        dialog.setCancelable(false);

    }
}