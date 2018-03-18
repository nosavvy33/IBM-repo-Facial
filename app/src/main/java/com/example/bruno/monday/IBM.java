package com.example.bruno.monday;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import java.io.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

import android.app.*;
import android.content.*;
import android.net.*;
import android.os.*;
import android.util.Log;
import android.view.*;
import android.graphics.*;
import android.widget.*;
import android.provider.*;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.microsoft.projectoxford.face.*;
import com.microsoft.projectoxford.face.contract.*;
import com.microsoft.projectoxford.face.rest.ClientException;

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

public class IBM extends Activity {

    TextView txtpersona;
    TextView txtvarpersona;
    TextView txtporcentaje;
    TextView txtvarporcentaje;

    RequestQueue rqservs;
    private final String ENDPOINTCANDIDATE = "http://ec2-18-216-84-204.us-east-2.compute.amazonaws.com/recfacial/api/apiandroid/encontrarcandidato.php";
    private FaceServiceClient faceServiceClient =
            new FaceServiceRestClient("https://westcentralus.api.cognitive.microsoft.com/face/v1.0", "11131afade8d4760865c7db0715c87ee");
    private final int PICK_IMAGE = 1;
    private ProgressDialog detectionProgressDialog;
    public Face[] finalface = new Face[1];
    private String aula = "";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_microsoft);
        Button button1 = (Button) findViewById(R.id.button1);
        txtpersona = (TextView) findViewById(R.id.person);
        txtvarpersona = (TextView) findViewById(R.id.varnombre);
        txtporcentaje = (TextView) findViewById(R.id.porcentaje);
        txtvarporcentaje = (TextView) findViewById(R.id.varporcentaje);
        txtvarporcentaje.setVisibility(View.INVISIBLE);
        txtporcentaje.setVisibility(View.INVISIBLE);
        txtvarpersona.setVisibility(View.INVISIBLE);
        txtpersona.setVisibility(View.INVISIBLE);
        rqservs = Volley.newRequestQueue(this);

        aula = getIntent().getStringExtra("aula");
        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent gallIntent = new Intent(Intent.ACTION_GET_CONTENT);
                gallIntent.setType("image/*");
                startActivityForResult(Intent.createChooser(gallIntent, "Select Picture"), PICK_IMAGE);
            }
        });


        detectionProgressDialog = new ProgressDialog(this);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri uri = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
                ImageView imageView = (ImageView) findViewById(R.id.imageView1);
                imageView.setImageBitmap(bitmap);
                detectAndFrame(bitmap);
                //identifying(finalface);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void detectAndFrame(final Bitmap imageBitmap) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        imageBitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
        ByteArrayInputStream inputStream =
                new ByteArrayInputStream(outputStream.toByteArray());
        AsyncTask<InputStream, String, Face[]> detectTask =
                new AsyncTask<InputStream, String, Face[]>() {
                    @Override
                    protected Face[] doInBackground(InputStream... params) {
                        try {
                            publishProgress("Detectando...");
                            Face[] result = faceServiceClient.detect(
                                    params[0],
                                    true,         // returnFaceId
                                    false,        // returnFaceLandmarks
                                    null           // returnFaceAttributes: a string like "age, gender"
                            );
                            if (result == null) {
                                publishProgress("Ningún resultado");
                                return null;
                            }
                            publishProgress(
                                    String.format("Detection Finished. %d face(s) detected",
                                            result.length));
                            return result;
                        } catch (Exception e) {
                            publishProgress("Error de detección de rostro");
                            return null;
                        }
                    }

                    @Override
                    protected void onPreExecute() {

                        detectionProgressDialog.show();
                    }

                    @Override
                    protected void onProgressUpdate(String... progress) {

                        detectionProgressDialog.setMessage(progress[0]);
                    }

                    @Override
                    protected void onPostExecute(Face[] result) {
    try {
        detectionProgressDialog.dismiss();
        if (result == null) return;
        ImageView imageView = (ImageView) findViewById(R.id.imageView1);

        imageView.setImageBitmap(drawFaceRectanglesOnBitmap(imageBitmap, result));
        Log.e("FACEID EN POSTEXECUTE ", String.valueOf(result[0].faceId) + String.valueOf(result.length));
        finalface[0] = result[0];
        //ff(result);
        imageBitmap.recycle();
        identifying(result);
        //identifying(finalface);
    }catch(ArrayIndexOutOfBoundsException e){
        txtvarpersona.setVisibility(View.VISIBLE);
        txtvarpersona.setText("No se pudo detectar ningún rostro, por favor intente con otra foto");
    }
                    }
                };
        detectTask.execute(inputStream);
    }

    /*public Face [] ff (Face[] in) throws IOException, ClientException {
        Face [] k = in;
        Log.e("sos un pendejo ",String.valueOf(k[0].faceId));
        finalface[0]=k[0];
        UUID [] idface = new UUID [1];
        return  k;
    }*/

    private Bitmap drawFaceRectanglesOnBitmap(Bitmap originalBitmap, Face[] faces) {
        Bitmap bitmap = originalBitmap.copy(Bitmap.Config.ARGB_8888, true);
        Canvas canvas = new Canvas(bitmap);
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setStyle(Paint.Style.STROKE);
        paint.setColor(Color.RED);
        int stokeWidth = 2;
        paint.setStrokeWidth(stokeWidth);
        UUID[] idface = new UUID[1];
        if (faces != null) {
            for (Face face : faces) {
                idface[0] = face.faceId;
                Log.e("RESULTADO ", String.valueOf(face.faceId));
                FaceRectangle faceRectangle = face.faceRectangle;
                canvas.drawRect(
                        faceRectangle.left,
                        faceRectangle.top,
                        faceRectangle.left + faceRectangle.width,
                        faceRectangle.top + faceRectangle.height,
                        paint);
            }
        }
        return bitmap;
    }

    public void identifying(final Face[] faces)  {
        AsyncTask<String, String, IdentifyResult[]> atask = new AsyncTask<String, String, IdentifyResult[]>() {
            @Override
            public IdentifyResult[] doInBackground(String... strings) {
                try {
                    UUID[] idface = new UUID[1];
                    idface[0] = faces[0].faceId;
                    publishProgress("Detectando...");
                    IdentifyResult[] result = faceServiceClient.identity(
                            aula, idface, 1
                    );
                    if (result == null) {
                        publishProgress("Ningún resultado");
                        return null;
                    }
                    publishProgress(
                            String.format("Detection Finished. %d face(s) detected",
                                    result.length));
                    return result;
                } catch (Exception e) {
                    Log.e("error",e.getMessage());
                    publishProgress("Error de detección de rostro");
                    return null;
                }
            }

            @Override
            protected void onPreExecute() {

                detectionProgressDialog.show();
            }

            @Override
            protected void onProgressUpdate(String... progress) {

                detectionProgressDialog.setMessage(progress[0]);
            }

            @Override
            protected void onPostExecute(IdentifyResult[] result) {
                try {
                    if (result == null) {
                        Log.e("sry", "nadin");
                    } else {
                        detectionProgressDialog.dismiss();
                        List<Candidate> arr = new ArrayList<>();
                        Log.e("length de  ", String.valueOf(result.length));
                        for (IdentifyResult i : result) {
                            arr = i.candidates;
                            Log.e("IDENTITY RESULT", String.valueOf(i.faceId));
                        }
                        Log.e("CON CONFIANZA ", String.valueOf(arr.get(0).confidence) + "\t" + String.valueOf(arr.get(0).personId));
                        String conf = String.valueOf(arr.get(0).confidence);
                        findPerson(String.valueOf(arr.get(0).personId), conf);
                    }
                }catch (IndexOutOfBoundsException e){
                    txtpersona.setVisibility(View.VISIBLE);
                    txtpersona.setText("No existen resultados, intente con otra foto");
                    txtvarpersona.setText("");
                    txtporcentaje.setText("");
                    txtvarporcentaje.setText("");
                }
                }
        };
        atask.execute("");
    }

    public void findPerson(String personid, final String confianza){
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, ENDPOINTCANDIDATE + "?personid=" + personid, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    //Toast.makeText(getApplicationContext(),"Hola "+response.getString("nombre")+" "+response.getString("paterno"),Toast.LENGTH_LONG).show();
                    txtpersona.setVisibility(View.VISIBLE);
                    txtvarpersona.setVisibility(View.VISIBLE);
                txtvarpersona.setText(response.getString("nombre")+" "+response.getString("paterno"));
                    txtporcentaje.setVisibility(View.VISIBLE);
                    txtvarporcentaje.setText(confianza);
                    txtvarporcentaje.setVisibility(View.VISIBLE);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("error ",error.getMessage());
                Toast.makeText(getApplicationContext(),"Error de conexión: revise su conexión a Internet",Toast.LENGTH_SHORT).show();
            }
        });
        rqservs.add(jsonObjectRequest);
    }





}