package com.example.fxxkit

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.example.fxxkit.DataClass.Exercise
import com.example.fxxkit.DataClass.Workout

class DBHandler(context: Context, name: String?, factory: SQLiteDatabase.CursorFactory?, version: Int):
    SQLiteOpenHelper(context, DATABASE_NAME, factory, DATABASE_VERSION){

    var dbNeedsRefresh = true;

    companion object{
        private val DATABASE_VERSION = 1
        private val DATABASE_NAME = "exerciseDB.db"
        val TABLE_EXERCISES = "exercise"
        val TABLE_WORKOUTS = "workout"
        val TABLE_WORKOUT_EXERCISE = "workout_exercise"

        val COLUMN_ID = "_id"
        val COLUMN_EXERCISENAME = "exercise_name"
        val COLUMN_WORKOUTNAME = "workout_name"
        val COLUMN_WORKOUT = "workout_id"
        val COLUMN_EXERCISE = "exercise_id"
    }

    override fun onCreate(db: SQLiteDatabase) {
        val CREATE_EXERCISE_TABLE = ("CREATE TABLE " + TABLE_EXERCISES +
                "(" + COLUMN_ID + " INTEGER PRIMARY KEY," + COLUMN_EXERCISENAME + " TEXT)")
        val CREATE_WORKOUT_TABLE = ("CREATE TABLE " + TABLE_WORKOUTS +
                "(" + COLUMN_ID + " INTEGER PRIMARY KEY," + COLUMN_WORKOUTNAME + " TEXT)")
        val CREATE_WORKOUT_EXERCISE_TABLE = ("CREATE TABLE " + TABLE_WORKOUT_EXERCISE +
                "(" + COLUMN_ID + "INTEGER PRIMARY KEY," + COLUMN_WORKOUT + " INTEGER," + COLUMN_EXERCISE + " INTEGER)")

        db.execSQL(CREATE_EXERCISE_TABLE)
        db.execSQL(CREATE_WORKOUT_TABLE)
        db.execSQL(CREATE_WORKOUT_EXERCISE_TABLE)

    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_EXERCISES)
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_WORKOUTS)
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_WORKOUT_EXERCISE)

        onCreate(db)
    }



    fun addInitialExercises(){
        val ex1 = Exercise("Push ups")
        val ex2 = Exercise("Sit ups")
        val ex3 = Exercise("Side crunches")

        addExercise(ex1)
        addExercise(ex2)
        addExercise(ex3)
    }

    fun addInitialWorkoutExercises(){
        val exerciseList: ArrayList<Exercise>? = getAllExercises()
        val workoutList: ArrayList<Workout>? = getAllWorkouts()

        if(workoutList != null && workoutList.size > 0){
            for(i in 0 ..1) {
                if(exerciseList != null && exerciseList.size > 0){
                    if(exerciseList.size >= 3){
                        for(j in 0..2){
                            addExerciseToWorkout(workoutList[i], exerciseList[j])
                        }
                    } else {
                        for(j in exerciseList.indices){
                            addExerciseToWorkout(workoutList[i], exerciseList[j])
                        }
                    }
                }
            }
        }
    }

    fun addInitialWorkouts(){
        val work1 = Workout("10 minute workout")
        val work2 = Workout("Basic half")

        addWorkout(work1)
        addWorkout(work2)
    }

    fun initialiseDatabase(){
        val dbold = this.writableDatabase
        onUpgrade(dbold, 1, 1)

        addInitialExercises()
        addInitialWorkouts()
        addInitialWorkoutExercises()
    }



    fun addExercise(exercise: Exercise){
        val values = ContentValues()
        val db = this.writableDatabase

        values.put(COLUMN_EXERCISENAME, exercise.exerciseName)

        db.insert(TABLE_EXERCISES, null, values)
        db.close()
    }

    fun addExerciseToWorkout(workout: Workout, exercise: Exercise){
        val values = ContentValues()
        val db = this.writableDatabase

        values.put(COLUMN_WORKOUT, workout.id)
        values.put(COLUMN_EXERCISE, exercise.id)

        db.insert(TABLE_WORKOUT_EXERCISE, null, values)
        db.close()
    }

    fun addWorkout(workout: Workout){
        val values = ContentValues()
        val db = this.writableDatabase

        values.put(COLUMN_WORKOUTNAME, workout.workoutName)

        db.insert(TABLE_WORKOUTS, null, values)
        db.close()
    }

    fun findExercise(exerciseName: String): Exercise?{
        val query = "SELECT * FROM $TABLE_EXERCISES WHERE $COLUMN_EXERCISENAME = \"$exerciseName\""

        val db = this.writableDatabase
        val cursor = db.rawQuery(query, null)
        var exercise: Exercise? = null

        if(cursor.moveToFirst()){
            cursor.moveToFirst()

            val id = Integer.parseInt(cursor.getString(0))
            val exerciseName = cursor.getString(1)

            exercise = Exercise(id, exerciseName)
            cursor.close()
        }

        db.close()
        return exercise
    }

    fun findExerciseById(exId: Int): Exercise?{
        val query = "SELECT * FROM $TABLE_EXERCISES WHERE $COLUMN_ID = \"$exId\""

        val db = this.readableDatabase
        val cursor = db.rawQuery(query, null)
        var exercise: Exercise? = null

        if(cursor.moveToFirst()){
            cursor.moveToFirst()

            val id = Integer.parseInt(cursor.getString(0))
            val exerciseName = cursor.getString(1)

            exercise = Exercise(id, exerciseName)
            cursor.close()
        }

        db.close()
        return exercise
    }

    @SuppressLint("Range")
    fun findAllWorkoutExercises(workout: Workout): Workout{
        if(workout.id != 0) {
            val query = "SELECT * FROM $TABLE_WORKOUT_EXERCISE WHERE $COLUMN_WORKOUT = \"$workout.id\""

            val db = this.readableDatabase
            val cursor = db.rawQuery(query, null)

            var exerciseIds: ArrayList<Int>? = ArrayList<Int>()
            var id: Int = -1
            var exerciseId: Int = -1

            if(cursor.moveToFirst()){
                do{
                    id = cursor.getInt(cursor.getColumnIndex(COLUMN_ID))
                    exerciseId = cursor.getInt(cursor.getColumnIndex(COLUMN_EXERCISE))

                    if(exerciseId >= 0){
                        val exercise = findExerciseById(exerciseId)
                        if (exercise != null) {
                            workout.exercises.add(exercise)
                        }
                    }
                } while(cursor.moveToNext())

                cursor.close()
            }
        }

        return workout
    }

    @SuppressLint("Range")
    fun getAllExercises(): ArrayList<Exercise>?{
        if(dbNeedsRefresh){
            dbNeedsRefresh = false;
            initialiseDatabase()
        }

        val exerciseList: ArrayList<Exercise> = ArrayList<Exercise>()

        val query = "SELECT * FROM $TABLE_EXERCISES"

        val db = this.readableDatabase
        val cursor = db.rawQuery(query, null)

        var exId: Int
        var exName: String

        if(cursor.moveToFirst()){
            do{
                exId = cursor.getInt(cursor.getColumnIndex(COLUMN_ID))
                exName = cursor.getString(cursor.getColumnIndex(COLUMN_EXERCISENAME))

                val exercise = Exercise(exId, exName)
                exerciseList.add(exercise)
            } while(cursor.moveToNext())
        }
        return exerciseList
    }

    @SuppressLint("Range")
    fun getAllWorkouts(): ArrayList<Workout>?{
        if(dbNeedsRefresh){
            dbNeedsRefresh = false
            initialiseDatabase()
        }

        val workoutList: ArrayList<Workout> = ArrayList<Workout>()

        val query = "SELECT * FROM $TABLE_WORKOUTS"
        val db = this.readableDatabase
        val cursor = db.rawQuery(query, null)

        var workId: Int
        var workName: String

        if(cursor.moveToFirst()){
            do{
                workId = cursor.getInt(cursor.getColumnIndex(COLUMN_ID))
                workName = cursor.getString(cursor.getColumnIndex(COLUMN_WORKOUTNAME))

                val workout = Workout(workId, workName)
                workoutList.add(workout)
            } while(cursor.moveToNext())
        }

        for(workout in workoutList){
            findAllWorkoutExercises(workout)
        }

        return workoutList
    }

    fun deleteExercise(exerciseName: String): Boolean{
        var result = false

        val query = "SELECT * FROM $TABLE_EXERCISES WHERE $COLUMN_EXERCISENAME = \"$exerciseName\""

        val db = this.writableDatabase

        val cursor = db.rawQuery(query, null)

        if(cursor.moveToFirst()){
            val id = Integer.parseInt(cursor.getString(0))
            db.delete(TABLE_EXERCISES, COLUMN_ID + " = ?", arrayOf(id.toString()))
            cursor.close()
            result = true
        }

        db.close()
        return result
    }
}