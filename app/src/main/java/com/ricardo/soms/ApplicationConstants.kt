package com.ricardo.soms

class ApplicationConstants {

    companion object {

        /**
         * The minimum signal strength a tag must return to be considered valid when interrogated by
         * a reader. The lower the signal strength, the exponentially worse that tag's signal. Critically
         * low signal strengths can be indicative of cross reads and should be filtered out immediately. Readers
         * can on occasion read through walls, over great distances, and across conductive surfaces. The easiest
         * method of negating this lack of discrimination is by validating received signal strengths from each tag.
         * The minimum accepted value may vary by application and facility.
         */
        const val MINIMUM_RSSI_VALUE: Short = -70

        /**
         * The minimum measurement for the amount of power transmitted from the reader to the RFID antenna.
         * This power is measured in decibels-milliwatts.
         */
        const val MINIMUM_TRANSMIT_POWER_INDEX: Int = 0

        /**
         * The maximum measurement for the amount of power transmitted from the reader to the RFID antenna.
         * This power is measured in decibels-milliwatts.
         */
        const val MAXIMUM_TRANSMIT_POWER_INDEX: Int = 300

        /**
         * The expected tag population in the field of view of the antenna.
         */
        const val TAG_POPULATION: Short = 100

        /**
         * How often an audio indicator can be played while performing inventory. Prevents overload in
         * high volume areas. Measured in milliseconds.
         */
        const val SCAN_TAGS_AUDIO_DELAY: Short = 30
    }

}