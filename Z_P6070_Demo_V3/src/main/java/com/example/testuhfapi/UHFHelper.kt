package com.example.testuhfapi

import android.annotation.SuppressLint
import android.content.Context
import android.media.AudioManager
import android.media.SoundPool
import com.example.z_android_sdk.Reader
import uhf.api.*
import kotlin.experimental.inv

/**
 *
 * @author kongfei
 */
object UHFHelper {


    private const val DEV_MODE = "P6070"

    private const val BUFF_SIZE = 10000
    private val tag_str_tmp = arrayOfNulls<String>(BUFF_SIZE)
    private val tag_str_rssi = arrayOfNulls<String>(BUFF_SIZE)
    private val tag_str_tid = arrayOfNulls<String>(BUFF_SIZE)
    private val tag_str_temp = arrayOfNulls<String>(BUFF_SIZE)
    var tag_str_tmp_write = 0
    var tag_str_tmp_Read = 0
    var tagsTimes_count = 0
    var newtagFlag = 0

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

    private val reader: Reader by lazy {
        Reader(DEV_MODE).apply {
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
                    if (result?.get(1) == "F8") { // LTU31���¶�
                        tagsTimes_count++
                        tag_str_tmp[tag_str_tmp_write] = result[2]
                        tag_str_rssi[tag_str_tmp_write] = result[3]
                        tag_str_temp[tag_str_tmp_write] = result[4]
                        tag_str_tid[tag_str_tmp_write] = "[TID]"
                        tag_str_tmp_write++
                        if (tag_str_tmp_write >= BUFF_SIZE) tag_str_tmp_write = 0
                        playSound()
                    }
                }
            })
        }
    }

    private fun handleData(data: ByteArray?) {
        if (data == null || data.isEmpty()) return
        val msb = data[0]
        val lsb = data[1]
        val pc: Int = ((msb.toInt() and 0x00ff) shl 8 or (lsb.toInt() and 0x00ff)) and 0xf800 shr 11

        val tmp = ByteArray(pc * 2)
        System.arraycopy(data, 2, tmp, 0, tmp.size)
        val tmpStr = ShareData.CharToString(tmp, tmp.size).replace(" ", "")

        val tid = ByteArray(data.size - 2 - pc * 2 - 3)
        System.arraycopy(data, 2 + pc * 2, tid, 0, tid.size)
        val tidStr = ShareData.CharToString(tid, tid.size).replace(" ", "")

        val rssiStr = "" + ((data[2 + pc * 2 + tid.size].toInt() and 0xFF shl 8
                or (data[2 + pc * 2 + 1 + tid.size].toInt() and 0xFF) - 1).toShort()).inv() / -10.0

        tag_str_tmp[tag_str_tmp_write] = tmpStr
        tag_str_rssi[tag_str_tmp_write] = rssiStr
        tag_str_temp[tag_str_tmp_write] = "��"


        if (tidStr.isEmpty()) {
            tag_str_tid[tag_str_tmp_write] = "[TID]"
        } else {
            tag_str_tid[tag_str_tmp_write] = tidStr
        }

        tag_str_tmp_write++
        if (tag_str_tmp_write >= BUFF_SIZE) tag_str_tmp_write = 0

        playSound()
    }

    private fun playSound() {

    }

    fun start() {
        val flag = reader.UHF_CMD(CommandType.MULTI_QUERY_TAGS_EPC,
                Multi_query_epc().apply { query_total = 1 })

    }

    fun stop() {
        val flag = reader.UHF_CMD(CommandType.STOP_MULTI_QUERY_TAGS_EPC, null)
    }

    fun close() {
        reader.CloseSerialPort()
    }

}