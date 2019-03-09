package pl.javasolutions.utils.xml

import java.time.LocalDate
import kotlin.reflect.KClass
import kotlin.reflect.full.cast

/**
 * representation object of xml base on map of values and map of attributes
 *
 * @author pawel.kowalski @ javasolutions
 */
class Node {

    /**
     * map of values red form xml
     */
    private val values = mutableMapOf<String, Any>()

    /**
     * map of attribute red from xml.
     * Attributes are used only in handled object
     */
    val attributes = mutableMapOf<String, String>()

    /**
     * add value to node.
     * if there is an value register on some name method will change map value to set.
     * otherwise an element will add to map.
     */
    @Suppress("UNCHECKED_CAST")
    fun addValue(name: String, value: Any) {

        // check if is there an element with name
        values.computeIfPresent(name.toLowerCase()) { _, oldValue ->

            // if it true ( an element exists ) check it's set already.
            when (oldValue) {

                // if its set apply add and return map
                is MutableSet<*> -> (oldValue as MutableSet<Any>).apply {
                    add(value)
                }

                // otherwise create set and apply old value and new value
                else ->
                    mutableSetOf<Any>().apply {
                        add(oldValue)
                        add(value)
                    }
            }

        } ?: values.apply {

            // when there is no element with name
            set(name.toLowerCase(), value)
        }
    }

    /**
     * get attribute value as long
     */
    fun getAttrAsLong(name: String): Long? =
        attributes[name.toLowerCase()]?.toLongOrNull()

    /**
     * get attribute value as string
     */
    fun getAttrAsString(name: String): String? =
        attributes[name.toLowerCase()]

    /**
     * get value as local date
     */
    fun getValueAsLocalDate(name: String): LocalDate? =
        values[name.toLowerCase()]?.let {
            LocalDate.parse(it as String)
        }


    /**
     * get value as string
     */
    fun getValueAsString(name: String): String? =
        values[name.toLowerCase()]?.let {
            it as String
        }

    /**
     * get value as specific object with type check
     *
     * @param name map key
     * @param clazz type of class to return
     *
     * @return return object of specific type.
     * if there is no object or object isn't type of specific class will return null
     */
    @Suppress("UNCHECKED_CAST")
    fun <T : Any> getValue(name: String, clazz: KClass<T>): T? =
        values[name.toLowerCase()]
            ?.let {
                if (clazz.isInstance(it)) {
                    return it as T
                }
                return null
            }


    /**
     * get value as set of specific types
     *
     * @param name key map
     * @param clazz type of class to return as set
     *
     * @return set of object of specific type with type check ( if there is map with wrong type will return empty set )
     * if there is no value in map will return empty set
     * if there is one value in map and it isn't instance of specific type will return empty set
     * if there is one value in map and it is instance of specific type will return set with this element ( one element set )
     */
    @Suppress("UNCHECKED_CAST")
    fun <T : Any> getValueAsSet(name: String, clazz: KClass<T>): MutableSet<T> =

        // get value
        values[name.toLowerCase()]
            ?.let { value ->
                when {
                    // when it's set
                    value is MutableSet<*> ->

                        // check it instance of first element
                        // if there is no element or it isn't instance of class will return default ( mutableSetOf )
                        value.firstOrNull()
                            ?.takeIf { v -> clazz.isInstance(v) }
                            ?.run {
                                value as MutableSet<T>
                            }

                    // when it's instance of class add to set and return
                    clazz.isInstance(value) ->
                        mutableSetOf<T>().apply {
                            add(clazz.cast(value))
                        }

                    // else return default ( mutableSetOf )
                    else -> null
                }

                // return empty set when there is no value
            } ?: mutableSetOf()


}