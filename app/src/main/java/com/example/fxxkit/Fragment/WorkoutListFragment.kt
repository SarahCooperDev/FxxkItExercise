package com.example.fxxkit.Fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.fxxkit.DBHandler
import com.example.fxxkit.R
import com.example.fxxkit.ViewHolder.WorkoutListAdapter
import com.example.fxxkit.ViewModel.WorkoutViewModel

/**
 * A simple [Fragment] subclass.
 * Use the [WorkoutListFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
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

        getWorkouts(view)

        recycler.adapter = WorkoutListAdapter(workoutList)

        return view
    }

    fun getWorkouts(view: View){
        val dbHandler = DBHandler(this.requireContext(), null, null, 1)
        val workouts = dbHandler.getAllWorkouts()

        if(workouts != null && workouts.size > 0){
            for(workout in workouts){
                workoutList.add(WorkoutViewModel(workout.workoutName))
            }

            println("Workouts successfully loaded")
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