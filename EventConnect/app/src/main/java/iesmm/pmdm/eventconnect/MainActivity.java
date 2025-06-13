package iesmm.pmdm.eventconnect;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import iesmm.pmdm.eventconnect.Fragments.administrador.ListaUsuariosAdminFragment;
import iesmm.pmdm.eventconnect.Fragments.ambosUsuarios.ListaEventosFragment;
import iesmm.pmdm.eventconnect.Fragments.ambosUsuarios.MapaFragment;
import iesmm.pmdm.eventconnect.Fragments.ambosUsuarios.PerfilFragment;



public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private FirebaseAuth mAuth;
    private DatabaseReference Database;
    private String userId, idRol;

    private DrawerLayout drawerLayout;
    private NavigationView sideNavigationView;
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mAuth = FirebaseAuth.getInstance();

        userId = getIntent().getStringExtra("userId");

        if ((userId == null || userId.isEmpty())) {
            redirectToLogin();
            return;
        }

        if (userId != null && !userId.isEmpty()) {
            Database = FirebaseDatabase.getInstance("https://eventconnectapp-96ed6-default-rtdb.europe-west1.firebasedatabase.app")
                    .getReference("usuarios").child(userId);

            Database.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    Log.d("MainActivity", "onDataChange() llamado");
                    if (snapshot.exists()) {
                         idRol = snapshot.child("id_rol").getValue(String.class);

                        // Cargo el layout correcto segun en el rol
                        // Rol de Administrador
                        if ("1".equals(idRol)) {
                            setContentView(R.layout.layout_admin_menu);

                            toolbar = findViewById(R.id.toolbar_admin);
                            drawerLayout = findViewById(R.id.drawer_layout_admin);
                            sideNavigationView = findViewById(R.id.nav_view_admin);
                            setSupportActionBar(toolbar);
                            sideNavigationView.getMenu().clear();
                            sideNavigationView.inflateMenu(R.menu.menu_admin);
                            sideNavigationView.setNavigationItemSelectedListener(MainActivity.this);
                            setupDrawerAndToolbar();
                            loadFragment(new MapaFragment(), R.id.nav_mapa_eventos);

                        // Rol de Usuario Normal
                        } else if ("2".equals(idRol)) {
                            setContentView(R.layout.layout_usuario_menu);
                            Log.d("MainActivity", "Cargando layout de usuario");

                            toolbar = findViewById(R.id.toolbar_usuario);
                            drawerLayout = findViewById(R.id.drawer_layout_usuario);
                            sideNavigationView = findViewById(R.id.nav_view_usuario);
                            setSupportActionBar(toolbar);

                            sideNavigationView.getMenu().clear();
                            sideNavigationView.inflateMenu(R.menu.menu_usuario);
                            sideNavigationView.setNavigationItemSelectedListener(MainActivity.this);

                            setupDrawerAndToolbar();

                            loadFragment(new MapaFragment(), R.id.nav_mapa_eventos);

                        } else {
                            setContentView(R.layout.layout_usuario_menu);
                            toolbar = findViewById(R.id.toolbar_usuario);
                            drawerLayout = findViewById(R.id.drawer_layout_usuario);
                            sideNavigationView = findViewById(R.id.nav_view_usuario);
                            setSupportActionBar(toolbar);
                            sideNavigationView.getMenu().clear();
                            sideNavigationView.inflateMenu(R.menu.menu_usuario);
                            sideNavigationView.setNavigationItemSelectedListener(MainActivity.this);
                            setupDrawerAndToolbar();
                            loadFragment(new PerfilFragment(), R.id.nav_perfil);
                        }

                    } else {
                        setContentView(R.layout.layout_usuario_menu);
                        toolbar = findViewById(R.id.toolbar_usuario);
                        drawerLayout = findViewById(R.id.drawer_layout_usuario);
                        sideNavigationView = findViewById(R.id.nav_view_usuario);
                        setSupportActionBar(toolbar);
                        sideNavigationView.getMenu().clear();
                        sideNavigationView.inflateMenu(R.menu.menu_usuario);
                        sideNavigationView.setNavigationItemSelectedListener(MainActivity.this);
                        setupDrawerAndToolbar();
                        loadFragment(new PerfilFragment(), R.id.nav_perfil);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    setContentView(R.layout.layout_usuario_menu);
                    toolbar = findViewById(R.id.toolbar_usuario);
                    drawerLayout = findViewById(R.id.drawer_layout_usuario);
                    sideNavigationView = findViewById(R.id.nav_view_usuario);
                    setSupportActionBar(toolbar);
                    sideNavigationView.getMenu().clear();
                    sideNavigationView.inflateMenu(R.menu.menu_usuario);
                    sideNavigationView.setNavigationItemSelectedListener(MainActivity.this);
                    setupDrawerAndToolbar();
                    loadFragment(new PerfilFragment(), R.id.nav_perfil);
                }
            });
        } else if (userId == null) {
            // Si userId es nulo, redirige a Login
            redirectToLogin();
        }
    }

    private void setupDrawerAndToolbar() {
        if (drawerLayout != null && toolbar != null) {
            ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                    this, drawerLayout, toolbar,
                    R.string.navigation_drawer_open, R.string.navigation_drawer_close);
            drawerLayout.addDrawerListener(toggle);
            toggle.syncState();
        }
    }

    private void loadFragment(Fragment fragment, int menuItemId) {
        int fragmentContainerId;
        if ("1".equals(idRol)) {
            fragmentContainerId = R.id.fragment_container_admin;
            Bundle args = new Bundle();
            args.putString("rol", idRol);
            fragment.setArguments(args);
        } else {
            fragmentContainerId = R.id.fragment_container_usuario;
            Bundle args = new Bundle();
            args.putString("rol", idRol);
            fragment.setArguments(args);
        }

        getSupportFragmentManager()
                .beginTransaction()
                .replace(fragmentContainerId, fragment)
                .commit();


        if (sideNavigationView != null) {
            sideNavigationView.setCheckedItem(menuItemId);
        }
    }

    // Menu de navegacion
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        Fragment selectedFragment = null;

        int fragmentContainerId;
        if ("1".equals(idRol)) {
            fragmentContainerId = R.id.fragment_container_admin;
        } else {
            fragmentContainerId = R.id.fragment_container_usuario;
        }

        if (id == R.id.nav_perfil) {
            selectedFragment = new PerfilFragment();
        } else if (id == R.id.nav_lista_eventos) {
            selectedFragment = new ListaEventosFragment();
        } else if (id == R.id.nav_mapa_eventos) {
            selectedFragment = new MapaFragment();
        } else if (id == R.id.nav_lista_usuarios) {
            selectedFragment = new ListaUsuariosAdminFragment();
        } else if (id == R.id.nav_cerrar_sesion) {
            logout();
            drawerLayout.closeDrawer(GravityCompat.START);
            return true;
        } else {
            Toast.makeText(this, "Opción no disponible", Toast.LENGTH_SHORT).show();
        }

        if (selectedFragment != null) {
            Bundle bundle = new Bundle();
            bundle.putString("idRol", idRol);
            selectedFragment.setArguments(bundle);
            getSupportFragmentManager().beginTransaction()
                    .replace(fragmentContainerId, selectedFragment)
                    .commit();

            setTitle(item.getTitle());

            if (sideNavigationView != null) {
                sideNavigationView.setCheckedItem(id);
            }
        }

        if (drawerLayout != null) {
            drawerLayout.closeDrawer(GravityCompat.START);
        }
        return true;
    }

    @Override
    protected void onStop() {
        super.onStop();
        logout();
    }

    // Cerrar sesion
    private void logout() {
        SharedPreferences prefs = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.clear();
        editor.apply();

        mAuth.signOut();

        redirectToLogin();
    }

    // Vuelvo hacia el Login
    private void redirectToLogin() {
        Intent intent = new Intent(MainActivity.this, Login.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}