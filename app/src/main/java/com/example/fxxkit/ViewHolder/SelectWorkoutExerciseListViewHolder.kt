package com.example.fxxkit.ViewHolder

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.example.fxxkit.DataClass.WorkoutExercise
import com.example.fxxkit.MainActivity
import com.example.fxxkit.R

class SelectWorkoutExerciseListAdapter(private val activity: MainActivity, private val workExList: ArrayList<WorkoutExercise>) :   RecyclerView.Adapter<SelectWorkoutExerciseListAdapter.SelectWorkoutExerciseListViewHolder>(){
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SelectWorkoutExerciseListViewHolder {
        val viewLayout = LayoutInflater.from(parent.context).inflate(
            R.layout.workout_exercise_row_item, parent, false)
        return SelectWorkoutExerciseListViewHolder(viewLayout)
    }

    override fun onBindViewHolder(holder: SelectWorkoutExerciseListViewHolder, position: Int) {
        val currentExercise = workExList[position]

        holder.id.text = currentExercise.exercise!!.id.toString()
        holder.name.text = currentExercise.exercise!!.name
        holder.description.text = currentExercise.exercise!!.description
        holder.isStrength.text = currentExercise.exercise!!.isStrengthening.toString()
        holder.isCondition.text = currentExercise.exercise!!.isConditioning.toString()
        holder.areaList.text = currentExercise.exercise!!.getAreasAsString()
        holder.setList.text = currentExercise.exercise!!.getSetAsString()
        holder.repList.text = currentExercise.exercise!!.getRepsAsString()

        if(currentExercise.isSelected){
            holder.isSelected.setChecked(true)
        } else {
            holder.isSelected.setChecked(false)
        }

        holder.repTime.text = currentExercise.exercise!!.repTime.toString()

        holder.row.setOnClickListener{
            if(holder.isSelected.isChecked){
                holder.isSelected.setChecked(false)
                currentExercise.isSelected = false
            } else{
                holder.isSelected.setChecked(true)
                currentExercise.isSelected = true
            }
        }

        holder.isSelected.setOnClickListener{
            if(holder.isSelected.isChecked){
                currentExercise.isSelected = true
            } else {
                currentExercise.isSelected = false
            }
        }
    }

    override fun getItemCount(): Int {
        return workExList.size
    }

    class SelectWorkoutExerciseListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
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
        val areaList: TextView = itemView.findViewById(R.id.area_list_txt)
    }
}