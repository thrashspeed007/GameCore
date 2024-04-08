package com.thrashspeed.gamecore.utils.igdb

class IgdbQuery {
    private val queryBuilder = StringBuilder()

    fun addFields(fields: List<String>): IgdbQuery {
        appendQuery("fields ${fields.joinToString( separator = ", " )};")
        return this
    }

    fun addSortBy(field: IgdbSortOptions, order: SortOrder = SortOrder.DESCENDING): IgdbQuery {
        appendQuery("sort ${field.sortOption} ${order.sortOrder};")
        return this
    }

    fun addWhereClause(vararg conditions: String): IgdbQuery {
        val nonEmptyConditions = conditions.filter { it.isNotBlank() }

        if (nonEmptyConditions.isNotEmpty()) {
            val whereClause = nonEmptyConditions.joinToString(" & ", prefix = "where ", postfix = ";")
            appendQuery(whereClause)
        }

        return this
    }

    fun addLimit(limit: Int): IgdbQuery {
        appendQuery("limit $limit;")
        return this
    }

    fun buildQuery(): String {
        return queryBuilder.toString()
    }

    private fun appendQuery(queryPart: String) {
        if (queryBuilder.isNotEmpty()) {
            queryBuilder.append(" ")
        }
        queryBuilder.append(queryPart)
    }

    enum class SortOrder(val sortOrder: String) {
        ASCENDING("asc"),
        DESCENDING("desc")
    }
}