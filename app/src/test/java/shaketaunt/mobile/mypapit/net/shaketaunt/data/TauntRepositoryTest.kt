package shaketaunt.mobile.mypapit.net.shaketaunt.data

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class TauntRepositoryTest {

    @Test
    fun defaultPhrasesContainClassicAndNewTauntsWithoutDuplicates() {
        val phrases = TauntRepository.DEFAULT_PHRASES
        assertEquals(80, phrases.size)
        assertEquals(phrases.size, phrases.map { it.lowercase() }.distinct().size)
        assertTrue(phrases.contains("just you are"))
        assertTrue(phrases.contains("ugly duckly"))
        assertTrue(phrases.contains("Prince Charming"))
        assertTrue(phrases.contains("silly!"))
        assertTrue(phrases.contains("a couch potato"))
        assertTrue(phrases.contains("a walking meme"))
        assertTrue(phrases.contains("one of a kind!"))
    }

    @Test
    fun nextTauntAlwaysStartsWithYouArePrefix() {
        val repository = TauntRepository(nextIndex = { 1 })
        val taunt = repository.nextTaunt()
        assertEquals("You are beautiful!", taunt)
    }

    @Test
    fun nextTauntNeverRepeatsPreviousTauntWhenMultiplePhrasesExist() {
        val repository = TauntRepository(
            phrases = listOf("fabulous", "grumpy", "silly!"),
            nextIndex = { 0 },
        )

        val first = repository.nextTaunt(previousTaunt = null)
        assertEquals("You are fabulous", first)

        val second = repository.nextTaunt(previousTaunt = first)
        assertNotEquals(first, second)
        assertEquals("You are grumpy", second)
    }
}
