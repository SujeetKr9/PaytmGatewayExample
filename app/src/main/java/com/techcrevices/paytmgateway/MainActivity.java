package com.techcrevices.paytmgateway;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ((Button)findViewById(R.id.payBtn)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                EditText amountBtn = (EditText)findViewById(R.id.amountBtn);

                if (!amountBtn.getText().toString().equals("")){

                            //   getCheckSum(generateString(),generateString(),amountBtn.getText().toString(),mobileBtn.getText().toString(),messageBtn.getText().toString());
                            Intent intent = new Intent(MainActivity.this, PaymentActivity.class);
                            intent.putExtra("amount", amountBtn.getText().toString());
                            startActivity(intent);
                            finish();

                }else {
                    Toast.makeText(MainActivity.this,"Enter Amount",Toast.LENGTH_LONG).show();
                }

            }
        });

    }
}
