package com.example.fxxkit.DataClass

import android.app.AlertDialog
import android.view.View
import android.widget.TextView
import com.example.fxxkit.MainActivity
import java.util.*
import kotlin.collections.ArrayList

object MultiselectLists {
    public var setSizesArray: Array<String> = arrayOf("3", "5", "10", "12", "15")
    public var repSizesArray: Array<String> = arrayOf("5", "10", "12", "15", "20", "30", "50")
    public var targettedMusclesArray: Array<String> = arrayOf("Calfs", "Quads", "Glutts", "Abs", "Triceps", "Biceps")

    private var setDialogString = "Possible number of sets for the exercise"
    private var repDialogString = "Possible number of reps per set"
    private var muscleDialogString = "Targetted muscles for exercise"

    /**
     * Function that creates a multiselect dialog upon clicking an element
     *
     * Inputs params: view - the current view context
     *
     * Updates the selectedSetNos array
     */
    public fun buildMultiselect(activity: MainActivity, view: View, clickedTxtVw: TextView,
                                 staticSelectionArray: Array<String>, selectedOptions: ArrayList<String>) {
        // Array that keeps track of the options selected, but only by index
        var selectedIndexArray = ArrayList<Int>()

        // Triggers the dialog on click
        clickedTxtVw.setOnClickListener { view ->
            val builder = AlertDialog.Builder(activity)
            var titleString = "Title"

            when(staticSelectionArray){
                setSizesArray -> titleString = setDialogString
                repSizesArray -> titleString = repDialogString
                targettedMusclesArray -> titleString = muscleDialogString
            }
            builder.setTitle(titleString)
            builder.setCancelable(false)

            // Creates the array of bools that indicates whether an option should already be checked
            val preselectedArray = BooleanArray(staticSelectionArray.size) { false }
            for (option in selectedOptions) {
                var optionIndex = staticSelectionArray.indexOf(option)
                preselectedArray[optionIndex] = true
                selectedIndexArray.add(optionIndex)
            }

            // Shows the options, and chooses what to do when one is selected
            builder.setMultiChoiceItems(staticSelectionArray, preselectedArray) { dialog, which, isChecked ->
                if (isChecked) {
                    selectedIndexArray.add(which)
                } else if (selectedIndexArray.contains(which)) {
                    selectedIndexArray.remove(Integer.valueOf(which))
                }
            }

            // Sets up button to complete the dialog
            builder.setPositiveButton("Done") { dialogInterface, i ->
                // Converts the selection of indexes into the the corresponding values
                selectedOptions.clear()
                selectedIndexArray.sort()
                for (j in selectedIndexArray.indices) {
                    selectedOptions.add(staticSelectionArray[selectedIndexArray[j]])
                }

                // Sets the TextView text to the selected sets
                clickedTxtVw.text = getStringFromArray(selectedOptions)
                selectedIndexArray.clear()
            }

            // Sets up the button to cancel the input
            builder.setNegativeButton("Cancel") { dialogInterface, i ->
                dialogInterface.dismiss()
            }

            // Sets up the button that clears all selected options
            builder.setNeutralButton("Clear") { dialogInterface, i ->
                selectedIndexArray.clear()
                selectedOptions.clear()
            }

            builder.show()
        }
    }

    public fun getStringFromArray(array: ArrayList<String>): String?{
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

    private fun getStringFromBoolArray(array: BooleanArray): String?{
        if(array.size > 0){
            var stringgedArray = ""
            for(i in 0..array.size - 2){
                stringgedArray += array[i].toString() + ", "
            }
            stringgedArray += array[array.size - 1]

            return stringgedArray
        }
        return null
    }
}