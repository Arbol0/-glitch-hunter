package com.example.glitchhunter1;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

import com.example.glitchhunter1.Adapter.PublicacionAdapter;
import com.example.glitchhunter1.Models.Juego;
import com.example.glitchhunter1.Models.Publicacion;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

public class Home extends AppCompatActivity {

    //XML
    ImageButton buscar_btn;
    //Variables
    String email="";
    String providerType="";
    String messageAuth="";
    RecyclerView mRecycler;
    FirebaseFirestore mFireStore;
    PublicacionAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        //XML
        buscar_btn = (ImageButton) findViewById(R.id.buscar_btn);
        //Execution
        getUserData();
        guardar_sesion(); // Siempre se actualiza con los Args -----<
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

    //Events
    public void search_game(View view){
        //Al crear intent - (actualContext, next Activity)
        Intent intent = new Intent(this, Buscador.class);
        //Iniciar Activity
        startActivity(intent);
    }
    public void go_perfil(View view){
        //Al crear intent - (actualContext, next Activity)
        Intent intent = new Intent(this, Perfil.class);
        //Iniciar Activity
        startActivity(intent);
    }
    public void go_crearPublicacion(View view){
        //Al crear intent - (actualContext, next Activity)
        Intent intent = new Intent(this, Crear_publicacion.class);
        //Iniciar Activity
        startActivity(intent);
    }

    //RecyclerView
    public void setmRecycler() {
        //RECICLER VIEW DATA
        mFireStore = FirebaseFirestore.getInstance();
        mRecycler = findViewById(R.id.reciclerViewPublicacion);
        //-LayoutManager
        mRecycler.setLayoutManager(new LinearLayoutManager(this));
        //-Query BBDD: collection de Publicaciones
        Query query = mFireStore.collection("publicacion");
        //Query Options
        FirestoreRecyclerOptions<Publicacion> firestoreRecyclerOptions =
                new FirestoreRecyclerOptions.Builder<Publicacion>()
                        .setQuery(query.orderBy("votos", Query.Direction.DESCENDING)
                                , Publicacion.class).build();
        //-Nuevo Adapter, con el Resultado BBDD
        mAdapter= new PublicacionAdapter(firestoreRecyclerOptions,  this);
        mAdapter.notifyDataSetChanged();
        mRecycler.setAdapter(mAdapter);
    }
    //Arguments - AuthActivity
    public void getUserData() {
        Intent intent= new Intent();
        Bundle datos = getIntent().getExtras();
        //datos
        email = datos.getString("email");
        providerType = datos.getString("providerType");
    }
    //Session
    public void guardar_sesion () {
        SharedPreferences app_progress = this.getSharedPreferences(getString(R.string.app_progress), MODE_PRIVATE);
        //Set datos - con un Editor
        SharedPreferences.Editor pref_editor = app_progress.edit();
        pref_editor.putString("email",email);
        pref_editor.putString("providerType",providerType);
        //Guardar
        pref_editor.commit();
    }
    public void showAlert(String message) {
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