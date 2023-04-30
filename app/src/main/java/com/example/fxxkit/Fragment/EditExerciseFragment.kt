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
import com.example.fxxkit.MainActivity
import com.example.fxxkit.R
import java.util.*
import kotlin.collections.ArrayList

class EditExerciseFragment : Fragment() {
    private lateinit var currentExercise: Exercise
    private var errorColor: String = "#cc0000"

    private var setSizesArray: Array<String> = arrayOf("3", "5", "10", "12", "15")
    private var repSizesArray: Array<String> = arrayOf("5", "10", "12", "15", "20", "30", "50")
    private var targettedMusclesArray: Array<String> = arrayOf("Calfs", "Quads", "Glutts", "Abs", "Triceps", "Biceps")

    private var selectedSetSizes = ArrayList<String>()
    private var selectedRepSizes = ArrayList<String>()
    private var selectedMuscles = ArrayList<String>()

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
            selectedSetSizes = currentExercise.possibleSetSize
            setSizeMultiselect.text = getStringFromArray(selectedSetSizes)
        }

        repSizeMultiselect = view.findViewById(R.id.rep_size_multiselect)
        if(currentExercise.possibleRepSize.size > 0){
            selectedRepSizes = currentExercise.possibleRepSize
            repSizeMultiselect.text = getStringFromArray(selectedRepSizes)
        }

        targettedMusclesMultiselect = view.findViewById(R.id.muscle_select)
        if(currentExercise.targettedMuscles.size > 0){
            selectedMuscles = currentExercise.targettedMuscles
            targettedMusclesMultiselect.text = getStringFromArray(selectedMuscles)
        }

        buildMultiselectSetNo(view)
        buildMultiselectRepNo(view)
        buildTargettedMusclesMultiselect(view)

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
        currentExercise.isConditioning = isConditioningBtn.isChecked
        currentExercise.isStrengthening = isStrengthBtn.isChecked
        currentExercise.possibleSetSize = selectedSetSizes
        currentExercise.possibleRepSize = selectedRepSizes
        currentExercise.targettedMuscles = selectedMuscles

        if(isStrengthBtn.isChecked()){ currentExercise.isStrengthening = true }
        if(isConditioningBtn.isChecked()){ currentExercise.isConditioning = true }
        currentExercise.possibleSetSize = selectedSetSizes
        currentExercise.possibleRepSize = selectedRepSizes
        currentExercise.targettedMuscles = selectedMuscles

        val dbHandler = DBHandler(this.requireContext(), null, null, 1)

        dbHandler.updateExercise(currentExercise)
    }

    private fun getStringFromArray(array: ArrayList<String>): String?{
        if(array.size > 0){
            var stringgedArray = ""
            for(i in 0..array.size - 2){
                stringgedArray += array[i] + ", "
            }
            stringgedArray += array[array.size - 1]

            return stringgedArray
        }
        return null
    }


    private fun buildTargettedMusclesMultiselect(view:View){
        // Array that keeps track of the options selected, but only by index
        var selectedOptionsArray =  ArrayList<Int>()

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
                selectedOptionsArray.add(itemIndex)
            }

            // Shows the options, and chooses what to do when one is selected
            builder.setMultiChoiceItems(targettedMusclesArray, preselectedArray){dialog, which, isChecked ->
                if(isChecked){
                    selectedOptionsArray.add(which)
                } else if(selectedOptionsArray.contains(which)){
                    selectedOptionsArray.remove(Integer.valueOf(which))
                }
            }

            // Sets up button to complete the dialog
            builder.setPositiveButton("Done"){ dialogInterface, i ->
                // Converts the selection of indexes into the the corresponding values
                selectedMuscles.clear()
                selectedOptionsArray.sort()
                for(j in selectedOptionsArray.indices){
                    selectedMuscles.add(targettedMusclesArray[selectedOptionsArray[j]])
                }

                // Sets the TextView text to the selected sets
                targettedMusclesMultiselect.text = getStringFromArray(selectedMuscles)
            }

            // Sets up the button to cancel the input
            builder.setNegativeButton("Cancel"){dialogInterface, i ->
                dialogInterface.dismiss()
                if(currentExercise.targettedMuscles.size > 0){
                    selectedMuscles = currentExercise.targettedMuscles
                    targettedMusclesMultiselect.text = getStringFromArray(selectedMuscles)
                }
            }

            // Sets up the button that clears all selected options
            builder.setNeutralButton("Clear"){dialogInterface, i ->
                selectedOptionsArray.clear()
                if(currentExercise.targettedMuscles.size > 0){
                    selectedMuscles = currentExercise.targettedMuscles
                    targettedMusclesMultiselect.text = getStringFromArray(selectedMuscles)
                }
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
        var selectedOptionsArray =  ArrayList<Int>()

        // Triggers the dialog on click
        setSizeMultiselect.setOnClickListener{view ->
            val builder = AlertDialog.Builder(activity)
            builder.setTitle("Set lengths for Exercise")
            builder.setCancelable(false)

            // Creates the array of bools that indicates whether an option should already be checked
            val preselectedArray = BooleanArray(setSizesArray.size){false}
            for(setSize in selectedSetSizes){
                var itemIndex = setSizesArray.indexOf(setSize)
                preselectedArray[itemIndex] = true
                selectedOptionsArray.add(itemIndex)
            }

            // Shows the options, and chooses what to do when one is selected
            builder.setMultiChoiceItems(setSizesArray, preselectedArray){dialog, which, isChecked ->
                if(isChecked){
                    selectedOptionsArray.add(which)
                } else if(selectedOptionsArray.contains(which)){
                    selectedOptionsArray.remove(Integer.valueOf(which))
                }
            }

            // Sets up button to complete the dialog
            builder.setPositiveButton("Done"){ dialogInterface, i ->
                // Converts the selection of indexes into the the corresponding values
                selectedSetSizes.clear()
                selectedOptionsArray.sort()
                for(j in selectedOptionsArray.indices){
                    selectedSetSizes.add(setSizesArray[selectedOptionsArray[j]])
                }

                // Sets the TextView text to the selected sets
                setSizeMultiselect.text = getStringFromArray(selectedSetSizes)
            }

            // Sets up the button to cancel the input
            builder.setNegativeButton("Cancel"){dialogInterface, i ->
                dialogInterface.dismiss()
                if(currentExercise.possibleSetSize.size > 0){
                    selectedSetSizes = currentExercise.possibleSetSize
                    setSizeMultiselect.text = getStringFromArray(selectedSetSizes)
                }
            }

            // Sets up the button that clears all selected options
            builder.setNeutralButton("Clear"){dialogInterface, i ->
                selectedOptionsArray.clear()
                if(currentExercise.possibleSetSize.size > 0){
                    selectedSetSizes = currentExercise.possibleSetSize
                    setSizeMultiselect.text = getStringFromArray(selectedSetSizes)
                }
            }

            builder.show()
        }
    }


    private fun buildMultiselectRepNo(view:View){
        // Array that keeps track of the options selected, but only by index
        var selectedOptionsArray =  ArrayList<Int>()

        // Triggers the dialog on click
        repSizeMultiselect.setOnClickListener{view ->
            val builder = AlertDialog.Builder(activity)
            builder.setTitle("Rep lengths for Exercise")
            builder.setCancelable(false)

            // Creates the array of bools that indicates whether an option should already be checked
            val preselectedArray = BooleanArray(repSizesArray.size){false}
            for(repSize in selectedRepSizes){
                var itemIndex = repSizesArray.indexOf(repSize)
                preselectedArray[itemIndex] = true
                selectedOptionsArray.add(itemIndex)
            }

            // Shows the options, and chooses what to do when one is selected
            builder.setMultiChoiceItems(repSizesArray, preselectedArray){dialog, which, isChecked ->
                if(isChecked){
                    selectedOptionsArray.add(which)
                } else if(selectedOptionsArray.contains(which)){
                    selectedOptionsArray.remove(Integer.valueOf(which))
                }
            }

            // Sets up button to complete the dialog
            builder.setPositiveButton("Done"){ dialogInterface, i ->
                // Converts the selection of indexes into the the corresponding values
                selectedRepSizes.clear()
                selectedOptionsArray.sort()
                for(j in selectedOptionsArray.indices){
                    selectedRepSizes.add(repSizesArray[selectedOptionsArray[j]])
                }

                // Sets the TextView text to the selected sets
                repSizeMultiselect.text = getStringFromArray(selectedRepSizes)
            }

            // Sets up the button to cancel the input
            builder.setNegativeButton("Cancel"){dialogInterface, i ->
                dialogInterface.dismiss()
                if(currentExercise.possibleRepSize.size > 0){
                    selectedRepSizes = currentExercise.possibleRepSize
                    repSizeMultiselect.text = getStringFromArray(selectedRepSizes)
                }
            }

            // Sets up the button that clears all selected options
            builder.setNeutralButton("Clear"){dialogInterface, i ->
                selectedOptionsArray.clear()
                if(currentExercise.possibleRepSize.size > 0){
                    selectedRepSizes = currentExercise.possibleRepSize
                    repSizeMultiselect.text = getStringFromArray(selectedRepSizes)
                }
            }

            builder.show()
        }
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