package iesmm.pmdm.eventconnect.Model;

import java.util.Map;
import java.util.HashMap;

public class Evento {

    private String id;
    private String titulo, descripcion, nombreCreador, categoria, fecha;
    private Double latitud;
    private Double longitud;
    private Map<String, Usuario> participantes;

    public Evento() {
    }

    public Evento(String id, String titulo, String descripcion, String nombreCreador, String categoria, String fecha, Double latitud, Double longitud, Map<String, Usuario> participantes) {
        this.id = id;
        this.titulo = titulo;
        this.descripcion = descripcion;
        this.nombreCreador = nombreCreador;
        this.categoria = categoria;
        this.fecha = fecha;
        this.latitud = latitud;
        this.longitud = longitud;
        this.participantes = participantes;
    }


    public Evento(String titulo, String descripcion, String fecha, Double latitud, Double longitud, String nombreCreador, String categoria) {
        this.titulo = titulo;
        this.descripcion = descripcion;
        this.fecha = fecha;
        this.latitud = latitud;
        this.longitud = longitud;
        this.nombreCreador = nombreCreador;
        this.categoria = categoria;
        this.participantes = new HashMap<>();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Map<String, Usuario> getParticipantes() {
        return participantes;
    }

    public void setParticipantes(Map<String, Usuario> participantes) {
        this.participantes = participantes;
    }

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

    @Override
    public String toString() {
        return "Evento{" +
                "id='" + id + '\'' +
                ", titulo='" + titulo + '\'' +
                ", descripcion='" + descripcion + '\'' +
                ", nombreCreador='" + nombreCreador + '\'' +
                ", categoria='" + categoria + '\'' +
                ", fecha='" + fecha + '\'' +
                ", latitud=" + latitud +
                ", longitud=" + longitud +
                ", participantes=" + participantes + // Ahora mostrará la representación del Map
                '}';
    }
}