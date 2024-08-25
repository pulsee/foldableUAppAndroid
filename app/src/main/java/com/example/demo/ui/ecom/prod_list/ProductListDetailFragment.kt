package com.example.demo.ui.ecom.prod_list

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.NavHostFragment
import androidx.slidingpanelayout.widget.SlidingPaneLayout
import com.example.demo.R
import com.example.demo.databinding.FragmentProductListDetailBinding
import com.example.demo.ui.ecom.prod_list.prod_details.ProductDetailsFragment

class ProductListDetailFragment : Fragment() {

    private var _binding: FragmentProductListDetailBinding? = null
    val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentProductListDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val slidingPaneLayout = binding.slidingPaneLayout
        slidingPaneLayout.lockMode = SlidingPaneLayout.LOCK_MODE_LOCKED

        // Set up navigation for the product list
        val productListNavHostFragment = childFragmentManager.findFragmentById(R.id.product_list_container) as NavHostFragment
        val productListNavController = productListNavHostFragment.navController

        // Handle navigation from list to detail
        productListNavController.addOnDestinationChangedListener { _, destination, arguments ->
            if (destination.id == R.id.productDetailsFragment) {
                val productId = arguments?.getInt("productId") ?: return@addOnDestinationChangedListener
                val detailFragment = ProductDetailsFragment()
                detailFragment.arguments = Bundle().apply {
                    putInt("productId", productId)
                }
                childFragmentManager.beginTransaction()
                    .replace(R.id.product_detail_container, detailFragment)
                    .commit()
                slidingPaneLayout.open()
            }
        }

        // Handle back navigation
        slidingPaneLayout.addPanelSlideListener(object : SlidingPaneLayout.PanelSlideListener {
            override fun onPanelSlide(panel: View, slideOffset: Float) {}
            override fun onPanelOpened(panel: View) {}
            override fun onPanelClosed(panel: View) {
                productListNavController.popBackStack(R.id.productListFragment, false)
            }
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}