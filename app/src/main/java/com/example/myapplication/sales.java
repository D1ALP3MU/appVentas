package com.example.myapplication;

import static android.content.ContentValues.TAG;
import static java.lang.Double.parseDouble;
import static java.lang.Integer.parseInt;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
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
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class sales extends AppCompatActivity {
    //Instanciar Firebase
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    EditText emailSale, dateSale, saleValue;
    int valorVentas, comision;
    String idSeller;

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
                if(!emailSale.getText().toString().isEmpty() && !dateSale.getText().toString().isEmpty() && !saleValue.getText().toString().isEmpty()){
                    saveSale(emailSale.getText().toString(), dateSale.getText().toString(), saleValue.getText().toString());
                } else {
                    Toast.makeText(getApplicationContext(), "Debe llenar los campos requeridos...", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void saveSale(String sEmailSale, String sDateSale, String SsaleValue) {
        valorVentas = parseInt(saleValue.getText().toString());
        // Buscar la identificación del vendedor
        db.collection("seller").whereEqualTo("Email", sEmailSale).get()
            .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    if (!task.getResult().isEmpty()) { // Si encuentra el email
                        // Condicional para validar el monto de la venta que sea superior a 10 millones
                        if (valorVentas > 10000000){
                            // Guardar los datos de la venta (sales)
                            Map<String, Object> sales = new HashMap<>(); // Tabla cursor
                            sales.put("Email", sEmailSale);
                            sales.put("Date", sDateSale);
                            sales.put("Salevalue", SsaleValue);

                            //Se calcula la comisión para llevarla al vendedor
                            comision = valorVentas * 2 / 100;

                            // para vendedores
                            db.collection("sales")
                                .add(sales)
                                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                    @Override
                                    public void onSuccess(DocumentReference documentReference) {
                                        // Llamo el método editCommision
                                        editCommision(sEmailSale,comision);
                                        Toast.makeText(getApplicationContext(), "Venta agregada con éxito..." + comision, Toast.LENGTH_SHORT).show();
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

    public void editCommision(String sEmailSale, int comision) {
        Map<String, Object> mseller = new HashMap<>();
        // extraer la commision
        db.collection("seller")
            .whereEqualTo("Email", sEmailSale)
            .get()
            .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            idSeller = document.getId();
                            Map<String, Object> seller = new HashMap<>();// Tabla cursor
                            seller = document.getData();

                            String mail = seller.get("Email").toString();
                            String phone = seller.get("Phone").toString();
                            String totalCommisionTemp =  seller.get("Total Commision").toString();
                            String name = seller.get("name").toString();

                            double totalCommisionVend = parseDouble(totalCommisionTemp);
                            double newCommision = comision + totalCommisionVend;
                            String val = String.valueOf(newCommision);

                            mseller.put("Email", mail);
                            mseller.put("Phone", phone);
                            mseller.put("Total Commision", val);
                            mseller.put("name", name);

                            // Llamo el método saveCommision
                            saveCommision(mseller,idSeller);
                        }

                    } else {
                        Log.d(TAG, "Error getting documents: ", task.getException());
                    }
                }
            });
    }

    public void saveCommision(Object s, String id){
        // actualizar la comision para guardarla en la colección seller
        db.collection("seller").document(id)
            .set(s)
            .addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    Toast.makeText(sales.this,"Commision actualizada correctamente...",Toast.LENGTH_SHORT).show();

                    // Vaciar las cajas de texto
                    emailSale.setText("");
                    dateSale.setText("");
                    saleValue.setText("");
                    emailSale.requestFocus(); //Enviar el foco al Email
                }
            })
            .addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(sales.this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }
    }