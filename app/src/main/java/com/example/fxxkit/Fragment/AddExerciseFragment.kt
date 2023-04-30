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
import com.example.fxxkit.DBHandler
import com.example.fxxkit.DataClass.Exercise
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
    private var errorColor: String = "#cc0000"

    private var setNoArray: Array<String> = arrayOf("3", "5", "10", "12", "15")
    private var repNoArray: Array<String> = arrayOf("5", "10", "12", "15", "20", "30", "50")
    private var targettedMusclesArray: Array<String> = arrayOf("Calfs", "Quads", "Glutts", "Abs", "Triceps", "Biceps")

    private val selectedSetNos = ArrayList<String>()
    private val selectedRepNos = ArrayList<String>()
    private val selectedMuscles = ArrayList<String>()

    private lateinit var exNameInput: EditText
    private lateinit var isStrengthBtn: ToggleButton
    private lateinit var isConditioningBtn: ToggleButton
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
        targettedMusclesMultiselect = view.findViewById<TextView>(R.id.muscle_select)

        val showBtn = view.findViewById<Button>(R.id.add_exercise_btn)
        showBtn.setOnClickListener{view ->
            if(exNameInput.text.toString().length < 1){
                Toast.makeText(activity, "Exercise name may not be blank", Toast.LENGTH_LONG).show()
                exNameInput.setBackgroundColor(Color.parseColor(errorColor))
            } else {
                addExercise(view)
                Toast.makeText(activity, "Added exercise to database", Toast.LENGTH_SHORT).show()
                (activity as MainActivity).navToPrevious(view)
            }
        }

        buildMultiselectSetNo(view)
        buildMultiselectRepNo(view)
        buildTargettedMusclesMultiselect(view)

        return view
    }

    private fun addExercise(view: View){
        val dbHandler = DBHandler(this.requireContext(), null, null, 1)

        val exerciseName = exNameInput.text.toString()

        val exercise = Exercise(exerciseName)

        if(isStrengthBtn.isChecked()){ exercise.isStrengthening = true }
        if(isConditioningBtn.isChecked()){ exercise.isConditioning = true }
        if(selectedSetNos.size > 0){ exercise.possibleSetSize = selectedSetNos }
        if(selectedRepNos.size > 0){ exercise.possibleRepSize = selectedRepNos }
        if(selectedMuscles.size > 0){ exercise.targettedMuscles = selectedMuscles }

        dbHandler.addExercise(exercise)
    }

    private fun buildTargettedMusclesMultiselect(view:View){
        // Array that keeps track of the options selected, but only by index
        var selectedIndexArray =  ArrayList<Int>()

        // Triggers the dialog on click
        targettedMusclesMultiselect.setOnClickListener{view ->
            val builder = AlertDialog.Builder(activity)
            builder.setTitle("Muscles targetted by exercise")
            builder.setCancelable(false)

            // Creates the array of bools that indicates whether an option should already be checked
            val preselectedArray = BooleanArray(targettedMusclesArray.size){false}
            for(item in selectedMuscles){
                var itemIndex = targettedMusclesArray.indexOf(item)
                preselectedArray[itemIndex] = true
            }

            // Shows the options, and chooses what to do when one is selected
            builder.setMultiChoiceItems(targettedMusclesArray, preselectedArray){dialog, which, isChecked ->
                if(isChecked){
                    selectedIndexArray.add(which)
                } else if(selectedIndexArray.contains(which)){
                    selectedIndexArray.remove(Integer.valueOf(which))
                }
            }

            // Sets up button to complete the dialog
            builder.setPositiveButton("Done"){ dialogInterface, i ->
                // Converts the selection of indexes into the the corresponding values
                selectedMuscles.clear()
                selectedIndexArray.sort()
                for(j in selectedIndexArray.indices){
                    selectedMuscles.add(targettedMusclesArray[selectedIndexArray[j]])
                }

                // Sets the TextView text to the selected sets
                targettedMusclesMultiselect.text = Arrays.toString(selectedMuscles.toTypedArray())
            }

            // Sets up the button to cancel the input
            builder.setNegativeButton("Cancel"){dialogInterface, i ->
                dialogInterface.dismiss()
            }

            // Sets up the button that clears all selected options
            builder.setNeutralButton("Clear"){dialogInterface, i ->
                selectedIndexArray.clear()
                selectedMuscles.clear()
            }

            builder.show()
        }
    }


    /**
     * Function that creates a multiselect dialog upon clicking an element
     *
     * Inputs params: view - the current view context
     *
     * Updates the selectedSetNos array
     */
    private fun buildMultiselectSetNo(view:View){
        // Array that keeps track of the options selected, but only by index
        var selectedIndexArray =  ArrayList<Int>()

        // The textview that the dialog is bound to
        val exSetNosInput = view.findViewById<TextView>(R.id.exercise_set_nos)

        // Triggers the dialog on click
        exSetNosInput.setOnClickListener{view ->
            val builder = AlertDialog.Builder(activity)
            builder.setTitle("Set lengths for Exercise")
            builder.setCancelable(false)

            // Creates the array of bools that indicates whether an option should already be checked
            val preselectedArray = BooleanArray(setNoArray.size){false}
            for(item in selectedSetNos){
                var itemIndex = setNoArray.indexOf(item)
                preselectedArray[itemIndex] = true
            }

            // Shows the options, and chooses what to do when one is selected
            builder.setMultiChoiceItems(setNoArray, preselectedArray){dialog, which, isChecked ->
                if(isChecked){
                    selectedIndexArray.add(which)
                } else if(selectedIndexArray.contains(which)){
                    selectedIndexArray.remove(Integer.valueOf(which))
                }
            }

            // Sets up button to complete the dialog
            builder.setPositiveButton("Done"){ dialogInterface, i ->
                // Converts the selection of indexes into the the corresponding values
                selectedSetNos.clear()
                selectedIndexArray.sort()
                for(j in selectedIndexArray.indices){
                    selectedSetNos.add(setNoArray[selectedIndexArray[j]])
                }

                // Sets the TextView text to the selected sets
                exSetNosInput.text = Arrays.toString(selectedSetNos.toTypedArray())
            }

            // Sets up the button to cancel the input
            builder.setNegativeButton("Cancel"){dialogInterface, i ->
                dialogInterface.dismiss()
            }

            // Sets up the button that clears all selected options
            builder.setNeutralButton("Clear"){dialogInterface, i ->
                selectedIndexArray.clear()
                selectedSetNos.clear()
            }

            builder.show()
        }
    }


    private fun buildMultiselectRepNo(view:View){
        // Array that keeps track of the options selected, but only by index
        var selectedIndexArray =  ArrayList<Int>()

        // The textview that the dialog is bound to
        val exRepNosInput = view.findViewById<TextView>(R.id.exercise_rep_nos)

        // Triggers the dialog on click
        exRepNosInput.setOnClickListener{view ->
            val builder = AlertDialog.Builder(activity)
            builder.setTitle("Rep lengths for Exercise")
            builder.setCancelable(false)

            // Creates the array of bools that indicates whether an option should already be checked
            val preselectedArray = BooleanArray(repNoArray.size){false}
            for(item in selectedRepNos){
                var itemIndex = repNoArray.indexOf(item)
                preselectedArray[itemIndex] = true
            }

            // Shows the options, and chooses what to do when one is selected
            builder.setMultiChoiceItems(repNoArray, preselectedArray){dialog, which, isChecked ->
                if(isChecked){
                    selectedIndexArray.add(which)
                } else if(selectedIndexArray.contains(which)){
                    selectedIndexArray.remove(Integer.valueOf(which))
                }
            }

            // Sets up button to complete the dialog
            builder.setPositiveButton("Done"){ dialogInterface, i ->
                // Converts the selection of indexes into the the corresponding values
                selectedRepNos.clear()
                selectedIndexArray.sort()
                for(j in selectedIndexArray.indices){
                    selectedRepNos.add(repNoArray[selectedIndexArray[j]])
                }

                // Sets the TextView text to the selected sets
                exRepNosInput.text = Arrays.toString(selectedRepNos.toTypedArray())
            }

            // Sets up the button to cancel the input
            builder.setNegativeButton("Cancel"){dialogInterface, i ->
                dialogInterface.dismiss()
            }

            // Sets up the button that clears all selected options
            builder.setNeutralButton("Clear"){dialogInterface, i ->
                selectedIndexArray.clear()
                selectedRepNos.clear()
            }

            builder.show()
        }
    }

    companion object {
        @JvmStatic
        fun newInstance() =
            AddExerciseFragment().apply {
                arguments = Bundle().apply {
                }
            }
    }
}