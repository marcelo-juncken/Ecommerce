package com.example.ecommerce.activity.usuario;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.example.ecommerce.adapter.AdapterSelecaoEndereco;
import com.example.ecommerce.databinding.ActivityUsuarioSelecionaEnderecoBinding;
import com.example.ecommerce.helper.FirebaseHelper;
import com.example.ecommerce.model.Endereco;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class UsuarioSelecionaEnderecoActivity extends AppCompatActivity implements AdapterSelecaoEndereco.onClick {

    private ActivityUsuarioSelecionaEnderecoBinding binding;

    private List<Endereco> enderecoList = new ArrayList<>();
    private AdapterSelecaoEndereco adapterEndereco;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityUsuarioSelecionaEnderecoBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.include7.txtTitulo.setText("Endereco de entrega");

        configCliques();
        configRv();
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
        adapterEndereco = new AdapterSelecaoEndereco(enderecoList,this);
        binding.rvEnderecos.setAdapter(adapterEndereco);
    }

    private void configCliques() {
        binding.btnCadastrar.setOnClickListener(v -> {
            resultLauncher.launch(new Intent(this, UsuarioFormEnderecoActivity.class));
        });
        binding.include7.include.ibVoltar.setOnClickListener(v -> finish());
    }

    @Override
    public void onClicK(Endereco endereco) {
        Intent intent = new Intent();
        intent.putExtra("enderecoSelecionado", endereco);
        setResult(RESULT_OK, intent);
        finish();
    }
    private final ActivityResultLauncher<Intent> resultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK) {
                    if (result.getData() != null) {
                        Endereco enderecoCadastrado = (Endereco) result.getData().getSerializableExtra("enderecoCadastrado");
                        binding.txtInfo.setText("");
                        enderecoList.add(0, enderecoCadastrado);
                        adapterEndereco.notifyItemInserted(0);
                    }
                }
            }
    );
}