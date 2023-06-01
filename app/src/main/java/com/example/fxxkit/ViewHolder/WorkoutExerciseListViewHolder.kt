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
        holder.idTxt.text = currentExercise.exercise!!.id.toString()
        holder.nameTxt.text = currentExercise.exercise!!.name.toString()
        holder.setTxt.text = currentExercise.setSize
        holder.repTxt.text = currentExercise.repSize
        holder.repTimeTxt.text = currentExercise.exercise!!.repTime.toString()
        holder.needsBothTxt.text = currentExercise.exercise!!.needsBothSides.toString()
        holder.totalTimeTxt.text = currentExercise.getTotalTimeString()
    }

    override fun getItemCount(): Int {
        return workExList.size
    }

    class WorkoutExerciseListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val idTxt: TextView = itemView.findViewById(R.id.exercise_id_txt)
        val nameTxt: TextView = itemView.findViewById(R.id.exercise_name_txt)
        val setTxt: TextView = itemView.findViewById(R.id.set_txt)
        val repTxt: TextView = itemView.findViewById(R.id.rep_txt)
        val needsBothTxt: TextView = itemView.findViewById(R.id.needs_both_txt)
        val repTimeTxt: TextView = itemView.findViewById(R.id.rep_time_txt)
        val totalTimeTxt: TextView = itemView.findViewById(R.id.total_time_txt)
    }
}