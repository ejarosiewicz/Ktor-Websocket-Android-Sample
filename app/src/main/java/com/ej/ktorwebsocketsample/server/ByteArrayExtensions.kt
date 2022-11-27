package com.ej.ktorwebsocketsample.server

/**
 * Example: [10,20,30,40,0,0,0] => [10,20,30,40] => StringOf([10,20,30,40])
 */
fun ByteArray.formatToString() = String(takeWhile { it.compareTo(0) != 0 }.toByteArray())