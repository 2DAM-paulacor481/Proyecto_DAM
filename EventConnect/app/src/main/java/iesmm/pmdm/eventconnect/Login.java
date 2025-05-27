package iesmm.pmdm.eventconnect;

import android.content.Intent;
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

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects; // Necesario para Objects.requireNonNull

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

            // *** INTENTO DE AUTENTICACIÓN 1: Con Firebase Authentication (RECOMENDADO) ***
            mAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            FirebaseUser user = mAuth.getCurrentUser();
                            if (user != null) {
                                String userId = user.getUid(); // Este es el UID de Firebase Auth
                                Log.d("Login", "Autenticación Firebase Auth exitosa. UID: " + userId);
                                fetchUserDetailsAndNavigate(userId);
                            } else {
                                Toast.makeText(Login.this, "Error: Usuario autenticado nulo.", Toast.LENGTH_SHORT).show();
                                progressBar.setVisibility(View.GONE);
                            }
                        } else {
                            // Si Firebase Auth falla, intentamos la autenticación manual contra la DB
                            Log.w("Login", "Autenticación Firebase Auth fallida, intentando con DB. Error: " + task.getException().getMessage());
                            // *** INTENTO DE AUTENTICACIÓN 2: Manual contra la Realtime Database (Tu método actual) ***
                            authenticateManuallyAgainstDatabase(email, password);
                        }
                    });
        });
    }

    // Método para manejar la autenticación manual contra la base de datos
    private void authenticateManuallyAgainstDatabase(String email, String password) {
        Query query = usersDatabaseRef.orderByChild("correo").equalTo(email);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                progressBar.setVisibility(View.GONE);
                if (dataSnapshot.exists()) {
                    boolean foundUser = false;
                    for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                        String storedPassword = userSnapshot.child("password").getValue(String.class);
                        // Asegúrate de que el password NO sea nulo
                        if (storedPassword != null && storedPassword.equals(password)) {
                            // Autenticación manual exitosa
                            String userId = userSnapshot.getKey(); // El ID de nodo en la DB (e.g., "usuario_1")
                            Log.d("Login", "Autenticación manual DB exitosa. userId: " + userId);
                            Toast.makeText(Login.this, "Inicio de sesión exitoso (DB).", Toast.LENGTH_SHORT).show();
                            fetchUserDetailsAndNavigate(userId); // Usa el mismo método para navegar
                            foundUser = true;
                            break; // Salimos del bucle al encontrar el usuario
                        }
                    }
                    if (!foundUser) {
                        editTextPassword.setError("Contraseña incorrecta.");
                        Toast.makeText(Login.this, "Contraseña incorrecta.", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    editTextEmail.setError("No se encontró ningún usuario con este correo electrónico.");
                    Toast.makeText(Login.this, "No se encontró ningún usuario con este correo electrónico.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                progressBar.setVisibility(View.GONE);
                Log.e("LoginActivity", "Error al consultar usuario en DB: " + databaseError.getMessage());
                Toast.makeText(Login.this, "Error al iniciar sesión desde DB", Toast.LENGTH_SHORT).show();
            }
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
                    finish(); // ¡Cierra Login Activity!
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


    @Override
    public void onStart() {
        super.onStart();
        // Cuando la actividad se inicia, comprobamos si ya hay un usuario logueado en Firebase Auth
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            String userId = currentUser.getUid(); // Obtener el UID de Firebase Auth
            Log.d("Login", "Usuario autenticado en onStart con UID: " + userId);
            // Si hay un usuario logueado, obtenemos su rol y navegamos
            fetchUserDetailsAndNavigate(userId);
        } else {
            Log.d("Login", "No hay usuario autenticado en onStart.");
            // Si no hay usuario, el ProgressBar puede estar visible si se quedó de un intento fallido
            progressBar.setVisibility(View.GONE);
        }
    }
}