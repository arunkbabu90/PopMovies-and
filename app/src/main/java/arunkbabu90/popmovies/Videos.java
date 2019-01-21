package arunkbabu90.popmovies;

import com.google.gson.annotations.SerializedName;

/**
 * An object DataType which stores all the information videos, trailers, teasers related to a movie
 */
public class Videos
{
    // Youtube Example URL https://www.youtube.com/watch?v=D86RtevtfrA
    private final String YOUTUBE_BASE_URL = "https://www.youtube.com/watch?v=";
    @SerializedName("name") private final String mName;
    @SerializedName("key") private final String key;
    @SerializedName("type") private final String type;


    public Videos(String name, String key, String type) {
        mName = name;
        this.key = key;
        this.type = type;
    }

    /**
     * Retrieves the Name of the Video
     * @return String name of the Video
     */
    public String getTrailerName() {
        return mName;
    }

    /**
     * Retrieves the Key associated with the Video. This key can be used for building the YouTube link
     * @return String Key of the Video
     * NOTE: THIS METHOD WILL BE FULLY IMPLEMENTED IN THE NEXT VERSION
     */
    public String getYouTubeURL() {
        return YOUTUBE_BASE_URL + key;
    }

    /**
     * Retrieves the Type of the video
     * @return String  type of the Video
     */
    public String getType() {
        return type;
    }
}
