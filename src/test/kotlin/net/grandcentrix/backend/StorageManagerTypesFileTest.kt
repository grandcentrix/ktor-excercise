package net.grandcentrix.backend

import io.mockk.every
import io.mockk.mockkObject
import io.mockk.unmockkAll
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.encodeToJsonElement
import net.grandcentrix.backend.StorageManagerTypesFile.Companion.StorageManagerTypesFileInstance
import net.grandcentrix.backend.enums.VideoType
import org.junit.Before
import org.junit.Test
import java.io.File
import kotlin.test.AfterTest
import kotlin.test.assertEquals

class StorageManagerTypesFileTest {

    companion object {
        val typeNames = VideoType.entries.map { it.name }.toMutableList()
        private const val FILE_NAME = "src/main/resources/testFile.json"
    }

    @Before
    fun beforeTests() {
        val typeNamesJson = Json.encodeToJsonElement(typeNames).toString()
        File(FILE_NAME).writeText(typeNamesJson)

        mockkObject(StorageManagerTypesFileInstance, recordPrivateCalls = true)
        every { StorageManagerTypesFileInstance["getFile"]() } returns File(FILE_NAME)
    }

    @AfterTest
    fun afterTest() {
        unmockkAll()
        File(FILE_NAME).writeText("[]")
    }

    @Test
    fun testGetContent() {
        assertEquals(StorageManagerTypesFileInstance.getContent(), typeNames)
    }

    @Test
    fun testSetContent() {
        typeNames.add("TEST")
        StorageManagerTypesFileInstance.setContent(typeNames)
    }

}