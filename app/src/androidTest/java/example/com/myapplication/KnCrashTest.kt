package example.com.myapplication

import android.content.Context
import androidx.sqlite.db.SupportSQLiteDatabase
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import io.requery.android.database.sqlite.SQLiteCustomExtension
import io.requery.android.database.sqlite.SQLiteDatabase
import io.requery.android.database.sqlite.SQLiteDatabaseConfiguration
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import kotlin.concurrent.thread

@RunWith(AndroidJUnit4::class)
class KnCrashTest {

  private fun db(): SupportSQLiteDatabase {
    val ctx: Context = ApplicationProvider.getApplicationContext()
    val file = ctx.filesDir.resolve("sqlitekn.db")
    val sqlitekn_extension = SQLiteCustomExtension("libsqlitekn", null)
    val config = SQLiteDatabaseConfiguration(file.path, SQLiteDatabase.CREATE_IF_NECESSARY , emptyList(), emptyList(), listOf(sqlitekn_extension))
    return SQLiteDatabase.openDatabase(config, null, null)
  }

  @Test
  fun select1() {
    db().use { db ->
      val result = db.query("SELECT 1").use { c -> c.moveToFirst();c.getInt(0) }
      assertEquals(1, result)
    }
  }

  @Test
  fun RETURN_STRING() {
    val db = db()
    db.query("SELECT RETURN_STRING('Hello World!')").use { c -> c.moveToFirst();c.getString(0) }
    thread {
      db.use { db ->
        val result = db.query("SELECT RETURN_STRING('Hello World!')").use { c -> c.moveToFirst();c.getString(0) }
        assertEquals("Hello World!", result)
      }
    }.join()
  }

  @Test
  fun DOUBLE_TO_STRING() {
    val db = db()
    val expect = "".padEnd(11, '0')
    assertEquals(expect, db.query("SELECT BUILD_STRING()").use { c -> c.moveToFirst();c.getString(0) })
    assertEquals(expect, db.query("SELECT BUILD_STRING()").use { c -> c.moveToFirst();c.getString(0) })
    assertEquals(expect, db.query("SELECT BUILD_STRING()").use { c -> c.moveToFirst();c.getString(0) })
    thread {
      db.use { db ->
        assertEquals(expect, db.query("SELECT BUILD_STRING()").use { c -> c.moveToFirst();c.getString(0) })
      }
    }.join()
  }
}