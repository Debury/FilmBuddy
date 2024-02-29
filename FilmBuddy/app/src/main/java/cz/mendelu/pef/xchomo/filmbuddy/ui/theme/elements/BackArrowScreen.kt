package cz.mendelu.pef.xchomo.filmbuddy.ui.theme.elements

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BackArrowScreen(
    topBarTitle: String,
    onBackClick: () -> Unit,
    drawFullScreenContent: Boolean = false,
    showCheckbox: Boolean = false,
    isChecked: Boolean = false,
    onDeleteClick: () -> Unit = {},
    onCheckedChange: (Boolean) -> Unit = {},
    content: @Composable (paddingValues: PaddingValues) -> Unit,
) {
    var isDeleteDialogOpen by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(text = topBarTitle)
                },
                navigationIcon = {
                    IconButton(onClick = { onBackClick() }) {
                        Icon(imageVector = Icons.Filled.ArrowBack, contentDescription = "")
                    }
                },
                actions = {
                    if (showCheckbox) {
                        Checkbox(
                            checked = isChecked,
                            onCheckedChange = onCheckedChange,
                            modifier = Modifier.padding(horizontal = 24.dp)
                        )
                        IconButton(
                            onClick = { isDeleteDialogOpen = true },
                            modifier = Modifier.padding(horizontal = 16.dp)
                        ) {
                            Icon(
                                Icons.Default.Delete,
                                contentDescription = "Delete",
                                tint = MaterialTheme.colorScheme.error
                            )
                        }
                    }
                }
            )
        }
    ) {
        if (!drawFullScreenContent) {
            LazyColumn(modifier = Modifier.padding(it)) {
                item {
                    content(it)
                }
            }
        } else {
            content(it)
        }
    }

    if (isDeleteDialogOpen) {
        DeleteConfirmationDialog(
            onConfirm = {
                onDeleteClick()
                isDeleteDialogOpen = false
            },
            onDismiss = { isDeleteDialogOpen = false }
        )
    }
}
