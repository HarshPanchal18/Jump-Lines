package com.harsh.jumpinglines.utils

import com.intellij.ide.plugins.PluginManagerCore
import com.intellij.ide.util.PropertiesComponent
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.openapi.editor.Document
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.editor.FoldingModel
import com.intellij.openapi.extensions.PluginId

val AnActionEvent.editor: Editor
    get() = this.getRequiredData(CommonDataKeys.EDITOR)

fun properties(): PropertiesComponent = PropertiesComponent.getInstance()

val NumberOfForwardLines: Int
    get() = properties().getInt(Const.FORWARD_LINES, 4)

val NumberOfBackwardLines: Int
    get() = properties().getInt(Const.BACKWARD_LINES, 2)

val jumpScore: Long
    get() = properties().getLong(Const.JUMP_SCORE, 0)

fun Long.inHumanReadableForm(): String {
    return when {
        this >= 1_000_000_000 -> String.format("%.2fB", this / 1_000_000_000.0)
        this >= 1_000_000 -> String.format("%.2fM", this / 1_000_000.0)
        this >= 1_00_000 -> String.format("%.2fL", this / 1_00_000.0)
        this >= 1_000 -> String.format("%.2fK", this / 1_000.0)
        else -> this.toString()
    }
}

fun increaseJumpScoreBy(score: Int) {
    properties().setValue(Const.JUMP_SCORE, (jumpScore + score).toString())
}

fun getPluginVersion(): String? {
    val pluginId = PluginId.getId("com.example.JumpingLines")
    val pluginDescriptor = PluginManagerCore.getPlugin(pluginId)

    return pluginDescriptor?.version
}

fun calculateForwardOffset(document: Document, foldingModel: FoldingModel, currentOffset: Int, linesToJump: Int): Int {

    var linesJumped = 0
    var targetOffset = currentOffset

    foldingModel.runBatchFoldingOperation {
        while (linesJumped < linesToJump) {
            val currentLineNumber = document.getLineNumber(targetOffset)
            val newLineNumber = currentLineNumber + 1

            if (newLineNumber >= document.lineCount)
                break

            val newOffset = document.getLineStartOffset(newLineNumber)
            val foldRegion = foldingModel.getCollapsedRegionAtOffset(newOffset)

            if (foldRegion != null) {
                // Skip the folded region
                targetOffset = foldRegion.endOffset

                // If the folded region ends at the last line of the document, stop jumping
                if (document.getLineNumber(targetOffset) >= document.lineCount - 1)
                    break

            } else {
                // move to the next line
                targetOffset = newOffset
                linesJumped++
            }
        }
    }

    return targetOffset
}

fun calculateBackwardOffset(document: Document, foldingModel: FoldingModel, currentOffset: Int, linesToJump: Int): Int {

    var linesJumped = 0
    var targetOffset = currentOffset

    foldingModel.runBatchFoldingOperation {
        while (linesJumped < linesToJump) {
            val currentLineNumber = document.getLineNumber(targetOffset)
            val newLineNumber = currentLineNumber - 1

            if (newLineNumber < 0)
                break

            val newOffset = document.getLineEndOffset(newLineNumber)
            val foldRegion = foldingModel.getCollapsedRegionAtOffset(newOffset)

            if (foldRegion != null) {
                // Skip the folded region
                targetOffset = foldRegion.startOffset

                // If the folded region ends at the last line of the document, stop jumping
                if (document.getLineNumber(targetOffset) <= 0)
                    break

            } else {
                // move to the next line
                targetOffset = newOffset
                linesJumped++
            }
        }
    }

    return targetOffset
}