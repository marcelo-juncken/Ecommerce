package com.example.ecommerce.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
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

    private final List<FormaPagamento> formaPagamentoList;
    private final onClickListener onClickListener;
    private final Context context;

    public AdapterLojaPagamento(List<FormaPagamento> formaPagamentoList, AdapterLojaPagamento.onClickListener onClickListener, Context context) {
        this.formaPagamentoList = formaPagamentoList;
        this.onClickListener = onClickListener;
        this.context = context;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_forma_pagamento_adapter, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        FormaPagamento formaPagamento = formaPagamentoList.get(position);

        holder.txtNome.setText(formaPagamento.getNome());
        holder.txtDescricao.setText(formaPagamento.getDescricao());

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
            holder.txtValor.setText("Nenhuma opção de desconto ou acréscimo selecionada");
            holder.txtTipoPagamento.setText("");
        }

        holder.itemView.setOnClickListener(v -> onClickListener.onClick(formaPagamento));

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

        ImageView imgDrag;
        TextView txtNome, txtDescricao, txtValor, txtTipoPagamento;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            txtNome = itemView.findViewById(R.id.txtNome);
            txtDescricao = itemView.findViewById(R.id.txtDescricao);
            txtValor = itemView.findViewById(R.id.txtValor);
            txtTipoPagamento = itemView.findViewById(R.id.txtTipoPagamento);

            imgDrag = itemView.findViewById(R.id.imgDrag);

        }
    }


}
