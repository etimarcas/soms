package com.ricardo.soms.objetos

class usuario {
    var idUser:String?=null
    var nombre:String?=null
    var pass:String?=null

    constructor(){}

    constructor(idUser_:String,nombre_:String,pass_:String){
        this.idUser = idUser_
        this.nombre = nombre_
        this.pass = pass_


    }
}