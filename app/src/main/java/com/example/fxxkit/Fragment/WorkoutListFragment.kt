package com.example.fxxkit.Fragment

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ImageButton
import android.widget.PopupMenu
import android.widget.Toast
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
    private var filterSetting = 0
    private lateinit var createWorkoutBtn : FloatingActionButton
    private lateinit var sortBtn: ImageButton
    private lateinit var searchBtn: ImageButton
    private lateinit var filterSearchBtn: ImageButton
    private lateinit var searchClearBtn: ImageButton
    private lateinit var searchEdit: EditText
    private lateinit var workoutListRecycler: RecyclerView
    private var allWorkouts: ArrayList<WorkoutViewModel> = ArrayList<WorkoutViewModel>()
    private var workoutList: ArrayList<WorkoutViewModel> = ArrayList<WorkoutViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        var view = inflater.inflate(R.layout.fragment_workout_list, container, false)

        createWorkoutBtn = view.findViewById<FloatingActionButton>(R.id.create_workout_btn)
        sortBtn = view.findViewById<ImageButton>(R.id.sort_btn)
        workoutListRecycler = view.findViewById<RecyclerView>(R.id.workout_list_rv)
        searchBtn = view.findViewById<ImageButton>(R.id.search_btn)
        filterSearchBtn = view.findViewById<ImageButton>(R.id.filter_search_btn)
        searchClearBtn = view.findViewById<ImageButton>(R.id.search_clear_btn)
        searchEdit = view.findViewById<EditText>(R.id.search_edit)

        loadWorkouts(view)

        workoutListRecycler.layoutManager = LinearLayoutManager(activity)
        workoutListRecycler.adapter = WorkoutListAdapter((activity as MainActivity), workoutList)

        setUpSortBtn()
        setUpFilterBtn()

        createWorkoutBtn.setOnClickListener { view -> (activity as MainActivity).navToCreateWorkout() }

        searchBtn.setOnClickListener {
            println("Search button clicked")
            when(filterSetting){
                0 -> filterByName()
                1 -> filterByFavourite()
            }
            searchEdit.clearFocus()
            val inputManager = context?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            inputManager.hideSoftInputFromWindow(view.windowToken, 0)
        }

        searchClearBtn.setOnClickListener {
            searchEdit.text.clear()
            searchEdit.clearFocus()
            val inputManager = context?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            inputManager.hideSoftInputFromWindow(view.windowToken, 0)

            workoutList.clear()
            workoutList.addAll(allWorkouts)
            workoutListRecycler.adapter?.notifyDataSetChanged()
        }
        return view
    }

    private fun filterByName(){
        if(searchEdit.text.toString().length > 0){
            var filteredList: ArrayList<WorkoutViewModel> = allWorkouts.filter{ it.name!!.lowercase().contains(searchEdit.text.toString().lowercase())} as ArrayList<WorkoutViewModel>
            workoutList.clear()
            workoutList.addAll(filteredList)
            workoutListRecycler.adapter?.notifyDataSetChanged()
        } else {
            Toast.makeText(activity, "There must be a search term to search by name", Toast.LENGTH_LONG).show()
        }
    }

    private fun filterByFavourite(){

    }

    private fun setUpFilterBtn(){
        filterSearchBtn.setOnClickListener {
            val filterPopup = PopupMenu(activity, filterSearchBtn)
            filterPopup.menuInflater.inflate(R.menu.workout_list_filter_menu, filterPopup.menu)
            filterPopup.setOnMenuItemClickListener { menuItem ->
                when(menuItem.itemId){
                    R.id.filter_name_item -> { println("Filter by name")}
                    R.id.filter_fav_item -> { println("Filter by favourite")}
                }
                true
            }
            filterPopup.show()
        }
    }

    private fun setUpSortBtn(){
        sortBtn.setOnClickListener {
            val sortPopup = PopupMenu(activity, sortBtn)
            sortPopup.menuInflater.inflate(R.menu.workout_list_sort_menu, sortPopup.menu)
            sortPopup.setOnMenuItemClickListener { menuItem ->
                when(menuItem.itemId){
                    R.id.sort_alpha_item -> { sortByAlpha() }
                    R.id.sort_reverse_alpha_item -> { sortByReverseAlpha() }
                    R.id.sort_chrono_item -> { sortByChrono() }
                    R.id.sort_reverse_chrono_item -> { sortByReverseChrono() }
                }
                true
            }
            sortPopup.show()
        }
    }

    private fun sortByChrono() {
        workoutList.sortBy{ it.id }
        workoutListRecycler.adapter?.notifyDataSetChanged()
    }

    private fun sortByReverseChrono(){
        workoutList.sortByDescending { it.id }
        workoutListRecycler.adapter?.notifyDataSetChanged()
    }

    private fun sortByAlpha(){
        workoutList.sortBy { it.name }
        workoutListRecycler.adapter?.notifyDataSetChanged()
    }

    private fun sortByReverseAlpha(){
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
                println("Workout exercise list size is ${workoutVM.workExList.size.toString()}")
                allWorkouts.add(workoutVM)
            }
            workoutList = allWorkouts.clone() as ArrayList<WorkoutViewModel>
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }

    companion object {
        @JvmStatic
        fun newInstance() = WorkoutListFragment().apply {}
    }
}