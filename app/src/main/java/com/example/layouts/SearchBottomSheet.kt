package com.example.layouts


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import com.example.layouts.databinding.SearchBottomSheetBinding
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialogFragment


/**
 * Created by mohammad sajjad on 22-03-2022.
 * EMAIL mohammadsajjad679@gmail.com
 */

class SearchBottomSheet : BottomSheetDialogFragment() {
    private lateinit var binding: SearchBottomSheetBinding
    private val items = arrayListOf("All", "item 1", "item 2", "item 3", "item 4")
    private val items2 = arrayListOf("-select one group-","group 1","group 2","group 3")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = SearchBottomSheetBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val ad = ArrayAdapter(view.context, android.R.layout.simple_spinner_item, items)
        binding.allSpinner.adapter = ad

        binding.allSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {

                /**Toast.makeText(p0?.context, items[p3], Toast.LENGTH_SHORT).show()**/


            }

            override fun onNothingSelected(p0: AdapterView<*>?) {

            }


        }
        val ad2 = ArrayAdapter(view.context,android.R.layout.simple_spinner_item,items2)
        binding.groupFilterSpinner.adapter = ad2
        binding.groupFilterSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
               // Toast.makeText(p0?.context, items2[p2], Toast.LENGTH_SHORT).show()
            }


            override fun onNothingSelected(p0: AdapterView<*>?) {

            }

        }


    }


    override fun onStart() {
        super.onStart()
        val behavior = BottomSheetBehavior.from(requireView().parent as View)
        behavior.state = BottomSheetBehavior.STATE_EXPANDED

    }


}