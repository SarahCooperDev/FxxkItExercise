package com.example.fxxkit.ViewHolder

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ListView
import android.widget.TableLayout
import android.widget.TableRow
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.example.fxxkit.DataClass.Exercise
import com.example.fxxkit.DataClass.WorkoutListData
import com.example.fxxkit.R
import com.example.fxxkit.ViewModel.ExerciseViewModel
import com.example.fxxkit.ViewModel.WorkoutViewModel

class WorkoutListAdapter(private val eList: List<WorkoutViewModel>) :   RecyclerView.Adapter<WorkoutListAdapter.WorkoutListViewHolder>(){
    private var expandedSize = ArrayList<Int>()
    private var workList = eList

    private var dummyData = ArrayList<String>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WorkoutListViewHolder {
        setCellSize()
        dummyData.add("Jump")
        dummyData.add("Squat")
        dummyData.add("Push")
        dummyData.add("Wriggle")
        dummyData.add("Writh")
        dummyData.add("Lay down")
        dummyData.add("Sleep")

        val viewLayout = LayoutInflater.from(parent.context).inflate(
            R.layout.workout_row_item, parent, false)
        return WorkoutListViewHolder(viewLayout)
    }

    override fun onBindViewHolder(holder: WorkoutListViewHolder, position: Int) {
        val currentWorkout = eList[position]
        holder.workout_name.text = currentWorkout.name

        var table = holder.exercise_tbl

        for(ex in dummyData){
            val context = holder.exercise_tbl.context
            var row: TableRow = TableRow(context)
            var txtView: TextView = TextView(context)

            txtView.setText(ex)
            row.addView(txtView)
            table.addView(row)
        }

        holder.workout_row.setOnClickListener{
            if(holder.exercise_tbl.visibility == View.VISIBLE){
                holder.exercise_tbl.visibility = View.GONE
                holder.workout_row.layoutParams.height = (holder.workout_row.height - 500)
            } else if(holder.exercise_tbl.visibility == View.GONE){
                holder.exercise_tbl.visibility = View.VISIBLE
                holder.workout_row.layoutParams.height = (holder.workout_row.height + 500)
            }
        }



        /***holder.workout_name.setOnClickListener{ view ->
            if(expandedSize[position] == 0){
                holder.exercise_name.layoutParams.height = 30
                expandedSize[position] = holder.exercise_name.layoutParams.height
            } else {
                holder.exercise_name.layoutParams.height = 0
                expandedSize[position] = holder.exercise_name.layoutParams.height
            }
        }***/
    }

    override fun getItemCount(): Int {
        return eList.size
    }

    class WorkoutListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val workout_name: TextView = itemView.findViewById(R.id.workout_name_txt)
        val workout_row: CardView = itemView.findViewById(R.id.workout_row_item)
        val exercise_tbl: TableLayout = itemView.findViewById(R.id.exercise_tbl)
    }

    fun setCellSize(){
        expandedSize = ArrayList()
        for(i in 0 until workList.size){
            expandedSize.add(0)
        }
    }
}