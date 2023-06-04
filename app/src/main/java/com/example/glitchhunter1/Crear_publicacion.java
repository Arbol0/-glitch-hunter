package com.example.glitchhunter1;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;
import java.util.Map;

public class Crear_publicacion extends AppCompatActivity {

    //XML
    ImageView imageView;
    EditText title, desc, juego;
    ImageButton btn_edit_image, btn_delete_image;
    //Atributos
    private FirebaseFirestore firestore;
    StorageReference storageReference;
    //Var
    String id;
    String storage_path= "publication_img/*";
    public static final int COD_SEL_STORAGE = 200;
    public static final int COD_SEL_IMAGE = 300;
    private Uri image_url;
    private String image_uri;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crear_publicacion);
        //XML
        title = findViewById(R.id.titulo);
        desc = findViewById(R.id.descripcion);
        juego = findViewById(R.id.juego);
        imageView = findViewById(R.id.imageView);
        btn_edit_image = findViewById(R.id.editImage_btn);
        btn_delete_image = findViewById(R.id.deleteImage_btn);
        //variables
        id = getIntent().getStringExtra("id_publicacion");
        firestore = FirebaseFirestore.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference();
        progressDialog = new ProgressDialog(this);
        //metodos
        if(id != null && id != ""){
            get_datos(id);
        }
    }

    //Events
    public void editImg_btn(View view){

        uploadImg_intent();

    }
    public void deleteImg_btn(View view){
        deleteImg();
    }
    public void publicar_btn(View view){
        comprobar_datos(view);
    }


    //Methods
    public void uploadImg_intent(){
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        //Gallery Method
        startActivityForResult(intent, COD_SEL_IMAGE);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == COD_SEL_IMAGE){
            try{
                image_url = data.getData();
            }catch (Exception e) {
                Toast.makeText(this, "Imagen no subida", Toast.LENGTH_SHORT).show();
            }
            //Upload
            uploadImg(image_url);
        }
    }
    public void uploadImg(Uri image_url){
        //Var
        SharedPreferences app_progress = this.getSharedPreferences(getString(R.string.app_progress), MODE_PRIVATE);
        String email = app_progress.getString("email", "");
        String rute_storage_photo;
        StorageReference reference;
        rute_storage_photo = "" + storage_path+ " " + "image" + " " + email + " " + id;
        reference = storageReference.child(rute_storage_photo);
        //progress dialog
        progressDialog.setMessage("Actualizando foto");
        progressDialog.show();
        //upload en el Storage
        reference.putFile(image_url).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                showAlert("imagen en el storage");
                //Upload exitoso
                Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
                showAlert("task tiene la imagen");
                while (!uriTask.isSuccessful());
                    if (uriTask.isSuccessful()) {
                        uriTask.addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                showAlert("taskSuccesfull");
                                //Añadir foto a Publicacion Actual
                                if(id != null && id != ""){
                                    String dowland_uri = uri.toString();
                                    HashMap<String, Object> map = new HashMap<>();
                                    map.put("image", dowland_uri);
                                    firestore.collection("publicacion").document(id).update(map);
                                    showAlert("Foto actualizada");
                                    progressDialog.dismiss();
                                } else {
                                    image_uri = uri.toString();;
                                    progressDialog.dismiss();
                                    showAlert("Foto subida al storage");
                                }
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                showAlert("Error al subir la foto");
                            }
                        });
                    }
                //}
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                showAlert("Error al cargar foto");
            }
        });
    }
    public void deleteImg(){
        //Quitar foto de publicacion Actual
        if(id != null && id != ""){
            DocumentReference docRef = firestore.collection("publicacion").document(id);
            Map<String,Object> updates = new HashMap<>();
            updates.put("image", FieldValue.delete());
            docRef.update(updates).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void unused) {
                    showAlert("imagen borrada");
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    showAlert("fallo al borrar");
                }
            });
        } else {
            showAlert("no hay imagen");
        }
    }
    public void comprobar_datos(View view) {
        //var
        SharedPreferences app_progress = this.getSharedPreferences(getString(R.string.app_progress), MODE_PRIVATE);
        //Comprobar si ya hay datos code usuario
        String titulo = this.title.getText().toString().trim(); //devuelve el string sin espacios de title
        String descripcion = this.desc.getText().toString();
        String juego = this.juego.getText().toString();
        String email = "";
        //comnprobar que esten llenos
        if(titulo.isEmpty() || descripcion.isEmpty() || juego.isEmpty()){
            showAlert("faltan campos por completar");
        } else if (app_progress.getString("email", "").isEmpty() && app_progress.getString("providerType", "").isEmpty()) {
            showAlert("No estas logeado");
        } else {
            email = app_progress.getString("email", "");
            //Nueva Publicacion?
            if(id == null || id == ""){
                publicar_datos(titulo, descripcion, email, juego);
            } else {
                actualizar_datos(titulo, descripcion, email, juego);
            }
        }
    }
    public void publicar_datos(String titulo, String descripcion, String email, String juego){
        //var
        if(image_uri!=null && image_uri!="") {
            String dowland_uri = image_uri;
            //Tabla -> Registro
            Map<String, Object> map = new HashMap<>();
            map.put("title", titulo);
            map.put("descripcion", descripcion);
            map.put("juego", juego);
            map.put("nombreUsuario", email);
            map.put("votos", 0);
            map.put("image", dowland_uri);
            //Insertar registro
            firestore.collection("publicacion").add(map).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                //Add exitosamente
                @Override
                public void onSuccess(DocumentReference documentReference) {
                    showAlert("publicacion creada exitosamente");
                    //Terminar el Activity
                    //finish();
                }
            }).addOnFailureListener(new OnFailureListener() {
                //Fallos Event
                @Override
                public void onFailure(@NonNull Exception e) {
                    showAlert("Fallo al ingresar");
                }
            });
        } else {
            //Tabla -> Registro
            Map<String, Object> map = new HashMap<>();
            map.put("title", titulo);
            map.put("descripcion", descripcion);
            map.put("juego", juego);
            map.put("nombreUsuario", email);
            map.put("votos", 0);
            //Insertar registro
            firestore.collection("publicacion").add(map).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                //Add exitosamente
                @Override
                public void onSuccess(DocumentReference documentReference) {
                    showAlert("publicacion creada exitosamente");
                    //Terminar el Activity
                    //finish();
                }
            }).addOnFailureListener(new OnFailureListener() {
                //Fallos Event
                @Override
                public void onFailure(@NonNull Exception e) {
                    showAlert("Fallo al ingresar");
                }
            });
        }

    }
    public void actualizar_datos(String titulo, String descripcion, String email, String juego){
        //Tabla -> Registro
        Map<String, Object> map = new HashMap<>();
        if(image_uri!=null&&image_uri!=""){
            map.put("image", image_uri);
        }
        map.put("title", titulo);
        map.put("descripcion", descripcion);
        map.put("juego", juego);
        map.put("nombreUsuario", email);
        showAlert("nombreusuario "+email);
        map.put("votos", 0);
        //Actualizar registro
        firestore.collection("publicacion").document(id).update(map).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                showAlert("publicacion actualizada exitosamente");
                //Terminar el Activity
                //finish();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                showAlert("Fallo al ingresar");
            }
        });
    }

    public void get_datos(String id){
        firestore.collection("publicacion").document(id).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                //Cojer datos de la BD
                String titleBD = documentSnapshot.getString("title");
                String descripcionBD = documentSnapshot.getString("descripcion");
                String juegoBD = documentSnapshot.getString("juego");
                //Set datosBD en el activity
                title.setText(titleBD);
                desc.setText(descripcionBD);
                juego.setText(juegoBD);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });
    }
    public void showAlert(String message){
        //crear alerta
        AlertDialog.Builder builderAlert = new AlertDialog.Builder(this);
        builderAlert.setTitle("Nota");
        builderAlert.setMessage(message);
        builderAlert.setPositiveButton("Ok", null);
        AlertDialog alerta = builderAlert.create();
        //Mostrar alerta
        alerta.show();
    }
}