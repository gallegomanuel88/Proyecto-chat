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

    JSONObject envioCliente;
    JSONObject recibidoServidor;

    String nombreUsuario = "manu";
    String mensaje = "Hola acabo de entrar";
    boolean privacidad = false;

    String id = "";
    String getMensaje = "";
    String dest = "";
    String checkBox = "";



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
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            //Llamada al metodo webSocket
            //connectWebSocket();
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
     * Metodo para conectar el webSocket en el servidor
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
                //clienteWS.send("Hello from " + Build.MANUFACTURER + " " + Build.MODEL);
            }

            @Override
            public void onMessage(final String s) {
                final String mensajeRecibidoS = s;
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            TextView ListaMensajes = (TextView) findViewById(R.id.messages);
                            ListaMensajes.append(recibeMensaje(mensajeRecibidoS) + "\n");
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
     * Envia el usuario, el mensaje y la privacidad.
     * @throws JSONException
     */
    public void enviarId () throws JSONException {
        envioCliente = new JSONObject();
        envioCliente.put("id", nombreUsuario);
        clienteWS.send(envioCliente.toString());
    }

    public void enviarMensaje(View view) throws JSONException {

        EditText editText = (EditText)findViewById(R.id.message);
        mensaje = editText.getText().toString();

        envioCliente = new JSONObject();
        envioCliente.put("id", nombreUsuario);
        envioCliente.put("msg", mensaje);
        envioCliente.put("privado", privacidad);
        envioCliente.put("dts", "ALL");

        editText.setText("");
        clienteWS.send(envioCliente.toString());

    }

    public String recibeMensaje(String s) throws JSONException {
        recibidoServidor = new JSONObject(s);
        id = recibidoServidor.getString("id");
        getMensaje = recibidoServidor.getString("msg");

        String mensaje = id + ": " + getMensaje;
        return mensaje;
    }

    /**
     * Crea un AlertDialog con el que introducir el nombre de usuario.
     * Si no se escribe un nombre se da por defecto el valor "User Default" a la variable nombreUsuario.
     */
    public void nombreUsuario (){
        final AlertDialog.Builder alertaUsuario = new AlertDialog.Builder(this);
        alertaUsuario.setTitle("Nombre usuario");

        final EditText nombreUsuario2 = new EditText(this);
        alertaUsuario.setView(nombreUsuario2);

        alertaUsuario.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if ((nombreUsuario2.getText().toString()).equals("")){
                    nombreUsuario = "User Default";
                    Toast.makeText(MainActivity.this, "Conectado como: "+nombreUsuario, Toast.LENGTH_SHORT).show();
                    connectWebSocket();
                }
                else {
                    nombreUsuario = nombreUsuario2.getText().toString();
                    Toast.makeText(MainActivity.this, "Conectado como: "+nombreUsuario, Toast.LENGTH_SHORT).show();
                    //Llamada al metodo webSocket
                    connectWebSocket();
                }

            }
        });
        alertaUsuario.create();
        alertaUsuario.show();
    }


}
