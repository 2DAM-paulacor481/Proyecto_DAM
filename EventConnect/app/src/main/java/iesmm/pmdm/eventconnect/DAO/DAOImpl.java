package iesmm.pmdm.eventconnect.DAO;


import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import iesmm.pmdm.eventconnect.Model.Usuario;

public class DAOImpl implements DAO{

    // Instancio la base de datos
    private DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();

    public DAOImpl() {

    }

    // Metodo para obtener los datos del usuario
    public List<String> getDatosUsuario (@NonNull FirebaseAuth mAuth) {

        // Cojo el usuario logueado
        FirebaseUser currentUser = mAuth.getCurrentUser();
        List<String> datosUsuario = new ArrayList<>();

        if (currentUser != null) {
            // Si el usuario está logueado, obtengo sus datos
            // Si el nombre de ususario del usuario logueado es nulo, devuelvo ""
            datosUsuario.add(currentUser.getDisplayName() != null ? currentUser.getDisplayName() : "");
            datosUsuario.add(currentUser.getEmail() != null ? currentUser.getEmail() : "");
        } else {
            // Si no lo está no devuelvo nada
            datosUsuario.add("");
            datosUsuario.add("");
        }
        return datosUsuario;
    }

    // Metodo para modificar los datos del usuario
    public void modificarDatosUsuario(@NonNull FirebaseAuth mAuth, String nombreNuevo, String emailNuevo) {
        FirebaseUser currentUser = mAuth.getCurrentUser();

        if (currentUser == null) {
            Log.e("Método Modificar", "Usuario no logueado. No se pueden modificar los datos.");
            return;
        }

        // Cadena uid del usuario
        String uid = currentUser.getUid();
        DatabaseReference usuarioRef = mDatabase.child("usuarios").child(uid);

        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                .setDisplayName(nombreNuevo)
                .build();

        // Actualizo el nombre de usuario
        currentUser.updateProfile(profileUpdates)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        usuarioRef.child("username").setValue(nombreNuevo);
                    } else {
                        Log.e("Método Modificar", "Fallo al actualizar el nombre" + task.getException().getMessage());
                    }
                });

        // Actualizo el correo
        currentUser.updateEmail(emailNuevo)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        usuarioRef.child("correo").setValue(emailNuevo);
                    } else {
                        Log.e(":::ACTUALIZAR", "Fallo al actualizar el correo " + task.getException().getMessage());
                    }
                });
    }

    // Metodo para obtener todos los usuarios
    public void obtenerTodosLosUsuarios(@NonNull ValueEventListener listener) {
        DatabaseReference usersRef = mDatabase.child("usuarios");
        usersRef.addListenerForSingleValueEvent(listener);
    }

    // Metodo para obtener todos los eventos
    public void obtenerTodosLosEventos(@NonNull ValueEventListener listener) {
        DatabaseReference eventosRef = mDatabase.child("eventos");
        eventosRef.addListenerForSingleValueEvent(listener);
    }

    // Metodo para obtener los eventos de un usuario
    public void obtenerEventosPorUsuario(@NonNull ValueEventListener listener, String nombreUsuarioActual) {
        DatabaseReference eventosRef = mDatabase.child("eventos");

        Query query = eventosRef.orderByChild("nombreCreador").equalTo(nombreUsuarioActual);

        query.addListenerForSingleValueEvent(listener);
    }


    // Metodo para crear un evento
    public void crearEvento(String categoria, String descripcion, String fecha, double latitud, double longitud, String nombreCreador, String titulo) {
        DatabaseReference eventosRef = mDatabase.child("eventos");

        String eventoId = eventosRef.push().getKey();

        // Creo un mapa con los datos del evento
        Map<String, Object> eventoData = new HashMap<>();
        eventoData.put("categoria", categoria);
        eventoData.put("descripcion", descripcion);
        eventoData.put("fecha", fecha);
        eventoData.put("latitud", latitud);
        eventoData.put("longitud", longitud);
        eventoData.put("nombreCreador", nombreCreador);
        eventoData.put("titulo", titulo);

        // Guardo el evento en la base de datos
        if (eventoId != null) {
            eventosRef.child(eventoId).setValue(eventoData);
        } else {
            Log.e("Metodo crear", "No se ha podido crear el evento");
        }
    }

    // Metodo para unir un usuario a un evento
    public void anadirParticipanteAEvento(String eventoId, String userId, String userName) {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        String userEmail = "";
        if (currentUser != null && currentUser.getEmail() != null) {
            userEmail = currentUser.getEmail();
        }

        // Creo una instancia de la clase usuario
        Usuario nuevoParticipante = new Usuario(userEmail, userName);

        // Referencia a la base de datos
        DatabaseReference participanteRef = mDatabase
                .child("eventos")
                .child(eventoId)
                .child("participantes")
                .child(userId);

        // Guardo el objeto Usuario completo
        participanteRef.setValue(nuevoParticipante)
                .addOnSuccessListener(aVoid -> {
                    Log.d("Metodo participación", "Se ha unido");
                })
                .addOnFailureListener(e -> {
                    Log.d("Metodo participación", "No se ha unido");
                });
    }


    // Metodo para eliminar un evento
    public void eliminarEvento(String eventoId, OnSuccessListener<Void> successListener, OnFailureListener failureListener) {
        if (eventoId == null || eventoId.isEmpty()) {
            failureListener.onFailure(new IllegalArgumentException("El id de evento no puede ser nulo o vacío."));
            return;
        }

        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
        DatabaseReference eventoRef = mDatabase.child("eventos").child(eventoId);

        eventoRef.removeValue()
                .addOnSuccessListener(successListener)
                .addOnFailureListener(failureListener);
    }
}