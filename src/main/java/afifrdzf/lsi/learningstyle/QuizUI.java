package afifrdzf.lsi.learningstyle;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by Afifrdzf on 11/29/2016.
 */

public class QuizUI extends AppCompatActivity {
    private TextView quizQuestion;
    private RadioGroup radioGroup;
    private RadioButton optionOne;
    private RadioButton optionTwo;
    private RadioButton optionThree;

    private int currentQuizQuestion = 0;
    private List<Integer> answer = new ArrayList();
    private int quizCount;
    private List<QuizWrapper> parsedObject;
    private QuizWrapper firstQuestion;

    private int auditoryPts = 0;
    private int visualPts = 0;
    private int tactilePts = 0;
    private String evaluationCode;
    private String participantId;
    private String criteria;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.quiz);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        quizQuestion = (TextView) findViewById(R.id.quiz_question);
        radioGroup = (RadioGroup) findViewById(R.id.radioGroup);
        optionOne = (RadioButton) findViewById(R.id.radio1);
        optionTwo = (RadioButton) findViewById(R.id.radio2);
        optionThree = (RadioButton) findViewById(R.id.radio3);
        Button previousButton = (Button) findViewById(R.id.previousquiz);
        Button nextButton = (Button) findViewById(R.id.nextquiz);

        AsyncObj asyncObject = new AsyncObj();
        asyncObject.execute("");

        new CountDownTimer(20000,1000){

            @Override
            public void onTick(long millisUntilFinished) {
                Toast.makeText(QuizUI.this,"seconds remaining "+millisUntilFinished/1000,Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFinish() {
                Toast.makeText(QuizUI.this,"done !",Toast.LENGTH_SHORT).show();
            }
        }.start();


        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int radioSelected = radioGroup.getCheckedRadioButtonId();
                int answerSelected = getSelectedAnswer(radioSelected);

                System.out.println("Current Quiz : "+currentQuizQuestion+" Quiz Count :"+quizCount);

                if(currentQuizQuestion < quizCount) {
                    if(answer.indexOf(currentQuizQuestion)==currentQuizQuestion){
                        answer.set(currentQuizQuestion, answerSelected);
                    } else {
                        answer.add(currentQuizQuestion, answerSelected);
                    }
                    if(currentQuizQuestion!=quizCount) {
                        currentQuizQuestion++;
                        System.out.println(answer);
                    }
                }if(currentQuizQuestion == quizCount){
                    AlertDialog.Builder builder = new AlertDialog.Builder(QuizUI.this);
                    builder.setMessage("Continue?")
                            .setTitle("Finish");
                    builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            // User clicked OK button
                           checkQuestion();
                            Intent intent = new Intent(QuizUI.this,Result.class);

                            String audPts = String.valueOf(auditoryPts);
                            String visPts = String.valueOf(visualPts);
                            String tacPts = String.valueOf(tactilePts);

                            intent.putExtra("auditory",audPts);
                            intent.putExtra("visual",visPts);
                            intent.putExtra("tactile",tacPts);
                            intent.putExtra("evaluationCode",evaluationCode);
                            intent.putExtra("participantId",participantId);
                            startActivity(intent);

                        }
                    });
                    builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            // User cancelled the dialog
                        }
                    });
                    AlertDialog dialog = builder.create();
                    dialog.show();
                }
                if(currentQuizQuestion<0){
                    return;
                }
                else {
                    uncheckedRadioButton();
                    try {
                        firstQuestion = parsedObject.get(currentQuizQuestion);
                        quizQuestion.setText(firstQuestion.getQuestion());
                        optionOne.setText(firstQuestion.getAnswer1());
                        optionTwo.setText(firstQuestion.getAnswer2());
                        optionThree.setText(firstQuestion.getAnswer3());
                    }catch (IndexOutOfBoundsException e){
                        return;
                    }
                }
            }
        });
        previousButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println("Current Quiz : "+currentQuizQuestion+" Quiz Count :"+quizCount);
                if(currentQuizQuestion>0) {
                    currentQuizQuestion--;
                }
                if(currentQuizQuestion<0){
                    return;
                }
                else {
                    if(answer.size()!=0||!answer.isEmpty()||currentQuizQuestion!=0) {
                        int ans = (int) answer.get(currentQuizQuestion);
                        checkRadioButton(ans);
                    }
                    firstQuestion = parsedObject.get(currentQuizQuestion);
                    quizQuestion.setText(firstQuestion.getQuestion());
                    optionOne.setText(firstQuestion.getAnswer1());
                    optionTwo.setText(firstQuestion.getAnswer2());
                    optionThree.setText(firstQuestion.getAnswer3());
                }
            }
        });
    }

    private class AsyncObj extends AsyncTask<String,Void,String>{

        private ProgressDialog progressDialog;

        @Override
        protected String doInBackground(String... params) {
            Intent p = getIntent();

            String jsonResult = p.getExtras().getString("quizList");
            evaluationCode = p.getExtras().getString("evaluationCode");
            participantId = p.getExtras().getString("participantId");
            criteria = p.getExtras().getString("criteria");

            System.out.println("Returned Json array " + jsonResult.toString());

            return jsonResult;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = ProgressDialog.show(QuizUI.this, "Downloading Quiz","Wait....", true);

        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            progressDialog.dismiss();
            System.out.println("Resulted Value: " + result);

            parsedObject = returnParsedJsonObject(result);
            if(parsedObject == null){
                return;
            }
            quizCount = parsedObject.size();
            firstQuestion = parsedObject.get(0);

            quizQuestion.setText(firstQuestion.getQuestion());
            optionOne.setText(firstQuestion.getAnswer1());
            optionTwo.setText(firstQuestion.getAnswer2());
            optionThree.setText(firstQuestion.getAnswer3());
        }
    }

    private List<QuizWrapper> returnParsedJsonObject(String result){

        List<QuizWrapper> jsonObject = new ArrayList<>();

        JSONArray jsonArray = null;

        QuizWrapper newItemObject;

        try {

            jsonArray = new JSONArray(result);
            List<List<String>> list = new ArrayList<>();


            for(int i = 0; i < jsonArray.length(); i++){

                try {
                    list.add(new ArrayList<String>());

                    JSONObject json = jsonArray.getJSONObject(i);
                    String question = json.getString("question");

                    JSONArray answerArray = json.getJSONArray("answerChoices");
                    list.get(i).add(0,question);
                    System.out.println("Question: "+list.get(i).get(0));

                    for(int j = 1;j <= answerArray.length(); j++){
                        JSONObject ansObj = answerArray.getJSONObject(j-1);
                        String answerQuestion = ansObj.getString("answer");
                        list.get(i).add(j,answerQuestion);
                        System.out.println("Answer :"+list.get(i).get(j));
                    }

                    String answerOptions1 = list.get(i).get(1);
                    String answerOptions2 = list.get(i).get(2);
                    String answerOptions3 = list.get(i).get(3);

                    newItemObject = new QuizWrapper(question, answerOptions1,answerOptions2,answerOptions3);

                    jsonObject.add(newItemObject);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return jsonObject;

    }

    private int getSelectedAnswer(int radioSelected){
        int answerSelected = 0;
        if(radioSelected == R.id.radio1){
            answerSelected = 1;
        }
        else if(radioSelected == R.id.radio2){
            answerSelected = 2;
        }
        else if(radioSelected == R.id.radio3){
            answerSelected = 3;
        }
        return answerSelected;
    }

    private void uncheckedRadioButton(){
        optionOne.setChecked(false);
        optionTwo.setChecked(false);
        optionThree.setChecked(false);
    }

    private void checkRadioButton(int answerSelected) {
        if (answerSelected == 1) {
            optionOne.setChecked(true);
        } else if (answerSelected == 2) {
            optionTwo.setChecked(true);
        } else if (answerSelected == 3) {
            optionThree.setChecked(true);
        }
        else
            uncheckedRadioButton();
    }

    private void checkQuestion(){

        Integer[] auditory = new Integer[] {1,4,7,10,13,16,19,22};
        List auditoryA = Arrays.asList(auditory);
        Integer[] visual = new Integer[] {2,5,8,11,14,17,20,23};
        List visualA = Arrays.asList(visual);
        Integer[] tactile = new Integer[] {3,6,9,12,15,18,21,24};
        List tactileA = Arrays.asList(tactile);
        ArrayList<String> listdata = new ArrayList<String>();

        try {
            JSONArray jsonArray = new JSONArray(criteria);
            if (jsonArray != null) {
                for (int i=0;i<jsonArray.length();i++){
                    listdata.add(jsonArray.get(i).toString());
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if(listdata.contains("Auditory")&&listdata.contains("Visual")||listdata.contains("Kinaesthatic")){
            for(int i = 0;i<answer.size();i++){
                int ans = answer.get(i);
                System.out.println("Return : "+answer+" answer : "+ans);

                if(auditoryA.contains(ans)){
                    auditoryPts+=5;
                }else if(visualA.contains(ans)){
                    visualPts+=5;
                }else if(tactileA.contains(ans)){
                    tactilePts+=5;
                }
            }
            System.out.println("Auditory : "+auditoryPts+" Visual : "+visualPts+" Kinaesthatic : "+tactilePts);
        }else{
            System.out.println("Check Error");
        }
    }
}
