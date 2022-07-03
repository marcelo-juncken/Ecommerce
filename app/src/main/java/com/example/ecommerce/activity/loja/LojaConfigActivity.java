package com.example.ecommerce.activity.loja;

import android.Manifest;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.ImageDecoder;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.InputType;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.ecommerce.R;
import com.example.ecommerce.databinding.ActivityLojaConfigBinding;
import com.example.ecommerce.helper.FirebaseHelper;
import com.example.ecommerce.helper.GetMask;
import com.example.ecommerce.model.Loja;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.normal.TedPermission;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class LojaConfigActivity extends AppCompatActivity {

    private ActivityLojaConfigBinding binding;

    private String caminhoImagem = null;

    private Loja loja;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLojaConfigBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        recuperaLoja();
        iniciaComponentes();
        configCliques();
    }

    private void configCliques() {
        binding.cvLoja.setOnClickListener(v -> verificaPermissaoGaleria());
        binding.include.include.ibVoltar.setOnClickListener(v -> finish());

        binding.btnSalvar.setOnClickListener(v -> {
            if (loja != null) {
                validaDados();
            } else {
                Toast.makeText(this, "Espere o carregamento da página", Toast.LENGTH_SHORT).show();
            }
        });


        binding.imgShow.setOnClickListener(v -> {
            binding.edtAccessToken.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
            binding.edtAccessToken.setSelection(binding.edtAccessToken.getText().length());
            binding.imgShow.setVisibility(View.GONE);
            binding.imgHide.setVisibility(View.VISIBLE);
        });

        binding.imgHide.setOnClickListener(v -> {
            binding.edtAccessToken.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
            binding.edtAccessToken.setSelection(binding.edtAccessToken.getText().length());
            binding.imgShow.setVisibility(View.VISIBLE);
            binding.imgHide.setVisibility(View.GONE);
        });
    }

    private void recuperaLoja() {
        DatabaseReference lojaRef = FirebaseHelper.getDatabaseReference()
                .child("loja");
        lojaRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    loja = snapshot.getValue(Loja.class);
                    configLoja();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void configLoja() {
        if (loja.getUrlLogo() != null) {
            Picasso.get().load(loja.getUrlLogo()).placeholder(R.drawable.ic_loadinggif).error(R.drawable.ic_empresa).into(binding.imgLoja);
        }

        if (loja.getPedidoMinimo() != 0) {
            binding.edtPedidoMinimo.setText(GetMask.getValor(loja.getPedidoMinimo()));
        }

        if (loja.getFreteGratis() != 0) {
            binding.edtFrete.setText(GetMask.getValor(loja.getPedidoMinimo()));
        }

        if (loja.getNome() != null) {
            binding.edtLoja.setText(loja.getNome());
        }

        if (loja.getCNPJ() != null) {
            binding.edtCNPJ.setText(loja.getCNPJ());
        }

        binding.edtPublicKey.setText(loja.getPublicKey());
        binding.edtAccessToken.setText(loja.getAccessToken());
        if (loja.getParcelas() != 0) {
            binding.edtParcelas.setText(String.valueOf(loja.getParcelas()));
        }
        statusButton(true);
    }

    private void validaDados() {
        String nomeLoja = binding.edtLoja.getText().toString().trim();
        String CNPJ = binding.edtCNPJ.getMasked();
        double frete = (double) binding.edtFrete.getRawValue() / 100;
        double pedidoMinimo = (double) binding.edtPedidoMinimo.getRawValue() / 100;

        String publicKey = binding.edtPublicKey.getText().toString().trim();
        String accessToken = binding.edtAccessToken.getText().toString().trim();
        String parcelasStr = binding.edtParcelas.getText().toString().trim();

        int parcelas = 0;
        if (!parcelasStr.isEmpty()) {
            parcelas = Integer.parseInt(parcelasStr);
        }

        if (!nomeLoja.isEmpty()) {
            if (!CNPJ.isEmpty()) {
                if (CNPJ.replace("_", "").replace(".", "").replace("/", "").replace("-", "").length() == 14) {
                    if (!publicKey.isEmpty()) {
                        if (!accessToken.isEmpty()) {
                            if (parcelas >= 1 && parcelas <= 12) {
                                ocultarTeclado();

                                if (caminhoImagem == null && loja.getUrlLogo() == null) {
                                    Toast.makeText(this, "Escolha uma imagem para a logo da loja", Toast.LENGTH_SHORT).show();
                                } else {
                                    statusButton(false);

                                    loja.setNome(nomeLoja);
                                    loja.setCNPJ(CNPJ);
                                    loja.setFreteGratis(frete);
                                    loja.setPedidoMinimo(pedidoMinimo);
                                    loja.setPublicKey(publicKey);
                                    loja.setAccessToken(accessToken);
                                    loja.setParcelas(parcelas);

                                    if (caminhoImagem != null) {
                                        salvarLogo();
                                    } else {
                                        loja.salvar();
                                        statusButton(true);
                                    }
                                }
                            } else {
                                binding.edtParcelas.requestFocus();
                                binding.edtParcelas.setError("A parcela deve ser um número inteiro entre 1 e 12");
                            }
                        } else {
                            binding.edtAccessToken.requestFocus();
                            binding.edtAccessToken.setError("Informe o Access Token");
                        }
                    } else {
                        binding.edtPublicKey.requestFocus();
                        binding.edtPublicKey.setError("Informe a Public Key");
                    }
                } else {
                    binding.edtCNPJ.requestFocus();
                    binding.edtCNPJ.setError("CNPJ inválido.");
                }
            } else {
                binding.edtCNPJ.requestFocus();
                binding.edtCNPJ.setError("Esse campo não pode estar vazio.");
            }
        } else {
            binding.edtLoja.requestFocus();
            binding.edtLoja.setError("Esse campo não pode estar vazio.");
        }
    }

    private void salvarLogo() {
        StorageReference logoRef = FirebaseHelper.getStorageReference()
                .child("imagens")
                .child("loja")
                .child(loja.getId() + ".jpeg");

        UploadTask uploadTask = logoRef.putFile(Uri.parse(caminhoImagem));
        uploadTask.addOnSuccessListener(taskSnapshot -> logoRef.getDownloadUrl().addOnCompleteListener(task -> {

            loja.setUrlLogo(task.getResult().toString());
            loja.salvar();
            caminhoImagem = null;
            statusButton(true);
        })).addOnFailureListener(e -> Toast.makeText(getBaseContext(), "Erro no upload da imagem.", Toast.LENGTH_SHORT).show());
    }


    private void verificaPermissaoGaleria() {
        PermissionListener permissionlistener = new PermissionListener() {
            @Override
            public void onPermissionGranted() {
                abreGaleria();
            }

            private void abreGaleria() {
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                resultLauncher.launch(intent);
            }

            @Override
            public void onPermissionDenied(List<String> deniedPermissions) {
                Toast.makeText(getBaseContext(), "Permissão negada", Toast.LENGTH_SHORT).show();
            }
        };

        TedPermission.create()
                .setPermissionListener(permissionlistener)
                .setDeniedTitle("Permissões negadas.")
                .setDeniedMessage("Para abrir a galeria é preciso aceitar a permissão. Deseja permitir?")
                .setDeniedCloseButtonText("Não")
                .setGotoSettingButtonText("Sim")
                .setPermissions(Manifest.permission.READ_EXTERNAL_STORAGE)
                .check();
    }


    private final ActivityResultLauncher<Intent> resultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK) {
                    if (result.getData() != null) {
                        Uri localImagemSelecionada = result.getData().getData();
                        caminhoImagem = localImagemSelecionada.toString();

                        Bitmap imagem;
                        try {
                            if (Build.VERSION.SDK_INT < 28) {
                                imagem = MediaStore.Images.Media.getBitmap(getContentResolver(), localImagemSelecionada);
                            } else {
                                ImageDecoder.Source source = ImageDecoder.createSource(getContentResolver(), localImagemSelecionada);
                                imagem = ImageDecoder.decodeBitmap(source);
                            }
                            binding.imgLoja.setImageBitmap(imagem);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                    }
                }
            }
    );

    private void statusButton(boolean isEnabled) {
        binding.btnSalvar.setEnabled(isEnabled);
        binding.cvLoja.setEnabled(isEnabled);
        if (isEnabled) {
            binding.progressBar.setVisibility(View.GONE);
        } else {
            binding.progressBar.setVisibility(View.VISIBLE);
        }
    }

    private void ocultarTeclado() {
        ((InputMethodManager) getSystemService(INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(
                binding.edtFrete.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS
        );
    }

    private void iniciaComponentes() {
        binding.edtPedidoMinimo.setLocale(new Locale("PT", "br"));
        binding.edtFrete.setLocale(new Locale("PT", "br"));
        binding.include.txtTitulo.setText("Configurações");
    }
}