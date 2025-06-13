package iesmm.pmdm.eventconnect.Fragments.ambosUsuarios;

import static android.content.ContentValues.TAG;

import android.annotation.SuppressLint;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.TextView;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;


import iesmm.pmdm.eventconnect.Adapters.EventoAdapter;
import iesmm.pmdm.eventconnect.DAO.DAOImpl;
import iesmm.pmdm.eventconnect.Model.Evento;
import iesmm.pmdm.eventconnect.R;

public class ListaEventosFragment extends Fragment {

    private RecyclerView recyclerView;
    private EventoAdapter eventoAdapter;
    private List<Evento> listaEventos;
    private DAOImpl dao;
    private TextView tvTitulo;

    private String idRol;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle args = getArguments();
        if (args != null) {
            idRol = args.getString("idRol");
            Log.d("ListaUsuariosAdmin", "Rol recibido en onCreate: " + idRol);
        } else {
            idRol = "0";
            Log.w("ListaUsuariosAdmin", "No se recibió el rol en los argumentos.");
        }

        dao = new DAOImpl();
    }

    @SuppressLint("MissingInflatedId")
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.layout_fragment_todos_lista_eventos, container, false);

        // Inicializar vistas
        recyclerView = view.findViewById(R.id.recycler_view_eventos);

        // Configurar RecyclerView
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        listaEventos = new ArrayList<>();
        eventoAdapter = new EventoAdapter(listaEventos, idRol);
        recyclerView.setAdapter(eventoAdapter);

        tvTitulo = view.findViewById(R.id.tv_titulo_lista_eventos);

        // Inicializar DAO
        dao = new DAOImpl();

        // Compruebo el rol del usuario
        if (idRol.equals("1")) {
            cargarEventos();
        } else if (idRol.equals("2")) {
            tvTitulo.setText("Mis Eventos");
            cargarEventosPorUsuario();
            cargarEventosPorUsuarioUnido();
        }

        return view;
    }

    // Metodo para cargar la lista de eventos
    private void cargarEventos() {
            // Cargo todos los eventos
            dao.obtenerTodosLosEventos(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    listaEventos.clear();
                    if (!snapshot.exists()) {
                        Log.d(TAG, "No se encontraron eventos");
                    }
                    for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                        Evento evento = postSnapshot.getValue(Evento.class);
                        if (evento != null) {
                            evento.setId(postSnapshot.getKey());
                            listaEventos.add(evento);
                        }
                    }
                    // Notifica al adaptador sobre los cambios de la lista
                    eventoAdapter.notifyDataSetChanged();
                    Log.d(TAG, "Eventos cargados: " + listaEventos.size());
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Log.w(TAG, "Error al cargar eventos: " + error.getMessage());
                }
            });
    }

    // Metodo para cargar la lista de eventos por usuario
    private void cargarEventosPorUsuario() {
        String currentUserName = FirebaseAuth.getInstance().getCurrentUser().getDisplayName();
        listaEventos.clear();

        // Cargo los eventos por usuario
        dao.obtenerEventosPorUsuario(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (!snapshot.exists()) {
                        Log.d(TAG, "No se encontraron eventos para el usuario: " + currentUserName);
                    }
                    for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                        Evento evento = postSnapshot.getValue(Evento.class);
                        if (evento != null) {
                            evento.setId(postSnapshot.getKey());
                            listaEventos.add(evento);
                            eventoAdapter.notifyDataSetChanged();
                        }
                    }
                    // Notifica al adaptador sobre los cambios de la lista
                    Log.d(TAG, "Eventos cargados para " + currentUserName + ": " + listaEventos.size());
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Log.w(TAG, "Error al cargar eventos para el usuario " + currentUserName + ": " + error.getMessage());
                }
            }, currentUserName);

    }

    // Metodo para cargar la lista de eventos por usuario unido a evento
    private void cargarEventosPorUsuarioUnido() {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser == null) {
            Log.d(TAG, "Usuario no logueado.");
            return;
        }

        final String currentUserName = currentUser.getDisplayName();
        final String currentUserEmail = currentUser.getEmail();

        if (currentUserName == null && currentUserEmail == null) {
            Log.d(TAG, "Nombre o correo del usuario no disponibles.");
            return;
        }

        dao.obtenerTodosLosEventos(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<Evento> eventosUnidos = new ArrayList<>();

                for (DataSnapshot eventoSnapshot : snapshot.getChildren()) {
                    Evento evento = eventoSnapshot.getValue(Evento.class);
                    if (evento != null) {
                        evento.setId(eventoSnapshot.getKey());

                        DataSnapshot participantesSnapshot = eventoSnapshot.child("participantes");
                        if (participantesSnapshot.exists()) {
                            for (DataSnapshot participanteEntry : participantesSnapshot.getChildren()) {
                                String participanteUsername = participanteEntry.child("username").getValue(String.class);
                                String participanteEmail = participanteEntry.child("correo").getValue(String.class);

                                if ((currentUserName != null && currentUserName.equals(participanteUsername)) ||
                                        (currentUserEmail != null && currentUserEmail.equals(participanteEmail))) {

                                    if (!listaEventos.contains(evento)) {
                                        eventosUnidos.add(evento);
                                    }
                                    break;
                                }
                            }
                        }
                    }
                }

                listaEventos.addAll(eventosUnidos);
                eventoAdapter.notifyDataSetChanged();
                Log.d(TAG, "Eventos unidos por participantes cargados: " + eventosUnidos.size());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.w(TAG, "Error al cargar todos los eventos para filtrar participantes: " + error.getMessage());
            }
        });
    }
}