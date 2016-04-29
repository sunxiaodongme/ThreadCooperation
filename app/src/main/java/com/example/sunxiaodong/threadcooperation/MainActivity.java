package com.example.sunxiaodong.threadcooperation;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private Button mGoObjectCooperation;
    private Button mGoConditionCooperation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
    }

    private void initView() {
        mGoObjectCooperation = (Button) findViewById(R.id.go_object_cooperation);
        mGoObjectCooperation.setOnClickListener(this);
        mGoConditionCooperation = (Button) findViewById(R.id.go_condition_cooperation);
        mGoConditionCooperation.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.go_object_cooperation:
                goObjectCooperation();
                break;
            case R.id.go_condition_cooperation:
                goConditionCooperation();
                break;
        }
    }

    private void goObjectCooperation() {
        Intent intent = new Intent(this, ObjectCooperationActivity.class);
        startActivity(intent);
    }

    private void goConditionCooperation() {
        Intent intent = new Intent(this, ConditionCooperationActivity.class);
        startActivity(intent);
    }

}
