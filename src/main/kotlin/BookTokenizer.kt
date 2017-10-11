import org.jsoup.Jsoup
import java.io.File
import java.util.*

class BookTokenizer {
    fun parseToCountMap(textFile: File): Map<String, Int> {
        var textFromFile = Jsoup.parse(textFile.readText()).text()
        val re = Regex("[^A-Za-z0-9|\\s+]")
        textFromFile = re.replace(textFromFile, "")
        val tokens = textFromFile.split(" ")
        val tokenMap = TreeMap<String, Int>()
        for (token in tokens) {
            if (tokenMap.containsKey(token)) {
                tokenMap[token] = tokenMap[token]!! + 1
            }
            else {
                tokenMap.put(token, 1)
            }
        }

        return tokenMap
    }

}