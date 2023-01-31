package com.example.fxxkit

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
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


        createExerciseList()

        recycler.adapter = ExerciseListAdapter(exerciseList)

        return view
    }

    private fun createExerciseList(){
        exerciseList = ArrayList<ExerciseViewModel>()
        exerciseList.add(ExerciseViewModel("Push ups", 5))
        exerciseList.add(ExerciseViewModel("Sit ups", 10))
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //var recyclerView = requireView().findViewById<RecyclerView>(R.id.exercise_list_rv)
        //recyclerView.adapter = exerciseListAdapter
        //recyclerView.layoutManager = LinearLayoutManager(activity)
            //.apply{
        //    layoutManager = LinearLayoutManager(activity)
         //   adapter = ExerciseListAdapter()
        //}
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *

         * @return A new instance of fragment ExerciseListFragment.
         */
        @JvmStatic
        fun newInstance() =
            ExerciseListFragment().apply {
                arguments = Bundle().apply {                }
            }
    }
}