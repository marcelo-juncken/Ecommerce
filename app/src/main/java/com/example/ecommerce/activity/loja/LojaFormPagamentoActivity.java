package com.example.ecommerce.activity.loja;

import android.content.Context;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import androidx.appcompat.app.AppCompatActivity;

import com.example.ecommerce.R;
import com.example.ecommerce.databinding.ActivityLojaFormPagamentoBinding;
import com.example.ecommerce.helper.GetMask;
import com.example.ecommerce.model.FormaPagamento;

import java.util.Locale;

public class LojaFormPagamentoActivity extends AppCompatActivity {

    private ActivityLojaFormPagamentoBinding binding;
    private FormaPagamento formaPagamento;

    private boolean novoPagamento = true;
    private boolean checkedAcresc = false;
    private boolean checkedDesc = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLojaFormPagamentoBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        iniciaComponentes();
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            novoPagamento = false;
            formaPagamento = (FormaPagamento) bundle.getSerializable("formaPagamentoSelecionada");
            configDados();
        } else {
            opcaoChecked(false);
            binding.include9.txtTitulo.setText("Nova forma de pagamento");
        }
        configCliques();


    }

    private void configDados() {

        binding.include9.txtTitulo.setText("Editando forma de pagamento");

        binding.edtFormaPagamento.setText(formaPagamento.getNome());
        binding.edtDescricao.setText(formaPagamento.getDescricao());
        binding.edtValor.setText(GetMask.getValor(formaPagamento.getValor()));

        binding.cbOpcao.setChecked(formaPagamento.isOpcao());

        if (formaPagamento.getTipoValor().equals("DESC")) {
            binding.rbDesconto.setChecked(true);
            binding.rbAcrescimo.setChecked(false);
        } else if (formaPagamento.getTipoValor().equals("ACRES")) {
            binding.rbAcrescimo.setChecked(true);
            binding.rbDesconto.setChecked(false);
        }

        opcaoChecked(formaPagamento.isOpcao());
    }


    private void validaDados() {
        String nome = binding.edtFormaPagamento.getText().toString().trim();
        String descricao = binding.edtDescricao.getText().toString().trim();
        double valor = (double) binding.edtValor.getRawValue() / 100;

        if (!nome.isEmpty()) {
            if (!descricao.isEmpty()) {
                ocultarTeclado();
                statusButton(true);

                if (formaPagamento == null) formaPagamento = new FormaPagamento();

                formaPagamento.setNome(nome);
                formaPagamento.setDescricao(descricao);

                formaPagamento.setOpcao(binding.cbOpcao.isChecked());

                if (binding.rbAcrescimo.isChecked()) {
                    formaPagamento.setTipoValor("ACRES");
                } else if (binding.rbDesconto.isChecked()) {
                    formaPagamento.setTipoValor("DESC");
                } else {
                    formaPagamento.setTipoValor("");
                }

                formaPagamento.setValor(valor);

                formaPagamento.salvar();
                if (novoPagamento) finish();

                statusButton(false);

            } else {
                binding.edtDescricao.requestFocus();
                binding.edtDescricao.setError("Informação obrigatória");
            }
        } else {
            binding.edtFormaPagamento.requestFocus();
            binding.edtFormaPagamento.setError("Informação obrigatória");
        }
    }

    private void configCliques() {
        binding.include9.txtTitulo.setText("Forma de pagamento");

        binding.include9.imbSalvar.setOnClickListener(v -> validaDados());
        binding.include9.include.ibVoltar.setOnClickListener(v -> finish());

        binding.cbOpcao.setOnCheckedChangeListener((buttonView, isChecked) -> opcaoChecked(isChecked));

        binding.rbAcrescimo.setOnClickListener(v -> {
            if(checkedAcresc){
                binding.rgValor.clearCheck();
                checkedAcresc = false;
            }else{
                binding.rbAcrescimo.setChecked(true);
                checkedAcresc = true;
                checkedDesc = false;
            }
        });

        binding.rbDesconto.setOnClickListener(v -> {
            if(checkedDesc){
                binding.rgValor.clearCheck();
                checkedDesc = false;
            }else{
                binding.rbDesconto.setChecked(true);
                checkedDesc = true;
                checkedAcresc = false;
            }

        });
    }

    private void opcaoChecked(boolean isChecked) {
        if (isChecked) {
            binding.lLOpcoes.setBackgroundResource(R.drawable.bg_borda_laranja);
            binding.rbDesconto.setButtonTintList(ColorStateList.valueOf(getColor(R.color.cor_laranja)));
            binding.rbAcrescimo.setButtonTintList(ColorStateList.valueOf(getColor(R.color.cor_laranja)));
            binding.edtValor.setBackgroundResource(R.drawable.bg_edit_on);
        } else {
            binding.lLOpcoes.setBackgroundResource(R.drawable.bg_borda_cinza);
            binding.rbDesconto.setButtonTintList(ColorStateList.valueOf(getColor(R.color.colorIconeOffBnv)));
            binding.rbAcrescimo.setButtonTintList(ColorStateList.valueOf(getColor(R.color.colorIconeOffBnv)));
            binding.edtValor.setBackgroundResource(R.drawable.bg_edit_off);
        }
    }

    private void statusButton(Boolean loading) {
        if (loading) {
            binding.progressBar.setVisibility(View.VISIBLE);
        } else {
            binding.progressBar.setVisibility(View.GONE);
        }
        binding.include9.imbSalvar.setEnabled(!loading);
        binding.rgValor.setEnabled(!loading);
    }

    private void ocultarTeclado() {
        ((InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(
                binding.edtFormaPagamento.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS
        );
    }

    private void iniciaComponentes(){
        binding.edtValor.setLocale(new Locale("PT", "br"));
    }
}