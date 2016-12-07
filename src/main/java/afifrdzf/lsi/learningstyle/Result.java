package afifrdzf.lsi.learningstyle;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;


/**
 * Created by Afifrdzf on 12/1/2016.
 */

public class Result extends AppCompatActivity {
    private String auditoryStr;
    private String visualStr;
    private String tactileStr;
    private String evaluationCode;
    private String participantId;
    private String nickName;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.result);

        TextView auditory = (TextView)findViewById(R.id.auditoryPoint);
        TextView visual = (TextView)findViewById(R.id.visualPoint);
        TextView tactile = (TextView)findViewById(R.id.tactilePoint);
        Button submitBtn = (Button)findViewById(R.id.submitBtn);

        Intent intent = getIntent();
        auditoryStr = intent.getExtras().getString("auditory");
        visualStr = intent.getExtras().getString("visual");
        tactileStr = intent.getExtras().getString("tactile");
        evaluationCode = intent.getExtras().getString("evaluationCode");
        participantId = intent.getExtras().getString("participantId");
        nickName = intent.getExtras().getString("nickName");

        auditory.setText(auditoryStr);
        visual.setText(visualStr);
        tactile.setText(tactileStr);
        submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                launcedProgressDialog();
                Toast.makeText(Result.this,"Berjaya", Toast.LENGTH_LONG).show();
            }
        });
    }

    public void launcedProgressDialog() {

        final ProgressDialog ringProgressDialog = ProgressDialog.show(Result.this, "Please wait ...","Sending data ..", true);
        ringProgressDialog.setCancelable(true);
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    // Here you should write your time consuming task...
                    RequestQueue queue = Volley.newRequestQueue(Result.this);
                    String url = "http://192.168.1.80:3000/save-result";

                    String json = "{evaluationCode:"+evaluationCode+",participantId:"+participantId+",result:{Visual:"+visualStr+",Auditory:"+auditoryStr+
                            ",Kinaesthatic:"+tactileStr+"}}";
                    System.out.println(json);
                    JSONObject jsonBody = new JSONObject(json);

                    JsonObjectRequest jsonRequest = new JsonObjectRequest(Request.Method.POST, url,
                            jsonBody, new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            try{
                                String result = response.toString();
                                System.out.println("Returned Json object " + result.toString());
                            }catch (Exception e){
                                e.printStackTrace();
                            }
                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            error.printStackTrace();
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
