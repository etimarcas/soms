package com.ricardo.soms

import android.content.Context
import android.os.AsyncTask
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.zebra.rfid.api3.*
import com.zebra.rfid.api3.Antennas.AntennaRfConfig
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
}



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        context=this

        iniciarSDK()

        test1.setOnClickListener{
            test1()
        }
    }

    fun iniciarSDK(){

        if(readers!=null){
            crearInstanciaTarea().execute()
        }else{
            conectarTarea().execute()
        }
    }



    inner class crearInstanciaTarea: AsyncTask<Void,Void,String>(){
    override fun doInBackground(vararg p0: Void?): String {

        readers = Readers(context,ENUM_TRANSPORT.ALL)
        availableRFIDReaderList = readers!!.GetAvailableRFIDReaderList()


        return ""
    }

    override fun onPostExecute(result: String?) {
        super.onPostExecute(result)
        conectarTarea().execute()
    }


}

    inner class conectarTarea: AsyncTask<Void,Void,String>(){
        override fun doInBackground(vararg p0: Void?): String {
            GetAvailableReader()
            if(reader != null){
                return connect()

            }
            return ""
        }

        override fun onPostExecute(result: String?) {
            super.onPostExecute(result)
        }

    }


    @Synchronized
    private fun GetAvailableReader() {
        try{

            if(readers != null){
                if(readers!!.GetAvailableRFIDReaderList() != null){

                    availableRFIDReaderList = readers!!.GetAvailableRFIDReaderList()

                    if(availableRFIDReaderList.size != 0){
                        if(availableRFIDReaderList.size == 1){
                            readerDevice = availableRFIDReaderList.get(0)
                            reader = readerDevice.rfidReader

                        }else{
                            var algo="es una lista de readers"
                        }

                    }

                }
            }

        }catch (e:Exception){Toast.makeText(this,"getAvailableReaders"+e.message,Toast.LENGTH_LONG).show()}

    }


    private fun isReaderConnected():Boolean{
        try{
        if(reader != null && reader?.isConnected == true){
            return true
        }else{
            return false
        }
        }catch (e:Exception){
            Toast.makeText(this,"error "+ e.message,Toast.LENGTH_LONG).show()
            return false
        }

    }


    fun test1(){

        if(!isReaderConnected()){
            Toast.makeText(this,"No conectado",Toast.LENGTH_LONG).show()
        }

        try{
            var config: AntennaRfConfig? = null

            config = reader?.Config?.Antennas?.getAntennaConfig(1) as Nothing?
            config?.transmitFrequencyIndex = 100
            config?.setrfModeTableIndex(0)
            config?.tari=0
            reader?.Config?.Antennas?.setAntennaRfConfig(1,config)
            Toast.makeText(this,"Antenna power Set to 220",Toast.LENGTH_LONG).show()

        }catch (e:InvalidUsageException){
            Toast.makeText(this,"error "+e.message,Toast.LENGTH_LONG).show()
        }
        catch (e:OperationFailureException){
            Toast.makeText(this,"error "+e.message,Toast.LENGTH_LONG).show()
        }
    catch (e:Exception){
        Toast.makeText(this,"error "+e.message,Toast.LENGTH_LONG).show()
    }

    }

    fun connect():String{

        if(reader != null){
            try{
                if(!reader?.isConnected!!){
                    reader?.connect()
                    configureReader()
                    return "conectado"
                }

            }catch (e:Exception){
                Toast.makeText(this,"Error fun connect: "+e.message,Toast.LENGTH_LONG).show()
            }
        }

        return ""
    }

    fun configureReader(){
        if(reader?.isConnected() == true){
            val triggerInfo = TriggerInfo()
            triggerInfo.StartTrigger.triggerType = START_TRIGGER_TYPE.START_TRIGGER_TYPE_IMMEDIATE
            triggerInfo.StopTrigger.triggerType = STOP_TRIGGER_TYPE.STOP_TRIGGER_TYPE_IMMEDIATE
        try{

            reader?.Events?.setHandheldEvent(true)
            reader?.Events?.setTagReadEvent(true)
            reader?.Events?.setAttachTagDataWithReadEvent(false)
            reader?.Config?.setLedBlinkEnable(true)

            reader?.Config?.setTriggerMode(ENUM_TRIGGER_MODE.RFID_MODE,true)

            reader?.Config?.startTrigger = triggerInfo.StartTrigger
            reader?.Config?.stopTrigger = triggerInfo.StopTrigger

            val MAX_POWER = reader?.ReaderCapabilities?.transmitPowerLevelValues?.size?.minus(1)

            antena = reader?.Config?.Antennas?.getAntennaRfConfig(1)!!
            if (MAX_POWER != null) {
                antena?.transmitPowerIndex=MAX_POWER
            }
            antena.setrfModeTableIndex(0)
            antena.tari=0
            reader?.Config?.Antennas?.setAntennaRfConfig(1, antena)

            val control : Antennas.SingulationControl? = reader?.Config?.Antennas?.getSingulationControl(1)
            control?.session = SESSION.SESSION_S0


            //tAG POPULATION
            control?.tagPopulation = ApplicationConstants.TAG_POPULATION
            control?.Action?.slFlag = SL_FLAG.SL_ALL

        }catch (e:Exception){
            Toast.makeText(this,"Error fun conconfigureReader: "+e.message,Toast.LENGTH_LONG).show()
        }


        }

    }

    fun handleTriggerPress(pressed:Boolean){
        try{
        if(pressed){
            if(!isReaderConnected()){

            }else{
                reader?.Actions?.Inventory?.perform()
            }
        }else{
            if(!isReaderConnected()){

            }else{
                reader?.Actions?.Inventory?.stop()
            }


        }

        }catch (e:Exception){Toast.makeText(this,"Error fun handleTriggerPress: "+e.message,Toast.LENGTH_LONG).show()}

    }

    fun handleTagData(tag:Array<TagData>){
        var sb = StringBuilder()

        for(tags in tag){
            sb.append(tags.tagID+"\n")
        }
    }

}