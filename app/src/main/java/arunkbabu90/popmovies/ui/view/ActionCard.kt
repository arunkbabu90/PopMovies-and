package arunkbabu90.popmovies.ui.view

import android.content.Context
import android.util.AttributeSet
import android.util.TypedValue
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import arunkbabu90.popmovies.R
import com.google.android.material.card.MaterialCardView

class ActionCard @JvmOverloads constructor(context: Context, attr: AttributeSet, defStyleAttributeSet: Int = 0)
    : MaterialCardView(context, attr, defStyleAttributeSet) {

        init {
            inflate(context, R.layout.view_action_card,this)

            val imageView : ImageView = findViewById(R.id.action_card_icon_view)
            val textView : TextView = findViewById(R.id.action_card_desc)

            val padding = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 10f, resources.displayMetrics).toInt()
            setCardBackgroundColor(ContextCompat.getColor(context, R.color.colorDarkGrey))

            radius = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 10f, resources.displayMetrics)
            cardElevation = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 6f, resources.displayMetrics).toInt().toFloat()
            setPadding(padding, padding, padding, padding)

            val attributes = context.obtainStyledAttributes(attr, R.styleable.ActionCard)
            try {
                imageView.setImageDrawable(attributes.getDrawable(R.styleable.ActionCard_icn))
                imageView.imageTintList = attributes.getColorStateList(R.styleable.ActionCard_iconTint)
                textView.text = attributes.getString(R.styleable.ActionCard_description)
            } finally {
                attributes.recycle()
            }
        }
    }