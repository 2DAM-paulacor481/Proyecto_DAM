package iesmm.pmdm.eventconnect.Adapters;

import android.util.Log; // Asegúrate de importar Log
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList; // Importar ArrayList
import java.util.List;
import java.util.Map;

import iesmm.pmdm.eventconnect.R;

public class UsuarioAdapter extends RecyclerView.Adapter<UsuarioAdapter.UsuarioViewHolder> {

    private List<Map<String, String>> listaUsuarios;

    public UsuarioAdapter(List<Map<String, String>> listaUsuarios) {
        this.listaUsuarios = listaUsuarios; // Se inicializa con la referencia pasada (vacía inicialmente)
        Log.d("ADAPTER_LIFECYCLE", "UsuarioAdapter constructor llamado. Tamaño inicial de lista: " + this.listaUsuarios.size());
    }

    // MÉTODO PARA ACTUALIZAR LOS DATOS DEL ADAPTADOR
    public void actualizarDatos(List<Map<String, String>> nuevosDatos) {
        Log.d("ADAPTER_LIFECYCLE", "actualizarDatos() llamado. Tamaño de nuevosDatos: " + nuevosDatos.size());

        // ******* EL CAMBIO CLAVE ESTÁ AQUÍ *******
        // Reemplaza la referencia de la lista interna del adaptador
        // con una nueva lista que contenga los nuevos datos.
        // Esto asegura que el adaptador siempre trabaje con la lista correcta y no con una referencia antigua.
        this.listaUsuarios = new ArrayList<>(nuevosDatos);

        notifyDataSetChanged(); // Notifica al RecyclerView que los datos han cambiado
        Log.d("ADAPTER_LIFECYCLE", "actualizarDatos() finalizado. Tamaño interno de listaUsuarios: " + this.listaUsuarios.size());
    }

    @Override
    public UsuarioViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Log.d("ADAPTER_LIFECYCLE", "onCreateViewHolder() llamado."); // Log para ver si se crean vistas
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_admin_elemento_lista, parent, false);
        return new UsuarioViewHolder(view);
    }

    @Override
    public void onBindViewHolder(UsuarioViewHolder holder, int position) {
        Map<String, String> usuario = listaUsuarios.get(position);
        Log.d("ADAPTER_DEBUG", "onBindViewHolder() llamado. Position: " + position + ", Usuario: " + usuario.get("username") + ", Correo: " + usuario.get("correo"));
        holder.txtUsername.setText(usuario.get("username"));
        holder.txtCorreo.setText(usuario.get("correo"));
    }

    @Override
    public int getItemCount() {
        Log.d("ADAPTER_COUNT", "getItemCount() llamado. Tamaño de listaUsuarios: " + listaUsuarios.size());
        return listaUsuarios.size();
    }

    static class UsuarioViewHolder extends RecyclerView.ViewHolder {
        TextView txtUsername, txtCorreo;

        public UsuarioViewHolder(View itemView) {
            super(itemView);
            txtUsername = itemView.findViewById(R.id.txtUsername);
            txtCorreo = itemView.findViewById(R.id.txtCorreo);
        }
    }
}