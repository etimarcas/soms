package com.ricardo.soms

import com.zebra.rfid.api3.*

class RFIDHandler {

    companion object {

        private val readers: Readers? = null

        lateinit var reader: RFIDReader

        private lateinit var events: RfidEventsListener

        private lateinit var antenna: Antennas.AntennaRfConfig


        private lateinit var device: ReaderDevice


        private lateinit var availableReaders: ArrayList<ReaderDevice>







    }

}