package com.example.glitchhunter1;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.widget.EditText;
import android.widget.SearchView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;


import com.example.glitchhunter1.Adapter.JuegoAdapter;
import com.example.glitchhunter1.Adapter.PublicacionAdapter;
import com.example.glitchhunter1.Models.Juego;
import com.example.glitchhunter1.Models.Publicacion;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

public class Buscador extends AppCompatActivity {

    //Variables
    RecyclerView mRecycler;
    FirebaseFirestore mFireStore;
    JuegoAdapter mAdapter;
    Query query;
    //XML
    EditText buscador;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_buscador);
        //Var
        buscador = findViewById(R.id.buscador_btn2);
        //Execute
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
    public void buscador(View view) {
        String text = buscador.getText().toString();
        buscar(text);
    }

    //Metodo
    public void setmRecycler() {
        //mFireStrore - Query a BBDD
        mFireStore = FirebaseFirestore.getInstance();
        Query query = mFireStore.collection("juego");
        FirestoreRecyclerOptions<Juego> firestoreRecyclerOptions = new FirestoreRecyclerOptions.Builder<Juego>().setQuery(query, Juego.class).build();
        //Adapter -> FirestoreRecyclerOptions<Juego>
        mAdapter= new JuegoAdapter(firestoreRecyclerOptions, this);
        mAdapter.notifyDataSetChanged();
        //Recycler -> Adapter
        mRecycler = findViewById(R.id.reciclerViewJuego);
        mRecycler.setLayoutManager(new LinearLayoutManager(this));
        mRecycler.setAdapter(mAdapter);
    }
    public void buscar(String text){
        showAlert(text);
        Query query = mFireStore.collection("juego");
        //Busqueda Options
        FirestoreRecyclerOptions<Juego> firestoreRecyclerOptions =
                new FirestoreRecyclerOptions.Builder<Juego>()
                        .setQuery(query.orderBy("nombre")
                        .startAt(text).endAt(text+"~"), Juego.class).build();
        //adapter y recycler
        mAdapter = new JuegoAdapter(firestoreRecyclerOptions, this);
        mAdapter.startListening();
        mRecycler.setAdapter(mAdapter);
    }
    public void go_home(View view){
        //Al crear intent - (actualContext, next Activity)
        Intent intent = new Intent(this, Home.class);
        //Iniciar Home
        startActivity(intent);
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