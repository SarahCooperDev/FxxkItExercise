package com.example.fxxkit

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.example.fxxkit.DataClass.Exercise

class DBHandler(context: Context, name: String?, factory: SQLiteDatabase.CursorFactory?, version: Int):
    SQLiteOpenHelper(context, DATABASE_NAME, factory, DATABASE_VERSION){


    override fun onCreate(db: SQLiteDatabase) {
        val CREATE_EXERCISE_TABLE = ("CREATE TABLE " + TABLE_EXERCISES +
                "(" + COLUMN_ID + " INTEGER PRIMARY KEY," + COLUMN_EXERCISENAME + " TEXT)")

        db.execSQL(CREATE_EXERCISE_TABLE)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_EXERCISES)
        onCreate(db)
    }

    companion object{
        private val DATABASE_VERSION = 1
        private val DATABASE_NAME = "exerciseDB.db"
        val TABLE_EXERCISES = "exercise"

        val COLUMN_ID = "_id"
        val COLUMN_EXERCISENAME = "exercisename"
    }

    fun addInitialExercises(){
        val dbold = this.writableDatabase
        onUpgrade(dbold, 1, 1)

        val ex1 = Exercise("Push ups")
        val ex2 = Exercise("Sit ups")
        val ex3 = Exercise("Side crunches")

        val values = ContentValues()
        val db = this.writableDatabase


        values.put(COLUMN_EXERCISENAME, ex1.exerciseName)
        db.insert(TABLE_EXERCISES, null, values)
        values.put(COLUMN_EXERCISENAME, ex2.exerciseName)
        db.insert(TABLE_EXERCISES, null, values)
        values.put(COLUMN_EXERCISENAME, ex3.exerciseName)
        db.insert(TABLE_EXERCISES, null, values)

        db.close()
    }

    fun addExercise(exercise: Exercise){
        val values = ContentValues()
        values.put(COLUMN_EXERCISENAME, exercise.exerciseName)

        val db = this.writableDatabase

        db.insert(TABLE_EXERCISES, null, values)
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

    @SuppressLint("Range")
    fun getAllExercises(): ArrayList<Exercise>?{
        //addInitialExercises()
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