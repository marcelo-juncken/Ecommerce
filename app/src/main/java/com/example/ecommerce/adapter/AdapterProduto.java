package com.example.ecommerce.adapter;

import android.content.Context;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ecommerce.R;
import com.example.ecommerce.helper.GetMask;
import com.example.ecommerce.model.ImagemUpload;
import com.example.ecommerce.model.Produto;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class AdapterProduto extends RecyclerView.Adapter<AdapterProduto.MyViewHolder> {

    private final List<Produto> produtoList;
    private final Context context;
    private final onClickListener onClickListener;

    public AdapterProduto(List<Produto> produtoList, Context context, AdapterProduto.onClickListener onClickListener) {
        this.produtoList = produtoList;
        this.context = context;
        this.onClickListener = onClickListener;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_produto_list, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Produto produto = produtoList.get(position);

        List<ImagemUpload> imagemUploadList = new ArrayList<>(produto.getImagemUploadMap().values());
        Collections.sort(imagemUploadList, (o1, o2) -> Math.toIntExact(o1.getIndex() - o2.getIndex()));

        Picasso.get().load(imagemUploadList.get(0).getCaminhoImagem()).error(R.drawable.ic_close).into(holder.imgProduto);
        holder.txtTitulo.setText(produto.getTitulo());

        if (produto.getValorAntigo() > 0) {

            int valor = (int) (Math.ceil((1 - (produto.getValorAtual() / produto.getValorAntigo())) * 100));
            holder.txtDescontoProduto.setText(String.format("%s%% OFF", valor));
            holder.txtDescontoProduto.setPaintFlags(holder.txtDescontoProduto.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        }

        holder.txtValorAtual.setText(context.getString(R.string.valor_produto, GetMask.getValor(produto.getValorAtual())));

        holder.itemView.setOnClickListener(v -> onClickListener.onClicK(produto));
    }

    @Override
    public int getItemCount() {
        return produtoList.size();
    }


    public interface onClickListener {
        void onClicK(Produto produto);
    }


    static class MyViewHolder extends RecyclerView.ViewHolder {
        ImageView imgProduto;
        TextView txtTitulo, txtValorAtual, txtDescontoProduto;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            imgProduto = itemView.findViewById(R.id.imgProduto);
            txtTitulo = itemView.findViewById(R.id.txtTitulo);
            txtValorAtual = itemView.findViewById(R.id.txtValorAtual);
            txtDescontoProduto = itemView.findViewById(R.id.txtDescontoProduto);
        }
    }
}
