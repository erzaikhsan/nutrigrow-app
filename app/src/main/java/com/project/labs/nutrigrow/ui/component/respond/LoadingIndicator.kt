package com.project.labs.nutrigrow.ui.component.respond

import android.os.Build.VERSION.SDK_INT
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import coil.ImageLoader
import coil.compose.AsyncImage
import coil.decode.GifDecoder
import coil.decode.ImageDecoderDecoder
import com.project.labs.nutrigrow.R

@Composable
fun LoadingIndicator(
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    Box(
        modifier = Modifier
            .fillMaxSize()
            .wrapContentHeight(Alignment.CenterVertically)
            .wrapContentWidth(Alignment.CenterHorizontally)
    ) {
        val gifEnabledLoader = ImageLoader.Builder(context)
            .components {
                if ( SDK_INT >= 28 ) {
                    add(ImageDecoderDecoder.Factory())
                } else {
                    add(GifDecoder.Factory())
                }
            }.build()
        AsyncImage(
            imageLoader = gifEnabledLoader,
            model = R.drawable.loading_baby,
            contentDescription = "Memuat",
            modifier = modifier
                .size(100.dp)
                .padding(16.dp)
                .wrapContentWidth(Alignment.CenterHorizontally)
                .wrapContentHeight(Alignment.CenterVertically)
        )
    }
}