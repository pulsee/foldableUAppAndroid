package com.example.demo.ui.ecom.prod_list
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResult
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.paging.LoadState
import androidx.recyclerview.widget.GridLayoutManager
import com.example.demo.databinding.FragmentProductListBinding
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class ProductListFragment : Fragment() {
    private lateinit var binding: FragmentProductListBinding
    private val viewModel: ProductListViewModel by viewModels {
        ProductListViewModelFactory(ProductRepository())
    }
    private lateinit var adapter : ProductPagingAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentProductListBinding.inflate(inflater, container, false)
//        setupLoadStateListener()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        observeProducts()
        setupLoadStateListener()
    }

    private fun setupRecyclerView() {
        adapter = ProductPagingAdapter { productId ->
            // Use setFragmentResult to communicate with parent fragment
            setFragmentResult("navigateToDetail", bundleOf("productId" to productId))
        }

        binding.productRecyclerView.layoutManager = GridLayoutManager(requireContext(), 2)
        binding.productRecyclerView.adapter =  adapter.withLoadStateHeaderAndFooter(
            header = ProductLoadStateAdapter { adapter.retry() },
            footer = ProductLoadStateAdapter { adapter.retry() }
        )
    }

    private fun observeProducts() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.products.collectLatest { pagingData ->
                adapter.submitData(pagingData)
            }
        }
    }
    private fun setupLoadStateListener() {
        viewLifecycleOwner.lifecycleScope.launch {
            adapter.loadStateFlow.collectLatest { loadStates ->
                binding.apply {
                    // Main refresh progress bar
                    refreshProgressBar.isVisible = loadStates.refresh is LoadState.Loading

                    // Header progress bar (top of list)
                    headerProgressBar.isVisible = loadStates.source.prepend is LoadState.Loading

                    // Footer progress bar (bottom of list)
                    footerProgressBar.isVisible = loadStates.source.append is LoadState.Loading

                    // RecyclerView visibility
                    productRecyclerView.isVisible = loadStates.refresh is LoadState.NotLoading

                    // Error view
                    errorView.isVisible = loadStates.refresh is LoadState.Error

                    // Empty view
                    emptyView.isVisible = loadStates.refresh is LoadState.NotLoading && adapter.itemCount == 0

                    // Retry button
                    retryButton.isVisible = loadStates.refresh is LoadState.Error

                    // Footer visibility is handled by the LoadStateAdapter
                    // You don't need to manually show/hide it

                    // Handle errors
                    val errorState = loadStates.source.append as? LoadState.Error
                        ?: loadStates.source.prepend as? LoadState.Error
                        ?: loadStates.append as? LoadState.Error
                        ?: loadStates.prepend as? LoadState.Error
                    errorState?.let {
                        Toast.makeText(
                            requireContext(),
                            "An error occurred: ${it.error}",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
            }
        }

        // Set up retry button click listener
        binding.retryButton.setOnClickListener { adapter.retry() }
    }

}