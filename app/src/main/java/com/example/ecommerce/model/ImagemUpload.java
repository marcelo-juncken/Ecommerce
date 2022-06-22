package com.example.ecommerce.model;

import java.io.Serializable;

public class ImagemUpload implements Serializable {

    private long index;
    private String caminhoImagem;

    public ImagemUpload(){
    }

    public ImagemUpload(String caminhoImagem) {
        this.index = System.currentTimeMillis();
        this.caminhoImagem = caminhoImagem;
    }

    public long getIndex() {
        return index;
    }

    public void setIndex(long index) {
        this.index = index;
    }

    public String getCaminhoImagem() {
        return caminhoImagem;
    }

    public void setCaminhoImagem(String caminhoImagem) {
        this.caminhoImagem = caminhoImagem;
    }
}
