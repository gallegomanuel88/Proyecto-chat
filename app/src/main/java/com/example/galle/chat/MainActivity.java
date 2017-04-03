package com.example.galle.chat;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.drafts.Draft_17;
import org.java_websocket.handshake.ServerHandshake;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    WebSocketClient clienteWS;
    JSONObject jsonEnvio;
    JSONObject jsonRecepcion;
    String nombreUsuario = "";
    String mensaje = "";
    String idReceptor = "";
    String mensajeReceptor = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();
        //Llama al metodo nombreUsuario para introducir el nombre
        nombreUsuario();
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }
    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.nav_camera) {
            nombreUsuario();
            Toast.makeText(MainActivity.this, "Conexion"+nombreUsuario, Toast.LENGTH_SHORT).show();
        } else if (id == R.id.nav_gallery) {
        } else if (id == R.id.nav_slideshow) {
        } else if (id == R.id.nav_manage) {
        } else if (id == R.id.nav_share) {
        } else if (id == R.id.nav_send) {
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
    /**
     * Metodo para conectar con el servidor.
     * Llama al metodo enviarId y crea un hilo para la recepcion de mensajes.
     */
    private void connectWebSocket() {
        URI uri;
        try {
            uri = new URI("ws://servidor-android-gallegomanuel88.c9users.io:8081");
        } catch (URISyntaxException e) {
            e.printStackTrace();
            return;
        }
        Map<String, String> headers = new HashMap<>();
        clienteWS = new WebSocketClient(uri, new Draft_17(), headers, 0){
            @Override
            public void onOpen(ServerHandshake serverHandshake) {
                Log.i("Websocket", "Opened");
                try {
                    enviarId();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            @Override
            public void onMessage(final String s) {
                final String mensajeRecibidos = s;
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            TextView ListaMensajes = (TextView) findViewById(R.id.mensajesMostrados);
                            ListaMensajes.append(recibeMensaje(mensajeRecibidos) + "\n");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
            @Override
            public void onClose(int i, String s, boolean b) {
                Log.i("Websocket", "Closed " + s);
            }
            @Override
            public void onError(Exception e) {
                Log.i("Websocket", "Error " + e.getMessage());
            }
        };
        clienteWS.connect();
    }
    /**
     * Envia el usuario y el mensaje en la conexion.
     * @throws JSONException
     */
    public void enviarId () throws JSONException {
        jsonEnvio = new JSONObject();
        jsonEnvio.put("id", nombreUsuario);
        clienteWS.send(jsonEnvio.toString());
    }
    /**
     * Envia el usuario y el mensaje cada vez que se pulsa el boton enviar.
     * Cambia el valor del editText mensaje.
     * @param view
     * @throws JSONException
     */
    public void enviarMensaje(View view) throws JSONException {
        EditText valorCajonMensaje = (EditText)findViewById(R.id.mensajeEnviar);
        mensaje = valorCajonMensaje.getText().toString();
        jsonEnvio = new JSONObject();
        jsonEnvio.put("id", nombreUsuario);
        jsonEnvio.put("msg", mensaje);
        valorCajonMensaje.setText("");
        clienteWS.send(jsonEnvio.toString());
    }
    /**
     *
     * @param s
     * @return devuelve el mensaje a mostrar.
     * @throws JSONException
     */
    public String recibeMensaje(String s) throws JSONException {
        jsonRecepcion = new JSONObject(s);
        idReceptor = jsonRecepcion.getString("id");
        mensajeReceptor = jsonRecepcion.getString("msg");
        String mensaje = idReceptor + ": " + mensajeReceptor;
        return mensaje;
    }
    /**
     * Crea un AlertDialog en el que introducir el nombre de usuario.
     * Si no se escribe un nombre se da por defecto el valor "User Default" a la variable nombreUsuario.
     */
    public void nombreUsuario (){
        final AlertDialog.Builder alertaUsuario = new AlertDialog.Builder(this);
        alertaUsuario.setTitle("Nombre usuario");
        final EditText nombreUsuarioRecogido = new EditText(this);
        alertaUsuario.setView(nombreUsuarioRecogido);
        alertaUsuario.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if ((nombreUsuarioRecogido.getText().toString()).equals("")){
                    nombreUsuario = "User Default";
                }
                else {
                    nombreUsuario = nombreUsuarioRecogido.getText().toString();
                }
                //Llamada al metodo webSocket
                connectWebSocket();
                Toast.makeText(MainActivity.this, "Conectado como: "+nombreUsuario, Toast.LENGTH_SHORT).show();
            }
        });
        alertaUsuario.create();
        alertaUsuario.show();
    }
}