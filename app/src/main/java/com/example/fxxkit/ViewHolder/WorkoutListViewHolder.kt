package com.example.fxxkit.ViewHolder

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.fxxkit.R
import com.example.fxxkit.ViewModel.ExerciseViewModel
import com.example.fxxkit.ViewModel.WorkoutViewModel

class WorkoutListAdapter(private val eList: List<WorkoutViewModel>) :   RecyclerView.Adapter<WorkoutListAdapter.WorkoutListViewHolder>(){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WorkoutListViewHolder {
        val viewLayout = LayoutInflater.from(parent.context).inflate(
            R.layout.workout_row_item, parent, false)
        return WorkoutListViewHolder(viewLayout)
    }

    override fun onBindViewHolder(holder: WorkoutListViewHolder, position: Int) {
        val currentWorkout = eList[position]
        holder.name.text = currentWorkout.name
    }

    override fun getItemCount(): Int {
        return eList.size
    }

    class WorkoutListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val name: TextView = itemView.findViewById(R.id.workout_name)
    }
}