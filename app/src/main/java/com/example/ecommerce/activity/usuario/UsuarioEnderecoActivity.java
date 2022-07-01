package com.example.ecommerce.activity.usuario;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.example.ecommerce.adapter.AdapterEndereco;
import com.example.ecommerce.databinding.ActivityUsuarioEnderecoBinding;
import com.example.ecommerce.databinding.SnackbarDeleteEnderecoBinding;
import com.example.ecommerce.helper.FirebaseHelper;
import com.example.ecommerce.helper.RecyclerRowMoveCallback;
import com.example.ecommerce.model.Categoria;
import com.example.ecommerce.model.Endereco;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.tsuryo.swipeablerv.SwipeLeftRightCallback;

import java.util.ArrayList;
import java.util.List;

public class UsuarioEnderecoActivity extends AppCompatActivity implements AdapterEndereco.onClick {

    private ActivityUsuarioEnderecoBinding binding;
    private List<Endereco> enderecoList = new ArrayList<>();
    private AdapterEndereco adapterEndereco;

    private Snackbar snackbar;
    private SnackbarDeleteEnderecoBinding snackbarBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityUsuarioEnderecoBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        configRv();

        configCliques();
    }

    @Override
    protected void onStart() {
        super.onStart();
        recuperaEnderecos();
    }

    private void recuperaEnderecos() {
        enderecoList.clear();
        if (FirebaseHelper.getAutenticado()) {
            DatabaseReference enderecosRef = FirebaseHelper.getDatabaseReference()
                    .child("enderecos")
                    .child(FirebaseHelper.getIdFirebase());
            enderecosRef.orderByChild("posicao").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        for (DataSnapshot ds : snapshot.getChildren()) {
                            Endereco endereco = ds.getValue(Endereco.class);
                            if (endereco != null) {
                                enderecoList.add(0, endereco);
                            }
                        }
                        binding.txtInfo.setText("");
                    } else {
                        binding.txtInfo.setText("Nenhum endereço cadastrado");
                    }
                    binding.progressBar.setVisibility(View.GONE);
                    adapterEndereco.notifyDataSetChanged();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }
    }

    private void configRv() {
        binding.rvEnderecos.setLayoutManager(new LinearLayoutManager(getBaseContext()));
        binding.rvEnderecos.setHasFixedSize(true);
        adapterEndereco = new AdapterEndereco(enderecoList, this);

        ItemTouchHelper.Callback callback = new RecyclerRowMoveCallback(adapterEndereco, true);
        ItemTouchHelper touchHelper = new ItemTouchHelper(callback);
        touchHelper.attachToRecyclerView(binding.rvEnderecos);

        binding.rvEnderecos.setAdapter(adapterEndereco);

        binding.rvEnderecos.setListener(new SwipeLeftRightCallback.Listener() {
            @Override
            public void onSwipedLeft(int position) {
            }

            @Override
            public void onSwipedRight(int position) {
                snackbarDeletaItem(position);
            }
        });

    }

    public void snackbarDeletaItem(int posicao) {
        LinearLayout.LayoutParams objLayoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        snackbar = Snackbar.make(binding.getRoot(), "", Snackbar.LENGTH_INDEFINITE).setAnimationMode(BaseTransientBottomBar.ANIMATION_MODE_SLIDE);
        // Get the Snackbar's layout view

        Snackbar.SnackbarLayout layout = (Snackbar.SnackbarLayout) snackbar.getView();

        // Set snackbar layout params

        layout.setPadding(0, 0, 0, 0);


        // Inflate our custom view
        snackbarBinding = SnackbarDeleteEnderecoBinding.inflate(LayoutInflater.from(this));


        objLayoutParams.setMargins(0, 0, 0, 0);

        snackbarBinding.txtApelido.setText(enderecoList.get(posicao).getNomeEndereco());
        // Configure our custom view
        snackbarBinding.btnNao.setOnClickListener(v -> {
            adapterEndereco.notifyDataSetChanged();
            snackbar.dismiss();
        });

        snackbarBinding.btnSim.setOnClickListener(v -> {

            snackbarBinding.progressBar.setVisibility(View.VISIBLE);
            enderecoList.get(posicao).deletar();
            enderecoList.remove(posicao);
            adapterEndereco.notifyItemRemoved(posicao);
            if (enderecoList.isEmpty()) binding.txtInfo.setText("Nenhum endereço cadastrado.");
            snackbar.dismiss();
        });

        // Add the view to the Snackbar's layout
        layout.addView(snackbarBinding.getRoot(), objLayoutParams);
        // Show the Snackbar
        snackbar.show();
    }

    private void configCliques() {
        binding.include3.txtTitulo.setText("Meus endereços");
        binding.include3.include.ibVoltar.setOnClickListener(v -> finish());
        binding.include3.imbAdd.setOnClickListener(v -> startActivity(new Intent(this, UsuarioFormEnderecoActivity.class)));
    }

    @Override
    public void onClicK(Endereco endereco) {
        Intent intent = new Intent(this, UsuarioFormEnderecoActivity.class);
        intent.putExtra("enderecoSelecionado", endereco);
        startActivity(intent);
    }
}