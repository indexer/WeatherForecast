package com.indexer.weather

import android.arch.lifecycle.ViewModelProviders
import android.arch.persistence.room.Room
import android.support.test.InstrumentationRegistry
import android.support.test.filters.LargeTest
import android.support.test.rule.ActivityTestRule
import android.support.test.runner.AndroidJUnit4
import android.test.InstrumentationTestCase
import com.indexer.weather.database.AppDatabase
import com.indexer.weather.viewmodel.HomeGridViewModel
import com.squareup.okhttp.mockwebserver.MockWebServer
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@LargeTest
class HomeActivityTest : InstrumentationTestCase() {
  private var server: MockWebServer? = null
  private var mDb: AppDatabase? = null

  @get:Rule
  var mActivityRule: ActivityTestRule<HomeActivity> = ActivityTestRule(HomeActivity::class.java)

  private val homeViewModel: HomeGridViewModel
    get() {
      val activity = mActivityRule.activity
      return ViewModelProviders.of(activity)
          .get(HomeGridViewModel::class.java)
    }

  @Before
  @Throws(Exception::class)
  public override fun setUp() {
    super.setUp()
    server = MockWebServer()
    server!!.start()
    val context = InstrumentationRegistry.getTargetContext()
    mDb = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java)
        .build()
    injectInstrumentation(InstrumentationRegistry.getInstrumentation())
  }

  @Test
  fun readDatabase() {
    val products = homeViewModel.saveWeatherInformation("Singapore", this!!.mDb!!)
    Assert.assertNotNull(products)
  }

}