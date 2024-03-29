package com.example.fxxkit.ViewHolder

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import com.example.fxxkit.DataClass.WorkoutExercise
import com.example.fxxkit.MainActivity
import com.example.fxxkit.R

/**
 * Adapter for recycler
 * Displays a list of exercises that have buttons to rearrange the order
 * Uses:
 *  - exercise_order_row_item
 */
class OrderExercisesListAdapter(private val activity: MainActivity, private val workExList: ArrayList<WorkoutExercise>) :   RecyclerView.Adapter<OrderExercisesListAdapter.OrderExercisesListViewHolder>(){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderExercisesListViewHolder {
        val viewLayout = LayoutInflater.from(parent.context).inflate(R.layout.row_item_exercise_order, parent, false)
        return OrderExercisesListViewHolder(viewLayout)
    }

    override fun onBindViewHolder(holder: OrderExercisesListViewHolder, position: Int) {
        val currentExercise = workExList[position]

        holder.name.text = currentExercise.exercise!!.name
        holder.sets.text = currentExercise.setSize
        holder.reps.text = currentExercise.repSize
        holder.repTime.text = currentExercise.exercise!!.repTime.toString()

        // Gets time in minutes and seconds
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

        holder.orderUpBtn.setOnClickListener { view -> order(position, currentExercise, true) }
        holder.orderDownBtn.setOnClickListener { view -> order(position, currentExercise, false) }
    }

    /**
     * Swaps the order of an item with the item in it's new position
     */
    private fun order(position: Int, currentExercise: WorkoutExercise, isMovingUp: Boolean){
        var currentOrderNo = currentExercise.orderNo
        if(currentOrderNo < 0){
            currentOrderNo = position
        }

        if(isMovingUp){
            if(currentOrderNo-1 < 0){
                Toast.makeText(activity, activity.baseContext.getString(R.string.error_index_lowest), Toast.LENGTH_SHORT).show()
            } else {
                currentExercise.orderNo = currentOrderNo -1
                workExList[currentOrderNo-1].orderNo = currentOrderNo
            }
        } else {
            if(currentOrderNo+1 > workExList.size-1){
                Toast.makeText(activity, activity.baseContext.getString(R.string.error_index_highest), Toast.LENGTH_SHORT).show()
            } else {
                currentExercise.orderNo = currentOrderNo +1
                workExList[currentOrderNo+1].orderNo = currentOrderNo
            }
        }

        workExList.sortBy { it.orderNo }
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int {
        return workExList.size
    }

    class OrderExercisesListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val name: TextView = itemView.findViewById(R.id.exercise_name_txt)
        val sets: TextView = itemView.findViewById(R.id.set_txt)
        val reps: TextView = itemView.findViewById(R.id.rep_txt)
        val repTime: TextView = itemView.findViewById(R.id.rep_time_txt)
        val totalTime: TextView = itemView.findViewById(R.id.total_time_txt)
        val orderUpBtn: ImageButton = itemView.findViewById(R.id.order_up_btn)
        val orderDownBtn: ImageButton = itemView.findViewById(R.id.order_down_btn)
    }
}