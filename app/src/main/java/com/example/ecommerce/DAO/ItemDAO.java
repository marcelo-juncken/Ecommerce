package com.example.ecommerce.DAO;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.example.ecommerce.model.ImagemUpload;
import com.example.ecommerce.model.ItemPedido;
import com.example.ecommerce.model.Produto;

import java.util.ArrayList;
import java.util.List;

public class ItemDAO {

    private final SQLiteDatabase write;
    private final SQLiteDatabase read;

    public ItemDAO(Context context) {
        DBHelper dbHelper = new DBHelper(context);
        write = dbHelper.getWritableDatabase();
        read = dbHelper.getReadableDatabase();
    }

    public long salvar(Produto produto) {
        long idRetorno = 0;

        List<ImagemUpload> imagemUploadList = new ArrayList<>(produto.getImagemUploadMap().values());
        imagemUploadList.sort((o1, o2) -> Math.toIntExact(o1.getIndex() - o2.getIndex()));

        ContentValues values = new ContentValues();
        values.put("id_firebase", produto.getId());
        values.put("nome", produto.getTitulo());
        values.put("valor", produto.getValorAtual());
        values.put("url_imagem", imagemUploadList.get(0).getCaminhoImagem());

        try {
            write.insert(DBHelper.TABELA_ITEM, null, values);
            Log.i("INFO_DB", "onCreate: Sucesso ao salvar a tabela.");
        } catch (Exception e) {
            Log.i("INFO_DB", "onCreate: Erro ao salvar item." + e.getMessage());
        }

        return idRetorno;
    }

    public boolean removerProduto(Produto produto) {

        String WHERE = " id=?";
        String[] args = {String.valueOf(produto.getIdLocal())};

        try {
            write.delete(DBHelper.TABELA_ITEM, WHERE, args);
            Log.i("INFO_DB", "onCreate: Sucesso ao deletar item.");
        } catch (Exception e) {
            Log.i("INFO_DB", "onCreate: Erro ao deletar o item." + e.getMessage());
            return false;
        }

        return true;
    }
}
