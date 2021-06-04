package com.oklab.grpc.service.option

import io.grpc.CallCredentials
import io.grpc.Metadata
import io.grpc.Status
import java.util.concurrent.Executor

/**
 * Created by okwon on 5/11/21.
 * Description :
 */

class AuthCallCredentials(private val token: String) : CallCredentials() {

    var META_DATA_KEY: Metadata.Key<String?>? =
        Metadata.Key.of("Authorization", Metadata.ASCII_STRING_MARSHALLER)

    override fun applyRequestMetadata(
        requestInfo: RequestInfo?,
        executor: Executor,
        metadataApplier: MetadataApplier
    ) {
        executor.execute {
            try {
                val headers = Metadata()
                headers.put(META_DATA_KEY, "Bearer $token")
                headers.put(Metadata.Key.of("lang", Metadata.ASCII_STRING_MARSHALLER), "TODO")
                headers.put(Metadata.Key.of("device_type", Metadata.ASCII_STRING_MARSHALLER), "google")
                headers.put(Metadata.Key.of("user_agent", Metadata.ASCII_STRING_MARSHALLER), "TODO")
                metadataApplier.apply(headers)
            } catch (e: Throwable) {
                metadataApplier.fail(Status.UNAUTHENTICATED.withCause(e))
            }
        }
    }

    override fun thisUsesUnstableApi() {
        // yes this is unstable :(
    }
}