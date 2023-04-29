package com.example.fxxkit

import android.service.autofill.Dataset
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.fxxkit.ViewModel.ExerciseViewModel

class ExerciseListAdapter(private val eList: List<ExerciseViewModel>) :   RecyclerView.Adapter<ExerciseListAdapter.ExerciseListViewHolder>(){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ExerciseListViewHolder {
        val viewLayout = LayoutInflater.from(parent.context).inflate(
            R.layout.exercise_row_item, parent, false)
        return ExerciseListViewHolder(viewLayout)
    }

    override fun onBindViewHolder(holder: ExerciseListViewHolder, position: Int) {
        val currentExercise = eList[position]
        holder.id.text = currentExercise.id.toString()
        holder.name.text = currentExercise.name
        holder.isStrength.text = currentExercise.isStrength.toString()
        holder.isCondition.text = currentExercise.isCondition.toString()
        holder.setList.text = currentExercise.possibleSetSize
        holder.repList.text = currentExercise.possibleRepSize
        holder.muscleList.text = currentExercise.targettedMuscles
    }

    override fun getItemCount(): Int {
        return eList.size
    }

    class ExerciseListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val id: TextView = itemView.findViewById(R.id.exercise_id_txt)
        val name: TextView = itemView.findViewById(R.id.exercise_name)
        val isStrength: TextView = itemView.findViewById(R.id.is_strength_txt)
        val isCondition: TextView = itemView.findViewById(R.id.is_condition_txt)
        val setList: TextView = itemView.findViewById(R.id.set_list_txt)
        val repList: TextView = itemView.findViewById(R.id.rep_list_txt)
        val muscleList: TextView = itemView.findViewById(R.id.muscle_list_txt)

    }
}

