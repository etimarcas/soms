package com.ricardo.soms

import android.content.Context
import android.os.AsyncTask
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.ricardo.soms.DbHelper.dbHelper
import com.ricardo.soms.objetos.*
import com.zebra.rfid.api3.*
import com.zebra.rfid.api3.Antennas.AntennaRfConfig
import com.zebra.rfid.api3.Antennas.SingulationControl
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

companion object {
    private lateinit var availableRFIDReaderList: ArrayList<ReaderDevice>
    private lateinit var readerDevice: ReaderDevice
    //lateinit var reader: RFIDReader
    private var reader: RFIDReader? = null
    private var readers: Readers? = null
    //lateinit var readers:Readers
    lateinit var context: Context
    private lateinit var antena: Antennas.AntennaRfConfig
    var rfModeTable: RFModeTable? = null

    var eventHandler: EventHandlerBoton? = null

    var lsTags:ArrayList<String> = ArrayList()

    var listaTags:ArrayList<tags> = ArrayList()

    lateinit var myTags2: Array<TagData>

    var adapter: TagsAdapter? = null

    var usr = ""
}



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        context=this


        val i = intent
        usr = i.getStringExtra("usuario").toString()


        readers = Readers(this,ENUM_TRANSPORT.ALL)




        //adapter = TagsAdapter(this, lsTags)

        //adapter = TagsAdapter(this, listaTags)


        //configurar()


        BtnProcesar.setOnClickListener{
            procesar()
        }

        reconectar.setOnClickListener{
            if(reader?.isConnected == true){
                stopInventory()
                reconectar.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.ic_baseline_play_circle_24,0,0,0)
                reconectar.text = "Capturar TAGS"
            }else{

                configurar() //comienza la lectura RFID
                reconectar.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.ic_baseline_stop_circle_24,0,0,0)
                reconectar.text = "Detener"

            }
        }

        stop.setOnClickListener {
            //se inhabilita para unificar el stop en un solo boton
            stopInventory()
        }

        BtnGuardar.setOnClickListener {
            guardarTags()
        }


    }

    private fun guardarTags() {
        val objDbH = dbHelper(this)
        var objUsr = usuario()
        var objProd = producto()
        var objInv = inventarios()
        var sucess = (-1).toLong()
        objInv = objDbH.selectIdInventario()
        try{

            for(listT in listaTags){

                objUsr.nombre = usr

                objProd.codigoEPC = listT.idTag
                objProd.cantidad = 1
                objProd.idSabueso = "RFID"


                objInv.producto = objProd
                objInv.usuario = objUsr

                sucess = objDbH.insertConteo(objInv)

            }
            if(sucess != (-1).toLong()){
                Toast.makeText(this,"Guardado!",Toast.LENGTH_LONG).show()
            }else{

                Toast.makeText(this,"No se pudo almacenar, intente nuevamente. "+sucess,Toast.LENGTH_LONG).show()
            }



        }catch (e:Exception){
            Toast.makeText(this,"Error guardarTags "+e.message,Toast.LENGTH_LONG).show()

        }






    }

    private fun configurar() {
        listaTags.clear()
        configurarRFIDHilo().execute()
    }


    inner class configurarRFIDHilo:AsyncTask<Void,Void,String>(){
        override fun doInBackground(vararg p0: Void?): String {
            // Toma el listado de reders disponibles
            availableRFIDReaderList =  readers!!.GetAvailableRFIDReaderList()

            if(availableRFIDReaderList.size != 0){
                if(availableRFIDReaderList.size == 1){
                    readerDevice = availableRFIDReaderList.get(0)
                    reader = availableRFIDReaderList.get(0).rfidReader

                }else{
                    var algo="es una lista de readers"
                }

            }
            // Establece la conexion con el reader
            //if(reader?.isConnected == true)
            reader?.connect()


            //Configuración de antena RFID

            //Configuración de antena RFID


            //Configuración de antena RFID
            try {
                rfModeTable = reader?.ReaderCapabilities?.RFModes?.getRFModeTableInfo(0)
            } catch (ex: Exception) {
            }

            // Este codigo no afecta al ser quitado

            // Este codigo no afecta al ser quitado
            var antennaRfConfig: AntennaRfConfig? = null
            try {
                antennaRfConfig = reader?.Config?.Antennas?.getAntennaRfConfig(1)
                antennaRfConfig?.setrfModeTableIndex(0)
                antennaRfConfig?.tari = 0

                antennaRfConfig?.transmitPowerIndex = 270
                reader?.Config?.Antennas?.setAntennaRfConfig(1, antennaRfConfig)

            } catch (e: InvalidUsageException) {
                e.printStackTrace()
            } catch (e: OperationFailureException) {
                e.printStackTrace()
            } catch (ex: java.lang.Exception) {
            }

            // Este codigo no afecta al ser quitado
            // Get Singulation Control for the antenna 1 Antennas.

            // Este codigo no afecta al ser quitado
            // Get Singulation Control for the antenna 1 Antennas.
            val singulationControl: SingulationControl
            try {
                singulationControl = reader?.Config?.Antennas?.getSingulationControl(1)!!

                // Set Singulation Control for the antenna 1
                //Antennas.SingulationControl singulationControl;
                singulationControl.session = SESSION.SESSION_S0
                singulationControl.tagPopulation = 30.toShort()
                singulationControl.Action.slFlag = SL_FLAG.SL_ALL
                singulationControl.Action.inventoryState = INVENTORY_STATE.INVENTORY_STATE_A
                reader?.Config?.Antennas?.setSingulationControl(1, singulationControl)
            } catch (e: InvalidUsageException) {
                e.printStackTrace()
            } catch (e: OperationFailureException) {
                e.printStackTrace()
            } catch (ex: java.lang.Exception) {
            }

            // Este codigo no afecta al ser quitado
            // Get and Set regulatory configuration settings

            // Este codigo no afecta al ser quitado
            // Get and Set regulatory configuration settings
            var regulatoryConfig: RegulatoryConfig? = null
            try {
                regulatoryConfig = reader?.Config?.getRegulatoryConfig()
                val regionInfo: RegionInfo? = reader?.ReaderCapabilities?.SupportedRegions?.getRegionInfo(1)
                regulatoryConfig?.region = regionInfo?.regionCode

                //aqui cambio bastante el codigo
                regionInfo?.isHoppingConfigurable?.let { regulatoryConfig?.setIsHoppingOn(it) }
                regulatoryConfig?.setEnabledChannels(regionInfo?.supportedChannels)
                reader?.Config?.setRegulatoryConfig(regulatoryConfig)
                reader?.Config?.saveConfig()
            } catch (e: InvalidUsageException) {
                e.printStackTrace()
            } catch (e: OperationFailureException) {
                e.printStackTrace()
            } catch (ex: java.lang.Exception) {
            }

            ///////////////////////////////////*************************************
            // Se agrega los siguientes los cuales son los que permiten realizar la captura
            ///////////////////////////////////*************************************
            // Se agrega los siguientes los cuales son los que permiten realizar la captura
            val eventHandler = EventHandlerBoton()
            // sin este codigo se captura los tag rfid, pero no se pueden mostrar en pantalla

            try {
                reader?.Events?.addEventsListener(eventHandler)
                // Subscribe required status notification
                reader?.Events?.setInventoryStartEvent(true)
                reader?.Events?.setInventoryStopEvent(true)
                // enables tag read notification. if this is set to false, no tag read notification is send
                reader?.Events?.setTagReadEvent(true)

                reader?.Events?.setReaderDisconnectEvent(true)
                reader?.Events?.setBatteryEvent(true)

            } catch (e: InvalidUsageException) {
                e.printStackTrace()
                //return 6;
            } catch (e: OperationFailureException) {
                e.printStackTrace()
                //return 7;
            }
            //Sin este codigo no toma lectura rfid
            //Sin este codigo no toma lectura rfid
            try {
                if (reader != null) reader?.Config?.getDeviceStatus(true, false, false)
            } catch (e: InvalidUsageException) {
                e.printStackTrace()
                //return 8
            } catch (e: OperationFailureException) {
                e.printStackTrace()
                //return 9
            } catch (ex: java.lang.Exception) {
                //return 10
            }
            //Sin este codigo no toma lectura rfid
            //Sin este codigo no toma lectura rfid
            try {
                // Get tag storage settings from the reader
                val tagStorageSettings: TagStorageSettings? =
                    reader?.Config?.getTagStorageSettings()
                // set tag storage settings on the reader with all fields
                tagStorageSettings?.setTagFields(TAG_FIELD.ALL_TAG_FIELDS)
                reader?.Config?.setTagStorageSettings(tagStorageSettings)
                val tagField = arrayOfNulls<TAG_FIELD>(4)
                tagField[0] = TAG_FIELD.PC
                tagField[1] = TAG_FIELD.PEAK_RSSI
                tagField[2] = TAG_FIELD.TAG_SEEN_COUNT
                tagField[3] = TAG_FIELD.CRC
                tagStorageSettings?.tagFields = tagField
                reader?.Config?.setTagStorageSettings(tagStorageSettings)
            } catch (e: InvalidUsageException) {
                e.printStackTrace()
                //return 11
            } catch (e: OperationFailureException) {
                e.printStackTrace()
                //return 12
            } catch (ex: java.lang.Exception) {
                //return 13
            }
            // Sin estas lineas de codigo no es posible tomar el inventario
            // Sin estas lineas de codigo no es posible tomar el inventario
            try {
                reader?.Config?.setUniqueTagReport(true)

                reader?.Actions?.Inventory?.perform()





            } catch (e: InvalidUsageException) {
                e.printStackTrace()
                //return 14
            } catch (e: OperationFailureException) {
                e.printStackTrace()
                //return 15
            } catch (ex: java.lang.Exception) {
                //return 16
            }

            // No se encuentra utilidad en este codigo igual se deja.
            // Read user memory bank for the given tag ID

            // No se encuentra utilidad en este codigo igual se deja.
            // Read user memory bank for the given tag ID
            val tagAccess = TagAccess()
            val readAccessParams = tagAccess.ReadAccessParams()
            val readAccessTag: TagData
            readAccessParams.accessPassword = 0
            readAccessParams.count = 4 // read 4 words

            readAccessParams.memoryBank = MEMORY_BANK.MEMORY_BANK_USER
            readAccessParams.offset = 0 // start reading from word offset 0

            try {
                readAccessTag = reader?.Actions?.TagAccess?.readWait("", readAccessParams, null)!!
                println(
                    readAccessTag.memoryBank.toString() + " : " +
                            readAccessTag.memoryBankData
                )

                ////////OK
            } catch (e: InvalidUsageException) {
                e.printStackTrace()
            } catch (e: OperationFailureException) {
                e.printStackTrace()
            } catch (ex: java.lang.Exception) {
            }

            return ""
        }

        override fun onPostExecute(result: String?) {

            progressBar.visibility = View.GONE
        }

        override fun onPreExecute() {
            progressBar.visibility = View.VISIBLE
        }


    }

    class EventHandlerBoton : RfidEventsListener{
        override fun eventReadNotify(p0: RfidReadEvents?) {
            // Recommended to use new method getReadTagsEx for better performance in case of large
            // tag population

            // Recommended to use new method getReadTagsEx for better performance in case of large
            // tag population
            val myTags: Array<TagData> = reader?.Actions?.getReadTags(100) as Array<TagData>





            var tg = tags()

            if (myTags != null) {
                for (index in myTags.indices) {
                    println("Tag ID " + myTags[index].tagID)


                     tg.idTag = myTags[index].tagID
                    ///





                    if (myTags[index].opCode == ACCESS_OPERATION_CODE.ACCESS_OPERATION_READ &&
                        myTags[index].opStatus == ACCESS_OPERATION_STATUS.ACCESS_SUCCESS) {
                        if (myTags[index].memoryBankData.length > 0) {
                            println(" Mem Bank Data " + myTags[index].memoryBankData)
                        }
                    }







                   if (myTags[index].isContainsLocationInfo) {
                       var tag = index
                        val dist = myTags[tag].LocationInfo.relativeDistance

                        tg.distanceTag = myTags[tag].LocationInfo.relativeDistance.toString()

                        println("Tag relative distance $dist")
                    }



                    if (!listaTags.contains(tg)) {

                        listaTags.add(tg)
                    }

                }
            }
        }



        override fun eventStatusNotify(e: RfidStatusEvents?) {
              if (e != null) {
                           println("estado: " + e.StatusEventData.getStatusEventType())
             }

        }

        fun localizar(idTag:String):Boolean{
        var s = false
            if(reader?.isConnected == true) {

                reader?.Actions?.TagLocationing?.Perform(asciiToHex(idTag), null,null)
                s = true
            }
            return s
        }

        fun asciiToHex(data:String):String{
            var p0 = data
            if(p0 != null){
                if (hextoascii.isDatainHex(p0)){
                    return p0
                }
                p0 = p0.substring(1,p0.length-1)
                val b: ByteArray = p0.toByteArray()
                val builder = StringBuilder()
                for (c in b) {
                    builder.append(Integer.toHexString(c.toInt()))
                }
                return builder.toString()
            }
            return p0
        }



    }


    override fun onPause() {
        super.onPause()
        try {
            reader?.disconnect()
        } catch (e: InvalidUsageException) {
            e.printStackTrace()
        } catch (e: OperationFailureException) {
            e.printStackTrace()
        } catch (e: java.lang.Exception) {
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        dispose()
    }

    private fun dispose() {
        try {
            if (readers != null) {
                reader = null
                readers!!.Dispose()
                readers = null
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onRestart() {
        super.onRestart()
        configurar()
    }

    @Synchronized
    fun stopInventory() {
        try {
            if(reader?.isConnected == true){

                reader?.disconnect()

                BtnProcesar.isEnabled = true
            }

        } catch (e: InvalidUsageException) {
            e.printStackTrace()
        } catch (e: OperationFailureException) {
            e.printStackTrace()
        } catch (e: java.lang.Exception) {
        }

    }

    fun RFIDReaderAppeared(readerDevice: ReaderDevice?) {

    }

    fun RFIDReaderDisappeared(readerDevice: ReaderDevice?) {

    }

    ///////////////////////////////////////////////////////////





    fun procesar(){
        //val adapter2 = TagsAdapter(this, listaTags)

    if(listaTags.isNotEmpty()){
    BtnGuardar.isEnabled = true

        adapter = TagsAdapter(this, listaTags)

        lv_encontrados.adapter = adapter


        lv_encontrados.onItemLongClickListener = AdapterView.OnItemLongClickListener{
                parent,view,position,id ->

            var tagSelect = listaTags.elementAt(position)
            Toast.makeText(this,"Item "+ tagSelect.codeTag + " " + tagSelect.distanceTag + " " + tagSelect.idTag,Toast.LENGTH_LONG).show()
            //abir una ventanita para mostrar mas info del tag seleccionado

            true
        }
    }else{

        Toast.makeText(this,"No se encontraron TAGS",Toast.LENGTH_LONG).show()
    }

    }










}
