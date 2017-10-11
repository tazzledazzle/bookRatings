import org.jsoup.Jsoup

class WebBookList {
    // look in the html here for a list of books: https://www.amazon.com/b/ref=s9_acsd_al_bw_clnk_r?_encoding=UTF8&node=9630682011&pf_rd_m=ATVPDKIKX0DER&pf_rd_s=merchandised-search-5&pf_rd_r=TG4FB5AZ2GEA6D6NMF8Z&pf_rd_r=TG4FB5AZ2GEA6D6NMF8Z&pf_rd_t=101&pf_rd_p=bb581cd3-b338-4734-adb1-fa05fd667cd6&pf_rd_p=bb581cd3-b338-4734-adb1-fa05fd667cd6&pf_rd_i=9069934011
    // <a class="a-link-normal s-access-detail-page  s-color-twister-title-link a-text-normal" title="Beneath a Scarlet Sky: A Novel" href="https://www.amazon.com/Beneath-Scarlet-Sky-Mark-Sullivan-ebook/dp/B01L1CEZ6K/ref=lp_9630682011_1_1/130-5845197-5628401?s=digital-text&amp;ie=UTF8&amp;qid=1507429535&amp;sr=1-1"><h2 data-attribute="Beneath a Scarlet Sky: A Novel" data-max-rows="0" class="a-size-medium s-inline  s-access-title  a-text-normal">Beneath a Scarlet Sky: A Novel</h2></a>
    // the link to the book on the first page //*[@id="result_[0-9]+"]/div/div[2]/div/div[2]/div[1]/div[1]/a
    // grabs the list on the first page //*[@id="mainResults"]/ul/li
    fun getBook() {
//        Jsoup.parse()
    }
}