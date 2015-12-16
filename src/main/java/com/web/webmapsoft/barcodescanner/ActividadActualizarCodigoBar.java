package com.web.webmapsoft.barcodescanner;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.web.webmapsoft.barcodescanner.model.Recipiente;
import com.web.webmapsoft.barcodescanner.negocio.ConsultasWs;
import com.web.webmapsoft.barcodescanner.ui.camera.AsyncResponseDatos;

import java.util.ArrayList;

/**
 * Created by mpalomar on 25/11/2015.
 */
public class ActividadActualizarCodigoBar extends Activity  implements AsyncResponseDatos {

    private Spinner capacidadCloro;
    private Button btnConsultar;
    private EditText numSerie;
    private EditText edCodigoBarras;
    private Button btActualizar;
    private EditText edCodigoBarrasCapturado;
    private Button btnScanearCodigoBarra;



    ConsultaWsTask consultaWsTask = new ConsultaWsTask();


    //A ProgressDialog object
    private ProgressDialog progressDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.actividad_actualizacodigobarra);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

        capacidadCloro = (Spinner) findViewById(R.id.x_capacidad_cloro);
        btnConsultar   = (Button) findViewById(R.id.x_consultar);
        numSerie       = (EditText) findViewById(R.id.x_numserie);
        edCodigoBarras = (EditText) findViewById(R.id.x_codigobarrasactual);
        btActualizar   = (Button) findViewById(R.id.x_actualizar);
        edCodigoBarrasCapturado = (EditText) findViewById(R.id.x_codigobarrascapturado);
        btnScanearCodigoBarra = (Button) findViewById(R.id.x_codigobarras);


        btnConsultar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnConsultarClick();
            }
        });

        btActualizar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnActualizaClick();
            }
        });

        btnScanearCodigoBarra.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnScanearClick();
            }
        });
        setSpinnerData();
    }

    public void btnScanearClick(){
        IntentIntegrator scanItegraIntegrator = new IntentIntegrator(this);
        scanItegraIntegrator.initiateScan();

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        IntentResult scanningResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (scanningResult != null) {
//we have a result
            String scanContent = scanningResult.getContents();
            String scanFormat = scanningResult.getFormatName();
            if (scanContent != null) {
                edCodigoBarrasCapturado.setText("P" + scanContent.toString());
            }
        }

    }

    public void btnActualizaClick(){
        if(!edCodigoBarras.getText().toString().isEmpty() || !edCodigoBarrasCapturado.getText().toString().isEmpty() || !edCodigoBarrasCapturado.getText().toString().equals("")){
            consultaWsTask = new ConsultaWsTask();
            consultaWsTask.delegate = this;
            String[] params = new String[3];
            params[0] = edCodigoBarras.getText().toString();
            params[1] = edCodigoBarrasCapturado.getText().toString();
            params[2] = "ACTUALIZAR";
            consultaWsTask.execute(params);

        }else{
            Toast.makeText(getApplicationContext(),"Seleccione el codigo de barras escaneando el nuevo codigo y consultando por numero de serie y capacidad", Toast.LENGTH_SHORT).show();

        }
    }
    public void btnConsultarClick(){
        if (capacidadCloro.getSelectedItem().toString().isEmpty() || numSerie.getText().toString().isEmpty()) {
            Toast.makeText(getApplicationContext(), "Por favor ingesar un numero de serie y seleccionar la capacidad del Cloro", Toast.LENGTH_SHORT).show();
        } else {
            //llamo ws
            consultaWsTask = new ConsultaWsTask();
            String[] params = new String[3];
            params[0]= numSerie.getText().toString();
            params[1] =capacidadCloro.getSelectedItem().toString();
            params[2] ="CONSULTASERIE";

            consultaWsTask.delegate = this;
            consultaWsTask.execute(params);
        }
    }
    public void setSpinnerData() {
        ArrayList<String> datos = new ArrayList<String>();
            datos.add("45");
            datos.add("60");
            datos.add("68");
            datos.add("907");
            datos.add("1000");

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                this, android.R.layout.simple_spinner_item,
                android.R.id.text1, datos);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        capacidadCloro.setAdapter(adapter);

    }
    @Override
    public void seteaDatos(Recipiente recipiente) {
        edCodigoBarras.setText(recipiente.getCodigoBarras());

    }
    private class ConsultaWsTask extends AsyncTask<String,Void,String> {
        public AsyncResponseDatos delegate=null;
        public Recipiente recipiente;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            //Create a new progress dialog
            progressDialog = new ProgressDialog(ActividadActualizarCodigoBar.this);
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
            String res = null;
            ConsultasWs consultasWs = new ConsultasWs(getApplicationContext());

            if(params[2].equals("CONSULTASERIE")) {
                res = consultasWs.consultaCodigoBarrasxNumSerie(params[0], params[1]);
                this.recipiente = new Recipiente();
                recipiente.setCodigoBarras(consultasWs.recipiente.getCodigoBarras());
            }

            if(params[2].equals("ACTUALIZAR")){
                res = consultasWs.actualizarCodigoBarras(params[0],params[1]);

            }

            return res;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            progressDialog.dismiss();
            if(s.equals("EXISTE")){
                Toast.makeText(getApplicationContext(),"Datos consultados con exito", Toast.LENGTH_SHORT).show();
                delegate.seteaDatos(this.recipiente);
            }else{
                Toast.makeText(getApplicationContext(),s, Toast.LENGTH_SHORT).show();
            }

        }
    }
}
