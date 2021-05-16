package com.mertrizakaradeniz.exploregame.utils

import android.app.Activity
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.view.inputmethod.InputMethodManager

class Utils {

    companion object {

        fun hasInternetConnection(context: Context): Boolean {
            val connectivityManager =
                context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                val activeNetwork = connectivityManager.activeNetwork ?: return false
                val capabilities =
                    connectivityManager.getNetworkCapabilities(activeNetwork) ?: return false
                return when {
                    capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
                    capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
                    capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
                    else -> false
                }
            } else {
                connectivityManager.activeNetworkInfo?.run {
                    return when (type) {
                        ConnectivityManager.TYPE_WIFI -> true
                        ConnectivityManager.TYPE_MOBILE -> true
                        ConnectivityManager.TYPE_ETHERNET -> true
                        else -> false
                    }
                }
            }
            return false
        }

        fun hideKeyboard(activity: Activity) {
            val inputMethodManager =
                activity.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager

            val currentFocusedView = activity.currentFocus
            currentFocusedView?.let {
                inputMethodManager.hideSoftInputFromWindow(
                    currentFocusedView.windowToken, InputMethodManager.HIDE_NOT_ALWAYS
                )
            }
        }
    }

}