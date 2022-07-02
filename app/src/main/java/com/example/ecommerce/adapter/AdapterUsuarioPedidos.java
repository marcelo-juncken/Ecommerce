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
import com.example.ecommerce.helper.GetMask;
import com.example.ecommerce.model.Pedido;
import com.example.ecommerce.model.StatusPedido;

import java.util.List;

public class AdapterUsuarioPedidos extends RecyclerView.Adapter<AdapterUsuarioPedidos.MyViewHolder> {

    private final List<Pedido> pedidoList;
    private final onClick onClickList;
    private final Context context;

    public AdapterUsuarioPedidos(List<Pedido> pedidoList, onClick onClickList, Context context) {
        this.pedidoList = pedidoList;
        this.onClickList = onClickList;
        this.context = context;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_usuario_pedido_adapter, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Pedido pedido = pedidoList.get(position);

        holder.txtPedido.setText(pedido.getId());
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
        holder.btnDetalhes.setOnClickListener(v -> onClickList.onClicK(pedido));
    }


    @Override
    public int getItemCount() {
        return pedidoList.size();
    }


    public interface onClick {
        void onClicK(Pedido pedido);
    }

    static class MyViewHolder extends RecyclerView.ViewHolder {

        TextView txtPedido, txtStatus, txtValor, txtData;
        Button btnDetalhes;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            txtPedido = itemView.findViewById(R.id.txtPedido);
            txtStatus = itemView.findViewById(R.id.txtStatus);
            txtValor = itemView.findViewById(R.id.txtValor);
            txtData = itemView.findViewById(R.id.txtData);
            btnDetalhes = itemView.findViewById(R.id.btnDetalhes);
        }
    }


}
