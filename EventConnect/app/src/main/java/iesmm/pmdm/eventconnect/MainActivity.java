package iesmm.pmdm.eventconnect;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    private String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();

        if (currentUser == null) {
            // No hay usuario, mandamos al login
            Intent intent = new Intent(this, Login.class);
            startActivity(intent);
            finish();
        } else {
            userId = currentUser.getUid();
            mDatabase = FirebaseDatabase.getInstance().getReference("usuarios").child(userId);

            mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        String idRol = snapshot.child("id_rol").getValue(String.class);

                        if (idRol != null) {
                            if ("1".equals(idRol)) {
                                setContentView(R.layout.activity_admin);
                            } else if ("2".equals(idRol)) {
                                setContentView(R.layout.activity_user);
                            } else {
                                Toast.makeText(MainActivity.this, "Rol desconocido", Toast.LENGTH_SHORT).show();
                                setContentView(R.layout.activity_user);
                            }
                        } else {
                            Toast.makeText(MainActivity.this, "No se encontró id_rol", Toast.LENGTH_SHORT).show();
                            setContentView(R.layout.activity_user);
                        }
                    } else {
                        Toast.makeText(MainActivity.this, "No se encontró el usuario", Toast.LENGTH_SHORT).show();
                        setContentView(R.layout.activity_user);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Log.e("MainActivity", "Error leyendo usuario: " + error.getMessage());
                    Toast.makeText(MainActivity.this, "Error leyendo usuario", Toast.LENGTH_SHORT).show();
                    setContentView(R.layout.activity_user);
                }
            });
        }
    }
}
