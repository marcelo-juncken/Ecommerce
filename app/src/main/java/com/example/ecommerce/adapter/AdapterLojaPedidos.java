package com.example.ecommerce.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ecommerce.R;
import com.example.ecommerce.helper.FirebaseHelper;
import com.example.ecommerce.helper.GetMask;
import com.example.ecommerce.model.Pedido;
import com.example.ecommerce.model.StatusPedido;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

public class AdapterLojaPedidos extends RecyclerView.Adapter<AdapterLojaPedidos.MyViewHolder> {

    private final List<Pedido> pedidoList;
    private final onClick onClickList;
    private final Context context;

    public AdapterLojaPedidos(List<Pedido> pedidoList, onClick onClickList, Context context) {
        this.pedidoList = pedidoList;
        this.onClickList = onClickList;
        this.context = context;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_loja_pedido_adapter, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Pedido pedido = pedidoList.get(position);

        holder.txtPedido.setText(pedido.getId());
        recuperaUsuario(holder, pedido.getIdCliente());

        holder.txtValor.setText(context.getString(R.string.valor_produto, GetMask.getValor(pedido.getTotal())));
        holder.txtData.setText(GetMask.getDate(pedido.getDataPedido(), 3));

        String status = StatusPedido.getStatus(pedido.getStatusPedido());

        switch (status) {
            case "Pendente":
                holder.txtStatus.setTextColor(ContextCompat.getColor(context, R.color.cor_status_pedido_pendente));
                break;
            case "Aprovado":
                holder.txtStatus.setTextColor(ContextCompat.getColor(context, R.color.cor_status_pedido_aprovado));
                break;
            case "Cancelado":
                holder.txtStatus.setTextColor(ContextCompat.getColor(context, R.color.cor_status_pedido_cancelado));
                break;
        }

        holder.txtStatus.setText(status);
        holder.btnDetalhes.setOnClickListener(v -> onClickList.onClicK(pedido, "detalhes"));
        holder.btnStatus.setOnClickListener(v -> onClickList.onClicK(pedido, "status"));
    }

    private void recuperaUsuario(MyViewHolder holder, String id){
        DatabaseReference usuarioRef = FirebaseHelper.getDatabaseReference()
                .child("usuarios")
                .child(id)
                .child("nome");
        usuarioRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    holder.txtCliente.setText(snapshot.getValue(String.class));
                } else {
                    holder.txtCliente.setText("Nome de cliente não encontrado");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                holder.txtCliente.setText("Erro ao carregar nome de cliente");
            }
        });
    }

    @Override
    public int getItemCount() {
        return pedidoList.size();
    }


    public interface onClick {
        void onClicK(Pedido pedido, String operacao);
    }

    static class MyViewHolder extends RecyclerView.ViewHolder {

        TextView txtPedido, txtStatus, txtCliente, txtValor, txtData;
        Button btnDetalhes, btnStatus;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            txtPedido = itemView.findViewById(R.id.txtPedido);
            txtStatus = itemView.findViewById(R.id.txtStatus);
            txtCliente = itemView.findViewById(R.id.txtCliente);
            txtValor = itemView.findViewById(R.id.txtValor);
            txtData = itemView.findViewById(R.id.txtData);
            btnDetalhes = itemView.findViewById(R.id.btnDetalhes);
            btnStatus = itemView.findViewById(R.id.btnStatus);
        }
    }


}
