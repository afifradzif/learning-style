package afifrdzf.lsi.learningstyle;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
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

/**
 * Created by Afifrdzf on 11/27/2016.
 */

public class Nickname extends AppCompatActivity {

    private Button nextBtn;
    private EditText nickNameET;

    private String nickName;
    private String participantId;
    private String evaluationCode;
    private String quizList;
    private String criteria;

    private String jsonResult;
    private Handler handler;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.room);
        handler= new Handler(Looper.getMainLooper());
        Thread.currentThread().interrupt();

        nickNameET = (EditText)findViewById(R.id.editText);
        nextBtn = (Button)findViewById(R.id.button2);
        nextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressDiag();
            }
        });
    }

    public void progressDiag() {
        try {
            // Here you should write your time consuming task...
            RequestQueue queue = Volley.newRequestQueue(Nickname.this);
            String url = "http://192.168.1.80:3000/quiz-list";


            nickName = nickNameET.getText().toString();
            Intent p = getIntent();
            participantId = p.getExtras().getString("participantId");
            evaluationCode = p.getExtras().getString("evaluationCode");
            criteria = p.getExtras().getString("criteria");
            String json = "{evaluationCode:"+evaluationCode+",participantId:"+participantId+",nickname:"+nickName+"}";
            System.out.println(json);
            JSONObject jsonBody = new JSONObject(json);

            JsonObjectRequest jsonRequest = new JsonObjectRequest(Request.Method.POST, url,
                    jsonBody, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {

                    try{
                        Boolean verified = (Boolean) response.get("successful");
                        if(verified==true) {
                            jsonResult = response.toString();
                            System.out.println("Returned Json object " + jsonResult.toString());
                            quizList = response.get("quizList").toString();

                            Intent e = new Intent(Nickname.this, QuizUI.class);
                            e.putExtra("quizList", quizList);
                            e.putExtra("participantId", participantId);
                            e.putExtra("evaluationCode", evaluationCode);
                            e.putExtra("criteria",criteria);
                            startActivity(e);
                        }else{
                            Toast.makeText(Nickname.this,"Quiz Error",Toast.LENGTH_LONG).show();
                        }

                    }catch (JSONException e){
                        e.printStackTrace();
                    }catch (Exception e2){
                        e2.printStackTrace();
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
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
