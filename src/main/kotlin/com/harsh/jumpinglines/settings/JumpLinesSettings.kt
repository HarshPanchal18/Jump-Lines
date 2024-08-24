package com.harsh.jumpinglines.settings

import com.harsh.jumpinglines.utils.Const
import com.harsh.jumpinglines.utils.NumberOfBackwardLines
import com.harsh.jumpinglines.utils.NumberOfForwardLines
import com.harsh.jumpinglines.utils.properties
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.options.Configurable
import javax.swing.JComponent

class JumpLinesSettings : Configurable {

    private val settingsPanel = JumpLinesSettingPanel()
    private var savedNumberOfFLines: Int = 0
    private var savedNumberOfBLines: Int = 0

    override fun createComponent(): JComponent {
        return settingsPanel.parentPanel
    }

    override fun isModified(): Boolean {
        return settingsPanel.getForwardLines() != savedNumberOfFLines ||
                settingsPanel.getBackwardLines() != savedNumberOfBLines
    }

    override fun apply() {
        val newForwardNumberOfLines: Int = settingsPanel.getForwardLines()
        val newBackwardNumberOfLines: Int = settingsPanel.getBackwardLines()

        ApplicationManager.getApplication().runWriteAction {
            // Set value or unset if equals to default value
            properties().setValue(Const.FORWARD_LINES, newForwardNumberOfLines, 4)
            properties().setValue(Const.BACKWARD_LINES, newBackwardNumberOfLines, 2)
        }

        // Save the value to plugin's settings
        savedNumberOfFLines = newForwardNumberOfLines
        savedNumberOfBLines = newBackwardNumberOfLines
    }

    override fun getDisplayName(): String {
        return "Jumping Lines Settings" // Display name in the settings dialog
    }

    override fun reset() {
        // Set default values
        settingsPanel.setNumberOfLines(NumberOfForwardLines, NumberOfBackwardLines)
        savedNumberOfFLines = NumberOfForwardLines
        savedNumberOfBLines = NumberOfBackwardLines
    }
}