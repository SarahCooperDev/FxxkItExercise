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
        holder.duration.text = currentExercise.duration.toString()
    }

    override fun getItemCount(): Int {
        return eList.size
    }

    class ExerciseListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val id: TextView = itemView.findViewById(R.id.exercise_id_txt)
        val name: TextView = itemView.findViewById(R.id.exercise_name)
        val duration: TextView = itemView.findViewById(R.id.exercise_duration)

    }
}

