package com.example.ecommerce.activity.app;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.ecommerce.R;
import com.example.ecommerce.adapter.AdapterUsuarioDetalhePedidos;
import com.example.ecommerce.databinding.ActivityDetalhesPedidoBinding;
import com.example.ecommerce.helper.GetMask;
import com.example.ecommerce.model.Pedido;

public class DetalhesPedidoActivity extends AppCompatActivity {

    private ActivityDetalhesPedidoBinding binding;

    private Pedido pedido;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityDetalhesPedidoBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            pedido = (Pedido) bundle.getSerializable("pedidoSelecionado");
            configEndereco();
            configPagamento();
        }
        iniciaComponentes();
        configCliques();
        configRv();
    }

    private void configEndereco() {
        StringBuilder enderecoEscolhido = new StringBuilder()
                .append(pedido.getEndereco().getLogradouro())
                .append(", ")
                .append(pedido.getEndereco().getNumero())
                .append("\n")
                .append(pedido.getEndereco().getBairro())
                .append(",\n")
                .append(pedido.getEndereco().getLocalidade())
                .append("/")
                .append(pedido.getEndereco().getUf())
                .append("\n")
                .append("CEP: ")
                .append(pedido.getEndereco().getCep());

        binding.txtEndereco.setText(enderecoEscolhido);

    }

    private void configPagamento() {
        double valorExtra;
        double totalPedido = pedido.getTotal();
        double frete = 0;

        if (pedido.getDesconto() > 0) {

            binding.txtTipo.setText("Desconto");
            valorExtra = pedido.getDesconto();
            binding.txtValorDesconto.setText(getString(R.string.valor_produto, GetMask.getValor(valorExtra)));
            totalPedido -= valorExtra;

        } else if (pedido.getAcrescimo() > 0) {

            binding.txtTipo.setText("Acréscimo");
            valorExtra = pedido.getAcrescimo();
            binding.txtValorDesconto.setText(getString(R.string.valor_produto, GetMask.getValor(valorExtra)));
            totalPedido += valorExtra;

        } else {
            binding.txtTipo.setText("Taxa");
            binding.txtValorDesconto.setText(getString(R.string.valor_produto, GetMask.getValor(0)));
        }

        totalPedido += frete;
        binding.txtValorTotal.setText(getString(R.string.valor_produto, GetMask.getValor(totalPedido)));
        binding.txtFormaPagamento.setText(pedido.getPagamento());

        binding.txtValorProdutos.setText(getString(R.string.valor_produto, GetMask.getValor(pedido.getTotal())));
    }

    private void configRv() {
        binding.rvProdutos.setLayoutManager(new LinearLayoutManager(getBaseContext()));
        binding.rvProdutos.setHasFixedSize(true);
        AdapterUsuarioDetalhePedidos adapterUsuarioDetalhePedidos = new AdapterUsuarioDetalhePedidos(pedido.getItemPedidoList(), getBaseContext());
        binding.rvProdutos.setAdapter(adapterUsuarioDetalhePedidos);
    }

    private void configCliques() {
        binding.include6.include.ibVoltar.setOnClickListener(v -> finish());
    }

    private void iniciaComponentes() {
        binding.include6.txtTitulo.setText("Detalhes do pedido");
    }
}