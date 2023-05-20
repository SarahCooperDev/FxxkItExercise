package com.example.fxxkit.Fragment

import android.app.AlertDialog
import android.graphics.Color
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.accessibility.AccessibilityManager.AudioDescriptionRequestedChangeListener
import android.widget.*
import androidx.core.content.ContextCompat
import com.example.fxxkit.DBHandler
import com.example.fxxkit.DataClass.Exercise
import com.example.fxxkit.DataClass.MultiselectLists
import com.example.fxxkit.MainActivity
import com.example.fxxkit.R
import java.util.*
import kotlin.collections.ArrayList

/**
 * A simple [Fragment] subclass.
 * Use the [AddExerciseFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class AddExerciseFragment : Fragment() {
    private var newExercise: Exercise = Exercise("Null")
    private var selectedSets = ArrayList<String>()
    private var selectedReps = ArrayList<String>()
    private var selectedMuscles = ArrayList<String>()

    private lateinit var exNameInput: EditText
    private lateinit var descriptionInput: EditText
    private lateinit var repTimeInput: EditText
    private lateinit var isStrengthBtn: ToggleButton
    private lateinit var isConditioningBtn: ToggleButton
    private lateinit var setSizeMultiselect: TextView
    private lateinit var repSizeMultiselect: TextView
    private lateinit var targettedMusclesMultiselect: TextView
    private lateinit var cancelBtn: ImageButton
    private lateinit var createBtn: ImageButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view =  inflater.inflate(R.layout.fragment_add_exercise, container, false)
        (activity as MainActivity).getSupportActionBar()?.customView?.findViewById<TextView>(R.id.appbar_title_id)?.setText("Create Exercise")

        selectedSets.add(MultiselectLists.setSizesArray[0])
        selectedReps.add(MultiselectLists.repSizesArray[0])
        selectedMuscles.add(MultiselectLists.targettedMusclesArray[0])

        exNameInput = view.findViewById<EditText>(R.id.exercise_name)
        descriptionInput = view.findViewById<EditText>(R.id.description_txt)
        repTimeInput = view.findViewById<EditText>(R.id.rep_time_txt)
        isStrengthBtn = view.findViewById<ToggleButton>(R.id.strengthening_toggle_btn)
        isConditioningBtn = view.findViewById<ToggleButton>(R.id.conditioning_toggle_btn)
        setSizeMultiselect = view.findViewById(R.id.set_size_multiselect)
        repSizeMultiselect = view.findViewById(R.id.rep_size_multiselect)
        targettedMusclesMultiselect = view.findViewById<TextView>(R.id.muscle_select)
        cancelBtn = view.findViewById<ImageButton>(R.id.cancel_btn)
        createBtn = view.findViewById<ImageButton>(R.id.create_btn)

        createBtn.setOnClickListener{view ->
            if(exNameInput.text.toString().length < 1){
                Toast.makeText(activity, "Exercise name may not be blank", Toast.LENGTH_LONG).show()
                exNameInput.setBackgroundColor(ContextCompat.getColor(context!!, R.color.dark_red))
            } else {
                addExercise()
                Toast.makeText(activity, "Added exercise to database", Toast.LENGTH_SHORT).show()
                (activity as MainActivity).navToExerciseList()
            }
        }
        cancelBtn.setOnClickListener { view ->
            (activity as MainActivity).navToPrevious()
        }

        setSizeMultiselect.setText(selectedSets[0])
        setSizeMultiselect.setOnClickListener { view ->
            MultiselectLists.showDialog(activity as MainActivity, layoutInflater, MultiselectLists.setSizesArray,  selectedSets, setSizeMultiselect)
        }

        repSizeMultiselect.setText(selectedReps[0])
        repSizeMultiselect.setOnClickListener { view ->
            MultiselectLists.showDialog(activity as MainActivity, layoutInflater, MultiselectLists.repSizesArray, selectedReps, repSizeMultiselect)
        }

        targettedMusclesMultiselect.setText(selectedMuscles[0])
        targettedMusclesMultiselect.setOnClickListener { view ->
            MultiselectLists.showDialog(activity as MainActivity, layoutInflater, MultiselectLists.targettedMusclesArray, selectedMuscles, targettedMusclesMultiselect)
        }

        return view
    }

    private fun addExercise(){
        val dbHandler = DBHandler(this.requireContext(), null, null, 1)

        newExercise.name = exNameInput.text.toString()
        newExercise.description = descriptionInput.text.toString()

        if(isStrengthBtn.isChecked()){ newExercise.isStrengthening = true }
        if(isConditioningBtn.isChecked()){ newExercise.isConditioning = true }
        if(selectedSets.size == 0){selectedSets.add(MultiselectLists.setSizesArray[0])}
        if(selectedReps.size == 0){selectedReps.add(MultiselectLists.repSizesArray[0])}
        if(selectedMuscles.size == 0){selectedMuscles.add(MultiselectLists.targettedMusclesArray[0])}

        newExercise.possibleSetSize = selectedSets
        newExercise.possibleRepSize = selectedReps
        newExercise.targettedMuscles = selectedMuscles

        try {
            var repTime = repTimeInput.text.toString().toInt()
            if(repTime != null){ newExercise.repTime = repTime}
        } catch(e: Exception){
            Toast.makeText(requireContext(), "Rep time must be number", Toast.LENGTH_LONG)
        }

        dbHandler.addExercise(newExercise)
    }


    companion object {
        @JvmStatic
        fun newInstance() =
            AddExerciseFragment().apply { }
    }
}