package com.ricardo.soms.objetos

class inventarios {

    var idInventario:String?=null
    var producto:producto?=null
    var usuario:usuario?=null
    var bodega:bodega?=null

    constructor()

    constructor(idInventario_:String,producto_:producto,usuario_:usuario,bodega_:bodega){
        this.idInventario = idInventario_
        this.producto = producto_
        this.usuario = usuario_
        this.bodega = bodega_
    }
}