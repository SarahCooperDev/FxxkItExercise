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
import java.util.*
import kotlin.collections.ArrayList

class EditExerciseFragment : Fragment() {
    private lateinit var currentExercise: Exercise
    private var errorColor: String = "#cc0000"

    private lateinit var idTxt: TextView
    private lateinit var nameEditTxt: EditText
    private lateinit var isStrengthBtn: ToggleButton
    private lateinit var isConditioningBtn: ToggleButton
    private lateinit var targettedMusclesMultiselect: TextView
    private lateinit var setSizeMultiselect: TextView
    private lateinit var repSizeMultiselect: TextView

    private lateinit var cancelBtn: ImageButton
    private lateinit var updateBtn: ImageButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {  }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_edit_exercise, container, false)

        idTxt = view.findViewById(R.id.exercise_id_txt)
        idTxt.text = currentExercise.id.toString()

        nameEditTxt = view.findViewById(R.id.exercise_name_edtxt)
        nameEditTxt.setText(currentExercise.name)

        isStrengthBtn = view.findViewById(R.id.strengthening_toggle_btn)
        if(currentExercise.isStrengthening){ isStrengthBtn.isChecked = true }

        isConditioningBtn = view.findViewById(R.id.conditioning_toggle_btn)
        if(currentExercise.isConditioning) { isConditioningBtn.isChecked = true }

        setSizeMultiselect = view.findViewById(R.id.set_size_multiselect)
        if(currentExercise.possibleSetSize.size > 0){
            setSizeMultiselect.text = MultiselectLists.getStringFromArray(currentExercise.possibleSetSize)
        }

        repSizeMultiselect = view.findViewById(R.id.rep_size_multiselect)
        if(currentExercise.possibleRepSize.size > 0){
            repSizeMultiselect.text = MultiselectLists.getStringFromArray(currentExercise.possibleRepSize)
        }

        targettedMusclesMultiselect = view.findViewById(R.id.muscle_select)
        if(currentExercise.targettedMuscles.size > 0){
            targettedMusclesMultiselect.text = MultiselectLists.getStringFromArray(currentExercise.targettedMuscles)
        }

        MultiselectLists.buildMultiselect((activity as MainActivity), view, setSizeMultiselect, MultiselectLists.setSizesArray,
            currentExercise.possibleSetSize)

        MultiselectLists.buildMultiselect((activity as MainActivity), view, repSizeMultiselect, MultiselectLists.repSizesArray,
            currentExercise.possibleRepSize)

        MultiselectLists.buildMultiselect((activity as MainActivity), view, targettedMusclesMultiselect,
            MultiselectLists.targettedMusclesArray,currentExercise.targettedMuscles)

        cancelBtn = view.findViewById(R.id.cancel_btn)
        cancelBtn.setOnClickListener{ view ->
            (activity as MainActivity).navToPrevious(view)
        }

        updateBtn = view.findViewById(R.id.update_btn)
        updateBtn.setOnClickListener{ view ->
            if(nameEditTxt.text.toString().length < 1){
                Toast.makeText(activity, "Exercise name may not be blank", Toast.LENGTH_LONG).show()
                nameEditTxt.setBackgroundColor(Color.parseColor(errorColor))
            } else {
                updateExercise()
                Toast.makeText(activity, "Updated exercise in database", Toast.LENGTH_SHORT).show()
                (activity as MainActivity).navToPrevious(view)
            }

        }

        return view
    }

    private fun updateExercise(){
        currentExercise.name = nameEditTxt.text.toString()
        if(isStrengthBtn.isChecked()){ currentExercise.isStrengthening = true }
        if(isConditioningBtn.isChecked()){ currentExercise.isConditioning = true }

        val dbHandler = DBHandler(this.requireContext(), null, null, 1)

        dbHandler.updateExercise(currentExercise)
    }

    companion object {
        @JvmStatic
        fun newInstance(editExercise: Exercise) = EditExerciseFragment().apply {
            arguments = Bundle().apply {
                currentExercise = editExercise
            }
        }
    }
}