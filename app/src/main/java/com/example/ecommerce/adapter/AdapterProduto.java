package com.example.ecommerce.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ecommerce.R;
import com.example.ecommerce.autenticacao.LoginActivity;
import com.example.ecommerce.helper.FirebaseHelper;
import com.example.ecommerce.helper.GetMask;
import com.example.ecommerce.model.Favorito;
import com.example.ecommerce.model.ImagemUpload;
import com.example.ecommerce.model.Produto;
import com.like.LikeButton;
import com.like.OnLikeListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class AdapterProduto extends RecyclerView.Adapter<AdapterProduto.MyViewHolder> {

    private final int layout;
    private final List<Produto> produtoList;
    private final Context context;
    private final onClickListener onClickListener;
    private final boolean favorito;
    private final List<String> idsFavoritos;
    private final onClickFavorito onClickFavorito;

    public AdapterProduto(int layout, List<Produto> produtoList, Context context, AdapterProduto.onClickListener onClickListener, boolean favorito, List<String> idsFavoritos, AdapterProduto.onClickFavorito onClickFavorito) {
        this.layout = layout;
        this.produtoList = produtoList;
        this.context = context;
        this.onClickListener = onClickListener;
        this.favorito = favorito;
        this.idsFavoritos = idsFavoritos;
        this.onClickFavorito = onClickFavorito;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(layout, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Produto produto = produtoList.get(position);

        List<ImagemUpload> imagemUploadList = new ArrayList<>(produto.getImagemUploadMap().values());
        imagemUploadList.sort((o1, o2) -> Math.toIntExact(o1.getIndex() - o2.getIndex()));

        Picasso.get().load(imagemUploadList.get(0).getCaminhoImagem()).error(R.drawable.ic_close).into(holder.imgProduto);
        holder.txtTitulo.setText(produto.getTitulo());

        if (favorito) {
            holder.btnLike.setLiked(idsFavoritos.contains(produto.getId()));
        } else {
            holder.btnLike.setVisibility(View.GONE);
        }

        if (produto.getValorAntigo() > 0) {

            int desconto = (int) (100 * (1 - produto.getValorAtual() / produto.getValorAntigo()));

            if (desconto >= 10) {
                holder.txtDescontoProduto.setText(String.format("-%s%%", desconto));
            } else {
                holder.txtDescontoProduto.setText("");
            }
        } else {
            holder.txtDescontoProduto.setText("");
        }

        holder.txtValorAtual.setText(context.getString(R.string.valor_produto, GetMask.getValor(produto.getValorAtual())));

        holder.itemView.setOnClickListener(v -> onClickListener.onClicK(produto));

        holder.btnLike.setOnLikeListener(new OnLikeListener() {
            @Override
            public void liked(LikeButton likeButton) {
                if (FirebaseHelper.getAutenticado()) {
                    onClickFavorito.onClickFavorito(produto, true);
                } else {
                    holder.btnLike.setLiked(false);
                    context.startActivity(new Intent(context, LoginActivity.class));
                }


            }

            @Override
            public void unLiked(LikeButton likeButton) {
                if (FirebaseHelper.getAutenticado()) {
                    onClickFavorito.onClickFavorito(produto, false);
                } else {
                    holder.btnLike.setLiked(true);
                    context.startActivity(new Intent(context, LoginActivity.class));
                }

            }
        });
    }

    @Override
    public int getItemCount() {
        return produtoList.size();
    }


    public interface onClickListener {
        void onClicK(Produto produto);
    }

    public interface onClickFavorito {
        void onClickFavorito(Produto produto, boolean liked);
    }


    static class MyViewHolder extends RecyclerView.ViewHolder {
        ImageView imgProduto;
        TextView txtTitulo, txtValorAtual, txtDescontoProduto;
        LikeButton btnLike;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            imgProduto = itemView.findViewById(R.id.imgProduto);
            txtTitulo = itemView.findViewById(R.id.txtTitulo);
            txtValorAtual = itemView.findViewById(R.id.txtValorAtual);
            txtDescontoProduto = itemView.findViewById(R.id.txtDescontoProduto);
            btnLike = itemView.findViewById(R.id.btnLike);
        }
    }
}
