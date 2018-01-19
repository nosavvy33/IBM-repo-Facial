package com.example.bruno.monday;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.JsonObject;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class Inicio extends AppCompatActivity {
    private String ENDPOINTSTREAM = "http://192.168.1.12/pasantia/reliable/api/apiandroid/ingresar.php";
    RequestQueue rqservs;

    private EditText user;
    private EditText pass;
    private Button login;

    private String usuario = "";
    private String contra = "";
    public Context context;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inicio);
        context = getApplicationContext();
        user = (EditText) findViewById(R.id.user);
        pass = (EditText) findViewById(R.id.pass);
        login = (Button) findViewById(R.id.login);
        rqservs = Volley.newRequestQueue(this);
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.e("sth","saassa"+"asa");
                usuario = String.valueOf(user.getText());
                contra = String.valueOf(pass.getText());
                Log.e("text",usuario+contra);
                if(usuario.equals("") || contra.equals("")){
                    Toast.makeText(getApplicationContext(),"Complete sus credenciales",Toast.LENGTH_SHORT).show();
                }else{
                    login(usuario,contra);
                }
            }
        });
    }

    public void login(String us, String co){
       JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, ENDPOINTSTREAM+"?usuario="+us+"&password="+co, null, new Response.Listener<JSONObject>() {
           @Override
           public void onResponse(JSONObject response) {
               try {
                   Log.e("msg",String.valueOf(response.getString("msg")));
                   if(String.valueOf(response.getString("msg")).equals("logged-in")){
                       Intent intent = new Intent(getApplicationContext(),Aulas.class);
                       startActivity(intent);
                   }else{
                       Toast.makeText(getApplicationContext(),"Credenciales inválidas, inténtelo de nuevo",Toast.LENGTH_SHORT).show();
                   }
               } catch (JSONException e) {
                   e.printStackTrace();
               }
           }
       }, new Response.ErrorListener() {
           @Override
           public void onErrorResponse(VolleyError error) {
               Toast.makeText(getApplicationContext(),"Error de conexión, revise su conexión a Internet",Toast.LENGTH_SHORT).show();

           }
       });
    rqservs.add(jsonObjectRequest);
    }





}
