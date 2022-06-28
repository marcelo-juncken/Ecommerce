package com.example.ecommerce.model;

import com.example.ecommerce.helper.FirebaseHelper;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Exclude;
import com.google.firebase.storage.StorageReference;

import java.io.Serializable;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Produto implements Serializable {
    private String id;
    private int idLocal;
    private Map<String, ImagemUpload> imagemUploadMap;
    private String titulo;
    private String descricao;
    private double valorAntigo;
    private double valorAtual;
    private Map<String, Boolean> idCategorias;
    private boolean rascunho = false;

    public Produto() {
        DatabaseReference produtoRef = FirebaseHelper.getDatabaseReference();
        this.setId(produtoRef.push().getKey());
    }

    public void salvar(boolean novoProduto) {
        DatabaseReference produtoRef = FirebaseHelper.getDatabaseReference()
                .child("produtos")
                .child(this.getId());
        produtoRef.setValue(this);
    }

    public void salvarImagem(ImagemUpload imagemUpload) {
        DatabaseReference produtoRef = FirebaseHelper.getDatabaseReference()
                .child("produtos")
                .child(this.getId())
                .child("imagemUploadMap")
                .child(String.valueOf(imagemUpload.getIndex()));
        produtoRef.child("index").setValue(imagemUpload.getIndex());
        produtoRef.child("caminhoImagem").setValue(imagemUpload.getCaminhoImagem());
    }

    public void deletar(){
        DatabaseReference produtoRef = FirebaseHelper.getDatabaseReference()
                .child("produtos")
                .child(this.getId());
        produtoRef.removeValue();
    }


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Exclude
    public int getIdLocal() {
        return idLocal;
    }

    public void setIdLocal(int idLocal) {
        this.idLocal = idLocal;
    }


    public Map<String, ImagemUpload> getImagemUploadMap() {
        return imagemUploadMap;
    }

    public void setImagemUploadMap(Map<String, ImagemUpload> imagemUploadMap) {
        this.imagemUploadMap = imagemUploadMap;
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

    public double getValorAntigo() {
        return valorAntigo;
    }

    public void setValorAntigo(double valorAntigo) {
        this.valorAntigo = valorAntigo;
    }

    public double getValorAtual() {
        return valorAtual;
    }

    public void setValorAtual(double valorAtual) {
        this.valorAtual = valorAtual;
    }

    public Map<String, Boolean> getIdCategorias() {
        return idCategorias;
    }

    public void setIdCategorias(Map<String, Boolean> idCategorias) {
        this.idCategorias = idCategorias;
    }

    public boolean isRascunho() {
        return rascunho;
    }

    public void setRascunho(boolean rascunho) {
        this.rascunho = rascunho;
    }
}
