package com.example.glitchhunter1.Adapter;

import android.app.Activity;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.glitchhunter1.Crear_publicacion;
import com.example.glitchhunter1.Juego_;
import com.example.glitchhunter1.Models.Juego;
import com.example.glitchhunter1.R;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class JuegoAdapter extends FirestoreRecyclerAdapter<Juego, JuegoAdapter.ViewHolder>{

    Activity activity;
    static String nombree;
    public static String juegoBusqueda="";
    /**
     * Create a new RecyclerView adapter that listens to a Firestore Query.  See {@link
     * FirestoreRecyclerOptions} for configuration options.
     *
     * @param options
     */
    public JuegoAdapter(@NonNull FirestoreRecyclerOptions<Juego> options, Activity activity) {
        super(options);
        this.activity = activity;
    }

    @Override
    protected void onBindViewHolder(@NonNull JuegoAdapter.ViewHolder viewHolder, int position, @NonNull Juego juego) {
        //Posicion e Id del viewHolder(publicacion) actual.
        DocumentSnapshot documentSnapshot = getSnapshots().getSnapshot(viewHolder.getAdapterPosition());
        final String id = documentSnapshot.getId();
        //inizialize
        viewHolder.juego.setText(juego.getNombre());
        juegoBusqueda = juegoBusqueda.toString();

        viewHolder.juego.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                go_Juego(id);
            }
        });
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.juego_item, parent, false);
        ViewHolder holder= new ViewHolder(itemView);
        return holder;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        Button juego;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            juego = itemView.findViewById(R.id.juego_btn);
        }
    }

    //Methods
    public void go_Juego(String id){
        //var
        Intent intent = new Intent(activity, Juego_.class);
        FirebaseFirestore.getInstance().collection("juego").document(id).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                nombree = documentSnapshot.getString("nombre");
                Toast.makeText(activity, "nombre1"+nombree, Toast.LENGTH_SHORT).show();
                intent.putExtra("juego_nombre", nombree);
                intent.putExtra("juego_id", id);
                activity.startActivity(intent);
            }
        });
    }
}
