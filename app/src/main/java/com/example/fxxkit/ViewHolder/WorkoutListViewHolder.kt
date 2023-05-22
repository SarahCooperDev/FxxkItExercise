package com.example.fxxkit.ViewHolder

import android.app.ActionBar.LayoutParams
import android.app.AlertDialog
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.fxxkit.DBHandler
import com.example.fxxkit.DataClass.WorkoutExercise
import com.example.fxxkit.MainActivity
import com.example.fxxkit.R
import com.example.fxxkit.ViewModel.WorkoutViewModel

class WorkoutListAdapter(private val activity: MainActivity, private val eList: List<WorkoutViewModel>) :   RecyclerView.Adapter<WorkoutListAdapter.WorkoutListViewHolder>(){
    private var expandedSize = ArrayList<Int>()
    private var workList = eList
    private lateinit var inflatedViewGroup: View

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WorkoutListViewHolder {
        inflatedViewGroup = LayoutInflater.from(parent.context).inflate(R.layout.workout_row_item, parent, false)
        return WorkoutListViewHolder(inflatedViewGroup)
    }

    override fun onBindViewHolder(holder: WorkoutListViewHolder, position: Int) {
        val currentWorkout = eList[position]
        holder.workout_id.text = currentWorkout.id.toString()
        holder.workout_name.text = currentWorkout.name
        holder.description_txt.text = currentWorkout.description
        if(currentWorkout.tags.size > 0){
            holder.tag_txt.text = currentWorkout.getTagDisplayString()
        }

        if(currentWorkout.isFavourited){
            holder.favourite_iv.setImageResource(R.drawable.ic_star_filled)
        } else {
            holder.favourite_iv.setImageResource(R.drawable.ic_star)
        }

        holder.workout_details_row.visibility = View.GONE
        holder.workout_exercises_row.visibility = View.GONE

        var table = holder.exercise_rv

        table.layoutManager = LinearLayoutManager(activity)
        table.adapter = WorkoutExerciseListAdapter(currentWorkout.workExList)
        table.visibility = View.GONE

        holder.workout_row.setOnClickListener{
            if(holder.exercise_rv.visibility == View.VISIBLE){
                holder.workout_details_row.visibility = View.GONE
                holder.workout_exercises_row.visibility = View.GONE
                holder.exercise_rv.visibility = View.GONE
            } else if(holder.exercise_rv.visibility == View.GONE){
                holder.workout_details_row.visibility = View.VISIBLE
                holder.workout_exercises_row.visibility = View.VISIBLE
                holder.exercise_rv.visibility = View.VISIBLE
            }
        }

        holder.detail_btn.setOnClickListener { view ->
            activity.navToWorkoutDetails(currentWorkout.id)
        }

        holder.edit_btn.setOnClickListener { view ->
            activity.navToEditWorkout(currentWorkout.id)
        }

        holder.delete_btn.setOnClickListener{ view ->
            val builder = AlertDialog.Builder(view.context)
            builder.setMessage("Are you sure you want to delete?")
                .setCancelable(false)
                .setPositiveButton("Yes"){ dialog, id ->
                    val dbHandler = DBHandler(activity, null, null, 1)
                    var result = dbHandler.deleteWorkout(currentWorkout.id)

                    if(result){
                        activity.navToWorkoutList()
                    }
                }
                .setNegativeButton("No"){ dialog, id ->
                    dialog.dismiss()
                }

            val alert = builder.create()
            alert.show()
        }
    }

    override fun getItemCount(): Int {
        return eList.size
    }

    class WorkoutListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val workout_name: TextView = itemView.findViewById(R.id.workout_name_txt)
        val workout_row: CardView = itemView.findViewById(R.id.workout_row_item)
        val workout_details_row: TableRow = itemView.findViewById(R.id.workout_details_row)
        val workout_exercises_row: TableRow = itemView.findViewById(R.id.workout_exercises_row)
        val exercise_rv: RecyclerView = itemView.findViewById(R.id.workout_exercise_rv)
        val workout_id: TextView = itemView.findViewById(R.id.workout_id_txt)
        val description_txt: TextView = itemView.findViewById(R.id.description_txt)
        val tag_txt: TextView = itemView.findViewById(R.id.tag_txt)
        val favourite_iv: ImageView = itemView.findViewById(R.id.favourite_iv)
        val detail_btn: ImageButton = itemView.findViewById(R.id.detail_btn)
        val edit_btn: ImageButton = itemView.findViewById(R.id.edit_btn)
        val delete_btn: ImageButton = itemView.findViewById(R.id.delete_btn)
    }
}