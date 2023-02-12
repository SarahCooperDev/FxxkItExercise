package com.example.fxxkit

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.fragment.app.FragmentManager
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar

class MainActivity : AppCompatActivity() {

    private lateinit var showWorkoutListBtn: FloatingActionButton
    private lateinit var showExerciseListBtn : FloatingActionButton
    private lateinit var createWorkoutBtn : FloatingActionButton
    private lateinit var addExerciseBtn : FloatingActionButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if(savedInstanceState == null){
            supportFragmentManager.beginTransaction()
                .add(R.id.main_fragment_view, WorkoutFragment.newInstance(), "workoutlist")
                .commit()
        }

        showWorkoutListBtn = findViewById<FloatingActionButton>(R.id.show_workout_list_btn)
        showWorkoutListBtn.visibility = View.GONE
        showExerciseListBtn = findViewById<FloatingActionButton>(R.id.show_exercises_btn)
        createWorkoutBtn = findViewById<FloatingActionButton>(R.id.create_workout_btn)
        addExerciseBtn = findViewById<FloatingActionButton>(R.id.add_exercise_btn)

        showWorkoutListBtn.setOnClickListener { view ->
            Snackbar.make(view, "Show workout list clicked", Snackbar.LENGTH_SHORT)
                .setAction("Action", null).show()

            supportFragmentManager.beginTransaction()
                .replace(R.id.main_fragment_view, WorkoutFragment.newInstance(), "workoutlist")
                .commit()

            setBtnVisibility(view, "workoutlist")
        }

        showExerciseListBtn.setOnClickListener { view ->
            Snackbar.make(view, "Show exercise button clicked", Snackbar.LENGTH_SHORT)
                .setAction("Action", null).show()

            supportFragmentManager.beginTransaction()
                .replace(R.id.main_fragment_view, ExerciseListFragment.newInstance(), "exerciselist")
                .commit()

            setBtnVisibility(view, "exerciselist")
        }

        createWorkoutBtn.setOnClickListener { view ->
            Snackbar.make(view, "Create workout button clicked", Snackbar.LENGTH_SHORT)
                .setAction("Action", null).show()

            supportFragmentManager.beginTransaction()
                .replace(R.id.main_fragment_view, CreateWorkoutFragment.newInstance(), "createworkout")
                .commit()

            setBtnVisibility(view, "createworkout")
        }

        addExerciseBtn.setOnClickListener { view ->
            supportFragmentManager.beginTransaction()
                .replace(R.id.main_fragment_view, AddExerciseFragment.newInstance(), "addexercise")
                .commit()

            setBtnVisibility(view, "addexercise")
        }
    }

    private fun setBtnVisibility(view: View, fragment: String){
        //val currentFrag = this.supportFragmentManager.fragments.last().tag

        when(fragment){
            "addexercise" -> {
                addExerciseBtn.visibility = View.GONE
                createWorkoutBtn.visibility = View.GONE
                showExerciseListBtn.visibility = View.VISIBLE
                showWorkoutListBtn.visibility = View.VISIBLE
            }
            "createworkout" -> {
                createWorkoutBtn.visibility = View.GONE
                addExerciseBtn.visibility = View.GONE
                showExerciseListBtn.visibility = View.GONE
                showWorkoutListBtn.visibility = View.VISIBLE
            }
            "exerciselist" -> {
                addExerciseBtn.visibility = View.VISIBLE
                createWorkoutBtn.visibility = View.GONE
                showWorkoutListBtn.visibility = View.VISIBLE
                showExerciseListBtn.visibility = View.GONE
            }
            "workoutlist" -> {
                showExerciseListBtn.visibility = View.VISIBLE
                addExerciseBtn.visibility = View.VISIBLE
                createWorkoutBtn.visibility = View.VISIBLE
                showWorkoutListBtn.visibility = View.GONE
            }
        }
    }
}