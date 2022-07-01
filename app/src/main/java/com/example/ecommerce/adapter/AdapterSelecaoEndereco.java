package com.example.ecommerce.adapter;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ecommerce.R;
import com.example.ecommerce.helper.RecyclerRowMoveCallback;
import com.example.ecommerce.model.Endereco;

import java.util.Collections;
import java.util.List;

public class AdapterSelecaoEndereco extends RecyclerView.Adapter<AdapterSelecaoEndereco.MyViewHolder>{

    private List<Endereco> enderecoList;
    private onClick onClickList;

    public AdapterSelecaoEndereco(List<Endereco> enderecoList, onClick onClickList) {
        this.enderecoList = enderecoList;
        this.onClickList = onClickList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_selecao_endereco_adapter, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Endereco endereco = enderecoList.get(position);

        holder.txtApelido.setText(endereco.getNomeEndereco());

        StringBuilder localEndereco = new StringBuilder()
                .append(endereco.getLogradouro())
                .append(", ")
                .append(endereco.getNumero())
                .append(", ")
                .append(endereco.getBairro())
                .append(", ")
                .append(endereco.getLocalidade())
                .append(" - ")
                .append(endereco.getUf())
                .append("\n")
                .append("CEP: ")
                .append(endereco.getCep());


        holder.txtEndereco.setText(localEndereco);

        holder.itemView.setOnClickListener(v -> {
            holder.rbEndereco.setChecked(true);
            onClickList.onClicK(endereco);
        });
    }


    @Override
    public int getItemCount() {
        return enderecoList.size();
    }


    public interface onClick {
        void onClicK(Endereco endereco);
    }

    static class MyViewHolder extends RecyclerView.ViewHolder {

        TextView txtApelido, txtEndereco;
        RadioButton rbEndereco;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            txtApelido = itemView.findViewById(R.id.txtApelido);
            txtEndereco = itemView.findViewById(R.id.txtEndereco);
            rbEndereco = itemView.findViewById(R.id.rbEndereco);
        }
    }


}
