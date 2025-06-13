package iesmm.pmdm.eventconnect.Fragments.administrador;

import android.app.AlertDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import iesmm.pmdm.eventconnect.Adapters.UsuarioAdapter;
import iesmm.pmdm.eventconnect.DAO.DAOImpl;
import iesmm.pmdm.eventconnect.Model.Usuario;
import iesmm.pmdm.eventconnect.R;

public class ListaUsuariosAdminFragment extends Fragment {

    private FirebaseAuth mAuth;
    private DAOImpl dao;
    private String idRol;

    private RecyclerView recyclerView;
    private UsuarioAdapter usuarioAdapter;
    private List<Map<String, String>> listaUsuarios; // Se declara aquí

    private ProgressBar progressBar;
    private TextView tvMensajeNoAdmin;
    private TextView tvTituloListaUsuarios;

    private AlertDialog progressDialog;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAuth = FirebaseAuth.getInstance();
        dao = new DAOImpl();

        // Recibo el rol del usuario
        Bundle args = getArguments();
        if (args != null) {
            idRol = args.getString("idRol");
            Log.d("ListaUsuariosAdmin", "Rol recibido en onCreate: " + idRol);
        } else {
            idRol = "0";
            Log.w("ListaUsuariosAdmin", "No se recibió el rol en los argumentos.");
        }
        listaUsuarios = new ArrayList<>();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.layout_fragment_admin_lista, container, false);

        recyclerView = view.findViewById(R.id.recyclerViewUsuariosAdmin);
        progressBar = view.findViewById(R.id.progressBarListaUsuarios);
        tvMensajeNoAdmin = view.findViewById(R.id.tvMensajeNoAdmin);
        tvTituloListaUsuarios = view.findViewById(R.id.tvTituloListaUsuarios);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        usuarioAdapter = new UsuarioAdapter(listaUsuarios);
        recyclerView.setAdapter(usuarioAdapter);

        // Compruebo el rol del usuario
        // Si es 1 administrador, muestro la lista de usuarios
        if ("1".equals(idRol)) {
            tvMensajeNoAdmin.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
            tvTituloListaUsuarios.setVisibility(View.VISIBLE);
            Log.d("ListaUsuariosAdmin", "Rol es 1, cargando usuarios");
            cargarListaDeUsuarios();
        } else {
            tvMensajeNoAdmin.setVisibility(View.VISIBLE);
            tvMensajeNoAdmin.setText("No tienes permisos para ver la lista de usuarios.");
            recyclerView.setVisibility(View.GONE);
            tvTituloListaUsuarios.setVisibility(View.GONE);
            progressBar.setVisibility(View.GONE);
            Toast.makeText(getContext(), "Permiso denegado: No eres administrador.", Toast.LENGTH_LONG).show();
        }

        return view;
    }

    // Metodo para cargar la lista de usuarios
    private void cargarListaDeUsuarios() {
        // Muestro el progressBar
        progressBar.setVisibility(View.VISIBLE);

        dao.obtenerTodosLosUsuarios(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                progressBar.setVisibility(View.GONE);
                // Limpio la lista
                listaUsuarios.clear();

                // Recorro la lista de usuarios
                if (snapshot.exists()) {
                    for (DataSnapshot userSnapshot : snapshot.getChildren()) {
                        Usuario usuario = userSnapshot.getValue(Usuario.class);
                        String rol = userSnapshot.child("id_rol").getValue(String.class);

                        // Si el rol es nulo, lo igualo a 0
                        if (rol == null) {
                            rol = "0";
                        }


                        if (usuario != null) {
                            Map<String, String> usuarioData = new HashMap<>();
                            usuarioData.put("username", usuario.getUsername());
                            usuarioData.put("correo", usuario.getCorreo());

                            listaUsuarios.add(usuarioData);
                            Log.d("ListaUsuariosAdmin", "Usuario procesado: " + usuario.getUsername() + ", Correo: " + usuario.getCorreo() + ", Rol: " + rol);
                        } else {
                            Log.w("ListaUsuariosAdmin", "Datos de usuario (username/correo) faltan para snapshot: " + userSnapshot.getKey());
                        }
                    }
                }

                // Si la lista está vacía, muestro un mensaje
                if (listaUsuarios.isEmpty()) {
                    Toast.makeText(getContext(), "No se encontraron usuarios para mostrar.", Toast.LENGTH_SHORT).show();
                }

                // Actualizo el adapter
                usuarioAdapter.actualizarDatos(listaUsuarios);
                Log.d("ListaUsuariosAdmin", "UsuarioAdapter actualizado con " + listaUsuarios.size() + " usuarios.");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                progressBar.setVisibility(View.GONE);
                Log.e("ListaUsuariosAdmin", "Error al cargar usuarios desde Firebase: " + error.getMessage());
                Toast.makeText(getContext(), "Error al cargar la lista: " + error.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }


    private void ocultarProgressDialog() {
        // Si el diálogo de progreso está visible, lo oculto
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ocultarProgressDialog();
    }
}