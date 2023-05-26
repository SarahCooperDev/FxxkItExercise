package com.example.fxxkit

import android.app.AlertDialog
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.fxxkit.DataClass.Exercise

class ExerciseListAdapter(private val eList: ArrayList<Exercise>, private val activity: MainActivity) :   RecyclerView.Adapter<ExerciseListAdapter.ExerciseListViewHolder>(){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ExerciseListViewHolder {
        val viewLayout = LayoutInflater.from(parent.context).inflate(
            R.layout.exercise_row_item, parent, false)
        return ExerciseListViewHolder(viewLayout)
    }

    override fun onBindViewHolder(holder: ExerciseListViewHolder, position: Int) {
        val currentExercise = eList[position]
        holder.id.text = currentExercise.id.toString()
        holder.name.text = currentExercise.name
        holder.description.text = currentExercise.description
        holder.isStrength.text = currentExercise.isStrengthening.toString()
        holder.isCondition.text = currentExercise.isConditioning.toString()
        holder.setList.text = currentExercise.getSetAsString()
        holder.repList.text = currentExercise.getRepsAsString()
        holder.repTime.text = currentExercise.repTime.toString()
        holder.muscleList.text = currentExercise.getMusclesAsString()

        if(currentExercise.tags.size > 0){
            holder.tagTxt.text = currentExercise.getTagDisplayString()
        } else {
            holder.tagTxt.text = "None"
        }

        holder.editBtn.setOnClickListener{view ->
            activity.navToEditExercise(currentExercise.id)
        }

        holder.deleteBtn.setOnClickListener{ view ->
            val builder = AlertDialog.Builder(view.context)
            builder.setMessage("Are you sure you want to delete?")
                .setCancelable(false)
                .setPositiveButton("Yes") { dialog, id ->
                    val dbHandler = DBHandler(view.context, null, null, 1)
                    dbHandler.deleteExercise(currentExercise.name)
                    eList.removeAt(position)
                    notifyItemRemoved(position)
                }
                .setNegativeButton("No") { dialog, id ->
                    dialog.dismiss()
                }

            val alert = builder.create()
            alert.show()
        }
    }

    override fun getItemCount(): Int {
        return eList.size
    }

    class ExerciseListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val id: TextView = itemView.findViewById(R.id.exercise_id_txt)
        val name: TextView = itemView.findViewById(R.id.exercise_name)
        val description: TextView = itemView.findViewById(R.id.description_txt)
        val isStrength: TextView = itemView.findViewById(R.id.is_strength_txt)
        val isCondition: TextView = itemView.findViewById(R.id.is_condition_txt)
        val setList: TextView = itemView.findViewById(R.id.set_list_txt)
        val repList: TextView = itemView.findViewById(R.id.rep_list_txt)
        val repTime: TextView = itemView.findViewById(R.id.rep_time_txt)
        val muscleList: TextView = itemView.findViewById(R.id.muscle_list_txt)
        val tagTxt: TextView = itemView.findViewById(R.id.tag_txt)
        val editBtn: ImageButton = itemView.findViewById(R.id.edit_btn)
        val deleteBtn: ImageButton = itemView.findViewById(R.id.delete_btn)
    }
}

