package com.example.fxxkit.Fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.fxxkit.*
import com.example.fxxkit.DataClass.Exercise
import com.example.fxxkit.DataClass.WorkoutExercise
import com.example.fxxkit.ViewModel.WorkoutViewModel

class EditWorkoutFragment : Fragment() {
    private lateinit var currentWorkout: WorkoutViewModel
    private lateinit var workoutName: EditText
    private lateinit var updateWorkoutBtn : Button
    private var allExerciseList: ArrayList<Exercise>? = null
    private var unselectedExerciseList: ArrayList<Exercise> = ArrayList<Exercise>()
    private var selectedExercises: ArrayList<WorkoutExercise> = ArrayList<WorkoutExercise>()
    private var unselectedWorkExercises: ArrayList<WorkoutExercise> = ArrayList<WorkoutExercise>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {  }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        var view = inflater.inflate(R.layout.fragment_edit_workout, container, false)
        var selectedRecycler = view.findViewById<RecyclerView>(R.id.selected_exercises_rv)
        selectedRecycler.layoutManager = LinearLayoutManager(activity)
        var unselectedRecycler = view.findViewById<RecyclerView>(R.id.unselected_exercises_rv)
        unselectedRecycler.layoutManager = LinearLayoutManager(activity)

        println("Workoutvm list size: ${currentWorkout.workExList.size.toString()}")

        loadAllExercises()
        filterExercisesBySelected()
        loadExercisesIntoWorkout(unselectedExerciseList)

        println("Have loaded exercises")
        println("Selected ex: ${selectedExercises.size.toString()}")
        println("All other ex: ${unselectedWorkExercises.size.toString()}")

        var selectAdapter = WorkoutExerciseListAdapter((activity as MainActivity), selectedExercises)
        selectedRecycler.adapter = selectAdapter
        selectAdapter.setOtherWorkExList(unselectedWorkExercises)

        var unselectAdapter = WorkoutExerciseListAdapter((activity as MainActivity), unselectedWorkExercises)
        unselectedRecycler.adapter = unselectAdapter
        unselectAdapter.setOtherWorkExList(selectedExercises)

        workoutName = view.findViewById<EditText>(R.id.workout_name_txt)
        updateWorkoutBtn = view.findViewById<Button>(R.id.update_workout_btn)

        workoutName.setText(currentWorkout.name)

        updateWorkoutBtn.setOnClickListener{ view ->
            updateWorkoutWithExercises()
            (activity as MainActivity).navToPrevious(view)
        }

        return view
    }

    private fun loadExercisesIntoWorkout(exerciseList: ArrayList<Exercise>){
        for(exercise in exerciseList){
            var workEx = WorkoutExercise(exercise)
            workEx.exercise = exercise
            unselectedWorkExercises.add(workEx)
        }
    }

    private fun filterExercisesBySelected(){
        if(allExerciseList != null && allExerciseList!!.size > 0){
            unselectedExerciseList = allExerciseList!!.clone() as ArrayList<Exercise>

            for(workExercise in currentWorkout.workExList){
                var element = unselectedExerciseList.firstOrNull{it.id == workExercise.exerciseId}
                if(element != null){
                    workExercise.exercise = element
                    selectedExercises.add(workExercise)
                    unselectedExerciseList.remove(element)
                }
            }
        }
    }

    private fun loadAllExercises(){
        val dbHandler = DBHandler(this.requireContext(), null, null, 1)
        allExerciseList = dbHandler.getAllExercises()
    }

    private fun updateWorkoutWithExercises(){

    }

    companion object {
        @JvmStatic
        fun newInstance(workoutVM: WorkoutViewModel) =
            EditWorkoutFragment().apply {
                arguments = Bundle().apply {
                    currentWorkout = workoutVM
                }
            }
    }
}