package tairov.baxti.shop

import android.annotation.SuppressLint
import java.text.SimpleDateFormat

object MainConsts {
    const val LOG_TAG: String = "MyLog"
    const val TOTALS: String = "totals"
    const val FROM_DATE: String = "fromDate"
    const val END_DATE: String = "endDate"
    const val REAL_TIME_DB_REF: String = "https://shop-15b50-default-rtdb.europe-west1.firebasedatabase.app"
    const val FORMAT_PATTERN: String = "dd-MM-yyyy"
    @SuppressLint("SimpleDateFormat")
    val SIMPLE_DATE_FORMAT = SimpleDateFormat(FORMAT_PATTERN)
}