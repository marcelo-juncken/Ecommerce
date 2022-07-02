package com.example.ecommerce.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ecommerce.R;
import com.example.ecommerce.helper.FirebaseHelper;
import com.example.ecommerce.helper.GetMask;
import com.example.ecommerce.model.ImagemUpload;
import com.example.ecommerce.model.ItemPedido;
import com.example.ecommerce.model.Pedido;
import com.example.ecommerce.model.Produto;
import com.example.ecommerce.model.StatusPedido;
import com.example.ecommerce.model.Usuario;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AdapterUsuarioDetalhePedidos extends RecyclerView.Adapter<AdapterUsuarioDetalhePedidos.MyViewHolder> {

    private final List<ItemPedido> itemPedidoList;
    private final Context context;

    public AdapterUsuarioDetalhePedidos(List<ItemPedido> itemPedidoList, Context context) {
        this.itemPedidoList = itemPedidoList;
        this.context = context;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_produto_detalhe_pedido, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        ItemPedido itempedido = itemPedidoList.get(position);

        recuperaProduto(holder, itempedido.getIdProduto());

        holder.txtProduto.setText(itempedido.getNomeProduto());
        holder.txtQtd.setText(MessageFormat.format("Quantidade: {0}", itempedido.getQuantidade()));
        holder.txtValor.setText(context.getString(R.string.valor_produto, GetMask.getValor(itempedido.getValor() * itempedido.getQuantidade())));

    }

    private void recuperaProduto(MyViewHolder holder, String idProduto) {
        DatabaseReference produtoRef = FirebaseHelper.getDatabaseReference()
                .child("produtos")
                .child(idProduto);
        produtoRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    Produto produto = snapshot.getValue(Produto.class);
                    if (produto != null) {
                        List<ImagemUpload> imagemUploadList = new ArrayList<>(produto.getImagemUploadMap().values());
                        imagemUploadList.sort((o1, o2) -> Math.toIntExact(o1.getIndex() - o2.getIndex()));
                        Picasso.get().load(imagemUploadList.get(0).getCaminhoImagem()).into(holder.imgProduto);
                    }
                } else {
                    Picasso.get().load(R.drawable.placeholder_form_categorias).into(holder.imgProduto);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }


    @Override
    public int getItemCount() {
        return itemPedidoList.size();
    }

    static class MyViewHolder extends RecyclerView.ViewHolder {

        ImageView imgProduto;
        TextView txtProduto, txtQtd, txtValor;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            imgProduto = itemView.findViewById(R.id.imgProduto);
            txtProduto = itemView.findViewById(R.id.txtProduto);
            txtQtd = itemView.findViewById(R.id.txtQtd);
            txtValor = itemView.findViewById(R.id.txtValor);
        }
    }


}
