package com.indexer.ottohub.rest

import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

/**
 * Created by indexer on 12/11/17.
 */
fun <T> Call<T>.enqueue(success: (response: Response<T>) -> Unit,
                        failure: (t: Throwable) -> Unit) {
    enqueue(object : Callback<T> {
        override fun onResponse(call: Call<T>?, response: Response<T>) = success(response)

        override fun onFailure(call: Call<T>?, t: Throwable) = failure(t)
    })
}