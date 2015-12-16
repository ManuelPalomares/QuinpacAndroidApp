package com.web.webmapsoft.barcodescanner.negocio;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;

import com.web.webmapsoft.barcodescanner.model.Recipiente;
import com.web.webmapsoft.clientews.NLIBINDING_SIBTC;
import com.web.webmapsoft.clientews.NLIOperationResult;
import com.web.webmapsoft.clientews.NLIZFmPmRfcWssibtcResponse;
import com.web.webmapsoft.clientews.NLIZstPmDataSibtc;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by mpalomar on 19/11/2015.
 */
public class ConsultasWs {



    public Recipiente recipiente = new Recipiente();
    public String usuario;
    public String clave;
    public String mensaje;
    public String tipoMensaje;

    public ConsultasWs(Context context) {

        UsuarioBd bd = new UsuarioBd(context,"bdsqlite",null,1);
        SQLiteDatabase db = bd.getWritableDatabase();
        Cursor cuDatos = db.rawQuery("SELECT usuario,clave from usuarios",null);
        cuDatos.moveToFirst();
        int cantidad = cuDatos.getCount();
        if(cantidad > 0){
            this.usuario = cuDatos.getString(0);
            this.clave   = cuDatos.getString(1);

        }

    }

    public Recipiente getRecipiente() {
        return recipiente;
    }

    public void setRecipiente(Recipiente recipiente) {
        this.recipiente = recipiente;
    }

    public String getUsuario() {
        return usuario;
    }

    public void setUsuario(String usuario) {
        this.usuario = usuario;
    }

    public String getClave() {
        return clave;
    }

    public void setClave(String clave) {
        this.clave = clave;
    }

    public String  consultarByCodigoBarra(String codigo){
        NLIZstPmDataSibtc recipientews = new NLIZstPmDataSibtc();
        recipientews.FchNextPrueba = "2015-01-01";
        recipientews.FchPruHidro   =  "2015-01-01";
        recipientews.CodBarras = codigo;
        String mensajeWsSalida = "";
        recipiente = new Recipiente();
        NLIBINDING_SIBTC clienteWs = new NLIBINDING_SIBTC();

        try {
            NLIZFmPmRfcWssibtcResponse respuesta ;

            respuesta = clienteWs.ZFmPmRfcWssibtc(recipientews,"CONSULTAR_COBA",this.clave,this.usuario);
            String res = respuesta.EvTipoRespu;

            if(!(respuesta.CsDataSibtc.NumSerie == null)){

                recipiente.setSerie(respuesta.CsDataSibtc.NumSerie);
                recipiente.setTaraReal(respuesta.CsDataSibtc.TaraReal);
                recipiente.setTaraImpresa(respuesta.CsDataSibtc.TaraImpresa);

                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

                Date date = null;
                Date date2 =null;
                date = sdf.parse(respuesta.CsDataSibtc.FchPruHidro);
                date2 = sdf.parse(respuesta.CsDataSibtc.FchNextPrueba);

                recipiente.setFechaHidroStatica(date);
                recipiente.setFechaPrueba(date2);
                recipiente.setCapacidadCloro(respuesta.CsDataSibtc.CapaCloro);
                recipiente.setTipoRecipiente(respuesta.CsDataSibtc.TipoRecipi);
                mensajeWsSalida = "Datos consultados con exito";
                this.mensaje = mensajeWsSalida;
                this.tipoMensaje = "S";
                return mensajeWsSalida;

            }


            if(res.equals("W")){
                mensajeWsSalida =respuesta.EvRespuesta;
                this.tipoMensaje = "W";
                this.mensaje = mensajeWsSalida;
                return mensajeWsSalida;
            }


        } catch (Exception e) {
            mensajeWsSalida = e.getMessage();
            this.mensaje = mensajeWsSalida;
            this.tipoMensaje = "W";

            return mensajeWsSalida;
        }

        return mensajeWsSalida;
    }

    public String  actLlenado(String codigo,String estacion, String lote){
        NLIZstPmDataSibtc recipientews = new NLIZstPmDataSibtc();
        recipientews.FchNextPrueba = "2015-01-01";
        recipientews.FchPruHidro   =  "2015-01-01";
        recipientews.Estacion = estacion;
        recipientews.CodBarras = codigo;
        recipientews.Lote = lote;

        String mensajeWsSalida = "";
        recipiente = new Recipiente();
        NLIBINDING_SIBTC clienteWs = new NLIBINDING_SIBTC();

        try {
            NLIZFmPmRfcWssibtcResponse respuesta ;

            respuesta = clienteWs.ZFmPmRfcWssibtc(recipientews,"LLENA_RECI",this.clave,this.usuario);
            String res = respuesta.EvTipoRespu;

            if(res.equals("E")){
                mensajeWsSalida =respuesta.EvRespuesta;
                this.tipoMensaje = "W";
                this.mensaje = mensajeWsSalida;
                return mensajeWsSalida;
            }

            if(res.equals("W")){
                mensajeWsSalida =respuesta.EvRespuesta;
                this.tipoMensaje = "W";
                this.mensaje = mensajeWsSalida;
                return mensajeWsSalida;
            }


            if(!(respuesta.CsDataSibtc.NumSerie == null)){

                recipiente.setSerie(respuesta.CsDataSibtc.NumSerie);
                recipiente.setTaraReal(respuesta.CsDataSibtc.TaraReal);
                recipiente.setTaraImpresa(respuesta.CsDataSibtc.TaraImpresa);

                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

                Date date = null;
                Date date2 =null;
                date = sdf.parse(respuesta.CsDataSibtc.FchPruHidro);
                date2 = sdf.parse(respuesta.CsDataSibtc.FchPruHidro);

                recipiente.setFechaHidroStatica(date);
                recipiente.setFechaPrueba(date2);
                recipiente.setCapacidadCloro(respuesta.CsDataSibtc.CapaCloro);
                recipiente.setTipoRecipiente(respuesta.CsDataSibtc.TipoRecipi);
                mensajeWsSalida = "Registros actualizados correctamente";
                this.mensaje = mensajeWsSalida;
                this.tipoMensaje = "S";
                return mensajeWsSalida;

            }








        } catch (Exception e) {
            mensajeWsSalida = e.getMessage();
            this.mensaje = mensajeWsSalida;
            this.tipoMensaje = "W";

            return mensajeWsSalida;
        }

        return mensajeWsSalida;
    }

    public String  actTara(String codigo){
        NLIZstPmDataSibtc recipientews = new NLIZstPmDataSibtc();
        recipientews.FchNextPrueba = "2015-01-01";
        recipientews.FchPruHidro   =  "2015-01-01";
        recipientews.TaraNueva = this.recipiente.getTaraNueva();

        recipientews.CodBarras = codigo;
        String mensajeWsSalida = "";
        recipiente = new Recipiente();
        NLIBINDING_SIBTC clienteWs = new NLIBINDING_SIBTC();

        try {
            NLIZFmPmRfcWssibtcResponse respuesta ;

            respuesta = clienteWs.ZFmPmRfcWssibtc(recipientews,"ACTU_TARA",this.clave,this.usuario);
            String res = respuesta.EvTipoRespu;


            if (res == null){
                mensajeWsSalida =respuesta.EvRespuesta;
                this.tipoMensaje = "W";
                this.mensaje = mensajeWsSalida;
                return mensajeWsSalida;
            }

            if(res.equals("E")){
                mensajeWsSalida =respuesta.EvRespuesta;
                this.tipoMensaje = "W";
                this.mensaje = mensajeWsSalida;
                return mensajeWsSalida;
            }

            if(res.equals("W")){
                mensajeWsSalida =respuesta.EvRespuesta;
                this.tipoMensaje = "W";
                this.mensaje = mensajeWsSalida;
                return mensajeWsSalida;
            }


            if(res.equals("S")){
                mensajeWsSalida =respuesta.EvRespuesta;
                this.tipoMensaje = "W";
                this.mensaje = mensajeWsSalida;
                return mensajeWsSalida;
            }











        } catch (Exception e) {
            mensajeWsSalida = e.getMessage();
            this.mensaje = mensajeWsSalida;
            this.tipoMensaje = "W";

            return mensajeWsSalida;
        }

        return mensajeWsSalida;
    }

    public String  loginValidacion(){


        NLIZstPmDataSibtc recipientews = new NLIZstPmDataSibtc();
        recipientews.FchNextPrueba = "2015-01-01";
        recipientews.FchPruHidro   =  "2015-01-01";
        NLIBINDING_SIBTC clienteWs = new NLIBINDING_SIBTC();
        String mensajeRes = "";
        NLIZFmPmRfcWssibtcResponse respuesta ;

        try {
            respuesta = clienteWs.ZFmPmRfcWssibtc(recipientews,"LOGIN",this.clave,this.usuario);
            String res = respuesta.EvTipoRespu;

            if(res.equals("E")){
                mensajeRes = respuesta.EvRespuesta;
            }

            if(res.equals("S")){
                mensajeRes = "OK";
            }

            if(res.isEmpty()){
                mensajeRes = respuesta.EvRespuesta;
            }

        } catch (Exception e) {
            mensajeRes = e.getMessage();

        }


        return mensajeRes;
    }

    public String consultaCodigoBarrasxNumSerie(String numeroSerie, String capCloro){


        NLIZstPmDataSibtc recipientews = new NLIZstPmDataSibtc();
        recipientews.FchNextPrueba = "2015-01-01";
        recipientews.FchPruHidro   =  "2015-01-01";
        recipientews.NumSerie      = numeroSerie;
        recipientews.CapaCloro     = capCloro;
        String mensajeRes = "";

        NLIBINDING_SIBTC clienteWs = new NLIBINDING_SIBTC();
        NLIZFmPmRfcWssibtcResponse respuesta ;

        try {
            respuesta = clienteWs.ZFmPmRfcWssibtc(recipientews,"CONSULTAR_COBA",this.clave,this.usuario);
            String res = respuesta.EvTipoRespu;

            if(res != null){
                if (res.equals("W")){
                    mensajeRes= respuesta.EvRespuesta;
                    return mensajeRes;
                }
            }

            if (respuesta.CsDataSibtc.CodBarras != null){
                this.recipiente.setCodigoBarras(respuesta.CsDataSibtc.CodBarras);
                mensajeRes = "EXISTE";
                return mensajeRes;

            }

        } catch (Exception e) {
            mensajeRes = e.getMessage();
        }

        return mensajeRes;
    }

    public String actualizarCodigoBarras(String codigoBarras, String codigoBarrasNuevo){
        String mensajeRespuesta = null;
        NLIZstPmDataSibtc recipientews = new NLIZstPmDataSibtc();
        recipientews.CodBarras = codigoBarras;
        recipientews.NewCodBarras = codigoBarrasNuevo;

        NLIBINDING_SIBTC clienteWs = new NLIBINDING_SIBTC();
        NLIZFmPmRfcWssibtcResponse respuesta ;
        String res = null;
        try {
            respuesta = clienteWs.ZFmPmRfcWssibtc(recipientews,"CAMB_COBA",this.clave,this.usuario);
            res = respuesta.EvTipoRespu;
            if(res !=null){
                if(res.equals("S")){
                    mensajeRespuesta = "Se realizo la actualizacion correctamente";
                }
                else {
                    mensajeRespuesta = respuesta.EvRespuesta;
                }
            }

        } catch (Exception e) {
             mensajeRespuesta  = e.getMessage();
        }

        return mensajeRespuesta;
    }










}
