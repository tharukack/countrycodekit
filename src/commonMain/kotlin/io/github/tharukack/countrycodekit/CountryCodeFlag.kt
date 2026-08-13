package io.github.tharukack.countrycodekit

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.github.tharukack.countrycodekit.internal.flagResourceFor
import org.jetbrains.compose.resources.painterResource

/** Displays the bundled PNG flag, or an ISO-code fallback for custom countries. */
@Composable
fun CountryCodeFlag(
    country: CountryCode,
    modifier: Modifier = Modifier,
    width: Dp = 28.dp,
    height: Dp = 21.dp,
    style: CountryCodeFlagStyle = CountryCodeFlagStyle.Rounded,
) {
    val shape = when (style) {
        CountryCodeFlagStyle.Rounded -> RoundedCornerShape(4.dp)
        CountryCodeFlagStyle.Circle -> CircleShape
    }
    val circleDiameter = (if (width < height) width else height) + 2.dp
    val renderedWidth = if (style == CountryCodeFlagStyle.Circle) circleDiameter else width
    val renderedHeight = if (style == CountryCodeFlagStyle.Circle) circleDiameter else height
    val resource = flagResourceFor(country.isoCode)
    Box(
        modifier = modifier
            .size(renderedWidth, renderedHeight)
            .clip(shape)
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .border(0.5.dp, Color.Black.copy(alpha = 0.12f), shape),
        contentAlignment = Alignment.Center,
    ) {
        if (resource != null) {
            Image(
                painter = painterResource(resource),
                contentDescription = "${country.name} flag",
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop,
            )
        } else {
            Text(
                text = country.isoCode.uppercase().take(3),
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontSize = 9.sp,
            )
        }
    }
}
