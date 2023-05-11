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
import com.example.fxxkit.*
import com.example.fxxkit.DataClass.Exercise
import com.example.fxxkit.DataClass.Workout
import com.example.fxxkit.DataClass.WorkoutExercise
import com.example.fxxkit.ViewHolder.WorkoutExerciseListAdapter
import com.example.fxxkit.ViewHolder.WorkoutListAdapter
import com.example.fxxkit.ViewModel.WorkoutViewModel

class EditWorkoutFragment : Fragment() {
    private lateinit var currentWorkout: WorkoutViewModel
    private lateinit var workoutName: EditText
    private lateinit var updateWorkoutBtn : Button
    private lateinit var addExerciseBtn: Button
    private lateinit var removeExerciseBtn: Button
    private lateinit var favBtn: ImageButton
    private lateinit var descTxt: EditText
    private lateinit var selectedExRV: RecyclerView
    private var allExerciseList: ArrayList<Exercise>? = null
    private var unselectedExerciseList: ArrayList<Exercise> = ArrayList<Exercise>()
    private var selectedExercises: ArrayList<WorkoutExercise> = ArrayList<WorkoutExercise>()
    private var unselectedWorkExercises: ArrayList<WorkoutExercise> = ArrayList<WorkoutExercise>()
    private var removedWorkExercises: ArrayList<WorkoutExercise> = ArrayList<WorkoutExercise>()
    private var addedWorkExercises: ArrayList<WorkoutExercise> = ArrayList<WorkoutExercise>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        var workoutId = arguments!!.getInt("workoutId")
        loadWorkout(workoutId)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        var view = inflater.inflate(R.layout.fragment_edit_workout, container, false)

        loadAllExercises()
        loadWorkoutExercises()
        filterExercisesBySelected()
        loadExercisesIntoWorkouts(unselectedExerciseList)

        workoutName = view.findViewById<EditText>(R.id.workout_name_txt)
        descTxt = view.findViewById<EditText>(R.id.description_txt)
        favBtn = view.findViewById<ImageButton>(R.id.fav_btn)
        updateWorkoutBtn = view.findViewById<Button>(R.id.update_workout_btn)
        addExerciseBtn = view.findViewById<Button>(R.id.add_exercise_btn)
        removeExerciseBtn = view.findViewById<Button>(R.id.remove_exercise_btn)
        selectedExRV = view.findViewById<RecyclerView>(R.id.selected_ex_rv)

        selectedExRV.layoutManager = LinearLayoutManager(activity)
        selectedExRV.adapter = WorkoutExerciseListAdapter(selectedExercises)

        workoutName.setText(currentWorkout.name)
        descTxt.setText(currentWorkout.description)
        setFavourite(true)

        addExerciseBtn.setOnClickListener { view -> buildAddExerciseDialog() }
        removeExerciseBtn.setOnClickListener { view -> buildRemoveExerciseDialog() }
        favBtn.setOnClickListener { view -> setFavourite(!currentWorkout.isFavourited) }

        updateWorkoutBtn.setOnClickListener{ view ->
            updateWorkoutWithExercises()
            (activity as MainActivity).navToPrevious()
        }

        return view
    }

    private fun setFavourite(isFavourited: Boolean){
        if(!isFavourited){
            currentWorkout.isFavourited = false
            favBtn.setImageResource(android.R.drawable.btn_star_big_off)
        } else {
            currentWorkout.isFavourited = true
            favBtn.setImageResource(android.R.drawable.btn_star_big_on)
        }
    }

    private fun buildAddExerciseDialog(){
        val builder = AlertDialog.Builder(activity)
        builder.setTitle("Add Exercise")
        builder.setCancelable(false)

        val inflater: LayoutInflater = requireActivity().getLayoutInflater()
        var view = inflater.inflate(R.layout.custom_add_exercise_dialog, null)

        var addExRV = view.findViewById<RecyclerView>(R.id.add_exercise_rv)
        addExRV.layoutManager = LinearLayoutManager(activity)
        addExRV.adapter = AddWorkoutExerciseListAdapter((activity as MainActivity), unselectedWorkExercises)

        builder.setView(view)

        builder.setPositiveButton("Done") { dialogInterface, i ->
            for(workEx in unselectedWorkExercises){
                println("Exercise is selected to be ${workEx.isSelected.toString()}")

                if(workEx.isSelected){
                    addedWorkExercises.add(workEx)
                }
            }

            for(workEx in addedWorkExercises){
                println("Sets is ${workEx.setSize} and reps is ${workEx.repSize}")
                unselectedWorkExercises.remove(workEx)
                workEx.workoutId = currentWorkout.id
                selectedExercises.add(workEx)
                selectedExRV.adapter!!.notifyDataSetChanged()
            }
        }

        builder.setNegativeButton("Cancel") { dialogInterface, i ->
            dialogInterface.dismiss()
        }

        builder.show()
    }

    private fun buildRemoveExerciseDialog(){
        val builder = AlertDialog.Builder(activity)
        builder.setTitle("Remove/Edit Exercise")
        builder.setCancelable(false)

        val inflater: LayoutInflater = requireActivity().getLayoutInflater()
        var view = inflater.inflate(R.layout.custom_remove_exercise_dialog, null)

        var removeExRV = view.findViewById<RecyclerView>(R.id.remove_exercise_rv)
        removeExRV.layoutManager = LinearLayoutManager(activity)
        removeExRV.adapter = AddWorkoutExerciseListAdapter((activity as MainActivity), selectedExercises)

        builder.setView(view)

        builder.setPositiveButton("Done") { dialogInterface, i ->
            for(workEx in selectedExercises){
                println("Exercise is selected to be ${workEx.isSelected.toString()}")
                if(!workEx.isSelected){
                    println("Workex id is: ${workEx.id.toString()}")
                    removedWorkExercises.add(workEx)
                }
            }

            for(workEx in removedWorkExercises){
                selectedExercises.remove(workEx)
                unselectedWorkExercises.add(workEx)
                selectedExRV.adapter!!.notifyDataSetChanged()
            }
        }

        builder.setNegativeButton("Cancel") { dialogInterface, i ->
            dialogInterface.dismiss()
        }

        builder.show()
    }

    private fun loadWorkout(workoutId: Int){
        val dbHandler = DBHandler(this.requireContext(), null, null, 1)
        var workout = dbHandler.findWorkoutById(workoutId)
        if(workout !=  null){
            currentWorkout = workout.workoutName?.let { WorkoutViewModel(workout.id, it) }!!
            currentWorkout.description = workout.description
            currentWorkout.isFavourited = workout.isFavourited
        }
    }
    private fun loadExercisesIntoWorkouts(exerciseList: ArrayList<Exercise>){
        for(exercise in exerciseList){
            var workEx = WorkoutExercise(exercise)
            workEx.exercise = exercise
            unselectedWorkExercises.add(workEx)
        }
    }

    private fun filterExercisesBySelected(){
        if(allExerciseList != null && allExerciseList!!.size > 0){
            unselectedExerciseList = allExerciseList!!.clone() as ArrayList<Exercise>

            for(workExercise in selectedExercises){
                var element = unselectedExerciseList.firstOrNull{it.id == workExercise.exerciseId}
                if(element != null){
                    workExercise.exercise = element
                    unselectedExerciseList.remove(element)
                }
            }
        }
    }

    private fun loadWorkoutExercises(){
        val dbHandler = DBHandler(this.requireContext(), null, null, 1)
        selectedExercises = dbHandler.findAllWorkoutExercises(currentWorkout.castWorkoutVMToWorkout())

        for(workEx in selectedExercises){
            workEx.isSelected = true
        }
    }

    private fun loadAllExercises(){
        val dbHandler = DBHandler(this.requireContext(), null, null, 1)
        allExerciseList = dbHandler.getAllExercises()
    }

    private fun updateWorkoutWithExercises(){
        val dbHandler = DBHandler(this.requireContext(), null, null, 1)
        currentWorkout.name = workoutName.text.toString()
        currentWorkout.description = descTxt.text.toString()
        var updatedWorkout = currentWorkout.castWorkoutVMToWorkout()

        dbHandler.updateWorkout(updatedWorkout)

        for(workEx in removedWorkExercises){ dbHandler.deleteWorkoutExercise(workEx.id) }
        for(workEx in addedWorkExercises){ dbHandler.addExerciseToWorkout(workEx) }
    }


    companion object {
        @JvmStatic
        fun newInstance(workoutVM: WorkoutViewModel) =
            EditWorkoutFragment().apply { arguments = Bundle().apply { currentWorkout = workoutVM  } }
    }
}