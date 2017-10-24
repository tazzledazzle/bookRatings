import org.jsoup.Jsoup
import java.io.File
import java.nio.charset.StandardCharsets
import java.nio.file.Files
import java.util.*
import javax.xml.bind.DatatypeConverter
import kotlin.collections.HashMap
import kotlin.experimental.and

/*
    key: TkrieIuuuWJcH1NOJIWLOw
    secret: XNBMLH1ADkqKdFerygwxNaCp3isnhuETJeW7NYoEjM
*/

/*
    SearchUrl: https://www.goodreads.com/search/index.xml?
    Parameters:
    q: The query text to match against book title, author, and ISBN fields. Supports boolean operators and phrase searching.
    page: Which page to return (default 1, optional)
    key: Developer key (required).
    search[field]: Field to search, one of 'title', 'author', or 'all' (default is 'all')
*/

//fun main(args:Array<String>) {
////    parseHtmlBooksToTextFiles(File("/Users/tschumacher/sandbox/bookRating/bookTexts2"))
//    val books = BookTexts()
//    books.addDocsFromLocation(File("/Users/tschumacher/sandbox/bookRating/bookTexts"))
//    books.queryDocs("woman")
//}
//
//fun parseHtmlBooksToTextFiles(locDir: File) {
//    val htmlTextFiles = locDir.listFiles()
//    htmlTextFiles.forEach { f ->
//        val text = Jsoup.parse(File("${f.absolutePath}/${f.name}.html").readText()).text()
//        File("${f.absolutePath}/${f.name}.txt").writeText(text)
//    }
//
//}

fun main(args:Array<String>) {
    val jsonManifests = ArrayList<String>()
    val textOs = """
        {    "packages" : [
                {
                    "name" : "apache",
                    "version" : "<%= version %>"
                },
                {
                    "name" : "bin",
                    "version" : "<%= version %>"
                },
                <% if windows %>
                {
                    "name" : "bin32",
                    "version" : "<%= version %>"
                },
                <% end %>
                {
                    "name" : "lib",
                    "version" : "<%= version %>"
                },
                {
                    "name" : "tomcat",
                    "version" : "<%= version %>"
                },
                {
                    "name" : "repository",
                    "version" : "<%= version %>"
                },
                {
                    "name" : "templates",
                    "version" : "<%= version %>"
                },
                {
                    "name" : "vizqlserver",
                    "version" : "<%= version %>"
                }
            ]
            } """
    var textElse = """
        {
    "baseName" : "tabadminagent",
    "description" : "Controls the local node in a Tableau Server cluster",
    "displayName" : "Tableau Server Administration Agent",
    "autoEnable" : true,
    "serviceManager" : "System",
<% if windows %>
<% else %>
    "linuxKillMode" : "process",
<% end %>
    "actions" : {
    },
    "packages" : [
	    {
            "name" : "apache",
            "version" : "<%= version %>"
        },
        {
            "name" : "managementlibs",
            "version" : "<%= version %>"
        },
        {
            "name" : "tabadminagent",
            "version" : "<%= version %>"
        },
        {
            "name" : "repository",
            "version" : "<%= version %>"
        },
        {
            "name" : "templates",
            "version" : "<%= version %>"
        }
    ]
}"""
    jsonManifests.add(textOs)
    jsonManifests.add(textElse)

    jsonManifests.forEach { manifest ->
        var text = manifest
        text = text.replace("<%= version %>", "mock_version")

        // if else contained
        if (text.contains("<% else %>")) {
            val re = "<% if windows %>((\\w|\\s|\"|:|\\{|}|,)*)<% else %>((\\w|\\s|\"|:|\\{|}|,)*)<% end %>".toRegex()
            val results = re.findAll(text).toList()
            if(results.isNotEmpty()) {
                val foundWin = results[0].groups[1]
                val foundLin = results[0].groups[3]
                val newRange = IntRange(foundWin!!.range.first - "<% if windows %>".length, foundLin!!.range.last + "<% end %>".length)
                text = if (System.getProperty("os.name").toLowerCase().indexOf("win") >= 0) {
                    text.replaceRange(newRange, foundWin.value)
                } else {
                    text.replaceRange(newRange, foundLin.value)
                }
            }
        }
        // if windows only
        else if (text.contains("<% if windows %>")) {
            val re = "<% if windows %>((\\w|\\s|\"|:|\\{|}|,)*)<% end %>".toRegex()
            val results = re.findAll(text).toList()
            if(results.isNotEmpty()) {
                val found = results[0].groups[1]!!
                val newRange = IntRange(found.range.first - "<% if windows %>".length, found.range.last + "<% end %>".length)
                text = text.replaceRange(newRange, found.value)
            }
        }

//        deployManifest.writeText(text)
        println("\n\n$text")
    }

}

