package com.example.ecommerce.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.ImageDecoder;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ecommerce.R;
import com.example.ecommerce.model.ImagemUpload;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class AdapterProdutoFotos extends RecyclerView.Adapter<AdapterProdutoFotos.MyViewHolder> {

    private final OnClickListener onClickListener;
    private List<ImagemUpload> imagemUploadList = new ArrayList<>();

    public AdapterProdutoFotos(OnClickListener onClickListener, List<ImagemUpload> imagemUploadList) {
        this.onClickListener = onClickListener;
        this.imagemUploadList = imagemUploadList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_produto_foto, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        ImagemUpload imagemUpload = imagemUploadList.get(position);

        Picasso.get().load(imagemUpload.getCaminhoImagem()).into(holder.imgFoto);
        holder.cardView.setOnClickListener(v -> onClickListener.onClick(position, true));
        holder.imgDelete.setOnClickListener(v -> onClickListener.onClick(position, false));
    }


    @Override
    public int getItemCount() {
        return imagemUploadList.size();
    }

    public interface OnClickListener {
        void onClick(int position, boolean isEditing);
    }

    static class MyViewHolder extends RecyclerView.ViewHolder {
        ImageView imgFoto;
        ImageView imgDelete;
        CardView cardView;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            imgFoto = itemView.findViewById(R.id.imgFoto);
            imgDelete = itemView.findViewById(R.id.imgDelete);
            cardView = itemView.findViewById(R.id.cardView);
        }
    }
}
