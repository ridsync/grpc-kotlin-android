package com.oklab.grpc.service

/**
 * Created by okwon on 5/17/21.
 * Description : DomainLayer Data Model
 */
sealed class DomainDTO<out T : Any> {
    data class Success<out T : Any>(val data: T, val message: String = "") : DomainDTO<T>()
    data class Failure(val code: Int = -1, val message: String = "", val throwable: Throwable?) : DomainDTO<Nothing>()
}