@file:Suppress("UNUSED_PARAMETER")

package sqlite.kn

import cnames.structs.sqlite3
import cnames.structs.sqlite3_context
import cnames.structs.sqlite3_value
import kotlinx.cinterop.*
import platform.android.ANDROID_LOG_INFO
import platform.android.__android_log_print
import sqlite3.*

@CName("sqlite3_sqlitekn_init")
fun init(
  db: CPointer<sqlite3>,
  pzErrMsg: CPointer<CPointerVar<ByteVar>>,
  pApi: CPointer<sqlite3_api_routines>
): Int {
  SQLITE_EXTENSION_INIT2_(pApi)

  sqlite3_create_function(db, "RETURN_STRING",    1, SQLITE_UTF8, null, staticCFunction(::RETURN_STRING), null, null)
  sqlite3_create_function(db, "BUILD_STRING",     0, SQLITE_UTF8, null, staticCFunction(::BUILD_STRING), null, null)

  __android_log_print(ANDROID_LOG_INFO.toInt(), "sqlitekn", "sqlite3_sqlitekn_init loaded: sqlite3_version=${sqlite3_libversion()?.toKString()}")
  return 0
}

internal fun RETURN_STRING(context: CPointer<sqlite3_context>?, argc: Int, argv: CPointer<CPointerVar<sqlite3_value>>?) {
  val value: String? = sqlite3_value_text(argv?.get(0))?.reinterpret<ByteVar>()?.toKString()

  if (value != null) {
    sqlite3_result_text (context, value, -1, SQLITE_TRANSIENT)
  } else {
    sqlite3_result_null(context)
  }
}

internal fun BUILD_STRING(context: CPointer<sqlite3_context>?, argc: Int, argv: CPointer<CPointerVar<sqlite3_value>>?) {
  val str = buildString { repeat(11) { append('0') } }
  sqlite3_result_text(context, str, -1, SQLITE_TRANSIENT)
}