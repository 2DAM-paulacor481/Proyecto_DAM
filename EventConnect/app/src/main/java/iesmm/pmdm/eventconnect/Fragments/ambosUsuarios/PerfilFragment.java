package iesmm.pmdm.eventconnect.Fragments.ambosUsuarios;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.List;

import iesmm.pmdm.eventconnect.DAO.DAOImpl;
import iesmm.pmdm.eventconnect.R;

public class PerfilFragment extends Fragment {

    private FirebaseAuth mAuth;
    private DAOImpl dao;
    private List datosUsuario;
    private TextView tvNombre, tvEmail;
    private Button btnModificarDatos, btnGuardar, btnCancelar;
    private EditText etNombre, etEmail;
    private LinearLayout datosEditar;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.layout_todos_perfil, container, false);

        mAuth = FirebaseAuth.getInstance();
        dao = new DAOImpl();

        datosUsuario = dao.getDatosUsuario(mAuth);

        tvNombre = view.findViewById(R.id.tvNombre);
        tvNombre.setText((CharSequence) datosUsuario.get(0));
        tvEmail = view.findViewById(R.id.tvEmail);
        tvEmail.setText((CharSequence) datosUsuario.get(1));
        btnModificarDatos = view.findViewById(R.id.btnModificarDatos);
        btnGuardar = view.findViewById(R.id.btnGuardar);
        btnCancelar = view.findViewById(R.id.btnCancelar);
        etNombre = view.findViewById(R.id.etNombre);
        etEmail = view.findViewById(R.id.etEmail);
        datosEditar = view.findViewById(R.id.datos_editar);

        btnModificarDatos.setOnClickListener(v -> {

            tvNombre.setVisibility(View.GONE);
            tvEmail.setVisibility(View.GONE);

            etNombre.setText(tvNombre.getText());
            etEmail.setText(tvEmail.getText());

            datosEditar.setVisibility(View.VISIBLE);
            etNombre.setVisibility(View.VISIBLE);
            etEmail.setVisibility(View.VISIBLE);
            btnGuardar.setVisibility(View.VISIBLE);
            btnCancelar.setVisibility(View.VISIBLE);
            btnModificarDatos.setVisibility(View.GONE);
        });

        btnGuardar.setOnClickListener(v -> {
            tvNombre.setText(etNombre.getText().toString());
            tvEmail.setText(etEmail.getText().toString());

            tvNombre.setVisibility(View.VISIBLE);
            tvEmail.setVisibility(View.VISIBLE);
            etNombre.setVisibility(View.GONE);
            etEmail.setVisibility(View.GONE);
            btnGuardar.setVisibility(View.GONE);
            btnCancelar.setVisibility(View.GONE);
            btnModificarDatos.setVisibility(View.VISIBLE);
            dao.modificarDatosUsuario(mAuth, etNombre.getText().toString(), etEmail.getText().toString());
        });

        btnCancelar.setOnClickListener(v -> {
            tvNombre.setVisibility(View.VISIBLE);
            tvEmail.setVisibility(View.VISIBLE);
            etNombre.setVisibility(View.GONE);
            etEmail.setVisibility(View.GONE);
            btnGuardar.setVisibility(View.GONE);
            btnCancelar.setVisibility(View.GONE);
            btnModificarDatos.setVisibility(View.VISIBLE);
        });



        return view;
    }
}