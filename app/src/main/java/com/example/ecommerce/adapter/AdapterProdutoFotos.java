package com.example.ecommerce.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.ImageDecoder;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ecommerce.R;

import java.io.IOException;
import java.util.List;

public class AdapterProdutoFotos extends RecyclerView.Adapter<AdapterProdutoFotos.MyViewHolder> {

    private List<String> fotosList;
    private OnClickListener onClickListener;
    private Context context;

    public AdapterProdutoFotos(List<String> fotosList, OnClickListener onClickListener, Context context) {
        this.fotosList = fotosList;
        this.onClickListener = onClickListener;
        this.context = context;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_produto_foto,parent , false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        String fotoPosition = fotosList.get(position);

        holder.imgFoto.setImageBitmap(getBitmap(Uri.parse(fotoPosition)));
        holder.cardView.setOnClickListener(v -> onClickListener.onClick(position,true));
        holder.imgDelete.setOnClickListener(v -> onClickListener.onClick(position,false));
    }

    private Bitmap getBitmap(Uri caminhoUri) {
        Bitmap bitmap = null;
        try {
            if (Build.VERSION.SDK_INT < 31) {
                bitmap = MediaStore.Images.Media.getBitmap(context.getContentResolver(), caminhoUri);
            } else {
                ImageDecoder.Source source = ImageDecoder.createSource(context.getContentResolver(), caminhoUri);
                bitmap = ImageDecoder.decodeBitmap(source);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        return bitmap;
    }
    @Override
    public int getItemCount() {
        return fotosList.size();
    }

    public interface OnClickListener{
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
