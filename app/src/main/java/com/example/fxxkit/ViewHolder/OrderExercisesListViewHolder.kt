package com.example.fxxkit.ViewHolder

import android.app.AlertDialog
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.example.fxxkit.AddWorkoutExerciseListAdapter
import com.example.fxxkit.DataClass.MultiselectLists
import com.example.fxxkit.DataClass.WorkoutExercise
import com.example.fxxkit.MainActivity
import com.example.fxxkit.R

class OrderExercisesListAdapter(private val activity: MainActivity, private val workExList: ArrayList<WorkoutExercise>) :   RecyclerView.Adapter<OrderExercisesListAdapter.OrderExercisesListViewHolder>(){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderExercisesListViewHolder {
        val viewLayout = LayoutInflater.from(parent.context).inflate(
            R.layout.exercise_order_row_item, parent, false)
        return OrderExercisesListViewHolder(viewLayout)
    }

    override fun onBindViewHolder(holder: OrderExercisesListViewHolder, position: Int) {
        println("Size is ${workExList.size}")
        val currentExercise = workExList[position]
        println("Current exercise: ${currentExercise.exercise!!.name}, is selected - ${currentExercise.isSelected.toString()}")

        holder.name.text = currentExercise.exercise!!.name
        holder.sets.text = currentExercise.setSize
        holder.reps.text = currentExercise.repSize
        holder.repTime.text = currentExercise.exercise!!.repTime.toString()
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

        holder.orderUpBtn.setOnClickListener { view ->
            order(position, currentExercise, true)
        }

        holder.orderDownBtn.setOnClickListener { view ->
            order(position, currentExercise, false)
        }
    }

    private fun order(position: Int, currentExercise: WorkoutExercise, isMovingUp: Boolean){
        println("Order number: ${currentExercise.orderNo}")
        var currentOrderNo = currentExercise.orderNo
        if(currentOrderNo < 0){
            currentOrderNo = position
            println("Setting order no to position ${position}")
        }

        if(isMovingUp){
            if(currentOrderNo-1 < 0){
                Toast.makeText(activity, "Can't move any higher", Toast.LENGTH_SHORT)
            } else {
                currentExercise.orderNo = currentOrderNo -1
                workExList[currentOrderNo-1].orderNo = currentOrderNo
            }
        } else {
            if(currentOrderNo+1 > workExList.size-1){
                Toast.makeText(activity, "Can't go any lower", Toast.LENGTH_SHORT)
            } else {
                currentExercise.orderNo = currentOrderNo +1
                workExList[currentOrderNo+1].orderNo = currentOrderNo
            }
        }

        workExList.sortBy { it.orderNo }
        notifyDataSetChanged()
    }

    private fun sortList(workExercise: WorkoutExercise, position: Int){
        //workExList.removeAt(position)
        //notifyItemRemoved(position)
        //notifyItemRangeChanged(position, workExList.size)
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int {
        return workExList.size
    }

    class OrderExercisesListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val row: CardView = itemView.findViewById(R.id.exercise_row)
        val name: TextView = itemView.findViewById(R.id.exercise_name_txt)
        val sets: TextView = itemView.findViewById(R.id.set_txt)
        val reps: TextView = itemView.findViewById(R.id.rep_txt)
        val repTime: TextView = itemView.findViewById(R.id.rep_time_txt)
        val totalTime: TextView = itemView.findViewById(R.id.total_time_txt)
        val orderUpBtn: ImageButton = itemView.findViewById(R.id.order_up_btn)
        val orderDownBtn: ImageButton = itemView.findViewById(R.id.order_down_btn)
        //val areaList: TextView = itemView.findViewById(R.id.area_list_txt)
    }
}