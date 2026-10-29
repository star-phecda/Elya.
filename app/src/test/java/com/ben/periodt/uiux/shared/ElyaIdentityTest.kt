package com.ben.periodt.uiux.shared

import com.ben.periodt.ui.shared.ElyaIdentity
import org.junit.Assert.assertEquals
import org.junit.Test

class ElyaIdentityTest {
    @Test
    fun contactEmailUsesTheMaintainerAddress() {
        assertEquals("dawnphecda@gmail.com", ElyaIdentity.CONTACT_EMAIL)
    }

    @Test
    fun settingsFooterUsesTheRequestedDedication() {
        assertEquals("for my babiiee.❤️", ElyaIdentity.SETTINGS_DEDICATION)
    }
}
