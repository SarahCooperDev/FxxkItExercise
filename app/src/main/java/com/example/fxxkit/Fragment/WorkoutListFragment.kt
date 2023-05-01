package com.example.fxxkit.Fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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

class WorkoutListFragment : Fragment() {
    private var layoutManager: RecyclerView.LayoutManager? = null
    private var workoutListAdapter: RecyclerView.Adapter<WorkoutListAdapter.WorkoutListViewHolder>? = null
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
        var recycler = view.findViewById<RecyclerView>(R.id.workout_list_rv)
        recycler.layoutManager = LinearLayoutManager(activity)

        workoutList = ArrayList<WorkoutViewModel>()
        loadWorkouts(view)

        recycler.adapter = WorkoutListAdapter((activity as MainActivity), workoutList)

        return view
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