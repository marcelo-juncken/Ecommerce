package com.example.ecommerce.activity.usuario;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.example.ecommerce.DAO.ItemPedidoDAO;
import com.example.ecommerce.R;
import com.example.ecommerce.databinding.ActivityUsuarioResumoPedidoBinding;
import com.example.ecommerce.helper.FirebaseHelper;
import com.example.ecommerce.helper.GetMask;
import com.example.ecommerce.model.Endereco;
import com.example.ecommerce.model.FormaPagamento;
import com.example.ecommerce.model.Pedido;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

public class UsuarioResumoPedidoActivity extends AppCompatActivity {

    private ActivityUsuarioResumoPedidoBinding binding;
    private ItemPedidoDAO itemPedidoDAO;

    private Endereco endereco;

    private FormaPagamento formaPagamento;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityUsuarioResumoPedidoBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        itemPedidoDAO = new ItemPedidoDAO(getBaseContext());

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            formaPagamento = (FormaPagamento) bundle.getSerializable("formaPagamentoSelecionada");
            configPagamento();
        }

        recuperaEndereco();
        configCliques();
    }

    private void finalizarPedido() {
        Pedido pedido = new Pedido();
        pedido.setIdCliente(FirebaseHelper.getIdFirebase());
        pedido.setEndereco(endereco);
        pedido.setItemPedidoList(itemPedidoDAO.getList());
        pedido.setPagamento(formaPagamento.getNome());

        if (formaPagamento.isOpcao()) {
            if (formaPagamento.getTipoValor().equals("DESC")) {
                pedido.setDesconto(formaPagamento.getValor());
            } else if (formaPagamento.getTipoValor().equals("ACRES")) {
                pedido.setAcrescimo(formaPagamento.getValor());
            }
        }

        pedido.setTotal(itemPedidoDAO.getTotalCarrinho());

        pedido.salvar(true);
        itemPedidoDAO.limparCarrinho();

        Intent intent = new Intent(this, MainActivityUsuario.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.putExtra("id", 1);
        startActivity(intent);


    }

    private void configPagamento() {
        if (formaPagamento.getTipoValor().equals("DESC")) {
            binding.txtTipo.setText("Desconto");
        } else if (formaPagamento.getTipoValor().equals("ACRES")) {
            binding.txtTipo.setText("Acréscimo");
        } else {
            binding.txtTipo.setText("Taxa");
        }
        binding.txtFormaPagamento.setText(formaPagamento.getNome());
        binding.txtValorDesconto.setText(getString(R.string.valor_produto, GetMask.getValor(formaPagamento.getValor())));
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

    private void configEndereco() {
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

        binding.progressBar.setVisibility(View.GONE);
    }


    private void configCliques() {
        binding.include6.txtTitulo.setText("Resumo do pedido");
        binding.include6.include.ibVoltar.setOnClickListener(v -> finish());
        binding.btnEndereco.setOnClickListener(v -> resultLauncher.launch(new Intent(this, UsuarioSelecionaEnderecoActivity.class)));

        binding.btnFinalizar.setOnClickListener(v -> {
            if (!itemPedidoDAO.getList().isEmpty()) {
                finalizarPedido();
            } else {
                Toast.makeText(this, "Carrinho vazio!", Toast.LENGTH_SHORT).show();
            }
        });

        binding.btnPagamento.setOnClickListener(v -> finish());
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