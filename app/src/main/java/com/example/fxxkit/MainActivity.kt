package com.example.fxxkit

import android.annotation.SuppressLint
import android.app.ActionBar.LayoutParams
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.ImageButton
import androidx.core.os.bundleOf
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.example.fxxkit.DataClass.Debugger
import com.example.fxxkit.DataClass.WorkoutExercise
import com.example.fxxkit.Fragment.*

/**
 * Container activity
 * Sets the custom action bar
 * Creates the nav controller
 */
class MainActivity : AppCompatActivity() {
    var debugger: Debugger = Debugger(true)

    private lateinit var workoutBtn: ImageButton
    private lateinit var exercisesBtn: ImageButton
    lateinit var navController: NavController

    private var generatedWorkoutExercises: ArrayList<WorkoutExercise> = ArrayList<WorkoutExercise>()

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        //migrateDB()

        var actionBar = supportActionBar
        actionBar?.setDisplayOptions(androidx.appcompat.app.ActionBar.DISPLAY_SHOW_CUSTOM)

        var customView = layoutInflater.inflate(R.layout.custom_action_bar, null)
        actionBar?.setCustomView(customView, androidx.appcompat.app.ActionBar.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT))
        var barParent = customView.parent as androidx.appcompat.widget.Toolbar
        barParent.setContentInsetsAbsolute(0, 0)

        var navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHostFragment.navController

        workoutBtn = findViewById(R.id.workout_btn)
        exercisesBtn = findViewById(R.id.exercises_btn)

        workoutBtn.setOnClickListener{ view -> this.navToWorkoutList() }
        exercisesBtn.setOnClickListener { view -> navToExerciseList() }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean{
        menuInflater.inflate(R.menu.action_bar_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.menu_workout_item -> {
                this.navToWorkoutList()
            }
            R.id.menu_add_workout_item -> { navToCreateWorkout() }
            R.id.menu_exercise_item -> { navToExerciseList()}
            R.id.menu_add_exercise_item -> { navToAddExercise() }
            R.id.menu_generate_workout_item -> { navToGenerateWorkout() }
        }
        return super.onOptionsItemSelected(item)
    }

    public fun getGeneratedWorkoutExercises(): ArrayList<WorkoutExercise>{
        return this.generatedWorkoutExercises
    }

    fun navToSuggestedWorkout(selectedWorkExes: ArrayList<WorkoutExercise>){
        this.generatedWorkoutExercises.clear()
        this.generatedWorkoutExercises = selectedWorkExes.clone() as ArrayList<WorkoutExercise>
        navController.navigate(R.id.action_generateWorkoutFragment_to_suggestedWorkoutFragment)
    }

    fun navToGenerateWorkout(){
        navController.navigate(R.id.action_global_generateWorkoutFragment)
    }

    fun navToExerciseList(){
        navController.navigate(R.id.action_global_exerciseListFragment)
    }

    fun navToExerciseDetails(exerciseId: Int){
        val bundle = bundleOf("exerciseId" to exerciseId)
        navController.navigate(R.id.action_global_exerciseDetailsFragment, bundle)
    }

    fun navToAddExercise(){
        navController.navigate(R.id.action_global_addExerciseFragment)
    }

    fun navToEditExercise(exerciseId: Int){
        var bundle = bundleOf("exerciseId" to exerciseId)
        navController.navigate(R.id.action_global_editExerciseFragment, bundle)
    }

    fun navToWorkoutList(){
        navController.navigate(R.id.action_global_workoutListFragment)
    }

    fun navToCreateWorkout(){
        navController.navigate(R.id.action_global_createWorkoutFragment)
    }

    fun navToWorkoutDetails(workoutId: Int){
        val bundle = bundleOf("workoutId" to workoutId)
        navController.navigate(R.id.action_global_workoutFragment, bundle)
    }

    fun navToEditWorkout(workoutId: Int){
        val bundle = bundleOf("workoutId" to workoutId)
        navController.navigate(R.id.action_global_editWorkoutFragment, bundle)
    }

    private fun migrateDB(){
        if(debugger.dbNeedsRefresh){
            println("Refreshing database")
            debugger.dbNeedsRefresh = false

            val dbHandler = DBHandler(this, null, null, 1)
            dbHandler.migrateDatabase()
        }
    }
}