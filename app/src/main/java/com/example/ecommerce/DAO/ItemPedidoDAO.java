package com.example.ecommerce.DAO;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.example.ecommerce.model.ImagemUpload;
import com.example.ecommerce.model.ItemPedido;
import com.example.ecommerce.model.Produto;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ItemPedidoDAO {

    private final SQLiteDatabase write;
    private final SQLiteDatabase read;

    public ItemPedidoDAO(Context context) {
        DBHelper dbHelper = new DBHelper(context);
        write = dbHelper.getWritableDatabase();
        read = dbHelper.getReadableDatabase();
    }


    public boolean salvar(ItemPedido itemPedido) {

        ContentValues values = new ContentValues();
        values.put("id_produto", itemPedido.getIdProduto());
        values.put("valor", itemPedido.getValor());
        values.put("quantidade", itemPedido.getQuantidade());

        try {
            write.insert(DBHelper.TABELA_ITEM_PEDIDO, null, values);
            Log.i("INFO_DB", "onCreate: Sucesso ao salvar a tabela.");
        } catch (Exception e) {
            Log.i("INFO_DB", "onCreate: Erro ao salvar itemPedido." + e.getMessage());
            return false;
        }

        return true;
    }

    public boolean editarProduto(ItemPedido itemPedido) {

        ContentValues values = new ContentValues();
        values.put("quantidade", itemPedido.getQuantidade());

        String WHERE = " id=?";
        String[] args = {String.valueOf(itemPedido.getId())};

        try {
            write.update(DBHelper.TABELA_ITEM_PEDIDO, values, WHERE, args);
            Log.i("INFO_DB", "onCreate: Sucesso ao salvar a tabela.");
        } catch (Exception e) {
            Log.i("INFO_DB", "onCreate: Erro ao atualizar o itemPedido." + e.getMessage());
            return false;
        }

        return true;
    }

    public boolean removerProduto(Produto produto) {

        String WHERE = " id=?";
        String[] args = {String.valueOf(produto.getIdLocal())};

        try {
            write.delete(DBHelper.TABELA_ITEM_PEDIDO, WHERE, args);
            Log.i("INFO_DB", "onCreate: Sucesso ao deletar item.");
        } catch (Exception e) {
            Log.i("INFO_DB", "onCreate: Erro ao deletar o item." + e.getMessage());
            return false;
        }

        return true;
    }

    public Produto getProduto(int idProduto) {
        Produto produto = null;

        String sql = "SELECT * FROM " + DBHelper.TABELA_ITEM + " WHERE id = " + idProduto + ";";
        Cursor cursor = read.rawQuery(sql, null);

        while (cursor.moveToNext()) {
            int id = cursor.getInt(cursor.getColumnIndexOrThrow("id"));
            String id_firebase = cursor.getString(cursor.getColumnIndexOrThrow("id_firebase"));
            String titulo = cursor.getString(cursor.getColumnIndexOrThrow("nome"));
            double valor = cursor.getDouble(cursor.getColumnIndexOrThrow("valor"));
            String url_imagem = cursor.getString(cursor.getColumnIndexOrThrow("url_imagem"));

            produto = new Produto();
            produto.setIdLocal(id);
            produto.setTitulo(titulo);
            produto.setValorAtual(valor);
            produto.setId(id_firebase);

            ImagemUpload imagemUpload = new ImagemUpload();
            imagemUpload.setCaminhoImagem(url_imagem);
            imagemUpload.setIndex(0);


            Map<String, ImagemUpload> imagemUploadMap = new HashMap<>();
            imagemUploadMap.put("0", imagemUpload);
            produto.setImagemUploadMap(imagemUploadMap);
        }
        cursor.close();

        return produto;
    }

    public List<ItemPedido> getList() {
        List<ItemPedido> itemPedidoList = new ArrayList<>();

        String sql = "SELECT * FROM " + DBHelper.TABELA_ITEM_PEDIDO + ";";
        Cursor cursor = read.rawQuery(sql, null);

        while (cursor.moveToNext()) {
            int id = cursor.getInt(cursor.getColumnIndexOrThrow("id"));
            String id_produto = cursor.getString(cursor.getColumnIndexOrThrow("id_produto"));
            double valor = cursor.getDouble(cursor.getColumnIndexOrThrow("valor"));
            int quantidade = cursor.getInt(cursor.getColumnIndexOrThrow("quantidade"));

            ItemPedido itemPedido = new ItemPedido();
            itemPedido.setIdProduto(id_produto);
            itemPedido.setId(id);
            itemPedido.setValor(valor);
            itemPedido.setQuantidade(quantidade);

            itemPedidoList.add(itemPedido);

        }
        cursor.close();

        return itemPedidoList;
    }

    public Double getTotalCarrinho() {
        double total = 0;
        for (ItemPedido itemPedido : getList()) {
            total += ((double) itemPedido.getQuantidade() * itemPedido.getValor());
        }

        return total;
    }
}
