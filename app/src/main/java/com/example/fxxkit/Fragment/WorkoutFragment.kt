package com.example.fxxkit.Fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.fxxkit.DBHandler
import com.example.fxxkit.DataClass.WorkoutExercise
import com.example.fxxkit.R
import com.example.fxxkit.ViewHolder.DetailWorkoutExerciseListAdapter
import com.example.fxxkit.ViewHolder.WorkoutExerciseListAdapter
import com.example.fxxkit.ViewModel.WorkoutViewModel

/**
 * A simple [Fragment] subclass.
 * Use the [WorkoutFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class WorkoutFragment : Fragment() {
    private lateinit var currentWorkout: WorkoutViewModel
    private var workExercises: ArrayList<WorkoutExercise> = ArrayList<WorkoutExercise>()

    private lateinit var titleTV: TextView
    private lateinit var exerciseRV: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let { }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        var view = inflater.inflate(R.layout.fragment_workout, container, false)

        titleTV = view.findViewById(R.id.workout_name_txt)
        exerciseRV = view.findViewById(R.id.exercise_rv)

        titleTV.setText(currentWorkout.name)

        loadWorkoutExercises()

        exerciseRV.layoutManager = LinearLayoutManager(activity)
        exerciseRV.adapter = DetailWorkoutExerciseListAdapter(workExercises)

        return view
    }

    private fun loadWorkoutExercises(){
        val dbHandler = DBHandler(this.requireContext(), null, null, 1)
        workExercises = dbHandler.findAllWorkoutExercises(currentWorkout.castWorkoutVMToWorkout())

        for(workEx in workExercises){
            workEx.isSelected = true
            println("Exercise ${workEx.exercise!!.name}")
            println("Sets ${workEx.setSize.toString()}")
            println("Reps ${workEx.repSize.toString()}")
        }
    }

    companion object {
        @JvmStatic
        fun newInstance(workoutVM: WorkoutViewModel) =
            WorkoutFragment().apply {
                arguments = Bundle().apply { currentWorkout = workoutVM }
            }
    }
}