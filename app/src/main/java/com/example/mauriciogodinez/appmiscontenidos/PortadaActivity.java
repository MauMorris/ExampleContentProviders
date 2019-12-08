package com.example.mauriciogodinez.appmiscontenidos;

import android.Manifest;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

public class PortadaActivity extends AppCompatActivity {

    private static final String TAG = PortadaActivity.class.getSimpleName();

    //Valores en tiempo que tomará nuestra animacion
    private static final int UI_ANIMATION_SHOW_BARS_DELAY = 1500;
    private static final int NEXT_LAYOUT_DELAY_MILLIS = 150;

    //valores para permisos
    private int readCall, readContacts;

    //Handler para manejar los hilos de la animacion
    private final Handler mShowHandler = new Handler();

    //CV para modificar los parametros en su visualizacion
    private View mContentView;

    @TargetApi(Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_portada);

        mContentView = findViewById(R.id.fullscreen_activity_portada);

        //Aqui modificamos la vista inicial de la Activity para la animacion
        mContentView.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN);

        //Valores donde guarda el estatus de los permisos que obtenemos al preguntar al usuario
        readCall = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CALL_LOG);
        readContacts = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS);
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (readCall != PackageManager.PERMISSION_GRANTED ||
                readContacts != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_CALL_LOG,
                            Manifest.permission.READ_CONTACTS},
                    1);
        } else {
            //ejecutamos el metodo para la animacion
            delayedShow(UI_ANIMATION_SHOW_BARS_DELAY);
            Log.d(TAG, "ejecutamos la animacion");
        }
    }

    //Aqui preguntamos si tenemos los permisos de ejecución, en caso contrario pregunta en un cuadro de Dialogo
    //Hasta no obtener los permisos no podemos ejecutar la Activity
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (grantResults.length > 0 &&
                grantResults[0] != PackageManager.PERMISSION_GRANTED ||
                grantResults[1] != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_CALL_LOG,
                            Manifest.permission.READ_CONTACTS},
                    1);
        } else {
            //ejecutamos el metodo para la animacion
            delayedShow(UI_ANIMATION_SHOW_BARS_DELAY);
        }
    }

    //anticipa las acciones de otros hilos e inicia el metodo despues del tiempo indicado
    private void delayedShow(int delayMillis) {
        mShowHandler.removeCallbacks(mShowRunnable);
        mShowHandler.postDelayed(mShowRunnable, delayMillis);
    }

    //hilo para la animacion
    private final Runnable mShowRunnable = new Runnable() {
        @SuppressLint("InlinedApi")
        @Override
        public void run() {
            mContentView.setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);

            mShowHandler.removeCallbacks(mShowRunnable);
            //Despues del tiempo ejecuta la siguiente tarea
            mShowHandler.postDelayed(mIntentRunnable, NEXT_LAYOUT_DELAY_MILLIS);
        }
    };
    //Automaticamente pasa a la siguiente Activity
    private final Runnable mIntentRunnable = new Runnable() {
        @SuppressLint("InlinedApi")
        @Override
        public void run() {
            Intent main1 = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(main1);
            //termina la activity actual
            finish();
        }
    };
}