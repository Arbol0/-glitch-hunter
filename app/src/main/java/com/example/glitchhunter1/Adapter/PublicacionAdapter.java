package com.example.glitchhunter1.Adapter;

import static android.content.Context.MODE_PRIVATE;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.glitchhunter1.Crear_publicacion;
import com.example.glitchhunter1.Models.Publicacion;
import com.example.glitchhunter1.R;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.Map;

public class PublicacionAdapter extends FirestoreRecyclerAdapter<Publicacion, PublicacionAdapter.ViewHolder> {
    //Atributos
    private FirebaseFirestore mFirestore = FirebaseFirestore.getInstance();
    Activity activity;
    String image;
    String emailActual;
    String emailPublicacion;
    /**
     * Create a new RecyclerView adapter that listens to a Firestore Query.  See {@link
     * FirestoreRecyclerOptions} for configuration options.
     *
     * @param options
     */
    public PublicacionAdapter(@NonNull FirestoreRecyclerOptions<Publicacion> options, Activity activity) {
        super(options);
        this.activity = activity;

    }

    @Override
    protected void onBindViewHolder(@NonNull PublicacionAdapter.ViewHolder viewHolder, int position, @NonNull Publicacion publicacion) {
        //Posicion e Id del viewHolder(publicacion) actual.
        DocumentSnapshot documentSnapshot = getSnapshots().getSnapshot(viewHolder.getAdapterPosition());
        final String id = documentSnapshot.getId();
        //Inicialize, elementos de publicacion_item(ViewHolder)
        viewHolder.user.setText(publicacion.getNombreUsuario());
        viewHolder.title.setText(publicacion.getTitle());
        viewHolder.game.setText(publicacion.getJuego());
        viewHolder.description.setText(publicacion.getDescripcion());
        viewHolder.votosNum.setText(publicacion.getVotos().toString());

        image = publicacion.getImage();
        try{
            if (!image.equals("") && image!=null){
                //Mostrar con picasso
                Picasso.with(activity.getApplicationContext())
                        .load(image)
                        .resize(600, 600)
                        .into(viewHolder.image);
            }
        }catch(Exception e){
            Log.d("Exception", "e: "+e);
        }
        //Events
        viewHolder.delete_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deletePublicacion(id);
            }
        });
        viewHolder.edit_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editarPublicacion(id);
            }
        });
        viewHolder.votar_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                votarPublicacion(id);
            }
        });
    }


    @NonNull
    @Override
    //Crear Holder - Añadiendo(inflate) publicacion_item
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView;
        itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.publicacion_item, parent, false);

        ViewHolder holder= new ViewHolder(itemView);
        return holder;
    }
    //Holder - Referencia a publicacion_item
    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView title, description, game, user;
        ImageView image;
        ImageButton votar_btn;
        TextView votosNum;
        ImageButton delete_btn;
        ImageButton edit_btn;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
                game = itemView.findViewById(R.id.game_view);
                user = itemView.findViewById(R.id.userName_view);
                title = itemView.findViewById(R.id.nombreJuego_view);
                description = itemView.findViewById(R.id.description_view);
                votar_btn = itemView.findViewById(R.id.vote_button);
                votosNum = itemView.findViewById(R.id.votosNum);
                image = itemView.findViewById(R.id.image);
                edit_btn = itemView.findViewById(R.id.edit_btn);
                delete_btn = itemView.findViewById(R.id.delete_btn);
        }

    }

    //METODOS
    private void deletePublicacion(String id){
        //email
        setEmailActual();
        mFirestore.collection("publicacion").document(id).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                //Cojer datos de la BD
                emailPublicacion = documentSnapshot.getString("nombreUsuario");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });
        //delete
        if(emailActual.equals(emailPublicacion)){
            mFirestore.collection("publicacion").document(id).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void unused) {
                    Toast.makeText(activity, "Eliminado correctamente", Toast.LENGTH_SHORT).show();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(activity, "Fallo al eliminar", Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            Toast.makeText(activity, "No tienes permiso", Toast.LENGTH_SHORT).show();
        }
    }
    private void editarPublicacion(String id){
        //email
        setEmailActual();
        mFirestore.collection("publicacion").document(id).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                //Cojer datos de la BD
                emailPublicacion = documentSnapshot.getString("nombreUsuario");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });
        if(emailActual.equals(emailPublicacion)) {
            //Edit activity
            Intent intent = new Intent(activity, Crear_publicacion.class);
            intent.putExtra("id_publicacion", id);
            activity.startActivity(intent);
        } else {
            Toast.makeText(activity, "No tienes permiso", Toast.LENGTH_SHORT).show();
        }
    }
    private void votarPublicacion(String id){
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        firestore.collection("publicacion").document(id).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                //Cojer datos de la BD
                String titleBD = documentSnapshot.getString("title");
                String descripcionBD = documentSnapshot.getString("descripcion");
                String juegoBD = documentSnapshot.getString("juego");
                String emailBD = documentSnapshot.getString("emailBD");
                Double votos = documentSnapshot.getDouble("votos");
                //Tabla -> Registro
                Map<String, Object> map = new HashMap<>();
                map.put("title", titleBD);
                map.put("descripcion", descripcionBD);
                map.put("juego", juegoBD);
                map.put("nombreUsuario", emailBD);
                map.put("votos", (votos+1));
                //Actualizar registro
                firestore.collection("publicacion").document(id).update(map).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Toast.makeText(activity, "like actualizado: "+votos+1, Toast.LENGTH_SHORT).show();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(activity, "Fallo al actualizar like", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(activity, "Fallo al cojer BBDD data", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setEmailActual(){
        //var
        SharedPreferences app_progress = activity.getSharedPreferences("com.example.glitchhunter1.PREFERENCE_FILE_KEY", MODE_PRIVATE);
        emailActual = app_progress.getString("email", "");
    }
}
