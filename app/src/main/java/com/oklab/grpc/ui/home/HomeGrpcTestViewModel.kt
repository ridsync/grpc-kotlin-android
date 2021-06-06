package com.oklab.grpc.ui.home

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.oklab.grpc.proto.RpcAuth
import com.oklab.grpc.service.DomainDTO
import com.oklab.grpc.service.TestGrpcService
import com.oklab.grpc.service.option.RpcCallResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class HomeGrpcTestViewModel(context: Context) : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "This is home Fragment"
    }
    val text: LiveData<String> = _text

    private val _dataProgress = MutableLiveData<Boolean>().apply {
        value = false
    }
    val dataProgress: LiveData<Boolean> = _dataProgress
    val grpcService = TestGrpcService(context)
    val rpcCallProcess = RpcCallResponse(grpcService)

    fun testRpcCall() {

        viewModelScope.launch(Dispatchers.IO){
            val request =  RpcAuth.JoinRequest.newBuilder().apply {
                nickname = "GRPC Tester"
            }.build()

            val result = rpcCallProcess.handleCallResultAsFlow(Dispatchers.IO) {
                grpcService.getAuthStub().join(request)
                }.flowOn(Dispatchers.IO)
                .onStart { _dataProgress.value = true }
                .onCompletion { _dataProgress.value = false  }
                .single()

            withContext(Dispatchers.Main) {
                when (result) {
                    is DomainDTO.Success -> {
                        _text.value = result.data.accessToken
                        Log.d("TAG", "gRPC Success !! AccessToken = result.data.accessToken")
                    }
                    is DomainDTO.Failure -> {
                        result.code
                        result.message
                        Log.d(
                            "TAG",
                            "gRPC Failure !! code: ${result.code} / Message: ${result.message}"
                        )
                    }
                }
            }
        }
    }

    private val msFlowChat = MutableStateFlow(RpcAuth.Message.newBuilder().build())

    fun sendMessageOnFlow() {
        msFlowChat.value = RpcAuth.Message.newBuilder().apply {
            message = "GRPC Message Hello !!"
            msgType = "TEXT"
        }.build()
    }

    fun startRpcCallStream() {
        viewModelScope.launch(Dispatchers.IO){
            val result = rpcCallProcess.handleCallResultAsFlow(Dispatchers.IO) {
                grpcService.getAuthStub().chat(msFlowChat)
            }
            .flowOn(Dispatchers.IO)
            .collect { recvMsg ->
                // TODO 메세지 수신.
                withContext(Dispatchers.Main) {
                    recvMsg
                }
            }
        }

    }

}