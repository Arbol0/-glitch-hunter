package com.example.glitchhunter1;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.example.glitchhunter1.Adapter.PublicacionAdapter;
import com.example.glitchhunter1.Models.Publicacion;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

public class Perfil extends AppCompatActivity {
    //XML
    TextView userName_view;
    //Variables
    String emailUser="";
    String providerType="";
    RecyclerView mRecycler;
    FirebaseFirestore mFireStore;
    PublicacionAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_perfil_);
        //XML
        userName_view = findViewById(R.id.userName_view);
        //Execution
        getUserData();
        updateUI();
        setmRecycler();
    }
    @Override
    protected void onStart() {
        super.onStart();
        mAdapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mAdapter.stopListening();
    }

    public void setmRecycler() {
        //RECICLER VIEW DATA
        mFireStore = FirebaseFirestore.getInstance();
        mRecycler = findViewById(R.id.recycler_perfil);
        //-LayoutManager
        mRecycler.setLayoutManager(new LinearLayoutManager(this));
        //-Query BBDD: collection de Publicaciones
        Query query = mFireStore.collection("publicacion");
        //Query Options
        FirestoreRecyclerOptions<Publicacion> firestoreRecyclerOptions =
                new FirestoreRecyclerOptions.Builder<Publicacion>()
                        .setQuery(query.orderBy("nombreUsuario")
                                .startAt(emailUser), Publicacion.class).build();
        //-Nuevo Adapter, con el Resultado BBDD
        mAdapter= new PublicacionAdapter(firestoreRecyclerOptions,  this);
        mAdapter.notifyDataSetChanged();
        mRecycler.setAdapter(mAdapter);
    }
    //metodos
    public void getUserData() {
        SharedPreferences app_progress = this.getSharedPreferences(getString(R.string.app_progress), MODE_PRIVATE);
        //datos
        emailUser = app_progress.getString("email", "");
        providerType = app_progress.getString("providerType", "");
    }
    public void go_home(View view){
        //Al crear intent - (actualContext, next Activity)
        Intent intent = new Intent(this, Home.class);
        //Iniciar Activity
        startActivity(intent);
    }

    public  void  cerrar_session(View view){
        //cerrar session
        SharedPreferences app_progress = this.getSharedPreferences(getString(R.string.app_progress), MODE_PRIVATE);
        //Set
        SharedPreferences.Editor pref_editor = app_progress.edit();
        pref_editor.clear();
        pref_editor.commit();
    }
    public void updateUI(){
        SharedPreferences app_progress = this.getSharedPreferences(getString(R.string.app_progress), MODE_PRIVATE);
        userName_view.setText(app_progress.getString("email", ""));
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