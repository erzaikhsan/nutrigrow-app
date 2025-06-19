package com.project.labs.nutrigrow.utils

import android.content.Context
import android.util.Log
import org.json.JSONObject
import org.tensorflow.lite.Interpreter
import java.nio.MappedByteBuffer
import java.nio.channels.FileChannel

class NutritionModel(context: Context) {
    private var interpreter: Interpreter

    init {
        val modelFile = loadModelFile(context, "model_status_gizi.tflite")
        interpreter = Interpreter(modelFile)
    }

    private fun loadModelFile(context: Context, modelFileName: String): MappedByteBuffer {
        val assetFileDescriptor = context.assets.openFd(modelFileName)
        val fileInputStream = assetFileDescriptor.createInputStream()
        val fileChannel = fileInputStream.channel
        return fileChannel.map(
            FileChannel.MapMode.READ_ONLY,
            assetFileDescriptor.startOffset,
            assetFileDescriptor.declaredLength
        )
    }

    fun predict(context: Context, inputData: FloatArray): FloatArray {

        val (dataMin, dataMax) = loadScalerParams(context)

        // Normalize input data
        val normalizedInput = normalizeInput(inputData, dataMin, dataMax)

        // Prepare output array
        val output = Array(1) { FloatArray(4) }

        // Run inference
        interpreter.run(arrayOf(normalizedInput), output)

        return output[0]
    }

    private fun loadScalerParams(context: Context): Pair<FloatArray, FloatArray> {
        val inputStream = context.assets.open("scaler_params.json")
        val json = inputStream.bufferedReader().use { it.readText() }
        val jsonObject = JSONObject(json)

        val dataMin = jsonObject.getJSONArray("data_min")
        val dataMax = jsonObject.getJSONArray("data_max")

        val dataMinArray = FloatArray(dataMin.length()) { dataMin.getDouble(it).toFloat() }
        val dataMaxArray = FloatArray(dataMax.length()) { dataMax.getDouble(it).toFloat() }

        return Pair(dataMinArray, dataMaxArray)
    }

    private fun normalizeInput(inputData: FloatArray, dataMin: FloatArray, dataMax: FloatArray): FloatArray {
        val normalizedData = FloatArray(inputData.size)
        for (i in inputData.indices) {
            normalizedData[i] = (inputData[i] - dataMin[i]) / (dataMax[i] - dataMin[i])
        }
        return normalizedData
    }
}