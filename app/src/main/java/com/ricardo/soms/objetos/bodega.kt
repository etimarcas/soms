package com.ricardo.soms.objetos

class bodega {

    var idBodega:String?=null
    var idSabueso:String?=null
    var bodega:String?=null

    constructor()

    constructor(idBodega_:String,bodega_:String){
        this.idBodega = idBodega_
        this.bodega = bodega_
    }
}