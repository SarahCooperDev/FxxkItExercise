package com.example.fxxkit.ViewModel

import com.example.fxxkit.DataClass.Exercise

class ExerciseViewModel {
    var id: Int
    var name: String
    var isStrength: Boolean?
    var isCondition: Boolean?
    var possibleSetSize: String?
    var possibleRepSize: String?
    var targettedMuscles: String?

    constructor(id: Int, name: String, isStrength: Boolean, isCondition: Boolean,
                possibleSetSize: String, possibleRepSize: String, targettedMuscles: String){
        this.id = id
        this.name = name
        this.isStrength = isStrength
        this.isCondition = isCondition
        this.possibleSetSize = possibleSetSize
        this.possibleRepSize = possibleRepSize
        this.targettedMuscles = targettedMuscles
    }
    constructor(id: Int, name: String){
        this.id = id
        this.name = name
        this.isStrength = false
        this.isCondition = false
        this.possibleSetSize = null
        this.possibleRepSize = null
        this.targettedMuscles = null
    }

    public fun convertExerciseToViewModel(exercise: Exercise){
        if(exercise.isStrengthening) {this.isStrength = true}
        if(exercise.isConditioning) { this.isCondition = true }
        if(exercise.possibleSetSize.size > 0){ this.possibleSetSize = exercise.getSetAsString()}
        if(exercise.possibleRepSize.size > 0){ this.possibleRepSize = exercise.getRepsAsString()}
        if(exercise.targettedMuscles.size > 0) { this.targettedMuscles = exercise.getMusclesAsString()}
    }

}