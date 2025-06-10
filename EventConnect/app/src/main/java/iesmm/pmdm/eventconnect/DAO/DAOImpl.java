package iesmm.pmdm.eventconnect.DAO;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class DAOImpl {

    private DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();

    public DAOImpl() {

    }

    public List<String> getDatosUsuario (@NonNull FirebaseAuth mAuth) {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        List<String> datosUsuario = new ArrayList<>();
        if (currentUser != null) {
            datosUsuario.add(currentUser.getDisplayName() != null ? currentUser.getDisplayName() : "");
            datosUsuario.add(currentUser.getEmail() != null ? currentUser.getEmail() : "");
        } else {
            datosUsuario.add("");
            datosUsuario.add("");
        }
        return datosUsuario;
    }

    public void modificarDatosUsuario(@NonNull FirebaseAuth mAuth, String nombreNuevo, String emailNuevo) {
        FirebaseUser currentUser = mAuth.getCurrentUser();

        if (currentUser == null) {
            Log.e(":::ACTUALIZAR", "Usuario no logueado. No se pueden modificar los datos.");
            return;
        }

        String uid = currentUser.getUid();
        DatabaseReference usuarioRef = mDatabase.child("usuarios").child(uid);

        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                .setDisplayName(nombreNuevo)
                .build();

        currentUser.updateProfile(profileUpdates)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Log.d(":::ACTUALIZAR", "Nombre en Auth actualizado con éxito.");
                        usuarioRef.child("username").setValue(nombreNuevo)
                                .addOnSuccessListener(aVoid -> Log.d(":::ACTUALIZAR", "Username actualizado en DB correctamente."))
                                .addOnFailureListener(e -> Log.e(":::ACTUALIZAR", "Fallo al actualizar username en DB: " + e.getMessage()));
                    } else {
                        Log.e(":::ACTUALIZAR", "Fallo al actualizar nombre en Auth: " + task.getException().getMessage());
                    }
                });

        currentUser.updateEmail(emailNuevo)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Log.d(":::ACTUALIZAR", "Correo en Auth actualizado con éxito.");
                        usuarioRef.child("correo").setValue(emailNuevo)
                                .addOnSuccessListener(aVoid -> Log.d(":::ACTUALIZAR", "Correo actualizado en DB correctamente."))
                                .addOnFailureListener(e -> Log.e(":::ACTUALIZAR", "Fallo al actualizar correo en DB: " + e.getMessage()));
                    } else {
                        Log.e(":::ACTUALIZAR", "Fallo al actualizar correo en Auth: " + task.getException().getMessage());
                    }
                });
    }

    // Método para obtener todos los usuarios
    public void obtenerTodosLosUsuarios(@NonNull ValueEventListener listener) {
        DatabaseReference usersRef = mDatabase.child("usuarios");
        usersRef.addListenerForSingleValueEvent(listener);
    }

    // Método para obtener todos los eventos
    public void obtenerTodosLosEventos(@NonNull ValueEventListener listener) {
        DatabaseReference eventosRef = mDatabase.child("eventos");
        eventosRef.addListenerForSingleValueEvent(listener);
    }

    // Método para obtener los eventos por usuario
    public void obtenerEventosPorUsuario(@NonNull ValueEventListener listener) {
        FirebaseUser currentUser = mAuth.getCurrentUser();

        if (currentUser == null) {
            Log.e("DAOImpl", "No hay usuario logueado.");
            return;
        }

        // Obtener el username del usuario logueado
        String nombreCreador = currentUser.getDisplayName();

        if (nombreCreador == null || nombreCreador.isEmpty()) {
            Log.e("DAOImpl", "El usuario logueado no tiene un nombre de usuario, no se pueden obtener eventos.");
            return;
        }

        DatabaseReference eventosRef = mDatabase.child("eventos");

        // Filtro por el nombre de usuarioq que lo creo
        Query query = eventosRef.orderByChild("nombreCreador").equalTo(nombreCreador);

        query.addListenerForSingleValueEvent(listener);

    }

    /*
    // Método para obtener los eventos creados por el usuario actualmente logueado.
    // Usará el 'username' del usuario logueado de tu nodo 'usuarios' como 'id_creador'.
    public void obtenerEventosCreadosPorUsuario(@NonNull ValueEventListener listener) {
        FirebaseUser currentUser = mAuth.getCurrentUser();

        if (currentUser == null) {
            Log.e("DAOImpl", "No hay usuario logueado para obtener sus eventos creados.");
            listener.onDataChange(null); // Notificar con datos nulos si no hay usuario
            return;
        }

        // Primero, obtener el username del usuario logueado (que es tu 'id_creador')
        String uid = currentUser.getUid();
        obtenerIdUsuarioPorUID(uid, new IdUsuarioCallback() {
            @Override
            public void onIdUsuarioObtenido(String idCreador) {
                if (idCreador == null || idCreador.isEmpty()) {
                    Log.e("DAOImpl", "Username (id_creador) del usuario logueado no encontrado en la base de datos.");
                    listener.onDataChange(null);
                    return;
                }

                DatabaseReference eventosRef = mDatabase.child(EVENTOS_NODE);
                Query query = eventosRef.orderByChild("id_creador").equalTo(idCreador);
                query.addListenerForSingleValueEvent(listener);
            }
        });

     */

}