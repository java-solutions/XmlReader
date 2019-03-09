package pl.javasolutions.utils.xml;

/**
 * Handler interface to create object from Node
 * it's written in java because kotlin does not support SAM interface written in kotlin him self (sic !!)
 *
 * @param <T> generic used as an returned object in the conversion function
 * @author pawel.kowalski @ javasolutions
 * @see Node
 */
public interface NodeHandler<T> {

    /**
     * method to convert Node to <b>T</b> object
     *
     * @param node map decorator from xml
     * @return converted object
     */
    T process(Node node);
}
