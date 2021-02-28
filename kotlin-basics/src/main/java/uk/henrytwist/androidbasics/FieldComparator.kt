package uk.henrytwist.androidbasics

inline fun <T> fieldComparator(builder: FieldComparator<T>.() -> Unit): FieldComparator<T> {

    val comparator = FieldComparator<T>()
    builder(comparator)
    return comparator
}

inline fun <T> Iterable<T>.sortedWithFieldComparator(builder: FieldComparator<T>.() -> Unit): List<T> {

    return sortedWith(fieldComparator(builder))
}

abstract class FieldComparatorBuilder<T> {

    val comparators = mutableListOf<Comparator<T>>()

    fun addField(nullsFirst: Boolean = false, descending: Boolean = false, selector: (T) -> Comparable<*>?) {

        comparators.add(Field(nullsFirst, descending, selector))
    }

    inline fun addGroup(builder: Group<T>.() -> Unit) {

        val group = Group<T>()
        builder(group)
        comparators.add(group)
    }

    protected fun aggregateCompare(p0: T, p1: T): Int {

        comparators.forEach {

            val diff = it.compare(p0, p1)
            if (diff != 0) return diff
        }

        return 0
    }
}

class FieldComparator<T> : FieldComparatorBuilder<T>(), Comparator<T> {

    override fun compare(p0: T, p1: T): Int {

        return aggregateCompare(p0, p1)
    }
}

class Field<T>(private val nullsFirst: Boolean, private val descending: Boolean, private val selector: (T) -> Comparable<*>?) : Comparator<T> {

    override fun compare(p0: T, p1: T): Int {

        val p0Field = selector(p0)
        val p1Field = selector(p1)

        if (p0Field == null && p1Field == null) {
            return 0
        } else if (p0Field != null && p1Field != null) {
            @Suppress("UNCHECKED_CAST")
            return (if (descending) -1 else 1) * (p0Field as Comparable<Any>).compareTo(p1Field)
        } else {
            return if ((p0Field == null) == nullsFirst) -1 else 1
        }
    }
}

class Group<T> : FieldComparatorBuilder<T>(), Comparator<T> {

    internal var predicate: (T) -> Boolean = { false }

    fun predicate(p: (T) -> Boolean) {

        predicate = p
    }

    override fun compare(p0: T, p1: T): Int {

        val p0InGroup = predicate(p0)
        val p1InGroup = predicate(p1)

        return if (p0InGroup != p1InGroup) {

            if (p0InGroup) -1 else 1
        } else if (!p0InGroup && !p1InGroup) {

            0
        } else {

            aggregateCompare(p0, p1)
        }
    }
}