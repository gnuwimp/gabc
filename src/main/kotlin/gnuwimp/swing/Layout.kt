/*
 * Copyright 2016 - 2024 gnuwimp@gmail.com
 * Released under the GNU General Public License v3.0
 */

package gnuwimp.swing

import java.awt.Component
import java.awt.Container
import java.awt.Dimension
import java.awt.LayoutManager
import javax.swing.JPanel

/**
 * Use a callback to do manual layout.
 */
class ManualLayout(val doLayout: (parent: Container) -> Unit) : LayoutManager {
    override fun addLayoutComponent(name: String, comp: Component) {}
    override fun minimumLayoutSize(parent: Container?) = Dimension(Swing.defFont.size, Swing.defFont.size)
    override fun preferredLayoutSize(parent: Container?) = Dimension(Swing.defFont.size, Swing.defFont.size)
    override fun removeLayoutComponent(comp: Component?) { }

    //--------------------------------------------------------------------------
    override fun layoutContainer(parent: Container?) {
        if (parent == null) {
            return
        }
        else {
            doLayout(parent)
        }
    }
}

//------------------------------------------------------------------------------
//------------------------------------------------------------------------------
//------------------------------------------------------------------------------

/**
 * Store widget and its grid size
 */
data class Widget(val comp: Component, val x: Int, val y: Int, val w: Int, val h: Int)

//------------------------------------------------------------------------------
//------------------------------------------------------------------------------
//------------------------------------------------------------------------------

/**
 * Resize all child widgets by using a grid.
 */
class Layout(val size: Int) : LayoutManager {
    private var x = -1
    private var y = -1
    private var width = -1
    private var height = -1
    private var widgets = arrayListOf<Widget>()

    override fun addLayoutComponent(name: String, comp: Component) {}
    override fun minimumLayoutSize(parent: Container?) = Dimension(14, 14)
    override fun preferredLayoutSize(parent: Container?) = Dimension(14, 14)
    override fun removeLayoutComponent(comp: Component?) { }

    /**
     * Place all child widgets according to its setting
     */
    override fun layoutContainer(parent: Container?) {
        if (parent == null) {
            return
        }
        else {
            for (c in widgets) {
                val x1 = if (c.x >= 0) {
                    c.x * size
                }
                else {
                    parent.width + c.x * size
                }

                val y1 = if (c.y >= 0) {
                    c.y * size
                }
                else {
                    parent.height + c.y * size
                }

                val x2 = when {
                    c.w == 0 -> parent.width
                    c.w > 0 -> x1 + c.w * size
                    else -> parent.width + c.w * size
                }

                val y2 = when {
                    c.h == 0 -> parent.height
                    c.h > 0 -> y1 + c.h * size
                    else -> parent.height + c.h * size
                }

                val w1 = x2 - x1
                val h1 = y2 - y1
                c.comp.resize(x1, y1, w1, h1)
            }
        }
    }

    /**
     * Add widget to list.
     */
    fun add(component: Component, x: Int, y: Int, w: Int, h: Int) {
        widgets.add(Widget(component, x, y, w, h))
    }
}

/**
 * A layout panel that uses a grid to lay out its child widgets.
 */
open class LayoutPanel(size: Int = Swing.defFont.size) : JPanel() {
    private val layoutManager = Layout(size)

    init {
        layout = layoutManager
    }

    /**
     * Add widget to layout.
     * x = 0 means start from grid 0.
     * x = -2 means start from grid width - 2.
     * w = 0 means use all available width.
     * w = -2 means use all available width from width - 2.
     * w = 2 means use width grid 2.
     */
    fun add(widget: Component, x: Int, y: Int, w: Int, h: Int) {
        add(widget)
        layoutManager.add(widget, x, y, w, h)
    }
}
