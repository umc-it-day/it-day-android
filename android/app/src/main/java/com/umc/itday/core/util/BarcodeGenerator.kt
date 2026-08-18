package com.umc.itday.core.util

import android.graphics.Bitmap
import android.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import com.google.zxing.BarcodeFormat
import com.google.zxing.MultiFormatWriter
import com.google.zxing.common.BitMatrix

object BarcodeGenerator {
    fun generateBarcode(
        content: String,
        width: Int = 600,
        height: Int = 180,
        format: BarcodeFormat = BarcodeFormat.CODE_128,
    ): ImageBitmap? {
        if (content.isBlank()) return null
        return try {
            val bitMatrix: BitMatrix = MultiFormatWriter().encode(content, format, width, height)
            val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
            for (x in 0 until width) {
                for (y in 0 until height) {
                    bitmap.setPixel(x, y, if (bitMatrix[x, y]) Color.BLACK else Color.TRANSPARENT)
                }
            }
            bitmap.asImageBitmap()
        } catch (e: Exception) {
            null
        }
    }
}
