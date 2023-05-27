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

class EditWorkoutFragment : Fragment() {
    private lateinit var currentWorkout: WorkoutViewModel
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
    private var allExerciseList: ArrayList<Exercise>? = null
    private var allTags: ArrayList<Tag> = ArrayList<Tag>()
    private var unedittedWorkoutExercises: ArrayList<WorkoutExercise> = ArrayList<WorkoutExercise>()
    private var unselectedExerciseList: ArrayList<Exercise> = ArrayList<Exercise>()
    private var selectedExercises: ArrayList<WorkoutExercise> = ArrayList<WorkoutExercise>()
    private var unselectedWorkExercises: ArrayList<WorkoutExercise> = ArrayList<WorkoutExercise>()
    private var removedWorkExercises: ArrayList<WorkoutExercise> = ArrayList<WorkoutExercise>()
    private var addedWorkExercises: ArrayList<WorkoutExercise> = ArrayList<WorkoutExercise>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        var workoutId = arguments!!.getInt("workoutId")
        loadWorkout(workoutId)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        var view = inflater.inflate(R.layout.fragment_edit_workout, container, false)
        (activity as MainActivity).getSupportActionBar()?.customView?.findViewById<TextView>(R.id.appbar_title_id)?.setText("Edit Workout")

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

    private fun setFavourite(isFavourited: Boolean){
        if(!isFavourited){
            currentWorkout.isFavourited = false
            favBtn.setImageResource(R.drawable.ic_star_filled)
        } else {
            currentWorkout.isFavourited = true
            favBtn.setImageResource(R.drawable.ic_star)
        }
    }

    private fun buildAddExerciseDialog(){
        val builder = AlertDialog.Builder(activity)
        builder.setTitle("Add Exercise")
        builder.setCancelable(false)

        val inflater: LayoutInflater = requireActivity().getLayoutInflater()
        var view = inflater.inflate(R.layout.custom_add_exercise_dialog, null)

        var addExRV = view.findViewById<RecyclerView>(R.id.add_exercise_rv)
        addExRV.layoutManager = LinearLayoutManager(activity)
        addExRV.adapter = AddWorkoutExerciseListAdapter((activity as MainActivity), unselectedWorkExercises)

        builder.setView(view)

        builder.setPositiveButton("Done") { dialogInterface, i ->
            for(workEx in unselectedWorkExercises){
                println("Exercise is selected to be ${workEx.isSelected.toString()}")

                if(workEx.isSelected){
                    addedWorkExercises.add(workEx)
                }
            }

            for(workEx in addedWorkExercises){
                println("Sets is ${workEx.setSize} and reps is ${workEx.repSize}")
                unselectedWorkExercises.remove(workEx)
                workEx.workoutId = currentWorkout.id
                selectedExercises.add(workEx)
                selectedExRV.adapter!!.notifyDataSetChanged()
            }
        }

        builder.setNegativeButton("Cancel") { dialogInterface, i ->
            dialogInterface.dismiss()
        }

        builder.show()
    }

    private fun buildOrderExercisesDialog(){
        val builder = AlertDialog.Builder(activity)
        builder.setTitle("Order Exercises")
        builder.setCancelable(false)

        val inflater: LayoutInflater = requireActivity().getLayoutInflater()
        var view = inflater.inflate(R.layout.custom_order_exercises_dialog, null)

        var orderExRV = view.findViewById<RecyclerView>(R.id.order_exercises_rv)
        orderExRV.layoutManager = LinearLayoutManager(activity)
        orderExRV.adapter = OrderExercisesListAdapter((activity as MainActivity), selectedExercises)

        builder.setView(view)

        builder.setPositiveButton("Done") { dialogInterface, i ->
            dialogInterface.dismiss()
            selectedExercises.sortBy { it.orderNo }
            selectedExRV.adapter?.notifyDataSetChanged()
        }

        builder.setNegativeButton("Cancel") { dialogInterface, i ->
            dialogInterface.dismiss()
        }

        builder.show()
    }

    private fun buildRemoveExerciseDialog(){
        val builder = AlertDialog.Builder(activity)
        builder.setTitle("Remove/Edit Exercise")
        builder.setCancelable(false)

        val inflater: LayoutInflater = requireActivity().getLayoutInflater()
        var view = inflater.inflate(R.layout.custom_remove_exercise_dialog, null)

        var removeExRV = view.findViewById<RecyclerView>(R.id.remove_exercise_rv)
        removeExRV.layoutManager = LinearLayoutManager(activity)
        removeExRV.adapter = AddWorkoutExerciseListAdapter((activity as MainActivity), selectedExercises)

        builder.setView(view)

        builder.setPositiveButton("Done") { dialogInterface, i ->
            for(workEx in selectedExercises){
                println("Exercise is selected to be ${workEx.isSelected.toString()}")
                if(workEx.isSelected){
                    println("Workex id is: ${workEx.id.toString()}")
                    removedWorkExercises.add(workEx)
                }
            }

            for(workEx in removedWorkExercises){
                selectedExercises.remove(workEx)
                unselectedWorkExercises.add(workEx)
                selectedExRV.adapter!!.notifyDataSetChanged()
            }
        }

        builder.setNegativeButton("Cancel") { dialogInterface, i ->
            dialogInterface.dismiss()
        }

        builder.show()
    }

    private fun loadWorkout(workoutId: Int){
        val dbHandler = DBHandler(this.requireContext(), null, null, 1)
        var workout = dbHandler.findWorkoutById(workoutId)
        if(workout !=  null){
            currentWorkout = workout.workoutName?.let { WorkoutViewModel(workout.id, it) }!!
            currentWorkout.description = workout.description
            currentWorkout.isFavourited = workout.isFavourited

            var workoutTags = dbHandler.getTagsForWorkout(workout)
            println("There are ${workoutTags.size} tags")
            if(workoutTags != null){
                workout.tags = workoutTags
                currentWorkout.tags = workoutTags
            }
        } else {
            println("Error: workout not found")
        }
    }
    private fun loadExercisesIntoWorkouts(exerciseList: ArrayList<Exercise>){
        for(exercise in exerciseList){
            var workEx = WorkoutExercise(exercise)
            workEx.exercise = exercise
            unselectedWorkExercises.add(workEx)
        }
    }

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

    private fun loadWorkoutExercises(){
        val dbHandler = DBHandler(this.requireContext(), null, null, 1)
        selectedExercises = dbHandler.findAllWorkoutExercises(currentWorkout.castWorkoutVMToWorkout())
        selectedExercises.sortBy { it.orderNo }

        for(workEx in selectedExercises){
            workEx.isSelected = true
            unedittedWorkoutExercises.add(WorkoutExercise(workEx.id, workEx.workoutId, workEx.exerciseId, workEx.setSize!!, workEx.repSize!!, workEx.orderNo))
        }
    }

    private fun loadAllExercises(){
        val dbHandler = DBHandler(this.requireContext(), null, null, 1)
        allExerciseList = dbHandler.getAllExercises()
    }

    private fun getAllTags(){
        val dbHandler = DBHandler(this.requireContext(), null, null, 1)
        allTags = dbHandler.getAllTags()
    }

    private fun updateWorkoutWithExercises(){
        val dbHandler = DBHandler(this.requireContext(), null, null, 1)

        currentWorkout.name = workoutName.text.toString()
        currentWorkout.description = descTxt.text.toString()
        var updatedWorkout = currentWorkout.castWorkoutVMToWorkout()
        dbHandler.updateWorkout(updatedWorkout)

        for(workEx in removedWorkExercises){ dbHandler.deleteWorkoutExercise(workEx.id) }
        for(workEx in addedWorkExercises){ dbHandler.addExerciseToWorkout(workEx) }
        selectedExercises.sortBy { it.orderNo }
        normaliseWorkExercises()
        for(workEx in selectedExercises){ checkAndUpdate(dbHandler, workEx)}

        updateTags(dbHandler)
    }

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

    private fun checkAndUpdate(dbHandler: DBHandler, workoutExercise: WorkoutExercise){
        println("Checking workex ${workoutExercise.id} of ${workoutExercise.exercise!!.name}")
        var unchangedWorkEx = unedittedWorkoutExercises.firstOrNull { it.id == workoutExercise.id }

        if(unchangedWorkEx!= null){
            if(workoutExercise.orderNo != unchangedWorkEx.orderNo){
                println("Old is ${unchangedWorkEx.orderNo}, new orderno is ${workoutExercise.orderNo}")
                dbHandler.updateWorkoutExercise(workoutExercise)
            } else {
                println("order no ${workoutExercise.orderNo} is the same as ${unchangedWorkEx.orderNo}")
            }
        } else {
            println("Couldn't find unchanged workex")
        }
    }

    private fun updateTags(dbHandler: DBHandler){
        var splitTags = tagInput.text.split(" ")
        for(tag in splitTags){
            var foundTag = currentWorkout.tags.firstOrNull{it.name!!.lowercase() == tag.toString().lowercase()}

            if(foundTag == null){
                foundTag = allTags.firstOrNull{ it.name!!.lowercase() == tag.toString().lowercase() }

                if(foundTag == null){
                    foundTag = Tag(tag.toString().lowercase())
                    foundTag.id = dbHandler.addTag(foundTag)!!
                    println("New tag ${tag} is ${foundTag.id}")
                }

                if(foundTag.id != null){
                    var result = dbHandler.addTagToWorkoutByIds(currentWorkout.id, foundTag.id)
                    println("Result is ${result}")
                }
            }
        }

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