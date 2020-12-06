package com.example.testuhfapi

import android.annotation.SuppressLint
import android.content.Context
import android.media.AudioManager
import android.media.SoundPool
import android.util.Log
import com.example.z_android_sdk.Reader
import uhf.api.*

/**
 * sanray PDA
 * @author kongfei
 */
object SanrayUHF {

    private const val DEV_MODE = "P6070"

    var onScanCallback: ((String?) -> Unit)? = null
    var isScan: Boolean = false
        private set
    private var count = 0
    private val soundThread: SoundThread by lazy { SoundThread() }
    private var reader: Reader? = null
        get() {
            if (field == null) {
                field = Reader(DEV_MODE).apply {
                    SetCallBack(object : MultiLableCallBack {
                        override fun method(data: ByteArray?) {
                            handleData(data)
                        }

                        override fun RecvActiveTag(p0: ActiveTag?) {
                        }

                        override fun BlueToothBtn() {
                        }

                        override fun BlueToothVoltage(p0: Int) {
                        }

                        override fun CmdRespond(result: Array<out String>?) {
                        }
                    })
                }
            }
            return field
        }

    private val context: Context by lazy {
        @SuppressLint("PrivateApi")
        val activityThread = Class.forName("android.app.ActivityThread")
        val thread = activityThread.getMethod("currentActivityThread").invoke(null as Any?)
        val app = activityThread.getMethod("getApplication").invoke(thread)
        app as Context
    }

    private val sound: Sound by lazy { Sound(context) }
    private val soundPool: SoundPool by lazy {
        SoundPool(10, AudioManager.STREAM_SYSTEM, 0)
                .apply { load(context, R.raw.duka3, 1) }
    }

    private fun handleData(data: ByteArray?) {
        if (!isScan || data == null || data.isEmpty()) return

        val msb = data[0]
        val lsb = data[1]
        val pc: Int = ((msb.toInt() and 0x00ff) shl 8 or (lsb.toInt() and 0x00ff)) and 0xf800 shr 11
        val array = data.sliceArray(IntRange(2, pc * 2 + 1))
        val value = ShareData.CharToString(array, array.size).replace(" ", "")

        count++
        onScanCallback?.invoke(value)
    }

    fun start() {
        Log.e("test", "¿ªÆôÉ¨Ãè")
        reader?.UHF_CMD(CommandType.MULTI_QUERY_TAGS_EPC,
                Multi_query_epc().apply { query_total = 0 })
        isScan = true
        count = 0
        soundThread.start()
    }

    fun stop() {
        Log.e("test", "Í£Ö¹É¨Ãè")
        isScan = false
        count = 0
        reader?.UHF_CMD(CommandType.STOP_MULTI_QUERY_TAGS_EPC, null)
    }

    fun close() {
        if (isScan) stop()
        onScanCallback = null
        soundThread.stop()
        reader?.CloseSerialPort()
        reader = null
        Log.e("test", "ÊÍ·Å×ÊÔ´")
    }

    //²¥·ÅÉùÒôÏß³Ì
    private class SoundThread : Runnable {

        @Volatile
        private var mThread: Thread? = null

        fun start() {
            mThread = Thread(this)
            mThread?.start()
        }

        fun stop() {
            mThread = null
        }

        override fun run() {
            val thisThread = Thread.currentThread()
            while (mThread === thisThread && isScan) {
                try {
                    if (count > 10) count = 10
                    soundPool.play(1, 1f, 1f, 1, 0, 1f)
                    count--
                    Thread.sleep(100)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }
}