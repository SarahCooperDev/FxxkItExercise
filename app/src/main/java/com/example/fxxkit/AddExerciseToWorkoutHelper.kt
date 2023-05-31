package com.example.fxxkit

import android.app.AlertDialog
import android.view.LayoutInflater
import android.view.View
import android.widget.*
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.fxxkit.DataClass.MultiselectLists
import com.example.fxxkit.DataClass.WorkoutExercise
import com.example.fxxkit.ViewHolder.WorkoutSelectListAdapter
import com.example.fxxkit.ViewModel.WorkoutViewModel

/**
 * Helper to construct a dialog that, when given an exercise, will allow the user to select a workout
 * to add that exercise to (and add the associated details)
 */
class AddExerciseToWorkoutHelper {
    private var activity: MainActivity
    private var allWorkouts: ArrayList<WorkoutViewModel>
    private var workoutExercise: WorkoutExercise

    private var selectedWorkout: WorkoutViewModel? = null
    private var workoutListLayout: LinearLayout? = null
    private var detailLayout: LinearLayout? = null
    private var selectedWorkoutTxt: TextView? = null
    private var dialog: AlertDialog? = null

    constructor(activity: MainActivity, allWorkouts: ArrayList<WorkoutViewModel>, workoutExercise: WorkoutExercise){
        this.activity = activity
        this.allWorkouts = allWorkouts
        this.workoutExercise = workoutExercise
    }


    /**
     * Builds and shows the dialog to add the clicked exercise to a workout
     */
    fun buildAddToWorkoutDialog(){
        val builder = AlertDialog.Builder(activity)
        builder.setCancelable(false)
        val inflater: LayoutInflater = activity!!.getLayoutInflater()
        var view = inflater.inflate(R.layout.dialog_select_workout, null)

        workoutListLayout = view.findViewById(R.id.workout_list_layout)
        detailLayout = view.findViewById(R.id.workout_exercise_details_layout)
        selectedWorkoutTxt = view.findViewById(R.id.selected_workout_txt)
        var backBtn: ImageButton = view.findViewById(R.id.back_btn)
        var setRadioGroup = view.findViewById<RadioGroup>(R.id.set_radiogrp)
        var repRadioGroup = view.findViewById<RadioGroup>(R.id.rep_radiogrp)
        var workoutRV: RecyclerView = view.findViewById(R.id.select_workout_rv)

        setUpRadioBtns(setRadioGroup, repRadioGroup)
        workoutRV.layoutManager = LinearLayoutManager(activity)
        workoutRV.adapter = WorkoutSelectListAdapter(activity, allWorkouts, ::callWorkoutSelected)
        detailLayout?.visibility = View.GONE
        builder.setView(view)

        // Sets up button to complete the dialog
        builder.setPositiveButton(activity!!.baseContext.getString(R.string.done_txt)) { dialogInterface, i ->
            if(workoutExercise.setSize == null || workoutExercise.repSize == null){
                Toast.makeText(activity!!.baseContext,  activity!!.baseContext.getString(R.string.error_set_rep_missing), Toast.LENGTH_LONG).show()
            } else {
                addExerciseToWorkout()
                dialogInterface.dismiss()
            }
        }

        // Sets up the button to cancel the input
        builder.setNegativeButton(activity!!.baseContext.getString(R.string.cancel_txt)) { dialogInterface, i ->
            dialogInterface.dismiss()
        }

        // Shows dialog and disables done button, until all necessary choices have been made
        dialog = builder.show()
        dialog?.getButton(AlertDialog.BUTTON_POSITIVE)?.isEnabled = false

        // Navigates to the list of workouts to select again
        backBtn.setOnClickListener { view ->
            workoutListLayout?.visibility = View.VISIBLE
            detailLayout?.visibility = View.GONE
            dialog?.getButton(AlertDialog.BUTTON_POSITIVE)?.isEnabled = false
        }

        // Handles selections of sets and reps, and enables done button if both are filled
        setRadioGroup.setOnCheckedChangeListener { group, checkedId ->
            val radio: RadioButton = view.findViewById(checkedId)
            workoutExercise.setSize = radio.text.toString()
            if (workoutExercise.setSize != null && workoutExercise.repSize != null) {
                dialog?.getButton(AlertDialog.BUTTON_POSITIVE)?.isEnabled = true
            }
        }

        repRadioGroup.setOnCheckedChangeListener { group, checkedId ->
            val radio: RadioButton = view.findViewById(checkedId)
            workoutExercise.repSize = radio.text.toString()
            if (workoutExercise.setSize != null && workoutExercise.repSize != null) {
                dialog?.getButton(AlertDialog.BUTTON_POSITIVE)?.isEnabled = true
            }
        }
    }



    /**
     * Function that is passed to WorkoutSelectListViewHolder, and called when a workout is clicked
     */
    fun callWorkoutSelected(workoutVM: WorkoutViewModel){
        // Checks to ensure exercise isn't already added to a workout
        var checkExercise = workoutVM.workExList.firstOrNull{ it.exercise!!.id == workoutExercise.exerciseId }
        if(checkExercise != null){
            Toast.makeText(activity!!.baseContext, activity!!.baseContext.getString(R.string.error_exercise_already_in_workout), Toast.LENGTH_SHORT).show()
        } else {
            // Changes the dialog view to the screen that shows set and rep selections
            this.selectedWorkout = workoutVM
            workoutListLayout!!.visibility = View.GONE
            detailLayout!!.visibility = View.VISIBLE
            selectedWorkoutTxt!!.text = "Workout: ${workoutVM.name}"
            workoutExercise.workoutId = workoutVM.id

            if(workoutExercise.setSize != null && workoutExercise.repSize != null){
                dialog!!.getButton(AlertDialog.BUTTON_POSITIVE).isEnabled = true
            }
        }
    }


    /**
     * Builds the sets and reps to show to add an exercise to a workout
     */
    fun setUpRadioBtns(setRadioGroup: RadioGroup, repRadioGroup: RadioGroup){
        // Shows the sets available for an exercise, as a selectable list
        if(workoutExercise!!.exercise != null && workoutExercise!!.exercise!!.possibleSetSize != null && workoutExercise!!.exercise!!.possibleSetSize.size > 0){
            if(workoutExercise!!.exercise!!.possibleSetSize[0] == MultiselectLists.setSizesArray[0]){
                for(i in 1 .. MultiselectLists.setSizesArray.size - 1){
                    var set = MultiselectLists.setSizesArray[i]
                    var radiobtn = RadioButton(activity)
                    radiobtn.setText(set)
                    setRadioGroup.addView(radiobtn)
                }
            } else {
                for(set in workoutExercise!!.exercise!!.possibleSetSize){
                    var radiobtn = RadioButton(activity)
                    radiobtn.setText(set)
                    setRadioGroup.addView(radiobtn)
                }
            }
        }

        // Shows the reps available for an exercise, as a selectable list
        if(workoutExercise!!.exercise != null && workoutExercise!!.exercise!!.possibleRepSize != null && workoutExercise!!.exercise!!.possibleRepSize.size > 0){
            if(workoutExercise!!.exercise!!.possibleRepSize[0] == MultiselectLists.repSizesArray[0]){
                for(i in 1 .. MultiselectLists.repSizesArray.size - 1){
                    var rep = MultiselectLists.repSizesArray[i]
                    var radiobtn = RadioButton(activity)
                    radiobtn.setText(rep)
                    repRadioGroup.addView(radiobtn)
                }
            } else {
                for (rep in workoutExercise!!.exercise!!.possibleRepSize) {
                    var radioBtn = RadioButton(activity)
                    radioBtn.setText(rep)
                    repRadioGroup.addView(radioBtn)
                }
            }
        }
    }


    /**
     * Adds the exercise to the selected workout
     */
    fun addExerciseToWorkout(){
        if(activity != null && workoutExercise != null && workoutExercise!!.workoutId >= 0){
            val dbHandler = DBHandler(activity!!.baseContext, null, null, 1)
            workoutExercise!!.orderNo = selectedWorkout!!.workExList.size

            if(workoutExercise!!.workoutId >= 0){
                var result = dbHandler.addExerciseToWorkout(workoutExercise!!)
                Toast.makeText(activity!!.baseContext, "Added exercise ${workoutExercise!!.exercise!!.name} to workout ${selectedWorkout!!.name}", Toast.LENGTH_LONG).show()
            }
        }
    }
}