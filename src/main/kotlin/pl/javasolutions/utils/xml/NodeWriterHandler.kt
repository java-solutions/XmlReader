package pl.javasolutions.utils.xml

import org.xml.sax.Attributes

/**
 * Handler to create Node, use in XmlReader
 *
 * @author pawel.kowalski @ javasolutions
 *
 * @see Node
 * @see XmlReader
 */
class NodeWriterHandler(
    /**
     * name of handler base on qname
     */
    val name: String,

    /**
     * handler object used in endElement
     */
    private val handler: NodeHandler<*>,

    /**
     * attribute of xml element.
     */
    attributes: Attributes?
) {

    private val node = Node()

    /**
     * last register element from xml
     * default element is value, for a case when register handler is for object with attributes only
     */
    private var activeElement: String =  "value"

    /**
     * convert xml attributes to map of strings in node
     */
    init {
        attributes?.run {
            for (i in 0..this.length) {
                this.getLocalName(i)
                    ?.takeIf { value -> value.isNotEmpty() }
                    ?.run {
                        node.attributes.put(this, attributes.getValue(i))
                    }
            }
        }

    }

    /**
     * start element event to save last element
     */
    fun startElement(qName: String) {
        activeElement = qName
    }

    /**
     * save value to node using active element
     */
    fun value(value: Any) {
        activeElement.run {
            node.addValue(this, value)
        }
    }

    /**
     * end element event clears active element and run process on handler
     */
    fun endElement(): Any? {
        activeElement = ""
        return handler.process(node)
    }
}