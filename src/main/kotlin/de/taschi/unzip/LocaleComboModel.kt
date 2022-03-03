package de.taschi.unzip

//    Locale Aware Unzipper
//    Copyright (C) 2022 S. Hillebrand <dev@hit-to-key.net>
//
//    This program is free software: you can redistribute it and/or modify
//    it under the terms of the GNU General Public License as published by
//    the Free Software Foundation, either version 3 of the License, or
//    (at your option) any later version.
//
//    This program is distributed in the hope that it will be useful,
//    but WITHOUT ANY WARRANTY; without even the implied warranty of
//    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
//    GNU General Public License for more details.
//
//    You should have received a copy of the GNU General Public License
//    along with this program.  If not, see <https://www.gnu.org/licenses/>.

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
