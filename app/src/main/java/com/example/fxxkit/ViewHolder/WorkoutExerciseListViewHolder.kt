package com.example.fxxkit.ViewHolder

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.example.fxxkit.AddWorkoutExerciseListAdapter
import com.example.fxxkit.DataClass.WorkoutExercise
import com.example.fxxkit.R

class WorkoutExerciseListAdapter(private val workExList: ArrayList<WorkoutExercise>) :   RecyclerView.Adapter<WorkoutExerciseListAdapter.WorkoutExerciseListViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WorkoutExerciseListAdapter.WorkoutExerciseListViewHolder {
        val viewLayout = LayoutInflater.from(parent.context).inflate(
            R.layout.workout_list_exercise_row, parent, false)
        return WorkoutExerciseListAdapter.WorkoutExerciseListViewHolder(viewLayout)
    }

    override fun onBindViewHolder(holder: WorkoutExerciseListAdapter.WorkoutExerciseListViewHolder, position: Int) {
        println("Exercise length in holder: " + workExList.size.toString())
        val currentExercise = workExList[position]

        holder.name.text = currentExercise.exercise!!.name.toString()
        holder.sets.text = currentExercise.setSize
        holder.reps.text = currentExercise.repSize
    }

    override fun getItemCount(): Int {
        return workExList.size
    }

    class WorkoutExerciseListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val name: TextView = itemView.findViewById(R.id.exercise_name_txt)
        val sets: TextView = itemView.findViewById(R.id.exercise_set_txt)
        val reps: TextView = itemView.findViewById(R.id.exercise_rep_text)
    }
}