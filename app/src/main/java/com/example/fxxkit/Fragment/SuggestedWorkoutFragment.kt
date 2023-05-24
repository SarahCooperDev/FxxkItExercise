package com.example.fxxkit.Fragment

import android.app.AlertDialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.fxxkit.AddWorkoutExerciseListAdapter
import com.example.fxxkit.DBHandler
import com.example.fxxkit.DataClass.Tag
import com.example.fxxkit.DataClass.Workout
import com.example.fxxkit.DataClass.WorkoutExercise
import com.example.fxxkit.MainActivity
import com.example.fxxkit.R
import com.example.fxxkit.ViewHolder.DetailWorkoutExerciseListAdapter
import com.example.fxxkit.ViewHolder.SelectWorkoutExerciseListAdapter

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [SuggestedWorkoutFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class SuggestedWorkoutFragment : Fragment() {
    private var allTags = ArrayList<Tag>()
    private var workExercises: ArrayList<WorkoutExercise> = ArrayList<WorkoutExercise>()
    private var isFavourited: Boolean = false

    private lateinit var cancelBtn: ImageButton
    private lateinit var saveBtn: ImageButton
    private lateinit var exerciseRV: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        var view = inflater.inflate(R.layout.fragment_suggested_workout, container, false)
        (activity as MainActivity).getSupportActionBar()?.customView?.findViewById<TextView>(R.id.appbar_title_id)?.setText("Suggested")

        exerciseRV = view.findViewById(R.id.exercise_rv)
        cancelBtn = view.findViewById(R.id.cancel_btn)
        saveBtn = view.findViewById(R.id.save_btn)

        getAllTags()
        workExercises = (activity as MainActivity).getGeneratedWorkoutExercises()
        for(workEx in workExercises){
            workEx.isSelected = true
        }

        exerciseRV.layoutManager = LinearLayoutManager(activity)
        exerciseRV.adapter = DetailWorkoutExerciseListAdapter(workExercises)

        cancelBtn.setOnClickListener { view ->
            (activity as MainActivity).navToPrevious()
        }

        saveBtn.setOnClickListener { view ->
            println("Saving workout")
            buildSaveWorkoutDialog()
        }

        return view
    }

    private fun buildSaveWorkoutDialog(){
        val builder = AlertDialog.Builder(activity)
        builder.setTitle("Saving workout...")
        builder.setCancelable(false)

        val inflater: LayoutInflater = requireActivity().getLayoutInflater()
        var view = inflater.inflate(R.layout.dialog_create_workout_input, null)
        var favBtn = view.findViewById<ImageButton>(R.id.fav_btn)
        var nameInput = view.findViewById<EditText>(R.id.workout_name_txt)
        var descriptionInput = view.findViewById<EditText>(R.id.description_txt)
        var tagInput = view.findViewById<EditText>(R.id.tag_input)

        favBtn.setOnClickListener { view ->
            if(isFavourited){
                isFavourited = false
                favBtn.setImageResource(R.drawable.ic_star)
            } else {
                isFavourited = true
                favBtn.setImageResource(R.drawable.ic_star_filled)
            }
        }

        builder.setView(view)

        builder.setPositiveButton("Done") { dialogInterface, i ->
            var workoutId = addWorkoutWithExercises(nameInput.text.toString(), descriptionInput.text.toString(), tagInput.text.toString())
            if(workoutId != null && workoutId >= 0){
                (activity as MainActivity).navToWorkoutDetails(workoutId)
            }
        }

        builder.setNegativeButton("Cancel") { dialogInterface, i ->
            dialogInterface.dismiss()
        }

        builder.show()
    }

    private fun addWorkoutWithExercises(name: String, description: String, tags: String): Int?{
        val workout = Workout(name)
        workout.description = description
        workout.isFavourited = isFavourited

        val dbHandler = DBHandler(this.requireContext(), null, null, 1)
        var workoutId = dbHandler.addWorkout(workout)

        println("New workout " + workoutId + " added")

        if(workoutId != null && workoutId >= 0){
            workout.id = workoutId

            var orderNo = 0
            for(workEx in workExercises){
                if(workEx.isSelected) {
                    workEx.workoutId = workoutId
                    workEx.orderNo = orderNo
                    orderNo++

                    var result = dbHandler.addExerciseToWorkout(workEx)
                }
            }

            var splitTags = tags.split(" ")
            for(tag in splitTags){
                var foundTag = allTags.firstOrNull{ it.name!!.lowercase() == tag.toString().lowercase() }
                if(foundTag == null){
                    foundTag = Tag(tag.toString().lowercase())
                    foundTag.id = dbHandler.addTag(foundTag)!!
                    println("New tag ${tag} is ${foundTag.id}")
                }

                if(foundTag.id != null){
                    var result = dbHandler.addTagToWorkout(workout, foundTag)
                    println("Result is ${result}")
                }
            }
        }

        return workoutId
    }

    private fun getAllTags(){
        val dbHandler = DBHandler(this.requireContext(), null, null, 1)
        allTags = dbHandler.getAllTags()
    }

    private fun loadWorkoutExercises(){
        val dbHandler = DBHandler(this.requireContext(), null, null, 1)
        var exercises = dbHandler.getAllExercises()

        if (exercises != null) {
            for(exercise in exercises){
                workExercises.add(WorkoutExercise(exercise))
            }
        }

        for(workEx in workExercises){
            workEx.isSelected = true
        }

        workExercises.sortBy{ it.orderNo }
    }

    companion object {
        @JvmStatic
        fun newInstance() =
            SuggestedWorkoutFragment().apply {
                arguments = Bundle().apply {
                }
            }
    }
}