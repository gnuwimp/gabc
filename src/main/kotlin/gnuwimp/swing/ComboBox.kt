/*
 * Copyright © 2016 - 2021 gnuwimp@gmail.com
 * Released under the GNU General Public License v3.0
 */

package gnuwimp.swing

import java.awt.event.ItemEvent
import java.awt.event.KeyEvent
import javax.swing.JComboBox

/**
 * A string combo box with callback for data changes from user.
 */
class ComboBox<T>(var strings: List<String>, index: Int = 0, val callback: ((String) -> Unit)? = null) : JComboBox<String>() {
    init {
        val parent = this
        setStrings(strings, index)

        //----------------------------------------------------------------------
        addItemListener { itemEvent ->
            val value = itemEvent.item.toString()

            if (itemEvent.stateChange == ItemEvent.SELECTED && value.isNotBlank()) {
                callback?.invoke(parent.text)
            }
        }

        //----------------------------------------------------------------------
        editor.editorComponent.addKeyListener(object : java.awt.event.KeyListener {
            override fun keyTyped(e: KeyEvent?) {}

            override fun keyPressed(e: KeyEvent?) {}

            override fun keyReleased(e: KeyEvent?) {
                callback?.invoke(parent.text)
            }
        })
    }

    /**
     * Add strings to data model for combobox
     */
    fun setStrings(strings: List<String>, selected: Int = 0) {
        val myModel = javax.swing.DefaultComboBoxModel<String>()

        for (string in strings) {
            myModel.addElement(string)
        }

        model = myModel

        if (selected in 0 until itemCount) {
            selectedIndex = selected
        }
    }

    /**
     * Set or get text from combobox.
     */
    var text: String
        get() = if (isEditable) editor.item.toString() else strings[selectedIndex]

        set(value) {
            editor.item = value
        }
}
