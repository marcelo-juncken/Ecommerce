package com.example.ecommerce.autenticacao;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.ecommerce.R;
import com.example.ecommerce.databinding.ActivityCadastroBinding;
import com.example.ecommerce.helper.FirebaseHelper;
import com.example.ecommerce.model.Loja;
import com.example.ecommerce.model.Usuario;

public class CadastroActivity extends AppCompatActivity {

    private ActivityCadastroBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCadastroBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        configCliques();
    }

    private void validaDados() {
        String nome = binding.edtNome.getText().toString().trim();
        String email = binding.edtEmail.getText().toString().trim();
        String senha = binding.edtSenha.getText().toString().trim();
        String confirmasenha = binding.edtConfirmaSenha.getText().toString().trim();

        if (!nome.isEmpty()) {
            if (!email.isEmpty()) {
                if (!senha.isEmpty()) {
                    if (!confirmasenha.isEmpty()) {
                        if (confirmasenha.equals(senha)) {

                            ocultarTeclado();
                            binding.progressBar.setVisibility(View.VISIBLE);
                            binding.btnCadastrar.setEnabled(false);

                            Usuario usuario = new Usuario();
                            usuario.setNome(nome);
                            usuario.setEmail(email);
                            usuario.setSenha(senha);

                            criarConta(usuario);

                        } else {
                            binding.edtConfirmaSenha.requestFocus();
                            binding.edtSenha.setError("As senhas não batem.");
                            binding.edtConfirmaSenha.setError("As senhas não batem.");
                        }
                    } else {
                        binding.edtConfirmaSenha.requestFocus();
                        binding.edtConfirmaSenha.setError("Confirme sua senha.");
                    }
                } else {
                    binding.edtSenha.requestFocus();
                    binding.edtSenha.setError("Digite uma senha.");
                }
            } else {
                binding.edtEmail.requestFocus();
                binding.edtEmail.setError("Informe seu email.");
            }
        } else {
            binding.edtNome.requestFocus();
            binding.edtNome.setError("Informe seu nome.");
        }

    }


    private void criarConta(Usuario usuario) {
        FirebaseHelper.getAuth().createUserWithEmailAndPassword(
                usuario.getEmail(), usuario.getSenha()
        ).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                usuario.setId(task.getResult().getUser().getUid());
                usuario.salvar();

                Intent intent = new Intent();
                intent.putExtra("email", usuario.getEmail());
                setResult(RESULT_OK, intent);
                finish();
            } else {
                Toast.makeText(this, FirebaseHelper.validaErros(task.getException().getMessage()), Toast.LENGTH_SHORT).show();
                binding.progressBar.setVisibility(View.GONE);
                binding.btnCadastrar.setEnabled(true);
            }
        });
    }


    private void configCliques() {
        binding.imbVoltar.ibVoltar.setOnClickListener(v -> finish());

        binding.btnCadastrar.setOnClickListener(v -> validaDados());

        binding.txtLogin.setOnClickListener(v -> finish());
    }

    private void ocultarTeclado() {
        ((InputMethodManager) getSystemService(INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(
                binding.edtNome.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS
        );
    }
}