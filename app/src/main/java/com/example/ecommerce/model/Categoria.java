package com.example.ecommerce.model;

import com.example.ecommerce.helper.FirebaseHelper;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.storage.StorageReference;

public class Categoria {
    private String id;
    private String urlImagem;
    private String nome;
    private boolean todas = false;
    private long posicao;

    public Categoria() {
        DatabaseReference categoriaRef = FirebaseHelper.getDatabaseReference();
        this.setId(categoriaRef.push().getKey());
        this.setPosicao(System.currentTimeMillis());
    }

    public void salvar() {
        DatabaseReference categoriaRef = FirebaseHelper.getDatabaseReference()
                .child("categorias")
                .child(this.getId());
        categoriaRef.setValue(this);

    }

    public void deletar() {
        DatabaseReference categoriaRef = FirebaseHelper.getDatabaseReference()
                .child("categorias")
                .child(this.getId());
        categoriaRef.removeValue();

        StorageReference storageReference = FirebaseHelper.getStorageReference()
                .child("imagens")
                .child("categorias")
                .child(getId() + ".jpeg");
        storageReference.delete();
    }

    public long getPosicao() {
        return posicao;
    }

    public void setPosicao(long posicao) {
        this.posicao = posicao;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUrlImagem() {
        return urlImagem;
    }

    public void setUrlImagem(String urlImagem) {
        this.urlImagem = urlImagem;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public boolean isTodas() {
        return todas;
    }

    public void setTodas(boolean todas) {
        this.todas = todas;
    }

}
