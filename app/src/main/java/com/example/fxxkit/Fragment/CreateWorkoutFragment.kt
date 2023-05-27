package com.example.fxxkit.Fragment

import android.app.AlertDialog
import android.graphics.Color
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.fxxkit.*
import com.example.fxxkit.DataClass.Exercise
import com.example.fxxkit.DataClass.Tag
import com.example.fxxkit.DataClass.Workout
import com.example.fxxkit.DataClass.WorkoutExercise
import com.example.fxxkit.ViewHolder.OrderExercisesListAdapter

/**
 * A simple [Fragment] subclass.
 * Use the [CreateWorkoutFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class CreateWorkoutFragment : Fragment() {
    private var isFavourited: Boolean = false
    private lateinit var workoutName: EditText
    private lateinit var addExerciseBtn: Button
    private lateinit var removeExerciseBtn: Button
    private lateinit var createBtn: ImageButton
    private lateinit var cancelBtn: ImageButton
    private lateinit var favBtn: ImageButton
    private lateinit var descTxt: EditText
    private lateinit var tagInput: EditText
    private lateinit var selectedExRecycler: RecyclerView
    private var workoutExerciseList: ArrayList<WorkoutExercise> = ArrayList<WorkoutExercise>()
    private var allWorkoutExercises: ArrayList<WorkoutExercise> = ArrayList<WorkoutExercise>()
    private var selectedExercises: ArrayList<WorkoutExercise> = ArrayList<WorkoutExercise>()
    private var allTags = ArrayList<Tag>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_create_workout, container, false)
        (activity as MainActivity).getSupportActionBar()?.customView?.findViewById<TextView>(R.id.appbar_title_id)?.setText("Create Workout")
        selectedExRecycler = view.findViewById<RecyclerView>(R.id.exercise_list_rv)
        selectedExRecycler.layoutManager = LinearLayoutManager(activity)

        var exerciseList = loadExercises()
        getAllTags()
        loadExercisesIntoWorkout(exerciseList)

        selectedExRecycler.adapter = OrderExercisesListAdapter((activity as MainActivity), selectedExercises)

        workoutName = view.findViewById<EditText>(R.id.workout_name_txt)
        createBtn = view.findViewById<ImageButton>(R.id.create_btn)
        cancelBtn = view.findViewById<ImageButton>(R.id.cancel_btn)
        favBtn = view.findViewById<ImageButton>(R.id.fav_btn)
        descTxt = view.findViewById<EditText>(R.id.description_txt)
        tagInput = view.findViewById<EditText>(R.id.tag_input)
        addExerciseBtn = view.findViewById(R.id.add_exercise_btn)
        removeExerciseBtn = view.findViewById(R.id.remove_exercise_btn)

        addExerciseBtn.setOnClickListener { view ->
            buildAddExerciseDialog()
        }

        removeExerciseBtn.setOnClickListener { view ->
            buildRemoveExerciseDialog()
        }


        createBtn.setOnClickListener{ view ->
            if(workoutName.text.toString().length < 1){
                Toast.makeText(activity, "Workout name may not be blank", Toast.LENGTH_LONG).show()
                workoutName.setBackgroundColor(ContextCompat.getColor(context!!, R.color.dark_red))
            } else {
                addWorkoutWithExercises()
                Toast.makeText(activity, "Added workout to database", Toast.LENGTH_SHORT).show()
                (activity as MainActivity).navToWorkoutList()
            }
        }

        cancelBtn.setOnClickListener { view ->
            (activity as MainActivity).navToPrevious()
        }

        favBtn.setOnClickListener { view ->
            if(isFavourited){
                isFavourited = false
                favBtn.setImageResource(R.drawable.ic_star)
            } else {
                isFavourited = true
                favBtn.setImageResource(R.drawable.ic_star_filled)
            }
        }

        return view
    }

    private fun buildAddExerciseDialog(){
        val builder = AlertDialog.Builder(activity)
        builder.setTitle("Add Exercise")
        builder.setCancelable(false)

        val inflater: LayoutInflater = requireActivity().getLayoutInflater()
        var view = inflater.inflate(R.layout.custom_add_exercise_dialog, null)

        var addExRV = view.findViewById<RecyclerView>(R.id.add_exercise_rv)
        addExRV.layoutManager = LinearLayoutManager(activity)
        allWorkoutExercises.sortBy { it.isSelected }
        addExRV.adapter = AddWorkoutExerciseListAdapter((activity as MainActivity), allWorkoutExercises)

        builder.setView(view)

        builder.setPositiveButton("Done") { dialogInterface, i ->
            for(workEx in allWorkoutExercises){
                if(workEx.isSelected){
                    selectedExercises.add(workEx)
                }
            }

            selectedExRecycler.adapter!!.notifyDataSetChanged()
            dialogInterface.dismiss()
        }

        builder.setNegativeButton("Cancel") { dialogInterface, i ->
            dialogInterface.dismiss()
        }

        builder.show()
    }

    private fun buildRemoveExerciseDialog(){
        val builder = AlertDialog.Builder(activity)
        builder.setTitle("Remove Exercise")
        builder.setCancelable(false)

        val inflater: LayoutInflater = requireActivity().getLayoutInflater()
        var view = inflater.inflate(R.layout.custom_remove_exercise_dialog, null)

        var removeExRV = view.findViewById<RecyclerView>(R.id.remove_exercise_rv)
        removeExRV.layoutManager = LinearLayoutManager(activity)
        removeExRV.adapter = AddWorkoutExerciseListAdapter((activity as MainActivity), selectedExercises)

        builder.setView(view)

        builder.setPositiveButton("Done") { dialogInterface, i ->
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

        builder.setNegativeButton("Cancel") { dialogInterface, i ->
            dialogInterface.dismiss()
        }

        builder.show()
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

            for(i in 0..selectedExercises.size-1){
                selectedExercises[i].workoutId = workoutId
                if(selectedExercises[i].orderNo < 0){
                    selectedExercises[i].orderNo = i
                }

                var result = dbHandler.addExerciseToWorkout(selectedExercises[i])
            }

            var splitTags = tagInput.text.split(" ")
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
    }

    private fun loadExercisesIntoWorkout(exerciseList: ArrayList<Exercise>){
        for(exercise in exerciseList){
            var workEx = WorkoutExercise(exercise)
            workEx.exercise = exercise
            allWorkoutExercises.add(workEx)
        }
    }

    private fun loadExercises(): ArrayList<Exercise>{
        val dbHandler = DBHandler(this.requireContext(), null, null, 1)
        var exerciseList = dbHandler.getAllExercises()!!
        return exerciseList
    }

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