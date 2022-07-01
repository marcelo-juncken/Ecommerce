package com.example.ecommerce.activity.usuario;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import com.example.ecommerce.databinding.ActivityUsuarioFormEnderecoBinding;
import com.example.ecommerce.model.Endereco;

public class UsuarioFormEnderecoActivity extends AppCompatActivity {

    private ActivityUsuarioFormEnderecoBinding binding;
    private Endereco endereco;
    private boolean novoEndereco = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityUsuarioFormEnderecoBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            statusButton(true);
            novoEndereco = false;

            endereco = (Endereco) bundle.getSerializable("enderecoSelecionado");
            configEndereco();
        } else {
            binding.include4.txtTitulo.setText("Novo endereço");
        }
        configCliques();
    }

    private void configEndereco() {
        binding.include4.txtTitulo.setText("Editar endereço");

        binding.editApelido.setText(endereco.getNomeEndereco());
        binding.editCEP.setText(endereco.getCep());
        binding.editUF.setText(endereco.getUf());
        binding.editNumero.setText(endereco.getNumero());
        binding.editLogradouro.setText(endereco.getLogradouro());
        binding.editBairro.setText(endereco.getBairro());
        binding.editMunicipio.setText(endereco.getLocalidade());

        statusButton(false);
    }

    private void validaDados() {
        String apelido = binding.editApelido.getText().toString().trim();
        String cep = binding.editCEP.getText().toString().trim();
        String uf = binding.editUF.getText().toString().trim();
        String numero = binding.editNumero.getText().toString().trim();
        String logradouro = binding.editLogradouro.getText().toString().trim();
        String bairro = binding.editBairro.getText().toString().trim();
        String municipio = binding.editMunicipio.getText().toString().trim();

        if (!apelido.isEmpty()) {
            if (!cep.isEmpty()) {
                if (!uf.isEmpty()) {
                    if (!numero.isEmpty()) {
                        if (!logradouro.isEmpty()) {
                            if (!bairro.isEmpty()) {
                                if (!municipio.isEmpty()) {
                                    if (endereco == null) endereco = new Endereco();

                                    ocultarTeclado();
                                    statusButton(true);

                                    endereco.setNomeEndereco(apelido);
                                    endereco.setCep(cep);
                                    endereco.setUf(uf);
                                    endereco.setNumero(numero);
                                    endereco.setLogradouro(logradouro);
                                    endereco.setBairro(bairro);
                                    endereco.setLocalidade(municipio);

                                    endereco.salvar();
                                    if (novoEndereco) {
                                        Intent intent = new Intent();
                                        intent.putExtra("enderecoCadastrado", endereco);
                                        setResult(RESULT_OK, intent);
                                        finish();
                                    }
                                    statusButton(false);
                                    Toast.makeText(this, "Endereço salvo com sucesso!", Toast.LENGTH_SHORT).show();
                                } else {
                                    binding.editMunicipio.requestFocus();
                                    binding.editMunicipio.setError("Esse campo não pode estar em branco.");
                                }
                            } else {
                                binding.editBairro.requestFocus();
                                binding.editBairro.setError("Esse campo não pode estar em branco.");
                            }
                        } else {
                            binding.editLogradouro.requestFocus();
                            binding.editLogradouro.setError("Esse campo não pode estar em branco.");
                        }
                    } else {
                        binding.editNumero.requestFocus();
                        binding.editNumero.setError("Esse campo não pode estar em branco.");
                    }
                } else {
                    binding.editUF.requestFocus();
                    binding.editUF.setError("Esse campo não pode estar em branco.");
                }
            } else {
                binding.editCEP.requestFocus();
                binding.editCEP.setError("Esse campo não pode estar em branco.");
            }
        } else {
            binding.editApelido.requestFocus();
            binding.editApelido.setError("Esse campo não pode estar em branco.");
        }
    }

    private void statusButton(Boolean loading) {
        if (loading) {
            binding.progressBar.setVisibility(View.VISIBLE);
        } else {
            binding.progressBar.setVisibility(View.GONE);
        }
        binding.btnBuscar.setEnabled(!loading);
        binding.include4.imbSalvar.setEnabled(!loading);
    }

    private void ocultarTeclado() {
        ((InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(
                binding.editApelido.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS
        );
    }

    private void configCliques() {
        binding.include4.include.ibVoltar.setOnClickListener(v -> finish());
        binding.include4.imbSalvar.setOnClickListener(v -> validaDados());
    }
}