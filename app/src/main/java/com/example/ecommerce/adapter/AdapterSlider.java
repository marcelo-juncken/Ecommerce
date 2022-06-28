package com.example.ecommerce.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.ecommerce.R;
import com.example.ecommerce.model.ImagemUpload;
import com.smarteist.autoimageslider.SliderViewAdapter;
import com.squareup.picasso.Picasso;

import java.util.List;

public class AdapterSlider extends SliderViewAdapter<AdapterSlider.MyViewHolder> {

    private final List<ImagemUpload> imagemUploadList;

    public AdapterSlider(List<ImagemUpload> imagemUploadList) {
        this.imagemUploadList = imagemUploadList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_slide_imagem, parent, false  );
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder viewHolder, int position) {
        ImagemUpload imagemUpload = imagemUploadList.get(position);
        Picasso.get().load(imagemUpload.getCaminhoImagem()).into(viewHolder.imgSlide);

    }

    @Override
    public int getCount() {
        return imagemUploadList.size();
    }

    static class MyViewHolder extends SliderViewAdapter.ViewHolder{

        ImageView imgSlide;
        public MyViewHolder(View itemView) {
            super(itemView);
            imgSlide = itemView.findViewById(R.id.imgSlide);
        }
    }

}
