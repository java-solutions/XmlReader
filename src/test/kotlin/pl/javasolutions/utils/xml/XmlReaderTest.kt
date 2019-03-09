package pl.javasolutions.utils.xml

import org.junit.Assert
import org.junit.Test
import pl.javasolutions.utils.xml.test.*
import java.time.LocalDate
import javax.xml.parsers.SAXParserFactory

class XmlReaderTest {

    private val fileName = "books.xml"

    @Test
    fun readBookXML() {

        val saxParser = SAXParserFactory.newInstance().newSAXParser()
        val inputStream = javaClass.getResourceAsStream("/$fileName")

        val handler = XmlReader()
            .addHandler("book", NodeHandler<Book> { node ->
                Book(
                    id = node.getAttrAsLong("id") ?: 0,
                    isbn = node.getValueAsString("isbn") ?: "",
                    title = node.getValueAsSet("title", Title::class),
                    authors = node.getValueAsSet("author", Author::class),
                    releaseDate = node.getValueAsLocalDate("releaseDate"),
                    publishers = node.getValueAsSet("publisher", Publisher::class)
                )
            })
            .addHandler("author", NodeHandler<Author> { node ->
                Author(
                    name = node.getValueAsString("name") ?: "",
                    surname = node.getValueAsString("surname") ?: ""
                )
            })
            .addHandler("books", NodeHandler<Books> { node ->
                Books(
                    books = node.getValueAsSet("book", Book::class)
                )
            })
            .addHandler("publisher", NodeHandler { node ->
                Publisher(
                    name = node.getValueAsString("name") ?: "",
                    year = node.getValueAsString("yearOfPublish") ?: ""
                )
            }).addHandler("title", NodeHandler { node ->
                Title(
                    lang = node.getAttrAsString("lang"),
                    value = node.getValueAsString("value")
                )
            })


        saxParser.parse(inputStream, handler)

        Assert.assertNotNull(handler.value)
        Assert.assertNotNull(handler.value is Books)

        val books = handler.value as Books
        Assert.assertNotNull(books.books)
        Assert.assertEquals(2, books.books.size)

        val book = books.books.firstOrNull { b -> b.id == 1L }
        Assert.assertNotNull(book)
        Assert.assertEquals("978-3-16-148410-0", book?.isbn)
        Assert.assertEquals(LocalDate.parse("2019-01-01"), book?.releaseDate)
        Assert.assertNotNull(book?.authors)
        Assert.assertEquals(2, book?.authors?.size)
        Assert.assertEquals(2, book?.publishers?.size)

        val title = book?.title?.firstOrNull { t -> t.lang == "pl" }
        Assert.assertNotNull(title)
        Assert.assertEquals("przykladowy tytul 1", title?.value)

        val titleEn = book?.title?.firstOrNull { t -> t.lang == "en" }
        Assert.assertNotNull(titleEn)
        Assert.assertEquals("english title 1", titleEn?.value)

        val author = book?.authors?.first { a -> a.name == "Author 2" }
        Assert.assertNotNull(author)
        Assert.assertEquals("Surname 2", author?.surname)

        val publisher = book?.publishers?.first { p -> p.name == "Publisher 1" }
        Assert.assertNotNull(publisher)
        Assert.assertEquals("1980", publisher?.year)

    }

}