package com.web.webmapsoft.barcodescanner;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.web.webmapsoft.barcodescanner.model.Recipiente;
import com.web.webmapsoft.barcodescanner.negocio.ConsultasWs;
import com.web.webmapsoft.barcodescanner.ui.camera.AsyncResponseDatos;
import com.web.webmapsoft.clientews.NLIBINDING_SIBTC;
import com.web.webmapsoft.clientews.NLIZstPmDataSibtc;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by mpalomar on 17/11/2015.
 */
public class ActividadLlenado extends Activity implements AsyncResponseDatos{

    private Button bt_camara;
    private static final int RC_BARCODE_CAPTURE = 9001;
    private EditText eCodigoBarras;
    private static final String TAG = "Actividad Llenado";
    private EditText eLote;
    private Button bRllenado;
    private CheckBox chNumeroSerie;
    private CheckBox chTaraReal;
    private CheckBox chHidros;
    private Recipiente recipiente;
    private Spinner spEstacion;
    private EditText eSiguientePrueba;
    private EditText eSerie;
    private EditText eTaraReal;
    private EditText ePruebaHidro;
    private EditText eTaraimpresa;
    private EditText eCloro;
    private TextView tvAdvertenciaTxt;
    private ImageView ibAdvertenciaimg;

    ConsultaWsTask consultasWs = new ConsultaWsTask();

    //A ProgressDialog object
    private ProgressDialog progressDialog;

    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.actividad_llenado);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

        bt_camara = (Button) findViewById(R.id.x_codigobarras);
        eCodigoBarras = (EditText) findViewById(R.id.x_codigobarrascapturado);
        eLote = (EditText) findViewById(R.id.x_lote);
        bRllenado = (Button) findViewById(R.id.x_registrarllenado);
        chNumeroSerie = (CheckBox) findViewById(R.id.x_checknumeroserie);
        chTaraReal = (CheckBox) findViewById(R.id.x_chtarareal);
        chHidros = (CheckBox) findViewById(R.id.x_chhidro);
        tvAdvertenciaTxt = (TextView) findViewById(R.id.x_addvertenciatexto);
        ibAdvertenciaimg = (ImageView) findViewById(R.id.x_advertenciaimg);

        //campos
        eSerie = (EditText) findViewById(R.id.x_numserie);
        eTaraReal = (EditText) findViewById(R.id.x_tarareal);
        ePruebaHidro = (EditText) findViewById(R.id.x_pruebahidro);
        eTaraimpresa = (EditText) findViewById(R.id.x_taraimpresa);
        eCloro = (EditText) findViewById(R.id.x_cloro);
        spEstacion = (Spinner) findViewById(R.id.x_estacion);
        eSiguientePrueba = (EditText) findViewById(R.id.x_siguientePrueba);

        //manejo respuesta asyntask
        consultasWs.delegate = this;

        // Create Date object.
        Date date = new Date();
        // Specify the desired date format
        String DATE_FORMAT = "yy/MM/dd";
        // Create object of SimpleDateFormat and pass the desired date format.
        SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT);
        sdf.format(date);
        Calendar c = sdf.getCalendar();
        StringBuilder lote = new StringBuilder();
        lote.append(c.get(Calendar.MONTH) + 1);
        String ano = c.get(Calendar.YEAR) + "";
        ano = ano.substring(2);

        lote.append(ano);

        if (c.get(Calendar.DAY_OF_MONTH) + 1 > 15) {
            lote.append("2");
        } else {
            lote.append("1");
        }


        eLote.setText(lote.toString());

        bt_camara.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                llamarActividadCamara();
            }
        });

        bRllenado.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validarLlenado();
            }
        });

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
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

    public void validarLlenado() {
        if (chHidros.isChecked() & chTaraReal.isChecked() & chNumeroSerie.isChecked()) {
            if(eCodigoBarras.getText().toString().equals("")) {
                Context context = getApplicationContext();
                CharSequence text = "Por favor escanear un codigo de barra valido";
                int duration = Toast.LENGTH_SHORT;

                Toast toast = Toast.makeText(context, text, duration);
                toast.show();
            }else {
                String[] strParams = new String[4];
                strParams[0] = "LLENA_RECI";

                strParams[1] = eCodigoBarras.getText().toString();
                strParams[2] = spEstacion.getSelectedItem().toString();
                strParams[3] = eLote.getText().toString();

                this.recipiente.setLote(eLote.getText().toString());
                this.recipiente.setEstacion(spEstacion.getSelectedItem().toString());

                //llamado a la tarea del ws
                consultasWs = new ConsultaWsTask();
                consultasWs.recipiente.setEstacion(this.recipiente.getEstacion());
                consultasWs.recipiente.setLote(this.recipiente.getLote());

                consultasWs.delegate = this;
                consultasWs.execute(strParams);
            }

        } else {
            Context context = getApplicationContext();
            CharSequence text = "No es posible realizar el llenado realize el checkeo de informacion";
            int duration = Toast.LENGTH_SHORT;

            Toast toast = Toast.makeText(context, text, duration);
            toast.show();
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
                eCodigoBarras.setText("P"+scanContent.toString());
                traerRecipienteWsCodigoBarra("P"+scanContent.toString());

            }
        }

        /*if (requestCode == RC_BARCODE_CAPTURE) {
            if (resultCode == CommonStatusCodes.SUCCESS) {
                if (data != null) {
                    Barcode barcode = data.getParcelableExtra(BarcodeCaptureActivity.BarcodeObject);
                    //statusMessage.setText(R.string.barcode_success);
                    eCodigoBarras.setText(barcode.displayValue);
                    traerRecipienteWsCodigoBarra(barcode.displayValue.toString());


                    Log.d(TAG, "Barcode read: " + barcode.displayValue);
                } else {
                    //statusMessage.setText(R.string.barcode_failure);
                    Log.d(TAG, "No barcode captured, intent data is null");
                }
            } else {
                Log.d(TAG, String.format(getString(R.string.barcode_error),
                        CommonStatusCodes.getStatusCodeString(resultCode)));

            }
        }
        else {
            super.onActivityResult(requestCode, resultCode, data);
        }*/

    }

    private void traerRecipienteWsCodigoBarra(String codigoBarra) {


        String[] strParams = new String[2];
        strParams[0] = "CONSULTAR";
        strParams[1] = codigoBarra;//codigoBarra; //"P30201";//


        //llamado a la tarea del ws
        consultasWs = new  ConsultaWsTask();
        consultasWs.delegate = this;
        consultasWs.execute(strParams);





    }

    public void seteaDatosRecipiente(Recipiente recipiente){
        this.recipiente = recipiente;

        eSerie.setText(recipiente.getSerie());
        eCloro.setText(recipiente.getCapacidadCloro());
        eTaraimpresa.setText(recipiente.getTaraImpresa());
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        SimpleDateFormat sdf2 = new SimpleDateFormat("dd/MM/yyyy");

        ePruebaHidro.setText(sdf.format(recipiente.getFechaHidroStatica()));
        eSiguientePrueba.setText(sdf2.format(recipiente.getFechaPrueba()));

        eTaraReal.setText(recipiente.getTaraReal());
        setSpinnerData();
        validaFechaPrueba();
    }


    public void setSpinnerData() {
        ArrayList<String> datos = new ArrayList<String>();

        if (this.recipiente.getTipoRecipiente().equals("T") ) {
            datos.add("A");
            datos.add("B");
            datos.add("C");
        }
        if (this.recipiente.getTipoRecipiente().equals("C") ) {
            datos.add("A");
            datos.add("B");
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                this, android.R.layout.simple_spinner_item,
                android.R.id.text1, datos);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spEstacion.setAdapter(adapter);

    }

    public void validaFechaPrueba(){
        Date fechaActual = new Date();

        //validad si la fecha es menor si la fecha es menor se muestra advertencia
        if(recipiente.getFechaPrueba().before(fechaActual)){
            eSiguientePrueba.setTextColor(Color.RED);
            tvAdvertenciaTxt.setVisibility(View.VISIBLE);
            ibAdvertenciaimg.setVisibility(View.VISIBLE);
        }else
        {
            eSiguientePrueba.setTextColor(Color.BLACK);
            tvAdvertenciaTxt.setVisibility(View.INVISIBLE);
            ibAdvertenciaimg.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "ActividadLlenado Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app deep link URI is correct.
                Uri.parse("android-app://com.web.webmapsoft.barcodescanner/http/host/path")
        );
        AppIndex.AppIndexApi.start(client, viewAction);
    }

    @Override
    public void onStop() {
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "ActividadLlenado Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app deep link URI is correct.
                Uri.parse("android-app://com.web.webmapsoft.barcodescanner/http/host/path")
        );
        AppIndex.AppIndexApi.end(client, viewAction);
        client.disconnect();
    }

    @Override
    public void seteaDatos(Recipiente recipiente) {
        seteaDatosRecipiente(recipiente);
        consultasWs.cancel(true);
    }


    private class ConsultaWsTask extends AsyncTask<String,Void,String>{

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
            progressDialog = new ProgressDialog(ActividadLlenado.this);
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

            this.recipiente = new Recipiente();

            if(params[0].equals("CONSULTAR")) {
                this.mensajeConsulta = consultasWs.consultarByCodigoBarra(params[1]);
                this.recipiente = consultasWs.recipiente;
                this.mensajeConsulta = consultasWs.mensaje;
                this.tipomensaje = consultasWs.tipoMensaje;
            }

            if(params[0].equals("LLENA_RECI")){
                consultasWs.setRecipiente(this.recipiente);

                this.mensajeConsulta = consultasWs.actLlenado(params[1],params[2],params[3]);
                this.recipiente = consultasWs.recipiente;
                this.mensajeConsulta = consultasWs.mensaje;
                this.tipomensaje = consultasWs.tipoMensaje;
            }



            return null;

        }

        protected void onPostExecute(String result) {

            //close the progress dialog
            progressDialog.dismiss();

            Toast.makeText(getApplicationContext(), this.mensajeConsulta,Toast.LENGTH_SHORT).show();
            if(!tipomensaje.equals("W")) {
                delegate.seteaDatos(this.recipiente);
            }

        }


    }


}



