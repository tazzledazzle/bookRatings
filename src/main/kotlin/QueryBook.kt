/*
<work>
    <id type="integer">13190160</id>
    <books_count type="integer">99</books_count>
    <ratings_count type="integer">251268</ratings_count>
    <text_reviews_count type="integer">25329</text_reviews_count>
    <original_publication_year type="integer">2011</original_publication_year>
    <original_publication_month type="integer">2</original_publication_month>
    <original_publication_day type="integer">8</original_publication_day>
    <average_rating>3.99</average_rating>
    <best_book type="Book">
        <id type="integer">8667848</id>
        <title>A Discovery of Witches (All Souls Trilogy, #1)</title>
        <author>
            <id type="integer">3849415</id>
            <name>Deborah Harkness</name>
        </author>
        <image_url>
            https://images.gr-assets.com/books/1322168805m/8667848.jpg
        </image_url>
        <small_image_url>
            https://images.gr-assets.com/books/1322168805s/8667848.jpg
        </small_image_url>
    </best_book>
</work>
*/
class QueryBook {
    @JvmField val title: String? = null
    @JvmField val id: Int? = null
    @JvmField val author: String? = null
    @JvmField val authorId: String? = null
//    @JvmField val averageRating: Double? = null
    @JvmField val imageUrl: String? = null
    @JvmField val smallImageUrl: String? = null
}

class WorkItem {
    @JvmField val id: Int? = null
    @JvmField val bookCount: Int? = null
    @JvmField val ratingsCount: Int? = null
    @JvmField val textReviewCount: Int? = null
    @JvmField val originalPublicationYear: Int? = null
    @JvmField val originalPublicationMonth: Int? = null
    @JvmField val originalPublicationDay: Int? = null
    @JvmField val averageRating: Double? = null
    @JvmField val book: QueryBook? = null

}