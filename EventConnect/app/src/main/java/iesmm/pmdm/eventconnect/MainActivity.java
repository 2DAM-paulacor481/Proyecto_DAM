package iesmm.pmdm.eventconnect;

import android.content.Intent;
import android.content.SharedPreferences;
import android.icu.text.LocaleDisplayNames;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;
// import android.widget.TextView; // Si usas tvUsuario y tvInfo, asegúrate de importarlo

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle; // Necesario para el botón de hamburguesa
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

// Importa tus fragmentos
import iesmm.pmdm.eventconnect.Fragments.administrador.ListaUsuariosAdminFragment;
import iesmm.pmdm.eventconnect.Fragments.ambosUsuarios.ListaEventosFragment;
import iesmm.pmdm.eventconnect.Fragments.ambosUsuarios.MapaFragment;
import iesmm.pmdm.eventconnect.Fragments.ambosUsuarios.PerfilFragment;
// TODO: Si tienes un fragmento de lista de usuarios para admin, impórtalo aquí
// import iesmm.pmdm.eventconnect.Fragments.AdminUserListFragment;
// TODO: Si tienes un fragmento de perfil de admin, impórtalo aquí
// import iesmm.pmdm.eventconnect.Fragments.PerfilAdminFragment;


public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private FirebaseAuth mAuth;
    private DatabaseReference Database;
    private String userId, idRol;

    // Referencias de UI de la actividad (ahora variables para el layout actual)
    private DrawerLayout drawerLayout;
    private NavigationView sideNavigationView; // Tu NavigationView lateral
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("MainActivity", "onCreate() llamado");

        mAuth = FirebaseAuth.getInstance();

        userId = getIntent().getStringExtra("userId");
        Log.d("MainActivity", "userId recibido del Intent: " + userId);

        if ((userId == null || userId.isEmpty())) {
            Log.d("MainActivity", "Usuario autenticado pero sin userId válido del Intent, redirigiendo a Login");
            redirectToLogin();
            return;
        }

        if (userId != null && !userId.isEmpty()) {
            Database = FirebaseDatabase.getInstance("https://eventconnectapp-96ed6-default-rtdb.europe-west1.firebasedatabase.app")
                    .getReference("usuarios").child(userId);
            Log.d("MainActivity", "Referencia a la base de datos con userId: " + Database);

            Database.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    Log.d("MainActivity", "onDataChange() llamado");
                    if (snapshot.exists()) {
                         idRol = snapshot.child("id_rol").getValue(String.class);

                        // Cargar el layout correcto basado en el rol
                        if ("1".equals(idRol)) { // Rol de Administrador
                            setContentView(R.layout.layout_admin_menu); // Cargar layout de administrador

                            toolbar = findViewById(R.id.toolbar_admin); // ID de tu Toolbar en activity_admin.xml
                            drawerLayout = findViewById(R.id.drawer_layout_admin); // ID de tu DrawerLayout en activity_admin.xml
                            sideNavigationView = findViewById(R.id.nav_view_admin); // ID de tu NavigationView en activity_admin.xml
                            setSupportActionBar(toolbar); // Establecer la Toolbar como ActionBar
                            sideNavigationView.getMenu().clear();
                            sideNavigationView.inflateMenu(R.menu.menu_admin);
                            sideNavigationView.setNavigationItemSelectedListener(MainActivity.this); // Set the listener
                            setupDrawerAndToolbar();
                            loadFragment(new MapaFragment(), R.id.nav_mapa_eventos); // O R.id.nav_perfil con new PerfilAdminFragment()

                        } else if ("2".equals(idRol)) { // Rol de Usuario Normal
                            setContentView(R.layout.layout_usuario_menu); // Cargar layout de usuario
                            Log.d("MainActivity", "Cargando layout de usuario");

                            toolbar = findViewById(R.id.toolbar_usuario); // ID de tu Toolbar en activity_user.xml
                            drawerLayout = findViewById(R.id.drawer_layout_usuario); // ID de tu DrawerLayout en activity_user.xml
                            sideNavigationView = findViewById(R.id.nav_view_usuario); // ID de tu NavigationView en activity_user.xml
                            setSupportActionBar(toolbar); // Establecer la Toolbar como ActionBar

                            sideNavigationView.getMenu().clear();
                            sideNavigationView.inflateMenu(R.menu.menu_usuario);
                            sideNavigationView.setNavigationItemSelectedListener(MainActivity.this); // Set the listener

                            setupDrawerAndToolbar();

                            loadFragment(new MapaFragment(), R.id.nav_mapa_eventos); // O R.id.nav_perfil con new PerfilUserFragment()

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
        //  el ID del contenedor de fragmentos basado en el rol actual
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

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        // Fragment q voy a cargar
        Fragment selectedFragment = null;

        // Id del fragment container según el rol
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
            bundle.putString("rol", idRol);
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

    private void logout() {
        SharedPreferences prefs = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.clear();
        editor.apply();

        mAuth.signOut();

        redirectToLogin();
    }

    private void redirectToLogin() {
        Intent intent = new Intent(MainActivity.this, Login.class); // Asegúrate que esta es tu clase de Login
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}