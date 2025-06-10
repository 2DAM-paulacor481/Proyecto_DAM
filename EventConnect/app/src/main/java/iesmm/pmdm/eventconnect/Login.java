package iesmm.pmdm.eventconnect;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;

public class Login extends AppCompatActivity {

    TextInputEditText editTextEmail, editTextPassword;
    Button buttonLogin;
    FirebaseAuth mAuth;
    ProgressBar progressBar;
    TextView textViewRegisterNow;
    DatabaseReference usersDatabaseRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mAuth = FirebaseAuth.getInstance();
        usersDatabaseRef = FirebaseDatabase.getInstance("https://eventconnectapp-96ed6-default-rtdb.europe-west1.firebasedatabase.app")
                .getReference("usuarios");

        editTextEmail = findViewById(R.id.email);
        editTextPassword = findViewById(R.id.password);
        buttonLogin = findViewById(R.id.btn_login);
        progressBar = findViewById(R.id.progressBar);
        textViewRegisterNow = findViewById(R.id.registerNow);

        textViewRegisterNow.setOnClickListener(view -> {
            Intent intent = new Intent(getApplicationContext(), Register.class);
            startActivity(intent);
            finish();
        });

        buttonLogin.setOnClickListener(view -> {
            progressBar.setVisibility(View.VISIBLE);

            String email = String.valueOf(editTextEmail.getText()).trim();
            String password = String.valueOf(editTextPassword.getText()).trim();

            if (TextUtils.isEmpty(email)) {
                Toast.makeText(Login.this, "Ingresa el email", Toast.LENGTH_SHORT).show();
                progressBar.setVisibility(View.GONE);
                return;
            }
            if (TextUtils.isEmpty(password)) {
                Toast.makeText(Login.this, "Ingresa la contraseña", Toast.LENGTH_SHORT).show();
                progressBar.setVisibility(View.GONE);
                return;
            }

            mAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            FirebaseUser user = mAuth.getCurrentUser();
                            if (user != null) {
                                String userId = user.getUid();
                                Log.d("Login", "Autenticación Firebase Auth exitosa. UID: " + userId);

                                fetchUserDetailsAndNavigate(userId);
                            } else {
                                Toast.makeText(Login.this, "Error: Usuario autenticado nulo.", Toast.LENGTH_SHORT).show();
                                progressBar.setVisibility(View.GONE);
                            }
                        } else {
                            Log.w("Login", "Autenticación Firebase Auth fallida, intentando con DB. Error: " + task.getException().getMessage());
                        }
                    });
        });
    }

    // Método para obtener los detalles del usuario (rol y nombre) y navegar a MainActivity
    private void fetchUserDetailsAndNavigate(String userId) {
        usersDatabaseRef.child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    String idRol = snapshot.child("id_rol").getValue(String.class);
                    String nombreUsuario = snapshot.child("nombre").getValue(String.class);

                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                    intent.putExtra("userId", userId); // Este userId puede ser UID de Auth o "usuario_X"
                    intent.putExtra("rol", idRol);
                    intent.putExtra("nombreUsuario", nombreUsuario);
                    startActivity(intent);
                    finish();
                } else {
                    // Esto puede ocurrir si un usuario se autentica con Auth pero no se guardan sus datos en DB con ese UID
                    // O si el userId manual no existe en la rama de usuarios.
                    Toast.makeText(Login.this, "Error: Datos de usuario no encontrados en la base de datos para ID: " + userId, Toast.LENGTH_LONG).show();
                    // Si el usuario se autenticó con Firebase Auth pero no hay datos en DB, podemos cerrar sesión de Auth
                    if (mAuth.getCurrentUser() != null && Objects.equals(mAuth.getCurrentUser().getUid(), userId)) {
                        mAuth.signOut();
                    }
                    progressBar.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("Login", "Error al obtener datos de usuario en fetchUserDetailsAndNavigate: " + error.getMessage());
                Toast.makeText(Login.this, "Error al cargar datos de usuario.", Toast.LENGTH_SHORT).show();
                progressBar.setVisibility(View.GONE);
            }
        });
    }

}