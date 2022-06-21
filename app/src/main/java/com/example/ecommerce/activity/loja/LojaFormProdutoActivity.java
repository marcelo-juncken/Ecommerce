package com.example.ecommerce.activity.loja;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.ecommerce.R;
import com.example.ecommerce.adapter.AdapterProdutoFotos;
import com.example.ecommerce.databinding.ActivityLojaFormProdutoBinding;
import com.example.ecommerce.databinding.BottomSheetDialogBinding;
import com.example.ecommerce.helper.FirebaseHelper;
import com.example.ecommerce.model.Produto;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.normal.TedPermission;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class LojaFormProdutoActivity extends AppCompatActivity implements AdapterProdutoFotos.OnClickListener {

    private ActivityLojaFormProdutoBinding binding;

    private String currentPhotoPath;
    private String caminho;

    private List<String> listaImagens = new ArrayList<>();
    private List<String> urlImagens = new ArrayList<>();
    private AdapterProdutoFotos adapterProdutoFotos;

    private boolean isNew = true;

    private int fotoPosition;

    private Produto produto;

    private boolean adapterClickable = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLojaFormProdutoBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        configCliques();
        configRV();
    }

    private void bottomSheetDialog() {

        BottomSheetDialogBinding dialogBinding = BottomSheetDialogBinding.inflate(LayoutInflater.from(this));

        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(this, R.style.BottomSheetDialog);
        bottomSheetDialog.setContentView(dialogBinding.getRoot());
        bottomSheetDialog.show();

        dialogBinding.btnCamera.setOnClickListener(v -> {
            verificaPermissaoCamera();
            bottomSheetDialog.dismiss();
        });
        dialogBinding.btnGaleria.setOnClickListener(v -> {
            verificaPermissaoGaleria();
            bottomSheetDialog.dismiss();
        });
        dialogBinding.btnCancelar.setOnClickListener(v -> bottomSheetDialog.dismiss());

    }

    private void verificaPermissaoCamera() {
        PermissionListener permissionlistener = new PermissionListener() {
            @Override
            public void onPermissionGranted() {
                abreCamera();
            }

            @Override
            public void onPermissionDenied(List<String> deniedPermissions) {
                Toast.makeText(getBaseContext(), "Permissão negada", Toast.LENGTH_SHORT).show();
            }


        };

        showDialogPermissao(permissionlistener, new String[]{Manifest.permission.CAMERA}, "câmera");
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        currentPhotoPath = image.getAbsolutePath();
        return image;
    }

    private void abreCamera() {
        caminho = "Camera";
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        // Create the File where the photo should go
        File photoFile = null;
        try {
            photoFile = createImageFile();
        } catch (IOException ex) {
            // Error occurred while creating the File
        }
        // Continue only if the File was successfully created
        if (photoFile != null) {
            Uri photoURI = FileProvider.getUriForFile(this,
                    "com.example.ecommerce.fileprovider",
                    photoFile);
            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
            resultLauncher.launch(takePictureIntent);
        }
    }

    private void verificaPermissaoGaleria() {
        PermissionListener permissionlistener = new PermissionListener() {
            @Override
            public void onPermissionGranted() {
                abreGaleria();
            }

            private void abreGaleria() {
                caminho = "Galeria";
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                resultLauncher.launch(intent);
            }

            @Override
            public void onPermissionDenied(List<String> deniedPermissions) {
                Toast.makeText(getBaseContext(), "Permissão negada", Toast.LENGTH_SHORT).show();
            }


        };

        showDialogPermissao(permissionlistener, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, "galeria");
    }

    private void showDialogPermissao(PermissionListener permissionlistener, String[] permissoes, String msg) {
        TedPermission.create()
                .setPermissionListener(permissionlistener)
                .setDeniedTitle("Permissões negadas.")
                .setDeniedMessage("Para abrir a " + msg + " é preciso aceitar a permissão. Deseja permitir?")
                .setDeniedCloseButtonText("Não")
                .setGotoSettingButtonText("Sim")
                .setPermissions(permissoes)
                .check();
    }

    private void configCliques() {
        binding.include.include.ibVoltar.setOnClickListener(v -> finish());
        binding.cardViewAdd.setOnClickListener(v -> {
            isNew = true;
            bottomSheetDialog();
        });
        binding.btnSalvar.setOnClickListener(v -> validaDados());
    }

    private void validaDados() {
        String titulo = binding.edtTitulo.getText().toString().trim();
        String descricao = binding.edtDescricao.getText().toString().trim();
        String de = binding.edtDe.getText().toString().trim();
        String por = binding.edtPor.getText().toString().trim();

        if (!titulo.isEmpty()) {
            if (!descricao.isEmpty()) {
                if (!por.isEmpty()) {
                    ocultarTeclado();
                    if (produto == null) produto = new Produto();

                    produto.setTitulo(titulo);
                    produto.setDescricao(descricao);
                    statusButton();
                    salvaFotos();
                } else {
                    binding.edtPor.requestFocus();
                    binding.edtPor.setError("Esse campo não pode estar em branco");
                }
            } else {
                binding.edtDescricao.requestFocus();
                binding.edtDescricao.setError("Esse campo não pode estar em branco");
            }
        } else {
            binding.edtTitulo.requestFocus();
            binding.edtTitulo.setError("Esse campo não pode estar em branco");
        }
    }

    private void salvaFotos() {
        for (int i = 0; i < listaImagens.size(); i++) {
            StorageReference storageReference = FirebaseHelper.getStorageReference()
                    .child("imagens")
                    .child("produtos")
                    .child(produto.getId())
                    .child("imagem_" + i + ".jpeg");

            UploadTask uploadTask = storageReference.putFile(Uri.parse(listaImagens.get(i)));
            uploadTask.addOnSuccessListener(taskSnapshot -> storageReference.getDownloadUrl().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    String urlImagem = task.getResult().toString();
                    urlImagens.add(urlImagem);
                    Log.d("DEBUGFOTO", "salvaFotos: " + urlImagem + " - " + urlImagens.get(0));


                    if (urlImagens.size() == listaImagens.size()) {
                        produto.setImagensUrl(urlImagens);
                        produto.salvar();
                        finish();
                    }
                }
            })).addOnFailureListener(e -> Toast.makeText(this, "Falha no upload, tente mais tarde", Toast.LENGTH_SHORT).show());
        }


    }


    private final ActivityResultLauncher<Intent> resultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK) {
                    if (result.getData() != null) {

                        String caminhoImagem;
                        if (caminho.equals("Galeria")) {
                            Uri localImagemSelecionada = result.getData().getData();
                            caminhoImagem = localImagemSelecionada.toString();
                            if (isNew) {
                                listaImagens.add(0,caminhoImagem);
                            } else {
                                listaImagens.set(fotoPosition, caminhoImagem);
                            }
                        } else if (caminho.equals("Camera")) {
                            File file = new File(currentPhotoPath);
                            caminhoImagem = String.valueOf(file.toURI());
                            if (isNew) {
                                listaImagens.add(0,caminhoImagem);
                            } else {
                                listaImagens.set(fotoPosition, caminhoImagem);
                            }
                        }
                        adapterProdutoFotos.notifyDataSetChanged();
                    }
                }
            }
    );

    private void statusButton() {
        binding.progressBar.setVisibility(View.VISIBLE);
        binding.btnSalvar.setEnabled(false);
        binding.btnCategoria.setEnabled(false);
        binding.cardViewAdd.setEnabled(false);
        adapterClickable = false;
    }

    private void ocultarTeclado() {
        ((InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(
                binding.edtPor.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS
        );
    }

    private void configRV() {
        binding.rvFotosItens.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        binding.rvFotosItens.setHasFixedSize(true);
        adapterProdutoFotos = new AdapterProdutoFotos(listaImagens, this, getBaseContext());
        binding.rvFotosItens.setAdapter(adapterProdutoFotos);
    }


    @Override
    public void onClick(int position, boolean isEditing) {
        if (adapterClickable) {
            if(isEditing) {
                this.fotoPosition = position;
                isNew = false;
                bottomSheetDialog();
            }else{
                listaImagens.remove(position);
                adapterProdutoFotos.notifyDataSetChanged();
            }
        }
    }
}