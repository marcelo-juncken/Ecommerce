package com.example.ecommerce.fragment.loja;

import static android.app.Activity.RESULT_OK;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.ImageDecoder;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.ecommerce.R;
import com.example.ecommerce.adapter.AdapterCategoria;
import com.example.ecommerce.databinding.DialogCategoriaFormBinding;
import com.example.ecommerce.databinding.FragmentCategoriasLojaBinding;
import com.example.ecommerce.databinding.SnackbarDeleteBinding;
import com.example.ecommerce.helper.FirebaseHelper;
import com.example.ecommerce.helper.RecyclerRowMoveCallback;
import com.example.ecommerce.model.Categoria;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.normal.TedPermission;
import com.tsuryo.swipeablerv.SwipeLeftRightCallback;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class CategoriasLojaFragment extends Fragment implements AdapterCategoria.onClickListener {

    private FragmentCategoriasLojaBinding binding;

    private AlertDialog dialog;

    private String caminhoImagem;

    private DialogCategoriaFormBinding categoriaBinding;
    private SnackbarDeleteBinding snackbarBinding;

    private Categoria categoria;

    private List<Categoria> categoriaList = new ArrayList<>();
    private AdapterCategoria adapterCategoria;

    private Snackbar snackbar;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentCategoriasLojaBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        configCliques();
        configRV();
        recuperaCategorias();
    }

    private void recuperaCategorias() {
        DatabaseReference categoriasRef = FirebaseHelper.getDatabaseReference()
                .child("categorias");
        categoriasRef.orderByChild("posicao").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                categoriaList.clear();
                if (snapshot.exists()) {
                    for (DataSnapshot ds : snapshot.getChildren()) {
                        categoriaList.add(ds.getValue(Categoria.class));
                    }
                    binding.txtInfo.setText("");
                }else{
                    binding.txtInfo.setText("Nenhuma categoria cadastrada.");
                }
                binding.progressBar.setVisibility(View.GONE);

                adapterCategoria.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                binding.progressBar.setVisibility(View.GONE);
                binding.txtInfo.setText("Erro ao carregar página");
            }
        });
    }

    private void configRV() {
        binding.rvCategorias.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.rvCategorias.setHasFixedSize(true);
        adapterCategoria = new AdapterCategoria(categoriaList, this);


        ItemTouchHelper.Callback callback = new RecyclerRowMoveCallback(adapterCategoria);
        ItemTouchHelper touchHelper = new ItemTouchHelper(callback);
        touchHelper.attachToRecyclerView(binding.rvCategorias);


        binding.rvCategorias.setAdapter(adapterCategoria);

        binding.rvCategorias.setListener(new SwipeLeftRightCallback.Listener() {
            @Override
            public void onSwipedLeft(int position) {

            }

            @Override
            public void onSwipedRight(int position) {
                snackbarDeletaItem(categoriaList.get(position));
            }
        });



    }
    @Override
    public void onClick(Categoria categoria) {
        Toast.makeText(getContext(), categoria.getNome(), Toast.LENGTH_SHORT).show();
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
                Toast.makeText(requireActivity(), "Permissão negada", Toast.LENGTH_SHORT).show();
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

    private void configCliques() {
        binding.imbAdd.setOnClickListener(v -> showDialog());
    }

    public void snackbarDeletaItem(Categoria categoria) {
        // Create the Snackbar
        LinearLayout.LayoutParams objLayoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        snackbar = Snackbar.make(binding.imbAdd, "", Snackbar.LENGTH_INDEFINITE);
        // Get the Snackbar's layout view

        Snackbar.SnackbarLayout layout = (Snackbar.SnackbarLayout) snackbar.getView();

        // Set snackbar layout params

        layout.setPadding(0, 0, 0, 0);

        // Inflate our custom view
        snackbarBinding = SnackbarDeleteBinding.inflate(LayoutInflater.from(getContext()));

        snackbarBinding.txtCategoria.setText(categoria.getNome());
        // Configure our custom view
        snackbarBinding.btnNao.setOnClickListener(v -> {
            adapterCategoria.notifyDataSetChanged();
            snackbar.dismiss();
        });

        snackbarBinding.btnSim.setOnClickListener(v -> {
            categoria.deletar();
            categoriaList.remove(categoria);
            adapterCategoria.notifyDataSetChanged();
            if (categoriaList.isEmpty()) binding.txtInfo.setText("Nenhuma categoria cadastrada.");
            snackbar.dismiss();
        });

        // Add the view to the Snackbar's layout
        layout.addView(snackbarBinding.getRoot(), objLayoutParams);
        // Show the Snackbar
        snackbar.show();
    }

    private void showDialog() {

        caminhoImagem = null;

        AlertDialog.Builder builder = new AlertDialog.Builder(
                getContext(), R.style.AlertDialog);

        categoriaBinding = DialogCategoriaFormBinding
                .inflate(LayoutInflater.from(getContext()));

        categoriaBinding.btnFechar.setOnClickListener(v -> {
            dialog.dismiss();
        });

        categoriaBinding.btnSalvar.setOnClickListener(v -> {
            validaDados();
        });

        categoriaBinding.imgCategoria.setOnClickListener(v -> verificaPermissaoGaleria());

        builder.setView(categoriaBinding.getRoot());

        dialog = builder.create();
        dialog.show();
    }

    private void validaDados() {
        String categoriaNome = categoriaBinding.edtCategoria.getText().toString().trim();

        if (!categoriaNome.isEmpty()) {
            ocultarTeclado();
            if (caminhoImagem != null) {
                categoriaBinding.progressBar.setVisibility(View.VISIBLE);
                categoriaBinding.btnSalvar.setEnabled(false);
                categoriaBinding.btnFechar.setEnabled(false);

                if (categoria == null) categoria = new Categoria();
                categoria.setNome(categoriaNome);
                categoria.setTodas(categoriaBinding.cbTodos.isChecked());

                salvaImagemFirebase();
            } else {
                Toast.makeText(requireContext(), "Escolha uma imagem para a categoria.", Toast.LENGTH_SHORT).show();
            }
        } else {
            categoriaBinding.edtCategoria.requestFocus();
            categoriaBinding.edtCategoria.setError("Esse campo não pode estar vazio.");
        }
    }

    private void salvaImagemFirebase() {
        StorageReference storageReference = FirebaseHelper.getStorageReference()
                .child("imagens")
                .child("categorias")
                .child(categoria.getId() + ".jpeg");

        UploadTask uploadTask = storageReference.putFile(Uri.parse(caminhoImagem));
        uploadTask.addOnSuccessListener(taskSnapshot -> storageReference.getDownloadUrl().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                String urlImagem = task.getResult().toString();
                categoria.setUrlImagem(urlImagem);
                categoria.setPosicao(System.currentTimeMillis());
                categoria.salvar();
                categoriaList.add(0, categoria);
                binding.txtInfo.setText("");
                adapterCategoria.notifyDataSetChanged();

                categoria = null;

                dialog.dismiss();

            }
            categoriaBinding.progressBar.setVisibility(View.GONE);
            categoriaBinding.btnSalvar.setEnabled(true);
            categoriaBinding.btnFechar.setEnabled(true);

        })).addOnFailureListener(e -> {
            Toast.makeText(getContext(), "Erro no upload da imagem, tente mais tarde.", Toast.LENGTH_SHORT).show();

            categoriaBinding.progressBar.setVisibility(View.GONE);
            categoriaBinding.btnSalvar.setEnabled(true);
            categoriaBinding.btnFechar.setEnabled(true);
        });
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
                            if (Build.VERSION.SDK_INT < 31) {
                                imagem = MediaStore.Images.Media.getBitmap(requireContext().getContentResolver(), localImagemSelecionada);
                            } else {
                                ImageDecoder.Source source = ImageDecoder.createSource(requireContext().getContentResolver(), localImagemSelecionada);
                                imagem = ImageDecoder.decodeBitmap(source);
                            }
                            categoriaBinding.imgCategoria.setImageBitmap(imagem);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                    }
                }
            }
    );

    private void ocultarTeclado() {
        ((InputMethodManager) requireContext().getSystemService(Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(
                categoriaBinding.edtCategoria.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS
        );
    }


}