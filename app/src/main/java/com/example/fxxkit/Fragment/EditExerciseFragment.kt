package com.example.fxxkit.Fragment

import android.app.AlertDialog
import android.graphics.Color
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.example.fxxkit.DBHandler
import com.example.fxxkit.DataClass.Exercise
import com.example.fxxkit.DataClass.MultiselectLists
import com.example.fxxkit.MainActivity
import com.example.fxxkit.R
import com.example.fxxkit.ViewModel.WorkoutViewModel
import java.util.*
import kotlin.collections.ArrayList

class EditExerciseFragment : Fragment() {
    private lateinit var currentExercise: Exercise
    private var errorColor: String = "#cc0000"

    private var selectedSets = ArrayList<String>()
    private var selectedReps = ArrayList<String>()
    private var selectedMuscles = ArrayList<String>()

    private lateinit var idTxt: TextView
    private lateinit var nameEditTxt: EditText
    private lateinit var descriptionInput: EditText
    private lateinit var isStrengthBtn: ToggleButton
    private lateinit var isConditioningBtn: ToggleButton
    private lateinit var targettedMusclesMultiselect: TextView
    private lateinit var setSizeMultiselect: TextView
    private lateinit var repSizeMultiselect: TextView
    private lateinit var repTimeInput: EditText

    private lateinit var cancelBtn: ImageButton
    private lateinit var updateBtn: ImageButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        var exerciseId = arguments!!.getInt("exerciseId")
        loadExercise(exerciseId)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_edit_exercise, container, false)
        (activity as MainActivity).getSupportActionBar()?.customView?.findViewById<TextView>(R.id.appbar_title_id)?.setText("Edit Exercise")

        idTxt = view.findViewById(R.id.exercise_id_txt)
        nameEditTxt = view.findViewById(R.id.exercise_name_edtxt)
        descriptionInput = view.findViewById(R.id.description_txt)
        isStrengthBtn = view.findViewById(R.id.strengthening_toggle_btn)
        isConditioningBtn = view.findViewById(R.id.conditioning_toggle_btn)
        setSizeMultiselect = view.findViewById(R.id.set_size_multiselect)
        repSizeMultiselect = view.findViewById(R.id.rep_size_multiselect)
        repTimeInput = view.findViewById(R.id.rep_time_txt)
        targettedMusclesMultiselect = view.findViewById(R.id.muscle_select)
        cancelBtn = view.findViewById(R.id.cancel_btn)
        updateBtn = view.findViewById(R.id.update_btn)

        idTxt.text = currentExercise.id.toString()
        nameEditTxt.setText(currentExercise.name)
        descriptionInput.setText(currentExercise.description)
        repTimeInput.setText(currentExercise.repTime.toString())

        if(currentExercise.isStrengthening){ isStrengthBtn.isChecked = true }
        if(currentExercise.isConditioning) { isConditioningBtn.isChecked = true }

        if(currentExercise.possibleSetSize.size > 0){
            setSizeMultiselect.text = MultiselectLists.getStringFromArray(currentExercise.possibleSetSize)
            selectedSets = currentExercise.possibleSetSize.clone() as ArrayList<String>
        }

        if(currentExercise.possibleRepSize.size > 0){
            repSizeMultiselect.text = MultiselectLists.getStringFromArray(currentExercise.possibleRepSize)
            selectedReps = currentExercise.possibleRepSize.clone() as ArrayList<String>
        }

        if(currentExercise.targettedMuscles.size > 0){
            targettedMusclesMultiselect.text = MultiselectLists.getStringFromArray(currentExercise.targettedMuscles)
            selectedMuscles = currentExercise.targettedMuscles.clone() as ArrayList<String>
        }

        setSizeMultiselect.setOnClickListener { view ->
            MultiselectLists.showDialog(activity as MainActivity, layoutInflater, MultiselectLists.setSizesArray, selectedSets, setSizeMultiselect)
        }

        repSizeMultiselect.setOnClickListener { view ->
            MultiselectLists.showDialog(activity as MainActivity, layoutInflater, MultiselectLists.repSizesArray, selectedReps, repSizeMultiselect)
        }

        targettedMusclesMultiselect.setOnClickListener { view ->
            MultiselectLists.showDialog(activity as MainActivity, layoutInflater, MultiselectLists.targettedMusclesArray, selectedMuscles, targettedMusclesMultiselect)
        }

        cancelBtn.setOnClickListener{ view ->
            (activity as MainActivity).navToPrevious()
        }

        updateBtn.setOnClickListener{ view ->
            if(nameEditTxt.text.toString().length < 1){
                Toast.makeText(activity, "Exercise name may not be blank", Toast.LENGTH_LONG).show()
                nameEditTxt.setBackgroundColor(Color.parseColor(errorColor))
            } else {
                updateExercise()
                Toast.makeText(activity, "Updated exercise in database", Toast.LENGTH_SHORT).show()
                (activity as MainActivity).navToPrevious()
            }
        }

        return view
    }

    private fun loadExercise(exerciseId: Int){
        val dbHandler = DBHandler(this.requireContext(), null, null, 1)
        var exercise = dbHandler.findExerciseById(exerciseId)
        if(exercise !=  null){
            currentExercise = exercise
        }
    }

    private fun updateExercise(){
        currentExercise.name = nameEditTxt.text.toString()
        currentExercise.description = descriptionInput.text.toString()
        if(isStrengthBtn.isChecked()){ currentExercise.isStrengthening = true }
        if(isConditioningBtn.isChecked()){ currentExercise.isConditioning = true }
        currentExercise.possibleSetSize = selectedSets
        currentExercise.possibleRepSize = selectedReps
        currentExercise.targettedMuscles = selectedMuscles

        try{
            var repTime = repTimeInput.text.toString().toInt()
            if(repTime != null){ currentExercise.repTime = repTime}
        } catch(e: Exception){
            Toast.makeText(requireContext(), "Rep time must be number", Toast.LENGTH_LONG)
        }

        val dbHandler = DBHandler(this.requireContext(), null, null, 1)

        dbHandler.updateExercise(currentExercise)
    }

    companion object {
        @JvmStatic
        fun newInstance() = EditExerciseFragment().apply { }
    }
}