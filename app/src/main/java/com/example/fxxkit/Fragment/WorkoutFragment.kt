package com.example.fxxkit.Fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.fxxkit.DBHandler
import com.example.fxxkit.DataClass.WorkoutExercise
import com.example.fxxkit.MainActivity
import com.example.fxxkit.R
import com.example.fxxkit.ViewHolder.DetailWorkoutExerciseListAdapter
import com.example.fxxkit.ViewHolder.WorkoutExerciseListAdapter
import com.example.fxxkit.ViewModel.WorkoutViewModel

/**
 * Shows the details of a workout
 */
class WorkoutFragment : Fragment() {
    private var workoutId: Int = -1
    private lateinit var currentWorkout: WorkoutViewModel
    private var workExercises: ArrayList<WorkoutExercise> = ArrayList<WorkoutExercise>()

    private lateinit var titleTV: TextView
    private lateinit var descriptionTV: TextView
    private lateinit var tagTxt: TextView
    private lateinit var favImage: ImageView
    private lateinit var exerciseRV: RecyclerView
    private lateinit var editBtn: ImageButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        workoutId = arguments!!.getInt("workoutId")
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        var view = inflater.inflate(R.layout.fragment_workout, container, false)
        (activity as MainActivity).getSupportActionBar()?.customView?.findViewById<TextView>(R.id.appbar_title_id)?.setText(getString(R.string.workout_details_title))

        titleTV = view.findViewById(R.id.workout_name_txt)
        descriptionTV = view.findViewById(R.id.description_txt)
        tagTxt = view.findViewById(R.id.tag_txt)
        favImage = view.findViewById(R.id.favourite_iv)
        exerciseRV = view.findViewById(R.id.exercise_rv)
        editBtn = view.findViewById(R.id.edit_btn)

        loadWorkout()
        loadWorkoutExercises()

        titleTV.setText(currentWorkout.name)
        descriptionTV.setText(currentWorkout.description)

        if(currentWorkout.tags.size > 0){ tagTxt.setText(currentWorkout.getTagDisplayString()) }
        else { tagTxt.setText(getString(R.string.no_tags_txt)) }
        if(currentWorkout.isFavourited){ favImage.setImageResource(android.R.drawable.btn_star_big_on) }
        else { favImage.setImageResource(android.R.drawable.btn_star_big_off) }

        exerciseRV.layoutManager = LinearLayoutManager(activity)
        exerciseRV.adapter = DetailWorkoutExerciseListAdapter(workExercises)

        editBtn.setOnClickListener { view -> (activity as MainActivity).navToEditWorkout(currentWorkout.id) }

        return view
    }

    /**
     * Loads the workout from the database
     */
    private fun loadWorkout(){
        val dbHandler = DBHandler(this.requireContext(), null, null, 1)
        var workout = dbHandler.findWorkoutById(workoutId)
        if(workout !=  null){
            currentWorkout = workout.workoutName?.let { WorkoutViewModel(workout.id, it) }!!
            currentWorkout.description = workout.description
            currentWorkout.isFavourited = workout.isFavourited
            var workoutTags = dbHandler.getTagsForWorkout(workout)

            if(workoutTags != null){
                workout.tags = workoutTags
                currentWorkout.tags = workoutTags
            }
        }
    }

    /**
     * Loads the workouts exercises
     */
    private fun loadWorkoutExercises(){
        val dbHandler = DBHandler(this.requireContext(), null, null, 1)
        workExercises = dbHandler.findAllWorkoutExercises(currentWorkout.castWorkoutVMToWorkout())

        for(workEx in workExercises){
            workEx.isSelected = true
        }

        workExercises.sortBy{ it.orderNo }
    }

    companion object {
        @JvmStatic
        fun newInstance(workoutVM: WorkoutViewModel) =
            WorkoutFragment().apply {  }
    }
}