package com.harsh.jumpinglines.jumps

import com.harsh.jumpinglines.notification.showNotification
import com.harsh.jumpinglines.utils.editor
import com.intellij.ide.util.PropertiesComponent
import com.intellij.openapi.actionSystem.ActionUpdateThread
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.editor.*
import com.intellij.openapi.project.DumbAwareAction

class JumpForwardLines : DumbAwareAction() {

	override fun actionPerformed(event: AnActionEvent) {

		event.project ?: return

		try {

			val editor: Editor = event.editor
			val document: Document = editor.document
			val caretModel: CaretModel = editor.caretModel
			val currentOffset: Int = caretModel.offset
			val scrollingModel: ScrollingModel = editor.scrollingModel
			val selectionModel: SelectionModel = editor.selectionModel

			val properties = PropertiesComponent.getInstance()
			val currentForwardNoOfLines = properties.getValue("JumpLines.NumberOfFLines", "4").toInt()

			// Calculate the new caret position
			val currentLineNumber: Int = document.getLineNumber(currentOffset)
			val newLineNumber: Int = currentLineNumber + currentForwardNoOfLines
			val currentColumn = currentOffset - document.getLineStartOffset(currentLineNumber)

			// Ensure the new line number is within valid bounds
			val validLineNumber: Int = newLineNumber.coerceIn(0, document.lineCount - 1)
			val newOffset: Int = document.getLineStartOffset(validLineNumber)
			caretModel.moveToOffset(newOffset)

			// Scrolling editor along with the cursor
			val newPosition = LogicalPosition(newLineNumber, currentColumn)
			caretModel.moveToLogicalPosition(newPosition)

			// Remove selection blocks before jumping
			if (selectionModel.hasSelection()) {
				selectionModel.removeSelection(/* allCarets = */ true)
			}

			scrollingModel.scrollTo(newPosition, ScrollType.RELATIVE)

		} catch (e: AssertionError) {
			showNotification("Nope, cursor can't jump outside the editor.")
		}

	}

	override fun update(e: AnActionEvent) {
		val project = e.project
		e.presentation.isEnabled = project != null
	}

	override fun getActionUpdateThread(): ActionUpdateThread {
		return ActionUpdateThread.EDT
	}
}