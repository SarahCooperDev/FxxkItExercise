package com.example.fxxkit

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar

class MainActivity : AppCompatActivity() {


    private lateinit var showExercisesBtn : FloatingActionButton
    private lateinit var createWorkoutBtn : FloatingActionButton
    private lateinit var addExerciseBtn : FloatingActionButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if(savedInstanceState == null){
            supportFragmentManager.beginTransaction()
                .add(R.id.main_fragment_view, WorkoutFragment.newInstance(), "workout")
                .commit()
        }

        showExercisesBtn = findViewById<FloatingActionButton>(R.id.show_exercises_btn)
        createWorkoutBtn = findViewById<FloatingActionButton>(R.id.create_workout_btn)
        addExerciseBtn = findViewById<FloatingActionButton>(R.id.add_exercise_btn)

        showExercisesBtn.setOnClickListener { view ->
            Snackbar.make(view, "Show exercise button clicked", Snackbar.LENGTH_SHORT)
                .setAction("Action", null).show()

            supportFragmentManager.beginTransaction()
                .replace(R.id.main_fragment_view, ExerciseListFragment.newInstance(), "exerciselist")
                .commit()
        }

        createWorkoutBtn.setOnClickListener { view ->
            Snackbar.make(view, "Create workout button clicked", Snackbar.LENGTH_SHORT)
                .setAction("Action", null).show()

            supportFragmentManager.beginTransaction()
                .replace(R.id.main_fragment_view, CreateWorkoutFragment.newInstance(), "createworkout")
                .commit()
        }

        addExerciseBtn.setOnClickListener { view ->
            Snackbar.make(view, "Add exercise button clicked", Snackbar.LENGTH_SHORT)
                .setAction("Action", null).show()

            supportFragmentManager.beginTransaction()
                .replace(R.id.main_fragment_view, AddExerciseFragment.newInstance(), "addexercise")
                .commit()
        }

    }
}