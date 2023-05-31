package com.example.fxxkit.Fragment

import android.app.AlertDialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.fxxkit.AddExerciseToWorkoutHelper
import com.example.fxxkit.DBHandler
import com.example.fxxkit.DataClass.Exercise
import com.example.fxxkit.DataClass.MultiselectLists
import com.example.fxxkit.DataClass.WorkoutExercise
import com.example.fxxkit.MainActivity
import com.example.fxxkit.R
import com.example.fxxkit.ViewHolder.WorkoutSelectListAdapter
import com.example.fxxkit.ViewModel.WorkoutViewModel

/**
 * Takes an exercise id, showing the details of an exercise
 */
class ExerciseDetailsFragment : Fragment() {
    private var exerciseId: Int = -1
    private var allWorkouts: ArrayList<WorkoutViewModel> = ArrayList()
    private lateinit var workoutExercise: WorkoutExercise
    private lateinit var currentExercise: Exercise

    private lateinit var idTxt: TextView
    private lateinit var nameTxt: TextView
    private lateinit var descriptionTxt: TextView
    private lateinit var setListTxt: TextView
    private lateinit var repListTxt: TextView
    private lateinit var repTimeTxt: TextView
    private lateinit var targettedAreasTxt: TextView
    private lateinit var isStrengthTxt: TextView
    private lateinit var isConditionTxt: TextView
    private lateinit var tagTxt: TextView
    private lateinit var createdTxt: TextView
    private lateinit var updatedTxt: TextView
    private lateinit var addToBtn: ImageButton
    private lateinit var editBtn: ImageButton
    private lateinit var deleteBtn: ImageButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        exerciseId = arguments!!.getInt("exerciseId")
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        var view = inflater.inflate(R.layout.fragment_exercise_details, container, false)
        loadExercise()
        loadWorkouts()

        idTxt = view.findViewById(R.id.exercise_id_txt)
        nameTxt = view.findViewById(R.id.name_txt)
        descriptionTxt = view.findViewById(R.id.description_txt)
        setListTxt = view.findViewById(R.id.set_list_txt)
        repListTxt = view.findViewById(R.id.rep_list_txt)
        repTimeTxt = view.findViewById(R.id.rep_time_txt)
        targettedAreasTxt = view.findViewById(R.id.area_list_txt)
        isStrengthTxt = view.findViewById(R.id.is_strength_txt)
        isConditionTxt = view.findViewById(R.id.is_condition_txt)
        tagTxt = view.findViewById(R.id.tag_txt)
        createdTxt = view.findViewById(R.id.created_date_txt)
        updatedTxt = view.findViewById(R.id.updated_date_txt)
        addToBtn = view.findViewById(R.id.add_to_workout_btn)
        editBtn = view.findViewById(R.id.edit_btn)
        deleteBtn = view.findViewById(R.id.delete_btn)

        idTxt.setText(currentExercise.id.toString())
        nameTxt.setText(currentExercise.name)
        descriptionTxt.setText(currentExercise.description)
        setListTxt.setText(currentExercise.getSetAsString())
        repListTxt.setText(currentExercise.getRepsAsString())
        repTimeTxt.setText(currentExercise.repTime.toString())
        targettedAreasTxt.setText(currentExercise.getAreasAsString())
        if(currentExercise.isStrengthening){ isStrengthTxt.setText(getString(R.string.is_strength_txt)) }
        else { isStrengthTxt.setText(getString(R.string.isnt_strength_txt)) }
        if(currentExercise.isConditioning){ isConditionTxt.setText(getString(R.string.is_condition_txt)) }
        else { isConditionTxt.setText(getString(R.string.isnt_condition_txt)) }
        createdTxt.setText(getString(R.string.created_txt) + " " + currentExercise.createdDate.toString())
        updatedTxt.setText(getString(R.string.updated_txt) + " " + currentExercise.updatedDate.toString())
        if(currentExercise.tags.size > 0){ tagTxt.setText(currentExercise.getTagDisplayString()) }
        else { tagTxt.setText(activity!!.baseContext.getString(R.string.no_tags_txt)) }

        addToBtn.setOnClickListener { view -> triggerAddDialog() }
        editBtn.setOnClickListener { view -> (activity as MainActivity).navToEditExercise(currentExercise.id) }

        deleteBtn.setOnClickListener{ view ->
            val builder = AlertDialog.Builder(view.context)
            builder.setMessage(activity!!.baseContext.getString(R.string.delete_confirmation))
                .setCancelable(false)
                .setPositiveButton(activity!!.baseContext.getString(R.string.yes_txt)) { dialog, id ->
                    val dbHandler = DBHandler(view.context, null, null, 1)
                    dbHandler.deleteExercise(currentExercise.id)
                }
                .setNegativeButton(activity!!.baseContext.getString(R.string.no_txt)) { dialog, id ->
                    dialog.dismiss()
                }

            val alert = builder.create()
            alert.show()
        }

        return view
    }

    private fun triggerAddDialog(){
        var workEx = WorkoutExercise(currentExercise)
        var helper = AddExerciseToWorkoutHelper((activity as MainActivity), allWorkouts, workEx)
        helper.buildAddToWorkoutDialog()
    }

    private fun loadExercise(){
        val dbHandler = DBHandler(this.requireContext(), null, null, 1)
        currentExercise = Exercise("")
        var exercise = dbHandler.findExerciseById(exerciseId)
        if(exercise !=  null){
            currentExercise = exercise
            workoutExercise = WorkoutExercise(currentExercise)
            var exerciseTags = dbHandler.getTagsForExercise(currentExercise!!)

            if(exerciseTags != null){
                currentExercise!!.tags = exerciseTags
            }
        }
    }

    fun loadWorkouts() {
        allWorkouts.clear()
        val dbHandler = DBHandler(this.requireContext(), null, null, 1)
        val workouts = dbHandler.getAllWorkouts()

        if(workouts != null && workouts.size > 0){
            for(workout in workouts){
                var workoutVM = WorkoutViewModel(workout.id, workout.workoutName!!)
                workoutVM.description = workout.description
                workoutVM.isFavourited = workout.isFavourited
                workoutVM.workExList = dbHandler.findAllWorkoutExercises(workout)
                workoutVM.workExList.sortBy { it.orderNo }

                for(workEx in workoutVM.workExList){
                    if(workEx.exerciseId > -1){
                        var exercise = dbHandler.findExerciseById(workEx.exerciseId)
                        workEx.exercise = exercise
                        workEx.isSelected = true
                    }
                }

                var workoutTags = dbHandler.getTagsForWorkout(workout)
                if(workoutTags != null){
                    workout.tags = workoutTags
                    workoutVM.tags = workoutTags
                }
                allWorkouts.add(workoutVM)
            }
        }
    }

    companion object {
        @JvmStatic
        fun newInstance() = ExerciseDetailsFragment().apply {}
    }
}