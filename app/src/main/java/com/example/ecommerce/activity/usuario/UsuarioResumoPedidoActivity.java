package com.example.ecommerce.activity.usuario;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.example.ecommerce.DAO.ItemDAO;
import com.example.ecommerce.DAO.ItemPedidoDAO;
import com.example.ecommerce.R;
import com.example.ecommerce.databinding.ActivityUsuarioResumoPedidoBinding;
import com.example.ecommerce.helper.FirebaseHelper;
import com.example.ecommerce.helper.GetMask;
import com.example.ecommerce.model.Endereco;
import com.example.ecommerce.model.Produto;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

public class UsuarioResumoPedidoActivity extends AppCompatActivity {

    private ActivityUsuarioResumoPedidoBinding binding;
    private ItemDAO itemDAO;
    private ItemPedidoDAO itemPedidoDAO;

    private Endereco endereco;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityUsuarioResumoPedidoBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        itemDAO = new ItemDAO(getBaseContext());
        itemPedidoDAO = new ItemPedidoDAO(getBaseContext());

        recuperaEndereco();
        configCliques();
    }

    private void recuperaEndereco() {
        if (FirebaseHelper.getAutenticado()) {
            DatabaseReference enderecosRef = FirebaseHelper.getDatabaseReference()
                    .child("enderecos")
                    .child(FirebaseHelper.getIdFirebase());
            enderecosRef.orderByChild("posicao").limitToLast(1).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        for (DataSnapshot ds : snapshot.getChildren()) {
                            endereco = ds.getValue(Endereco.class);
                            configEndereco();
                            configDados();
                            break;
                        }
                    } else {
                        binding.txtEndereco.setText("Nenhum endereço cadastrado");
                        binding.btnEndereco.setText("Cadastrar endereço");
                        configDados();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                }
            });
        }
    }

    private void configEndereco(){
        StringBuilder enderecoEscolhido = new StringBuilder()
                    .append(endereco.getLogradouro())
                    .append(", ")
                    .append(endereco.getNumero())
                    .append("\n")
                    .append(endereco.getBairro())
                    .append(",\n")
                    .append(endereco.getLocalidade())
                    .append("/")
                    .append(endereco.getUf())
                    .append("\n")
                    .append("CEP: ")
                    .append(endereco.getCep());

        binding.txtEndereco.setText(enderecoEscolhido);
        binding.btnEndereco.setText("Alterar endereço de entrega");

    }

    private void configDados() {

        int frete = 0;
        binding.txtValorProdutos.setText(getString(R.string.valor_produto, GetMask.getValor(itemPedidoDAO.getTotalCarrinho())));
        double total = itemPedidoDAO.getTotalCarrinho() + frete;
        binding.txtValorTotal.setText(getString(R.string.valor_produto, GetMask.getValor(total)));
        binding.textValor.setText(getString(R.string.valor_produto, GetMask.getValor(total)));

        binding.progressBar.setVisibility(View.GONE);
    }


    private void configCliques() {
        binding.include6.txtTitulo.setText("Resumo do pedido");
        binding.include6.include.ibVoltar.setOnClickListener(v -> finish());
        binding.btnEndereco.setOnClickListener(v -> resultLauncher.launch(new Intent(this, UsuarioSelecionaEnderecoActivity.class)));
    }

    private final ActivityResultLauncher<Intent> resultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK) {
                    if (result.getData() != null) {
                        endereco = (Endereco) result.getData().getSerializableExtra("enderecoSelecionado");
                        configEndereco();
                    }
                }
            }
    );
}