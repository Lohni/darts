package com.lohni.darts.ui.composable

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog

@Composable
fun InfoDialog(
    show: Boolean,
    title: String = "Info",
    proceedLabel: String = "Proceed",
    onDissmiss: () -> Unit,
    content: @Composable RowScope.() -> Unit
) {
    if (show) {
        Dialog(onDismissRequest = { onDissmiss.invoke() }) {
            Card {
                Column(
                    modifier = Modifier.padding(
                        top = 24.dp,
                        start = 24.dp,
                        end = 24.dp,
                        bottom = 8.dp
                    )
                ) {
                    Row(
                        horizontalArrangement = Arrangement.Center,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                title,
                                fontSize = 22.sp,
                                modifier = Modifier.padding(top = 8.dp)
                            )
                        }
                    }
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 32.dp)
                    ) {
                        content.invoke(this)
                    }

                    Row(
                        modifier = Modifier
                            .align(Alignment.End)
                            .padding(top = 16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.End
                    ) {
                        TextButton(
                            onClick = {
                                onDissmiss.invoke()
                            }
                        ) {
                            Text(proceedLabel)
                        }
                    }
                }
            }
        }
    }
}
