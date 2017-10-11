class Book(var author: String, var title: String, var series: String = "") {
    init {
        var tmp = author.split(", ")
//        println(tmp)
        if (tmp.size != 2){
            tmp += ""
        }
        author = "${tmp[1]} ${tmp[0]}"
    }
}

class ResultBook {
    var id: Int = 0
    var booksCount: Int = 0
    var ratingsCount: Int = 0
    var textReviewsCount: Int = 0
    var originalPublicationYear: Int = 0
    var originalPublicationMonth: Int =0
    var originalPublicationDay: Int = 0
    var averageRating: Double = 0.0
    var bookId: Int = 0
    var title: String = ""
    var author: String = ""
    var authorId: Int = 0
    var imageUrl: String = ""
    var smallImageUrl: String = ""
    var incomplete: Boolean = false

    override fun toString(): String {
        return "$title by $author -- Rating: $averageRating"
    }
}