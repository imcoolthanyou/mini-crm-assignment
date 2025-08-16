package gautam.projects.crm_assignment.core.utils

import android.content.Context
import android.net.ConnectivityManager

@Suppress("DEPRECATION")
fun Context.isOnline(): Boolean{

    val cm = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    return cm.activeNetworkInfo?.isConnectedOrConnecting == true

}