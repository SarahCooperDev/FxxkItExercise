package com.example.fxxkit.Fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
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
    private var workoutId: Int = -1
    private lateinit var currentWorkout: WorkoutViewModel
    private var workExercises: ArrayList<WorkoutExercise> = ArrayList<WorkoutExercise>()

    private lateinit var titleTV: TextView
    private lateinit var descriptionTV: TextView
    private lateinit var favImage: ImageView
    private lateinit var exerciseRV: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        workoutId = arguments!!.getInt("workoutId")
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        var view = inflater.inflate(R.layout.fragment_workout, container, false)

        titleTV = view.findViewById(R.id.workout_name_txt)
        descriptionTV = view.findViewById(R.id.description_txt)
        favImage = view.findViewById(R.id.favourite_iv)
        exerciseRV = view.findViewById(R.id.exercise_rv)


        loadWorkout()
        loadWorkoutExercises()

        titleTV.setText(currentWorkout.name)
        descriptionTV.setText(currentWorkout.description)
        if(currentWorkout.isFavourited){
            favImage.setImageResource(android.R.drawable.btn_star_big_on)
        } else {
            favImage.setImageResource(android.R.drawable.btn_star_big_off)
        }

        exerciseRV.layoutManager = LinearLayoutManager(activity)
        exerciseRV.adapter = DetailWorkoutExerciseListAdapter(workExercises)

        return view
    }

    private fun loadWorkout(){
        val dbHandler = DBHandler(this.requireContext(), null, null, 1)
        var workout = dbHandler.findWorkoutById(workoutId)
        if(workout !=  null){
            currentWorkout = workout.workoutName?.let { WorkoutViewModel(workout.id, it) }!!
            currentWorkout.description = workout.description
            currentWorkout.isFavourited = workout.isFavourited
        }
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
            WorkoutFragment().apply {  }
    }
}