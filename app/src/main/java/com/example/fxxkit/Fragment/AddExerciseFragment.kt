package com.example.fxxkit.Fragment

import android.app.AlertDialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.example.fxxkit.DBHandler
import com.example.fxxkit.DataClass.Exercise
import com.example.fxxkit.R
import java.util.*
import kotlin.collections.ArrayList

/**
 * A simple [Fragment] subclass.
 * Use the [AddExerciseFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class AddExerciseFragment : Fragment() {

    private var setNoArray: Array<String> = arrayOf("3", "5", "10", "12", "15")
    val selectedSetNos = ArrayList<String>()

    private var repNoArray: Array<String> = arrayOf("5", "10", "12", "15", "20", "30", "50")
    val selectedRepNos = ArrayList<String>()

    private lateinit var exNameInput: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view =  inflater.inflate(R.layout.fragment_add_exercise, container, false)

        exNameInput = view.findViewById<EditText>(R.id.exercise_name)
        val exSetNosInput = view.findViewById<TextView>(R.id.exercise_set_nos)
        val exRepNosInput = view.findViewById<TextView>(R.id.exercise_rep_nos)


        val showBtn = view.findViewById<Button>(R.id.add_exercise_btn)
        showBtn.setOnClickListener{view ->
            val setNosText = Arrays.toString(selectedSetNos.toTypedArray())
            val repNosText = Arrays.toString(selectedRepNos.toTypedArray())
            val text = exNameInput.text.toString() + ", " + setNosText + ", " + repNosText

            addExercise(view)
        }

        buildMultiselectSetNo(view)
        buildMultiselectRepNo(view)

        return view
    }

    private fun addExercise(view: View){
        val dbHandler = DBHandler(this.requireContext(), null, null, 1)

        val exerciseName = exNameInput.text.toString()

        val exercise = Exercise(exerciseName)

        dbHandler.addExercise(exercise)

        Toast.makeText(activity, "Added exercise to database", Toast.LENGTH_SHORT).show()
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
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.

         * @return A new instance of fragment AddExerciseFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance() =
            AddExerciseFragment().apply {
                arguments = Bundle().apply {
                }
            }
    }
}