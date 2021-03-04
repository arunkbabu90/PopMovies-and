package arunkbabu90.popmovies.ui.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import arunkbabu90.popmovies.R

class FavouritesActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_favourites)

        window.statusBarColor = ContextCompat.getColor(this, R.color.colorPurpleDark)
        window.navigationBarColor = ContextCompat.getColor(this, R.color.colorPurple)
    }
}