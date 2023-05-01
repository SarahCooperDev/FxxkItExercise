package com.example.fxxkit

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.example.fxxkit.DataClass.Exercise

class WorkoutExerciseListAdapter(private val eList: List<Exercise>, var selectedExercises: ArrayList<Exercise>) :   RecyclerView.Adapter<WorkoutExerciseListAdapter.WorkoutExerciseListViewHolder>(){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WorkoutExerciseListViewHolder {
        val viewLayout = LayoutInflater.from(parent.context).inflate(
            R.layout.exercise_row_item, parent, false)
        return WorkoutExerciseListViewHolder(viewLayout)
    }

    override fun onBindViewHolder(holder: WorkoutExerciseListViewHolder, position: Int) {
        val currentExercise = eList[position]
        holder.id.text = currentExercise.id.toString()
        holder.name.text = currentExercise.name
        holder.isStrength.text = currentExercise.isStrengthening.toString()
        holder.isCondition.text = currentExercise.isConditioning.toString()
        holder.setList.text = currentExercise.getSetAsString()
        holder.repList.text = currentExercise.getRepsAsString()
        holder.muscleList.text = currentExercise.getMusclesAsString()

        holder.row.setOnClickListener{
            if(holder.isSelected.isChecked){
                holder.isSelected.setChecked(false)
                selectedExercises = selectedExercises.filter{ it.id.toString() != holder.id.text.toString() } as ArrayList<Exercise>
            } else{
                holder.isSelected.setChecked(true)
                selectedExercises.add(Exercise(holder.id.text.toString().toInt(), holder.name.text.toString()))
            }
        }

        holder.isSelected.setOnClickListener{
            if(holder.isSelected.isChecked){
                selectedExercises.add(Exercise(holder.id.text.toString().toInt(), holder.name.text.toString()))
            } else {
                selectedExercises = selectedExercises.filter{ it.id.toString() != holder.id.text.toString() } as ArrayList<Exercise>
            }
        }
    }


    override fun getItemCount(): Int {
        return eList.size
    }

    class WorkoutExerciseListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val row: CardView = itemView.findViewById(R.id.exercise_row_item)
        val id: TextView = itemView.findViewById(R.id.exercise_id_txt)
        val name: TextView = itemView.findViewById(R.id.exercise_name)
        val isSelected: CheckBox = itemView.findViewById(R.id.is_selected_chbx)
        val isStrength: TextView = itemView.findViewById(R.id.is_strength_txt)
        val isCondition: TextView = itemView.findViewById(R.id.is_condition_txt)
        val setList: TextView = itemView.findViewById(R.id.set_list_txt)
        val repList: TextView = itemView.findViewById(R.id.rep_list_txt)
        val muscleList: TextView = itemView.findViewById(R.id.muscle_list_txt)
    }
}

