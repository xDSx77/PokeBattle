package fr.epita.android.pokebattle.typehelp
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import com.bumptech.glide.Glide
import fr.epita.android.pokebattle.R
import fr.epita.android.pokebattle.Utils
import fr.epita.android.pokebattle.main.MainActivity

class TypeEntryAdapter(private var context: Context, private var typesList : List<String>) : BaseAdapter() {

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
        typeIllustration.setOnClickListener {
            (cvView.context as MainActivity).TypeHelp(type)
        }

        Glide.with(context)
             .load(Utils.typeToRDrawable(type))
             .into(typeIllustration)
        return cvView
    }

}