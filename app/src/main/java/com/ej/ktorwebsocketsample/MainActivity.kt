package com.ej.ktorwebsocketsample

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.ej.ktorwebsocketsample.databinding.ActivityMainBinding
import com.ej.ktorwebsocketsample.server.KtorClient
import com.ej.ktorwebsocketsample.server.KtorServer
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.net.Inet4Address
import java.net.InetAddress
import java.net.NetworkInterface
import java.util.Collections

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private lateinit var ktorClient: KtorClient
    private lateinit var ktorServer: KtorServer

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ktorClient = KtorClient(
            onReceiveListener = {
                binding.consoleClient.text = it
            }
        )
        ktorServer = KtorServer()

        binding.connect.setOnClickListener {
            GlobalScope.launch {
                ktorClient.start(hostname = binding.address.text.toString())
            }
        }

        binding.send.setOnClickListener {
            GlobalScope.launch {
                ktorClient.sendResponse(binding.textToSend.text.toString())
            }
        }
    }


    override fun onResume() {
        super.onResume()
        val ipAddres = getIPAddress()
        binding.consoleServer.text = ipAddres
        GlobalScope.launch {
            ktorServer.start(hostname = ipAddres){
                binding.consoleServer.text  = "${binding.consoleServer.text}\n$it"
            }
        }
    }

    fun getIPAddress(): String {
        try {
            val interfaces: List<NetworkInterface> = Collections.list(NetworkInterface.getNetworkInterfaces())
            for (networkInterface in interfaces) {
                val inetAddresses: List<InetAddress> =
                    Collections.list(networkInterface.inetAddresses)
                for (inetAddress in inetAddresses) {
                    if (!inetAddress.isLoopbackAddress && inetAddress is Inet4Address) {
                        return inetAddress.hostAddress
                    }
                }
            }
        } catch (ignored: Exception) {
            Log.e("XXX", "No ip address")
        }
        return ""
    }
}