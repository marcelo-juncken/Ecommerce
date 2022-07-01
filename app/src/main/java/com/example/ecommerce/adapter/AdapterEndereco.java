package com.example.ecommerce.adapter;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ecommerce.R;
import com.example.ecommerce.helper.RecyclerRowMoveCallback;
import com.example.ecommerce.model.Endereco;

import java.util.Collections;
import java.util.List;

public class AdapterEndereco extends RecyclerView.Adapter<AdapterEndereco.MyViewHolder> implements RecyclerRowMoveCallback.RecyclerViewRowTouchHelperContract {

    private List<Endereco> enderecoList;
    private onClick onClickList;

    public AdapterEndereco(List<Endereco> enderecoList, onClick onClickList) {
        this.enderecoList = enderecoList;
        this.onClickList = onClickList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_endereco_adapter, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Endereco endereco = enderecoList.get(position);

        holder.txtApelido.setText(endereco.getNomeEndereco());

        StringBuilder localEndereco = new StringBuilder()
                .append(endereco.getLogradouro())
                .append(", ")
                .append(endereco.getNumero());
        holder.txtEndereco.setText(localEndereco);

        holder.itemView.setOnClickListener(v -> onClickList.onClicK(endereco));
    }


    @Override
    public int getItemCount() {
        return enderecoList.size();
    }

    @Override
    public void onRowMoved(int from, int to) {

        if (from < to) {
            for (int i = from; i < to; i++) {
                trocaPosicoes(i, i + 1);
                Collections.swap(enderecoList, i, i + 1);
            }
        } else {
            for (int i = from; i > to; i--) {
                trocaPosicoes(i, i - 1);
                Collections.swap(enderecoList, i, i - 1);
            }
        }
        notifyItemMoved(from, to);
    }

    private void trocaPosicoes(int from, int to) {
        long positionTemp;
        positionTemp = enderecoList.get(from).getPosicao(); // pega a posicao do 0 e guarda na positionTemp
        enderecoList.get(from).setPosicao(enderecoList.get(to).getPosicao()); // seta a posicao do 0 igual a do 1
        enderecoList.get(to).setPosicao(positionTemp); // seta a posicao do 1 igual a positionTemp
        enderecoList.get(from).salvar();
        enderecoList.get(to).salvar();
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

    public interface onClick {
        void onClicK(Endereco endereco);
    }

    static class MyViewHolder extends RecyclerView.ViewHolder {

        TextView txtApelido, txtEndereco;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            txtApelido = itemView.findViewById(R.id.txtApelido);
            txtEndereco = itemView.findViewById(R.id.txtEndereco);
        }
    }


}
