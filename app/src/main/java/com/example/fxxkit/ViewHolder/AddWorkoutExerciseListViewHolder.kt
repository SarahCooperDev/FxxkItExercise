package com.example.fxxkit

import android.app.AlertDialog
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.example.fxxkit.DataClass.WorkoutExercise

class AddWorkoutExerciseListAdapter(private val activity: MainActivity, private val workExList: ArrayList<WorkoutExercise>) :   RecyclerView.Adapter<AddWorkoutExerciseListAdapter.AddWorkoutExerciseListViewHolder>(){
    var dialogTitleString = "Specify sets and reps for this workout"

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AddWorkoutExerciseListViewHolder {
        val viewLayout = LayoutInflater.from(parent.context).inflate(
            R.layout.workout_exercise_row_item, parent, false)
        return AddWorkoutExerciseListViewHolder(viewLayout)
    }

    override fun onBindViewHolder(holder: AddWorkoutExerciseListViewHolder, position: Int) {
        val currentExercise = workExList[position]
        println("Current exercise: ${currentExercise.exercise!!.name}, is selected - ${currentExercise.isSelected.toString()}")

        holder.id.text = currentExercise.exercise!!.id.toString()
        holder.name.text = currentExercise.exercise!!.name
        holder.description.text = currentExercise.exercise!!.description

        holder.isStrength.text = currentExercise.exercise!!.isStrengthening.toString()
        holder.isCondition.text = currentExercise.exercise!!.isConditioning.toString()
        holder.muscleList.text = currentExercise.exercise!!.getMusclesAsString()

        if(currentExercise.isSelected){
            holder.setList.text = currentExercise.setSize
            holder.repList.text = currentExercise.repSize
            holder.isSelected.setChecked(true)
        } else {
            holder.setList.text = currentExercise.exercise!!.getSetAsString()
            holder.repList.text = currentExercise.exercise!!.getRepsAsString()
            holder.isSelected.setChecked(false)
        }

        holder.repTime.text = currentExercise.exercise!!.repTime.toString()

        holder.row.setOnClickListener{
            if(holder.isSelected.isChecked){
                holder.isSelected.setChecked(false)
                currentExercise.isSelected = false
                holder.setList.text = currentExercise.exercise!!.getSetAsString()
                holder.repList.text = currentExercise.exercise!!.getRepsAsString()
            } else{
                holder.isSelected.setChecked(true)
                buildWorkoutExerciseDialog(currentExercise, holder)
                currentExercise.isSelected = true
            }
        }

        holder.isSelected.setOnClickListener{
            if(holder.isSelected.isChecked){
                buildWorkoutExerciseDialog(currentExercise, holder)
                currentExercise.isSelected = true
            } else {
                currentExercise.isSelected = false
                holder.setList.text = currentExercise.exercise!!.getSetAsString()
                holder.repList.text = currentExercise.exercise!!.getRepsAsString()
            }
        }
    }

    private fun swapList(workExercise: WorkoutExercise, position: Int){
        workExList.removeAt(position)
        notifyItemRemoved(position)
        notifyItemRangeChanged(position, workExList.size)
    }

    private fun buildWorkoutExerciseDialog(currentExercise: WorkoutExercise, holder: AddWorkoutExerciseListViewHolder){
        val builder = AlertDialog.Builder(activity)
        builder.setTitle(dialogTitleString)
        builder.setCancelable(false)

        val inflater: LayoutInflater = activity.getLayoutInflater()
        // Shows the options, and chooses what to do when one is selected
        var view = inflater.inflate(R.layout.custom_workout_exercise_dialog, null)
        var setRadioGrp = view.findViewById<RadioGroup>(R.id.set_radiogrp)
        var repRadioGrp = view.findViewById<RadioGroup>(R.id.rep_radiogrp)

        if(currentExercise.exercise != null && currentExercise.exercise!!.possibleSetSize != null && currentExercise.exercise!!.possibleSetSize.size > 0){
            for(set in currentExercise.exercise!!.possibleSetSize){
                var radiobtn = RadioButton(activity)
                radiobtn.setText(set)
                setRadioGrp.addView(radiobtn)
            }
        }

        if(currentExercise.exercise != null && currentExercise.exercise!!.possibleRepSize != null && currentExercise.exercise!!.possibleRepSize.size > 0){
            for(rep in currentExercise.exercise!!.possibleRepSize){
                var radioBtn = RadioButton(activity)
                radioBtn.setText(rep)
                repRadioGrp.addView(radioBtn)
            }
        }

        setRadioGrp.setOnCheckedChangeListener(RadioGroup.OnCheckedChangeListener{ group, checkedId ->
            val radio: RadioButton = view.findViewById(checkedId)
            Toast.makeText(activity," On checked change :" + " ${radio.text}", Toast.LENGTH_SHORT).show()
            currentExercise.setSize = radio.text.toString()
            holder.setList.text = radio.text.toString()
        })

        repRadioGrp.setOnCheckedChangeListener(RadioGroup.OnCheckedChangeListener{ group, checkedId ->
            val radio: RadioButton = view.findViewById(checkedId)
            Toast.makeText(activity," On checked change :" + " ${radio.text}", Toast.LENGTH_SHORT).show()
            currentExercise.repSize = radio.text.toString()
            holder.repList.text = radio.text.toString()
        })

        builder.setView(view)

        // Sets up button to complete the dialog
        builder.setPositiveButton("Done") { dialogInterface, i ->
            println("Done in workout exercise")
        }

        // Sets up the button to cancel the input
        builder.setNegativeButton("Cancel") { dialogInterface, i ->
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
        val muscleList: TextView = itemView.findViewById(R.id.muscle_list_txt)
    }
}

