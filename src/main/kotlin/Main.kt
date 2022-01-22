/**
 * @author Jesús López Sánchez
 */


import java.util.logging.Level
import java.util.logging.LogManager
import org.w3c.dom.Document
import org.w3c.dom.Element
import org.w3c.dom.Node
import org.w3c.dom.NodeList
import java.io.File
import java.time.LocalDate
import javax.xml.parsers.DocumentBuilderFactory

/**
 * @param [CatalogoLibrosXML]
 * @constructor cargador: sirve para pasarle la ruta del XML en el main después de instanciar el objeto.
 * ESte proyecto solo tiene una única clase.
 */

class CatalogoLibrosXML(private val cargador: String) {

    companion object {
        val l = LogManager.getLogManager().getLogger("").apply { level = Level.ALL }
    }

    /**
     * @param se inicializa la fuinción [readXml] que convierte a documento el XML que se pasa a través de la ruta
     * en la variable de clase [cargador] una vez intanciada en el main.
     */


    private var xmlDoc: Document? = null

    init {
        try {
            xmlDoc = readXml(cargador)
            xmlDoc?.let { it.documentElement.normalize() }
        } catch (e: Exception) {
            requireNotNull(xmlDoc, { e.message.toString() })
        }
    }

    /**
     * @param la función [readXml] convierte a documento.
     * @return construye a documento el path que se le ha pasado en el main
     */

    private fun readXml(pathName: String): Document {
        val xmlFile = File(pathName)
        return DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(xmlFile)
    }

    /**
     * @param la función [obtenerListaNodosPorNombre] busca el primer ELEMENT del XML que conincida con el
     * string que se le pase en la id, en este caso id = "book".
     * @return devuelve una lista de libros leyendo los nodos del XML que se le pasa en el id
     */

    private fun obtenerListaNodosPorNombre(doc: Document, tagName: String): MutableList<Node> {
        val bookList: NodeList = doc.getElementsByTagName(tagName)
        val lista = mutableListOf<Node>()
        for (i in 0..bookList.length - 1)
            lista.add(bookList.item(i))
        return lista
    }

    /**
     * @param la función [obtenerAtributosEnMapKV] lee cada ELEMENT posterior a la id "book"
     * @return devuelve un mapa los nodos de cada libro: author, title, tema ect.
     */

    fun obtenerAtributosEnMapKV(e: Element): MutableMap<String, String> {
        val mMap = mutableMapOf<String, String>()
        for (j in 0..e.attributes.length - 1)
            mMap.putIfAbsent(e.attributes.item(j).nodeName, e.attributes.item(j).nodeValue)
        return mMap
    }

    /**
     *@param la función [existeLibro] usa dos funciones anteriormente creada, primero comprueba si el parametro es nulo
     * o está en blanco. En el caso de que pase un string que coincida, busca con la función [obtenerListaNodosPorNombre]
     * luego en nodos del libro si el primer elemento (index of first) es igual a un nodo de elemento comprueba si la id es igual
     * a la id del elemento.
     * @return  Devuelve true si existe, `false` en caso contrario.
     * */
    fun existeLibro(idLibro: String): Boolean {
        var existe: Boolean
        if (idLibro.isNullOrBlank())
            existe = false
        else {
            var encontrado = xmlDoc?.let {
                var nodosLibro = obtenerListaNodosPorNombre(it, "book")
                ((nodosLibro.indexOfFirst {
                    if (it.getNodeType() === Node.ELEMENT_NODE) {
                        val elem = it as Element
                        obtenerAtributosEnMapKV(elem)["id"] == idLibro
                    } else
                        false
                }) >= 0)
            }
            existe = (encontrado != null && encontrado)
        }
        return existe
    }

    /**
     *@param la función [infoLibro] sirve para crear la información del libro. Primero comprueba si la id es nula
     * o en blanco, luego si existe lee la id "book" que coincida con la idque se le  pasa en el amin y luego crea
     * un mapa de cada elemento del XML que coincida con la id que se le pasa.
     * @return  Devuelve un mapa con el valor de cada elemento del libro.
     * */
    fun infoLibro(idLibro: String): Map<String, Any> {
        var m = mutableMapOf<String, Any>()
        if (!idLibro.isNullOrBlank())
            xmlDoc?.let {
                var nodosLibro = obtenerListaNodosPorNombre(it, "book")

                var posicionDelLibro = nodosLibro.indexOfFirst {
                    if (it.getNodeType() === Node.ELEMENT_NODE) {
                        val elem = it as Element
                        obtenerAtributosEnMapKV(elem)["id"] == idLibro
                    } else false
                }
                if (posicionDelLibro >= 0) {
                    if (nodosLibro[posicionDelLibro].getNodeType() === Node.ELEMENT_NODE) {
                        val elem = nodosLibro[posicionDelLibro] as Element
                        m.put("id", idLibro)
                        m.put("author", elem.getElementsByTagName("author").item(0).textContent)
                        m.put("genre", elem.getElementsByTagName("genre").item(0).textContent)
                        m.put("price", elem.getElementsByTagName("price").item(0).textContent.toDouble())
                        m.put(
                            "publish_date",
                            LocalDate.parse(elem.getElementsByTagName("publish_date").item(0).textContent)
                        )
                        m.put("description", elem.getElementsByTagName("description").item(0).textContent)
                    }
                }
            }
        return m
    }

    /**
     * @param esta función solo sirve para obtener el resultado en pantalla a través de log en vez de usar println.
     */
    fun i(msg: String) {
        l.info { msg }
    }
}

fun main() {
    var portatil = "/home/edu/IdeaProjects/IESRA-DAM-Prog/ejercicios/src/main/kotlin/un5/eje5_4/Catalog.xml"
    var casa =
        "/home/usuario/Documentos/workspace/IdeaProjects/IESRA-DAM/ejercicios/src/main/kotlin/un5/eje5_4/Catalog.xml"
    var cat = CatalogoLibrosXML(casa)
    var id = "bk105"
    cat.i(cat.existeLibro(id).toString())
    cat.i(cat.infoLibro(id).toString())
}


