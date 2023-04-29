package com.example.fxxkit.Fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.fxxkit.*
import com.example.fxxkit.DataClass.Exercise
import com.example.fxxkit.DataClass.Workout
import com.example.fxxkit.ViewModel.ExerciseViewModel

/**
 * A simple [Fragment] subclass.
 * Use the [CreateWorkoutFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class CreateWorkoutFragment : Fragment() {
    private lateinit var workoutName: EditText
    private lateinit var createWorkoutBtn : Button
    private var exerciseListAdapter: RecyclerView.Adapter<ExerciseListAdapter.ExerciseListViewHolder>? = null
    private lateinit var exerciseList: ArrayList<ExerciseViewModel>
    var selectedExercises = ArrayList<ExerciseViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_create_workout, container, false)
        var recycler = view.findViewById<RecyclerView>(R.id.exercise_list_rv)
        recycler.layoutManager = LinearLayoutManager(activity)

        exerciseList = ArrayList<ExerciseViewModel>()

        getExercises(view)

        recycler.adapter = WorkoutExerciseListAdapter(exerciseList, selectedExercises)

        workoutName = view.findViewById<EditText>(R.id.workout_name_txt)
        createWorkoutBtn = view.findViewById<Button>(R.id.create_workout_btn)

        createWorkoutBtn.setOnClickListener{ view ->
            addWorkoutWithExercises(view)
            (activity as MainActivity).navToPrevious(view)
        }

        return view
    }

    private fun addWorkoutWithExercises(view: View){
        val workout = Workout(workoutName.text.toString())

        val dbHandler = DBHandler(this.requireContext(), null, null, 1)
        var workoutId = dbHandler.addWorkout(workout)

        println("New workout " + workoutId + " added")

        if(workoutId != null && workoutId >= 0){
            workout.id = workoutId

            for(exVM in selectedExercises){
                var exercise = Exercise(exVM.id, exVM.name)

                var result = dbHandler.addExerciseToWorkout(workout, exercise)
            }
        }
    }

    private fun getExercises(view: View){
        val dbHandler = DBHandler(this.requireContext(), null, null, 1)
        val exercises = dbHandler.getAllExercises()

        if (exercises != null && exercises.size > 0) {
            for(exercise in exercises){
                exerciseList.add(ExerciseViewModel(exercise.id, exercise.exerciseName, 30))
            }
        }
    }

    companion object {
        @JvmStatic
        fun newInstance() =
            CreateWorkoutFragment().apply { arguments = Bundle().apply { } }
    }
}