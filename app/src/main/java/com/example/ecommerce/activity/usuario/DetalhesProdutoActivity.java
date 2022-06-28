package com.example.ecommerce.activity.usuario;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.ecommerce.DAO.ItemDAO;
import com.example.ecommerce.DAO.ItemPedidoDAO;
import com.example.ecommerce.R;
import com.example.ecommerce.adapter.AdapterProduto;
import com.example.ecommerce.adapter.AdapterSlider;
import com.example.ecommerce.autenticacao.LoginActivity;
import com.example.ecommerce.databinding.ActivityDetalhesProdutoBinding;
import com.example.ecommerce.databinding.DialogAddItemCarrinhoBinding;
import com.example.ecommerce.helper.FirebaseHelper;
import com.example.ecommerce.helper.GetMask;
import com.example.ecommerce.model.Categoria;
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
import com.smarteist.autoimageslider.IndicatorView.animation.type.IndicatorAnimationType;
import com.smarteist.autoimageslider.SliderAnimations;

import java.util.ArrayList;
import java.util.List;

public class DetalhesProdutoActivity extends AppCompatActivity implements AdapterProduto.onClickListener, AdapterProduto.onClickFavorito {

    private ActivityDetalhesProdutoBinding binding;
    private Produto produtoSelecionado;

    private AdapterProduto adapterProduto;
    private final List<Produto> produtoList = new ArrayList<>();
    private final List<String> idsFavoritosList = new ArrayList<>();

    private final List<String> idsTodasCategorias = new ArrayList<>();

    private ItemDAO itemDAO;
    private ItemPedidoDAO itemPedidoDAO;

    private AlertDialog dialog;
    private DialogAddItemCarrinhoBinding bindingDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityDetalhesProdutoBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        itemDAO = new ItemDAO(this);
        itemPedidoDAO = new ItemPedidoDAO(this);

        recuperaCategorias();

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            produtoSelecionado = (Produto) bundle.getSerializable("produtoSelecionado");
            configProduto();
        }
        configCliques();
        recuperaProdutos();
        configRv();

    }

    @Override
    protected void onStart() {
        super.onStart();
        recuperaFavoritos();
    }

    private void recuperaCategorias() {
        DatabaseReference categoriassRef = FirebaseHelper.getDatabaseReference()
                .child("categorias");
        categoriassRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                idsTodasCategorias.clear();
                if (snapshot.exists()) {
                    for (DataSnapshot ds : snapshot.getChildren()) {
                        Categoria categoria = ds.getValue(Categoria.class);
                        if (categoria != null && categoria.isTodas()) {
                            idsTodasCategorias.add(categoria.getId());
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });

    }

    private void recuperaFavoritos() {
        if (FirebaseHelper.getAutenticado()) {
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
                    }
                    binding.progressBar.setVisibility(View.GONE);
                    binding.txtInfo.setText("");
                    binding.likeButton.setLiked(idsFavoritosList.contains(produtoSelecionado.getId()));
                    adapterProduto.notifyDataSetChanged();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                }
            });
        } else {
            adapterProduto.notifyDataSetChanged();
            binding.progressBar.setVisibility(View.GONE);
            binding.txtInfo.setText("");
        }
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
                        Produto produto = ds.getValue(Produto.class);
                        if (produto != null) {
                            for (String categoria : produtoSelecionado.getIdCategorias().keySet()) {
                                if (!idsTodasCategorias.contains(categoria) && produto.getIdCategorias().containsKey(categoria)) {
                                    if (!produto.getId().equals(produtoSelecionado.getId())) {
                                        produtoList.add(0, ds.getValue(Produto.class));
                                        break;
                                    }
                                }
                            }

                        }
                    }
                } else {
                    binding.rvProdutos.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                binding.progressBar.setVisibility(View.GONE);
                binding.txtInfo.setText("Erro ao carregar página");
            }
        });
    }


    private void configRv() {
        binding.rvProdutos.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        binding.rvProdutos.setHasFixedSize(true);
        adapterProduto = new AdapterProduto(R.layout.item_produto_similar, produtoList, getBaseContext(), this, true, idsFavoritosList, this);
        binding.rvProdutos.setAdapter(adapterProduto);
    }

    private void configProduto() {
        binding.txtNomeProduto.setText(produtoSelecionado.getTitulo());
        binding.txtDescricao.setText(produtoSelecionado.getDescricao());
        binding.textValor.setText(getString(R.string.valor_produto, GetMask.getValor(produtoSelecionado.getValorAtual())));

        List<ImagemUpload> imagemUploadList = new ArrayList<>(produtoSelecionado.getImagemUploadMap().values());
        imagemUploadList.sort((o1, o2) -> Math.toIntExact(o1.getIndex() - o2.getIndex()));

        binding.sliderView.setSliderAdapter(new AdapterSlider(imagemUploadList));
        binding.sliderView.startAutoCycle();
        binding.sliderView.setScrollTimeInSec(4);
        binding.sliderView.setIndicatorAnimation(IndicatorAnimationType.WORM);
        binding.sliderView.setSliderTransformAnimation(SliderAnimations.FADETRANSFORMATION);

    }

    private void showDialog() {
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this, R.style.AlertDialog);

        bindingDialog = DialogAddItemCarrinhoBinding.inflate(getLayoutInflater());
        builder.setView(bindingDialog.getRoot());


        bindingDialog.btnFechar.setOnClickListener(v -> {
            dialog.dismiss();
        });

        bindingDialog.btnCarrinho.setOnClickListener(v -> {
            dialog.dismiss();
            Intent intent = new Intent(this, MainActivityUsuario.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            intent.putExtra("id", 2);
            finish();
            startActivity(intent);
        });

        dialog = builder.create();

        dialog.show();
        dialog.setCancelable(false);

    }

    private void configCliques() {
        binding.include.txtTitulo.setText("Detalhes do produto");
        binding.include.include.ibVoltar.setOnClickListener(v -> finish());

        binding.txtDescricao.setOnClickListener(v -> {
            if (binding.txtDescricao.getMaxLines() == 3) {
                binding.txtDescricao.setMaxLines(Integer.MAX_VALUE);
                binding.txtDescricao.setEllipsize(null);
            } else {
                binding.txtDescricao.setMaxLines(3);
                binding.txtDescricao.setEllipsize(TextUtils.TruncateAt.END);
            }

        });

        binding.likeButton.setOnLikeListener(new OnLikeListener() {
            @Override
            public void liked(LikeButton likeButton) {
                if (FirebaseHelper.getAutenticado()) {
                    if (!idsFavoritosList.contains(produtoSelecionado.getId())) {
                        idsFavoritosList.add(produtoSelecionado.getId());
                    }
                    Favorito.salvar(idsFavoritosList);
                } else {
                    likeButton.setLiked(false);
                    startActivity(new Intent(getBaseContext(), LoginActivity.class));
                }
            }

            @Override
            public void unLiked(LikeButton likeButton) {
                if (FirebaseHelper.getAutenticado()) {
                    idsFavoritosList.remove(produtoSelecionado.getId());
                    Favorito.salvar(idsFavoritosList);
                } else {
                    likeButton.setLiked(true);
                    startActivity(new Intent(getBaseContext(), LoginActivity.class));
                }

            }
        });


        binding.btnComprar.setOnClickListener(v -> {
            addCarrinho();
        });
    }

    private void addCarrinho() {
        ItemPedido itemPedido = new ItemPedido();
        itemPedido.setQuantidade(1);
        itemPedido.setIdProduto(produtoSelecionado.getId());
        itemPedido.setValor(produtoSelecionado.getValorAtual());

        itemPedidoDAO.salvar(itemPedido);
        itemDAO.salvar(produtoSelecionado);
        showDialog();
    }

    @Override
    public void onClicK(Produto produto) {
        Intent intent = new Intent(this, DetalhesProdutoActivity.class);
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
}