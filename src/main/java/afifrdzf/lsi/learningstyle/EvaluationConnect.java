package afifrdzf.lsi.learningstyle;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;

/**
 * Created by Afifrdzf on 11/26/2016.
 */

public class EvaluationConnect extends AppCompatActivity{

    private Button nextBtn;
    private EditText roomNum;

    public String participantId;
    public String evaluationCode;
    private Map<String,Integer> criteriaNameList;

    private String jsonResult;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.name);

        roomNum = (EditText)findViewById(R.id.editText2);
        nextBtn = (Button)findViewById(R.id.button3);
        nextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                launcedProgressDialog();
            }
        });

    }

    public void launcedProgressDialog() {

        final ProgressDialog ringProgressDialog = ProgressDialog.show(EvaluationConnect.this, "Please wait ...","Sending data ..", true);
        ringProgressDialog.setCancelable(true);
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {

                    // Here you should write your time consuming task...
                    RequestQueue queue = Volley.newRequestQueue(EvaluationConnect.this);
                    String url ="http://192.168.1.80:3000/verify-code";

                    evaluationCode = roomNum.getText().toString();
                    String json = "{evaluationCode:"+evaluationCode+"}";
                    System.out.println(json);
                    JSONObject jsonBody = new JSONObject(json);

                    JsonObjectRequest jsonRequest = new JsonObjectRequest(Request.Method.POST, url,
                            jsonBody, new Response.Listener<JSONObject>() {
                                @Override
                                public void onResponse(JSONObject response) {

                                    try{
                                        Boolean verified = (Boolean) response.get("verified");
                                        if(verified==true) {
                                            jsonResult = response.toString();
                                            System.out.println("Returned Json object " + jsonResult.toString());
                                            participantId = (String) response.get("participantId");
                                            String criteria = response.getJSONArray("criteriaNameList").toString();
                                            Intent i = new Intent(EvaluationConnect.this, Nickname.class);
                                            i.putExtra("participantId", participantId);
                                            i.putExtra("evaluationCode", evaluationCode);
                                            i.putExtra("criteria",criteria);
                                            System.out.println(criteria);
                                            startActivity(i);
                                        }else{
                                            AlertDialog.Builder builder = new AlertDialog.Builder(EvaluationConnect.this);
                                            builder.setMessage("Please check your Evaluation Code")
                                                    .setTitle("Problem");
                                            builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                                public void onClick(DialogInterface dialog, int id) {
                                                    // User clicked OK button
                                                }
                                            });
                                            AlertDialog dialog = builder.create();
                                            dialog.show();
                                            ringProgressDialog.dismiss();
                                        }
                                    }catch (JSONException e){
                                        e.printStackTrace();
                                    }
                                }
                            }, new Response.ErrorListener() {
                                @Override
                                public void onErrorResponse(VolleyError error) {
                                    Toast.makeText(EvaluationConnect.this, error.toString(), Toast.LENGTH_LONG).show();
                                    AlertDialog.Builder builder = new AlertDialog.Builder(EvaluationConnect.this);
                                    builder.setMessage("Please check your Evaluation Code")
                                            .setTitle("Problem");
                                    builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {
                                            // User clicked OK button

                                        }
                                    });
                                    AlertDialog dialog = builder.create();
                                    dialog.show();
                                    ringProgressDialog.dismiss();
                                }
                            });

                    queue.add(jsonRequest);
                    // Let the progress ring for 10 secods...
                    Thread.sleep(10000);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                ringProgressDialog.dismiss();
            }
        }).start();
    }
}
