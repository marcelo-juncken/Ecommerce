package com.example.ecommerce.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ecommerce.DAO.ItemPedidoDAO;
import com.example.ecommerce.R;
import com.example.ecommerce.helper.GetMask;
import com.example.ecommerce.model.ImagemUpload;
import com.example.ecommerce.model.ItemPedido;
import com.example.ecommerce.model.Produto;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class AdapterCarrinho extends RecyclerView.Adapter<AdapterCarrinho.MyViewHolder> {

    private List<ItemPedido> itemPedidoList;
    private ItemPedidoDAO itemPedidoDAO;
    private Context context;
    private onClick onClick;

    public AdapterCarrinho(List<ItemPedido> itemPedidoList, ItemPedidoDAO itemPedidoDAO, Context context, onClick onClick) {
        this.itemPedidoList = itemPedidoList;
        this.itemPedidoDAO = itemPedidoDAO;
        this.context = context;
        this.onClick = onClick;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_adapter_carrinho, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        ItemPedido itemPedido = itemPedidoList.get(position);
        Produto produto = itemPedidoDAO.getProduto(itemPedido.getId());

        List<ImagemUpload> imagemUploadList = new ArrayList<>(produto.getImagemUploadMap().values());
        Picasso.get().load(imagemUploadList.get(0).getCaminhoImagem()).into(holder.imgProduto);

        holder.txtProduto.setText(produto.getTitulo());
        holder.txtQtd.setText(String.valueOf(itemPedido.getQuantidade()));
        holder.txtValor.setText(context.getString(R.string.valor_produto, GetMask.getValor(itemPedido.getValor() * itemPedido.getQuantidade())));

        holder.itemView.setOnClickListener(v -> onClick.onClick(position, "detalhes"));
        holder.imgDelete.setOnClickListener(v -> onClick.onClick(position, "deletar"));
        holder.imbRem.setOnClickListener(v -> onClick.onClick(position, "remover"));
        holder.imbAdd.setOnClickListener(v -> onClick.onClick(position, "adicionar"));
    }

    @Override
    public int getItemCount() {
        return itemPedidoList.size();
    }

    public interface onClick {
        void onClick(int position, String operacao);
    }

    static class MyViewHolder extends RecyclerView.ViewHolder {

        ImageView imgProduto, imgDelete;
        TextView txtProduto, txtQtd, txtValor;
        ImageButton imbRem, imbAdd;


        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            imgProduto = itemView.findViewById(R.id.imgProduto);
            imgDelete = itemView.findViewById(R.id.imgDelete);
            txtProduto = itemView.findViewById(R.id.txtProduto);
            txtQtd = itemView.findViewById(R.id.txtQtd);
            txtValor = itemView.findViewById(R.id.txtValor);
            imbRem = itemView.findViewById(R.id.imbRem);
            imbAdd = itemView.findViewById(R.id.imbAdd);
        }
    }
}
