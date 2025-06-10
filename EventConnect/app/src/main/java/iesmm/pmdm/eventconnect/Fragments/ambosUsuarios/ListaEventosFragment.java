package iesmm.pmdm.eventconnect.Fragments.ambosUsuarios;

import static android.content.ContentValues.TAG;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
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

    private FirebaseAuth mAuth;
    private RecyclerView recyclerView;
    private EventoAdapter eventoAdapter;
    private List<Evento> listaEventos;
    private DAOImpl dao;

    private String currentUser;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);
        currentUser = sharedPreferences.getString("idRol", "2");
        Log.d(":::USUARIO", currentUser);
        Log.d(TAG, "Rol del usuario actual: " + currentUser);

        dao = new DAOImpl();
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.layout_fragment_todos_lista_eventos, container, false);

        // Inicializar vistas
        recyclerView = view.findViewById(R.id.recycler_view_eventos);

        // Configurar RecyclerView
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        listaEventos = new ArrayList<>();
        eventoAdapter = new EventoAdapter(listaEventos);
        recyclerView.setAdapter(eventoAdapter);

        // Inicializar DAO
        dao = new DAOImpl();

        cargarMisEventos();

        return view;
    }

    private void cargarMisEventos() {
        dao.obtenerEventosPorUsuario(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                listaEventos.clear();
                if (!snapshot.exists()) {
                    Log.d(TAG, "No se encontraron eventos para el usuario actual.");

                }
                for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                    Evento evento = postSnapshot.getValue(Evento.class);
                    if (evento != null) {
                        listaEventos.add(evento);
                    }
                }
                eventoAdapter.notifyDataSetChanged();
                Log.d(TAG, "Mis eventos cargados: " + listaEventos.size());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.w(TAG, "Error al cargar mis eventos: " + error.getMessage());
            }
        });
    }
}