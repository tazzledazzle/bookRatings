package lucerne

import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.given

object IndexerTests: Spek({
    given("an indexer") {
        val indexDir = "/Users/tschumacher/sandbox/bookRating/textfiles"
        val indexer = Indexer(indexDir)

    }
})