package com.example.fxxkit.ViewHolder

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TableRow
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.example.fxxkit.DataClass.WorkoutExercise
import com.example.fxxkit.R

/**
 * Adapter for recycler
 * Displays a list of WorkoutExercises for a given workout
 * Uses:
 *  - workout_exercise_detail_row_item
 */
class DetailWorkoutExerciseListAdapter(private val workExList: ArrayList<WorkoutExercise>) : RecyclerView.Adapter<DetailWorkoutExerciseListAdapter.DetailWorkoutExerciseListViewHolder>(){
    private var showDetails: Boolean = false
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DetailWorkoutExerciseListViewHolder {
        val viewLayout = LayoutInflater.from(parent.context).inflate(R.layout.workout_exercise_detail_row, parent, false)
        return DetailWorkoutExerciseListViewHolder(viewLayout)
    }

    override fun onBindViewHolder(holder: DetailWorkoutExerciseListViewHolder, position: Int) {
        val currentExercise = workExList[position]

        holder.id.text = currentExercise.exercise!!.id.toString()
        holder.name.text = currentExercise.exercise!!.name.toString()
        holder.description.text = currentExercise.exercise!!.description.toString()
        holder.sets.text = currentExercise.setSize
        holder.reps.text = currentExercise.repSize
        holder.repTime.text = currentExercise.exercise!!.repTime.toString() + "s"
        holder.areas.text = currentExercise.exercise!!.getAreasAsString()
        holder.strength.text = currentExercise.exercise!!.isStrengthening.toString()
        holder.condition.text = currentExercise.exercise!!.isConditioning.toString()

        // Displays time as minutes and seconds
        var totalTime = currentExercise.getTotalTimeInSecs()
        if(totalTime != null){
            var minutes = totalTime/60
            var minutesString = minutes.toString()
            if(minutes < 1){
                minutesString = "0"
            }
            var seconds = totalTime%60
            var secondsString = seconds.toString()
            if(seconds < 10 && seconds > 0){
                secondsString = "0" + seconds.toString()
            } else if(seconds < 10){
                secondsString = "00"
            }
            holder.totalTime.text = minutesString + ":" + secondsString + "m"
        } else {
            holder.totalTime.text = "N/A"
        }

        showDetails = true
        toggleVisibility(holder)

        holder.row.setOnClickListener {
            toggleVisibility(holder)
        }
    }

    /**
     * Shows more, when clicked
     */
    private fun toggleVisibility(holder: DetailWorkoutExerciseListViewHolder){
        if(showDetails){
            showDetails = false
            holder.description.visibility = View.GONE
            holder.areaRow.visibility = View.GONE
            holder.strengthRow.visibility = View.GONE
            holder.conditionRow.visibility = View.GONE
        } else {
            showDetails = true
            holder.description.visibility = View.VISIBLE
            holder.areaRow.visibility = View.VISIBLE
            holder.strengthRow.visibility = View.VISIBLE
            holder.conditionRow.visibility = View.VISIBLE
        }
    }

    override fun getItemCount(): Int {
        return workExList.size
    }

    class DetailWorkoutExerciseListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val row: CardView = itemView.findViewById(R.id.workout_exercise_row_item)
        val areaRow: TableRow = itemView.findViewById(R.id.areas_row)
        val strengthRow: TableRow = itemView.findViewById(R.id.strength_row)
        val conditionRow: TableRow = itemView.findViewById(R.id.condition_row)
        val id: TextView = itemView.findViewById(R.id.exercise_id_txt)
        val name: TextView = itemView.findViewById(R.id.exercise_name_txt)
        val description: TextView = itemView.findViewById(R.id.description_txt)
        val sets: TextView = itemView.findViewById(R.id.set_list_txt)
        val reps: TextView = itemView.findViewById(R.id.rep_list_txt)
        val repTime: TextView = itemView.findViewById(R.id.rep_time_txt)
        val totalTime: TextView = itemView.findViewById(R.id.total_time_txt)
        val areas: TextView = itemView.findViewById(R.id.area_list_txt)
        val strength: TextView = itemView.findViewById(R.id.is_strength_txt)
        val condition: TextView = itemView.findViewById(R.id.is_condition_txt)
    }
}