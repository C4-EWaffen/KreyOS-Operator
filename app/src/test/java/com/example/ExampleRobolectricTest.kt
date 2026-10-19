package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.domain.MaterialCategorizer
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

  @Test
  fun `read string from context`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val appName = context.getString(R.string.app_name)
    assertEquals("KreyOS", appName)
  }

  @Test
  fun `verify underground copper calculation`() {
    val (weight, value) = MaterialCategorizer.calculateUndergroundCopper(100.0, 5.2)
    assertEquals(520.0, weight, 0.01)
    // 520 kg * 4.30 €/kg = 2236.0 €
    assertEquals(2236.0, value, 0.01)
  }
}
