package com.example.snotes;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.util.Base64;
import android.util.Log;
import android.view.HapticFeedbackConstants;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;
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


public class SaveActivity extends AppCompatActivity {

    private Button rtrnbtn;
    private EditText NameInput;

    private EditText HintInput;

    private NoteDao NoteDao;

    private EditText HashInput;
    private Switch switchDel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        AppDatabase db = Room.databaseBuilder(getApplicationContext(),
                AppDatabase.class, "database-name").allowMainThreadQueries().build();
        NoteDao = db.NoteDao();



        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_save);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);





            return insets;
        });

        rtrnbtn = findViewById(R.id.rtrnbttn);
        rtrnbtn.setOnClickListener(new View.OnClickListener() {
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

                finish();

            }
        });

        NameInput = findViewById(R.id.NameInput);

        HintInput = findViewById(R.id.HintInput);

        String EditingIdent = getIntent().getStringExtra("EditingId");


        if (EditingIdent != null) {
            String EditingFile = NoteDao.getNameById(Integer.parseInt(EditingIdent));
            NameInput.setText(EditingFile);
        }

        HashInput = findViewById(R.id.hasher);

        switchDel = findViewById(R.id.switchDel);

        Button SaveBtn = findViewById(R.id.SaveBtn);



        SaveBtn.setOnClickListener(new View.OnClickListener() {
            int isSaved = 0;



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

                String currentTitle = NameInput.getText().toString().trim();
                String hash = HashInput.getText().toString();
                String EditingId = getIntent().getStringExtra("EditingId");


                // sprawdzanie nazwy
                if (currentTitle.isEmpty()) {
                    Toast.makeText(SaveActivity.this, "Name cannot be empty", Toast.LENGTH_SHORT).show();
                    return;
                }


                if (currentTitle.length() > 20) {
                    Toast.makeText(SaveActivity.this, "Name cannot be longer than 20 characters", Toast.LENGTH_SHORT).show();
                    return;
                }


                // sprawdzanie hasła
                if (hash.isEmpty()) {
                    Toast.makeText(SaveActivity.this, "Hash cannot be empty", Toast.LENGTH_SHORT).show();
                    return;
                }


                if (hash.length() != 4) {
                    Toast.makeText(SaveActivity.this, "Hash must be 4 digits", Toast.LENGTH_SHORT).show();
                    return;
                }


                // sprawdzanie czy istnieje taka nazwa
                Integer IdCheck = NoteDao.getIdByTitle(currentTitle);


                if (IdCheck != null) {

                    // jeżeli nie edytujemy albo znaleziony ID nie jest aktualnym ID
                    if (EditingId == null || IdCheck != Integer.parseInt(EditingId)) {

                        Toast.makeText(
                                SaveActivity.this,
                                "Note already exists",
                                Toast.LENGTH_SHORT
                        ).show();

                        return;
                    }
                }


                // szyfrowanie
                String encryptedContent = Hasher();


                String hint;

                if (HintInput.getText().toString().isEmpty()) {
                    hint = "No hint";
                } else {
                    hint = HintInput.getText().toString();
                }



                // EDYCJA
                if (EditingId != null) {

                    Note note = new Note(
                            currentTitle,
                            encryptedContent,
                            hint
                    );



                    if(switchDel.isChecked()) {
                        NoteDao.deleteById(
                                Integer.parseInt(EditingId)
                        );
                    }
                    else {



                            // jeżeli nie edytujemy albo znaleziony ID nie jest aktualnym ID
                        if (currentTitle.equals(NoteDao.getNameById(Integer.parseInt(EditingId)))) {

                                Toast.makeText(
                                        SaveActivity.this,
                                        "Note already exists",
                                        Toast.LENGTH_SHORT
                                ).show();

                                return;
                            }


                    }

                    NoteDao.insert(note);

                }

                // NOWA NOTATKA
                else {

                    Note note = new Note(
                            currentTitle,
                            encryptedContent,
                            hint
                    );

                    NoteDao.insert(note);

                }


                // powrót do MainActivity
                Intent intent = new Intent(
                        SaveActivity.this,
                        MainActivity.class
                );

                intent.setFlags(
                        Intent.FLAG_ACTIVITY_NEW_TASK |
                                Intent.FLAG_ACTIVITY_CLEAR_TASK
                );

                startActivity(intent);
                finish();
            }

            public String Hasher() {

                String content = getIntent().getStringExtra("content");
                String password = HashInput.getText().toString();

                try {
                    MessageDigest md = MessageDigest.getInstance("SHA-256");
                    byte[] key = md.digest(password.getBytes(StandardCharsets.UTF_8));

                    SecretKeySpec secretKey = new SecretKeySpec(key, "AES");

                    Cipher cipher = Cipher.getInstance("AES");
                    cipher.init(Cipher.ENCRYPT_MODE, secretKey);

                    String encryptedContent = Base64.encodeToString(
                            cipher.doFinal(content.getBytes()),
                            Base64.DEFAULT
                    );

                    System.out.println(encryptedContent);
                    return encryptedContent;
                } catch (Exception e) {
                    Log.e("SaveActivity", "Error in Hasher", e);
                }
                return null;
            }
            });





        }




    }


