package com.example.ecommerce.model;

import com.example.ecommerce.helper.FirebaseHelper;
import com.google.firebase.database.DatabaseReference;

import java.io.Serializable;

public class FormaPagamento implements Serializable {

    private String id;
    private String nome;
    private String descricao;
    private double Valor;
    private String tipoValor; // "DESC ou ACRES"
    private boolean opcao = false;
    private long posicao;

    public FormaPagamento() {
        DatabaseReference pagamentoRef = FirebaseHelper.getDatabaseReference();
        this.setId(pagamentoRef.push().getKey());
        this.setPosicao(System.currentTimeMillis());
    }

    public void salvar() {
        DatabaseReference pagamentoRef = FirebaseHelper.getDatabaseReference()
                .child("formapagamento")
                .child(this.getId());
        pagamentoRef.setValue(this);
    }

    public void remover() {
        DatabaseReference pagamentoRef = FirebaseHelper.getDatabaseReference()
                .child("formapagamento")
                .child(this.getId());
        pagamentoRef.removeValue();
    }

    public long getPosicao() {
        return posicao;
    }

    public void setPosicao(long posicao) {
        this.posicao = posicao;
    }

    public boolean isOpcao() {
        return opcao;
    }

    public void setOpcao(boolean opcao) {
        this.opcao = opcao;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public double getValor() {
        return Valor;
    }

    public void setValor(double valor) {
        Valor = valor;
    }

    public String getTipoValor() {
        return tipoValor;
    }

    public void setTipoValor(String tipoValor) {
        this.tipoValor = tipoValor;
    }
}
