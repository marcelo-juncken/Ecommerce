package com.example.ecommerce.model;

import com.example.ecommerce.helper.FirebaseHelper;
import com.google.firebase.database.DatabaseReference;

import java.util.List;

public class Produto {
    private String id;
    private List<String> imagensUrl;
    private String titulo;
    private String descricao;
    private double de;
    private double por;
    private List<Categoria> categoriaList;

    public Produto() {
        DatabaseReference produtoRef = FirebaseHelper.getDatabaseReference();
        this.setId(produtoRef.push().getKey());
    }


    public void salvar() {
        DatabaseReference produtoRef = FirebaseHelper.getDatabaseReference()
                .child("produtos")
                .child(getId());
        produtoRef.setValue(this);
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public List<String> getImagensUrl() {
        return imagensUrl;
    }


    public void setImagensUrl(List<String> imagensUrl) {
        this.imagensUrl = imagensUrl;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public double getDe() {
        return de;
    }

    public void setDe(double de) {
        this.de = de;
    }

    public double getPor() {
        return por;
    }

    public void setPor(double por) {
        this.por = por;
    }

    public List<Categoria> getCategoriaList() {
        return categoriaList;
    }

    public void setCategoriaList(List<Categoria> categoriaList) {
        this.categoriaList = categoriaList;
    }


}
