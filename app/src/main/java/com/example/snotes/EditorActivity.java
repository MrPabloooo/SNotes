package com.example.snotes;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import org.w3c.dom.Text;

public class EditorActivity extends AppCompatActivity {

    private EditText EditorText;
    private Button exitbtn;

    private String editingid;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        if("true".equals(getIntent().getStringExtra("ForViewing"))) {

        }
        else {
            getOnBackPressedDispatcher().addCallback(this,
                    new OnBackPressedCallback(true) {
                        @Override
                        public void handleOnBackPressed() {

                            new AlertDialog.Builder(EditorActivity.this)
                                    .setTitle("Confirmation")
                                    .setMessage("Do you want to save changes?")
                                    .setPositiveButton("Yes", (dialog, which) -> {

                                        finish();
                                    })
                                    .setNegativeButton("No", (dialog, which) -> {
                                        dialog.dismiss();
                                    })
                                    .show();


                        }
                    });
        }


        String ContentToEdit = getIntent().getStringExtra("contentToEdit");

        editingid = getIntent().getStringExtra("idToEdit");


        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_editor);

        EditorText = findViewById(R.id.EditorTxt);



        if(ContentToEdit != null) {
            EditorText.setText(ContentToEdit);
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });



    exitbtn = findViewById(R.id.exitbuttion);

        if("true".equals(getIntent().getStringExtra("ForViewing"))) {
            EditorText.setEnabled(false);
            exitbtn.setVisibility(View.GONE);
        }


            exitbtn.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            if ("true".equals(getIntent().getStringExtra("ForViewing"))) {

            }
            else {
                Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);

                if (android.os.Build.VERSION.SDK_INT >= 26) {
                    vibrator.vibrate(
                            VibrationEffect.createOneShot(
                                    100,
                                    VibrationEffect.DEFAULT_AMPLITUDE
                            )
                    );
                }

                Intent intent = new Intent(EditorActivity.this, SaveActivity.class);


                intent.putExtra("content", EditorText.getText().toString());
                intent.putExtra("EditingId", editingid);


                startActivity(intent);
            }
        }
    });



}

}
