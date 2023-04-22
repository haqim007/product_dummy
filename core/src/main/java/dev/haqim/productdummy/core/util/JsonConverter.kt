package dev.haqim.productdummy.core.util

import java.io.BufferedReader
import java.io.FileInputStream
import java.io.InputStreamReader


object JsonConverter {
    const val ASSET_BASE_PATH = "../core/src/main/assets/"
    
    fun readJsonFile(filename: String): String {
        val br = BufferedReader(InputStreamReader(FileInputStream(ASSET_BASE_PATH + filename)))
        val sb = StringBuilder()
        var line = br.readLine()
        while (line != null) {
            sb.append(line)
            line = br.readLine()
        }
        return sb.toString()
    }
}