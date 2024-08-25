package com.example.demo.ui.ecom.prod_list.prod_details

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.demo.ui.ecom.prod_list.Product
import com.example.demo.ui.ecom.prod_list.ProductRepository
import kotlinx.coroutines.launch

class ProductDetailsViewModel(private val repository: ProductRepository) : ViewModel() {

    private val _productDetails = MutableLiveData<Product>()
    val productDetails: LiveData<Product> = _productDetails

    private val _quantity = MutableLiveData(1)
    val quantity: LiveData<Int> = _quantity

    fun loadProductDetails(productId: Int) {
        viewModelScope.launch {
            try {
                val product = repository.getProductDetails(productId)
                _productDetails.value = product
            } catch (e: Exception) {
                // Handle error
            }
        }
    }

    fun incrementQuantity() {
        _quantity.value = (_quantity.value ?: 1) + 1
    }

    fun decrementQuantity() {
        _quantity.value = (_quantity.value ?: 1).let { if (it > 1) it - 1 else 1 }
    }

    fun addToCart() {
        // Implement add to cart logic
        val product = _productDetails.value
        val quantity = _quantity.value
        if (product != null && quantity != null) {
            // Add to cart logic here
        }
    }
}

class ProductDetailsViewModelFactory(private val repository: ProductRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ProductDetailsViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ProductDetailsViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}