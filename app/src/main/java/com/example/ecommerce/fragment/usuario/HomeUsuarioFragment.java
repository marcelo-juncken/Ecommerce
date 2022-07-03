package com.example.ecommerce.fragment.usuario;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.ecommerce.R;
import com.example.ecommerce.activity.usuario.DetalhesProdutoActivity;
import com.example.ecommerce.adapter.AdapterCategoria;
import com.example.ecommerce.adapter.AdapterProduto;
import com.example.ecommerce.databinding.FragmentHomeUsuarioBinding;
import com.example.ecommerce.helper.FirebaseHelper;
import com.example.ecommerce.model.Categoria;
import com.example.ecommerce.model.Favorito;
import com.example.ecommerce.model.Produto;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.text.Normalizer;
import java.util.ArrayList;
import java.util.List;

public class HomeUsuarioFragment extends Fragment implements AdapterCategoria.onClickListener, AdapterProduto.onClickListener, AdapterProduto.onClickFavorito {

    private FragmentHomeUsuarioBinding binding;

    private final List<Categoria> categoriaList = new ArrayList<>();

    private final List<String> idsFavoritosList = new ArrayList<>();

    private AdapterCategoria adapterCategoria;

    private AdapterProduto adapterProduto;
    private final List<Produto> produtoList = new ArrayList<>();
    private final List<Produto> produtosPesquisaList = new ArrayList<>();
    private final List<Produto> produtosSelecionadosList = new ArrayList<>();

    private String idCategoriaSelecionada;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentHomeUsuarioBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        configRvProdutos();
        configRvCategorias();
        recuperaCategorias();
        recuperaProdutos();
        configSearchView();


    }

    @Override
    public void onStart() {
        super.onStart();
        if (!produtoList.isEmpty()) {
            recuperaFavoritos();
        }
    }


    private void configSearchView() {
        binding.searchView.findViewById(androidx.appcompat.R.id.search_close_btn).setOnClickListener(v -> limparPesquisa());


        binding.searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                ocultarTeclado();
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                filtraProdutosPesquisa(newText);
                return true;
            }
        });
    }

    private void limparPesquisa() {
        EditText searchText = binding.searchView.findViewById(androidx.appcompat.R.id.search_src_text);
        searchText.getText().clear();
        binding.searchView.clearFocus();
        filtraProdutosPesquisa("");
        ocultarTeclado();
    }

    private void filtraProdutosPesquisa(String pesquisa) {
        produtosPesquisaList.clear();
        for (Produto produto : produtoList) {

            String produtoNome = Normalizer.normalize(produto.getTitulo(), Normalizer.Form.NFD)
                    .replaceAll("\\p{InCombiningDiacriticalMarks}+", "")
                    .toLowerCase()
                    .trim();

            String pesquisaNome = Normalizer.normalize(pesquisa, Normalizer.Form.NFD)
                    .replaceAll("\\p{InCombiningDiacriticalMarks}+", "")
                    .toLowerCase()
                    .trim();

            if (produtoNome.contains(pesquisaNome)) {
                produtosPesquisaList.add(produto);
            }
            if (produto.equals(produtoList.get(produtoList.size() - 1))) {
                filtraProdutosCategoria();
            }
        }
    }

    private void filtraProdutosCategoria() {
        produtosSelecionadosList.clear();
        for (Produto produto : produtosPesquisaList) {
            if (produto.getIdCategorias().containsKey(idCategoriaSelecionada)) {
                produtosSelecionadosList.add(produto);
            }
            if (produto.equals(produtosPesquisaList.get(produtosPesquisaList.size() - 1))) {
                adapterProduto.notifyDataSetChanged();
            }
        }
        if (produtosPesquisaList.isEmpty()) adapterProduto.notifyDataSetChanged();

    }

    private void recuperaFavoritos() {
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
                    }
                    EditText searchText = binding.searchView.findViewById(androidx.appcompat.R.id.search_src_text);
                    filtraProdutosPesquisa(searchText.getText().toString());
                    binding.progressBar.setVisibility(View.GONE);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                }
            });
        } else {
            adapterProduto.notifyDataSetChanged();
            binding.progressBar.setVisibility(View.GONE);
        }
    }

    private void recuperaCategorias() {
        DatabaseReference categoriasRef = FirebaseHelper.getDatabaseReference()
                .child("categorias");
        categoriasRef.orderByChild("posicao").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                categoriaList.clear();
                if (snapshot.exists()) {
                    for (DataSnapshot ds : snapshot.getChildren()) {
                        Categoria categoria = ds.getValue(Categoria.class);
                        if (categoria != null) {
                            categoriaList.add(categoria);
                            if (categoria.isTodas()) {
                                idCategoriaSelecionada = categoria.getId();
                            }
                        }

                    }
                }
                adapterCategoria.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    private void configRvCategorias() {
        binding.rvCategorias.setLayoutManager(new LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false));
        binding.rvCategorias.setHasFixedSize(true);
        adapterCategoria = new AdapterCategoria(R.layout.item_categoria_horizontal, true, categoriaList, this);
        binding.rvCategorias.setAdapter(adapterCategoria);
    }

    private void recuperaProdutos() {
        produtoList.clear();
        produtosSelecionadosList.clear();
        DatabaseReference produtosRef = FirebaseHelper.getDatabaseReference()
                .child("produtos");
        produtosRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    for (DataSnapshot ds : snapshot.getChildren()) {
                        produtoList.add(0, ds.getValue(Produto.class));
                    }
                    binding.txtInfo.setText("");
                    produtosSelecionadosList.addAll(produtoList);
                    produtosPesquisaList.addAll(produtoList);
                    recuperaFavoritos();
                } else {
                    binding.txtInfo.setText("Nenhum produto cadastrado.");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                binding.progressBar.setVisibility(View.GONE);
                binding.txtInfo.setText("Erro ao carregar página");
            }
        });
    }

    private void configRvProdutos() {
        binding.rvProdutos.setLayoutManager(new GridLayoutManager(getContext(), 2));
        binding.rvProdutos.setHasFixedSize(true);
        adapterProduto = new AdapterProduto(R.layout.item_produto_list, produtosSelecionadosList, getContext(), this, true, idsFavoritosList, this);
        binding.rvProdutos.setAdapter(adapterProduto);
    }

    @Override
    public void onClick(Categoria categoria) {
        idCategoriaSelecionada = categoria.getId();
        filtraProdutosCategoria();
    }

    @Override
    public void onClicK(Produto produto) {
        ocultarTeclado();
        binding.searchView.clearFocus();
        Intent intent = new Intent(requireContext(), DetalhesProdutoActivity.class);
        intent.putExtra("produtoSelecionado", produto);
        startActivity(intent);
    }


    @Override
    public void onClickFavorito(Produto produto, boolean liked) {
        if (liked) {
            if (!idsFavoritosList.contains(produto.getId())) {
                idsFavoritosList.add(produto.getId());
            }
        } else {
            idsFavoritosList.remove(produto.getId());
        }
        Favorito.salvar(idsFavoritosList);
    }

    private void ocultarTeclado() {
        ((InputMethodManager) requireContext().getSystemService(Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(
                binding.searchView.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS
        );
    }
}