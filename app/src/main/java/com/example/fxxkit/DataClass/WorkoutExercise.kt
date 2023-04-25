package com.example.fxxkit.DataClass

class WorkoutExercise(){
    var id: Int = -1
    var workoutId: Int = -1
    var exerciseId: Int = -1

    constructor(id: Int, workoutId: Int, exerciseId: Int) : this() {
        this.id = id
        this.workoutId = workoutId
        this.exerciseId = exerciseId
    }
}
