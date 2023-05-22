package com.example.fxxkit.Fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.fxxkit.DBHandler
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
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    private var workExercises: ArrayList<WorkoutExercise> = ArrayList<WorkoutExercise>()

    private lateinit var exerciseRV: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //workExercises = arguments!!.getBundle ("workoutExercises")
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        var view = inflater.inflate(R.layout.fragment_suggested_workout, container, false)
        (activity as MainActivity).getSupportActionBar()?.customView?.findViewById<TextView>(R.id.appbar_title_id)?.setText("New Workout")

        exerciseRV = view.findViewById(R.id.exercise_rv)

        loadWorkoutExercises()

        exerciseRV.layoutManager = LinearLayoutManager(activity)
        exerciseRV.adapter = DetailWorkoutExerciseListAdapter(workExercises)

        return view
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
        fun newInstance(param1: String, param2: String) =
            SuggestedWorkoutFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}