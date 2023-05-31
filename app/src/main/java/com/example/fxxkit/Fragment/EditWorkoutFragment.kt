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
import com.example.fxxkit.DataClass.WorkoutExercise
import com.example.fxxkit.ViewHolder.DetailWorkoutExerciseListAdapter
import com.example.fxxkit.ViewHolder.OrderExercisesListAdapter
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

    private lateinit var nameInput: EditText
    private lateinit var cancelBtn: ImageButton
    private lateinit var updateBtn : ImageButton
    private lateinit var addExerciseBtn: Button
    private lateinit var orderExercisesBtn: Button
    private lateinit var removeExerciseBtn: Button
    private lateinit var favBtn: ImageButton
    private lateinit var descriptionInput: EditText
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

        nameInput = view.findViewById(R.id.workout_name_txt)
        descriptionInput = view.findViewById(R.id.description_txt)
        tagInput = view.findViewById(R.id.tag_input)
        favBtn = view.findViewById(R.id.fav_btn)
        updateBtn = view.findViewById(R.id.update_btn)
        cancelBtn = view.findViewById(R.id.cancel_btn)
        addExerciseBtn = view.findViewById(R.id.add_exercise_btn)
        orderExercisesBtn = view.findViewById(R.id.order_exercises_btn)
        removeExerciseBtn = view.findViewById(R.id.remove_exercise_btn)
        selectedExRV = view.findViewById(R.id.selected_ex_rv)

        selectedExRV.layoutManager = LinearLayoutManager(activity)
        selectedExRV.adapter = DetailWorkoutExerciseListAdapter(selectedExercises)

        nameInput.setText(currentWorkout.name)
        descriptionInput.setText(currentWorkout.description)
        tagInput.setText(currentWorkout.getTagInputString())
        setFavourite(!currentWorkout.isFavourited)

        addExerciseBtn.setOnClickListener { view -> buildAddExerciseDialog() }
        orderExercisesBtn.setOnClickListener { view -> buildOrderExercisesDialog() }
        removeExerciseBtn.setOnClickListener { view -> buildRemoveExerciseDialog() }
        favBtn.setOnClickListener { view -> setFavourite(currentWorkout.isFavourited) }

        updateBtn.setOnClickListener{ view ->
            if(nameInput.text.toString().length < 1){
                Toast.makeText(activity, getString(R.string.error_blank_name_txt), Toast.LENGTH_LONG).show()
                nameInput.setBackgroundColor(Color.parseColor(getString(R.string.colour_error)))
            } else {
                updateWorkoutWithExercises()
                Toast.makeText(activity, "Updated workout ${nameInput.text.toString()}", Toast.LENGTH_SHORT).show()
                (activity as MainActivity).navToWorkoutList()
            }
        }
        cancelBtn.setOnClickListener { view ->
            (activity as MainActivity).navToWorkoutList()
        }

        return view
    }

    /**
     * Toggles image when favourited/not favourited
     */
    private fun setFavourite(isFavourited: Boolean){
        if(isFavourited){
            currentWorkout.isFavourited = false
            favBtn.setImageResource(R.drawable.ic_star)
        } else {
            currentWorkout.isFavourited = true
            favBtn.setImageResource(R.drawable.ic_star_filled)
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
        var view = inflater.inflate(R.layout.dialog_add_exercise, null)

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
        var view = inflater.inflate(R.layout.dialog_order_exercises, null)

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
        var view = inflater.inflate(R.layout.dialog_remove_exercise, null)

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
            currentWorkout = WorkoutViewModel(workout)

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
        currentWorkout.name = nameInput.text.toString()
        currentWorkout.description = descriptionInput.text.toString()
        var updatedWorkout = currentWorkout.castWorkoutVMToWorkout()
        updatedWorkout.setUpdatedDate()
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
            tag.trim()
            if(tag.length > 0){
                var foundTag = currentWorkout.tags.firstOrNull{it.name!!.lowercase() == tag.lowercase()}

                if(foundTag == null){
                    foundTag = allTags.firstOrNull{ it.name!!.lowercase() == tag.lowercase() }

                    if(foundTag == null){
                        foundTag = Tag(tag.lowercase())
                        foundTag.id = dbHandler.addTag(foundTag)!!
                    }

                    if(foundTag.id != null){
                        var result = dbHandler.addTagToWorkoutByIds(currentWorkout.id, foundTag.id)
                    }
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