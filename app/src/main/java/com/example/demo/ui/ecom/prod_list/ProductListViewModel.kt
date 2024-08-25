package com.example.demo.ui.ecom.prod_list

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
/*

class ProductListViewModel(private val repository: ProductRepository) : ViewModel() {
    private val _uiState = MutableLiveData<ProductListUiState>().apply {
        value = ProductListUiState()
    }
    val uiState: LiveData<ProductListUiState> = _uiState

    private var currentPage = 1

    init {
        loadProducts()
    }

    fun loadProducts() {
        _uiState.value = _uiState.value?.copy(isLoading = true, error = null)
        viewModelScope.launch {
            try {
                val newProducts = repository.fetchProducts(currentPage)
                val currentProducts = _uiState.value?.products ?: emptyList()
                _uiState.value = ProductListUiState(
                    products = currentProducts + newProducts,
                    isLoading = false
                )
                currentPage++
            } catch (e: Exception) {
                _uiState.value = _uiState.value?.copy(
                    isLoading = false,
                    error = "Failed to load products: ${e.message}"
                )
            }
        }
    }

    private suspend fun fetchProducts(page: Int): List<Product> {
        // Simulate network delay
        delay(1000)
        // Return dummy data
        return (1..10).map { i ->
            Product(
                id = (page - 1) * 10 + i,
                name = "Product ${(page - 1) * 10 + i}",
                price = (10 + i * 1.5),
                imageUrl = "https://dog.ceo/api/breeds/image/random",
                rating = (3 + kotlin.random.Random.nextFloat() * 2)
            )
        }
    }
}*/
class ProductListViewModel(private val repository: ProductRepository) : ViewModel() {
    val products: Flow<PagingData<Product>> = repository.getProductPager()
        .cachedIn(viewModelScope)
}