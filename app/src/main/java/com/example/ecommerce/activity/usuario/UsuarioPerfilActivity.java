package com.example.ecommerce.activity.usuario;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import com.example.ecommerce.databinding.ActivityUsuarioPerfilBinding;
import com.example.ecommerce.helper.FirebaseHelper;
import com.example.ecommerce.model.Usuario;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

public class UsuarioPerfilActivity extends AppCompatActivity {

    private ActivityUsuarioPerfilBinding binding;
    private Usuario usuario;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityUsuarioPerfilBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        iniciaComponentes();
        configCliques();
        recuperaUsuario();
    }

    private void recuperaUsuario() {
        if (FirebaseHelper.getAutenticado()) {
            DatabaseReference usuarioRef = FirebaseHelper.getDatabaseReference()
                    .child("usuarios")
                    .child(FirebaseHelper.getIdFirebase());
            usuarioRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        usuario = snapshot.getValue(Usuario.class);
                        if (usuario != null) {
                            configDados();
                        }
                    } else {
                        binding.progressBar.setVisibility(View.GONE);
                    }

                }
                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(getBaseContext(), "Erro, tente mais tarde.", Toast.LENGTH_SHORT).show();
                    binding.progressBar.setVisibility(View.GONE);
                }
            });
        } else {
            Toast.makeText(this, "Você não está autenticado", Toast.LENGTH_SHORT).show();
            finish();
        }

    }

    private void configDados() {
        binding.edtNome.setText(usuario.getNome());
        binding.edtTelefone.setText(usuario.getTelefone());
        binding.edtEmail.setText(usuario.getEmail());
        binding.progressBar.setVisibility(View.GONE);
    }

    private void validaDados() {
        String nome = binding.edtNome.getText().toString().trim();
        String telefone = binding.edtTelefone.getText().toString().trim();

        if (!nome.isEmpty()) {
            if (!telefone.isEmpty()) {
                if (telefone.replace("_", "").replace("-", "").replace("(", "").replace(")", "").replace(" ", "").length() == 11) {
                    binding.progressBar.setVisibility(View.VISIBLE);
                    ocultarTeclado();

                    if(usuario!=null){
                        usuario.setNome(nome);
                        usuario.setTelefone(telefone);
                        usuario.salvar();
                    }else{
                        Toast.makeText(this, "Aguarde, as informações estão sendo carregadas.", Toast.LENGTH_SHORT).show();
                    }


                    binding.progressBar.setVisibility(View.GONE);

                    Toast.makeText(this, "Perfil salvo com sucesso", Toast.LENGTH_SHORT).show();

                } else {
                    binding.edtTelefone.requestFocus();
                    binding.edtTelefone.setError("Telefone inválido.");
                }
            } else {
                binding.edtTelefone.requestFocus();
                binding.edtTelefone.setError("Esse campo não pode estar em branco.");
            }
        } else {
            binding.edtNome.requestFocus();
            binding.edtNome.setError("Informe seu nome.");
        }
    }

    private void configCliques() {
        binding.include10.include.ibVoltar.setOnClickListener(v -> finish());
        binding.include10.imbSalvar.setOnClickListener(v -> {
            if (FirebaseHelper.getAutenticado()) {
                validaDados();
            }
        });
    }

    private void ocultarTeclado() {
        ((InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(
                binding.edtEmail.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS
        );
    }

    private void iniciaComponentes() {
        binding.include10.txtTitulo.setText("Meus dados");
    }
}