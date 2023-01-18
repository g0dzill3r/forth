/**
 * Implements variables storage.
 */

class ForthVariables {
    private var ptr = 1000
    private val addrs = mutableMapOf<String, Int> ()
    private val variables = mutableMapOf<Int, Int> ()

    fun has (variable: String): Int? = addrs [variable]

    fun map (variable: String): Int {
        return addrs[variable] ?: run {
            val addr = ptr ++
            addrs [variable] = addr
            variables [addr] = 0
            addr
        }
    }

    fun fetch (addr: Int): Int? = variables[addr]

    fun store (addr: Int, value: Int) {
        variables[addr] = value
        return
    }
}

// EOF