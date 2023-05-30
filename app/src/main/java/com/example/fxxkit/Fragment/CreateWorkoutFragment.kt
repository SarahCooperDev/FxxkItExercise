package com.example.fxxkit.Fragment

import android.app.AlertDialog
import android.graphics.Color
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
import com.example.fxxkit.DataClass.Tag
import com.example.fxxkit.DataClass.Workout
import com.example.fxxkit.DataClass.WorkoutExercise
import com.example.fxxkit.ViewHolder.OrderExercisesListAdapter

/**
 * Fragment that allows user to create a workout
 */
class CreateWorkoutFragment : Fragment() {
    private lateinit var workoutName: EditText
    private lateinit var addExerciseBtn: Button
    private lateinit var removeExerciseBtn: Button
    private lateinit var createBtn: ImageButton
    private lateinit var cancelBtn: ImageButton
    private lateinit var favBtn: ImageButton
    private lateinit var descTxt: EditText
    private lateinit var tagInput: EditText
    private lateinit var selectedExRecycler: RecyclerView

    private var isFavourited: Boolean = false
    private var allWorkoutExercises: ArrayList<WorkoutExercise> = ArrayList<WorkoutExercise>()
    private var selectedExercises: ArrayList<WorkoutExercise> = ArrayList<WorkoutExercise>()
    private var allTags = ArrayList<Tag>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_create_workout, container, false)
        (activity as MainActivity).getSupportActionBar()?.customView?.findViewById<TextView>(R.id.appbar_title_id)?.setText(getString(R.string.create_workout_title))

        workoutName = view.findViewById(R.id.workout_name_txt)
        createBtn = view.findViewById(R.id.create_btn)
        cancelBtn = view.findViewById(R.id.cancel_btn)
        favBtn = view.findViewById(R.id.fav_btn)
        descTxt = view.findViewById(R.id.description_txt)
        tagInput = view.findViewById(R.id.tag_input)
        addExerciseBtn = view.findViewById(R.id.add_exercise_btn)
        removeExerciseBtn = view.findViewById(R.id.remove_exercise_btn)
        selectedExRecycler = view.findViewById(R.id.exercise_list_rv)

        var exerciseList = loadExercises()
        loadExercisesIntoWorkout(exerciseList)
        getAllTags()

        selectedExRecycler.layoutManager = LinearLayoutManager(activity)
        selectedExRecycler.adapter = OrderExercisesListAdapter((activity as MainActivity), selectedExercises)

        favBtn.setOnClickListener { view ->
            if(isFavourited){
                isFavourited = false
                favBtn.setImageResource(R.drawable.ic_star)
            } else {
                isFavourited = true
                favBtn.setImageResource(R.drawable.ic_star_filled)
            }
        }

        addExerciseBtn.setOnClickListener { view -> buildAddExerciseDialog() }
        removeExerciseBtn.setOnClickListener { view -> buildRemoveExerciseDialog() }

        createBtn.setOnClickListener{ view ->
            if(workoutName.text.toString().length < 1){
                Toast.makeText(activity, getString(R.string.error_blank_name_txt), Toast.LENGTH_LONG).show()
                workoutName.setBackgroundColor(Color.parseColor(getString(R.string.colour_error)))
            } else {
                createWorkoutWithExercises()
                Toast.makeText(activity, "Created workout ${workoutName.text.toString()}", Toast.LENGTH_SHORT).show()
                (activity as MainActivity).navToWorkoutList()
            }
        }

        cancelBtn.setOnClickListener { view -> (activity as MainActivity).navToPrevious() }

        return view
    }

    /**
     * Builds and displays a pop-up that allows exercises to be added to the workout
     *
     * Uses:
     *  - custom_add_exercise_dialog.xml
     *  - AddWorkoutExerciseListAdapter
     */
    private fun buildAddExerciseDialog(){
        val builder = AlertDialog.Builder(activity)
        builder.setTitle(getString(R.string.dialog_title_add_exercise))
        builder.setCancelable(false)

        val inflater: LayoutInflater = requireActivity().getLayoutInflater()
        var view = inflater.inflate(R.layout.dialog_add_exercise, null)

        var addExRV = view.findViewById<RecyclerView>(R.id.add_exercise_rv)
        addExRV.layoutManager = LinearLayoutManager(activity)
        allWorkoutExercises.sortBy { it.isSelected }
        addExRV.adapter = AddWorkoutExerciseListAdapter((activity as MainActivity), allWorkoutExercises)

        builder.setView(view)

        builder.setPositiveButton(getString(R.string.done_txt)) { dialogInterface, i ->
            for(workEx in allWorkoutExercises){
                if(workEx.isSelected){
                    selectedExercises.add(workEx)
                }
            }

            selectedExRecycler.adapter!!.notifyDataSetChanged()
            dialogInterface.dismiss()
        }

        builder.setNegativeButton(getString(R.string.cancel_txt)) { dialogInterface, i ->
            dialogInterface.dismiss()
        }

        builder.show()
    }

    /**
     * Builds and displays a pop-up that allows exercises to be removed from the workout
     *
     * Uses:
     *  - custom_remove_exercise_dialog.xml
     *  - AddWorkoutExerciseListAdapter
     */
    private fun buildRemoveExerciseDialog(){
        val builder = AlertDialog.Builder(activity)
        builder.setTitle(getString(R.string.dialog_title_remove_exercise))
        builder.setCancelable(false)

        val inflater: LayoutInflater = requireActivity().getLayoutInflater()
        var view = inflater.inflate(R.layout.dialog_remove_exercise, null)

        var removeExRV = view.findViewById<RecyclerView>(R.id.remove_exercise_rv)
        removeExRV.layoutManager = LinearLayoutManager(activity)
        removeExRV.adapter = AddWorkoutExerciseListAdapter((activity as MainActivity), selectedExercises)

        builder.setView(view)

        builder.setPositiveButton(getString(R.string.done_txt)) { dialogInterface, i ->
            var deleteWorkoutExercise = ArrayList<WorkoutExercise>()
            for(workEx in selectedExercises){
                if(!workEx.isSelected){
                    deleteWorkoutExercise.add(workEx)
                }
            }

            for(workEx in deleteWorkoutExercise){
                selectedExercises.remove(workEx)
            }

            selectedExRecycler.adapter!!.notifyDataSetChanged()
            dialogInterface.dismiss()
        }

        builder.setNegativeButton(getString(R.string.cancel_txt)) { dialogInterface, i ->
            dialogInterface.dismiss()
        }

        builder.show()
    }

    /**
     * Adds workout to database, then adds WorkoutExercises for all the exercises
     * Calls the addTags function
     */
    private fun createWorkoutWithExercises(){
        val workout = Workout(workoutName.text.toString())
        workout.description = descTxt.text.toString()
        workout.isFavourited = isFavourited

        val dbHandler = DBHandler(this.requireContext(), null, null, 1)
        var workoutId = dbHandler.addWorkout(workout)

        if(workoutId != null && workoutId >= 0){
            workout.id = workoutId

            for(i in 0..selectedExercises.size-1){
                selectedExercises[i].workoutId = workoutId
                if(selectedExercises[i].orderNo < 0){
                    selectedExercises[i].orderNo = i
                }

                var result = dbHandler.addExerciseToWorkout(selectedExercises[i])
            }

            addTags(workout)
        }
    }

    /**
     * Adds any new tags to the database, then adds all tags to the workout provided
     */
    private fun addTags(workout: Workout){
        val dbHandler = DBHandler(this.requireContext(), null, null, 1)
        var splitTags = tagInput.text.split(" ")
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
     * Loads exercises into WorkoutExercise objects, so they can be "selected"
     */
    private fun loadExercisesIntoWorkout(exerciseList: ArrayList<Exercise>){
        for(exercise in exerciseList){
            var workEx = WorkoutExercise(exercise)
            workEx.exercise = exercise
            allWorkoutExercises.add(workEx)
        }
    }

    /**
     * Gets all the exercises, so the user can add any to the workout
     */
    private fun loadExercises(): ArrayList<Exercise>{
        val dbHandler = DBHandler(this.requireContext(), null, null, 1)
        var exerciseList = dbHandler.getAllExercises()!!
        return exerciseList
    }

    /**
     * Gets all existing tags, to ensure existing tags aren't re-added to database
     */
    private fun getAllTags(){
        val dbHandler = DBHandler(this.requireContext(), null, null, 1)
        allTags = dbHandler.getAllTags()
    }

    companion object {
        @JvmStatic
        fun newInstance() =
            CreateWorkoutFragment().apply { arguments = Bundle().apply { } }
    }
}