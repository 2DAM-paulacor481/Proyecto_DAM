package iesmm.pmdm.eventconnect.Adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

import java.util.List;

import iesmm.pmdm.eventconnect.DAO.DAOImpl;
import iesmm.pmdm.eventconnect.Model.Evento;
import iesmm.pmdm.eventconnect.R;

public class EventoAdapter extends RecyclerView.Adapter<EventoAdapter.EventoViewHolder> {

    List<Evento> listaEventos;
    String idRol;
    DAOImpl dao;

    // Recibo la lista de eventos
    public EventoAdapter(List<Evento> listaEventos) {
        this.listaEventos = listaEventos;
    }

    public EventoAdapter(List<Evento> listaEventos, String idRol) {
        this.listaEventos = listaEventos;
        this.idRol = idRol;
    }

    // ViewHolder para cada elemento de la lista
    static class EventoViewHolder extends RecyclerView.ViewHolder {
        TextView txtTitulo, txtDescripcion, txtCategoria, txtFecha, txtCreadorEvento;
        Button btnElimnarEvento;

        public EventoViewHolder(View itemView) {
            super(itemView);
            txtTitulo = itemView.findViewById(R.id.titulo_evento);
            txtCategoria = itemView.findViewById(R.id.categoria_evento);
            txtFecha = itemView.findViewById(R.id.fecha_evento);
            txtDescripcion = itemView.findViewById(R.id.descripcion_evento);
            txtCreadorEvento = itemView.findViewById(R.id.creador_evento);
            btnElimnarEvento = itemView.findViewById(R.id.btnEliminarEvento);
        }
    }

    @Override
    public EventoViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.layout_todos_elemento_evento, parent, false);

        dao = new DAOImpl();
        return new EventoAdapter.EventoViewHolder(view);

    }

    @Override
    public void onBindViewHolder(@NonNull EventoViewHolder holder, int position) {
        Evento evento = listaEventos.get(position);

        holder.txtTitulo.setText(evento.getTitulo());
        holder.txtCategoria.setText("Categoría: " + (evento.getCategoria() != null ? evento.getCategoria() : "N/A"));
        holder.txtFecha.setText("Fecha: " + (evento.getFecha() != null ? evento.getFecha() : "N/A"));
        holder.txtDescripcion.setText(evento.getDescripcion());
        holder.txtCreadorEvento.setText("Creador: " + (evento.getNombreCreador() != null ? evento.getNombreCreador() : "Desconocido"));

        if (idRol.equals("1")) {
            holder.btnElimnarEvento.setOnClickListener(v -> eliminarEvento(v.getContext(), evento));
            holder.btnElimnarEvento.setVisibility(View.VISIBLE);
        }
    }

    private void eliminarEvento(Context context, Evento eventoAEliminar) {
        // Verifico si quiero eliminar el evento
        new AlertDialog.Builder(context)
                .setTitle("Eliminar Evento")
                .setMessage("¿Estás seguro de que quieres eliminar el evento " + eventoAEliminar.getTitulo() + "?")
                .setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Toast.makeText(context, "Eliminando evento: " + eventoAEliminar.getTitulo(), Toast.LENGTH_SHORT).show();

                        // LLamo al metodo del DAOImpl para eliminar
                        dao.eliminarEvento(eventoAEliminar.getId(),
                                new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Toast.makeText(context, "Evento '" + eventoAEliminar.getTitulo() + "' eliminado correctamente.", Toast.LENGTH_SHORT).show();

                                        int position = listaEventos.indexOf(eventoAEliminar);
                                        if (position != -1) {
                                            // Elimino el evento de la lista
                                            listaEventos.remove(position);
                                            notifyItemRemoved(position);
                                        }
                                    }
                                },
                                new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(context, "Error al eliminar evento: " + e.getMessage(), Toast.LENGTH_LONG).show();
                                        Log.e("EventoAdapter", "Fallo al eliminar evento: " + eventoAEliminar.getTitulo() + " - " + e.getMessage(), e);
                                    }
                                }
                        );
                    }
                })
                .setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        Toast.makeText(context, "No se ha eliminado ningún evento", Toast.LENGTH_SHORT).show();
                    }
                })
                .show();
    }

    @Override
    public int getItemCount() {
        // Contador para los elementos de la lista
        return listaEventos.size();
    }
}
