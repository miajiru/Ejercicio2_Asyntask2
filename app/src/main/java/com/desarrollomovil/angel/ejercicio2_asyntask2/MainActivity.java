package com.desarrollomovil.angel.ejercicio2_asyntask2;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private Button botonDescarga;

    Handler manejador;//Handler

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        botonDescarga = (Button)findViewById(R.id.botonDescarga);
    }

    public void onClickBotonDescarga(View v) throws InterruptedException {
        TareaDescarga task = new TareaDescarga(); //Tarea asincrona
        botonDescarga.setText("Cancelar");//boton pulsado texto del boton en 'cancelar'
        task.execute(3000);//Tarea asincrona, enviamos tres segundos para poder dar la opcion de cancelar al usuario
    }

    private class TareaDescarga extends AsyncTask<Integer, Integer, String> {

        private ProgressDialog progreso;


        @Override
        protected void onPreExecute() {
            progreso = new ProgressDialog(MainActivity.this);
            progreso.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            progreso.setMessage("Descargando...");
            progreso.setCancelable(true);
            progreso.setMax(100);
            progreso.setProgress(0);//Valores del progressbar

            manejador = new Handler();//Manejador que nos servira para conectar con el hilo principal

            botonDescarga.setOnClickListener(new View.OnClickListener(){//Asociamos el evento para que el boton pulsado pueda cancelar opcion
                public void onClick(View v) {
                    cancel(true);
                }
            });
        }

        @Override
        protected String doInBackground(Integer...params) {
            try {
                Thread.currentThread().sleep(params[0]);//pausa para poder cancelar descarga
                manejador.post(new Runnable() {
                    @Override
                    public void run() {
                        progreso.show();//Comunicamos al hilo principal que muestre progressbar
                    }
                });
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            for(int i = 1; i <= 100; i++){//Comienza la descarga
                SystemClock.sleep(30);
                publishProgress(i+1);
                if(isCancelled()){//Comprobamos si se ha cancelado la descarga
                    progreso.cancel();//Cerramos el progressbar
                    break;
                }
            }
            return "Descarga Completada!!";
        }

        @Override
        protected void onCancelled() {
            Toast.makeText(MainActivity.this, "Descarga cancelada", Toast.LENGTH_LONG).show();//La descarga ha sido cancelada
            botonDescarga.setText("Descargar");

            botonDescarga.setOnClickListener(new View.OnClickListener(){//Asociamos de nuevo el evento de descarga
                public void onClick(View v) {
                    try {
                        onClickBotonDescarga(v);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            });
        }

        @Override
        protected void onProgressUpdate(Integer... porc){
            int prog = porc[0].intValue();
            progreso.setProgress(prog);//progressbar muestra la situación de la descarga
        }

        @Override
        protected void onPostExecute(String cad){
            progreso.dismiss();
            Toast.makeText(MainActivity.this,cad, Toast.LENGTH_SHORT).show();//Se recibe la cadena de 'Descarga completada'
            botonDescarga.setText("Descargar");//El boton vuelve al texto descarga

            botonDescarga.setOnClickListener(new View.OnClickListener(){//Asociamos de nuevo el evento de descarga
                public void onClick(View v) {
                    try {
                        onClickBotonDescarga(v);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            });
        }

    }
}
