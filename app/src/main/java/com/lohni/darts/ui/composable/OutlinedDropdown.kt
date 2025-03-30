package com.lohni.darts.ui.composable

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.height
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowDropDown
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.Icon
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun <T> OutlinedDropdown(
    modifier: Modifier,
    initialItem: T,
    itemText: (T) -> String,
    inputLabel: String = "Select",
    selectOptions: State<Collection<T>>,
    enabled: Boolean = true,
    onSelect: (T) -> Unit
) {
    OutlinedDropdown(modifier, initialItem, itemText, inputLabel, selectOptions.value, enabled, onSelect)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun <T> OutlinedDropdown(
    modifier: Modifier,
    initialItem: T,
    itemText: (T) -> String,
    inputLabel: String = "Select",
    selectOptions: Collection<T>,
    enabled: Boolean = true,
    onSelect: (T) -> Unit
) {
    var dropdownExpanded by remember { mutableStateOf(false) }
    ExposedDropdownMenuBox(
        expanded = dropdownExpanded,
        onExpandedChange = { if (enabled) dropdownExpanded = it },
        modifier = modifier
            .clickable {
                if (!selectOptions.isEmpty() && enabled) dropdownExpanded = true
            }
    ) {
        OutlinedTextField(
            value = itemText.invoke(initialItem),
            readOnly = true,
            onValueChange = {},
            singleLine = true,
            enabled = enabled,
            label = { Text(inputLabel) },
            modifier = Modifier
                .height(60.dp)
                .menuAnchor(MenuAnchorType.PrimaryEditable),
            trailingIcon = {
                Icon(
                    Icons.Rounded.ArrowDropDown,
                    contentDescription = "Select",
                )
            })
        ExposedDropdownMenu(
            expanded = dropdownExpanded,
            onDismissRequest = { dropdownExpanded = false }
        ) {
            selectOptions.forEach {
                DropdownMenuItem(
                    text = { Text(itemText.invoke(it)) },
                    onClick = {
                        dropdownExpanded = false
                        onSelect.invoke(it)
                    }
                )
            }
        }
    }
}