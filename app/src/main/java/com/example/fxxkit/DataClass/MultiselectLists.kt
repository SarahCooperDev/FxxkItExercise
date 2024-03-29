package com.example.fxxkit.DataClass

import android.app.AlertDialog
import android.view.LayoutInflater
import android.widget.CheckBox
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import com.example.fxxkit.MainActivity
import com.example.fxxkit.R
import kotlin.collections.ArrayList

object MultiselectLists {
    public var setSizesArray: Array<String> = arrayOf("All", "1", "2", "3", "4", "5", "6", "10", "12", "15")
    public var repSizesArray: Array<String> = arrayOf("All", "1", "2", "3", "5", "6", "8", "10", "12", "15", "20", "24", "30", "50")
    public var targettedAreaArray: Array<String> = arrayOf("All", "Calfs", "Quads", "Glutts", "Abs", "Triceps", "Biceps",
        "Ankles", "Knees", "Hips", "Wrists", "Elbows", "Shoulders", "Neck", "Back")


    /**
     *Function that creates a custom dialog showing a list of checkboxes
     *
     *Inputs params:
     *      - activity: MainActivity (used for context, to inflate custom dialog view)
     *      - layoutInflater: LayoutInflater (used to inflate the custom dialog view)
     *      - itemList: Array<String> (the static multiselect list used to construct the checkboxes)
     *      - selectedItems: ArrayList<String> (the list that is loaded with the selected checkbox text when confirming)
     *      - textView: TextView (the element that needs to have its text updated to match the new selected options)
     *
     * Updates the selected items
     */

    fun showDialog(activity: MainActivity, layoutInflater: LayoutInflater, itemList: Array<String>, selectedItems: ArrayList<String>, textView: TextView){
        val checkboxes = ArrayList<CheckBox>()
        val builder = AlertDialog.Builder(activity).create()
        val dialog = layoutInflater.inflate(R.layout.dialog_sets_reps_areas, null)

        val leftLayout = dialog.findViewById<LinearLayout>(R.id.left_layout)
        val rightLayout = dialog.findViewById<LinearLayout>(R.id.right_layout)
        val cancelBtn = dialog.findViewById<ImageButton>(R.id.cancel_btn)
        val doneBtn = dialog.findViewById<ImageButton>(R.id.done_btn)

        var isLeft = true
        for(item in itemList){
            var chkbx = CheckBox(activity)
            chkbx.setText(item)
            chkbx.setPadding(5, 5, 5, 5)
            chkbx.setOnClickListener { view ->
                if(chkbx == checkboxes[0]){
                    for(box in checkboxes){
                        box.isChecked = false
                    }
                    chkbx.isChecked = true
                } else {
                    checkboxes[0].isChecked = false
                }
            }

            if(selectedItems.contains(item)){
                chkbx.isChecked = true
            }

            if(isLeft){
                isLeft = false
                leftLayout.addView(chkbx)
            } else {
                isLeft = true
                rightLayout.addView(chkbx)
            }

            checkboxes.add(chkbx)
        }

        doneBtn.setOnClickListener { view ->
            selectedItems.clear()
            for(box in checkboxes){
                if(box.isChecked){
                    selectedItems.add(box.text.toString())
                }
            }

            var textString = ""
            for(item in selectedItems){
                textString += item + ", "
            }

            textString = textString.dropLast(2)
            textView.setText(textString)

            builder.dismiss()
        }

        cancelBtn.setOnClickListener { view ->
            builder.dismiss()
        }

        builder.setView(dialog)
        builder.setCanceledOnTouchOutside(true)
        builder.show()
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

    private fun getArrayListFromString(text: String): ArrayList<String>{
        var list = ArrayList<String>()
        var arrayList = text.split(", ").toTypedArray()

        for(txt in arrayList){
            list.add(txt)
        }

        return list
    }

    public fun getRepAsArrayList(): ArrayList<String>{
        var list = ArrayList<String>()
        for(item in repSizesArray){
            if(item != "All"){
                list.add(item)
            }
        }
        return list
    }

    public fun getSetAsArrayList(): ArrayList<String>{
        var list = ArrayList<String>()
        for(item in setSizesArray){
            if(item != "All"){
                list.add(item)
            }
        }
        return list
    }
}