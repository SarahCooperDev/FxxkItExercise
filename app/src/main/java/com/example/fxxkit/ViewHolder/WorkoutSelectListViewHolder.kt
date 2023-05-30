package com.example.fxxkit.ViewHolder

import android.app.AlertDialog
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.fxxkit.DBHandler
import com.example.fxxkit.MainActivity
import com.example.fxxkit.R
import com.example.fxxkit.ViewModel.WorkoutViewModel

class WorkoutSelectListAdapter(private val activity: MainActivity, private val eList: ArrayList<WorkoutViewModel>, private val selectWorkout: (WorkoutViewModel) -> (Unit)) :   RecyclerView.Adapter<WorkoutSelectListAdapter.WorkoutSelectListViewHolder>(){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WorkoutSelectListViewHolder {
        var inflatedViewGroup = LayoutInflater.from(parent.context).inflate(R.layout.row_item_workout_select, parent, false)
        return WorkoutSelectListViewHolder(inflatedViewGroup)
    }

    override fun onBindViewHolder(holder: WorkoutSelectListViewHolder, position: Int) {
        val currentWorkout = eList[position]

        holder.workoutIdTxt.text = currentWorkout.id.toString()
        holder.workoutNameTxt.text = currentWorkout.name.toString()

        holder.workoutRow.setOnClickListener { view ->
            selectWorkout(currentWorkout)
        }
    }

    override fun getItemCount(): Int {
        return eList.size
    }

    class WorkoutSelectListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val workoutRow: LinearLayout = itemView.findViewById(R.id.workout_row)
        val workoutIdTxt: TextView = itemView.findViewById(R.id.workout_id_txt)
        val workoutNameTxt: TextView = itemView.findViewById(R.id.workout_name_txt)
    }
}