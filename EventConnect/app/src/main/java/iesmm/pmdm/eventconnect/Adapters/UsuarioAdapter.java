package iesmm.pmdm.eventconnect.Adapters;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import iesmm.pmdm.eventconnect.R;

public class UsuarioAdapter extends RecyclerView.Adapter<UsuarioAdapter.UsuarioViewHolder> {

    private List<Map<String, String>> listaUsuarios;

    public UsuarioAdapter(List<Map<String, String>> listaUsuarios) {
        this.listaUsuarios = listaUsuarios;
    }


    public void actualizarDatos(List<Map<String, String>> nuevosDatos) {
        // Actualizo los datos de la lista
        this.listaUsuarios = new ArrayList<>(nuevosDatos);

        notifyDataSetChanged();
    }

    @Override
    public UsuarioViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_admin_elemento_lista, parent, false);
        return new UsuarioViewHolder(view);
    }

    @Override
    public void onBindViewHolder(UsuarioViewHolder holder, int position) {
        Map<String, String> usuario = listaUsuarios.get(position);
        holder.txtUsername.setText(usuario.get("username"));
        holder.txtCorreo.setText(usuario.get("correo"));
    }

    @Override
    public int getItemCount() {
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