package com.ricardo.soms.objetos

class producto {

    var idProducto:String?=null
    var idSabueso:String?=null
    var descripcion:String?=null
    var codigoBarras13:String?=null
    var codigoBarras14:String?=null
    var codigoEPC:String?=null
    var unidadEmpaque:Int?=null
    var cantidad:Int?=null

    constructor()
    constructor(idProducto_:String,idSabueso_:String,descripcion_:String,codigoBarras13_:String,codigoBarras14_:String,codigoEPC_:String,unidadEmpaque_:Int,cantidad_:Int){

        this.idProducto = idProducto_
        this.idSabueso = idSabueso_
        this.descripcion = descripcion_
        this.codigoBarras13 = codigoBarras13_
        this.codigoBarras14 = codigoBarras14_
        this.codigoEPC = codigoEPC_
        this.unidadEmpaque = unidadEmpaque_
        this.cantidad = cantidad_
    }
}