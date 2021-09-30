package tairov.baxti.shop.firms

import android.os.Build
import androidx.annotation.RequiresApi
import tairov.baxti.shop.MainConsts
import java.time.format.DateTimeFormatter

object FirmsConsts {
    const val FIRMS: String = "firms"
    const val FIRM_NAME: String = "name"
    const val FIRM_PAY: String = "pay"
    const val FIRM_DEBT: String = "debt"
    const val FIRM_DETAIL: String = "firmDetail"
    @RequiresApi(Build.VERSION_CODES.O)
    val DATE_TIME_FORMATTER: DateTimeFormatter = DateTimeFormatter.ofPattern(
        MainConsts.FORMAT_PATTERN
    )
}