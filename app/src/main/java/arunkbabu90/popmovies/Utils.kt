@file:JvmName("Utils")
package arunkbabu90.popmovies

import android.app.Activity
import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.view.View
import android.view.animation.AnimationUtils
import android.view.animation.LayoutAnimationController
import android.view.inputmethod.InputMethodManager
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import arunkbabu90.popmovies.data.api.POSTER_BASE_URL
import arunkbabu90.popmovies.data.api.YOUTUBE_THUMB_BASE_URL
import arunkbabu90.popmovies.data.api.YOUTUBE_VIDEO_BASE_URL
import java.text.SimpleDateFormat
import java.util.*

/**
 * App lvl variable used to indicate that the User is currently in VerificationEmailFragment
 */
@JvmField
var inVerificationEmailFragment: Boolean = false

/**
 * Intelligently calculates the number of grid columns to be displayed on screen with respect to
 * the available screen size
 * @param context The Application Context
 * @return int  The number of columns to be displayed
 */
fun calculateNoOfColumns(context: Context?): Int {
    return if (context != null) {
        val displayMetrics = context.resources.displayMetrics
        val dpWidth = displayMetrics.widthPixels / displayMetrics.density
        val columnWidth = 115
        (dpWidth / columnWidth).toInt()
    } else {
        0
    }
}

/**
 * Hides the virtual keyboard from the activity
 * @param activity The current activity where the virtual keyboard exists
 */
fun closeSoftInput(activity: FragmentActivity?) {
    val inputMethodManager: InputMethodManager? = activity?.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
    var v: View? = activity.currentFocus
    if (v == null) v = View(activity)

    inputMethodManager?.hideSoftInputFromWindow(v.windowToken, 0)
}

/**
 * Check for internet availability
 * @return True: if internet is available; False otherwise
 */
fun isNetworkConnected(context: Context?): Boolean {
    if (context == null) return false

    val cm: ConnectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
        val network: Network = cm.activeNetwork ?: return false
        val nc: NetworkCapabilities = cm.getNetworkCapabilities(network) ?: return false

        return nc.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) ||
                nc.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) ||
                nc.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) ||
                nc.hasTransport(NetworkCapabilities.TRANSPORT_BLUETOOTH)
    } else {
        return cm.activeNetworkInfo?.isConnected ?: false
    }
}

/**
 * Checks whether the provided email is valid
 * @param email The email id to be verified
 * @return False if the email is valid
 */
fun verifyEmail(email: String): Boolean {
    val mailFormat = Regex("^([a-zA-B 0-9.-]+)@([a-zA-B 0-9]+)\\.([a-zA-Z]{2,8})(\\.[a-zA-Z]{2,8})?$")
    return !email.matches(mailFormat)
}

/**
 * Returns the full image URL from the image path endpoint
 * @param path The image path endpoint
 * @param size The size of the image
 *        One of [IMG_SIZE_MID, IMG_SIZE_LARGE, IMG_SIZE_ORIGINAL]
 */
fun getImageUrl(path: String, size: String): String = POSTER_BASE_URL + size + path

/**
 * Returns the full YouTube thumbnail URL from the video id
 * @param videoId The video's Id
 * @param size The size of the image
 *        One of [YT_IMG_SIZE_SMALL, YT_IMG_SIZE_MID, YT_IMG_SIZE_LARGE, YT_IMG_SIZE_ORIGINAL]
 */
fun getYouTubeThumbUrl(videoId: String, size: String): String = YOUTUBE_THUMB_BASE_URL + videoId + size

/**
 * Returns the full YouTube Video URL from the video id
 * @param videoId The video's Id
 */
fun getYouTubeVideoUrl(videoId: String): String = YOUTUBE_VIDEO_BASE_URL + videoId

/**
 * Returns the short date string from the given month number [Ex: Jan, Feb..]
 * @param month Int The month number. (must be in 1 - 12 range)
 */
fun getShortDateString(month: Int) =
    when (month) {
        1 -> "Jan"
        2 -> "Feb"
        3 -> "Mar"
        4 -> "Apr"
        5 -> "May"
        6 -> "Jun"
        7 -> "Jul"
        8 -> "Aug"
        9 -> "Sept"
        10 -> "Oct"
        11 -> "Nov"
        12 -> "Dec"
        else -> ""
    }

/**
 * Starts the Reveal Layout Animation on recycler view items
 * @param context The context
 * @param recyclerView The recyclerview to run the animation on
 * @param reverseAnimation Whether to reverse the animation effect.
 * If True then the animation will play in the reverse order
 */
fun runStackedRevealAnimation(context: Context, recyclerView: RecyclerView, reverseAnimation: Boolean) {
    val controller: LayoutAnimationController = if (reverseAnimation) {
        AnimationUtils.loadLayoutAnimation(context, R.anim.stacked_reveal_layout_animation_reverse)
    } else {
        AnimationUtils.loadLayoutAnimation(context, R.anim.stacked_reveal_layout_animation)
    }

    recyclerView.layoutAnimation = controller
    recyclerView.scheduleLayoutAnimation()
}

/**
 * Starts the Pull-Down Layout Animation on recycler view items
 * @param context The context
 * @param recyclerView The recyclerview to run the animation on
 */
fun runPullDownAnimation(context: Context, recyclerView: RecyclerView) {
    val controller = AnimationUtils.loadLayoutAnimation(context, R.anim.pull_down_layout_animation_reverse)
    recyclerView.layoutAnimation = controller
    recyclerView.scheduleLayoutAnimation()
}

/**
 * Converts the timestamp to a descriptive logical date string. Logical means the dates will
 * be like "Today", "Yesterday", "Tue, 19 Jun 1997" depending on the current date
 * @param timestamp The epoch timestamp to be converted
 * @return String: The date string (Ex: Tuesday, 10 June 1998)
 */
fun getLogicalDateString(timestamp: Long): String {
    val logicalDate: String
    val date = Date(timestamp)
    val currentDate = Date(System.currentTimeMillis())
    val sdf = SimpleDateFormat("EEE, dd MMM yyyy", Locale.getDefault())
    val df = SimpleDateFormat("dd", Locale.getDefault())
    val myf = SimpleDateFormat("MM yyyy", Locale.getDefault())
    sdf.timeZone = TimeZone.getDefault()

    val dateStr = sdf.format(date)
    val currentStr = sdf.format(currentDate)
    val day = df.format(date).toInt()
    val currDay = df.format(currentDate).toInt()
    val mnthYr = myf.format(date)
    val currMnthYr = myf.format(currentDate)

    logicalDate = if (dateStr == currentStr) {
        "Today"
    } else if (day == currDay - 1 && mnthYr == currMnthYr) {
        "Yesterday"
    } else {
        dateStr
    }

    return logicalDate
}

/**
 * Converts the timestamp to a descriptive logical short date string. Logical means the dates
 * will be like "Tue", "18 Mar" depending on the current date
 * @param timestamp The epoch timestamp to be converted
 * @return String: The date string
 */
fun getLogicalShortDate(timestamp: Long): String {
    val date = Date(timestamp)
    val c1 = Calendar.getInstance(TimeZone.getDefault())
    val c2 = Calendar.getInstance(TimeZone.getDefault())
    c1.timeInMillis = timestamp
    c2.timeInMillis = System.currentTimeMillis()

    val sdf: SimpleDateFormat = if (c1[Calendar.WEEK_OF_YEAR] == c2[Calendar.WEEK_OF_YEAR]) {
        // If same week then show only the day name (Ex: Mon, Tue)
        SimpleDateFormat("EEE", Locale.getDefault())
    } else {
        // Else show Date & Month (Ex: 10 Sep)
        SimpleDateFormat("dd MMM", Locale.getDefault())
    }

    sdf.timeZone = TimeZone.getDefault()

    return sdf.format(date)
}