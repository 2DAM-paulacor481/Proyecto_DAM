package iesmm.pmdm.eventconnect.Fragments.ambosUsuarios;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;

import com.google.android.gms.maps.SupportMapFragment;

import iesmm.pmdm.eventconnect.R;

public class MapaFragment extends Fragment {

    private SupportMapFragment mapFragment;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.layout_fragment_mapa, container, false);

        if (getActivity() != null) {
            DrawerLayout drawerLayout = getActivity().findViewById(R.id.drawer_layout_usuario);
            if (drawerLayout != null) {
                drawerLayout.openDrawer(GravityCompat.START);
            }
        }

        mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        if (mapFragment == null) {
            mapFragment = SupportMapFragment.newInstance();
            getChildFragmentManager().beginTransaction().replace(R.id.map, mapFragment).commit();
        }

        //Inicialización del mapa abajo

        return view;
    }
}
