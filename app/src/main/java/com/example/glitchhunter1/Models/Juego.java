package com.example.glitchhunter1.Models;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.glitchhunter1.Adapter.JuegoAdapter;
import com.example.glitchhunter1.Adapter.PublicacionAdapter;
import com.example.glitchhunter1.R;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

public class Juego {
    //Var
    String nombre;

    public Juego(String nombre) {
        this.nombre = nombre;
    }
    public Juego() {
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }


}
