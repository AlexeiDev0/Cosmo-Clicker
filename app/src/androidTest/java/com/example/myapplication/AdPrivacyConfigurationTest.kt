package com.example.myapplication

import android.Manifest
import android.content.pm.PackageManager
import androidx.test.core.app.ActivityScenario
import androidx.test.platform.app.InstrumentationRegistry
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Test

class AdPrivacyConfigurationTest {
    @Test fun adRequestsStayIdleBeforeSeparatePermissionAndAfterStop() {
        ActivityScenario.launch(MainActivity::class.java).use { scenario ->
            scenario.onActivity { activity ->
                val ads = RewardedAdsController(activity)
                try {
                    ads.prepare()
                    assertFalse(ads.requestsAllowed.value)
                    assertEquals(RewardedAdStatus.IDLE, ads.status.value)
                    ads.stopRequests()
                    ads.prepare()
                    assertEquals(RewardedAdStatus.IDLE, ads.status.value)
                } finally {
                    ads.close()
                }
            }
        }
    }

    @Test fun advertisingAndSensitivePermissionsAreAbsentFromInstalledPackage() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val permissions = listOf(
            "com.google.android.gms.permission.AD_ID",
            "android.permission.ACCESS_ADSERVICES_AD_ID",
            "android.permission.ACCESS_ADSERVICES_ATTRIBUTION",
            "android.permission.ACCESS_ADSERVICES_TOPICS",
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.RECORD_AUDIO,
            Manifest.permission.READ_CONTACTS
        )
        permissions.forEach { permission ->
            assertEquals(permission, PackageManager.PERMISSION_DENIED,
                context.packageManager.checkPermission(permission, context.packageName))
        }
    }
}
