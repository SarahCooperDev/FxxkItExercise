package com.example.fxxkit.ViewHolder

import android.app.AlertDialog
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TableLayout
import android.widget.TableRow
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.example.fxxkit.DBHandler
import com.example.fxxkit.MainActivity
import com.example.fxxkit.R
import com.example.fxxkit.ViewModel.WorkoutViewModel

class WorkoutListAdapter(private val activity: MainActivity, private val eList: List<WorkoutViewModel>) :   RecyclerView.Adapter<WorkoutListAdapter.WorkoutListViewHolder>(){
    private var expandedSize = ArrayList<Int>()
    private var workList = eList

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WorkoutListViewHolder {
        setCellSize()

        val viewLayout = LayoutInflater.from(parent.context).inflate(
            R.layout.workout_row_item, parent, false)
        return WorkoutListViewHolder(viewLayout)
    }

    override fun onBindViewHolder(holder: WorkoutListViewHolder, position: Int) {
        val currentWorkout = eList[position]
        holder.workout_id.text = currentWorkout.id.toString()
        holder.workout_name.text = currentWorkout.name

        var table = holder.exercise_tbl

        for(ex in currentWorkout.workExList!!){
            val context = holder.exercise_tbl.context

            if(ex.exercise != null && ex.exercise!!.name != null){
                var row: TableRow = TableRow(context)
                var txtView: TextView = TextView(context)
                txtView.setText(ex.exercise!!.name)
                row.addView(txtView)
                table.addView(row)
            }
        }

        holder.workout_row.setOnClickListener{
            var totalHeight = holder.workout_name.height * currentWorkout.workExList.size

            if(holder.exercise_tbl.visibility == View.VISIBLE){
                holder.exercise_tbl.visibility = View.GONE
                if(holder.workout_row.layoutParams.height - totalHeight > 10){
                    holder.workout_row.layoutParams.height = (holder.workout_row.height - totalHeight)
                }
            } else if(holder.exercise_tbl.visibility == View.GONE){
                holder.exercise_tbl.visibility = View.VISIBLE
                holder.workout_row.layoutParams.height = (holder.workout_row.height + totalHeight)
            }
        }

        holder.edit_btn.setOnClickListener { view ->
            println("Clicked edit button")
        }

        holder.delete_btn.setOnClickListener{ view ->
            val builder = AlertDialog.Builder(view.context)
            builder.setMessage("Are you sure you want to delete?")
                .setCancelable(false)
                .setPositiveButton("Yes"){ dialog, id ->
                    val dbHandler = DBHandler(activity, null, null, 1)
                    var result = dbHandler.deleteWorkout(currentWorkout.id)

                    if(result){
                        activity.navToWorkoutList(view)
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
        val exercise_tbl: TableLayout = itemView.findViewById(R.id.exercise_tbl)
        val workout_id: TextView = itemView.findViewById(R.id.workout_id_txt)
        val edit_btn: ImageButton = itemView.findViewById(R.id.edit_btn)
        val delete_btn: ImageButton = itemView.findViewById(R.id.delete_btn)
    }

    fun setCellSize(){
        expandedSize = ArrayList()
        for(i in 0 until workList.size){
            expandedSize.add(0)
        }
    }
}