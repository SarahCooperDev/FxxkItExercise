package com.example.fxxkit.Fragment

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.fxxkit.DBHandler
import com.example.fxxkit.DataClass.Exercise
import com.example.fxxkit.ExerciseListAdapter
import com.example.fxxkit.MainActivity
import com.example.fxxkit.R
import com.example.fxxkit.ViewModel.WorkoutViewModel
import com.google.android.material.floatingactionbutton.FloatingActionButton

/**
 * A simple [Fragment] subclass.
 * Use the [ExerciseListFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class ExerciseListFragment : Fragment() {
    private var filterSetting = 0
    private var sortSetting = 2

    private var allExercises: ArrayList<Exercise> = ArrayList<Exercise>()
    private var exerciseList: ArrayList<Exercise> = ArrayList<Exercise>()

    private lateinit var addExerciseBtn : FloatingActionButton
    private lateinit var searchBtn: ImageButton
    private lateinit var filterSearchBtn: ImageButton
    private lateinit var searchEdit: EditText
    private lateinit var searchClearBtn: ImageButton
    private lateinit var sortBtn: ImageButton
    private lateinit var exerciseRecycler: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        var view = inflater.inflate(R.layout.fragment_exercise_list, container, false)
        (activity as MainActivity).getSupportActionBar()?.customView?.findViewById<TextView>(R.id.appbar_title_id)?.setText("Exercises")

        addExerciseBtn = view.findViewById<FloatingActionButton>(R.id.add_exercise_btn)
        exerciseRecycler = view.findViewById<RecyclerView>(R.id.exercise_list_rv)
        sortBtn = view.findViewById<ImageButton>(R.id.sort_btn)
        searchBtn = view.findViewById<ImageButton>(R.id.search_btn)
        filterSearchBtn = view.findViewById<ImageButton>(R.id.filter_search_btn)
        searchClearBtn = view.findViewById<ImageButton>(R.id.search_clear_btn)
        searchEdit = view.findViewById<EditText>(R.id.search_edit)

        addExerciseBtn.setOnClickListener { view -> (activity as MainActivity).navToAddExercise() }
        searchBtn.setOnClickListener { search() }
        searchClearBtn.setOnClickListener { clearSearch() }
        setUpSortBtn()
        setUpFilterBtn()

        loadExercises(view)

        exerciseRecycler.layoutManager = LinearLayoutManager(activity)
        exerciseRecycler.adapter = ExerciseListAdapter(exerciseList, activity as MainActivity)

        return view
    }

    private fun search(){
        println("Search button clicked")
        when(filterSetting){
            0 -> filterByName()
        }
        searchEdit.clearFocus()
        val inputManager = context?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputManager.hideSoftInputFromWindow(view?.windowToken, 0)
    }

    private fun clearSearch(){
        searchEdit.text.clear()
        searchEdit.clearFocus()
        val inputManager = context?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputManager.hideSoftInputFromWindow(view?.windowToken, 0)

        exerciseList.clear()
        exerciseList.addAll(allExercises)
        exerciseRecycler.adapter?.notifyDataSetChanged()
    }

    private fun setUpFilterBtn(){
        filterSearchBtn.setOnClickListener {
            val filterPopup = PopupMenu(activity, filterSearchBtn)
            filterPopup.menuInflater.inflate(R.menu.workout_list_filter_menu, filterPopup.menu)
            filterPopup.setOnMenuItemClickListener { menuItem ->
                when(menuItem.itemId){
                    R.id.filter_name_item -> {
                        filterSetting = 0
                    }
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
            when(sortSetting){
                0 -> sortPopup.menu.findItem(R.id.sort_alpha_item)
                1 -> sortPopup.menu.findItem(R.id.sort_reverse_alpha_item)
                2 -> {
                    val menuView = sortPopup.menu.findItem(R.id.sort_chrono_item)
                }
                3 -> sortPopup.menu.findItem(R.id.sort_reverse_chrono_item)
            }

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

    private fun filterByName(){
        if(searchEdit.text.toString().length > 0){
            var filteredList: ArrayList<Exercise> = allExercises.filter{ it.name!!.lowercase().contains(searchEdit.text.toString().lowercase())} as ArrayList<Exercise>
            exerciseList.clear()
            exerciseList.addAll(filteredList)
            exerciseRecycler.adapter?.notifyDataSetChanged()
        } else {
            Toast.makeText(activity, "There must be a search term to search by name", Toast.LENGTH_LONG).show()
        }
    }

    private fun sortByChrono() {
        exerciseList.sortBy{ it.id }
        exerciseRecycler.adapter?.notifyDataSetChanged()
    }

    private fun sortByReverseChrono(){
        exerciseList.sortByDescending { it.id }
        exerciseRecycler.adapter?.notifyDataSetChanged()
    }

    private fun sortByAlpha(){
        exerciseList.sortBy { it.name }
        exerciseRecycler.adapter?.notifyDataSetChanged()
    }

    private fun sortByReverseAlpha(){
        exerciseList.sortByDescending { it.name }
        exerciseRecycler.adapter?.notifyDataSetChanged()
    }

    private fun loadExercises(view: View){
        val dbHandler = DBHandler(this.requireContext(), null, null, 1)
        val retrievedList = dbHandler.getAllExercises()

        if(retrievedList != null && retrievedList.size > 0){
            allExercises = retrievedList
            exerciseList = allExercises.clone() as ArrayList<Exercise>
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }

    companion object {

        @JvmStatic
        fun newInstance() =
            ExerciseListFragment().apply { }
    }
}