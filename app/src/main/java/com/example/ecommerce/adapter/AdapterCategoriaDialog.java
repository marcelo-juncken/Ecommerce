package com.example.ecommerce.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ecommerce.R;
import com.example.ecommerce.model.Categoria;
import com.squareup.picasso.Picasso;

import java.util.List;

public class AdapterCategoriaDialog extends RecyclerView.Adapter<AdapterCategoriaDialog.MyViewHolder> {

    private final List<Categoria> categoriaList;
    private final onClickListener onClickListener;
    private final List<String> idsCategoriasSelecionadas;

    public AdapterCategoriaDialog(List<Categoria> categoriaList, AdapterCategoriaDialog.onClickListener onClickListener, List<String> idsCategoriasSelecionadas) {
        this.categoriaList = categoriaList;
        this.onClickListener = onClickListener;
        this.idsCategoriasSelecionadas = idsCategoriasSelecionadas;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_categoria_dialog, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Categoria categoria = categoriaList.get(position);

        if (idsCategoriasSelecionadas.contains(categoria.getId())){
            holder.cbCategoria.setChecked(true);
        } else {
            holder.cbCategoria.setChecked(false);
        }

        holder.txtCategoria.setText(categoria.getNome());
        Picasso.get().load(categoria.getUrlImagem()).into(holder.imgCategoria);

        holder.itemView.setOnClickListener(v -> {
            holder.cbCategoria.setChecked(!holder.cbCategoria.isChecked());
            onClickListener.onClick(categoria);
        });

    }

    @Override
    public int getItemCount() {
        return categoriaList.size();
    }


    public interface onClickListener {
        void onClick(Categoria categoria);
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {

        ImageView imgCategoria;
        CheckBox cbCategoria;
        TextView txtCategoria;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            imgCategoria = itemView.findViewById(R.id.imgCategoria);
            cbCategoria = itemView.findViewById(R.id.cbCategoria);
            txtCategoria = itemView.findViewById(R.id.txtCategoria);
        }
    }
}
