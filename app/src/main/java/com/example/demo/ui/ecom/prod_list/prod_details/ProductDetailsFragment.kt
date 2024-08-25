package com.example.demo.ui.ecom.prod_list.prod_details
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.viewpager2.widget.ViewPager2
import com.example.demo.R
import com.example.demo.databinding.FragmentProductDetailsBinding
import com.example.demo.ui.ecom.prod_list.ProductRepository
import me.relex.circleindicator.CircleIndicator3

class ProductDetailsFragment : Fragment() {

    private var _binding: FragmentProductDetailsBinding? = null
    private val binding get() = _binding!!

    private val args: ProductDetailsFragmentArgs by navArgs()
    private val viewModel: ProductDetailsViewModel by viewModels { ProductDetailsViewModelFactory(
        ProductRepository()
    ) }

    private lateinit var imageCarouselAdapter: ImageCarouselAdapter
    private lateinit var reviewsAdapter: ReviewsAdapter
    private val handler = Handler(Looper.getMainLooper())
    private var runnable: Runnable? = null
    private val scrollDelay = 3000L // 3 seconds


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentProductDetailsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupToolbar()
        setupMenu()
        setupImageCarousel()
        setupReviewsRecyclerView()
        setupQuantityControl()
        setupAddToCartButton()
        observeProductDetails()

        viewModel.loadProductDetails(args.productId)
    }
    private fun setupToolbar() {
        (activity as? AppCompatActivity)?.supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            setDisplayShowHomeEnabled(true)
            title = "Product Details"
        }
    }

    private fun setupMenu() {
        (requireActivity() as MenuHost).addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                // Add menu items here if needed
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                return when (menuItem.itemId) {
                    android.R.id.home -> {
                        findNavController().navigateUp()
                        true
                    }
                    else -> false
                }
            }
        }, viewLifecycleOwner, Lifecycle.State.RESUMED)
    }
    private fun setupImageCarousel() {
        imageCarouselAdapter = ImageCarouselAdapter()
        binding.imageCarousel.adapter = imageCarouselAdapter

        val indicator: CircleIndicator3 = binding.indicator
        indicator.setViewPager(binding.imageCarousel)

        // Register the adapter observer
        imageCarouselAdapter.registerAdapterDataObserver(indicator.adapterDataObserver)

        // Set up auto-scrolling
        binding.imageCarousel.registerOnPageChangeCallback(object :
            ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                stopAutoScroll()
                startAutoScroll()
            }
        })
    }


    private fun setupReviewsRecyclerView() {
        reviewsAdapter = ReviewsAdapter()
        binding.reviewsRecyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = reviewsAdapter
        }
    }

    private fun setupQuantityControl() {
        binding.incrementButton.setOnClickListener { viewModel.incrementQuantity() }
        binding.decrementButton.setOnClickListener { viewModel.decrementQuantity() }
    }

    private fun setupAddToCartButton() {
        binding.addToCartButton.setOnClickListener { viewModel.addToCart() }
    }

    private fun observeProductDetails() {
        viewModel.productDetails.observe(viewLifecycleOwner) { product ->
            binding.productTitle.text = product.name
            binding.productDescription.text = product.description
            binding.productPrice.text = "$${product.price}"
            binding.productRating.rating = product.rating

            imageCarouselAdapter.submitList(product.images)
            reviewsAdapter.submitList(product.reviews)
        }

        viewModel.quantity.observe(viewLifecycleOwner) { quantity ->
            binding.quantityText.text = quantity.toString()
        }
        startAutoScroll()
    }
    private fun startAutoScroll() {
        runnable = Runnable {
            val currentItem = binding.imageCarousel.currentItem
            val itemCount = imageCarouselAdapter.itemCount
            binding.imageCarousel.setCurrentItem((currentItem + 1) % itemCount, true)
        }
        handler.postDelayed(runnable!!, scrollDelay)
    }

    private fun stopAutoScroll() {
        runnable?.let { handler.removeCallbacks(it) }
    }

    override fun onResume() {
        super.onResume()
        startAutoScroll()
    }

    override fun onPause() {
        super.onPause()
        stopAutoScroll()
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}