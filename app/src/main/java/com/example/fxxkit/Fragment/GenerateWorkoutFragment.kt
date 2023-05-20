package com.example.fxxkit.Fragment

import android.app.AlertDialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.fxxkit.DBHandler
import com.example.fxxkit.DataClass.Exercise
import com.example.fxxkit.DataClass.WorkoutExercise
import com.example.fxxkit.MainActivity
import com.example.fxxkit.R
import com.example.fxxkit.ViewHolder.SelectWorkoutExerciseListAdapter

class GenerateWorkoutFragment : Fragment() {
    private lateinit var durationInput: EditText
    private lateinit var muscleTargetTxt: TextView
    private lateinit var muscleExcludeTxt: TextView
    private lateinit var strengthChkbx: CheckBox
    private lateinit var conditionChkbx: CheckBox
    private lateinit var excludeExTxt: TextView
    private lateinit var generateBtn: ImageButton
    private lateinit var cancelBtn: ImageButton

    private var allExercises: ArrayList<Exercise> = ArrayList<Exercise>()
    private var allWorkoutExercises: ArrayList<WorkoutExercise> = ArrayList<WorkoutExercise>()
    private var targettedMuscles: ArrayList<String> = ArrayList<String>()
    private var excludedMuscles: ArrayList<String> = ArrayList<String>()
    private var excludedWorkoutExercises: ArrayList<WorkoutExercise> = ArrayList<WorkoutExercise>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        var view = inflater.inflate(R.layout.fragment_generate_workout, container, false)
        (activity as MainActivity).getSupportActionBar()?.customView?.findViewById<TextView>(R.id.appbar_title_id)?.setText("Generate Workout")

        loadExercises(view)
        loadExercisesIntoWorkout()

        durationInput = view.findViewById(R.id.duration_input)
        muscleTargetTxt = view.findViewById(R.id.muscle_target_txt)
        muscleExcludeTxt = view.findViewById(R.id.muscle_exclude_txt)
        strengthChkbx = view.findViewById(R.id.strength_chkbx)
        conditionChkbx = view.findViewById(R.id.condition_chkbx)
        excludeExTxt = view.findViewById(R.id.exclude_ex_txt)
        cancelBtn = view.findViewById(R.id.cancel_btn)
        generateBtn = view.findViewById(R.id.generate_btn)

        muscleTargetTxt.setOnClickListener { view -> buildTargetMusclesDialog() }
        muscleExcludeTxt.setOnClickListener { view -> buildExcludeMusclesDialog() }
        excludeExTxt.setOnClickListener { view -> buildExcludeExerciseDialog() }

        generateBtn.setOnClickListener { view ->
            (activity as MainActivity).navToSuggestedWorkout()
        }

        cancelBtn.setOnClickListener { view ->
            (activity as MainActivity).navToPrevious()
        }

        return view
    }

    private fun loadExercisesIntoWorkout(){
        for(exercise in allExercises){
            var workEx = WorkoutExercise(exercise)
            workEx.exercise = exercise
            allWorkoutExercises.add(workEx)
        }
    }

    fun buildExcludeExerciseDialog(){
        val builder = AlertDialog.Builder(context).create()
        val view = layoutInflater.inflate(R.layout.dialog_select_exercises, null)
        var exerciseRV = view.findViewById<RecyclerView>(R.id.exercise_rv)
        exerciseRV.layoutManager = LinearLayoutManager(activity)
        var excludedExercisesAdapter = SelectWorkoutExerciseListAdapter((activity as MainActivity), allWorkoutExercises)
        exerciseRV.adapter = excludedExercisesAdapter

        var doneBtn = view.findViewById<Button>(R.id.done_btn)
        var cancelBtn = view.findViewById<Button>(R.id.cancel_btn)

        cancelBtn.setOnClickListener { view ->
            builder.dismiss()
        }
        doneBtn.setOnClickListener { view ->
            var selectedWorkExes = ArrayList<WorkoutExercise>()
            for(workExes in allWorkoutExercises){
                if(workExes.isSelected){
                    selectedWorkExes.add(workExes)
                }
            }
            var excludeTxt = "[None]"
            if(selectedWorkExes.size > 0){
                excludeTxt = ""
                for(workEx in selectedWorkExes){
                    excludeTxt += "- " + workEx.exercise?.name + "\n"
                }
            }

            excludeExTxt.setText(excludeTxt)

            builder.dismiss()
        }

        builder.setView(view)
        builder.setCanceledOnTouchOutside(true)
        builder.show()
    }

    fun buildTargetMusclesDialog(){
        val builder = AlertDialog.Builder(context).create()
        val view = layoutInflater.inflate(R.layout.custom_dialog_muscles, null)
        var muscleBoxes: MutableMap<String, CheckBox> = HashMap<String, CheckBox>()
        var allChk = view.findViewById<CheckBox>(R.id.all_chk)
        muscleBoxes["Calfs"] = view.findViewById<CheckBox>(R.id.calfs_chk)
        muscleBoxes["Quads"] = view.findViewById<CheckBox>(R.id.quads_chk)
        muscleBoxes["Glutts"] = view.findViewById<CheckBox>(R.id.glutts_chk)
        muscleBoxes["Abs"] = view.findViewById<CheckBox>(R.id.abs_chk)
        muscleBoxes["Triceps"] = view.findViewById<CheckBox>(R.id.triceps_chk)
        muscleBoxes["Bicepts"] = view.findViewById<CheckBox>(R.id.biceps_chk)

        var clearBtn = view.findViewById<Button>(R.id.clear_btn)
        var cancelBtn = view.findViewById<Button>(R.id.cancel_btn)
        var doneBtn = view.findViewById<Button>(R.id.done_btn)

        if(targettedMuscles.size == 0){
            allChk.isChecked = true
        } else {
            for(muscle in targettedMuscles){
                muscleBoxes[muscle]?.isChecked = true
            }
        }

        allChk.setOnClickListener { view ->
            if(allChk.isChecked){
                for(muscle in muscleBoxes){
                    muscle.value.isChecked = false
                }
            }
        }

        for(muscle in muscleBoxes){
            muscle.value.setOnClickListener { view ->
                if(muscle.value.isChecked){
                    allChk.isChecked = false
                }
            }
        }

        clearBtn.setOnClickListener { view ->
            allChk.isChecked = true
            for(muscle in muscleBoxes){
                muscle.value.isChecked = false
            }
        }

        cancelBtn.setOnClickListener { view ->
            builder.dismiss()
        }

        doneBtn.setOnClickListener { view ->
            if(allChk.isChecked){
                targettedMuscles.clear()
                muscleTargetTxt.setText("[All]")
            } else {
                var targetTxt = "["
                for(muscle in muscleBoxes){
                    if(muscle.value.isChecked){
                        targettedMuscles.add(muscle.key)
                        targetTxt += (muscle.key + ", ")
                    }
                }

                targetTxt = targetTxt.dropLast(2)
                targetTxt += "]"
                muscleTargetTxt.setText(targetTxt)
            }

            builder.dismiss()
        }

        builder.setView(view)
        builder.setCanceledOnTouchOutside(true)
        builder.show()
    }


    fun buildExcludeMusclesDialog(){
        val builder = AlertDialog.Builder(context).create()
        val view = layoutInflater.inflate(R.layout.custom_dialog_muscles, null)
        var muscleBoxes: MutableMap<String, CheckBox> = HashMap<String, CheckBox>()
        var allChk = view.findViewById<CheckBox>(R.id.all_chk)
        allChk.setText("None")
        muscleBoxes["Calfs"] = view.findViewById<CheckBox>(R.id.calfs_chk)
        muscleBoxes["Quads"] = view.findViewById<CheckBox>(R.id.quads_chk)
        muscleBoxes["Glutts"] = view.findViewById<CheckBox>(R.id.glutts_chk)
        muscleBoxes["Abs"] = view.findViewById<CheckBox>(R.id.abs_chk)
        muscleBoxes["Triceps"] = view.findViewById<CheckBox>(R.id.triceps_chk)
        muscleBoxes["Bicepts"] = view.findViewById<CheckBox>(R.id.biceps_chk)

        var clearBtn = view.findViewById<Button>(R.id.clear_btn)
        var cancelBtn = view.findViewById<Button>(R.id.cancel_btn)
        var doneBtn = view.findViewById<Button>(R.id.done_btn)

        if(excludedMuscles.size == 0){
            allChk.isChecked = true
        } else {
            for(muscle in excludedMuscles){
                muscleBoxes[muscle]?.isChecked = true
            }
        }

        allChk.setOnClickListener { view ->
            if(allChk.isChecked){
                for(muscle in muscleBoxes){
                    muscle.value.isChecked = false
                }
            }
        }

        for(muscle in muscleBoxes){
            muscle.value.setOnClickListener { view ->
                if(muscle.value.isChecked){
                    allChk.isChecked = false
                }
            }
        }

        clearBtn.setOnClickListener { view ->
            allChk.isChecked = true
            for(muscle in muscleBoxes){
                muscle.value.isChecked = false
            }
        }

        cancelBtn.setOnClickListener { view ->
            builder.dismiss()
        }

        doneBtn.setOnClickListener { view ->
            if(allChk.isChecked){
                excludedMuscles.clear()
                muscleExcludeTxt.setText("[None]")
            } else {
                var targetTxt = "["
                for(muscle in muscleBoxes){
                    if(muscle.value.isChecked){
                        excludedMuscles.add(muscle.key)
                        targetTxt += (muscle.key + ", ")
                    }
                }

                targetTxt = targetTxt.dropLast(2)
                targetTxt += "]"
                muscleExcludeTxt.setText(targetTxt)
            }

            builder.dismiss()
        }

        builder.setView(view)
        builder.setCanceledOnTouchOutside(true)
        builder.show()
    }

    private fun loadExercises(view: View){
        val dbHandler = DBHandler(this.requireContext(), null, null, 1)
        val retrievedList = dbHandler.getAllExercises()

        if(retrievedList != null && retrievedList.size > 0){
            allExercises = retrievedList
        }
    }

    companion object {
        @JvmStatic
        fun newInstance() = GenerateWorkoutFragment().apply { }
    }
}