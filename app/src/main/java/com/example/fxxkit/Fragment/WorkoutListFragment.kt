package com.example.fxxkit.Fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.PopupMenu
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.fxxkit.DBHandler
import com.example.fxxkit.DataClass.Exercise
import com.example.fxxkit.DataClass.WorkoutExercise
import com.example.fxxkit.MainActivity
import com.example.fxxkit.R
import com.example.fxxkit.ViewHolder.WorkoutListAdapter
import com.example.fxxkit.ViewModel.WorkoutViewModel
import com.google.android.material.floatingactionbutton.FloatingActionButton

class WorkoutListFragment : Fragment() {
    private lateinit var createWorkoutBtn : FloatingActionButton
    private lateinit var sortBtn: ImageButton
    private lateinit var workoutListRecycler: RecyclerView
    private lateinit var workoutList: ArrayList<WorkoutViewModel>
    private var expandedSize = ArrayList<Int>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        var view = inflater.inflate(R.layout.fragment_workout_list, container, false)

        createWorkoutBtn = view.findViewById<FloatingActionButton>(R.id.create_workout_btn)
        sortBtn = view.findViewById<ImageButton>(R.id.sort_btn)
        workoutListRecycler = view.findViewById<RecyclerView>(R.id.workout_list_rv)
        workoutListRecycler.layoutManager = LinearLayoutManager(activity)

        workoutList = ArrayList<WorkoutViewModel>()
        loadWorkouts(view)

        workoutListRecycler.adapter = WorkoutListAdapter((activity as MainActivity), workoutList)

        createWorkoutBtn.setOnClickListener { view -> (activity as MainActivity).navToCreateWorkout() }
        sortBtn.setOnClickListener {
            val sortPopup = PopupMenu(activity, sortBtn)
            sortPopup.menuInflater.inflate(R.menu.workout_list_sort_menu, sortPopup.menu)
            sortPopup.setOnMenuItemClickListener { menuItem ->
                println("Clicked menu item")
                when(menuItem.itemId){
                    R.id.alpha_item -> { sortByAlpha() }
                    R.id.reverse_alpha_item -> { sortByReverseAlpha() }
                    R.id.chrono_item -> { sortByChrono() }
                    R.id.reverse_chrono_item -> { sortByReverseChrono() }
                }
                true
            }
            sortPopup.show()
        }

        return view
    }

    private fun sortByChrono() {
        println("Chronologically")
        workoutList.sortBy{ it.id }
        workoutListRecycler.adapter?.notifyDataSetChanged()
    }

    private fun sortByReverseChrono(){
        println("Reverse Chrono")
        workoutList.sortByDescending { it.id }
        workoutListRecycler.adapter?.notifyDataSetChanged()
    }

    private fun sortByAlpha(){
        println("Alphabetically")
        workoutList.sortBy { it.name }
        workoutListRecycler.adapter?.notifyDataSetChanged()
    }

    private fun sortByReverseAlpha(){
        println("Reverse Alpha")
        workoutList.sortByDescending { it.name }
        workoutListRecycler.adapter?.notifyDataSetChanged()
    }

    fun loadWorkouts(view: View){
        val dbHandler = DBHandler(this.requireContext(), null, null, 1)
        val workouts = dbHandler.getAllWorkouts()

        if(workouts != null && workouts.size > 0){
            for(workout in workouts){
                var workoutVM = WorkoutViewModel(workout.id, workout.workoutName!!)
                workoutVM.workExList = dbHandler.findAllWorkoutExercises(workout)

                for(workEx in workoutVM.workExList){
                    if(workEx.exerciseId > -1){
                        var exercise = dbHandler.findExerciseById(workEx.exerciseId)
                        workEx.exercise = exercise
                        workEx.isSelected = true
                    }
                }
                workoutList.add(workoutVM)
            }
        }
    }

    private fun setCellSize(){
        expandedSize = ArrayList()
        for(i in 0 until workoutList.size){
            expandedSize.add(0)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }

    companion object {
        @JvmStatic
        fun newInstance() =
            WorkoutListFragment().apply {}
    }
}