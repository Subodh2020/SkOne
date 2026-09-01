package io.skone.ui.overlay

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class SKOverlayComponentsTest {

    @Test
    fun snackbar_actionRequiresLabel() {
        val snack = SKSnackbarComponent.create(id = "s1", message = "Saved", actionLabel = null)
        snack.performAction()
        snack.setActionLabel("Undo")
        snack.performAction()
        assertEquals("Undo", snack.actionLabel)
        snack.setVisible(false)
        assertFalse(snack.visible)
    }

    @Test
    fun alertDialog_confirmHides() {
        val alert = SKAlertDialogComponent.create(
            id = "a1",
            title = "Delete?",
            message = "Cannot undo",
            dismissLabel = null,
        )
        assertTrue(alert.visible)
        assertEquals(null, alert.dismissLabel)
        alert.confirm()
        assertFalse(alert.visible)
    }

    @Test
    fun dialog_dismiss() {
        val dialog = SKDialogComponent.create(id = "d1", title = "Info")
        dialog.dismiss()
        assertFalse(dialog.visible)
    }
}
