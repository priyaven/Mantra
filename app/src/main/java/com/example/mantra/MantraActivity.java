package com.example.mantra;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class MantraActivity extends AppCompatActivity {

    String mantraTitle = "";
    String mantraText = "";
    int mantraCount = 0;
    int mantraPos = -1;

    public int incrementMantraCount(){
        this.mantraCount++;
        return this.mantraCount;
    }

    public void resetMantraCount(){
        this.mantraCount = 0;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mantra);

        Intent intent = getIntent();
        mantraTitle = intent.getStringExtra(MainActivity.MANTRATITLE);
        mantraText = intent.getStringExtra(MainActivity.MANTRATEXT);
        mantraCount = intent.getIntExtra(MainActivity.MANTRACOUNT,0);
        mantraPos = intent.getIntExtra(MainActivity.MANTRAPOS,-1);

        EditText textView = (EditText)findViewById(R.id.chanttext);
        textView.setText(mantraText);

        final EditText titleView = (EditText)findViewById(R.id.titletext);
        titleView.setText(mantraTitle);

        EditText countView = (EditText)findViewById(R.id.counttext);
        countView.setText(new Integer(mantraCount).toString());

        Button clickButton = (Button) findViewById(R.id.chantcount);
        clickButton.setOnClickListener( new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                int newMantraCount = incrementMantraCount();
                EditText countView = (EditText)findViewById(R.id.counttext);
                countView.setText(new Integer(newMantraCount).toString());
            }
        });

        Button resetButton = (Button)findViewById(R.id.resetbutton);
        resetButton.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resetMantraCount();
                EditText countView = (EditText)findViewById(R.id.counttext);
                countView.setText(new Integer(mantraCount).toString());
            }
        });

        Button cancelButton = (Button)findViewById(R.id.cancelbutton);
        cancelButton.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent myIntent = new Intent(MantraActivity.this,MainActivity.class);
                myIntent.putExtra("IsCancel",true);
                MantraActivity.this.startActivity(myIntent);
            }
        });

        Button saveButton = (Button)findViewById(R.id.savebutton);
        saveButton.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v){
                Intent myIntent = new Intent(MantraActivity.this, MainActivity.class);
                EditText textView = (EditText)findViewById(R.id.chanttext);
                EditText titlView = (EditText)findViewById(R.id.titletext);
                myIntent.putExtra("IsCancel",false);
                myIntent.putExtra("IsSave",true);
                myIntent.putExtra("newText","" + textView.getText());
                myIntent.putExtra("newCount",mantraCount);
                myIntent.putExtra("mantraPos",mantraPos);
                myIntent.putExtra("newTitle","" + titlView.getText());
                MantraActivity.this.startActivity(myIntent);
            }
        });

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_VOLUME_UP)){
            int newMantraCount = incrementMantraCount();
            EditText countView = (EditText)findViewById(R.id.counttext);
            countView.setText(new Integer(newMantraCount).toString());
        }
        return true;
    }
}
