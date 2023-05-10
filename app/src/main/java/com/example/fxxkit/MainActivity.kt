package com.example.fxxkit

import android.annotation.SuppressLint
import android.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ImageButton
import android.widget.TextView
import androidx.core.os.bundleOf
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import com.example.fxxkit.DataClass.Debugger
import com.example.fxxkit.DataClass.Exercise
import com.example.fxxkit.DataClass.Workout
import com.example.fxxkit.Fragment.*
import com.example.fxxkit.ViewModel.WorkoutViewModel
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar

class MainActivity : AppCompatActivity() {
    public var debugger: Debugger = Debugger(true)

    private lateinit var previousBtn: ImageButton
    private lateinit var exercisesBtn: ImageButton
    private var navHistory: ArrayList<String> = ArrayList<String>()
    public lateinit var navController: NavController

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        supportActionBar?.setDisplayOptions(androidx.appcompat.app.ActionBar.DISPLAY_SHOW_CUSTOM)
        supportActionBar?.setCustomView(R.layout.custom_action_bar)
        getSupportActionBar()?.customView?.findViewById<TextView>(R.id.appbar_title_id)?.setText("Workouts")

        previousBtn = findViewById<ImageButton>(R.id.previous_btn)
        exercisesBtn = findViewById<ImageButton>(R.id.exercises_btn)

        var navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHostFragment.navController

        previousBtn.setOnClickListener{ view -> navToPrevious() }
        exercisesBtn.setOnClickListener { view -> navToExerciseList() }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean{
        menuInflater.inflate(R.menu.action_bar_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        println("On options item selected")
        println(item.itemId.toString())
        val id = item.itemId
        when(id){
            R.id.menu_workout_item -> { navToWorkoutList() }
            R.id.menu_add_workout_item -> { navToCreateWorkout() }
            R.id.menu_exercise_item -> { navToExerciseList()}
            R.id.menu_add_exercise_item -> { navToAddExercise() }
            R.id.menu_generate_workout_item -> {}
        }
        return super.onOptionsItemSelected(item)
    }

    fun navToPrevious(){
        println("Navigating to previous")
        navController.popBackStack()
    }

    public fun navToExerciseList(){
        navController.navigate(R.id.action_global_exerciseListFragment)
        getSupportActionBar()?.customView?.findViewById<TextView>(R.id.appbar_title_id)?.setText("Exercises")
    }

    public fun navToAddExercise(){
        navController.navigate(R.id.action_global_addExerciseFragment)
        getSupportActionBar()?.customView?.findViewById<TextView>(R.id.appbar_title_id)?.setText("Add Exercise")
    }

    public fun navToEditExercise(exerciseId: Int){
        var bundle = bundleOf("exerciseId" to exerciseId)
        navController.navigate(R.id.action_exerciseListFragment_to_editExerciseFragment, bundle)
        getSupportActionBar()?.customView?.findViewById<TextView>(R.id.appbar_title_id)?.setText("Edit Exercise")
    }

    public fun navToWorkoutList(){
        navController.navigate(R.id.action_global_workoutListFragment)
        getSupportActionBar()?.customView?.findViewById<TextView>(R.id.appbar_title_id)?.setText("Workouts")
    }

    public fun navToCreateWorkout(){
        navController.navigate(R.id.action_global_createWorkoutFragment)
        getSupportActionBar()?.customView?.findViewById<TextView>(R.id.appbar_title_id)?.setText("Create Workout")
    }

    public fun navToWorkoutDetails(workoutId: Int){
        val bundle = bundleOf("workoutId" to workoutId)
        navController.navigate(R.id.action_workoutListFragment_to_workoutFragment, bundle)
        getSupportActionBar()?.customView?.findViewById<TextView>(R.id.appbar_title_id)?.setText("Workout Details")
    }

    public fun navToEditWorkout(workoutId: Int){
        val bundle = bundleOf("workoutId" to workoutId)
        navController.navigate(R.id.action_workoutListFragment_to_editWorkoutFragment, bundle)
        getSupportActionBar()?.customView?.findViewById<TextView>(R.id.appbar_title_id)?.setText("Update Workout")
    }

    private fun initialiseDB(){
        if(debugger.dbNeedsRefresh){
            println("Refreshing database")
            debugger.dbNeedsRefresh = false

            val dbHandler = DBHandler(this, null, null, 1)
            dbHandler.initialiseDatabase()
        }
    }
}