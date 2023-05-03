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
import com.example.fxxkit.DataClass.WorkoutExercise
import com.example.fxxkit.ViewModel.WorkoutViewModel

class EditWorkoutFragment : Fragment() {
    private lateinit var currentWorkout: WorkoutViewModel
    private lateinit var workoutName: EditText
    private lateinit var updateWorkoutBtn : Button
    private var exerciseListAdapter: RecyclerView.Adapter<ExerciseListAdapter.ExerciseListViewHolder>? = null
    private var workoutExerciseList: ArrayList<WorkoutExercise> = ArrayList<WorkoutExercise>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {  }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        var view = inflater.inflate(R.layout.fragment_edit_workout, container, false)
        var recycler = view.findViewById<RecyclerView>(R.id.exercise_list_rv)
        recycler.layoutManager = LinearLayoutManager(activity)

        loadExerciseDetails()

        recycler.adapter = WorkoutExerciseListAdapter((activity as MainActivity), currentWorkout.workExList)

        workoutName = view.findViewById<EditText>(R.id.workout_name_txt)
        updateWorkoutBtn = view.findViewById<Button>(R.id.update_workout_btn)

        workoutName.setText(currentWorkout.name)

        updateWorkoutBtn.setOnClickListener{ view ->
            updateWorkoutWithExercises()
            (activity as MainActivity).navToPrevious(view)
        }

        return view
    }

    private fun loadExerciseDetails(){
        val dbHandler = DBHandler(this.requireContext(), null, null, 1)
        var nullWorkExs = ArrayList<WorkoutExercise>()

        for(workEx in currentWorkout.workExList){
            println("Finding exercise: ${workEx.exerciseId}")
            var fullExercise = dbHandler.findExerciseById(workEx.exerciseId)
            if(fullExercise != null){
                workEx.exercise = fullExercise
                println("Found exercise: ${workEx.exercise!!.name}")
            } else {
                println("returned null")
                nullWorkExs.add(workEx)
            }
        }

        for(workEx in nullWorkExs){
            currentWorkout.workExList.remove(workEx)
        }
    }

    private fun loadExercises(){

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