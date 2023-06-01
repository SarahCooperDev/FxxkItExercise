package com.example.fxxkit.DataClass

/**
 * Represents an instance of an exercise being saved in a workout
 */
class WorkoutExercise{
    var id: Int = -1
    var workoutId: Int = -1
    var exerciseId: Int = -1
    var exercise: Exercise? = null
    var setSize: String? = null
    var repSize: String? = null
    var isSelected: Boolean = false
    var orderNo: Int = -1
    var totalTime: Int = 0

    constructor(id: Int, workoutId: Int, exerciseId: Int, setSize: String, repSize: String, orderNo: Int){
        this.id = id
        this.workoutId = workoutId
        this.exerciseId = exerciseId
        this.setSize = setSize
        this.repSize = repSize
        this.orderNo = orderNo
    }
    constructor(id: Int, workoutId: Int, exerciseId: Int, setSize: String, repSize: String){
        this.id = id
        this.workoutId = workoutId
        this.exerciseId = exerciseId
        this.setSize = setSize
        this.repSize = repSize
    }
    constructor(id: Int, workoutId: Int, exerciseId: Int){
        this.id = id
        this.workoutId = workoutId
        this.exerciseId = exerciseId
    }

    constructor(exerciseId: Int){
        this.id = -1
        this.workoutId = -1
        this.exerciseId = exerciseId
    }

    constructor(exercise: Exercise){
        this.exercise = exercise
        this.exerciseId = exercise.id
    }


    constructor(){}

    public fun getTotalTimeInSecs(): Int?{
        try {
            var seconds = exercise?.repTime
            var setInt = setSize?.toInt()
            var repInt = repSize?.toInt()
            var total = setInt!! * repInt!! * seconds!!
            if(exercise!!.needsBothSides){ total = total * 2 }
            return total
        } catch(e: Exception){
            return null
        }

        return null
    }

    public fun getTotalTimeString(): String{
        var totalTime = getTotalTimeInSecs()
        if(totalTime != null){
            var minutes = totalTime/60
            var minutesString = minutes.toString()
            if(minutes < 1){
                minutesString = "0"
            }
            var seconds = totalTime%60
            var secondsString = seconds.toString()
            if(seconds < 10 && seconds > 0){
                secondsString = "0" + seconds.toString()
            } else if(seconds < 10){
                secondsString = "00"
            }
            return minutesString + ":" + secondsString + "m"
        } else {
            return "N/A"
        }
    }
}
