package com.oklab.grpc.service

import android.content.Context
import com.oklab.grpc.R
import com.oklab.grpc.proto.AuthServiceGrpcKt
import io.grpc.ConnectivityState
import io.grpc.ManagedChannel
import io.grpc.android.AndroidChannelBuilder
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.asExecutor
import java.net.URL
import java.util.concurrent.TimeUnit
import io.grpc.okhttp.OkHttpChannelBuilder
import com.oklab.grpc.service.option.AuthCallCredentials


/**
 * Created by okwon on 4/30/21.
 * Description :
 */
class TestGrpcService (val application: Context) {

    private val channel: ManagedChannel by lazy {
        val url = URL(application.getString(R.string.server_url))
        val port = if (url.port == -1) url.defaultPort else url.port

        val okHttpBuilder = OkHttpChannelBuilder.forAddress(url.host, port)
//            .sslSocketFactory(getSSLSocketFactory()) // TODO
        val builder = AndroidChannelBuilder.usingBuilder(okHttpBuilder)
        if (url.protocol == "https") {
            builder.useTransportSecurity()
        } else {
            builder.usePlaintext()
        }
        builder.context(application)
        builder.enableRetry()
        builder.maxRetryAttempts(2)
        builder.userAgent("userAgent")
//        builder.connectionSpec(ConnectionSpec.COMPATIBLE_TLS)
        builder.executor(Dispatchers.Default.asExecutor()).build()
    }

    private val authStub by lazy {
        AuthServiceGrpcKt.AuthServiceCoroutineStub(channel)
//            .withInterceptors(AuthTokenInterceptor())
            .withCallCredentials(AuthCallCredentials("my auth token"))
    }


    @JvmName("getChannelJvm")
    fun getChannel(): ManagedChannel = channel

    @JvmName("getAuthStubJvm")
    fun getAuthStub(): AuthServiceGrpcKt.AuthServiceCoroutineStub = authStub.withDeadlineAfter(18, TimeUnit.SECONDS)

    /**
     *  RPC 서버 연결 종료!?
     */
    fun shutdownChannel() {
        channel.shutdownNow().awaitTermination(3, TimeUnit.SECONDS)
    }

    fun registryRpcStateCallback(state: ConnectivityState, callback: () -> Unit ){
        channel.notifyWhenStateChanged(state,callback)
    }

//    private fun getSSLSocketFactory(): SSLSocketFactory {
//
////        val serverCrtFile = application.resources.openRawResource(R.raw.grpc_ca)
////        val serverCertificate =
////            CertificateFactory.getInstance("X.509").generateCertificate(serverCrtFile)
//
//        val caKeyStore: KeyStore = KeyStore.getInstance(KeyStore.getDefaultType()).apply {
//            load(null, null)
//            setCertificateEntry("server", serverCertificate)
//        }
//
//        val trustManagerFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm()).apply {
//            init(caKeyStore)
//        }
//
//        val sslContext = SSLContext.getInstance("TLS").apply {
//            init(null, trustManagerFactory.trustManagers, null)
//        }
//        return sslContext.socketFactory
//    }

//        private fun getStubWithMetadata(): Metadata  {
//            val metadata = Metadata()
//            metadata.put(Metadata.Key.of("hl", Metadata.ASCII_STRING_MARSHALLER), "ko")
//            metadata.put(Metadata.Key.of("store", Metadata.ASCII_STRING_MARSHALLER), "google")
//            metadata.put(Metadata.Key.of("auth", Metadata.ASCII_STRING_MARSHALLER), "token")
//            return metadata
//        }

        /**
         * Test Rpc
         */
//        suspend fun testBiDirectionalStream() {
//            val request = RpcAuth.JoinRequest.newBuilder()
//                .setNickname("oktest")
//                .build()
////            val reply = stub.chat()
//        }
//
//        suspend fun testCoroutine(): RpcAuth.JoinResponse {
//            val request = RpcAuth.JoinRequest.newBuilder()
//                .setNickname("oktest")
//                .build()
//            return authStub.join(request)
            // Example
//        val searchReq = DtkService.MemberSearchRequest.newBuilder().apply {
//            hl = DtkService.LangType.KO
//            today = Timestamp.getDefaultInstance()
//        }
            // File Example
//        DtkService.UploadPhotoRequest.newBuilder().apply {
//            chunkData = ByteString.copyFrom(File("path").readBytes())
//        }

//        }
}