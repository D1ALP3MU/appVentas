package com.example.myapplication;

import static java.lang.Double.parseDouble;
import static java.lang.Integer.parseInt;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.Map;

public class sales extends AppCompatActivity {
    //Instanciar Firebase
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    EditText emailSale, dateSale, saleValue;
    int valorVentas;

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

        btnsaveSale.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveSale(emailSale.getText().toString(), dateSale.getText().toString(), saleValue.getText().toString());
            }
        });
    }

    private void saveSale(String sEmailSale, String sDateSale, String SsaleValue) {
        valorVentas = parseInt(saleValue.getText().toString());
        // Buscar la identificación del vendedor
        db.collection("seller")
                .whereEqualTo("Email", sEmailSale)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            if (!task.getResult().isEmpty()) { // Si lo encuentra el documento
                                if (valorVentas > 10000000){
                                    // Guardar los datos de la venta (sales)
                                    Map<String, Object> sales = new HashMap<>(); // Tabla cursor
                                    sales.put("Email", sEmailSale);
                                    sales.put("Date", sDateSale);
                                    sales.put("Salevalue", SsaleValue);

                                    db.collection("sales")
                                        .add(sales)
                                        .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                            @Override
                                            public void onSuccess(DocumentReference documentReference) {
                                                Toast.makeText(getApplicationContext(), "Venta agregada con éxito...", Toast.LENGTH_SHORT).show();

                                                //Limpiar las cajas de texto
                                                emailSale.setText("");
                                                dateSale.setText("");
                                                saleValue.setText("");
                                                emailSale.requestFocus(); //Enviar el foco al email

                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Toast.makeText(getApplicationContext(), "Error! la venta no se agregó...", Toast.LENGTH_SHORT).show();
                                            }
                                        });
                                } else {
                                    Toast.makeText(getApplicationContext(), "El valor de la venta debe ser superior a 10 millones", Toast.LENGTH_SHORT).show();
                                }

                            }
                            else
                            {
                                Toast.makeText(getApplicationContext(),"El Email del vendedor no existe, inténtelo nuevamente",Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                });
    }
}