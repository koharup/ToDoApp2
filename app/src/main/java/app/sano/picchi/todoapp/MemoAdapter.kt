package app.sano.picchi.todoapp

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import kotlinx.android.synthetic.main.layout_item_memo.view.*

class MemoAdapter internal constructor(
    context: Context,
    textViewResourceId: Int,
    objects: List<Memo>,
    private val listener: CustomOnItemClickLister
) :
    ArrayAdapter<Memo>(context, textViewResourceId, objects) {

    private val layoutinflater: LayoutInflater

    init {
        layoutinflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        var convertView = convertView
        val memo = getItem(position)

        if (convertView == null) {
            convertView = layoutinflater.inflate(R.layout.layout_item_memo, null)
        }

        convertView!!.setOnClickListener { listener.onItemClick(position) }

        val checkBox = convertView.checkBox
        checkBox.setOnClickListener{
            listener.onItemCheckClick(position,checkBox.isChecked)
        }
        //titleTextにセットする(memoクラスのtitleを)!!!
        convertView.titleText.text = memo!!.title
        convertView.contentText.text = memo.content
        checkBox.setChecked(memo.ischeck)
        return convertView
    }
}



