package com.example.ecommerce.activity.usuario;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import com.example.ecommerce.api.CEPService;
import com.example.ecommerce.databinding.ActivityUsuarioFormEnderecoBinding;
import com.example.ecommerce.model.Endereco;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class UsuarioFormEnderecoActivity extends AppCompatActivity {

    private ActivityUsuarioFormEnderecoBinding binding;
    private Endereco endereco;
    private Endereco enderecoFiltrado;
    private boolean novoEndereco = true;

    private Retrofit retrofit;

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
            binding.include4.txtTitulo.setText("Editar endereço");
            configEndereco();

        } else {
            binding.include4.txtTitulo.setText("Novo endereço");
        }
        iniciaRetrofit();
        configCliques();
    }

    private void iniciaRetrofit() {
        retrofit = new Retrofit
                .Builder()
                .baseUrl("https://viacep.com.br/ws/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    }

    private void configEndereco() {

        if (enderecoFiltrado != null) {
            binding.editUF.setText(enderecoFiltrado.getUf());
            binding.editLogradouro.setText(enderecoFiltrado.getLogradouro());
            binding.editBairro.setText(enderecoFiltrado.getBairro());
            binding.editMunicipio.setText(enderecoFiltrado.getLocalidade());

            enderecoFiltrado = null;
        }else{
            binding.editUF.setText(endereco.getUf());
            binding.editLogradouro.setText(endereco.getLogradouro());
            binding.editBairro.setText(endereco.getBairro());
            binding.editMunicipio.setText(endereco.getLocalidade());

            binding.editApelido.setText(endereco.getNomeEndereco());
            binding.editNumero.setText(endereco.getNumero());
            binding.editCEP.setText(endereco.getCep());
        }

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
                if(cep.replace("_", "").replace("-", "").length() == 8){
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
                }else{
                    binding.editCEP.requestFocus();
                    binding.editCEP.setError("CEP incompleto.");
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
        binding.btnBuscar.setOnClickListener(v -> buscarCep());
    }

    private void buscarCep() {
        statusButton(true);
        String cep = binding.editCEP.getMasked().replaceAll("_", "").replace("-", "");
        if (cep.length() == 8) {
            buscarEndereco(cep);
        } else {
            enderecoFiltrado = null;
            statusButton(false);
            Toast.makeText(this, "CEP inválido", Toast.LENGTH_SHORT).show();
        }
    }

    private void buscarEndereco(String cep) {

        binding.progressBar.setVisibility(View.VISIBLE);

        CEPService cepService = retrofit.create(CEPService.class);
        Call<Endereco> call = cepService.recuperaCEP(cep);

        call.enqueue(new Callback<Endereco>() {
            @Override
            public void onResponse(@NonNull Call<Endereco> call, @NonNull Response<Endereco> response) {
                if (response.isSuccessful()) {

                    enderecoFiltrado = response.body();

                    if (enderecoFiltrado != null) {
                        if (enderecoFiltrado.getLocalidade() == null) {
                            Toast.makeText(getBaseContext(), "CEP inválido", Toast.LENGTH_SHORT).show();
                            statusButton(false);
                        } else {
                            binding.editCEP.setError(null);
                            configEndereco();
                        }
                    }

                } else {
                    Toast.makeText(getBaseContext(), "Tente novamente mais tarde.", Toast.LENGTH_SHORT).show();
                    statusButton(false);
                }

            }

            @Override
            public void onFailure(@NonNull Call<Endereco> call, @NonNull Throwable t) {
                Toast.makeText(getBaseContext(), "Tente novamente mais tarde.", Toast.LENGTH_SHORT).show();
                statusButton(false);
            }
        });
    }
}