/*
 * Copyright 2016 - 2024 gnuwimp@gmail.com
 * Released under the GNU General Public License v3.0
 */

package gnuwimp.swing

import java.awt.Color
import java.awt.Component
import java.awt.event.MouseAdapter
import java.awt.event.MouseEvent
import javax.swing.JTable
import javax.swing.table.AbstractTableModel
import javax.swing.table.DefaultTableCellRenderer
import javax.swing.table.JTableHeader
import javax.swing.table.TableCellRenderer

/**
 * Adapter class for receiving click events when a table header is clicked
 */
abstract class TableHeader : MouseAdapter() {
    /**
     * Column index that has been clicked on or 99
     */
    fun columnIndex(event: MouseEvent?): Int {
        return if (event != null) {
            val header      = event.source as JTableHeader
            val columnModel = header.columnModel
            val indexAtX    = columnModel.getColumnIndexAtX(event.x)

            columnModel.getColumn(indexAtX).modelIndex
        }
        else
            99
    }

    /**
     * True if control key has been pressed while clicking table header
     */
    fun isControlDown(event: MouseEvent?) = event != null && event.isControlDown
}

//------------------------------------------------------------------------------
//------------------------------------------------------------------------------
//------------------------------------------------------------------------------

/**
 * A table class for coloring rows in table view
 */
open class Table : JTable() {
    /**
     * Update view when data has been changed
     */
    fun fireModel() {
        (this.model as AbstractTableModel).fireTableDataChanged()
    }

    /**
     * Get background color, default color when not overrided
     */
    open fun getRowBgColor(row: Int, column: Int): Color = background

    /**
     * Get foreground color, default color when not overrided
     */
    open fun getRowFgColor(row: Int, column: Int): Color = foreground

    /**
     * Create component with foreground and background color set
     */
    override fun prepareRenderer(renderer: TableCellRenderer, row: Int, column: Int): Component {
        val component = super.prepareRenderer(renderer, row, column)

        return if (isRowSelected(row))
            component
        else {
            val r = convertRowIndexToModel(row)
            component.background = getRowBgColor(r, column)
            component.foreground = getRowFgColor(r, column)
            component
        }
    }

    /**
     * Get or set selected row in table
     */
    var selectRow: Int
        get() = selectionModel.minSelectionIndex

        set(value) {
            if (value < 0) {
                selectionModel.clearSelection()
            }
            else {
                selectionModel.setSelectionInterval(value, value)
            }
        }

    /**
     * Align text in column, but not in header
     * Text align values are SwingConstants.LEFT, SwingConstants.CENTER or SwingConstants.RIGHT
     */
    fun setColumnAlign(column: Int, align: Int) {
        val rend = DefaultTableCellRenderer()
        rend.horizontalAlignment = align
        this.columnModel.getColumn(column).cellRenderer = rend
    }

    /**
     * Set width of column
     */
    fun setColumnWidth(column: Int, min: Int, pref: Int, max: Int) {
        columnModel.getColumn(column).minWidth       = min
        columnModel.getColumn(column).preferredWidth = pref
        columnModel.getColumn(column).maxWidth       = max
    }
}
