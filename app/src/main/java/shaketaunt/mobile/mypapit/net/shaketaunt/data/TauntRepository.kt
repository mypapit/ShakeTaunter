/*
 * Copyright 2018-2026 Mohammad Hafiz bin Ismail <mypapit@gmail.com>
 * http://blog.mypapit.net
 *
 * Redistribution and use in source and binary forms, with or without modification, are permitted
 * provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this list of conditions
 * and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright notice, this list of conditions
 * and the following disclaimer in the documentation and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR
 * IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND
 * FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS;
 * OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT
 * LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package shaketaunt.mobile.mypapit.net.shaketaunt.data

import java.security.SecureRandom

/**
 * Provides randomized taunts from the classic Shake Taunter phrase list while avoiding immediate
 * back-to-back repeats.
 */
class TauntRepository(
    private val phrases: List<String> = DEFAULT_PHRASES,
    private val nextIndex: (Int) -> Int = { bound -> SecureRandom().nextInt(bound) },
) {
    init {
        require(phrases.isNotEmpty()) { "Taunt phrase list must not be empty." }
    }

    fun nextTaunt(previousTaunt: String? = null): String {
        val index = nextIndex(phrases.size).floorMod(phrases.size)
        val candidate = "$PREFIX${phrases[index]}"
        if (phrases.size > 1 && candidate == previousTaunt) {
            val fallbackIndex = (index + 1) % phrases.size
            return "$PREFIX${phrases[fallbackIndex]}"
        }
        return candidate
    }

    private fun Int.floorMod(modulus: Int): Int {
        val rem = this % modulus
        return if (rem < 0) rem + modulus else rem
    }

    companion object {
        private const val PREFIX = "You are "

        val DEFAULT_PHRASES: List<String> = listOf(
            "just you are",
            "beautiful!",
            "naughty!",
            "cowardly lion",
            "lazy",
            "hardworking",
            "ugly duckly",
            "fabulous",
            "mean",
            "moody",
            "selfish",
            "prudent",
            "heroic",
            "tough as nail",
            "kind",
            "glamorous",
            "vain",
            "strong",
            "like a lady",
            "gorgeous!",
            "stupendous",
            "superb",
            "pompous!",
            "amazing",
            "great",
            "bossy",
            "cuckoo",
            "outrageous",
            "courageous",
            "adaptable",
            "adventurous",
            "frank",
            "passionate",
            "rational",
            "witty",
            "childish",
            "friendly",
            "super friendly",
            "clever",
            "industrious",
            "Prince Charming",
            "grumpy",
            "jealous",
            "vulgar",
            "silly!",
            "unstoppable!",
            "a couch potato",
            "dramatic!",
            "mischievous",
            "a drama queen",
            "chaotic good",
            "a sleepyhead",
            "too cool",
            "stubborn as a mule",
            "a daydreamer",
            "ridiculous!",
            "hilarious",
            "a show-off",
            "cheeky!",
            "unpredictable",
            "a chatterbox",
            "overthinking it",
            "a legend!",
            "suspiciously quiet",
            "a snack thief",
            "extra!",
            "bold and brash",
            "a walking meme",
            "eccentric",
            "utterly charming",
            "a procrastinator",
            "fearless!",
            "quirky",
            "a smart aleck",
            "dazzling!",
            "a night owl",
            "impatient!",
            "spectacular",
            "a goofball",
            "one of a kind!",
        )
    }
}
