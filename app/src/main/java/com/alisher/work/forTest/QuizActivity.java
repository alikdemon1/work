package com.alisher.work.forTest;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.alisher.work.R;
import com.alisher.work.models.Question;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Yesmakhan on 19.03.2016.
 */
public class QuizActivity extends AppCompatActivity{

    String cat_id;
    List<Question> quesList;
    int score=0;
    int qid=0;
    Question currentQ;
    TextView txtQuestion;
    RadioButton rda, rdb, rdc, rdd;
    Button butNext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz);
        getAllQuestions();
        currentQ=quesList.get(qid);
        txtQuestion=(TextView)findViewById(R.id.textView1);
        rda=(RadioButton)findViewById(R.id.radio0);
        rdb=(RadioButton)findViewById(R.id.radio1);
        rdc=(RadioButton)findViewById(R.id.radio2);
        rdd=(RadioButton)findViewById(R.id.radio3);
        butNext=(Button)findViewById(R.id.button1);
        setQuestionView();
        butNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RadioGroup grp = (RadioGroup) findViewById(R.id.radioGroup1);
                RadioButton answer = (RadioButton) findViewById(grp.getCheckedRadioButtonId());
                Log.d("yourans", currentQ.getANSWER() + " " + answer.getText());
                if (currentQ.getANSWER().equals(answer.getText())) {
                    score++;
                    Log.d("score", "Your score" + score);
                }
                if (qid < quesList.size()) {
                    currentQ = quesList.get(qid);
                    setQuestionView();
                } else {
                    Intent intent = new Intent(QuizActivity.this, ResultActivity.class);
                    Bundle b = new Bundle();
                    b.putInt("score", score); //Your score
                    intent.putExtras(b); //Put your score to your next Intent
                    intent.putExtra("categoryId",cat_id);
                    startActivity(intent);
                    finish();
                }
            }
        });
    }

    private void setQuestionView() {
        txtQuestion.setText(currentQ.getQUESTION());
        rda.setText(currentQ.getOPTA());
        rdb.setText(currentQ.getOPTB());
        rdc.setText(currentQ.getOPTC());
        rdd.setText(currentQ.getOPTD());
        qid++;
    }

    private void getAllQuestions() {
        quesList = new ArrayList<>();
        cat_id = getIntent().getStringExtra("catId");
        ParseQuery<ParseObject> query = ParseQuery.getQuery("Question");
        query.whereEqualTo("catId", cat_id);
        try {
            List<ParseObject> questions = query.find();
            Log.d("SIZE OF QUESIONS", questions.size()+"");
            for (ParseObject q : questions) {
                Question questionItem = new Question();
                questionItem.setID(q.getObjectId());
                questionItem.setOPTA(q.getString("option1"));
                questionItem.setOPTB(q.getString("option2"));
                questionItem.setOPTC(q.getString("option3"));
                questionItem.setOPTD(q.getString("option4"));
                questionItem.setQUESTION(q.getString("question"));
                questionItem.setANSWER(q.getString("correct"));
                quesList.add(questionItem);
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }
}
