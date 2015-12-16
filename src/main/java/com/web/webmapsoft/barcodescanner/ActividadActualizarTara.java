package com.web.webmapsoft.barcodescanner;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.web.webmapsoft.barcodescanner.model.Recipiente;
import com.web.webmapsoft.barcodescanner.negocio.ConsultasWs;
import com.web.webmapsoft.barcodescanner.ui.camera.AsyncResponseDatos;

/**
 * Created by mpalomar on 21/11/2015.
 */
public class ActividadActualizarTara extends Activity  implements AsyncResponseDatos{


    private EditText etNumeroSerie; //x_numserie
    private EditText etTipo; //x_tarareal
    private EditText etCapacidadCloro; //x_capacidadCloro
    private EditText etTaraImpresa; //x_taraImpresa
    private EditText etTaraReal; //x_tarareal
    private EditText etTaraNueva; //x_taranueva
    private EditText etCodigoCapturado; //x_codigobarrascapturado
    private Button  btCodigoBarras;
    private Button  btActualizarTara;


    private Recipiente recipiente;
    private ImageView imRecipiente;

    //A ProgressDialog object
    private ProgressDialog progressDialog;

    ConsultaWsTask consultasWs = new ConsultaWsTask();



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.actualizar_tara);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        etNumeroSerie = (EditText) findViewById(R.id.x_numserie);
        etTipo        = (EditText) findViewById(R.id.x_tipoRecipiente);
        etCapacidadCloro = (EditText) findViewById(R.id.x_capacidadCloro);
        etTaraImpresa = (EditText) findViewById(R.id.x_taraImpresa);
        etTaraReal = (EditText) findViewById(R.id.x_tarareal);
        etTaraNueva   = (EditText) findViewById(R.id.x_taranueva);
        btCodigoBarras = (Button) findViewById(R.id.x_codigobarras);
        etCodigoCapturado =(EditText) findViewById(R.id.x_codigobarrascapturado);
        imRecipiente = (ImageView) findViewById(R.id.x_imagenrecipien);
        btActualizarTara = (Button) findViewById(R.id.x_actualizar);

        btCodigoBarras.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                llamarActividadCamara();
            }
        });

        btActualizarTara.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                actualizarTara();
            }
        });

        //manejo respuesta asyntask
        consultasWs.delegate = this;

    }

    public void llamarActividadCamara() {
        IntentIntegrator scanItegraIntegrator = new IntentIntegrator(this);
        scanItegraIntegrator.initiateScan();

        // launch barcode activity.
        /*Intent intent = new Intent(this, BarcodeCaptureActivity.class);

        intent.putExtra(BarcodeCaptureActivity.AutoFocus, false);
        intent.putExtra(BarcodeCaptureActivity.UseFlash, false);

        startActivityForResult(intent, RC_BARCODE_CAPTURE);
*/

    }

    public void actualizarTara(){
       if(etTaraNueva.getText().toString().equals("") || etTaraNueva.getText().toString().isEmpty()){
           Context context = getApplicationContext();
           CharSequence text = "No es posible actualizar el tara por favor ingresar un valor";
           int duration = Toast.LENGTH_SHORT;

           Toast toast = Toast.makeText(context, text, duration);
           toast.show();
       }else
       {
           //llamo ws
           if(etCodigoCapturado.getText().toString().isEmpty()){
               Context context = getApplicationContext();
               CharSequence text = "Por favor escanear un codigo de barra valido";
               int duration = Toast.LENGTH_SHORT;

               Toast toast = Toast.makeText(context, text, duration);
               toast.show();
           }else{
               String[] strParams = new String[2];
               strParams[0] = "ACTU_TARA";
               //strParams[1] ="P30668";
               strParams[1] = etCodigoCapturado.getText().toString();

               this.recipiente.setTaraNueva(etTaraNueva.getText().toString());


               //llamado a la tarea del ws
               consultasWs = new ConsultaWsTask();
               consultasWs.setRecipiente(this.recipiente);

               consultasWs.delegate = this;
               consultasWs.execute(strParams);
           }


       }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        IntentResult scanningResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (scanningResult != null) {
//we have a result
            String scanContent = scanningResult.getContents();
            String scanFormat = scanningResult.getFormatName();
            if (scanContent != null) {
                etCodigoCapturado.setText("P"+scanContent.toString());

                String[] strParams = new String[2];
                strParams[0] = "CONSULTAR";
                strParams[1] ="P"+scanContent.toString();

                //llamado a la tarea del ws
                consultasWs = new  ConsultaWsTask();
                consultasWs.delegate = this;
                consultasWs.execute(strParams);


            }
        }

    }

    private void traerRecipienteWsCodigoBarra(Recipiente recipiente) {
        this.recipiente = recipiente;

        etNumeroSerie.setText(recipiente.getSerie());
        etCapacidadCloro.setText(recipiente.getCapacidadCloro());
        etTaraImpresa.setText(recipiente.getTaraImpresa());
        etTaraReal.setText(recipiente.getTaraReal());
        if(recipiente.getTipoRecipiente().equals("T")){
            etTipo.setText("Tambor");

            if(recipiente.getTaraReal().equals("907 KG")){
                Drawable resTambor = getResources().getDrawable(R.drawable.tambor907);
                imRecipiente.setBackground(resTambor);
            }
            else{
                Drawable resTambor = getResources().getDrawable(R.drawable.tambor);
                imRecipiente.setBackground(resTambor);
            }

            if(recipiente.getTaraReal().equals("1000 KG")){
                Drawable resTambor = getResources().getDrawable(R.drawable.tambor1000);
                imRecipiente.setBackground(resTambor);
            }else{
                Drawable resTambor = getResources().getDrawable(R.drawable.tambor);
                imRecipiente.setBackground(resTambor);
            }


        }
        if(recipiente.getTipoRecipiente().equals("C")) {
            etTipo.setText("Cilindro");
            if(recipiente.getTaraReal().equals("68 KG")){
                Drawable resCilindro = getResources().getDrawable(R.drawable.cilindros6068);
                imRecipiente.setBackground(resCilindro);
            }else{
                Drawable resCilindro = getResources().getDrawable(R.drawable.cilindro);
                imRecipiente.setBackground(resCilindro);
            }

            if(recipiente.getTaraReal().equals("60 KG")){
                Drawable resCilindro = getResources().getDrawable(R.drawable.cilindros6068);
                imRecipiente.setBackground(resCilindro);

            }else{
                Drawable resCilindro = getResources().getDrawable(R.drawable.cilindro);
                imRecipiente.setBackground(resCilindro);
            }
        }

    }

    @Override
    public void seteaDatos(Recipiente recipiente) {
        traerRecipienteWsCodigoBarra(recipiente);
    }


    private class ConsultaWsTask extends AsyncTask<String,Void,String> {

        public Recipiente recipiente = new Recipiente();
        public String mensajeConsulta;
        public AsyncResponseDatos delegate=null;
        public String tipomensaje;

        public Recipiente getRecipiente() {
            return recipiente;
        }

        public void setRecipiente(Recipiente recipiente) {
            this.recipiente = recipiente;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            //Create a new progress dialog
            progressDialog = new ProgressDialog(ActividadActualizarTara.this);
            //Set the progress dialog to display a horizontal progress bar
            //progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            //Set the dialog title to 'Loading...'
            progressDialog.setTitle("Consultando...");
            //Set the dialog message to 'Loading application View, please wait...'
            progressDialog.setMessage("Consultando datos...");
            //This dialog can't be canceled by pressing the back key
            progressDialog.setCancelable(false);
            //This dialog isn't indeterminate
            progressDialog.setIndeterminate(false);
            //The maximum number of items is 100
            progressDialog.setMax(10);
            //Set the current progress to zero
            progressDialog.setProgress(0);
            //Display the progress dialog
            progressDialog.show();

        }

        @Override
        protected String doInBackground(String... params) {
            ConsultasWs consultasWs = new ConsultasWs(getApplicationContext());



            if(params[0].equals("CONSULTAR")) {
                this.recipiente = new Recipiente();
                this.mensajeConsulta = consultasWs.consultarByCodigoBarra(params[1]);
                this.recipiente = consultasWs.recipiente;
                this.mensajeConsulta = consultasWs.mensaje;
                this.tipomensaje = consultasWs.tipoMensaje;
            }

            if(params[0].equals("ACTU_TARA")){
                consultasWs.setRecipiente(this.recipiente);
                this.mensajeConsulta = consultasWs.actTara(params[1]);
                this.recipiente = consultasWs.recipiente;
                this.mensajeConsulta = consultasWs.mensaje;
                this.tipomensaje = consultasWs.tipoMensaje;
            }




            return null;

        }

        protected void onPostExecute(String result) {

            //close the progress dialog
            progressDialog.dismiss();

            Toast.makeText(getApplicationContext(), this.mensajeConsulta, Toast.LENGTH_SHORT).show();
            if(!tipomensaje.equals("W")) {
                delegate.seteaDatos(this.recipiente);
            }

        }


    }



}
