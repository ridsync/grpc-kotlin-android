package com.oklab.grpc.service.option

import android.util.Log
import io.grpc.*
import io.grpc.ForwardingClientCall.SimpleForwardingClientCall
import io.grpc.ForwardingClientCallListener.SimpleForwardingClientCallListener


/**
 * Created by okwon on 5/10/21.
 * Description :
 */
class AuthTokenInterceptor : ClientInterceptor {
    override fun <ReqT, RespT> interceptCall(
        methodDescriptor: MethodDescriptor<ReqT, RespT>?,
        callOptions: CallOptions?,
        channel: Channel
    ): ClientCall<ReqT, RespT> {
        return object : SimpleForwardingClientCall<ReqT, RespT>(
            channel.newCall(
                methodDescriptor,
                callOptions
            )
        ) {
            override fun start(responseListener: Listener<RespT>?, headers: Metadata) {
                headers.put(
                    Metadata.Key.of("Authorization", Metadata.ASCII_STRING_MARSHALLER),
                    "valid_token"
                )
                headers.put(
                    Metadata.Key.of("lang", Metadata.ASCII_STRING_MARSHALLER),
                    "ko"
                )
                headers.put(Metadata.Key.of("os", Metadata.ASCII_STRING_MARSHALLER), "android")
                super.start(object : SimpleForwardingClientCallListener<RespT>(responseListener) {
                    override fun onHeaders(headers: Metadata) {
                        Log.d("TAG","Response Headers : $headers")
                        super.onHeaders(headers)
                    }

                    override fun onClose(status: Status?, trailers: Metadata?) {
                        Log.d("TAG","onClose status : $status / trailers : $trailers")
                        super.onClose(status, trailers)
                    }

                    override fun onMessage(message: RespT) {
                        super.onMessage(message)
                    }

                    override fun onReady() {
                        Log.d("TAG","onReady ")
                        super.onReady()
                    }
                }, headers)
            }
        }
    }
}