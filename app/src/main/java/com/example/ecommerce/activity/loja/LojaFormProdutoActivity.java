package com.example.ecommerce.activity.loja;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.ecommerce.R;
import com.example.ecommerce.adapter.AdapterCategoriaDialog;
import com.example.ecommerce.adapter.AdapterProdutoFotos;
import com.example.ecommerce.databinding.ActivityLojaFormProdutoBinding;
import com.example.ecommerce.databinding.BottomSheetDialogBinding;

import com.example.ecommerce.databinding.DialogFormProdutoCategoriaBinding;
import com.example.ecommerce.helper.FirebaseHelper;
import com.example.ecommerce.helper.GetMask;
import com.example.ecommerce.model.Categoria;
import com.example.ecommerce.model.ImagemUpload;
import com.example.ecommerce.model.Produto;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.normal.TedPermission;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class LojaFormProdutoActivity extends AppCompatActivity implements AdapterProdutoFotos.OnClickListener, AdapterCategoriaDialog.onClickListener {

    private ActivityLojaFormProdutoBinding binding;

    private String currentPhotoPath;
    private String caminho;

    private final List<ImagemUpload> imagemUploadList = new ArrayList<>();
    private final List<ImagemUpload> imagemAddList = new ArrayList<>();
    private final Map<String, ImagemUpload> imagemUploadMap = new HashMap<>();

    private final List<ImagemUpload> imagemDeleteList = new ArrayList<>();
    private AdapterProdutoFotos adapterProdutoFotos;

    private boolean isNewImg = true;

    private Produto produto;

    private boolean adapterClickable = true;

    private int editPosition;

    private DialogFormProdutoCategoriaBinding bindingDialogCategoria;

    private AdapterCategoriaDialog adapterCategoriaDialog;
    private final List<Categoria> categoriaList = new ArrayList<>();
    private List<String> categoriasSelecionadasList = new ArrayList<>();
    private List<String> idsCategoriasSelecionadasList = new ArrayList<>();
    private List<String> categoriasSelecionadasTempList = new ArrayList<>();
    private List<String> idsCategoriasSelecionadasTempList = new ArrayList<>();


    private boolean novoProduto = true;
    private AlertDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLojaFormProdutoBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.edtValorAtual.setLocale(new Locale("PT", "br"));
        binding.edtValorAntigo.setLocale(new Locale("PT", "br"));
        recuperaCategorias();

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            binding.include.txtTitulo.setText("Editar produto");
            novoProduto = false;
            String produtoId = (String) bundle.getSerializable("produtoSelecionado");
            recuperaProduto(produtoId);

        } else {
            binding.include.txtTitulo.setText("Novo produto");
            binding.progressBar.setVisibility(View.GONE);
        }


        configCliques();
        configRVFotos();
    }

    private void recuperaProduto(String produtoId) {
        DatabaseReference produtosRef = FirebaseHelper.getDatabaseReference()
                .child("produtos")
                .child(produtoId);
        produtosRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    produto = snapshot.getValue(Produto.class);
                    configProduto();
                } else {
                    finish();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void recuperaCategorias() {
        DatabaseReference produtosRef = FirebaseHelper.getDatabaseReference()
                .child("categorias");
        produtosRef.orderByChild("posicao").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    for (DataSnapshot ds : snapshot.getChildren()) {
                        categoriaList.add(ds.getValue(Categoria.class));
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void configProduto() {

        imagemUploadMap.putAll(produto.getImagemUploadMap());
        imagemUploadList.addAll(produto.getImagemUploadMap().values());
        Collections.sort(imagemUploadList, (o1, o2) -> Math.toIntExact(o1.getIndex() - o2.getIndex()));

        binding.edtTitulo.setText(produto.getTitulo());
        binding.edtDescricao.setText(produto.getDescricao());
        binding.edtValorAntigo.setText(GetMask.getValor(produto.getValorAntigo()));
        binding.edtValorAtual.setText(GetMask.getValor(produto.getValorAtual()));

        idsCategoriasSelecionadasList.addAll(produto.getIdCategorias());
        for (Categoria categoria : categoriaList) {
            if (produto.getIdCategorias().contains(categoria.getId())) {
                categoriasSelecionadasList.add(categoria.getNome());
            }
        }

        binding.btnCategoria.setText(TextUtils.join(", ", categoriasSelecionadasList));
        adapterProdutoFotos.notifyDataSetChanged();

        binding.progressBar.setVisibility(View.GONE);

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

    private void showDialogCategorias() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.AlertDialog);
        bindingDialogCategoria = DialogFormProdutoCategoriaBinding
                .inflate(LayoutInflater.from(this));

        configRVCategorias();
        bindingDialogCategoria.btnFechar.setOnClickListener(v -> {

            adapterCategoriaDialog.notifyDataSetChanged();
            dialog.dismiss();
        });
        bindingDialogCategoria.btnSalvar.setOnClickListener(v -> {

            idsCategoriasSelecionadasList = new ArrayList<>(idsCategoriasSelecionadasTempList);
            categoriasSelecionadasList = new ArrayList<>(categoriasSelecionadasTempList);
            binding.btnCategoria.setText(TextUtils.join(", ", categoriasSelecionadasList));

            if (!idsCategoriasSelecionadasList.isEmpty()) {
                binding.btnCategoria.setError(null);
            } else {
                binding.btnCategoria.setError("");
            }
            adapterCategoriaDialog.notifyDataSetChanged();
            dialog.dismiss();
        });

        if (categoriaList.isEmpty()) {
            bindingDialogCategoria.txtInfo.setText("Nenhuma categoria cadastrada.");
        } else {
            bindingDialogCategoria.txtInfo.setText("");
        }
        bindingDialogCategoria.progressBar.setVisibility(View.GONE);

        builder.setView(bindingDialogCategoria.getRoot());

        dialog = builder.create();
        dialog.show();
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
            isNewImg = true;
            bottomSheetDialog();
        });
        binding.btnSalvar.setOnClickListener(v -> validaDados());

        binding.btnCategoria.setOnClickListener(v -> {
            idsCategoriasSelecionadasTempList = new ArrayList<>(idsCategoriasSelecionadasList);
            categoriasSelecionadasTempList = new ArrayList<>(categoriasSelecionadasList);
            showDialogCategorias();
        });
    }

    private void validaDados() {
        String titulo = binding.edtTitulo.getText().toString().trim();
        String descricao = binding.edtDescricao.getText().toString().trim();

        double valorAntigo = (double) binding.edtValorAntigo.getRawValue() / 100;
        double valorAtual = (double) binding.edtValorAtual.getRawValue() / 100;

        if (!titulo.isEmpty()) {
            if (!categoriasSelecionadasList.isEmpty()) {
                binding.btnCategoria.setError(null);
                if (!descricao.isEmpty()) {
                    if (valorAtual > 0) {
                        if (valorAtual < valorAntigo || valorAntigo == 0) {
                            ocultarTeclado();

                            if (imagemUploadMap.isEmpty()) {
                                showDialog();
                            } else {
                                if (produto == null) produto = new Produto();
                                statusButton();
                                produto.setTitulo(titulo);
                                produto.setDescricao(descricao);
                                produto.setValorAtual(valorAtual);
                                produto.setValorAntigo(valorAntigo);
                                produto.setIdCategorias(idsCategoriasSelecionadasList);
                                produto.salvar(novoProduto);

                                deletarImagens(this::salvarFotos);
                            }
                        } else {
                            binding.edtValorAntigo.requestFocus();
                            binding.edtValorAtual.setError("O valor atual tem que ser menor que o valor antigo.");
                            binding.edtValorAtual.setError("O valor atual tem que ser menor que o valor antigo.");
                        }
                    } else {
                        binding.edtValorAtual.requestFocus();
                        binding.edtValorAtual.setError("Esse campo não pode estar em branco");
                    }
                } else {
                    binding.edtDescricao.requestFocus();
                    binding.edtDescricao.setError("Esse campo não pode estar em branco");
                }
            } else {
                binding.btnCategoria.setError("Esse campo não pode estar em branco");
            }
        } else {
            binding.edtTitulo.requestFocus();
            binding.edtTitulo.setError("Esse campo não pode estar em branco");
        }
    }

    public interface MyCallback {
        void onCallback();
    }

    public void deletarImagens(MyCallback myCallback) {
        if (!imagemDeleteList.isEmpty()) {
            for (int i = 0; i < imagemDeleteList.size(); i++) {
                StorageReference storageReference = FirebaseHelper.getStorageReference()
                        .child("imagens")
                        .child("produtos")
                        .child(produto.getId())
                        .child(imagemDeleteList.get(i).getIndex() + ".jpeg");
                int finalI = i;
                storageReference.delete().addOnCompleteListener(task -> {

                    DatabaseReference produtoRef = FirebaseHelper.getDatabaseReference()
                            .child("produtos")
                            .child(produto.getId())
                            .child("imagemUploadMap")
                            .child(String.valueOf(imagemDeleteList.get(finalI).getIndex()));
                    produtoRef.removeValue().addOnCompleteListener(task1 -> {

                        if (finalI + 1 == imagemDeleteList.size()) {
                            myCallback.onCallback();
                        }
                    });

                });

            }
        } else {
            salvarFotos();
        }
    }

    private void salvarFotos() {
        if (!imagemAddList.isEmpty()) {
            for (int i = 0; i < imagemAddList.size(); i++) {
                ImagemUpload imagemUpload = imagemAddList.get(i);

                    StorageReference storageReference = FirebaseHelper.getStorageReference()
                            .child("imagens")
                            .child("produtos")
                            .child(produto.getId())
                            .child(imagemAddList.get(i).getIndex() + ".jpeg");

                    UploadTask uploadTask = storageReference.putFile(Uri.parse(imagemUpload.getCaminhoImagem()));
                    int finalI = i;
                    uploadTask.addOnSuccessListener(taskSnapshot -> storageReference.getDownloadUrl().addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {

                            imagemUpload.setCaminhoImagem(task.getResult().toString());
                            imagemUploadMap.put(String.valueOf(imagemUpload.getIndex()), imagemUpload);

                            produto.salvarImagem(imagemUpload);

                            if (finalI + 1 == imagemAddList.size()) {
                                finish();
                            }
                        }
                    })).addOnFailureListener(e -> Toast.makeText(this, "Falha no upload, tente mais tarde", Toast.LENGTH_SHORT).show());
            }
        } else {
            finish();
        }
    }

    private final ActivityResultLauncher<Intent> resultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK) {
                    if (result.getData() != null) {

                        String caminhoImagem = "";
                        if (caminho.equals("Galeria")) {
                            Uri localImagemSelecionada = result.getData().getData();
                            caminhoImagem = localImagemSelecionada.toString();

                        } else if (caminho.equals("Camera")) {
                            File file = new File(currentPhotoPath);
                            caminhoImagem = String.valueOf(file.toURI());
                        }

                        ImagemUpload imagemUpload;
                        if (isNewImg) {
                            imagemUpload = new ImagemUpload(caminhoImagem);
                            imagemUploadMap.put(String.valueOf(imagemUpload.getIndex()), imagemUpload);
                            imagemUploadList.add(imagemUpload);
                        } else {
                            imagemUpload = imagemUploadList.get(editPosition);

                            imagemUpload.setCaminhoImagem(caminhoImagem);
                            imagemUploadMap.put(String.valueOf(imagemUpload.getIndex()), imagemUpload);
                            imagemUploadList.get(editPosition).setCaminhoImagem(caminhoImagem);
                        }
                        imagemAddList.add(imagemUpload);

                    }
                    adapterProdutoFotos.notifyDataSetChanged();
                }
            }
    );

    private void showDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(
                this);

        builder.setCancelable(false);
        builder.setMessage("É necessário ter pelo menos uma imagem para o produto.");
        builder.setNegativeButton("Fechar", (dialog, which) -> {
            dialog.dismiss();
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void statusButton() {
        binding.progressBar.setVisibility(View.VISIBLE);
        binding.btnSalvar.setEnabled(false);
        binding.btnCategoria.setEnabled(false);
        binding.cardViewAdd.setEnabled(false);
        adapterClickable = false;
    }

    private void ocultarTeclado() {
        ((InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(
                binding.edtValorAtual.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS
        );
    }

    private void configRVFotos() {
        binding.rvFotosItens.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        binding.rvFotosItens.setHasFixedSize(true);
        adapterProdutoFotos = new AdapterProdutoFotos(this, imagemUploadList);
        binding.rvFotosItens.setAdapter(adapterProdutoFotos);
    }

    private void configRVCategorias() {
        bindingDialogCategoria.rvCategorias.setLayoutManager(new LinearLayoutManager(this));
        bindingDialogCategoria.rvCategorias.setHasFixedSize(true);
        adapterCategoriaDialog = new AdapterCategoriaDialog(categoriaList, this, idsCategoriasSelecionadasList);
        bindingDialogCategoria.rvCategorias.setAdapter(adapterCategoriaDialog);
    }

    @Override
    public void onClick(int position, boolean isEditing) {
        if (adapterClickable) {
            if (isEditing) {
                editPosition = position;
                isNewImg = false;
                bottomSheetDialog();
            } else {
                ImagemUpload imagemUpload = imagemUploadList.get(position);
                if(!imagemAddList.contains(imagemUpload)){
                    imagemDeleteList.add(imagemUpload);
                } else {
                    imagemAddList.remove(imagemUpload);
                }
                imagemUploadMap.remove(String.valueOf(imagemUpload.getIndex()));
                imagemUploadList.remove(imagemUpload);

                adapterProdutoFotos.notifyDataSetChanged();
            }
        }
    }


    @Override
    public void onClick(Categoria categoria) {
        if (idsCategoriasSelecionadasTempList.contains(categoria.getId())) {

            idsCategoriasSelecionadasTempList.remove(categoria.getId());
            categoriasSelecionadasTempList.remove(categoria.getNome());
        } else {
            idsCategoriasSelecionadasTempList.add(categoria.getId());
            categoriasSelecionadasTempList.add(categoria.getNome());
        }
    }

}