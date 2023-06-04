package com.example.glitchhunter1;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.example.glitchhunter1.Adapter.JuegoAdapter;
import com.example.glitchhunter1.Adapter.PublicacionAdapter;
import com.example.glitchhunter1.Models.Juego;
import com.example.glitchhunter1.Models.Publicacion;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

public class Juego_ extends AppCompatActivity {

    //Var
    String nombre="";
    String email="";
    RecyclerView mRecycler;
    FirebaseFirestore mFireStore;
    PublicacionAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_juego);
        getData();
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

    //metodos
    public void search_game(View view){
        //Al crear intent - (actualContext, next Activity)
        Intent intent = new Intent(this, Buscador.class);
        //Iniciar Activity
        startActivity(intent);
    }
    public void go_perfil(View view){
        //Al crear intent - (actualContext, next Activity)
        Intent intent = new Intent(this, Perfil.class);
        //Iniciar Home
        startActivity(intent);
    }
    public void go_home2(View view){
        //Al crear intent - (actualContext, next Activity)
        Intent intent = new Intent(this, Home.class);
        //Iniciar Home
        startActivity(intent);
    }


    //Metodo
    public void setmRecycler() {
        //mFireStrore - Query a BBDD
        mFireStore = FirebaseFirestore.getInstance();
        Query query = mFireStore.collection("publicacion");
        FirestoreRecyclerOptions<Publicacion> firestoreRecyclerOptions =
                new FirestoreRecyclerOptions.Builder<Publicacion>()
                        .setQuery(query.orderBy("juego").
                        startAt(nombre).endAt(nombre+"~"), Publicacion.class).build();
        //Adapter -> FirestoreRecyclerOptions<Juego>
        mAdapter = new PublicacionAdapter(firestoreRecyclerOptions, this);
        mAdapter.notifyDataSetChanged();
        mAdapter.startListening();
        //Recycler -> Adapter
        mRecycler = findViewById(R.id.reciclerViewPublicacionJuego);
        mRecycler.setLayoutManager(new LinearLayoutManager(this));
        mRecycler.setAdapter(mAdapter);
    }

    public void getData() {
        Intent intent= new Intent();
        Bundle datos = getIntent().getExtras();
        //datos
        String id = datos.getString("juego_id");
        nombre = datos.getString("juego_nombre");
        showAlert("el nombre es "+nombre+"y el id"+id);
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

    //mFirestore.collection("publicacion").document(id)
}