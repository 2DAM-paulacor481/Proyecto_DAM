package iesmm.pmdm.eventconnect.DAO;


import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

public interface DAO {

    List<String> getDatosUsuario (@NonNull FirebaseAuth mAuth);

    void modificarDatosUsuario(@NonNull FirebaseAuth mAuth, String nombreNuevo, String emailNuevo);

    void obtenerTodosLosUsuarios(@NonNull ValueEventListener listener);

    void obtenerTodosLosEventos(@NonNull ValueEventListener listener);

    void obtenerEventosPorUsuario(@NonNull ValueEventListener listener, String nombreUsuarioActual);

    void crearEvento(String categoria, String descripcion, String fecha, double latitud, double longitud, String nombreCreador, String titulo);

    void anadirParticipanteAEvento(String eventoId, String userId, String userName);

    void eliminarEvento(String eventoId, OnSuccessListener<Void> successListener, OnFailureListener failureListener);
}
