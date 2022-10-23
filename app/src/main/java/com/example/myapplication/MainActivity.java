package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
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
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    // Instanciar Firebase
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    String idSeller; // Variable que contendrá el id de cada cliente

    EditText email, name, phone;
    int comision = 0;
    EditText totalCommision;


    @SuppressLint({"MissingInflatedId", "LocalSuppress"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // Instanciar y refenciar los IDs del archivo xml
        email = (EditText) findViewById(R.id.etEmail);
        name = (EditText) findViewById(R.id.etname);
        phone = (EditText) findViewById(R.id.etPhone);
        totalCommision = (EditText) findViewById(R.id.etTotalCommision);
        Button btnSaveSeller = findViewById(R.id.btnsave);
        Button btnSearchSeller = findViewById(R.id.btnsearch);
        Button btnEditSeller = findViewById(R.id.btnedit);
        Button btnDeleteSeller = findViewById(R.id.btndelete);
        TextView reglinkSales = findViewById(R.id.reglinkVentas);

        totalCommision.setFocusable(false);

        reglinkSales.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), sales.class));
            }
        });

        btnSaveSeller.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveSeller(email.getText().toString(), name.getText().toString(), phone.getText().toString(), totalCommision.getText().toString());
            }
        });
        
        btnSearchSeller.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchSeller(email.getText().toString());
            }
        });

        btnEditSeller.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editSeller(email.getText().toString(), name.getText().toString(), phone.getText().toString(), totalCommision.getText().toString());
            }
        });

        btnDeleteSeller.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Confirmación de borrado
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(MainActivity.this);
                alertDialogBuilder.setMessage("¿ Está seguro de eliminar el vendedor con Email: " + email.getText().toString() + " ?");
                alertDialogBuilder.setPositiveButton("Sí",
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface arg0, int arg1) {
                            // Validar si el vendedor tiene ventas registradas
                            db.collection("sales")
                                .whereEqualTo("Email", email.getText().toString())
                                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                        if (task.isSuccessful()) {
                                            if (!task.getResult().isEmpty()) { // Si encontró el documento
                                                Toast.makeText(getApplicationContext(),"No es posible eliminar el vendedor...",Toast.LENGTH_SHORT).show();

                                            } else {
                                                // Se eliminará el vendedor con el email respectivo
                                                db.collection("seller").document(idSeller)
                                                    .delete()
                                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                        @Override
                                                        public void onSuccess(Void aVoid) {
                                                            Toast.makeText(MainActivity.this,"Vendedor borrado correctamente...",Toast.LENGTH_SHORT).show();

                                                            //Limpiar las cajas de texto
                                                            email.setText("");
                                                            name.setText("");
                                                            phone.setText("");
                                                            email.requestFocus(); //Enviar el foco al ident
                                                        }
                                                    })
                                                    .addOnFailureListener(new OnFailureListener() {
                                                        @Override
                                                        public void onFailure(@NonNull Exception e) {
                                                            Toast.makeText(getApplicationContext(),"Error: " + e.getMessage(),Toast.LENGTH_SHORT).show();
                                                        }
                                                    });
                                            }
                                        }
                                    }
                                });
                        }
                    });
                alertDialogBuilder.setNegativeButton("No",
                    new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface arg0, int arg1) {

                        }
                    });
                AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.show();
            }
        });
    }

    private void editSeller(String sEmail, String sName, String sPhone, String sTotalCommision) {

        if(sTotalCommision.isEmpty()){
            sTotalCommision = "";
        } else{
            sTotalCommision = totalCommision.getText().toString();
        }

        Map<String, Object> mseller = new HashMap<>();
        mseller.put("Email", sEmail);
        mseller.put("name", sName);
        mseller.put("Phone", sPhone);
        mseller.put("Total Commision", sTotalCommision);

        db.collection("seller").document(idSeller)
            .set(mseller)
            .addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    Toast.makeText(MainActivity.this,"Vendedor actualizado correctamente...",Toast.LENGTH_SHORT).show();

                    // Vaciar las cajas de texto
                    email.setText("");
                    name.setText("");
                    phone.setText("");
                    totalCommision.setText("");
                    email.requestFocus(); //Enviar el foco al Email
                }
            })
            .addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(MainActivity.this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
    }

    private void searchSeller(String sEmail) {
        db.collection("seller")
            .whereEqualTo("Email", sEmail)
            .get()
            .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if (task.isSuccessful()) {
                        if (!task.getResult().isEmpty()) { // Si encontró el documento
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                idSeller = document.getId();
                                Toast.makeText(getApplicationContext(),"ID seller: " + idSeller, Toast.LENGTH_LONG).show();
                                email.setText(document.getString("Email"));
                                name.setText(document.getString("name"));
                                phone.setText(document.getString("Phone"));
                                totalCommision.setText(document.getString("Total Commision"));
                            }
                        }
                        else {
                            Toast.makeText(getApplicationContext(),"El Email del vendedor no existe...",Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            });
    }

    private void saveSeller(String sEmail, String sName, String sPhone, String sTotalCommision) {
        // Buscar la identificación del vendedor nuevo
        db.collection("seller")
            .whereEqualTo("Email", sEmail)
            .get()
            .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if (task.isSuccessful()) {
                        if (task.getResult().isEmpty()) { // Si no encuentra el documento
                            //sTotalCommision.setText("0");
                            // Guardar los datos del vendedor (seller)
                            Map<String, String> seller = new HashMap<>(); // Tabla cursor
                            seller.put("Email", sEmail);
                            seller.put("name", sName);
                            seller.put("Phone", sPhone);
                            String var = "0";
                            seller.put("Total Commision", var);

                            db.collection("seller")
                                .add(seller)
                                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                    @Override
                                    public void onSuccess(DocumentReference documentReference) {
                                        Toast.makeText(getApplicationContext(), "Vendedor agregado con éxito...", Toast.LENGTH_SHORT).show();

                                        //Limpiar las cajas de texto
                                        email.setText("");
                                        name.setText("");
                                        phone.setText("");
                                        email.requestFocus(); //Enviar el foco al email

                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(getApplicationContext(), "Error! el vendedor no se agregó...", Toast.LENGTH_SHORT).show();
                                    }
                                });
                        }
                        else {
                            Toast.makeText(getApplicationContext(),"El Email del vendedor ya existe, inténtelo con otro",Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            });
    }
}