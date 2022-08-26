package com.ricardo.soms

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import com.ricardo.soms.objetos.tags
import kotlinx.android.synthetic.main.list_tags_adapter.view.*

class TagsAdapter(private val mContext: Context, private val lista:List<tags>):
ArrayAdapter<tags>(mContext,0,lista){ //cmbiar tags por String

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {

        val layout = LayoutInflater.from(mContext).inflate(R.layout.list_tags_adapter,parent,false)


        val tag = lista[position]
        //layout.lbl_idtag.setText(""+position+1)
        //layout.lbl_tag.setText(""+lista[position])

        layout.lbl_idtag.setText(tag.idTag.toString())
        layout.lbl_tag.setText(tag.distanceTag.toString())

        return layout
    }


}