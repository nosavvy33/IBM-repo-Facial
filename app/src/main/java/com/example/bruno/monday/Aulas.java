package com.example.bruno.monday;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import com.example.bruno.monday.Aula;
import com.google.gson.JsonObject;

public class Aulas extends AppCompatActivity {

    private Button continuar;
    private Spinner spinner = null;
    RequestQueue rqservs;
    private String aulaselected ="";

    private final String ENDPOINT= "http://192.168.1.12/pasantia/reliable/api/apiandroid/aulas.php";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_aulas);
        rqservs = Volley.newRequestQueue(this);
        spinner = (Spinner)findViewById(R.id.spinner);
        continuar = (Button)findViewById(R.id.continuar);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                aulaselected = spinner.getSelectedItem().toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        aulasItems();

        continuar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(aulaselected.equals("")){
                    Toast.makeText(getApplicationContext(),"Seleccione un aula",Toast.LENGTH_SHORT).show();
                }else{
                    Log.e("aula ",aulaselected);
                    choosePhoto(aulaselected);
                }
            }
        });


    }

    public void choosePhoto(String aula){
        Intent intent = new Intent(getApplicationContext(),Microsoft.class);
        intent.putExtra("aula",aula);
        startActivity(intent);
    }




    public void llenarSpinner(ArrayList lista, Spinner spin) {

        final ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(
                this, R.layout.spinner_item, lista);

        spinnerArrayAdapter.setDropDownViewResource(R.layout.spinner_item);
        spin.setAdapter(spinnerArrayAdapter);
    }

    public void aulasItems(){
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, ENDPOINT, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    int x = response.length()-1;
                    ArrayList<String> aulas = new ArrayList<>();
                    while(x >= 0){
                        JSONObject js = response.getJSONObject(String.valueOf(x));
                        aulas.add(js.getString("nombre"));
                        x--;
                    }
                    llenarSpinner(aulas,spinner);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        rqservs.add(jsonObjectRequest);
    }

}
