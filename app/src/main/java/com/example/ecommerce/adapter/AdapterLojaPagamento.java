package com.example.ecommerce.adapter;

import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ecommerce.R;
import com.example.ecommerce.helper.GetMask;
import com.example.ecommerce.helper.RecyclerRowMoveCallback;
import com.example.ecommerce.model.FormaPagamento;

import java.util.Collections;
import java.util.List;

public class AdapterLojaPagamento extends RecyclerView.Adapter<AdapterLojaPagamento.MyViewHolder> implements RecyclerRowMoveCallback.RecyclerViewRowTouchHelperContract {

    private int row_index = -1;
    private static int layout;
    private final List<FormaPagamento> formaPagamentoList;
    private final onClickListener onClickListener;
    private final Context context;

    public AdapterLojaPagamento(int layout, List<FormaPagamento> formaPagamentoList, AdapterLojaPagamento.onClickListener onClickListener, Context context) {
        AdapterLojaPagamento.layout = layout;
        this.formaPagamentoList = formaPagamentoList;
        this.onClickListener = onClickListener;
        this.context = context;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(layout, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        FormaPagamento formaPagamento = formaPagamentoList.get(position);

        holder.txtNome.setText(formaPagamento.getNome());
        holder.txtDescricao.setText(formaPagamento.getDescricao());

        if (layout == R.layout.item_forma_pagamento_adapter) {
            if (formaPagamento.isOpcao()) {
                holder.txtValor.setText(context.getString(R.string.valor_produto, GetMask.getValor(formaPagamento.getValor())));

                if (formaPagamento.getTipoValor().equals("DESC")) {
                    holder.txtTipoPagamento.setText("Desconto");
                } else if (formaPagamento.getTipoValor().equals("ACRES")) {
                    holder.txtTipoPagamento.setText("Acréscimo");
                } else {
                    holder.txtTipoPagamento.setText("");
                }
            } else {
                holder.txtValor.setText("");
                holder.txtTipoPagamento.setText("");
            }

        } else {
            if (row_index != position){
                holder.rbOpcao.setChecked(false);
            }
        }


        holder.itemView.setOnClickListener(v -> {
            if (layout == R.layout.item_pagamento_pedido_adapter) {
                holder.rbOpcao.setChecked(true);
                row_index = holder.getAdapterPosition();
                notifyDataSetChanged();
            }
            onClickListener.onClick(formaPagamento);
        });

    }

    @Override
    public int getItemCount() {
        return formaPagamentoList.size();
    }

    @Override
    public void onRowMoved(int from, int to) {

        if (from < to) {
            for (int i = from; i < to; i++) {
                trocaPosicoes(i, i + 1);
                Collections.swap(formaPagamentoList, i, i + 1);
            }
        } else {
            for (int i = from; i > to; i--) {
                trocaPosicoes(i, i - 1);
                Collections.swap(formaPagamentoList, i, i - 1);
            }
        }
        notifyItemMoved(from, to);

    }

    private void trocaPosicoes(int from, int to) {
        long positionTemp;
        positionTemp = formaPagamentoList.get(from).getPosicao(); // pega a posicao do 0 e guarda na positionTemp
        formaPagamentoList.get(from).setPosicao(formaPagamentoList.get(to).getPosicao()); // seta a posicao do 0 igual a do 1
        formaPagamentoList.get(to).setPosicao(positionTemp); // seta a posicao do 1 igual a positionTemp
        formaPagamentoList.get(from).salvar();
        formaPagamentoList.get(to).salvar();
    }

    @Override
    public void onRowSelected(RecyclerView.ViewHolder viewHolder) {
        viewHolder.itemView.setBackgroundResource(R.color.colorIconeOnBnv);
        viewHolder.itemView.setAlpha(0.8F);
    }

    @Override
    public void onRowClear(RecyclerView.ViewHolder viewHolder) {
        viewHolder.itemView.setBackgroundColor(Color.WHITE);
        viewHolder.itemView.setAlpha(1F);
    }


    public interface onClickListener {
        void onClick(FormaPagamento formaPagamento);
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {

        TextView txtNome, txtDescricao, txtValor, txtTipoPagamento;
        RadioButton rbOpcao;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            txtNome = itemView.findViewById(R.id.txtNome);
            txtDescricao = itemView.findViewById(R.id.txtDescricao);
            txtValor = itemView.findViewById(R.id.txtValor);
            txtTipoPagamento = itemView.findViewById(R.id.txtTipoPagamento);

            if (layout == R.layout.item_pagamento_pedido_adapter) {
                rbOpcao = itemView.findViewById(R.id.rbOpcao);
            }
        }
    }

}
