package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.firebase.firestore.FirebaseFirestore;

public class sales extends AppCompatActivity {
    //Instanciar Firebase
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    EditText emailSale, dateSale, saleValue;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sales);
        // Instanciar y refeenciar los IDs del archivo xml
        emailSale = (EditText) findViewById(R.id.etEmailSale);
        dateSale = (EditText) findViewById(R.id.etdateSale);
        saleValue = (EditText) findViewById(R.id.etsaleValue);
        Button btnsaveSale = findViewById(R.id.btnsaveSale);
        TextView linkSeller = findViewById(R.id.reglinkSeller);

        // Evento click del linkSeller
        linkSeller.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), MainActivity.class));
            }
        });
    }
}