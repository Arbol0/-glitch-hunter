package com.example.glitchhunter1.Models;

import java.sql.Struct;

public class Publicacion {
    String title;
    String descripcion;
    String nombreUsuario;
    String juego;
    Double votos;
    String image;


    //Constucor
    public Publicacion(String title, String descripcion,String nombreUsuario, String juego, Double votos, String image) {
        this.title = title;
        this.descripcion = descripcion;
        this.nombreUsuario = nombreUsuario;
        this.juego = juego;
        this.votos = votos;
        this.image = image;
    }
    //Constructor vacio
    public Publicacion() {

    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getNombreUsuario() {
        return nombreUsuario;
    }

    public void setNombreUsusario(String nombreUsusario) {
        this.nombreUsuario = nombreUsusario;
    }

    public String getJuego() {
        return juego;
    }

    public void setJuego(String juego) {
        this.juego = juego;
    }

    public Double getVotos(){return votos;}

    public void setVotos(Double votos) {
        this.votos = votos;
    }
    public String getImage(){
        return image;
    }
    public void setImage(String image){
        this.image = image;
    }

}
