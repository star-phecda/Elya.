package com.ben.periodt.uiux.shared

import com.ben.periodt.ui.shared.ElyaIdentity
import org.junit.Assert.assertEquals
import org.junit.Test

class ElyaIdentityTests {
    @Test
    fun maintainerContactUsesTheCurrentElyaEmail() {
        assertEquals("dawnphecda@gmail.com", ElyaIdentity.CONTACT_EMAIL)
    }

    @Test
    fun settingsDedicationUsesTheApprovedExactCopy() {
        assertEquals("for my babiiee.❤️", ElyaIdentity.SETTINGS_DEDICATION)
    }
}
