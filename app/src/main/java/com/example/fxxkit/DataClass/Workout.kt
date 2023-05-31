package com.example.fxxkit.DataClass

import java.time.LocalDate
import java.time.LocalDateTime
import java.util.*
import kotlin.collections.ArrayList

/**
 * Represents a workout object
 */
class Workout {
    var id: Int = -1
    var workoutName: String? = null
    var description: String? = null
    var isFavourited: Boolean = false
    var exercises: ArrayList<Exercise> = ArrayList()
    var tags: ArrayList<Tag> = ArrayList()
    var createdDate: LocalDate = LocalDate.now()
    var updatedDate: LocalDate = LocalDate.now()

    constructor(id: Int, workoutName: String, exercises: ArrayList<Exercise>){
        this.id = id
        this.workoutName = workoutName
        this.exercises = exercises
    }
    constructor(id: Int, workoutName: String){
        this.id = id
        this.workoutName = workoutName
    }

    constructor(workoutName: String){
        this.workoutName = workoutName
    }

    constructor(id: Int){
        this.id = id
    }

    public fun setUpdatedDate(){
        this.updatedDate = LocalDate.now()
    }

    public fun getTagDisplayString(): String?{
        if(tags.size < 1){
            return null
        } else {
            var tagString = ""
            for(tag in this.tags){
                tagString += tag.name + ", "
            }
            tagString = tagString.dropLast(2)

            return tagString
        }
    }

    public fun getTagInputString(): String?{
        if(tags.size < 1){
            return null
        } else {
            var tagString = ""
            for(tag in this.tags){
                tagString += tag.name + " "
            }
            tagString = tagString.dropLast(1)

            return tagString
        }
    }
}