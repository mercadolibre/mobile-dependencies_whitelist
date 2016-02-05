package com.mercadolibre.android.gradle.base

/**
 * @author Nahuel Barrios, on 04/02/16. Copyright (C) 2014 MercadoLibre.com
 */
class BasePluginTest extends GroovyTestCase {

    void testGetGreater() {
        def a = "3.0"
        def b = "2.10"
        def c = "2.6"
        def d = "1.11.123"
        def e = "1.11"

        assertEquals(b + " should be greater than " + c, b, BasePlugin.getGreater(b, c))
        assertEquals(b + " should be greater than " + c, b, BasePlugin.getGreater(c, b))
        assertEquals(a + " should be greater than " + b, a, BasePlugin.getGreater(a, b))
        assertEquals(b + " should be greater than " + e, b, BasePlugin.getGreater(e, b))
        assertEquals(d + " should be greater than " + e, d, BasePlugin.getGreater(e, d))
    }

    void testShouldUpgradeGradleWrapperVersion() {
        assertFalse(BasePlugin.shouldUpgradeGradleWrapper("2.6"))
        assertFalse(BasePlugin.shouldUpgradeGradleWrapper("2.10"))
        assertFalse(BasePlugin.shouldUpgradeGradleWrapper("3"))
        assertTrue(BasePlugin.shouldUpgradeGradleWrapper("1"))
        assertTrue(BasePlugin.shouldUpgradeGradleWrapper("1.10"))
        assertTrue(BasePlugin.shouldUpgradeGradleWrapper("2.5"))
    }
}
