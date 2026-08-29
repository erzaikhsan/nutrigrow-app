package com.project.labs.nutrigrow.ui.component.input

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.project.labs.nutrigrow.R
import com.project.labs.nutrigrow.ui.theme.SurfaceCard

@Composable
fun SearchCard(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    modifier: Modifier = Modifier,
) {
    ElevatedCard(
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.elevatedCardColors(SurfaceCard),
        modifier = modifier
            .fillMaxWidth()
            .padding(start = 15.dp, end = 15.dp, top = 10.dp, bottom = 15.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .padding(end = 8.dp)
            ) {
                TextField(
                    value = value,
                    onValueChange = onValueChange,
                    shape = RoundedCornerShape(7.dp),
                    placeholder = { Text(placeholder, style = MaterialTheme.typography.titleMedium) },
                    singleLine = true,
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = SurfaceCard,
                        unfocusedContainerColor = SurfaceCard,
                        disabledContainerColor = SurfaceCard,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent,
                        disabledIndicatorColor = Color.Transparent,
                    ),
                    keyboardOptions = KeyboardOptions.Default.copy(
                        keyboardType = KeyboardType.Text,
                        imeAction = ImeAction.Search
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(55.dp)
                )
            }
            if (value.isBlank()) {
                Box(modifier = Modifier.padding(end = 12.dp)) {
                    Icon(
                        painter = painterResource(id = R.drawable.round_search_24),
                        contentDescription = "Cari",
                    )
                }
            } else {
                IconButton(onClick = { onValueChange("") }) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Hapus kata kunci pencarian",
                    )
                }
            }
        }
    }
}
