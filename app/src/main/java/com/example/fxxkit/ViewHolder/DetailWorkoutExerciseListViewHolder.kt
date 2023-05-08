package com.example.fxxkit.ViewHolder

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.example.fxxkit.DataClass.WorkoutExercise
import com.example.fxxkit.R

class DetailWorkoutExerciseListAdapter(private val workExList: ArrayList<WorkoutExercise>) : RecyclerView.Adapter<DetailWorkoutExerciseListAdapter.DetailWorkoutExerciseListViewHolder>(){
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DetailWorkoutExerciseListAdapter.DetailWorkoutExerciseListViewHolder {
        val viewLayout = LayoutInflater.from(parent.context).inflate(
            R.layout.workout_exercise_detail_row, parent, false)
        return DetailWorkoutExerciseListAdapter.DetailWorkoutExerciseListViewHolder(viewLayout)
    }

    override fun onBindViewHolder(holder: DetailWorkoutExerciseListAdapter.DetailWorkoutExerciseListViewHolder, position: Int) {
        println("Exercise length in holder: " + workExList.size.toString())
        val currentExercise = workExList[position]

        println("Current exercise muscles size ${currentExercise.exercise!!.targettedMuscles.size.toString() }")

        holder.id.text = currentExercise.exercise!!.id.toString()
        holder.name.text = currentExercise.exercise!!.name.toString()
        holder.sets.text = currentExercise.setSize
        holder.reps.text = currentExercise.repSize
        holder.muscles.text = currentExercise.exercise!!.getMusclesAsString()
        holder.strength.text = currentExercise.exercise!!.isStrengthening.toString()
        holder.condition.text = currentExercise.exercise!!.isConditioning.toString()
    }

    override fun getItemCount(): Int {
        return workExList.size
    }

    class DetailWorkoutExerciseListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val row: CardView = itemView.findViewById(R.id.workout_exercise_row_item)
        val id: TextView = itemView.findViewById(R.id.exercise_id_txt)
        val name: TextView = itemView.findViewById(R.id.exercise_name_txt)
        val sets: TextView = itemView.findViewById(R.id.set_list_txt)
        val reps: TextView = itemView.findViewById(R.id.rep_list_txt)
        val muscles: TextView = itemView.findViewById(R.id.muscle_list_txt)
        val strength: TextView = itemView.findViewById(R.id.is_strength_txt)
        val condition: TextView = itemView.findViewById(R.id.is_condition_txt)
    }
}