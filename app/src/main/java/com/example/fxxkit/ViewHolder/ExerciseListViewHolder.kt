package com.example.fxxkit

import android.app.AlertDialog
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.fxxkit.DataClass.Exercise
import com.example.fxxkit.DataClass.MultiselectLists
import com.example.fxxkit.DataClass.WorkoutExercise
import com.example.fxxkit.ViewHolder.WorkoutSelectListAdapter
import com.example.fxxkit.ViewModel.WorkoutViewModel

/**
 * Adapter for recycler
 * Displays a list of exercises
 * Uses:
 *  - row_item_exercise
 */
class ExerciseListAdapter(private val eList: ArrayList<Exercise>, private val workoutList: ArrayList<WorkoutViewModel>, private val activity: MainActivity) :   RecyclerView.Adapter<ExerciseListAdapter.ExerciseListViewHolder>(){
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ExerciseListViewHolder {
        val viewLayout = LayoutInflater.from(parent.context).inflate(R.layout.row_item_exercise, parent, false)
        return ExerciseListViewHolder(viewLayout)
    }

    override fun onBindViewHolder(holder: ExerciseListViewHolder, position: Int) {
        val currentExercise = eList[position]
        holder.id.text = currentExercise.id.toString()
        holder.name.text = currentExercise.name
        holder.description.text = currentExercise.description
        if(currentExercise.isStrengthening){ holder.isStrength.text = activity.baseContext.getString(R.string.is_strength_txt) }
        else{ holder.isStrength.text = activity.baseContext.getString(R.string.isnt_strength_txt) }
        if(currentExercise.isConditioning){ holder.isCondition.text = activity.baseContext.getString(R.string.is_condition_txt) }
        else{ holder.isCondition.text = activity.baseContext.getString(R.string.isnt_condition_txt) }
        holder.setList.text = currentExercise.getSetAsString()
        holder.repList.text = currentExercise.getRepsAsString()
        holder.repTime.text = currentExercise.repTime.toString()
        holder.needsBothTxt.text = currentExercise.needsBothSides.toString()
        holder.areaList.text = currentExercise.getAreasAsString()

        if(currentExercise.tags.size > 0){ holder.tagTxt.text = currentExercise.getTagDisplayString()
        } else { holder.tagTxt.text = activity.baseContext.getString(R.string.none_txt) }

        holder.editBtn.setOnClickListener{view -> activity.navToEditExercise(currentExercise.id) }
        holder.addBtn.setOnClickListener { view -> triggerAddDialog(currentExercise, workoutList) }
        holder.row.setOnClickListener { view -> activity.navToExerciseDetails(currentExercise.id) }

        holder.deleteBtn.setOnClickListener{ view ->
            val builder = AlertDialog.Builder(view.context)
            builder.setMessage(activity.baseContext.getString(R.string.delete_confirmation))
                .setCancelable(false)
                .setPositiveButton(activity.baseContext.getString(R.string.yes_txt)) { dialog, id ->
                    val dbHandler = DBHandler(view.context, null, null, 1)
                    dbHandler.deleteExercise(currentExercise.id)
                    eList.removeAt(position)
                    notifyItemRemoved(position)
                }
                .setNegativeButton(activity.baseContext.getString(R.string.no_txt)) { dialog, id ->
                    dialog.dismiss()
                }

            val alert = builder.create()
            alert.show()
        }
    }

    private fun triggerAddDialog(currentExercise: Exercise, workoutList: ArrayList<WorkoutViewModel>){
        var workEx = WorkoutExercise(currentExercise)
        var helper = AddExerciseToWorkoutHelper((activity as MainActivity), workoutList, workEx)
        helper.buildAddToWorkoutDialog()
    }

    override fun getItemCount(): Int {
        return eList.size
    }

    class ExerciseListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val row: CardView = itemView.findViewById(R.id.exercise_row_item)
        val id: TextView = itemView.findViewById(R.id.exercise_id_txt)
        val name: TextView = itemView.findViewById(R.id.exercise_name)
        val description: TextView = itemView.findViewById(R.id.description_txt)
        val isStrength: TextView = itemView.findViewById(R.id.is_strength_txt)
        val isCondition: TextView = itemView.findViewById(R.id.is_condition_txt)
        val setList: TextView = itemView.findViewById(R.id.set_list_txt)
        val repList: TextView = itemView.findViewById(R.id.rep_list_txt)
        val repTime: TextView = itemView.findViewById(R.id.rep_time_txt)
        val needsBothTxt: TextView = itemView.findViewById(R.id.needs_both_txt)
        val areaList: TextView = itemView.findViewById(R.id.area_list_txt)
        val tagTxt: TextView = itemView.findViewById(R.id.tag_txt)
        val addBtn: ImageButton = itemView.findViewById(R.id.add_to_workout_btn)
        val editBtn: ImageButton = itemView.findViewById(R.id.edit_btn)
        val deleteBtn: ImageButton = itemView.findViewById(R.id.delete_btn)
    }
}

