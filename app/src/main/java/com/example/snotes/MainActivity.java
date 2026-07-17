package com.example.snotes;

import static android.util.Log.v;
import static java.sql.DriverManager.println;

import android.animation.Animator;
import android.animation.AnimatorInflater;
import android.animation.StateListAnimator;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.ColorStateList;
import com.google.android.material.R.attr;
import android.graphics.Color;
import android.graphics.drawable.Animatable;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.view.Gravity;
import android.view.HapticFeedbackConstants;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.room.Room;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.color.MaterialColors;

import java.util.List;


public class MainActivity extends AppCompatActivity {

    private Button addbtn;

    private int queing;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);





            return insets;
        });

        queing = 0;

        addbtn = findViewById(R.id.Addbutton);
        addbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                if(queing == 0) {
                    queing = 1;

                    Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);

                    if (android.os.Build.VERSION.SDK_INT >= 26) {
                        vibrator.vibrate(
                                VibrationEffect.createOneShot(
                                        100,
                                        VibrationEffect.DEFAULT_AMPLITUDE
                                )
                        );
                    }

                    new Handler().postDelayed(() -> {

                        startActivity(new Intent(MainActivity.this, EditorActivity.class));


                    }, 150);

                }




            }



        });








    }



    private void loadNotes() {

        AppDatabase db = Room.databaseBuilder(getApplicationContext(),
                AppDatabase.class, "database-name")
                .fallbackToDestructiveMigration()
                .allowMainThreadQueries()
                .build();
        NoteDao noteDao = db.NoteDao();

        LinearLayout resultcont = findViewById(R.id.ResultsCont);
        resultcont.removeAllViews();

        List<Note> allNotes = noteDao.getAll();

        int query = allNotes.size();

        if(query == 0) {
                TextView textView = new TextView(MainActivity.this);
            textView.setText("Nothing to show");
            textView.setPadding(0, 150, 0, 0);
            textView.setTextSize(36);
            textView.setGravity(android.view.Gravity.CENTER);
            resultcont.addView(textView);

        }

        else{

            for(int i = 0 ; i < allNotes.size(); i++) {

                Note note = allNotes.get(i);



                LinearLayout notecont = new LinearLayout(MainActivity.this);
                notecont.setOrientation(LinearLayout.HORIZONTAL);
                notecont.setPadding(0, 25, 0, 0);



                MaterialButton resultbtn = new MaterialButton(MainActivity.this);
                resultbtn.setText(note.title);
                resultbtn.setTag(note.id);



                resultbtn.setInsetTop(0);
                resultbtn.setInsetBottom(0);
                resultbtn.setHeight(200);
                resultbtn.setPadding(0, 0, 0, 0);

                LinearLayout.LayoutParams params =
                        new LinearLayout.LayoutParams(
                                0,                                      // width
                                LinearLayout.LayoutParams.MATCH_PARENT, // height
                                1f                                      // weight
                        );

                int margin = (int) (16 * getResources().getDisplayMetrics().density);

                params.setMargins(
                        25, // left
                        10, // top
                        0, // right
                        10  // bottom
                );
                resultbtn.setLayoutParams(params);


                resultbtn.setGravity(Gravity.CENTER);




                notecont.addView(resultbtn);


                StateListAnimator animator = AnimatorInflater.loadStateListAnimator(
                        this,
                        R.animator.buttonpressanim
                );




                resultbtn.setStateListAnimator(animator);


                resultbtn.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {

                        if(queing == 0)
                        {
                            queing = 1;
                            new Handler().postDelayed(() -> {

                                Note note = noteDao.getById(Integer.parseInt(resultbtn.getTag().toString()));

                                Intent intent = new Intent(MainActivity.this, HashActivity.class);
                                intent.putExtra("contentToEdit", note.content);
                                intent.putExtra("idToEdit", resultbtn.getTag().toString());
                                startActivity(intent);

                            }, 150);

                        }




                    }


                });



                MaterialButton deletebtn = new MaterialButton(MainActivity.this);
                deletebtn.setText("Delete");
                deletebtn.setTextSize(12);
                deletebtn.setTag(note.id);

                deletebtn.setBackgroundTintList(
                        ColorStateList.valueOf(
                                MaterialColors.getColor(
                                        deletebtn,
                                        android.R.attr.colorError
                                )
                        )
                );

                deletebtn.setInsetTop(0);
                deletebtn.setInsetBottom(0);
                deletebtn.setHeight(200);
                deletebtn.setPadding(0, 0, 0, 0);

                deletebtn.setGravity(Gravity.CENTER);

                LinearLayout.LayoutParams params2 =
                        new LinearLayout.LayoutParams(
                                200,                                // width
                                LinearLayout.LayoutParams.WRAP_CONTENT, // height
                                0f                                     // weight
                        );


                int margin2 = (int) (16 * getResources().getDisplayMetrics().density);

                params2.setMargins(
                        25, // left
                        10, // top
                        25, // right
                        10  // bottom
                );

                deletebtn.setLayoutParams(params2);
                notecont.addView(deletebtn);


                StateListAnimator animatorer = AnimatorInflater.loadStateListAnimator(
                        this,
                        R.animator.buttonpressanim
                );


                deletebtn.setStateListAnimator(animatorer);




                deletebtn.setOnClickListener(new View.OnClickListener() {
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

                        new Handler().postDelayed(() -> {

                            if (android.os.Build.VERSION.SDK_INT >= 26) {
                                vibrator.vibrate(
                                        VibrationEffect.createOneShot(
                                                250,
                                                VibrationEffect.DEFAULT_AMPLITUDE
                                        )
                                );
                            }

                        }, 150);



                        new AlertDialog.Builder(MainActivity.this)
                                .setTitle("Confirmation")
                                .setMessage("Do you want to delete this note?")
                                .setPositiveButton("Yes", (dialog, which) -> {

                                    noteDao.deleteById((int) deletebtn.getTag());
                                    loadNotes();
                                })
                                .setNegativeButton("No", (dialog, which) -> {
                                    dialog.dismiss();
                                })
                                .show();



                    }
                });

                resultcont.addView(notecont);

            }
        }

    }


    @Override
    protected void onResume() {
        super.onResume();
        queing = 0;
        loadNotes();
    }



}
