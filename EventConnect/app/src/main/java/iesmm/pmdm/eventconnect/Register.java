package iesmm.pmdm.eventconnect;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
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
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


import java.util.HashMap;

public class Register extends AppCompatActivity {

    TextInputEditText editTextEmail, editTextPassword, editTextUsername;
    Button buttonReg;
    FirebaseAuth mAuth;
    ProgressBar progressBar;
    TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mAuth = FirebaseAuth.getInstance();
        editTextUsername = findViewById(R.id.username);
        editTextEmail = findViewById(R.id.email);
        editTextPassword = findViewById(R.id.password);
        buttonReg = findViewById(R.id.btn_register);
        progressBar = findViewById(R.id.progressBar);
        textView = findViewById(R.id.loginNow);

        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), Login.class);
                startActivity(intent);
                finish();
            }
        });

        buttonReg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressBar.setVisibility(View.VISIBLE);

                String email = String.valueOf(editTextEmail.getText());
                String password = String.valueOf(editTextPassword.getText());
                String username = String.valueOf(editTextUsername.getText());

                if (TextUtils.isEmpty(username)){
                    Toast.makeText(Register.this, "Ingresa el nombre de usuario", Toast.LENGTH_SHORT).show();
                    progressBar.setVisibility(View.GONE);
                    return;
                }

                if (TextUtils.isEmpty(email)) {
                    Toast.makeText(Register.this, "Ingresa el email", Toast.LENGTH_SHORT).show();
                    progressBar.setVisibility(View.GONE);
                    return;
                }

                if (TextUtils.isEmpty(password)) {
                    Toast.makeText(Register.this, "Ingresa la contraseña", Toast.LENGTH_SHORT).show();
                    progressBar.setVisibility(View.GONE);
                    return;
                }

                mAuth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    FirebaseUser user = mAuth.getCurrentUser();
                                    if (user != null) {
                                        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                                .setDisplayName(username)
                                                .build();

                                        user.updateProfile(profileUpdates)
                                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> profileTask) {
                                                        if (profileTask.isSuccessful()) {
                                                            DatabaseReference reference = FirebaseDatabase.getInstance("https://eventconnectapp-96ed6-default-rtdb.europe-west1.firebasedatabase.app")
                                                                    .getReference("usuarios");
                                                            HashMap<String, Object> map = new HashMap<>();
                                                            map.put("correo", email);
                                                            map.put("id_rol", "2");
                                                            map.put("password", password);
                                                            map.put("username", username);

                                                            reference.child(user.getUid()).setValue(map)
                                                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                        @Override
                                                                        public void onComplete(@NonNull Task<Void> dbTask) {
                                                                            progressBar.setVisibility(View.GONE);
                                                                            if (dbTask.isSuccessful()) {
                                                                                Toast.makeText(Register.this, "Registro exitoso", Toast.LENGTH_SHORT).show();
                                                                                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                                                                                startActivity(intent);
                                                                                finish();
                                                                            } else {
                                                                                Toast.makeText(Register.this, "Error al guardar datos en la base de datos.", Toast.LENGTH_SHORT).show();
                                                                            }
                                                                        }
                                                                    });
                                                        } else {
                                                            progressBar.setVisibility(View.GONE);
                                                            Toast.makeText(Register.this, "Error al actualizar el perfil de usuario en Auth.", Toast.LENGTH_SHORT).show();
                                                        }
                                                    }
                                                });
                                    }
                                } else {
                                    progressBar.setVisibility(View.GONE);
                                    Toast.makeText(Register.this, "Fallo de registro: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
    }
}