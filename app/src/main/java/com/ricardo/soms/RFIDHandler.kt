package com.ricardo.soms

import android.os.AsyncTask
import android.util.Log
import android.widget.TextView
import com.zebra.rfid.api3.*

class RFIDHandler //:Readers.RFIDReaderEventHandler
{


    companion object {
        const val TAG = "RFID_SAMPLE"

        private var readers: Readers? = null

        private lateinit var availableRFIDReaderList: ArrayList<ReaderDevice>

        private lateinit var readerDevice: ReaderDevice

        lateinit var reader: RFIDReader

       //* private var eventHandler: EventHandler? = null

        //private lateinit var antenna: Antennas.AntennaRfConfig

        lateinit var textView:TextView

        private var context: MainActivity? = null

        var readername = "RFD8500123"

    }
/*
    fun onCreate(activity: MainActivity) {
        // application context
        context = activity
        // Status UI
        //textView = activity.statusTextViewRFID
        // SDK
        InitSDK()
    }

    fun Test1():String{
        if(!isReaderConnect()){
            try{
                var config: Antennas.AntennaRfConfig? = null
                config = reader.Config.Antennas.getAntennaRfConfig(1)
                config.transmitFrequencyIndex=100
                config.setrfModeTableIndex(0)
                config.tari=0
                reader.Config.Antennas.setAntennaRfConfig(1,config)

            }catch (ex:InvalidUsageException){
                ex.printStackTrace()
            }catch (ex:OperationFailureException){
                ex.printStackTrace()
                return ex.results.toString() + " "+ex.vendorMessage
            }


        }


        return "Antenna power Set to 220"
    }

    fun isReaderConnect():Boolean{
        return if (reader != null && reader.isConnected) true else {
            Log.d(RFIDHandler.TAG, "Reader conectado")
            false
        }

    }
    //ciclo de vida
    fun onResume():String{

        return connect()
    }

    fun onPause(){
        disconnect()
    }

    fun onDestroy(){
        dispose()
    }

    //sdk RFID ZEBRA

    fun InitSDK(){
        Log.d(TAG, "InitSDK")
        if (readers == null) {
            CreateInstanceTask().execute()
        } else {
            ConnectionTask().execute()
        }
    }


    inner class CreateInstanceTask: AsyncTask<Void, Void, String>(){
        override fun doInBackground(vararg p0: Void?): String {
            Log.d(TAG, "CreateInstanceTask")
            // Based on support available on host device choose the reader type
            val invalidUsageException: InvalidUsageException? = null
            readers = Readers(context, ENUM_TRANSPORT.ALL)
            availableRFIDReaderList = readers!!.GetAvailableRFIDReaderList()
            if (invalidUsageException != null) {
                readers!!.Dispose()
                readers = null
                if (readers == null) {
                    readers = Readers(context, ENUM_TRANSPORT.BLUETOOTH)
                }
            }
            return ""
        }

        override fun onPostExecute(result: String?) {
            super.onPostExecute(result)
            ConnectionTask().execute()
        }

    }

    inner class ConnectionTask: AsyncTask<Void, Void, String>(){
        override fun doInBackground(vararg p0: Void?): String {
            Log.d(TAG, "ConnectionTask")
            GetAvailableReader()
            return if (reader != null) connect() else "Failed to find or connect reader"
        }

        override fun onPostExecute(result: String?) {
            super.onPostExecute(result)
            textView.setText(result)
        }


    }


    @Synchronized
    private fun GetAvailableReader() {
        Log.d(TAG, "GetAvailableReader")
        if (readers != null) {
            Readers.attach(this)
            if (readers!!.GetAvailableRFIDReaderList() != null) {
                availableRFIDReaderList = readers!!.GetAvailableRFIDReaderList()
                if (availableRFIDReaderList.size != 0) {
                    // if single reader is available then connect it
                    if (availableRFIDReaderList.size == 1) {
                        readerDevice = availableRFIDReaderList[0]
                        reader = readerDevice.getRFIDReader()
                    } else {
                        // search reader specified by name
                        for (device in availableRFIDReaderList) {
                            if (device.name == readername) {
                                readerDevice = device
                                reader = readerDevice.getRFIDReader()
                            }
                        }
                    }
                }
            }
        }
    }

    override fun RFIDReaderAppeared(p0: ReaderDevice?) {
        Log.d(TAG, "RFIDReaderAppeared " + readerDevice.name)
        ConnectionTask().execute()
    }

    override fun RFIDReaderDisappeared(p0: ReaderDevice?) {
        Log.d(TAG, "RFIDReaderDisappeared " + readerDevice.name)
        if (readerDevice.name == reader.hostName) disconnect()
    }

    @Synchronized
    private fun connect(): String? {
        if (reader != null) {
            Log.d(TAG, "connect " + reader.hostName)
            try {
                if (!reader.isConnected) {
                    // Establish connection to the RFID Reader
                    reader.connect()
                    ConfigureReader()
                    return "Connected"
                }
            } catch (e: InvalidUsageException) {
                e.printStackTrace()
            } catch (e: OperationFailureException) {
                e.printStackTrace()
                Log.d(TAG, "OperationFailureException " + e.vendorMessage)
                val des = e.results.toString()
                return "Connection failed" + e.vendorMessage + " " + des
            }
        }
        return ""
    }

    private fun ConfigureReader() {
        Log.d(TAG, "ConfigureReader " + reader.hostName)
        if (reader.isConnected) {
            val triggerInfo = TriggerInfo()
            triggerInfo.StartTrigger.triggerType = START_TRIGGER_TYPE.START_TRIGGER_TYPE_IMMEDIATE
            triggerInfo.StopTrigger.triggerType = STOP_TRIGGER_TYPE.STOP_TRIGGER_TYPE_IMMEDIATE
            try {
                // receive events from reader
                if (eventHandler == null) eventHandler =
                    EventHandler()
                reader.Events.addEventsListener(eventHandler)
                // HH event
                reader.Events.setHandheldEvent(true)
                // tag event with tag data
                reader.Events.setTagReadEvent(true)
                reader.Events.setAttachTagDataWithReadEvent(false)
                // set trigger mode as rfid so scanner beam will not come
                reader.Config.setTriggerMode(ENUM_TRIGGER_MODE.RFID_MODE, true)
                // set start and stop triggers
                reader.Config.startTrigger = triggerInfo.StartTrigger
                reader.Config.stopTrigger = triggerInfo.StopTrigger
                // power levels are index based so maximum power supported get the last one
                MAX_POWER = reader.ReaderCapabilities.transmitPowerLevelValues.size - 1
                // set antenna configurations
                val config = reader.Config.Antennas.getAntennaRfConfig(1)
                config.transmitPowerIndex = MAX_POWER
                config.setrfModeTableIndex(0)
                config.tari = 0
                reader.Config.Antennas.setAntennaRfConfig(1, config)
                // Set the singulation control
                val s1_singulationControl = reader.Config.Antennas.getSingulationControl(1)
                s1_singulationControl.session = SESSION.SESSION_S0
                s1_singulationControl.Action.inventoryState = INVENTORY_STATE.INVENTORY_STATE_A
                s1_singulationControl.Action.slFlag = SL_FLAG.SL_ALL
                reader.Config.Antennas.setSingulationControl(1, s1_singulationControl)
                // delete any prefilters
                reader.Actions.PreFilters.deleteAll()
                //
            } catch (e: InvalidUsageException) {
                e.printStackTrace()
            } catch (e: OperationFailureException) {
                e.printStackTrace()
            }
        }
    }

    @Synchronized
    private fun disconnect() {
        Log.d(TAG, "disconnect " + reader)
        try {
            if (reader != null) {
                reader.Events.removeEventsListener(eventHandler)
                reader.disconnect()
                context.runOnUiThread(Runnable { textView.text = "Disconnected" })
            }
        } catch (e: InvalidUsageException) {
            e.printStackTrace()
        } catch (e: OperationFailureException) {
            e.printStackTrace()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    class EventHandler: RfidEventsListener {
        override fun eventReadNotify(e: RfidReadEvents?) {

            // Recommended to use new method getReadTagsEx for better performance in case of large tag population
            val myTags = reader.Actions.getReadTags(100)
            if (myTags != null) {
                for (index in myTags.indices) {
                    Log.d(TAG, "Tag ID " + myTags[index].tagID)
                    if (myTags[index].opCode === ACCESS_OPERATION_CODE.ACCESS_OPERATION_READ &&
                        myTags[index].opStatus === ACCESS_OPERATION_STATUS.ACCESS_SUCCESS
                    ) {
                        if (myTags[index].memoryBankData.length > 0) {
                            Log.d(TAG, " Mem Bank Data " + myTags[index].memoryBankData)
                        }
                    }
                    if (myTags[index].isContainsLocationInfo) {
                        val dist = myTags[index].LocationInfo.relativeDistance
                        Log.d(TAG, "Tag relative distance $dist")
                    }
                }
                // possibly if operation was invoked from async task and still busy
                // handle tag data responses on parallel thread thus THREAD_POOL_EXECUTOR
                AsyncDataUpdate().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, *myTags)
            }
        }

        override fun eventStatusNotify(rfidStatusEvents: RfidStatusEvents?) {
            if (rfidStatusEvents != null) {
                Log.d(
                    TAG,
                    "Status Notification: " + rfidStatusEvents.StatusEventData.getStatusEventType()
                )
            }
            if (rfidStatusEvents != null) {
                if (rfidStatusEvents.StatusEventData.getStatusEventType() === STATUS_EVENT_TYPE.HANDHELD_TRIGGER_EVENT) {
                    if (rfidStatusEvents.StatusEventData.HandheldTriggerEventData.getHandheldEvent() === HANDHELD_TRIGGER_EVENT_TYPE.HANDHELD_TRIGGER_PRESSED) {
                        object : AsyncTask<Void?, Void?, Void?>() {
                            protected fun doInBackground(vararg voids: Void): Void? {
                                //context.handleTriggerPress(true)
                                return null
                            }
                        }.execute()
                    }
                    if (rfidStatusEvents.StatusEventData.HandheldTriggerEventData.getHandheldEvent() === HANDHELD_TRIGGER_EVENT_TYPE.HANDHELD_TRIGGER_RELEASED) {
                        object : AsyncTask<Void?, Void?, Void?>() {
                            protected fun doInBackground(vararg voids: Void): Void? {
                                context.handleTriggerPress(false)
                                return null
                            }
                        }.execute()
                    }
                }
            }
        }



    }


    inner class AsyncDataUpdate: AsyncTask<TagData, Void, Void>() {
        override fun doInBackground(vararg params: TagData?): Void? {
            context.handleTagdata(params.get(0))
            return null
        }


    }

    internal interface ResponseHandlerInterface {
        fun handleTagdata(tagData: Array<TagData?>?)
        fun handleTriggerPress(pressed: Boolean) //void handleStatusEvents(Events.StatusEventData eventData);
    }*/

}