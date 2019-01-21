package arunkbabu90.popmovies;

import android.content.Context;
import android.util.DisplayMetrics;

public class MovieUtils
{
    /*
     * Code adapted from https://stackoverflow.com/questions/33575731/gridlayoutmanager-how-to-auto-fit-columns
     */
    /**
     * Intelligently calculates the number of grid columns to be displayed on screen with respect to
     * the available screen size
     * @param context The Application Context
     * @return int  The number of columns to be displayed
     */
    public static int calculateNoOfColumns(Context context) {
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        float dpWidth = displayMetrics.widthPixels / displayMetrics.density;
        int columnWidth = 115;
        return (int) (dpWidth / columnWidth);
    }
}
