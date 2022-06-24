package com.injent.miscalls.util;

public interface CustomOnBackPressedFragment {
    /**
     * @return true to initiate back action or false to cancel it.
     *
     * Called by pressing the back button
     */
    boolean onBackPressed();
}
