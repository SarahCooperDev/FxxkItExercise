package com.example.fxxkit.DataClass

class Tag {
    var id: Int = -1
    var name: String? = null

    constructor(){
    }

    constructor(name: String){
        this.name = name
    }

    constructor(id: Int, name: String){
        this.id = id
        this.name = name
    }
}