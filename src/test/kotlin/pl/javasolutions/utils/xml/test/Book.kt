package pl.javasolutions.utils.xml.test

import java.time.LocalDate

class Book(
    var id: Long,
    var authors: Set<Author> = setOf(),
    var title: Set<Title> = setOf(),
    var isbn: String,
    var releaseDate: LocalDate? = null,
    var publishers: Set<Publisher> = setOf()
)