package com.lohni.darts.ui.composable

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowDropDown
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.Icon
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun <T> CustomDropdown(
    modifier: Modifier,
    initialItem: T,
    itemText: (T) -> String,
    selectOptions: State<Collection<T>>,
    enabled: Boolean = true,
    onSelect: (T) -> Unit
) {
    CustomDropdown(modifier, initialItem, itemText, selectOptions.value, enabled, onSelect)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun <T> CustomDropdown(
    modifier: Modifier,
    initialItem: T,
    itemText: (T) -> String,
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
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier
                .height(50.dp)
                .clip(shape = RoundedCornerShape(5.dp))
                .menuAnchor(MenuAnchorType.PrimaryEditable),
        ) {
            val alpha = if (enabled) 1f else 0.6f
            Text(
                text = itemText.invoke(initialItem),
                fontSize = 18.sp,
                maxLines = 1,
                textAlign = TextAlign.Start,
                modifier = Modifier.alpha(alpha)
            )
            Icon(
                imageVector = Icons.Rounded.ArrowDropDown,
                contentDescription = "Dropdown",
                modifier = Modifier.padding(end = 4.dp).alpha(alpha)
            )
        }
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