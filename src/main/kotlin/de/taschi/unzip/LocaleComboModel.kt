package de.taschi.unzip

import java.nio.charset.Charset
import javax.swing.ComboBoxModel
import javax.swing.event.ListDataListener

class LocaleComboModel: ComboBoxModel<Charset> {

    var selectedIdx = -1

    private val elements = Charset.availableCharsets().values.sortedBy { it.displayName() }

    override fun getSize(): Int = elements.size

    override fun getElementAt(index: Int): Charset = elements[index]

    override fun addListDataListener(l: ListDataListener?) {
        // noop - list data never changes, thus listeners never fire
    }

    override fun removeListDataListener(l: ListDataListener?) {
        // noop - list data never changes, thus listeners never fire
    }

    override fun setSelectedItem(anItem: Any?) {
        // this will set selectedIdx back to 1
        selectedIdx = elements.indexOf(anItem)
    }

    override fun getSelectedItem(): Any? {
        return if (selectedIdx >= 0) {
            elements[selectedIdx]
        } else {
            null
        }
    }

}
