package com.oklab.grpc.service.option

import android.util.Log
import com.oklab.grpc.service.DomainDTO
import com.oklab.grpc.service.TestGrpcService
import io.grpc.Metadata
import io.grpc.Status
import io.grpc.StatusException
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*


/**
 * Created by okwon on 2021/05/26.
 * Description : RemoteDataSource Response Common Processing
 */
open class RpcCallResponse (private val rpcService: TestGrpcService) {

    suspend fun <T : Any> handleCallResultAsFlow(
        dispatcherIO: CoroutineDispatcher = Dispatchers.IO,
        rpcCall: suspend () -> T
    ) = flow {

        val result = try {
            DomainDTO.Success(rpcCall())
        } catch (ex: Exception) {
            Log.e("TAG","rpcCall Exception $ex")
            when (ex) {
                is StatusException -> {
                    when (ex.status.code) {
                        Status.Code.UNAUTHENTICATED -> {
                            Log.e("TAG","StatusException UNAUTHENTICATED ${ex.cause}")
                            val trailers = ex.trailers
                            val code = trailers[Metadata.Key.of("code", Metadata.ASCII_STRING_MARSHALLER)]
                            if(code == "expire refresh_token"){
                                DomainDTO.Failure(throwable = ex)
                            } else {
                                throw ex
                            }
                        }
                        Status.Code.UNAVAILABLE -> {
                        }
                        Status.Code.DATA_LOSS -> {
                            Log.e("TAG","StatusException DATA_LOSS trailers = ${ex.trailers}")
                        }
                        else -> { }
                    }
                    DomainDTO.Failure(throwable = ex)
                }
                else -> DomainDTO.Failure(throwable = ex)
            }
        }
        this.emit(result)
    }.retryWhen { cause, attempt ->
        Log.e("TAG","retryWhen ${cause.cause} / attempt $attempt")
        if (attempt >= 2) {
            return@retryWhen false
        }
        when (cause) {
            is StatusException -> {
                when (cause.status.code) {
                    Status.Code.UNAUTHENTICATED -> {
                        // TODO JWT 엑세스 토큰 갱신
                        val trailers = cause.trailers
                        val code = trailers[Metadata.Key.of("code", Metadata.ASCII_STRING_MARSHALLER)]
                        if(code == "expire access_token"){
                            //val newJWToken = rpcService.getAuthStub().getNewJWToken()
//                    rpcService.setNewJWToken(newJWToken) // try catch??
                            return@retryWhen true
                        } else {
                            return@retryWhen false
                        }
                    }
                    else -> {
                        delay(1000)
                        return@retryWhen false
                    }
                }
            }
            else -> {
                delay(1000)
                return@retryWhen true
            }
        }
    }.catch {
        emit(DomainDTO.Failure(throwable = it.cause))
    }.flowOn(dispatcherIO)

}