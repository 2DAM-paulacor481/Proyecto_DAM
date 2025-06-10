package iesmm.pmdm.eventconnect.Adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

import iesmm.pmdm.eventconnect.Model.Evento;
import iesmm.pmdm.eventconnect.R;

public class EventoAdapter extends RecyclerView.Adapter<EventoAdapter.EventoViewHolder> {

    List<Evento> listaEventos;

    // Recibo la lista de eventos
    public EventoAdapter(List<Evento> listaEventos) {
        this.listaEventos = listaEventos;
    }

    static class EventoViewHolder extends RecyclerView.ViewHolder {
        TextView txtTitulo, txtDescripcion, txtCategoria, txtFecha, txtCreadorEvento;

        public EventoViewHolder(View itemView) {
            super(itemView);
            txtTitulo = itemView.findViewById(R.id.titulo_evento);
            txtCategoria = itemView.findViewById(R.id.categoria_evento);
            txtFecha = itemView.findViewById(R.id.fecha_evento);
            txtDescripcion = itemView.findViewById(R.id.descripcion_evento);
            txtCreadorEvento = itemView.findViewById(R.id.creador_evento);
        }
    }

    @Override
    public EventoViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.layout_todos_elemento_evento, parent, false);
        return new EventoAdapter.EventoViewHolder(view);

    }

    @Override
    public void onBindViewHolder(@NonNull EventoViewHolder holder, int position) {
        // Asigna los datos de un evento a las vistas de la tarjeta
        Evento evento = listaEventos.get(position);

        holder.txtTitulo.setText(evento.getTitulo());
        holder.txtCategoria.setText("Categoría: " + (evento.getCategoria() != null ? evento.getCategoria() : "N/A"));
        holder.txtFecha.setText("Fecha: " + (evento.getFecha() != null ? evento.getFecha() : "N/A"));
        holder.txtDescripcion.setText(evento.getDescripcion());
        holder.txtCreadorEvento.setText("Creador: " + (evento.getNombreCreador() != null ? evento.getNombreCreador() : "Desconocido"));

    }

    @Override
    public int getItemCount() {
        // Devuelve el número total de elementos en la lista
        return listaEventos.size();
    }
}
