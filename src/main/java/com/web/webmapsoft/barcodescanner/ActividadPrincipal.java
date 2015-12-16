package com.web.webmapsoft.barcodescanner;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.web.webmapsoft.barcodescanner.model.Recipiente;
import com.web.webmapsoft.barcodescanner.negocio.ConsultasWs;
import com.web.webmapsoft.barcodescanner.negocio.UsuarioBd;

/**
 * Created by mpalomar on 16/11/15.
 */
public class ActividadPrincipal extends Activity {

    private Button btnLlenado;
    private Button btnActTara;
    private Button btnActCodigo;
    private Button btnLogin;
    private Button btnCerrarApp;

    private EditText eUsuario;
    private EditText ePassword;


    //A ProgressDialog object
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(validaSiestaRegistrado()) {

            setContentView(R.layout.activity_principal);
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
            btnCerrarApp = (Button)findViewById(R.id.x_cerrarapp);

            btnLlenado = (Button) findViewById(R.id.x_llenado);
            btnActTara = (Button) findViewById(R.id.x_acttara);
            btnActCodigo = (Button) findViewById(R.id.x_actcodigo);
            btnLlenado.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    abrirFormaLLenado();

                }
            });
            btnActTara.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    abrirFormaActTara();
                }
            });
            btnActCodigo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    abrirFormaActualizaCodigo();
                }
            });
            btnCerrarApp.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    cerrarApp();
                }
            });
        }else{

            setContentView(R.layout.actividad_login);
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
            btnLogin = (Button) findViewById(R.id.x_botonlogin);
            ePassword = (EditText) findViewById(R.id.x_password);
            eUsuario  = (EditText) findViewById(R.id.x_usuario);


            btnLogin.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(eUsuario.getText().toString().isEmpty() || ePassword.getText().toString().isEmpty()){

                        Toast.makeText(getApplicationContext(),"Ingresar usuario y contraseña", Toast.LENGTH_SHORT).show();
                    }else
                    {
                        ConsultaWsTask consultaWsTask = new ConsultaWsTask();
                        String[] params = new String[2];
                        params[0] = eUsuario.getText().toString();
                        params[1] = ePassword.getText().toString();

                        consultaWsTask.execute(params);

                    }
                }
            });

        }
    }

    public void cerrarApp(){


        UsuarioBd bd = new UsuarioBd(getApplicationContext(),"bdsqlite",null,1);
        SQLiteDatabase db = bd.getWritableDatabase();
        db.delete("Usuarios",null,null);
        Toast.makeText(getApplicationContext(), "Cerrando App", Toast.LENGTH_SHORT).show();
        finish();
        System.runFinalization();
        System.exit(0);
        ActividadPrincipal.this.finish();
    }
    public Boolean validaSiestaRegistrado(){
        UsuarioBd bd = new UsuarioBd(this,"bdsqlite",null,1);
        SQLiteDatabase db = bd.getWritableDatabase();
        Cursor cuDatos = db.rawQuery("SELECT usuario,clave from usuarios",null);
        cuDatos.moveToFirst();
        int cantidad = cuDatos.getCount();
        if(cantidad == 0){
            return false;
        }
        else
            return true;


    }

    public void abrirFormaLLenado(){
        Intent intent = new Intent(this,ActividadLlenado.class);
        startActivity(intent);

    }

    public void abrirFormaActTara(){
        Intent intent = new Intent(this,ActividadActualizarTara.class);
        startActivity(intent);
    }

    public void abrirFormaActualizaCodigo(){
        Intent intent = new Intent(this,ActividadActualizarCodigoBar.class);
        startActivity(intent);
    }



    private class ConsultaWsTask extends AsyncTask<String,Void,String> {

        private String usuario ="";
        private String password= "";

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            //Create a new progress dialog
            progressDialog = new ProgressDialog(ActividadPrincipal.this);
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
            this.usuario = params[0];
            this.password= params[1];

            ConsultasWs consultasWs = new ConsultasWs(getApplicationContext());
            Recipiente recipiente = new Recipiente();
            consultasWs.setRecipiente(recipiente);
            consultasWs.setUsuario(this.usuario);
            consultasWs.setClave(this.password);
            String res = consultasWs.loginValidacion();

            return res;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            //close the progress dialog
            progressDialog.dismiss();
            if(s.equals("OK")){


                UsuarioBd bd = new UsuarioBd(getApplicationContext(),"bdsqlite",null,1);
                SQLiteDatabase db = bd.getWritableDatabase();
                ContentValues registro = new ContentValues();
                registro.put("usuario",this.usuario);
                registro.put("clave",this.password);
                db.insert("Usuarios",null,registro);

                Intent intent = getIntent();
                finish();
                startActivity(intent);
            }else {
                Toast.makeText(getApplicationContext(), s, Toast.LENGTH_SHORT).show();
            }


        }
    }

}
