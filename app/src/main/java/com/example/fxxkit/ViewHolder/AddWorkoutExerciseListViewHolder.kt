package com.example.fxxkit

import android.app.AlertDialog
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.example.fxxkit.DataClass.MultiselectLists
import com.example.fxxkit.DataClass.WorkoutExercise

/**
 * Adapter for recycler
 * Displays a list of WorkoutExercises, that are selectable, and trigger set/rep dialog on click
 * Uses:
 *  - workout_exercise_row_item
 */
class AddWorkoutExerciseListAdapter(private val activity: MainActivity, private val workExList: ArrayList<WorkoutExercise>) :   RecyclerView.Adapter<AddWorkoutExerciseListAdapter.AddWorkoutExerciseListViewHolder>(){
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AddWorkoutExerciseListViewHolder {
        val viewLayout = LayoutInflater.from(parent.context).inflate(R.layout.row_item_workout_exercise, parent, false)
        return AddWorkoutExerciseListViewHolder(viewLayout)
    }

    override fun onBindViewHolder(holder: AddWorkoutExerciseListViewHolder, position: Int) {
        val currentExercise = workExList[position]

        holder.id.text = currentExercise.exercise!!.id.toString()
        holder.name.text = currentExercise.exercise!!.name
        holder.description.text = currentExercise.exercise!!.description
        holder.isStrength.text = currentExercise.exercise!!.isStrengthening.toString()
        holder.isCondition.text = currentExercise.exercise!!.isConditioning.toString()
        holder.areaList.text = currentExercise.exercise!!.getAreasAsString()
        holder.repTime.text = currentExercise.exercise!!.repTime.toString()

        // If user has selected it, it will show the chosen set/rep size
        if(currentExercise.isSelected){
            holder.setList.text = currentExercise.setSize
            holder.repList.text = currentExercise.repSize
            holder.isSelected.setChecked(true)
        } else {
            holder.setList.text = currentExercise.exercise!!.getSetAsString()
            holder.repList.text = currentExercise.exercise!!.getRepsAsString()
            holder.isSelected.setChecked(false)
        }

        holder.row.setOnClickListener{
            if(holder.isSelected.isChecked){
                holder.isSelected.setChecked(false)
                currentExercise.isSelected = false
                holder.setList.text = currentExercise.exercise!!.getSetAsString()
                holder.repList.text = currentExercise.exercise!!.getRepsAsString()
            } else{
                buildWorkoutExerciseDialog(currentExercise, holder)
            }
        }

        holder.isSelected.setOnClickListener{
            if(holder.isSelected.isChecked){
                buildWorkoutExerciseDialog(currentExercise, holder)
            } else {
                currentExercise.isSelected = false
                holder.setList.text = currentExercise.exercise!!.getSetAsString()
                holder.repList.text = currentExercise.exercise!!.getRepsAsString()
            }
        }
    }

    /**
     * Builds and shows the dialog to select the sets/reps for a workout exercise
     */
    private fun buildWorkoutExerciseDialog(currentExercise: WorkoutExercise, holder: AddWorkoutExerciseListViewHolder){
        val builder = AlertDialog.Builder(activity)
        builder.setTitle(activity.baseContext.getString(R.string.dialog_chose_sets_reps))
        builder.setCancelable(false)

        val inflater: LayoutInflater = activity.getLayoutInflater()
        var view = inflater.inflate(R.layout.dialog_set_and_rep, null)
        var setRadioGrp = view.findViewById<RadioGroup>(R.id.set_radiogrp)
        var repRadioGrp = view.findViewById<RadioGroup>(R.id.rep_radiogrp)

        // Shows the sets available for the exercise, as a selectable list
        if(currentExercise.exercise != null && currentExercise.exercise!!.possibleSetSize != null && currentExercise.exercise!!.possibleSetSize.size > 0){
            if(currentExercise.exercise!!.possibleSetSize[0] == MultiselectLists.setSizesArray[0]){
                for(i in 1 .. MultiselectLists.setSizesArray.size - 1){
                    var set = MultiselectLists.setSizesArray[i]
                    var radiobtn = RadioButton(activity)
                    radiobtn.setText(set)
                    setRadioGrp.addView(radiobtn)
                }
            } else {
                for(set in currentExercise.exercise!!.possibleSetSize){
                    var radiobtn = RadioButton(activity)
                    radiobtn.setText(set)
                    setRadioGrp.addView(radiobtn)
                }
            }
        }

        // Shows the reps available for an exercise, as a selectable list
        if(currentExercise.exercise != null && currentExercise.exercise!!.possibleRepSize != null && currentExercise.exercise!!.possibleRepSize.size > 0){
            if(currentExercise.exercise!!.possibleRepSize[0] == MultiselectLists.repSizesArray[0]){
                for(i in 1 .. MultiselectLists.repSizesArray.size - 1){
                    var rep = MultiselectLists.repSizesArray[i]
                    var radiobtn = RadioButton(activity)
                    radiobtn.setText(rep)
                    repRadioGrp.addView(radiobtn)
                }
            } else {
                for (rep in currentExercise.exercise!!.possibleRepSize) {
                    var radioBtn = RadioButton(activity)
                    radioBtn.setText(rep)
                    repRadioGrp.addView(radioBtn)
                }
            }
        }

        setRadioGrp.setOnCheckedChangeListener(RadioGroup.OnCheckedChangeListener{ group, checkedId ->
            val radio: RadioButton = view.findViewById(checkedId)
            currentExercise.setSize = radio.text.toString()
            holder.setList.text = radio.text.toString()
        })

        repRadioGrp.setOnCheckedChangeListener(RadioGroup.OnCheckedChangeListener{ group, checkedId ->
            val radio: RadioButton = view.findViewById(checkedId)
            currentExercise.repSize = radio.text.toString()
            holder.repList.text = radio.text.toString()
        })

        builder.setView(view)

        // Sets up button to complete the dialog
        builder.setPositiveButton(activity.baseContext.getString(R.string.done_txt)) { dialogInterface, i ->
            if(currentExercise.repSize != null && currentExercise.setSize != null){
                holder.isSelected.setChecked(true)
                currentExercise.isSelected = true
            } else {
                Toast.makeText(activity.baseContext, activity.baseContext.getString(R.string.error_set_rep_missing), Toast.LENGTH_LONG)
            }
            dialogInterface.dismiss()
        }

        // Sets up the button to cancel the input
        builder.setNegativeButton(activity.baseContext.getString(R.string.cancel_txt)) { dialogInterface, i ->
            dialogInterface.dismiss()
        }

        builder.show()
    }


    override fun getItemCount(): Int {
        return workExList.size
    }

    class AddWorkoutExerciseListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val row: CardView = itemView.findViewById(R.id.exercise_row_item)
        val id: TextView = itemView.findViewById(R.id.exercise_id_txt)
        val name: TextView = itemView.findViewById(R.id.exercise_name)
        val description: TextView = itemView.findViewById(R.id.description_txt)
        val isSelected: CheckBox = itemView.findViewById(R.id.is_selected_chbx)
        val isStrength: TextView = itemView.findViewById(R.id.is_strength_txt)
        val isCondition: TextView = itemView.findViewById(R.id.is_condition_txt)
        val setList: TextView = itemView.findViewById(R.id.set_list_txt)
        val repList: TextView = itemView.findViewById(R.id.rep_list_txt)
        val repTime: TextView = itemView.findViewById(R.id.rep_time_txt)
        val areaList: TextView = itemView.findViewById(R.id.area_list_txt)
    }
}

