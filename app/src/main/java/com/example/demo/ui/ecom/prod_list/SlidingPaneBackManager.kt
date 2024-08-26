package com.example.demo.ui.ecom.prod_list


import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.slidingpanelayout.widget.SlidingPaneLayout

class SlidingPaneBackManager(private val slidingPaneLayout: SlidingPaneLayout) :
    OnBackPressedCallback(
        slidingPaneLayout.isSlideable && slidingPaneLayout.isOpen
    ), SlidingPaneLayout.PanelSlideListener {

    init {
        slidingPaneLayout.addPanelSlideListener(this)
        slidingPaneLayout.addOnLayoutChangeListener { _, _, _, _, _, _, _, _, _ ->
            updateEnabledState()
        }
    }

    override fun handleOnBackPressed() {
        slidingPaneLayout.closePane()
    }

    private fun updateEnabledState() {
        // Only intercept the back button when the sliding pane layout is slideable
        // (in other words, only one of the two panes is visible) and when the sliding pane layout
        // is open (in other words, when the detail pane is open)
        isEnabled = slidingPaneLayout.isSlideable && slidingPaneLayout.isOpen
    }

    override fun onPanelSlide(panel: View, slideOffset: Float) {}

    override fun onPanelOpened(panel: View) {
        updateEnabledState()
    }

    override fun onPanelClosed(panel: View) {
        updateEnabledState()
    }
}