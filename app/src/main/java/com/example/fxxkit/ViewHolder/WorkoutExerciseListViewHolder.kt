package com.example.fxxkit.ViewHolder

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.fxxkit.DataClass.WorkoutExercise
import com.example.fxxkit.R

/**
 * Adapter for recycler
 * Displays a list of workout exercises, with bare minimum details
 * Uses:
 *  - workout_list_exercise_row
 */
class WorkoutExerciseListAdapter(private val workExList: ArrayList<WorkoutExercise>) :   RecyclerView.Adapter<WorkoutExerciseListAdapter.WorkoutExerciseListViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WorkoutExerciseListViewHolder {
        val viewLayout = LayoutInflater.from(parent.context).inflate(R.layout.row_item_workout_list_exercise, parent, false)
        return WorkoutExerciseListViewHolder(viewLayout)
    }

    override fun onBindViewHolder(holder: WorkoutExerciseListViewHolder, position: Int) {
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