package com.example.fxxkit

import android.service.autofill.Dataset
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.example.fxxkit.DataClass.Exercise
import com.example.fxxkit.ViewModel.ExerciseViewModel

class WorkoutExerciseListAdapter(private val eList: List<ExerciseViewModel>, var selectedExercises: ArrayList<ExerciseViewModel>) :   RecyclerView.Adapter<WorkoutExerciseListAdapter.WorkoutExerciseListViewHolder>(){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WorkoutExerciseListViewHolder {
        val viewLayout = LayoutInflater.from(parent.context).inflate(
            R.layout.workout_exercise_row_item, parent, false)
        return WorkoutExerciseListViewHolder(viewLayout)
    }

    override fun onBindViewHolder(holder: WorkoutExerciseListViewHolder, position: Int) {
        val currentExercise = eList[position]
        holder.id.text = currentExercise.id.toString()
        holder.name.text = currentExercise.name
        holder.duration.text = currentExercise.duration.toString()

        holder.row.setOnClickListener{
            if(holder.isSelected.isChecked){
                holder.isSelected.setChecked(false)
                selectedExercises = selectedExercises.filter{ it.id.toString() != holder.id.text.toString() } as ArrayList<ExerciseViewModel>
            } else{
                holder.isSelected.setChecked(true)
                selectedExercises.add(ExerciseViewModel(holder.id.text.toString().toInt(), holder.name.text.toString(), -1))
            }
        }

        holder.isSelected.setOnClickListener{
            if(holder.isSelected.isChecked){
                selectedExercises.add(ExerciseViewModel(holder.id.text.toString().toInt(), holder.name.text.toString(), -1))
            } else {
                selectedExercises = selectedExercises.filter{ it.id.toString() != holder.id.text.toString() } as ArrayList<ExerciseViewModel>
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
        val duration: TextView = itemView.findViewById(R.id.exercise_duration)
        val isSelected: CheckBox = itemView.findViewById(R.id.is_selected_chbx)
    }
}

