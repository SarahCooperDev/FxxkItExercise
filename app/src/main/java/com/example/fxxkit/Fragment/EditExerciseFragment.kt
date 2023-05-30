package com.example.fxxkit.Fragment

import android.graphics.Color
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.example.fxxkit.DBHandler
import com.example.fxxkit.DataClass.Exercise
import com.example.fxxkit.DataClass.MultiselectLists
import com.example.fxxkit.DataClass.Tag
import com.example.fxxkit.MainActivity
import com.example.fxxkit.R
import kotlin.collections.ArrayList

/**
 * Takes an exercise id, allows user to edit the exercise
 */
class EditExerciseFragment : Fragment() {
    private lateinit var currentExercise: Exercise
    private var selectedSets = ArrayList<String>()
    private var selectedReps = ArrayList<String>()
    private var selectedAreas = ArrayList<String>()
    private var allTags = ArrayList<Tag>()

    private lateinit var idTxt: TextView
    private lateinit var nameEditTxt: EditText
    private lateinit var descriptionInput: EditText
    private lateinit var isStrengthBtn: ToggleButton
    private lateinit var isConditioningBtn: ToggleButton
    private lateinit var targettedAreasMultiselect: TextView
    private lateinit var setSizeMultiselect: TextView
    private lateinit var repSizeMultiselect: TextView
    private lateinit var repTimeInput: EditText
    private lateinit var tagInput: EditText
    private lateinit var cancelBtn: ImageButton
    private lateinit var updateBtn: ImageButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        var exerciseId = arguments!!.getInt("exerciseId")
        loadExercise(exerciseId)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_edit_exercise, container, false)
        (activity as MainActivity).getSupportActionBar()?.customView?.findViewById<TextView>(R.id.appbar_title_id)?.setText(getString(R.string.edit_exercise_title))

        idTxt = view.findViewById(R.id.exercise_id_txt)
        nameEditTxt = view.findViewById(R.id.exercise_name_edtxt)
        descriptionInput = view.findViewById(R.id.description_txt)
        isStrengthBtn = view.findViewById(R.id.strengthening_toggle_btn)
        isConditioningBtn = view.findViewById(R.id.conditioning_toggle_btn)
        setSizeMultiselect = view.findViewById(R.id.set_size_multiselect)
        repSizeMultiselect = view.findViewById(R.id.rep_size_multiselect)
        repTimeInput = view.findViewById(R.id.rep_time_txt)
        targettedAreasMultiselect = view.findViewById(R.id.area_select)
        tagInput = view.findViewById(R.id.tag_input)
        cancelBtn = view.findViewById(R.id.cancel_btn)
        updateBtn = view.findViewById(R.id.update_btn)

        idTxt.text = currentExercise.id.toString()
        nameEditTxt.setText(currentExercise.name)
        descriptionInput.setText(currentExercise.description)
        repTimeInput.setText(currentExercise.repTime.toString())

        if(currentExercise.isStrengthening){ isStrengthBtn.isChecked = true }
        if(currentExercise.isConditioning) { isConditioningBtn.isChecked = true }
        if(currentExercise.tags.size > 0){ tagInput.setText(currentExercise.getTagInputString()) }

        if(currentExercise.possibleSetSize.size > 0){
            setSizeMultiselect.text = MultiselectLists.getStringFromArray(currentExercise.possibleSetSize)
            selectedSets = currentExercise.possibleSetSize.clone() as ArrayList<String>
        }

        if(currentExercise.possibleRepSize.size > 0){
            repSizeMultiselect.text = MultiselectLists.getStringFromArray(currentExercise.possibleRepSize)
            selectedReps = currentExercise.possibleRepSize.clone() as ArrayList<String>
        }

        if(currentExercise.targettedAreas.size > 0){
            targettedAreasMultiselect.text = MultiselectLists.getStringFromArray(currentExercise.targettedAreas)
            selectedAreas = currentExercise.targettedAreas.clone() as ArrayList<String>
        }

        setSizeMultiselect.setOnClickListener { view -> MultiselectLists.showDialog(activity as MainActivity, layoutInflater, MultiselectLists.setSizesArray, selectedSets, setSizeMultiselect) }
        repSizeMultiselect.setOnClickListener { view -> MultiselectLists.showDialog(activity as MainActivity, layoutInflater, MultiselectLists.repSizesArray, selectedReps, repSizeMultiselect) }
        targettedAreasMultiselect.setOnClickListener { view -> MultiselectLists.showDialog(activity as MainActivity, layoutInflater, MultiselectLists.targettedAreaArray, selectedAreas, targettedAreasMultiselect) }

        cancelBtn.setOnClickListener{ view -> (activity as MainActivity).navToWorkoutList() }

        updateBtn.setOnClickListener{ view ->
            if(nameEditTxt.text.toString().length < 1){
                Toast.makeText(activity, getString(R.string.error_blank_name_txt), Toast.LENGTH_LONG).show()
                nameEditTxt.setBackgroundColor(Color.parseColor(getString(R.string.colour_error)))
            } else {
                updateExercise()
                Toast.makeText(activity, "Updated exercise ${currentExercise.name}", Toast.LENGTH_SHORT).show()
                (activity as MainActivity).navToExerciseList()
            }
        }

        return view
    }

    /**
     * Loads the exercise from the database upon navigation
     */
    private fun loadExercise(exerciseId: Int){
        val dbHandler = DBHandler(this.requireContext(), null, null, 1)
        var exercise = dbHandler.findExerciseById(exerciseId)
        allTags = dbHandler.getAllTags()

        if(exercise !=  null){
            currentExercise = exercise

            var tagList = dbHandler.getTagsForExercise(currentExercise)
            if(tagList.size > 0){
                currentExercise.tags = tagList
            }
        }
    }

    /**
     * Updates the database with new exercise information
     */
    private fun updateExercise(){
        currentExercise.name = nameEditTxt.text.toString()
        currentExercise.description = descriptionInput.text.toString()
        if(isStrengthBtn.isChecked()){ currentExercise.isStrengthening = true }
        if(isConditioningBtn.isChecked()){ currentExercise.isConditioning = true }
        currentExercise.possibleSetSize = selectedSets
        currentExercise.possibleRepSize = selectedReps
        currentExercise.targettedAreas = selectedAreas

        try{
            var repTime = repTimeInput.text.toString().toInt()
            if(repTime != null){ currentExercise.repTime = repTime}
        } catch(e: Exception){
            Toast.makeText(requireContext(), getString(R.string.error_reptime_not_number), Toast.LENGTH_LONG)
        }

        val dbHandler = DBHandler(this.requireContext(), null, null, 1)
        dbHandler.updateExercise(currentExercise)

        updateTags()
    }

    /**
     * Adds new tags, and deletes the ExerciseTag for removed tags
     */
    private fun updateTags(){
        val dbHandler = DBHandler(this.requireContext(), null, null, 1)
        var splitTags = tagInput.text.split(" ")

        // Adds new tags
        for(tag in splitTags){
            var foundTag = currentExercise.tags.firstOrNull{it.name!!.lowercase() == tag.toString().lowercase()}

            if(foundTag == null){
                foundTag = allTags.firstOrNull{ it.name!!.lowercase() == tag.toString().lowercase() }

                if(foundTag == null){
                    foundTag = Tag(tag.toString().lowercase())
                    foundTag.id = dbHandler.addTag(foundTag)!!
                }

                if(foundTag.id != null){
                    var result = dbHandler.addTagToExerciseByIds(currentExercise.id, foundTag.id)
                }
            }
        }

        // Deletes ExerciseTag record for removed tags
        for(tag in currentExercise.tags){
            var foundTag = splitTags.firstOrNull{it.lowercase() == tag.name!!.lowercase()}

            if(foundTag == null){
                dbHandler.deleteTagFromExercise(currentExercise, tag)
            }
        }
    }

    companion object {
        @JvmStatic
        fun newInstance() = EditExerciseFragment().apply { }
    }
}