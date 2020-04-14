package fr.epita.android.pokebattle
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.fragment_main.*

class TypeEntryAdapter(private var context: Context, private var typesList : List<Int>) : BaseAdapter() {

    override fun getCount(): Int {
        return typesList.size
    }

    override fun getItemId(position: Int): Long {
        return typesList[position].toLong()
    }

    override fun getItem(position: Int): Any {
        return typesList[position]
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        var cvView = convertView
        val type = typesList[position]
        if (convertView == null) {
            val inflater: LayoutInflater = LayoutInflater.from(context)
            cvView = inflater.inflate(R.layout.type_entry, null)
        }
        val typeIllustration: ImageView = cvView!!.findViewById(R.id.TypeEntryImageView)

        Glide.with(context)
             .load(type)
             .into(typeIllustration)
        return cvView
    }

}