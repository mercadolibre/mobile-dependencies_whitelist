package com.mercadolibre.android.gradle.baseplugin.unitary.providers

import com.google.gson.JsonObject
import com.mercadolibre.android.gradle.baseplugin.core.action.modules.lint.dependencies.Dependency
import com.mercadolibre.android.gradle.baseplugin.core.action.utils.JsonUtils
import com.mercadolibre.android.gradle.baseplugin.core.components.GROUP_CONSTANT
import com.mercadolibre.android.gradle.baseplugin.core.components.NAME_CONSTANT
import com.mercadolibre.android.gradle.baseplugin.core.components.VERSION_CONSTANT
import com.mercadolibre.android.gradle.baseplugin.managers.ANY_GROUP
import com.mercadolibre.android.gradle.baseplugin.managers.ANY_NAME
import com.mercadolibre.android.gradle.baseplugin.managers.VERSION_1
import org.junit.Assert

class JsonProviderTest {

    @org.junit.Test
    fun `When parsing a JsonElement get proper value`() {
        val expected = Dependency(ANY_GROUP, ANY_NAME, VERSION_1, null, null)
        val item = JsonObject()
        item.addProperty(GROUP_CONSTANT, ANY_GROUP)
        item.addProperty(NAME_CONSTANT, ANY_NAME)
        item.addProperty(VERSION_CONSTANT, VERSION_1)

        Assert.assertEquals(expected.name, JsonUtils.getVariableFromJson(NAME_CONSTANT, item, ".*"))
        Assert.assertEquals(expected.group, JsonUtils.getVariableFromJson(GROUP_CONSTANT, item, ""))
        Assert.assertEquals(expected.version, JsonUtils.getVariableFromJson(VERSION_CONSTANT, item, ".*"))
    }

    @org.junit.Test
    fun `When parsing a non valid field in JsonElement get default value`() {
        val expected: String? = null

        val item = JsonObject()
        item.addProperty(ANY_GROUP, ANY_GROUP)
        val actual = JsonUtils.getVariableFromJson("no_exist", item, null)

        Assert.assertEquals(expected, actual)
    }
}
