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
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.single
import kotlinx.coroutines.launch

class HomeGrpcTestViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "This is home Fragment"
    }
    val text: LiveData<String> = _text

    private val _dataProgress = MutableLiveData<Boolean>().apply {
        value = false
    }
    val dataProgress: LiveData<Boolean> = _dataProgress


    fun testRpcCall(context: Context) {
        val grpcService = TestGrpcService(context)
        val rpcCallProcess = RpcCallResponse(grpcService)

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

            when(result) {
                is DomainDTO.Success -> {
                    _text.value = result.data.accessToken
                    Log.d("TAG","gRPC Success !! AccessToken = result.data.accessToken")
                }
                is DomainDTO.Failure -> {
                    result.code
                    result.message
                    Log.d("TAG","gRPC Failure !! code: ${result.code} / Message: ${result.message}")
                }
            }
        }
    }
}