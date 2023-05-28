package com.example.fxxkit.Fragment

import android.app.AlertDialog
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
import com.example.fxxkit.ViewHolder.DetailWorkoutExerciseListAdapter
import com.example.fxxkit.ViewHolder.OrderExercisesListAdapter
import com.example.fxxkit.ViewHolder.WorkoutExerciseListAdapter
import com.example.fxxkit.ViewHolder.WorkoutListAdapter
import com.example.fxxkit.ViewModel.WorkoutViewModel

/**
 * Allows user to edit workout details and exercises
 */
class EditWorkoutFragment : Fragment() {
    private lateinit var currentWorkout: WorkoutViewModel
    private var allExerciseList: ArrayList<Exercise>? = null
    private var allTags: ArrayList<Tag> = ArrayList<Tag>()
    private var unedittedWorkoutExercises: ArrayList<WorkoutExercise> = ArrayList<WorkoutExercise>()
    private var unselectedExerciseList: ArrayList<Exercise> = ArrayList<Exercise>()
    private var selectedExercises: ArrayList<WorkoutExercise> = ArrayList<WorkoutExercise>()
    private var unselectedWorkExercises: ArrayList<WorkoutExercise> = ArrayList<WorkoutExercise>()
    private var removedWorkExercises: ArrayList<WorkoutExercise> = ArrayList<WorkoutExercise>()
    private var addedWorkExercises: ArrayList<WorkoutExercise> = ArrayList<WorkoutExercise>()

    private lateinit var workoutName: EditText
    private lateinit var cancelBtn: ImageButton
    private lateinit var updateBtn : ImageButton
    private lateinit var addExerciseBtn: Button
    private lateinit var orderExercisesBtn: Button
    private lateinit var removeExerciseBtn: Button
    private lateinit var favBtn: ImageButton
    private lateinit var descTxt: EditText
    private lateinit var tagInput: EditText
    private lateinit var selectedExRV: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        var workoutId = arguments!!.getInt("workoutId")
        loadWorkout(workoutId)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        var view = inflater.inflate(R.layout.fragment_edit_workout, container, false)
        (activity as MainActivity).getSupportActionBar()?.customView?.findViewById<TextView>(R.id.appbar_title_id)?.setText(getString(R.string.edit_workout_title))

        loadAllExercises()
        loadWorkoutExercises()
        getAllTags()
        filterExercisesBySelected()
        loadExercisesIntoWorkouts(unselectedExerciseList)

        workoutName = view.findViewById<EditText>(R.id.workout_name_txt)
        descTxt = view.findViewById<EditText>(R.id.description_txt)
        tagInput = view.findViewById<EditText>(R.id.tag_input)
        favBtn = view.findViewById<ImageButton>(R.id.fav_btn)
        updateBtn = view.findViewById<ImageButton>(R.id.update_btn)
        cancelBtn = view.findViewById<ImageButton>(R.id.cancel_btn)
        addExerciseBtn = view.findViewById<Button>(R.id.add_exercise_btn)
        orderExercisesBtn = view.findViewById<Button>(R.id.order_exercises_btn)
        removeExerciseBtn = view.findViewById<Button>(R.id.remove_exercise_btn)
        selectedExRV = view.findViewById<RecyclerView>(R.id.selected_ex_rv)

        selectedExRV.layoutManager = LinearLayoutManager(activity)
        selectedExRV.adapter = DetailWorkoutExerciseListAdapter(selectedExercises)

        workoutName.setText(currentWorkout.name)
        descTxt.setText(currentWorkout.description)
        tagInput.setText(currentWorkout.getTagInputString())
        setFavourite(true)

        addExerciseBtn.setOnClickListener { view -> buildAddExerciseDialog() }
        orderExercisesBtn.setOnClickListener { view -> buildOrderExercisesDialog() }
        removeExerciseBtn.setOnClickListener { view -> buildRemoveExerciseDialog() }
        favBtn.setOnClickListener { view -> setFavourite(!currentWorkout.isFavourited) }

        updateBtn.setOnClickListener{ view ->
            updateWorkoutWithExercises()
            (activity as MainActivity).navToPrevious()
        }
        cancelBtn.setOnClickListener { view ->
            (activity as MainActivity).navToPrevious()
        }

        return view
    }

    /**
     * Toggles image when favourited/not favourited
     */
    private fun setFavourite(isFavourited: Boolean){
        if(!isFavourited){
            currentWorkout.isFavourited = false
            favBtn.setImageResource(R.drawable.ic_star_filled)
        } else {
            currentWorkout.isFavourited = true
            favBtn.setImageResource(R.drawable.ic_star)
        }
    }

    /**
     * Builds and shows a dialog that allows users to select exercises to add to the workout
     * Uses:
     *  - custom_add_exercise_dialog
     *  - AddWorkoutExerciseListAdapter
     */
    private fun buildAddExerciseDialog(){
        val builder = AlertDialog.Builder(activity)
        builder.setTitle(getString(R.string.dialog_title_add_exercise))
        builder.setCancelable(false)

        val inflater: LayoutInflater = requireActivity().getLayoutInflater()
        var view = inflater.inflate(R.layout.custom_add_exercise_dialog, null)

        var addExRV = view.findViewById<RecyclerView>(R.id.add_exercise_rv)
        addExRV.layoutManager = LinearLayoutManager(activity)
        addExRV.adapter = AddWorkoutExerciseListAdapter((activity as MainActivity), unselectedWorkExercises)

        builder.setView(view)

        builder.setPositiveButton(getString(R.string.done_txt)) { dialogInterface, i ->
            for(workEx in unselectedWorkExercises){
                if(workEx.isSelected){
                    addedWorkExercises.add(workEx)
                }
            }

            for(workEx in addedWorkExercises){
                unselectedWorkExercises.remove(workEx)
                workEx.workoutId = currentWorkout.id
                selectedExercises.add(workEx)
                selectedExRV.adapter!!.notifyDataSetChanged()
            }
        }

        builder.setNegativeButton(getString(R.string.cancel_txt)) { dialogInterface, i ->
            dialogInterface.dismiss()
        }

        builder.show()
    }

    /**
     * Builds and shows a dialog that allows the user to rearrange the exercises order in the workout
     * Uses:
     *  - custom_order_exercises_dialog
     *  - OrderExercisesListAdapter
     */
    private fun buildOrderExercisesDialog(){
        val builder = AlertDialog.Builder(activity)
        builder.setTitle(getString(R.string.dialog_title_order_exercises))
        builder.setCancelable(false)

        val inflater: LayoutInflater = requireActivity().getLayoutInflater()
        var view = inflater.inflate(R.layout.custom_order_exercises_dialog, null)

        var orderExRV = view.findViewById<RecyclerView>(R.id.order_exercises_rv)
        orderExRV.layoutManager = LinearLayoutManager(activity)
        orderExRV.adapter = OrderExercisesListAdapter((activity as MainActivity), selectedExercises)

        builder.setView(view)

        builder.setPositiveButton(getString(R.string.done_txt)) { dialogInterface, i ->
            dialogInterface.dismiss()
            selectedExercises.sortBy { it.orderNo }
            selectedExRV.adapter?.notifyDataSetChanged()
        }

        builder.setNegativeButton(getString(R.string.cancel_txt)) { dialogInterface, i ->
            dialogInterface.dismiss()
        }

        builder.show()
    }

    /**
     * Builds and displays a dialog that allows users to remove exercises from the workout
     * Uses:
     *  - custom_remove_exercise_dialog
     *  - AddWorkoutExerciseListAdapter
     */
    private fun buildRemoveExerciseDialog(){
        val builder = AlertDialog.Builder(activity)
        builder.setTitle(getString(R.string.dialog_title_remove_exercise))
        builder.setCancelable(false)

        val inflater: LayoutInflater = requireActivity().getLayoutInflater()
        var view = inflater.inflate(R.layout.custom_remove_exercise_dialog, null)

        var removeExRV = view.findViewById<RecyclerView>(R.id.remove_exercise_rv)
        removeExRV.layoutManager = LinearLayoutManager(activity)
        removeExRV.adapter = AddWorkoutExerciseListAdapter((activity as MainActivity), selectedExercises)

        builder.setView(view)

        builder.setPositiveButton(getString(R.string.done_txt)) { dialogInterface, i ->
            for(workEx in selectedExercises){
                if(workEx.isSelected){
                    removedWorkExercises.add(workEx)
                }
            }

            for(workEx in removedWorkExercises){
                selectedExercises.remove(workEx)
                unselectedWorkExercises.add(workEx)
                selectedExRV.adapter!!.notifyDataSetChanged()
            }
        }

        builder.setNegativeButton(getString(R.string.cancel_txt)) { dialogInterface, i ->
            dialogInterface.dismiss()
        }

        builder.show()
    }

    /**
     * Loads the workout from the initial id
     */
    private fun loadWorkout(workoutId: Int){
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
        } else {
            println(getString(R.string.error_workout_not_found))
        }
    }

    /**
     * Takes all exercises and loads them into WorkoutExercise objects, so they can be selected and ordered
     */
    private fun loadExercisesIntoWorkouts(exerciseList: ArrayList<Exercise>){
        for(exercise in exerciseList){
            var workEx = WorkoutExercise(exercise)
            workEx.exercise = exercise
            unselectedWorkExercises.add(workEx)
        }
    }

    /**
     * ??
     */
    private fun filterExercisesBySelected(){
        if(allExerciseList != null && allExerciseList!!.size > 0){
            unselectedExerciseList = allExerciseList!!.clone() as ArrayList<Exercise>

            for(workExercise in selectedExercises){
                var element = unselectedExerciseList.firstOrNull{it.id == workExercise.exerciseId}
                if(element != null){
                    workExercise.exercise = element
                    unselectedExerciseList.remove(element)
                }
            }
        }
    }

    /**
     * Loads all the WorkoutExercises for a given Workout
     */
    private fun loadWorkoutExercises(){
        val dbHandler = DBHandler(this.requireContext(), null, null, 1)
        selectedExercises = dbHandler.findAllWorkoutExercises(currentWorkout.castWorkoutVMToWorkout())
        selectedExercises.sortBy { it.orderNo }

        for(workEx in selectedExercises){
            workEx.isSelected = true
            unedittedWorkoutExercises.add(WorkoutExercise(workEx.id, workEx.workoutId, workEx.exerciseId, workEx.setSize!!, workEx.repSize!!, workEx.orderNo))
        }
    }

    /**
     * Loads all exercises that exist in the database
     */
    private fun loadAllExercises(){
        val dbHandler = DBHandler(this.requireContext(), null, null, 1)
        allExerciseList = dbHandler.getAllExercises()
    }

    /**
     * Loads all tags that exist in the database
     */
    private fun getAllTags(){
        val dbHandler = DBHandler(this.requireContext(), null, null, 1)
        allTags = dbHandler.getAllTags()
    }

    /**
     * Updates the workout, and the associated WorkoutExercises
     */
    private fun updateWorkoutWithExercises(){
        val dbHandler = DBHandler(this.requireContext(), null, null, 1)

        // Workout details
        currentWorkout.name = workoutName.text.toString()
        currentWorkout.description = descTxt.text.toString()
        var updatedWorkout = currentWorkout.castWorkoutVMToWorkout()
        dbHandler.updateWorkout(updatedWorkout)

        // Workout exercises, added and removed
        for(workEx in removedWorkExercises){ dbHandler.deleteWorkoutExercise(workEx.id) }
        for(workEx in addedWorkExercises){ dbHandler.addExerciseToWorkout(workEx) }
        selectedExercises.sortBy { it.orderNo }
        normaliseWorkExercises()
        for(workEx in selectedExercises){ checkAndUpdate(dbHandler, workEx)}

        // Tags, added and removed
        updateTags(dbHandler)
    }

    /**
     * Goes through, making sure there are not duplicate order numbers in the WorkoutExercises
     */
    private fun normaliseWorkExercises(){
        for(i in 0..selectedExercises.size-1){
            var allOfOrder = selectedExercises.filter{it.orderNo == selectedExercises[i].orderNo}
            if(allOfOrder.size > 1){
                for(j in 1..allOfOrder.size-1){
                    allOfOrder[j].orderNo += 1
                }
            }
        }
    }

    /**
     * Checks if a WorkoutExercise exists, and has had the order number changed. If yes, updates
     */
    private fun checkAndUpdate(dbHandler: DBHandler, workoutExercise: WorkoutExercise){
        var unchangedWorkEx = unedittedWorkoutExercises.firstOrNull { it.id == workoutExercise.id }

        if(unchangedWorkEx!= null){
            if(workoutExercise.orderNo != unchangedWorkEx.orderNo){
                dbHandler.updateWorkoutExercise(workoutExercise)
            }
        }
    }

    /**
     * Adds new tags, and removes WorkoutTags that are no longer needed
     */
    private fun updateTags(dbHandler: DBHandler){
        var splitTags = tagInput.text.split(" ")

        // Adding new ones
        for(tag in splitTags){
            var foundTag = currentWorkout.tags.firstOrNull{it.name!!.lowercase() == tag.toString().lowercase()}

            if(foundTag == null){
                foundTag = allTags.firstOrNull{ it.name!!.lowercase() == tag.toString().lowercase() }

                if(foundTag == null){
                    foundTag = Tag(tag.toString().lowercase())
                    foundTag.id = dbHandler.addTag(foundTag)!!
                }

                if(foundTag.id != null){
                    var result = dbHandler.addTagToWorkoutByIds(currentWorkout.id, foundTag.id)
                }
            }
        }

        // Removes old tags that have been removed from Workout
        for(tag in currentWorkout.tags){
            var foundTag = splitTags.firstOrNull{it.lowercase() == tag.name!!.lowercase()}

            if(foundTag == null){
                dbHandler.deleteTagFromWorkoutById(currentWorkout.id, tag)
            }
        }
    }


    companion object {
        @JvmStatic
        fun newInstance(workoutVM: WorkoutViewModel) =
            EditWorkoutFragment().apply { arguments = Bundle().apply { currentWorkout = workoutVM  } }
    }
}