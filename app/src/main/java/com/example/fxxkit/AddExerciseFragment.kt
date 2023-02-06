package com.example.fxxkit

import android.app.AlertDialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.google.android.material.snackbar.Snackbar
import java.util.*
import kotlin.collections.ArrayList

/**
 * A simple [Fragment] subclass.
 * Use the [AddExerciseFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class AddExerciseFragment : Fragment() {

    private lateinit var setNoArray: Array<String>
    //private lateinit var selectedSetNosArray: Array<Boolean>

    private fun createSetArray(){
        setNoArray = arrayOf("3", "5", "10", "12", "15", "20", "30", "50")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view =  inflater.inflate(R.layout.fragment_add_exercise, container, false)

        val exNameInput = view.findViewById<EditText>(R.id.exercise_name)
        val exSetNosInput = view.findViewById<TextView>(R.id.exercise_set_nos)
        val exRepNosInput = view.findViewById<EditText>(R.id.exercise_rep_nos)


        val showBtn = view.findViewById<Button>(R.id.add_exercise_btn)
        showBtn.setOnClickListener{view ->
            val text = exNameInput.text.toString() + ", " + exSetNosInput.text.toString() + ", " + exRepNosInput.text.toString()
            Toast.makeText(activity, text, Toast.LENGTH_SHORT).show()
        }

        createSetArray()
        var selectedArray =  ArrayList<Int>()
        var selectedSetNosArray = Array(setNoArray.size){false}

        exSetNosInput.setOnClickListener{view ->
            val builder = AlertDialog.Builder(activity)
            builder.setTitle("Set lengths for Exercise")
            builder.setCancelable(false)
            builder.setMultiChoiceItems(setNoArray, null){dialog, which, isChecked ->
                if(isChecked){
                    selectedArray.add(which)
                } else if(selectedArray.contains(which)){
                    selectedArray.remove(Integer.valueOf(which))
                }
            }

            builder.setPositiveButton("Done"){ dialogInterface, i ->
                val selectedStrings = ArrayList<String>()

                for(j in selectedArray.indices){
                    selectedStrings.add(setNoArray[selectedArray[j]])
                }

                Snackbar.make(view, "Items selected are: " + Arrays.toString(selectedStrings.toTypedArray()), Snackbar.LENGTH_SHORT)
                    .setAction("Action", null).show()
            }

            builder.show()
        }

        return view
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.

         * @return A new instance of fragment AddExerciseFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance() =
            AddExerciseFragment().apply {
                arguments = Bundle().apply {
                }
            }
    }
}