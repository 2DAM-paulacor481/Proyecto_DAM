package iesmm.pmdm.eventconnect.Model;

import java.util.Date;

public class Evento {

    private String titulo, descripcion, nombreCreador, categoria, fecha;
    private Double latitud;
    private Double longitud;

    // Constructor con todos los campos
    public Evento(String titulo, String descripcion, String fecha, Double latitud, Double longitud, String nombreCreador, String categoria) {
        this.titulo = titulo;
        this.descripcion = descripcion;
        this.fecha = fecha;
        this.latitud = latitud;
        this.longitud = longitud;
        this.nombreCreador = nombreCreador;
        this.categoria = categoria;
    }

    // Constructor sin argumentos
    public Evento() {

    }

    // Getters y Setters
    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    public Double getLatitud() {
        return latitud;
    }

    public void setLatitud(Double latitud) {
        this.latitud = latitud;
    }

    public Double getLongitud() {
        return longitud;
    }

    public void setLongitud(Double longitud) {
        this.longitud = longitud;
    }

    public String getNombreCreador() {
        return nombreCreador;
    }

    public void setNombreCreador(String nombreCreador) {
        this.nombreCreador = nombreCreador;
    }

    public String getCategoria() {
        return categoria;
    }

    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }



}
