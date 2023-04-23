package com.example.fxxkit

import android.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.ImageButton
import android.widget.TextView
import com.example.fxxkit.Fragment.AddExerciseFragment
import com.example.fxxkit.Fragment.CreateWorkoutFragment
import com.example.fxxkit.Fragment.ExerciseListFragment
import com.example.fxxkit.Fragment.WorkoutListFragment
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar

class MainActivity : AppCompatActivity() {
    private lateinit var showExerciseListBtn : FloatingActionButton
    private lateinit var createWorkoutBtn : FloatingActionButton
    private lateinit var addExerciseBtn : FloatingActionButton
    private lateinit var previousBtn: ImageButton
    private var navHistory: ArrayList<String> = ArrayList<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if(savedInstanceState == null){
            supportFragmentManager.beginTransaction()
                .add(R.id.main_fragment_view, WorkoutListFragment.newInstance(), "workoutlist").commit()
        }

        supportActionBar?.setDisplayOptions(androidx.appcompat.app.ActionBar.DISPLAY_SHOW_CUSTOM)
        supportActionBar?.setCustomView(R.layout.custom_action_bar)
        getSupportActionBar()?.customView?.findViewById<TextView>(R.id.appbar_title_id)?.setText("Workouts")

        showExerciseListBtn = findViewById<FloatingActionButton>(R.id.show_exercises_btn)
        createWorkoutBtn = findViewById<FloatingActionButton>(R.id.create_workout_btn)
        addExerciseBtn = findViewById<FloatingActionButton>(R.id.add_exercise_btn)
        previousBtn = findViewById<ImageButton>(R.id.previous_btn)

        addToNavHistory("workoutList")
        previousBtn.visibility = View.GONE

        previousBtn.setOnClickListener{ view -> navToPrevious(view) }
        showExerciseListBtn.setOnClickListener { view -> navToExerciseList(view) }
        createWorkoutBtn.setOnClickListener { view -> navToCreateWorkout(view) }
        addExerciseBtn.setOnClickListener { view -> navToAddExercise(view) }
    }

    private fun clearBtns(){
        showExerciseListBtn.visibility = View.GONE
        addExerciseBtn.visibility = View.GONE
        createWorkoutBtn.visibility = View.GONE
    }

    private fun navToPrevious(view: View){
        var previous = getPreviousFragment()

        if(previous != null){
            when(previous) {
                "addExercise" -> { navToAddExercise(view) }
                "createWorkout" -> { navToCreateWorkout(view) }
                "exerciseList" -> { navToExerciseList(view) }
                "workoutList" -> { navToWorkoutList(view) }
            }
        }

        if(navHistory.size < 2){
            previousBtn.visibility = View.GONE
        }
    }

    private fun navToAddExercise(view: View){
        supportFragmentManager.beginTransaction()
            .replace(R.id.main_fragment_view, AddExerciseFragment.newInstance(),"addExercise")
            .commit()

        addToNavHistory("addExercise")
        clearBtns()
        getSupportActionBar()?.customView?.findViewById<TextView>(R.id.appbar_title_id)?.setText("Add Exercise")
    }

    private fun navToCreateWorkout(view: View){
        supportFragmentManager.beginTransaction()
            .replace(R.id.main_fragment_view, CreateWorkoutFragment.newInstance(),"createWorkout")
            .commit()

        addToNavHistory("createWorkout")
        clearBtns()
        getSupportActionBar()?.customView?.findViewById<TextView>(R.id.appbar_title_id)?.setText("Create Workout")
    }

    private fun navToExerciseList(view: View){
        supportFragmentManager.beginTransaction()
            .replace(R.id.main_fragment_view, ExerciseListFragment.newInstance(),"exerciseList")
            .commit()

        addToNavHistory("exerciseList")
        clearBtns()
        addExerciseBtn.visibility = View.VISIBLE
        getSupportActionBar()?.customView?.findViewById<TextView>(R.id.appbar_title_id)?.setText("Exercises")
    }

    private fun navToWorkoutList(view: View){
        supportFragmentManager.beginTransaction()
            .replace(R.id.main_fragment_view, WorkoutListFragment.newInstance(),"workoutList")
            .commit()

        addToNavHistory("workoutList")
        clearBtns()
        showExerciseListBtn.visibility = View.VISIBLE
        createWorkoutBtn.visibility = View.VISIBLE
        getSupportActionBar()?.customView?.findViewById<TextView>(R.id.appbar_title_id)?.setText("Workouts")
    }

    private fun getPreviousFragment(): String? {
        if(navHistory.size > 1){
            navHistory.removeLast()
            return navHistory.removeLast()
        } else {
            return null
        }
    }

    private fun addToNavHistory(fragment: String){
        navHistory.add(fragment)

        if(navHistory.size > 1){
            previousBtn.visibility = View.VISIBLE
        }

        println("Navigated to " + fragment)
    }
}