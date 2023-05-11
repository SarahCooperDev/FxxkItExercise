package com.example.fxxkit.Fragment

import android.graphics.Color
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.fxxkit.*
import com.example.fxxkit.DataClass.Exercise
import com.example.fxxkit.DataClass.Workout
import com.example.fxxkit.DataClass.WorkoutExercise

/**
 * A simple [Fragment] subclass.
 * Use the [CreateWorkoutFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class CreateWorkoutFragment : Fragment() {
    private var isFavourited: Boolean = false
    private lateinit var workoutName: EditText
    private lateinit var createWorkoutBtn : Button
    private lateinit var favBtn: ImageButton
    private lateinit var descTxt: EditText
    private var exerciseListAdapter: RecyclerView.Adapter<ExerciseListAdapter.ExerciseListViewHolder>? = null
    private var workoutExerciseList: ArrayList<WorkoutExercise> = ArrayList<WorkoutExercise>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_create_workout, container, false)
        var recycler = view.findViewById<RecyclerView>(R.id.exercise_list_rv)
        recycler.layoutManager = LinearLayoutManager(activity)

        var exerciseList = loadExercises()
        loadExercisesIntoWorkout(exerciseList)

        recycler.adapter = AddWorkoutExerciseListAdapter((activity as MainActivity), workoutExerciseList)

        workoutName = view.findViewById<EditText>(R.id.workout_name_txt)
        createWorkoutBtn = view.findViewById<Button>(R.id.create_workout_btn)
        favBtn = view.findViewById<ImageButton>(R.id.fav_btn)
        descTxt = view.findViewById<EditText>(R.id.description_txt)

        createWorkoutBtn.setOnClickListener{ view ->
            if(workoutName.text.toString().length < 1){
                Toast.makeText(activity, "Workout name may not be blank", Toast.LENGTH_LONG).show()
                workoutName.setBackgroundColor(ContextCompat.getColor(context!!, R.color.dark_red))
            } else {
                addWorkoutWithExercises()
                Toast.makeText(activity, "Added workout to database", Toast.LENGTH_SHORT).show()
                (activity as MainActivity).navToWorkoutList()
            }
        }

        favBtn.setOnClickListener { view ->
            if(isFavourited){
                isFavourited = false
                favBtn.setImageResource(android.R.drawable.btn_star_big_off)
            } else {
                isFavourited = true
                favBtn.setImageResource(android.R.drawable.btn_star_big_on)
            }
        }

        return view
    }

    private fun addWorkoutWithExercises(){
        val workout = Workout(workoutName.text.toString())
        workout.description = descTxt.text.toString()
        workout.isFavourited = isFavourited

        val dbHandler = DBHandler(this.requireContext(), null, null, 1)
        var workoutId = dbHandler.addWorkout(workout)

        println("New workout " + workoutId + " added")

        if(workoutId != null && workoutId >= 0){
            workout.id = workoutId

            for(workEx in workoutExerciseList){
                if(workEx.isSelected) {
                    workEx.workoutId = workoutId
                    var result = dbHandler.addExerciseToWorkout(workEx)
                }
            }
        }
    }

    private fun loadExercisesIntoWorkout(exerciseList: ArrayList<Exercise>){
        for(exercise in exerciseList){
            var workEx = WorkoutExercise(exercise)
            workEx.exercise = exercise
            workoutExerciseList.add(workEx)
        }
    }

    private fun loadExercises(): ArrayList<Exercise>{
        val dbHandler = DBHandler(this.requireContext(), null, null, 1)
        var exerciseList = dbHandler.getAllExercises()!!
        return exerciseList
    }

    companion object {
        @JvmStatic
        fun newInstance() =
            CreateWorkoutFragment().apply { arguments = Bundle().apply { } }
    }
}