package cz.mendelu.pef.xchomo.filmbuddy.ui.theme.elements

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import cz.mendelu.pef.xchomo.filmbuddy.R

@Composable
fun DeleteConfirmationDialog(
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(text = stringResource(R.string.delete_confirmation)) },
        text = { Text(text = stringResource(R.string.sure_delete)) },
        confirmButton = {
                Button(
                    onClick = onConfirm,
                ) {
                    Text(text = stringResource(R.string.delete))
                }
        },
        dismissButton = {
                Button(
                    onClick = onDismiss
                ) {
                    Text(stringResource(R.string.cancel))
                }

        }
    )
}