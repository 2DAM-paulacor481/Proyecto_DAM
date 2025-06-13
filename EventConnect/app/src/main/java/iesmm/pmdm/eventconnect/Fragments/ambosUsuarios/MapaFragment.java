package iesmm.pmdm.eventconnect.Fragments.ambosUsuarios;

import static android.content.ContentValues.TAG;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.cardview.widget.CardView;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import iesmm.pmdm.eventconnect.DAO.DAOImpl;
import iesmm.pmdm.eventconnect.Model.Evento;
import iesmm.pmdm.eventconnect.R;

public class MapaFragment extends Fragment implements OnMapReadyCallback, GoogleMap.OnMapClickListener, GoogleMap.OnMarkerClickListener {

    private SupportMapFragment mapFragment;
    private GoogleMap googleMap;
    private CardView cvNuevoEvento, cvEvento;
    private TextView tv2Titulo, tv2Categoria, tv2Fecha, tv2Descripcion, tv2NombreCreador;
    private Button btnSeleccionarUbicacion, btnUnirseAEvento, btnCerrar, btnCerrar2;

    private DAOImpl dao;
    private LatLng latLngSeleccionada;
    private Calendar selectedDateCalendar;

    private List<Evento> listaEventos;
    private Evento eventoSeleccionado;
    private Marker ultimoMarcadorPulsado = null;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.layout_fragment_mapa, container, false);

        if (getActivity() != null) {
            DrawerLayout drawerLayout = getActivity().findViewById(R.id.drawer_layout_usuario);
            if (drawerLayout != null) {
                if (!drawerLayout.isDrawerOpen(GravityCompat.START)) {
                }
            }
        }

        dao = new DAOImpl();

        cvNuevoEvento = view.findViewById(R.id.cvEventoNuevo);
        cvEvento = view.findViewById(R.id.cvEvento);
        btnSeleccionarUbicacion = view.findViewById(R.id.btnCrearEvento);

        btnCerrar = view.findViewById(R.id.btnCerrar);
        btnCerrar2 = view.findViewById(R.id.btnCerrar2);
        btnUnirseAEvento = view.findViewById(R.id.btnUnirseAEvento);

        tv2Titulo = view.findViewById(R.id.tv2Titulo);
        tv2Categoria = view.findViewById(R.id.tv2Categoria);
        tv2Fecha = view.findViewById(R.id.tv2Fecha);
        tv2Descripcion = view.findViewById(R.id.tv2Descripcion);
        tv2NombreCreador = view.findViewById(R.id.tv2NombreCreador);

        listaEventos = new ArrayList<>();

        btnCerrar.setOnClickListener(v -> {
            cvNuevoEvento.setVisibility(View.GONE);
            cargarEventos();
        });


        btnCerrar2.setOnClickListener(v -> {
            cvEvento.setVisibility(View.GONE);
            if (ultimoMarcadorPulsado != null) {
                ultimoMarcadorPulsado.setIcon(BitmapDescriptorFactory.fromBitmap(Bitmap.createScaledBitmap(BitmapFactory.
                        decodeResource(getResources(), R.drawable.marcador2), 100, 100, false)));
                ultimoMarcadorPulsado = null;
            }
            cargarEventos();
        });

        btnSeleccionarUbicacion.setOnClickListener(v -> showEventDialog());

        // Botón para unirse a un evento
        btnUnirseAEvento.setOnClickListener(v -> {
            if (eventoSeleccionado != null && FirebaseAuth.getInstance().getCurrentUser() != null) {
                String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
                String userName = FirebaseAuth.getInstance().getCurrentUser().getDisplayName();
                if (userName == null || userName.isEmpty()) {
                    userName = FirebaseAuth.getInstance().getCurrentUser().getEmail();
                    if (userName == null || userName.isEmpty()) {
                        userName = "Usuario Desconocido";
                    }
                }

                dao.anadirParticipanteAEvento(
                        eventoSeleccionado.getId(),
                        userId,
                        userName
                );
                Toast.makeText(getContext(), "Unido correctamente", Toast.LENGTH_SHORT).show();
            } else if (FirebaseAuth.getInstance().getCurrentUser() == null) {
                Toast.makeText(getContext(), "Debes iniciar sesión para unirte a un evento.", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getContext(), "No hay ningún evento seleccionado para unirse.", Toast.LENGTH_SHORT).show();
            }
        });

        mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        if (mapFragment == null) {
            mapFragment = SupportMapFragment.newInstance();
            getChildFragmentManager().beginTransaction().replace(R.id.map, mapFragment).commit();
        }

        mapFragment.getMapAsync(this);

        return view;
    }

    @Override
    public void onMapReady(@NonNull GoogleMap map) {
        googleMap = map;
        googleMap.setOnMapClickListener(this);
        googleMap.setOnMarkerClickListener(this);

        cargarEventos();
    }

    @Override
    public void onMapClick(@NonNull LatLng latLng) {

        if (cvNuevoEvento.getVisibility() == View.GONE) {
            cvNuevoEvento.setVisibility(View.VISIBLE);
            cvEvento.setVisibility(View.GONE);
        }

        latLngSeleccionada = latLng;

        // Limpio todos los marcadores existentes
        googleMap.clear();

        googleMap.addMarker(new MarkerOptions()
                .position(latLng)
                .icon(BitmapDescriptorFactory.fromBitmap(Bitmap.createScaledBitmap(BitmapFactory.
                        decodeResource(getResources(), R.drawable.marcador), 100, 100, false))));

        cargarEventos();
    }

    // Muestra el diálogo para crear un nuevo evento
    private void showEventDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.layout_dialog_crear_evento, null);

        EditText eventNameEditText = dialogView.findViewById(R.id.eventNameEditText);
        EditText eventDescriptionEditText = dialogView.findViewById(R.id.eventDescriptionEditText);
        EditText eventCategoryEditText = dialogView.findViewById(R.id.eventCategoryEditText);
        TextView selectedDateTextView = dialogView.findViewById(R.id.selectedDateTextView);
        Button pickDateButton = dialogView.findViewById(R.id.pickDateButton);

        selectedDateCalendar = Calendar.getInstance();

        updateDateTextView(selectedDateTextView, selectedDateCalendar);

        pickDateButton.setOnClickListener(v -> {

            new DatePickerDialog(requireContext(),
                    (view, year, month, dayOfMonth) -> {
                        selectedDateCalendar.set(Calendar.YEAR, year);
                        selectedDateCalendar.set(Calendar.MONTH, month);
                        selectedDateCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                        new TimePickerDialog(requireContext(),
                                (timePicker, hourOfDay, minute) -> {
                                    selectedDateCalendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                                    selectedDateCalendar.set(Calendar.MINUTE, minute);
                                    selectedDateCalendar.set(Calendar.SECOND, 0);
                                    selectedDateCalendar.set(Calendar.MILLISECOND, 0);
                                    updateDateTextView(selectedDateTextView, selectedDateCalendar);
                                },
                                selectedDateCalendar.get(Calendar.HOUR_OF_DAY),
                                selectedDateCalendar.get(Calendar.MINUTE),
                                true
                        ).show();
                    },
                    selectedDateCalendar.get(Calendar.YEAR),
                    selectedDateCalendar.get(Calendar.MONTH),
                    selectedDateCalendar.get(Calendar.DAY_OF_MONTH))
                    .show();
        });

        builder.setView(dialogView)
                .setPositiveButton("Aceptar", null)
                .setNegativeButton("Cancelar", (dialog, id) -> {
                    dialog.cancel();
                    Toast.makeText(getContext(), "Creación de evento cancelada.", Toast.LENGTH_SHORT).show();
                });

        AlertDialog dialog = builder.create();
        dialog.show();

        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(v -> {
            String titulo = eventNameEditText.getText().toString().trim();
            String descripcion = eventDescriptionEditText.getText().toString().trim();
            String categoria = eventCategoryEditText.getText().toString().trim();
            String fecha = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(selectedDateCalendar.getTime());

            if (titulo.isEmpty() || descripcion.isEmpty() || categoria.isEmpty()) {
                Toast.makeText(getContext(), "Por favor, completa todos los campos.", Toast.LENGTH_SHORT).show();
            } else if (latLngSeleccionada == null) {
                Toast.makeText(getContext(), "Por favor, selecciona una ubicación en el mapa primero.", Toast.LENGTH_SHORT).show();
            } else {
                Calendar now = Calendar.getInstance();

                if (selectedDateCalendar.before(now)) {
                    Toast.makeText(getContext(), "No se puede crear un evento con una fecha y hora anterior a la actual.", Toast.LENGTH_LONG).show();
                } else {
                    String nombreCreador = "Desconocido";
                    if (FirebaseAuth.getInstance().getCurrentUser() != null) {
                        nombreCreador = FirebaseAuth.getInstance().getCurrentUser().getDisplayName();
                        if (nombreCreador == null || nombreCreador.isEmpty()) {
                            nombreCreador = FirebaseAuth.getInstance().getCurrentUser().getEmail();
                            if (nombreCreador == null || nombreCreador.isEmpty()) {
                                nombreCreador = "Usuario sin nombre/email";
                            }
                        }
                    }

                    dao.crearEvento(
                            categoria,
                            descripcion,
                            fecha,
                            latLngSeleccionada.latitude,
                            latLngSeleccionada.longitude,
                            nombreCreador,
                            titulo
                    );

                    Toast.makeText(getContext(), "Evento '" + titulo + "' creado con éxito.", Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                    cargarEventos();
                }
            }
        });
    }

    // Metodo para cargar la lista de eventos
    private void cargarEventos() {
        googleMap.clear();

        dao.obtenerTodosLosEventos(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                listaEventos.clear();

                if (!snapshot.exists()) {
                    Log.d(TAG, "No se encontraron eventos");
                    return;
                }

                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
                // Obtengo la fecha y hora actual
                Date fechaActual = new Date();


                for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                    Evento evento = postSnapshot.getValue(Evento.class);

                    if (evento != null) {
                        try {
                            Date fechaEvento = sdf.parse(evento.getFecha());

                            // Comprobar si la fecha y hora del evento es posterior a la fecha y hora actual
                            if (fechaEvento.after(fechaActual)) {
                                evento.setId(postSnapshot.getKey());
                                listaEventos.add(evento);
                                Log.d("EVENTOCARGADO", "Evento posterior a la fecha actual: " + evento.getTitulo() + " - Fecha: " + evento.getFecha());

                                LatLng latLng = new LatLng(evento.getLatitud(), evento.getLongitud());

                                googleMap.addMarker(new MarkerOptions()
                                                .position(latLng)
                                                .icon(BitmapDescriptorFactory.fromBitmap(Bitmap.createScaledBitmap(BitmapFactory.
                                                        decodeResource(getResources(), R.drawable.marcador2), 100, 100, false))))
                                        .setTag(evento); // Es fundamental que el tag sea el objeto Evento completo
                            }
                        } catch (ParseException e) {
                            Log.e(TAG, "Error al parsear la fecha");
                        } catch (NullPointerException e) {
                            Log.e(TAG, "Error al procesar el evento");
                        }

                    } else {
                        Log.w(TAG, "Evento es null");
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.w(TAG, "Error al cargar eventos: " + error.getMessage());
            }
        });
    }

    // Actualiza el TextView con la fecha seleccionada
    private void updateDateTextView(TextView textView, Calendar calendar) {
        String dateFormat = "dd/MM/yyyy HH:mm";
        SimpleDateFormat sdf = new SimpleDateFormat(dateFormat, Locale.getDefault());
        textView.setText("Fecha del evento: " + sdf.format(calendar.getTime()));
    }

    @Override
    public boolean onMarkerClick(@NonNull Marker marker) {
        marker.setIcon(BitmapDescriptorFactory.fromBitmap(Bitmap.createScaledBitmap(
                BitmapFactory.decodeResource(getResources(), R.drawable.marcador3),
                100, 100, false)));

        Object tag = marker.getTag();
        if (tag instanceof Evento) {
            eventoSeleccionado = (Evento) tag;
        } else {
            Log.e("MapaFragment", "Error al pulsar el marker");
            return false;
        }

        tv2Titulo.setText(eventoSeleccionado.getTitulo());
        tv2Categoria.setText(eventoSeleccionado.getCategoria());
        tv2Fecha.setText(eventoSeleccionado.getFecha());
        tv2Descripcion.setText(eventoSeleccionado.getDescripcion());
        tv2NombreCreador.setText(eventoSeleccionado.getNombreCreador());

        cvEvento.setVisibility(View.VISIBLE);
        cvNuevoEvento.setVisibility(View.GONE);

        return false;
    }
}