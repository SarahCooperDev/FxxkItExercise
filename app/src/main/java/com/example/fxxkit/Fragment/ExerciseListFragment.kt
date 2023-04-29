package com.example.fxxkit.Fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.fxxkit.DBHandler
import com.example.fxxkit.ExerciseListAdapter
import com.example.fxxkit.MainActivity
import com.example.fxxkit.R
import com.example.fxxkit.ViewModel.ExerciseViewModel

/**
 * A simple [Fragment] subclass.
 * Use the [ExerciseListFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class ExerciseListFragment : Fragment() {

    private var layoutManager: RecyclerView.LayoutManager? = null
    private var exerciseListAdapter: RecyclerView.Adapter<ExerciseListAdapter.ExerciseListViewHolder>? = null
    private lateinit var exerciseList: ArrayList<ExerciseViewModel>


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        var view = inflater.inflate(R.layout.fragment_exercise_list, container, false)
        var recycler = view.findViewById<RecyclerView>(R.id.exercise_list_rv)
        recycler.layoutManager = LinearLayoutManager(activity)

        exerciseList = ArrayList<ExerciseViewModel>()

        getExercises(view)

        recycler.adapter = ExerciseListAdapter(exerciseList)

        return view
    }

    private fun getExercises(view: View){
        val dbHandler = DBHandler(this.requireContext(), null, null, 1)
        val exercises = dbHandler.getAllExercises()

        if (exercises != null) {
            if(exercises.size > 0){
                for(exercise in exercises){
                    exerciseList.add(ExerciseViewModel(exercise.id, exercise.exerciseName, 30))
                }
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }

    companion object {

        @JvmStatic
        fun newInstance() =
            ExerciseListFragment().apply {
                arguments = Bundle().apply {                }
            }
    }
}