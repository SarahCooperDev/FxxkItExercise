package com.example.fxxkit.DataClass

class Workout {
    var id: Int = 0
    var workoutName: String? = null

    constructor(id: Int, workoutName: String){
        this.id = id
        this.workoutName = workoutName
    }

    constructor(workoutName: String){
        this.workoutName = workoutName
    }
}