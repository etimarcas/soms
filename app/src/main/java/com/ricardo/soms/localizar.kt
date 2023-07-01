package com.ricardo.soms

import android.media.AudioManager
import android.media.ToneGenerator
import android.os.AsyncTask
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.zebra.rfid.api3.*
import java.util.*


class localizar : AppCompatActivity() {

companion object {

    var mConnectedReader: RFIDReader? = null

    var readers: Readers? = null

    var mConnectedDevice: ReaderDevice? = null

    private var mContext: localizar? = null

    private lateinit var textrfid:TextView
    private lateinit var pglocalizar:ProgressBar
    private lateinit var tbIdTag:EditText

    var toneType = ToneGenerator.TONE_PROP_BEEP
    var tone = ToneGenerator(AudioManager.STREAM_ALARM, 100)

    var inventario=0


}

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_localizar)

        /*if(savedInstanceState == null){
             mEventHandler = eventHandler()
        }else{
            mConnectedReader?.Events?.removeEventsListener(mEventHandler)
            mEventHandler = eventHandler()
            mConnectedReader?.Events?.addEventsListener(mEventHandler)
        }*/


        mContext=this

        textrfid = findViewById(R.id.textrfid)
        pglocalizar = findViewById(R.id.pglocalizar)
        tbIdTag = findViewById(R.id.tbIdTag)

        if(readers == null){
            readers = Readers(this,ENUM_TRANSPORT.ALL)
        }

        if(savedInstanceState == null) {
            loadReaders().execute()
        }


    }




    inner class loadReaders:AsyncTask<Void,Void,Boolean>(){
            override fun doInBackground(vararg p0: Void?): Boolean {
                var invalidUsageException: InvalidUsageException? = null
                var resultado=false

              try {
                  if(readers != null){

                      if(readers!!.GetAvailableRFIDReaderList() != null){

                          var listaReadersDisponibles :ArrayList<ReaderDevice> = readers!!.GetAvailableRFIDReaderList()

                          if(listaReadersDisponibles.size != 0 && listaReadersDisponibles.size == 1){
                              mConnectedDevice = listaReadersDisponibles.get(0)
                          }

                          if(mConnectedDevice != null){
                              mConnectedReader = mConnectedDevice?.rfidReader
                              mConnectedReader?.connect()
                              configurarReader()
                              resultado = true
                          }

                      }
                  }
              }catch (e:InvalidUsageException){
                  e.printStackTrace()
                  invalidUsageException = e
              }catch (e:OperationFailureException){
                e.printStackTrace()
                println(e.toString())
              }

                if(invalidUsageException != null){
                    readers!!.Dispose()

                }


                return resultado
            }

            override fun onPostExecute(result: Boolean?) {
                super.onPostExecute(result)
                if(result == true){
                    Toast.makeText(applicationContext,"Reader Conectado",Toast.LENGTH_LONG).show()
                }else{
                    Toast.makeText(applicationContext,"Reader no disponible",Toast.LENGTH_LONG).show()
                }

            }



    }


    fun configurarReader() {

        if(mConnectedReader?.isConnected == true){
            var triggerInfo = TriggerInfo()
            triggerInfo.StartTrigger.setTriggerType(START_TRIGGER_TYPE.START_TRIGGER_TYPE_IMMEDIATE)
            triggerInfo.StopTrigger.setTriggerType(STOP_TRIGGER_TYPE.STOP_TRIGGER_TYPE_IMMEDIATE)

            try{


                //if(mEventHandler == null){
                   var mEventHandler = eventHandlerLoc()
                    mConnectedReader?.Events?.addEventsListener(mEventHandler)

                    mConnectedReader?.Events?.setHandheldEvent(true)
                    mConnectedReader?.Events?.setTagReadEvent(true)
                    mConnectedReader?.Events?.setAttachTagDataWithReadEvent(false)
                    mConnectedReader?.Events?.setInventoryStartEvent(true)
                    mConnectedReader?.Events?.setInventoryStopEvent(true)
                    mConnectedReader?.Events?.setReaderDisconnectEvent(true)

                /*





                    //mConnectedReader?.Events?.setBatteryEvent(true)
*/
                    /*

                    mConnectedReader?.Config?.setTriggerMode(ENUM_TRIGGER_MODE.RFID_MODE,true)
                    mConnectedReader?.Config?.startTrigger = triggerInfo.StartTrigger
                    mConnectedReader?.Config?.stopTrigger = triggerInfo.StopTrigger

*/
                /*
                    //pruebas
                    mConnectedReader?.Config?.getDeviceStatus(true, false, false)
                    val tagStorageSettings: TagStorageSettings? = mConnectedReader?.Config?.tagStorageSettings
                    tagStorageSettings?.setTagFields(TAG_FIELD.ALL_TAG_FIELDS)
                    mConnectedReader?.Config?.tagStorageSettings = tagStorageSettings
                    val tagField = arrayOfNulls<TAG_FIELD>(4)
                    tagField[0] = TAG_FIELD.PC
                    tagField[1] = TAG_FIELD.PEAK_RSSI
                    tagField[2] = TAG_FIELD.TAG_SEEN_COUNT
                    tagField[3] = TAG_FIELD.CRC
                    tagStorageSettings?.tagFields = tagField
                    mConnectedReader?.Config?.tagStorageSettings = tagStorageSettings
*/
                    //configura el reader para que al capturar los tags sehan unicos por ID
                    mConnectedReader?.Config?.setUniqueTagReport(true)


               // }


            }catch (e:InvalidUsageException){
                println(e.toString())
            }
        }

    }





    class eventHandler : RfidEventsListener {
        override fun eventReadNotify(e: RfidReadEvents?) {
            if(mConnectedReader?.isConnected == true){

                val myTags: Array<TagData> = mConnectedReader?.Actions?.getReadTags(100) as Array<TagData>

                if (myTags != null) {

                    for (index in myTags.indices) {
                        println("Tag ID " + myTags[index].tagID)

                        if (myTags[index].isContainsLocationInfo) {
                            val dist = myTags[index].LocationInfo.relativeDistance
                            println("Tag relative distance $dist")
                        }

                    }
                }


                var async = AsyncDataUpdate()
                async.execute(myTags)

           }



        }

        override fun eventStatusNotify(e: RfidStatusEvents?) {
            if (e != null) {
                println("estado: " + e.StatusEventData.getStatusEventType())
            }
        }

    }


    class eventHandlerLoc : RfidEventsListener {
        override fun eventReadNotify(e: RfidReadEvents?) {
            if(mConnectedReader?.isConnected == true){

                val myTags: Array<TagData> = mConnectedReader?.Actions?.getReadTags(100) as Array<TagData>

                if (myTags != null) {

                    for (index in myTags.indices) {
                        println("Tag ID " + myTags[index].tagID)

                        if (myTags[index].isContainsLocationInfo) {
                            val dist = myTags[index].LocationInfo.relativeDistance
                            println("Tag relative distance $dist")
                        }

                    }
                }


                var async = AsyncLocationUpdate()
                async.execute(myTags)

            }



        }

        override fun eventStatusNotify(e: RfidStatusEvents?) {
            if (e != null) {
                println("estado: " + e.StatusEventData.getStatusEventType())
            }
        }

    }


     class AsyncDataUpdate:AsyncTask<Array<TagData>,Void,Void>(){


        override fun doInBackground(vararg p0: Array<TagData>?): Void? {

            mContext?.handleTagdata(p0.get(0))

            return null
        }

    }

     class AsyncLocationUpdate:AsyncTask<Array<TagData>,Void,Void>(){


        override fun doInBackground(vararg p0: Array<TagData>?): Void? {

            mContext?.handleTagLocation(p0.get(0))

            return null
        }

    }


    fun handleTagdata(tagData: Array<TagData>?) {
        val sb = StringBuilder()
        if (tagData != null) {
            for (index in tagData.indices) {
                sb.append(tagData.get(index)?.getTagID()  + "\n")
                //+ tagData.get(index)?.LocationInfo?.relativeDistance
            }
            runOnUiThread { textrfid.append(sb.toString()) }
        }
    }

    fun iniciar(view: View) {
        if(estadoReader()){
            if(inventario==0){
                mConnectedReader?.Actions?.Inventory?.perform()
                inventario=1
            }else{
                mConnectedReader?.Actions?.Inventory?.stop()

                inventario=0
            }
        }

    }



    fun handleTagLocation(tagData: Array<TagData>?) {
        val sb = StringBuilder()
        if (tagData != null) {
            for (index in tagData.indices) {
                sb.append(tagData.get(index)?.LocationInfo?.relativeDistance.toString())

                //+
            }
            runOnUiThread {
                var data = sb.toString()
                pglocalizar.setProgress(data.toIntOrNull()!!)
                textrfid.setText(data)
                if(data.toLongOrNull()!! > 0){
                    startIntervalBeep(data.toLong())
                }

            }
        }
    }

    fun localizar(view: View) {

        if(estadoReader()){
            if(inventario==0){
                //Configura el reader para localizar TAG por ID
                var idTag = tbIdTag.text.trim().toString()
                if(idTag != null && idTag != "")
                mConnectedReader?.Actions?.TagLocationing?.Perform(idTag, null, null)

                inventario=1
            }else{

                mConnectedReader?.Actions?.TagLocationing?.Stop()
                inventario=0
            }
        }

    }

    fun estadoReader():Boolean{
        return mConnectedReader != null && mConnectedReader?.isConnected == true

    }

    private fun beep(){

        tone.startTone(toneType)
        tone.stopTone()
    }

    private fun startIntervalBeep(tiempo:Long){
        var POLLING_INTERVAL1 = 0+(((300-0)*(100-tiempo))/100)
            beep()
            val task: TimerTask = object : TimerTask() {
                override fun run() {

                    tone.stopTone()
                }
            }
            var locatebeep = Timer()
            locatebeep.schedule(task, POLLING_INTERVAL1.toLong(), 10)



    }


}