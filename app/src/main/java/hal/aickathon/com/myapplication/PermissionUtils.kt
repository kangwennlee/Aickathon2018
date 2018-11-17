package hal.aickathon.com.myapplication

import android.app.Activity
import android.content.pm.PackageManager
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import java.util.*

/**
 * Created by Kangwenn on 20/3/2018.
 */

object PermissionUtils {
    fun requestPermission(
        activity: Activity, requestCode: Int, vararg permissions: String
    ): Boolean {
        var granted = true
        val permissionsNeeded = ArrayList<String>()

        for (s in permissions) {
            val permissionCheck = ContextCompat.checkSelfPermission(activity, s)
            val hasPermission = permissionCheck == PackageManager.PERMISSION_GRANTED
            granted = granted and hasPermission
            if (!hasPermission) {
                permissionsNeeded.add(s)
            }
        }

        if (granted) {
            return true
        } else {
            ActivityCompat.requestPermissions(
                activity,
                permissionsNeeded.toTypedArray(),
                requestCode
            )
            return false
        }
    }


    fun permissionGranted(
        requestCode: Int, permissionCode: Int, grantResults: IntArray
    ): Boolean {
        return requestCode == permissionCode && grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED
    }
}