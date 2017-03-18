package com.example.mauriciogodinez.appmiscontenidos;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.annotation.TargetApi;
import android.content.ContentResolver;
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
    Uri uriLlamada = CallLog.Calls.CONTENT_URI;
    //Indicamos los Index para las columnas que nos regresará nuestro cursor al consultar el CP.
    int numero_i;
    int fecha_i;
    int tipo_i;
    int duracion_i;
    int geocode_i;
    //Aqui almacenamos los valores que regrese la consulta del CP.
    String numero;
    Long fecha;
    int tipo;
    String duracion;
    String geocode;
    String tipoLlamada;
    //en el arreglo indicamos las columnas que regresará en el query del Content Resolver
    String[] campos = {
            CallLog.Calls.NUMBER,
            CallLog.Calls.DATE,
            CallLog.Calls.TYPE,
            CallLog.Calls.DURATION,
            CallLog.Calls.GEOCODED_LOCATION,
    };
    //Es el TextView que guardará todos los datos que obtengamos de la consulta.
    TextView tvLlamadas;

    @TargetApi(Build.VERSION_CODES.M)

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_main);

            tvLlamadas = (TextView) findViewById(R.id.tvLlamadas);
            tvLlamadas.setText("");
        }

        @Override
        protected void onStart(){
            super.onStart();
            tvLlamadas = (TextView) findViewById(R.id.tvLlamadas);

            try{
                ContentResolver contentResolver = getContentResolver();
                //Consultamos con la URI especificada y que los regrese ordenados DESC con ref Calls.DATE
                Cursor registros = contentResolver.query(uriLlamada, campos, null, null,
                                                                    CallLog.Calls.DATE + " DESC");
                //Creamos el Formatter para obtener una fecha legible
                SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yy HH:mm");
                //Con la matriz del cursos, hacemos un recorrido para que regrese los datos indicados
                while(registros.moveToNext()){
                    //Obtengo los indices de las columnas
                    numero_i = registros.getColumnIndex(campos[0]);
                    fecha_i = registros.getColumnIndex(campos[1]);
                    tipo_i = registros.getColumnIndex(campos[2]);
                    duracion_i = registros.getColumnIndex(campos[3]);
                    geocode_i = registros.getColumnIndex(campos[4]);

                    //Obtengo los datos a partir del indice obtenido
                    numero = registros.getString(numero_i);
                    fecha = registros.getLong(fecha_i);//Epoch UNIX
                    tipo = registros.getInt(tipo_i);
                    duracion = registros.getString(duracion_i);
                    geocode = registros.getString(geocode_i);

                    //Validando tipo de llamada
                    switch (tipo){
                        case CallLog.Calls.INCOMING_TYPE:
                            tipoLlamada = getResources().getString(R.string.entrada);
                            break;
                        case CallLog.Calls.MISSED_TYPE:
                            tipoLlamada = getResources().getString(R.string.perdida);
                            break;
                        case CallLog.Calls.OUTGOING_TYPE:
                            tipoLlamada = getResources().getString(R.string.salida);
                            break;
                        case Call.STATE_CONNECTING:
                            tipoLlamada = getResources().getString(R.string.conectando);
                            break;
                        default:
                            tipoLlamada = getResources().getString(R.string.undefined);
                    }
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
            }catch(Exception e){
                Log.v(TAG, e.getMessage());
            }
        }
    }
