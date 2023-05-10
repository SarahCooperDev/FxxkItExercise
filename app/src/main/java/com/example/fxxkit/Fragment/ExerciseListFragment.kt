package com.example.fxxkit.Fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.fxxkit.DBHandler
import com.example.fxxkit.DataClass.Exercise
import com.example.fxxkit.ExerciseListAdapter
import com.example.fxxkit.MainActivity
import com.example.fxxkit.R
import com.google.android.material.floatingactionbutton.FloatingActionButton

/**
 * A simple [Fragment] subclass.
 * Use the [ExerciseListFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class ExerciseListFragment : Fragment() {

    private lateinit var addExerciseBtn : FloatingActionButton
    private var exerciseListAdapter: RecyclerView.Adapter<ExerciseListAdapter.ExerciseListViewHolder>? = null
    private lateinit var exerciseList: ArrayList<Exercise>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        var view = inflater.inflate(R.layout.fragment_exercise_list, container, false)
        addExerciseBtn = view.findViewById<FloatingActionButton>(R.id.add_exercise_btn)
        addExerciseBtn.setOnClickListener { view -> (activity as MainActivity).navToAddExercise() }
        var recycler = view.findViewById<RecyclerView>(R.id.exercise_list_rv)
        recycler.layoutManager = LinearLayoutManager(activity)

        exerciseList = ArrayList<Exercise>()

        loadExercises(view)

        recycler.adapter = ExerciseListAdapter(exerciseList, activity as MainActivity)

        return view
    }

    private fun loadExercises(view: View){
        val dbHandler = DBHandler(this.requireContext(), null, null, 1)
        val retrievedList = dbHandler.getAllExercises()

        if(retrievedList != null && retrievedList.size > 0){
            exerciseList = retrievedList
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