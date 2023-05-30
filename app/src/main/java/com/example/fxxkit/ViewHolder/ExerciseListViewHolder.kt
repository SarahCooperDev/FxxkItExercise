package com.example.fxxkit

import android.app.AlertDialog
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
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
    private lateinit var detailLayout: LinearLayout
    private lateinit var workoutListLayout: LinearLayout
    private lateinit var selectedWorkoutTxt: TextView
    private lateinit var dialog: AlertDialog
    private var workoutVM: WorkoutViewModel = WorkoutViewModel(-1)
    private var workoutExercise: WorkoutExercise = WorkoutExercise()


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
        holder.areaList.text = currentExercise.getAreasAsString()

        if(currentExercise.tags.size > 0){ holder.tagTxt.text = currentExercise.getTagDisplayString()
        } else { holder.tagTxt.text = activity.baseContext.getString(R.string.none_txt) }

        holder.editBtn.setOnClickListener{view -> activity.navToEditExercise(currentExercise.id) }
        holder.addBtn.setOnClickListener { view -> buildAddToWorkoutDialog(currentExercise) }

        holder.deleteBtn.setOnClickListener{ view ->
            val builder = AlertDialog.Builder(view.context)
            builder.setMessage(activity.baseContext.getString(R.string.delete_confirmation))
                .setCancelable(false)
                .setPositiveButton(activity.baseContext.getString(R.string.yes_txt)) { dialog, id ->
                    val dbHandler = DBHandler(view.context, null, null, 1)
                    dbHandler.deleteExercise(currentExercise.name)
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

    private fun buildAddToWorkoutDialog(currentExercise: Exercise){
        workoutExercise = WorkoutExercise(currentExercise)
        val builder = AlertDialog.Builder(activity)
        builder.setCancelable(false)
        val inflater: LayoutInflater = activity.getLayoutInflater()
        var view = inflater.inflate(R.layout.dialog_select_workout, null)

        workoutListLayout = view.findViewById(R.id.workout_list_layout)
        detailLayout = view.findViewById(R.id.workout_exercise_details_layout)
        selectedWorkoutTxt = view.findViewById(R.id.selected_workout_txt)
        var backBtn: ImageButton = view.findViewById(R.id.back_btn)
        var setRadioGroup = view.findViewById<RadioGroup>(R.id.set_radiogrp)
        var repRadioGroup = view.findViewById<RadioGroup>(R.id.rep_radiogrp)
        var workoutRV: RecyclerView = view.findViewById(R.id.select_workout_rv)

        setUpRadioBtns(setRadioGroup, repRadioGroup)
        workoutRV.layoutManager = LinearLayoutManager(activity)
        workoutRV.adapter = WorkoutSelectListAdapter(activity, workoutList, ::callWorkoutSelected)
        detailLayout.visibility = View.GONE
        builder.setView(view)

        // Sets up button to complete the dialog
        builder.setPositiveButton(activity.baseContext.getString(R.string.done_txt)) { dialogInterface, i ->
            if(workoutExercise.setSize == null || workoutExercise.repSize == null){
                Toast.makeText(activity.baseContext, "You must choose set and rep sizes", Toast.LENGTH_LONG).show()
            } else {
                addExerciseToWorkout()
                dialogInterface.dismiss()
            }
        }

        // Sets up the button to cancel the input
        builder.setNegativeButton(activity.baseContext.getString(R.string.cancel_txt)) { dialogInterface, i ->
            dialogInterface.dismiss()
        }

        dialog = builder.show()
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).isEnabled = false

        backBtn.setOnClickListener { view ->
            workoutListLayout.visibility = View.VISIBLE
            detailLayout.visibility = View.GONE
            dialog.getButton(AlertDialog.BUTTON_POSITIVE).isEnabled = false
        }

        setRadioGroup.setOnCheckedChangeListener(RadioGroup.OnCheckedChangeListener{ group, checkedId ->
            val radio: RadioButton = view.findViewById(checkedId)
            workoutExercise.setSize = radio.text.toString()
            if(workoutExercise.setSize != null && workoutExercise.repSize != null){
                dialog.getButton(AlertDialog.BUTTON_POSITIVE).isEnabled = true
            }
        })

        repRadioGroup.setOnCheckedChangeListener(RadioGroup.OnCheckedChangeListener{ group, checkedId ->
            val radio: RadioButton = view.findViewById(checkedId)
            workoutExercise.repSize = radio.text.toString()
            if(workoutExercise.setSize != null && workoutExercise.repSize != null){
                dialog.getButton(AlertDialog.BUTTON_POSITIVE).isEnabled = true
            }
        })
    }

    private fun addExerciseToWorkout(){
        val dbHandler = DBHandler(activity.baseContext, null, null, 1)
        workoutExercise.orderNo = workoutVM.workExList.size

        if(workoutExercise.workoutId >= 0){
            var result = dbHandler.addExerciseToWorkout(workoutExercise)
            Toast.makeText(activity.baseContext, "Added exercise ${workoutExercise.exercise!!.name} to workout ${workoutVM.name}", Toast.LENGTH_LONG).show()
        }
    }

    fun callWorkoutSelected(workoutVM: WorkoutViewModel){
        var checkExercise = workoutVM.workExList.firstOrNull{ it.id == workoutExercise.exerciseId}
        if(checkExercise != null){
            Toast.makeText(activity.baseContext, "Workout already contains this exercise!", Toast.LENGTH_LONG).show()
        } else {
            this.workoutVM = workoutVM
            workoutListLayout.visibility = View.GONE
            detailLayout.visibility = View.VISIBLE
            selectedWorkoutTxt.text = "Workout: ${workoutVM.name}"
            workoutExercise.workoutId = workoutVM.id

            if(workoutExercise.setSize != null && workoutExercise.repSize != null){
                dialog.getButton(AlertDialog.BUTTON_POSITIVE).isEnabled = true
            }
        }
    }

    private fun setUpRadioBtns(setRadioGroup: RadioGroup, repRadioGroup: RadioGroup){
        if(workoutExercise.exercise != null && workoutExercise.exercise!!.possibleSetSize != null && workoutExercise.exercise!!.possibleSetSize.size > 0){
            if(workoutExercise.exercise!!.possibleSetSize[0] == MultiselectLists.setSizesArray[0]){
                for(i in 1 .. MultiselectLists.setSizesArray.size - 1){
                    var set = MultiselectLists.setSizesArray[i]
                    var radiobtn = RadioButton(activity)
                    radiobtn.setText(set)
                    setRadioGroup.addView(radiobtn)
                }
            } else {
                for(set in workoutExercise.exercise!!.possibleSetSize){
                    var radiobtn = RadioButton(activity)
                    radiobtn.setText(set)
                    setRadioGroup.addView(radiobtn)
                }
            }
        }

        // Shows the reps available for an exercise, as a selectable list
        if(workoutExercise.exercise != null && workoutExercise.exercise!!.possibleRepSize != null && workoutExercise.exercise!!.possibleRepSize.size > 0){
            if(workoutExercise.exercise!!.possibleRepSize[0] == MultiselectLists.repSizesArray[0]){
                for(i in 1 .. MultiselectLists.repSizesArray.size - 1){
                    var rep = MultiselectLists.repSizesArray[i]
                    var radiobtn = RadioButton(activity)
                    radiobtn.setText(rep)
                    repRadioGroup.addView(radiobtn)
                }
            } else {
                for (rep in workoutExercise.exercise!!.possibleRepSize) {
                    var radioBtn = RadioButton(activity)
                    radioBtn.setText(rep)
                    repRadioGroup.addView(radioBtn)
                }
            }
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
        val areaList: TextView = itemView.findViewById(R.id.area_list_txt)
        val tagTxt: TextView = itemView.findViewById(R.id.tag_txt)
        val addBtn: ImageButton = itemView.findViewById(R.id.add_to_workout_btn)
        val editBtn: ImageButton = itemView.findViewById(R.id.edit_btn)
        val deleteBtn: ImageButton = itemView.findViewById(R.id.delete_btn)
    }
}

