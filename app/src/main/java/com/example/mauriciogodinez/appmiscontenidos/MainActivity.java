package com.example.mauriciogodinez.appmiscontenidos;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.annotation.TargetApi;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.provider.CallLog;
import android.telecom.Call;
import android.util.Log;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "ContentPLlamadas";

    //Creamos la URI del ContentProvider que utilizaremos
    private Uri uriLlamada = CallLog.Calls.CONTENT_URI;

    //en el arreglo indicamos las columnas que regresará en el query del ContentResolver
    private String[] colContentResolver = {
            CallLog.Calls.NUMBER,
            CallLog.Calls.DATE,
            CallLog.Calls.TYPE,
            CallLog.Calls.DURATION,
            CallLog.Calls.GEOCODED_LOCATION,
    };

    //Es el TextView que guardará todos los datos que obtengamos de la consulta.
    private TextView tvLlamadas;

    @TargetApi(Build.VERSION_CODES.M)

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tvLlamadas = findViewById(R.id.tvLlamadas);
        tvLlamadas.setText("");
    }

    @Override
    protected void onStart() {
        super.onStart();
        tvLlamadas = findViewById(R.id.tvLlamadas);

        try {
            //Indicamos los Index para las columnas que nos regresará nuestro cursor
            // al consultar el ContentProvider.
            int numero_index, fecha_index, tipo_index, duracion_index, geocode_index;

            //Aqui almacenamos los valores que regrese la consulta del ContentProvider.
            String numero;
            long fecha;
            int tipo;
            String duracion, geocode, tipoLlamada;

            //Consultamos con la URI especificada para que los regrese
            // ordenados DESC con ref Calls.DATE
            Cursor registros = getContentResolver().query(uriLlamada, colContentResolver, null, null,
                    CallLog.Calls.DATE + " DESC");

            //Creamos el Formatter para obtener una fecha legible
            SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yy HH:mm");

            //Con la matriz del cursor, hacemos un recorrido
            // para que regrese los datos indicados
            if (registros != null) {
                while (registros.moveToNext()) {
                    //Obtengo los indices de las columnas
                    numero_index = registros.getColumnIndex(colContentResolver[0]);
                    fecha_index = registros.getColumnIndex(colContentResolver[1]);
                    tipo_index = registros.getColumnIndex(colContentResolver[2]);
                    duracion_index = registros.getColumnIndex(colContentResolver[3]);
                    geocode_index = registros.getColumnIndex(colContentResolver[4]);

                    //Obtengo los datos a partir de los indices
                    numero = registros.getString(numero_index);
                    fecha = registros.getLong(fecha_index);//Epoch UNIX
                    tipo = registros.getInt(tipo_index);
                    duracion = registros.getString(duracion_index);
                    geocode = registros.getString(geocode_index);

                    //Validando tipo de llamada
                    tipoLlamada = returnTipoLlamada(tipo);

                    //Creamos un string para concatenar los datos y agregarlos al TextView
                    String detalle = getResources().getString(R.string.etiqueta_numero) + numero + "\n"
                            + getResources().getString(R.string.etiqueta_fecha) + formatter.format(new Date(fecha)) + "\n"
                            + getResources().getString(R.string.etiqueta_tipo) + tipo + "\n"
                            + getResources().getString(R.string.etiqueta_duracion) + duracion + getResources().getString(R.string.segundos) + "\n"
                            + getResources().getString(R.string.etiqueta_geoCode) + geocode + "\n"
                            + getResources().getString(R.string.etiqueta_llamada) + tipoLlamada + "\n\n";

                    //Agregamos el string al TextView y continuamos con el bucle
                    tvLlamadas.append(detalle);
                }
                //liberamos la memoria del cursor
                registros.close();
            }
        } catch (Exception e) {
            Log.v(TAG, e.getMessage());
        }
    }

    /**
     * Método que valida el tipo de llamada que regresa el ContentProvider y asigna
     * el string correspondiente (entrada, perdida, salida, conectando, no definida)
     * @param tipo registro CallLog.Calls.TYPE proporcionado por el ContentProvider
     * @return string asignado al tipo
     */
    private String returnTipoLlamada(int tipo){
        switch (tipo) {
            case CallLog.Calls.INCOMING_TYPE:
                return getResources().getString(R.string.entrada);
            case CallLog.Calls.MISSED_TYPE:
                return getResources().getString(R.string.perdida);
            case CallLog.Calls.OUTGOING_TYPE:
                return getResources().getString(R.string.salida);
            case Call.STATE_CONNECTING:
                return getResources().getString(R.string.conectando);
            default:
                return getResources().getString(R.string.undefined);
        }
    }
}