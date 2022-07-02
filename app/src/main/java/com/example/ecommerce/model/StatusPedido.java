package com.example.ecommerce.model;

public enum StatusPedido {
    PENDENTE, APROVADO, CANCELADO;

    public static String getStatus(StatusPedido status){
        switch (status) {
            case APROVADO:
                return "Aprovado";
            case CANCELADO:
                return "Cancelado";
            default:
                return "Pendente";
        }
    }
}
