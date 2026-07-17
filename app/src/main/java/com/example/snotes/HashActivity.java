package com.example.snotes;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


import androidx.activity.EdgeToEdge;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.room.Room;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;


public class HashActivity extends AppCompatActivity {

        private NoteDao NoteDao;

        private EditText UnHasher;
        private Button UnHashBtn;

        private Button exitbtn;

        private TextView HintText;

        private TextView HashName;

        private Button getinbtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activty_hash);
        
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        AppDatabase db = Room.databaseBuilder(getApplicationContext(),
                AppDatabase.class, "database-name")
                .fallbackToDestructiveMigration()
                .allowMainThreadQueries()
                .build();
        NoteDao = db.NoteDao();

        String ContentToEdit = getIntent().getStringExtra("contentToEdit");
        String idToEdit = getIntent().getStringExtra("idToEdit");

        HashName = findViewById(R.id.NazwaHash);

        if (idToEdit != null) {
            HashName.setText("Note: " + NoteDao.getNameById(Integer.parseInt(idToEdit)));
        }


        HintText = findViewById(R.id.HintText);
        
        if (idToEdit != null) {
            HintText.setText("Hint: " + NoteDao.getHintById(Integer.parseInt(idToEdit)));
        }

        if (ContentToEdit == null) {
            Toast.makeText(this, "Brak content", Toast.LENGTH_SHORT).show();
            return;
        }

        getinbtn = findViewById(R.id.getinbtn);

        getinbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);

                if (android.os.Build.VERSION.SDK_INT >= 26) {
                    vibrator.vibrate(
                            VibrationEffect.createOneShot(
                                    100,
                                    VibrationEffect.DEFAULT_AMPLITUDE
                            )
                    );
                }


                String editingid = getIntent().getStringExtra("idToEdit");

                Intent intent = new Intent(HashActivity.this, EditorActivity.class);
                intent.putExtra("contentToEdit", ContentToEdit);
                intent.putExtra("idToEdit", editingid);
                intent.putExtra("ForViewing", "true");
                startActivity(intent);

            }
        });


        exitbtn = findViewById(R.id.exitbuttioner);

        exitbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        UnHashBtn = findViewById(R.id.UnHashBtn);

        UnHasher = findViewById(R.id.UnHasher);

        UnHashBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);

                if (android.os.Build.VERSION.SDK_INT >= 26) {
                    vibrator.vibrate(
                            VibrationEffect.createOneShot(
                                    100,
                                    VibrationEffect.DEFAULT_AMPLITUDE
                            )
                    );
                }


                if (UnHasher.getText().toString().length() == 4) {



                    int UnHashing = 0;

                    if (UnHashing == 0) {

                        UnHashing = 1;

                        try {
                            MessageDigest md = MessageDigest.getInstance("SHA-256");
                            byte[] key = md.digest(UnHasher.getText().toString().getBytes(StandardCharsets.UTF_8));

                            SecretKeySpec secretKey = new SecretKeySpec(key, "AES");

                            Cipher cipher = Cipher.getInstance("AES");
                            cipher.init(Cipher.DECRYPT_MODE, secretKey);

                            String decrypted = new String(
                                    cipher.doFinal(Base64.decode(ContentToEdit, Base64.DEFAULT))
                            );

                            System.out.println(decrypted);


                            String editingid = getIntent().getStringExtra("idToEdit");

                            Intent intent = new Intent(HashActivity.this, EditorActivity.class);
                            intent.putExtra("contentToEdit", decrypted);
                            intent.putExtra("idToEdit", editingid);
                            intent.putExtra("ForViewing", "false");
                            startActivity(intent);
                            finish();

                        } catch (Exception e) {
                            e.printStackTrace();
                            Toast.makeText(HashActivity.this, "Error decrypting note", Toast.LENGTH_SHORT).show();
                        }

                    }
                }
            }
        });

    }


}
