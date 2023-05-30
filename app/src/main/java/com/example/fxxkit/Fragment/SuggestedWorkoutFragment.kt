package com.example.fxxkit.Fragment

import android.app.AlertDialog
import android.graphics.Color
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.fxxkit.DBHandler
import com.example.fxxkit.DataClass.Tag
import com.example.fxxkit.DataClass.Workout
import com.example.fxxkit.DataClass.WorkoutExercise
import com.example.fxxkit.MainActivity
import com.example.fxxkit.R
import com.example.fxxkit.ViewHolder.DetailWorkoutExerciseListAdapter

/**
 * Shows the list of exercises a generated workout has
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
        (activity as MainActivity).getSupportActionBar()?.customView?.findViewById<TextView>(R.id.appbar_title_id)?.setText(getString(R.string.suggested_title))

        exerciseRV = view.findViewById(R.id.exercise_rv)
        cancelBtn = view.findViewById(R.id.cancel_btn)
        saveBtn = view.findViewById(R.id.save_btn)

        getAllTags()
        workExercises = (activity as MainActivity).getGeneratedWorkoutExercises()
        for(workEx in workExercises){ workEx.isSelected = true }

        exerciseRV.layoutManager = LinearLayoutManager(activity)
        exerciseRV.adapter = DetailWorkoutExerciseListAdapter(workExercises)

        cancelBtn.setOnClickListener { view -> (activity as MainActivity).navToWorkoutList() }
        saveBtn.setOnClickListener { view -> buildSaveWorkoutDialog() }

        return view
    }

    /**
     * Builds and shows the dialog that users must fill to save a workout in the database
     * Uses:
     *  - dialog_create_workout_input
     */
    private fun buildSaveWorkoutDialog(){
        val builder = AlertDialog.Builder(activity)
        builder.setTitle(getString(R.string.dialog_save_workout))
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

        builder.setPositiveButton(getString(R.string.done_txt)) { dialogInterface, i ->
            if(nameInput.text.toString().length < 1){
                Toast.makeText(activity, getString(R.string.error_blank_name_txt), Toast.LENGTH_LONG).show()
                nameInput.setBackgroundColor(Color.parseColor(getString(R.string.colour_error)))
            } else {
                var workoutId = addWorkoutWithExercises(nameInput.text.toString(), descriptionInput.text.toString(), tagInput.text.toString())
                Toast.makeText(activity, "Created workout ${nameInput.text.toString()}", Toast.LENGTH_SHORT).show()
                if (workoutId != null) {
                    (activity as MainActivity).navToWorkoutDetails(workoutId)
                }
            }
        }

        builder.setNegativeButton(getString(R.string.cancel_txt)) { dialogInterface, i ->
            dialogInterface.dismiss()
        }

        builder.show()
    }

    /**
     * Saves the workout to the database
     */
    private fun addWorkoutWithExercises(name: String, description: String, tags: String): Int?{
        val workout = Workout(name)
        workout.description = description
        workout.isFavourited = isFavourited

        val dbHandler = DBHandler(this.requireContext(), null, null, 1)
        var workoutId = dbHandler.addWorkout(workout)

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

            addTags(tags, workout)
        }

        return workoutId
    }

    /**
     * Adds new tags to the database, and new WorkoutTags
     */
    private fun addTags(tags: String, workout: Workout){
        val dbHandler = DBHandler(this.requireContext(), null, null, 1)
        var splitTags = tags.split(" ")
        for(tag in splitTags){
            var foundTag = allTags.firstOrNull{ it.name!!.lowercase() == tag.toString().lowercase() }
            if(foundTag == null){
                foundTag = Tag(tag.toString().lowercase())
                foundTag.id = dbHandler.addTag(foundTag)!!
            }

            if(foundTag.id != null){
                var result = dbHandler.addTagToWorkout(workout, foundTag)
            }
        }
    }

    /**
     * Gets all tags from the database, to prevent duplicates
     */
    private fun getAllTags(){
        val dbHandler = DBHandler(this.requireContext(), null, null, 1)
        allTags = dbHandler.getAllTags()
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