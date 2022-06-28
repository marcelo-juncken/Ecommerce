package com.example.ecommerce.adapter;

import android.graphics.Color;
import android.graphics.PorterDuff;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ecommerce.R;
import com.example.ecommerce.helper.RecyclerRowMoveCallback;
import com.example.ecommerce.model.Categoria;
import com.squareup.picasso.Picasso;

import java.util.Collections;
import java.util.List;

public class AdapterCategoria extends RecyclerView.Adapter<AdapterCategoria.MyViewHolder> implements RecyclerRowMoveCallback.RecyclerViewRowTouchHelperContract {

    private static int layout;
    private final boolean background;
    private int row_index = 0;
    private final List<Categoria> categoriaList;
    private final onClickListener onClickListener;

    public AdapterCategoria(int layout, boolean background, List<Categoria> categoriaList, AdapterCategoria.onClickListener onClickListener) {
        AdapterCategoria.layout = layout;
        this.background = background;
        this.categoriaList = categoriaList;
        this.onClickListener = onClickListener;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(layout, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Categoria categoria = categoriaList.get(position);

        holder.txtCategoria.setText(categoria.getNome());
        Picasso.get().load(categoria.getUrlImagem()).into(holder.imgCategoria);

        if(background){
            holder.itemView.setOnClickListener(v -> {
                onClickListener.onClick(categoria);
                row_index = holder.getAdapterPosition();
                notifyDataSetChanged();
            });
            if(row_index == position){
                holder.itemView.setBackgroundResource(R.drawable.bg_categoria_home);
                holder.txtCategoria.setTextColor(Color.parseColor("#FFFFFF"));
                holder.imgCategoria.setColorFilter(Color.parseColor("#FFFFFF"), PorterDuff.Mode.SRC_IN);
            }else{
                holder.itemView.setBackgroundColor(Color.parseColor("#FFFFFF"));
                holder.txtCategoria.setTextColor(Color.parseColor("#808080"));
                holder.imgCategoria.setColorFilter(Color.parseColor("#000000"), PorterDuff.Mode.SRC_IN);
            }
        }else{
            holder.itemView.setOnClickListener(v -> onClickListener.onClick(categoria));
        }
    }

    @Override
    public int getItemCount() {
        return categoriaList.size();
    }

    @Override
    public void onRowMoved(int from, int to) {

        if (layout == R.layout.item_categoria_vertical) {
            if (from < to) {
                for (int i = from; i < to; i++) {
                    trocaPosicoes(i, i + 1);
                    Collections.swap(categoriaList, i, i + 1);
                }
            } else {
                for (int i = from; i > to; i--) {
                    trocaPosicoes(i, i - 1);
                    Collections.swap(categoriaList, i, i - 1);
                }
            }
            notifyItemMoved(from, to);
        }
    }

    private void trocaPosicoes(int from, int to) {
        if (layout == R.layout.item_categoria_vertical) {
            long positionTemp;
            positionTemp = categoriaList.get(from).getPosicao(); // pega a posicao do 0 e guarda na positionTemp
            categoriaList.get(from).setPosicao(categoriaList.get(to).getPosicao()); // seta a posicao do 0 igual a do 1
            categoriaList.get(to).setPosicao(positionTemp); // seta a posicao do 1 igual a positionTemp
            categoriaList.get(from).salvar();
            categoriaList.get(to).salvar();
        }

    }

    @Override
    public void onRowSelected(RecyclerView.ViewHolder viewHolder) {
        if (layout == R.layout.item_categoria_vertical) {
            viewHolder.itemView.setBackgroundResource(R.color.colorIconeOnBnv);
            viewHolder.itemView.setAlpha(0.8F);
        }

    }

    @Override
    public void onRowClear(RecyclerView.ViewHolder viewHolder) {
        if (layout == R.layout.item_categoria_vertical) {
            viewHolder.itemView.setBackgroundColor(Color.WHITE);
            viewHolder.itemView.setAlpha(1F);
        }

    }


    public interface onClickListener {
        void onClick(Categoria categoria);
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {

        ImageView imgCategoria;
        ImageView imgDrag;
        TextView txtCategoria;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            imgCategoria = itemView.findViewById(R.id.imgCategoria);
            txtCategoria = itemView.findViewById(R.id.txtCategoria);

            if (layout == R.layout.item_categoria_vertical) {
                imgDrag = itemView.findViewById(R.id.imgDrag);
            }
        }
    }


}
