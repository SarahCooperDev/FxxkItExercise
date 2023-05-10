package com.example.fxxkit.Fragment

import android.app.AlertDialog
import android.graphics.Color
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import android.widget.ToggleButton
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

    private lateinit var exNameInput: EditText
    private lateinit var isStrengthBtn: ToggleButton
    private lateinit var isConditioningBtn: ToggleButton
    private lateinit var setSizeMultiselect: TextView
    private lateinit var repSizeMultiselect: TextView
    private lateinit var targettedMusclesMultiselect: TextView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let { }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view =  inflater.inflate(R.layout.fragment_add_exercise, container, false)

        exNameInput = view.findViewById<EditText>(R.id.exercise_name)
        isStrengthBtn = view.findViewById<ToggleButton>(R.id.strengthening_toggle_btn)
        isConditioningBtn = view.findViewById<ToggleButton>(R.id.conditioning_toggle_btn)
        setSizeMultiselect = view.findViewById(R.id.set_size_multiselect)
        repSizeMultiselect = view.findViewById(R.id.rep_size_multiselect)
        targettedMusclesMultiselect = view.findViewById<TextView>(R.id.muscle_select)

        val showBtn = view.findViewById<Button>(R.id.add_exercise_btn)
        showBtn.setOnClickListener{view ->
            if(exNameInput.text.toString().length < 1){
                Toast.makeText(activity, "Exercise name may not be blank", Toast.LENGTH_LONG).show()
                exNameInput.setBackgroundColor(ContextCompat.getColor(context!!, R.color.dark_red))
            } else {
                addExercise(view)
                Toast.makeText(activity, "Added exercise to database", Toast.LENGTH_SHORT).show()
                (activity as MainActivity).navToExerciseList()
            }
        }

        MultiselectLists.buildMultiselect((activity as MainActivity), view, setSizeMultiselect, MultiselectLists.setSizesArray,
            newExercise.possibleSetSize)

        MultiselectLists.buildMultiselect((activity as MainActivity), view, repSizeMultiselect, MultiselectLists.repSizesArray,
            newExercise.possibleRepSize)

        MultiselectLists.buildMultiselect((activity as MainActivity), view, targettedMusclesMultiselect,
            MultiselectLists.targettedMusclesArray,newExercise.targettedMuscles)

        return view
    }

    private fun addExercise(view: View){
        val dbHandler = DBHandler(this.requireContext(), null, null, 1)

        val exerciseName = exNameInput.text.toString()

        newExercise.name = exerciseName

        if(isStrengthBtn.isChecked()){ newExercise.isStrengthening = true }
        if(isConditioningBtn.isChecked()){ newExercise.isConditioning = true }

        dbHandler.addExercise(newExercise)
    }


    companion object {
        @JvmStatic
        fun newInstance() =
            AddExerciseFragment().apply {
                arguments = Bundle().apply {  }
            }
    }
}