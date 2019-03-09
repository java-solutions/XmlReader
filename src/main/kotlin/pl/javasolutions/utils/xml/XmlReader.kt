package pl.javasolutions.utils.xml

import org.apache.log4j.Logger
import org.xml.sax.Attributes
import org.xml.sax.helpers.DefaultHandler
import java.util.*

/**
 * Handler to read xml base on SAX DefaultHandler
 * Base on LIFO algorithm
 *
 * @author pawel.kowalski @ javasolutions
 *
 * @see DefaultHandler
 * @see Deque
 */
class XmlReader : DefaultHandler() {

    private val logger: Logger = Logger.getLogger(XmlReader::class.java)

    /**
     * handlers map
     */
    private val handlers = mutableMapOf<String, NodeHandler<*>>()

    /**
     * LIFO Que
     */
    private val activeHandlers: Deque<NodeWriterHandler> = LinkedList<NodeWriterHandler>()

    /**
     * Object representation of red xml
     */
    var value: Any? = null
        private set

    /**
     * add handlers to map
     *
     * @return XmlReader return this object
     */
    fun addHandler(name: String, handler: NodeHandler<*>): XmlReader {
        handlers.putIfAbsent(name.toLowerCase(), handler)
        return this
    }

    /**
     * Receive notification of the start of an element.
     */
    override fun startElement(uri: String?, localName: String?, qName: String?, attributes: Attributes?) {
        logger.debug("<$qName>")

        // if qname is not empty
        qName?.takeIf { value -> value.isNotEmpty() }
            ?.toLowerCase()
            ?.run {

                // run active handler with start element
                activeHandlers.peekLast()?.startElement(this)

                // add to active handler new node writer when qname has handler
                handlers[this]?.let {
                    activeHandlers.addLast(NodeWriterHandler(this, it, attributes))
                }
            }
    }

    /**
     * Receive notification of character data inside an element.
     */
    override fun characters(ch: CharArray?, start: Int, length: Int) {

        // take char array build and trim value
        String(ch!!, start, length).trim()
            .takeIf { value -> value.isNotEmpty() }
            ?.run {

                // set value to active node writer
                activeHandlers.peekLast()?.value(this)

                logger.debug("    $this")
            }
    }

    /**
     * Receive notification of the end of an element.
     */
    override fun endElement(uri: String?, localName: String?, qName: String) {
        logger.debug("</$qName>")

        // take active handler if its register on qname of an element and its not last handler
        activeHandlers.takeIf { h -> h.peekLast().name == qName.toLowerCase() && h.size > 1 }

            // get and remove last handler
            ?.pollLast().run {

            // run end element event and write output object as value on surrounding element
            this?.endElement()?.run {
                activeHandlers.peekLast().value(this)
            }
        }
    }

    /**
     * Receive notification of the end of the document.
     */
    override fun endDocument() {

        // end document with end event and taking output object as value.
        value = activeHandlers.pollLast().endElement()
    }
}