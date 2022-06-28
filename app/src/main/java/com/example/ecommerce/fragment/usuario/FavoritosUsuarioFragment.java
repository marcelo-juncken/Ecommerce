package com.example.ecommerce.fragment.usuario;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.ecommerce.R;
import com.example.ecommerce.activity.usuario.DetalhesProdutoActivity;
import com.example.ecommerce.adapter.AdapterProduto;
import com.example.ecommerce.autenticacao.LoginActivity;
import com.example.ecommerce.databinding.FragmentFavoritosUsuarioBinding;
import com.example.ecommerce.helper.FirebaseHelper;
import com.example.ecommerce.model.Favorito;
import com.example.ecommerce.model.Produto;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class FavoritosUsuarioFragment extends Fragment implements AdapterProduto.onClickListener, AdapterProduto.onClickFavorito {

    private FragmentFavoritosUsuarioBinding binding;

    private AdapterProduto adapterProduto;
    private List<Produto> produtoList = new ArrayList<>();
    private final List<String> idsFavoritosList = new ArrayList<>();

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentFavoritosUsuarioBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        configRvProdutos();
        configCliques();
    }

    @Override
    public void onStart() {
        super.onStart();
        recuperaFavoritos();
    }

    private void recuperaFavoritos() {
        if (FirebaseHelper.getAutenticado()) {
            binding.txtInfo.setTag("Autenticado");
            DatabaseReference favoritosRef = FirebaseHelper.getDatabaseReference()
                    .child("favoritos")
                    .child(FirebaseHelper.getIdFirebase());
            favoritosRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    idsFavoritosList.clear();
                    if (snapshot.exists()) {
                        for (DataSnapshot ds : snapshot.getChildren()) {
                            idsFavoritosList.add(ds.getValue(String.class));
                        }
                        recuperaProdutos();
                    } else {
                        binding.progressBar.setVisibility(View.GONE);
                        binding.txtInfo.setText("Nenhum produto no seu favorito.");
                    }

                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                }
            });
        } else {
            produtoList.clear();
            idsFavoritosList.clear();
            adapterProduto.notifyDataSetChanged();
            binding.progressBar.setVisibility(View.GONE);
            binding.txtInfo.setText("Usuário não autenticado");
            binding.txtInfo.setTag("naoAutenticado");
        }
    }

    private void recuperaProdutos() {
        produtoList.clear();
        for (int i = 0; i < idsFavoritosList.size(); i++) {
            DatabaseReference produtosRef = FirebaseHelper.getDatabaseReference()
                    .child("produtos")
                    .child(idsFavoritosList.get(i));
            produtosRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        Produto produto = snapshot.getValue(Produto.class);
                        if (produto != null) {
                            produtoList.add(0, produto);
                            if (produtoList.size() == idsFavoritosList.size()) {
                                adapterProduto.notifyDataSetChanged();
                                binding.progressBar.setVisibility(View.GONE);
                                binding.txtInfo.setText("");
                            }
                        }
                    } else {
                        binding.txtInfo.setText("Nenhum produto no seu favorito.");
                        adapterProduto.notifyDataSetChanged();
                    }
                    binding.progressBar.setVisibility(View.GONE);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    binding.progressBar.setVisibility(View.GONE);
                    binding.txtInfo.setText("Erro ao carregar página");
                }
            });

        }
    }

    private void configRvProdutos() {
        binding.rvProdutos.setLayoutManager(new GridLayoutManager(getContext(), 2));
        binding.rvProdutos.setHasFixedSize(true);
        adapterProduto = new AdapterProduto(R.layout.item_produto_list,produtoList, getContext(), this, true, idsFavoritosList, this);
        binding.rvProdutos.setAdapter(adapterProduto);
    }

    private void configCliques() {
        binding.txtInfo.setOnClickListener(v -> {
            if (binding.txtInfo.getTag().equals("naoAutenticado"))
                startActivity(new Intent(requireActivity(), LoginActivity.class));
        });
    }

    @Override
    public void onClicK(Produto produto) {
        Intent intent = new Intent(requireContext(), DetalhesProdutoActivity.class);
        intent.putExtra("produtoSelecionado", produto);
        startActivity(intent);
    }


    @Override
    public void onClickFavorito(Produto produto, boolean liked) {
        if (liked) {
            if (!idsFavoritosList.contains(produto.getId())) {
                idsFavoritosList.add(produto.getId());
                produtoList.add(produto);
            }
        } else {
            idsFavoritosList.remove(produto.getId());
            produtoList.remove(produto);
        }
        Favorito.salvar(idsFavoritosList);


        if (produtoList.isEmpty()) {
            binding.txtInfo.setText("Nenhum produto no seu favorito.");
        }
        adapterProduto.notifyDataSetChanged();
    }
}