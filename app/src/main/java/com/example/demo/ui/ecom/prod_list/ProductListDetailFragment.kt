package com.example.demo.ui.ecom.prod_list

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.NavHostFragment
import androidx.slidingpanelayout.widget.SlidingPaneLayout
import com.example.demo.R
import com.example.demo.databinding.FragmentProductListDetailBinding
import com.example.demo.ui.ecom.prod_list.prod_details.ProductDetailsFragment

class ProductListDetailFragment : Fragment() {

    private var _binding: FragmentProductListDetailBinding? = null
    val binding get() = _binding!!
    private lateinit var backManager: SlidingPaneBackManager

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentProductListDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val slidingPaneLayout = binding.slidingPaneLayout
        slidingPaneLayout.lockMode = SlidingPaneLayout.LOCK_MODE_LOCKED
        backManager = SlidingPaneBackManager(binding.slidingPaneLayout)
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, backManager)

        // Set up navigation for the product list
        val productListNavHostFragment = childFragmentManager.findFragmentById(R.id.product_detail_container) as NavHostFragment
        val navController = productListNavHostFragment.navController

        // Handle navigation from list to detail
        childFragmentManager.setFragmentResultListener("navigateToDetail", viewLifecycleOwner) { _, bundle ->
            val productId = bundle.getInt("productId")
            navController.navigate(R.id.productDetailsFragment,
                bundleOf("productId" to productId))
            slidingPaneLayout.open()
        }

        // Handle back navigation
        slidingPaneLayout.addPanelSlideListener(object : SlidingPaneLayout.PanelSlideListener {
            override fun onPanelSlide(panel: View, slideOffset: Float) {}
            override fun onPanelOpened(panel: View) {}
            override fun onPanelClosed(panel: View) {
                navController.popBackStack(R.id.productDetailsFragment, false)
            }
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}