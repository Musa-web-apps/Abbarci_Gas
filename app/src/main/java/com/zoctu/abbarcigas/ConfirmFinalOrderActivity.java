package com.zoctu.abbarcigas;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class ConfirmFinalOrderActivity extends AppCompatActivity {

    private EditText nameEditText,phoneEditText,addressEditText,cityEditText;
    private Button confirmOrderBtn;
    private String totalAmount="";

    @Override
    protected void onCreate(Bundle savedInstanceState)

    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirm_final_order);

        totalAmount=getIntent().getStringExtra("Total Price");
        Toast.makeText(this, "Total Price = Ushs " + totalAmount, Toast.LENGTH_LONG).show();

        confirmOrderBtn=findViewById(R.id.confirm_final_order_btn);
        nameEditText=findViewById(R.id.shipment_name);
        phoneEditText=findViewById(R.id.shipment_phone_number);
        addressEditText=findViewById(R.id.shipment_address);
        cityEditText=findViewById(R.id.shipment_city);

    }
}
