package com.example.mauriciogodinez.appmiscontenidos;

import android.annotation.SuppressLint;
import android.databinding.DataBindingUtil;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.annotation.TargetApi;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.provider.CallLog;
import android.telecom.Call;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.mauriciogodinez.appmiscontenidos.databinding.ActivityMainBinding;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements
        LoaderManager.LoaderCallbacks<Cursor> {

    private static final int ID_CALLLOG_LOADER = 123;
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

    private ActivityMainBinding mMainBinding;

    @TargetApi(Build.VERSION_CODES.M)

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mMainBinding = DataBindingUtil.setContentView(this, R.layout.activity_main);

        getSupportLoaderManager().initLoader(ID_CALLLOG_LOADER, null, this);
    }

    /**
     * Método que valida el tipo de llamada que regresa el ContentProvider y asigna
     * el string correspondiente (entrada, perdida, salida, conectando, no definida)
     *
     * @param tipo registro CallLog.Calls.TYPE proporcionado por el ContentProvider
     * @return string asignado al tipo
     */
    private String returnTipoLlamada(int tipo) {
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

    private void setData(Cursor registros) {
        //Creamos el Formatter para obtener una fecha legible
        SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yy HH:mm", Locale.ENGLISH);

        //Con la matriz del cursor, hacemos un recorrido para que regrese los datos indicados
        if (registros != null) {
            while (registros.moveToNext()) {
                //Obtengo los datos a partir de los indices
                String numero = registros.getString(registros.getColumnIndex(colContentResolver[0]));
                //Epoch UNIX
                long fecha = registros.getLong(registros.getColumnIndex(colContentResolver[1]));
                int tipo = registros.getInt(registros.getColumnIndex(colContentResolver[2]));
                String duracion = registros.getString(registros.getColumnIndex(colContentResolver[3]));
                String geocode = registros.getString(registros.getColumnIndex(colContentResolver[4]));

                //Validando tipo de llamada
                String tipoLlamada = returnTipoLlamada(tipo);

                //Creamos un string para concatenar los datos y agregarlos al TextView
                String detalle = getResources().getString(R.string.etiqueta_numero) + numero + "\n"
                        + getResources().getString(R.string.etiqueta_fecha) + formatter.format(new Date(fecha)) + "\n"
                        + getResources().getString(R.string.etiqueta_tipo) + tipo + "\n"
                        + getResources().getString(R.string.etiqueta_duracion) + duracion + getResources().getString(R.string.segundos) + "\n"
                        + getResources().getString(R.string.etiqueta_geoCode) + geocode + "\n"
                        + getResources().getString(R.string.etiqueta_llamada) + tipoLlamada + "\n\n";

                //Agregamos el string al TextView y continuamos con el bucle
                mMainBinding.llamadasTextView.append(detalle);
            }
            //liberamos la memoria del cursor
            registros.close();
        }
    }

    @SuppressLint("StaticFieldLeak")
    @NonNull
    @Override
    public Loader<Cursor> onCreateLoader(int loaderId, @Nullable Bundle loaderArgs) {

        return new AsyncTaskLoader<Cursor>(this) {
            @Override
            protected void onStartLoading() {
                mMainBinding.llamadasTextView.setText("");
               mMainBinding.loadingIndicatorProgressbar.setVisibility(View.VISIBLE);
                forceLoad();
            }

            @Nullable
            @Override
            public Cursor loadInBackground() {
                return getContentResolver().query(
                        uriLlamada,
                        colContentResolver,
                        null,
                        null,
                        CallLog.Calls.DATE + " DESC");
            }
        };
    }

    @Override
    public void onLoadFinished(@NonNull Loader<Cursor> loader, Cursor cursor) {
        mMainBinding.loadingIndicatorProgressbar.setVisibility(View.INVISIBLE);
        setData(cursor);
    }

    @Override
    public void onLoaderReset(@NonNull Loader<Cursor> loader) {
    }
}