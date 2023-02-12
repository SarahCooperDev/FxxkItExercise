package com.example.fxxkit.DataClass

class Exercise {
    var id: Int = 0
    var exerciseName: String? = null

    constructor(id:Int, exerciseName: String){
        this.id = id
        this.exerciseName = exerciseName
    }

    constructor(exerciseName: String){
        this.exerciseName = exerciseName
    }
}