package arunkbabu90.popmovies;

import com.google.gson.annotations.SerializedName;

public class Reviews
{
   @SerializedName("author") private String author;
   @SerializedName("content") private String content;

    public Reviews(String author, String content) {
        this.author = author;
        this.content = content;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
