package kz.dvij.dvij_compose3.login

import com.google.android.gms.common.api.Status

data class LoadingState private constructor(val status: Status, val msg: String? = null ) {
    enum class Status {
        RUNNING,
        SUCCESS,
        FAILED,
        IDLE
    }

    companion object {
        val LOADED = LoadingState(Status.SUCCESS)
        val IDLE = LoadingState(Status.IDLE)
        val LOADING = LoadingState(Status.RUNNING)
       fun error(msg: String?) = LoadingState(Status.FAILED, msg)
    }
}