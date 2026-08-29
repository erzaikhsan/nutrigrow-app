package com.project.labs.nutrigrow.ui.component.input

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.VisualTransformation
import com.project.labs.nutrigrow.ui.theme.BrandGreen
import com.project.labs.nutrigrow.ui.theme.FieldOutline
import com.project.labs.nutrigrow.ui.theme.Radius
import com.project.labs.nutrigrow.ui.theme.Spacing
import com.project.labs.nutrigrow.ui.theme.SurfaceCard

@Composable
fun NutriTextField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    label: String? = null,
    placeholder: String? = null,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    singleLine: Boolean = true,
    enabled: Boolean = true,
    readOnly: Boolean = false,
    isError: Boolean = false,
    errorMessage: String? = null,
    trailingIcon: @Composable (() -> Unit)? = null,
    leadingIcon: @Composable (() -> Unit)? = null,
) {
    Column(modifier = modifier.fillMaxWidth()) {
        if (label != null) {
            Text(
                text = label,
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(bottom = Spacing.xs),
            )
        }
        TextField(
            value = value,
            onValueChange = onValueChange,
            shape = RoundedCornerShape(Radius.sm),
            placeholder = placeholder?.let {
                { Text(text = it, style = MaterialTheme.typography.titleMedium) }
            },
            keyboardOptions = keyboardOptions,
            visualTransformation = visualTransformation,
            singleLine = singleLine,
            enabled = enabled,
            readOnly = readOnly,
            isError = isError,
            trailingIcon = trailingIcon,
            leadingIcon = leadingIcon,
            colors = TextFieldDefaults.colors(
                focusedContainerColor = SurfaceCard,
                unfocusedContainerColor = SurfaceCard,
                disabledContainerColor = SurfaceCard,
                focusedIndicatorColor = BrandGreen,
                unfocusedIndicatorColor = FieldOutline,
                disabledIndicatorColor = FieldOutline,
            ),
            modifier = Modifier.fillMaxWidth(),
        )
        if (isError && errorMessage != null) {
            Text(
                text = errorMessage,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.padding(top = Spacing.xs),
            )
        }
    }
}

@Composable
fun NutriSearchField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    modifier: Modifier = Modifier,
) {
    TextField(
        value = value,
        onValueChange = onValueChange,
        shape = RoundedCornerShape(Radius.sm),
        placeholder = { Text(text = placeholder, style = MaterialTheme.typography.titleMedium) },
        singleLine = true,
        keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Text),
        colors = TextFieldDefaults.colors(
            focusedContainerColor = SurfaceCard,
            unfocusedContainerColor = SurfaceCard,
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
            disabledIndicatorColor = Color.Transparent,
        ),
        modifier = modifier.fillMaxWidth(),
    )
}
