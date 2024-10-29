package utils

import java.util.Base64

object StringUtilities {
  def hexStringToByteArray(hex: String): Array[Byte] = {
    val len = hex.length
    val byteArray = new Array[Byte](len / 2)
    for (i <- 0 until len by 2) {
      byteArray(i / 2) = ((Character.digit(hex.charAt(i), 16) << 4) + Character.digit(hex.charAt(i + 1), 16)).toByte
    }
    byteArray
  }
  
  def convertBytesToBase64(bytes: Array[Byte]): String = {
    Base64.getEncoder.encodeToString(bytes)
  }
}
