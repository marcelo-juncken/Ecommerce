package com.example.ecommerce.fragment.loja;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;

import com.example.ecommerce.R;
import com.example.ecommerce.activity.loja.LojaFormProdutoActivity;
import com.example.ecommerce.adapter.AdapterProduto;
import com.example.ecommerce.databinding.DialogLojaProdutoBinding;
import com.example.ecommerce.databinding.FragmentProdutosLojaBinding;
import com.example.ecommerce.helper.FirebaseHelper;
import com.example.ecommerce.model.ImagemUpload;
import com.example.ecommerce.model.Produto;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.ListResult;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ProdutosLojaFragment extends Fragment implements AdapterProduto.onClickListener {

    private FragmentProdutosLojaBinding binding;

    private AdapterProduto adapterProduto;
    private final List<Produto> produtoList = new ArrayList<>();

    private AlertDialog dialog;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentProdutosLojaBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        configCliques();
        configRV();
    }

    @Override
    public void onStart() {
        super.onStart();
        recuperaProdutos();
    }

    private void recuperaProdutos() {
        DatabaseReference produtosRef = FirebaseHelper.getDatabaseReference()
                .child("produtos");
        produtosRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                produtoList.clear();
                if (snapshot.exists()) {
                    for (DataSnapshot ds : snapshot.getChildren()) {
                        produtoList.add(0, ds.getValue(Produto.class));
                    }
                    binding.txtInfo.setText("");
                } else {
                    binding.txtInfo.setText("Nenhum produto cadastrado.");
                }
                binding.progressBar.setVisibility(View.GONE);
                adapterProduto.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                binding.progressBar.setVisibility(View.GONE);
                binding.txtInfo.setText("Erro ao carregar página");
            }
        });
    }

    private void configRV() {
        binding.rvProdutos.setLayoutManager(new GridLayoutManager(requireContext(), 2));
        binding.rvProdutos.setHasFixedSize(true);
        adapterProduto = new AdapterProduto(R.layout.item_produto_list, produtoList, getContext(), this, false, null, null);
        binding.rvProdutos.setAdapter(adapterProduto);
    }

    private void configCliques() {
        binding.toolbar.imbAdd.setOnClickListener(v -> startActivity(new Intent(requireActivity(), LojaFormProdutoActivity.class)));
    }

    private void showDialog(Produto produto) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext(), R.style.AlertDialog);

        DialogLojaProdutoBinding dialogBinding = DialogLojaProdutoBinding
                .inflate(LayoutInflater.from(requireContext()));

        List<ImagemUpload> imagemUploadList = new ArrayList<>(produto.getImagemUploadMap().values());
        Collections.sort(imagemUploadList, (o1, o2) -> Math.toIntExact(o1.getIndex() - o2.getIndex()));

        Picasso.get().load(imagemUploadList.get(0).getCaminhoImagem()).error(R.drawable.ic_close).into(dialogBinding.imgProduto);
        dialogBinding.txtNomeProduto.setText(produto.getTitulo());
        dialogBinding.cbRascunho.setChecked(produto.isRascunho());

        dialogBinding.imbFechar.setOnClickListener(v -> {
            dialog.dismiss();
        });
        dialogBinding.cvRascunho.setOnClickListener(v -> {
            dialogBinding.cbRascunho.setChecked(!dialogBinding.cbRascunho.isChecked());
            produto.setRascunho(!produto.isRascunho());
            produto.salvar(false);

        });

        dialogBinding.cvEditar.setOnClickListener(v -> {
            dialog.dismiss();

            Intent intent = new Intent(requireActivity(), LojaFormProdutoActivity.class);
            intent.putExtra("produtoSelecionado", produto.getId());
            startActivity(intent);

        });

        dialogBinding.cvRemover.setOnClickListener(v -> {
            dialog.dismiss();

            binding.progressBar.setVisibility(View.VISIBLE);
            binding.txtInfo.setText("Deletando produto...");
            deletarFotosProduto(produto, () -> {
                produto.deletar();
                produtoList.remove(produto);
                if(produtoList.isEmpty()){
                    binding.txtInfo.setText("Nenhum produto cadastrado.");
                } else {
                    binding.txtInfo.setText("");
                }
                binding.progressBar.setVisibility(View.GONE);


                adapterProduto.notifyDataSetChanged();
            });

        });


        builder.setView(dialogBinding.getRoot());

        dialog = builder.create();
        dialog.show();

    }

    public interface FireBaseCallback {
        void onCallback();
    }

    public void deletarFotosProduto(Produto produto, FireBaseCallback myCallback) {
        StorageReference storageReference = FirebaseHelper.getStorageReference()
                .child("imagens")
                .child("produtos")
                .child(produto.getId());
        storageReference.listAll().addOnCompleteListener(task -> {

            for (int i = 0; i < task.getResult().getItems().size(); i++) {
                int finalI = i;
                task.getResult().getItems().get(i).delete().addOnCompleteListener(task1 -> {
                    if (finalI + 1 == task.getResult().getItems().size()) {
                        myCallback.onCallback();
                    }
                });

            }

        });


    }

    @Override
    public void onClicK(Produto produto) {
        showDialog(produto);
    }
}