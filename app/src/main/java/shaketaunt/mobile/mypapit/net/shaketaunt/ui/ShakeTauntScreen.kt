package shaketaunt.mobile.mypapit.net.shaketaunt.ui

import android.graphics.BitmapFactory
import android.graphics.Shader
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageShader
import androidx.compose.ui.graphics.ShaderBrush
import androidx.compose.ui.graphics.TileMode
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.LocalResources
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import shaketaunt.mobile.mypapit.net.shaketaunt.BuildConfig
import shaketaunt.mobile.mypapit.net.shaketaunt.R
import shaketaunt.mobile.mypapit.net.shaketaunt.ui.theme.IronOutline
import shaketaunt.mobile.mypapit.net.shaketaunt.ui.theme.IronPrimaryDark
import shaketaunt.mobile.mypapit.net.shaketaunt.ui.theme.IronSurface
import shaketaunt.mobile.mypapit.net.shaketaunt.ui.theme.IronSurfaceVariant
import shaketaunt.mobile.mypapit.net.shaketaunt.ui.theme.PureWhite
import shaketaunt.mobile.mypapit.net.shaketaunt.ui.theme.ShakeTaunterTheme
import shaketaunt.mobile.mypapit.net.shaketaunt.ui.theme.SoftSilver
import kotlin.math.roundToInt

private val TopBarContainerColor = Color(0xCC1B1B1B)

@Composable
fun IronGripBackground(modifier: Modifier = Modifier) {
    val resources = LocalResources.current
    val shaderBrush = remember(resources) {
        val bitmap = BitmapFactory.decodeResource(resources, R.drawable.irongrip)
        val shader: Shader = ImageShader(
            image = bitmap.asImageBitmap(),
            tileModeX = TileMode.Repeated,
            tileModeY = TileMode.Repeated,
        )
        ShaderBrush(shader)
    }
    Canvas(modifier = modifier.fillMaxSize()) {
        drawRect(brush = shaderBrush)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShakeTauntScreen(
    uiState: ShakeTauntUiState,
    onManualTaunt: () -> Unit,
    onReplayTaunt: () -> Unit,
    onToggleMute: () -> Unit,
    onShowAbout: () -> Unit,
    onDismissAbout: () -> Unit,
    onOpenGitHub: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val hapticFeedback = LocalHapticFeedback.current
    val shakeOffsetX = remember { Animatable(0f) }

    LaunchedEffect(uiState.tauntGeneration) {
        if (uiState.tauntGeneration > 0) {
            val offsets = listOf(-18f, 18f, -12f, 12f, -6f, 0f)
            for (target in offsets) {
                shakeOffsetX.animateTo(
                    targetValue = target,
                    animationSpec = tween(durationMillis = 42),
                )
            }
        }
    }

    Box(modifier = modifier.fillMaxSize()) {
        IronGripBackground()

        Scaffold(
            containerColor = Color.Transparent,
            topBar = {
                TopAppBar(
                    title = {
                        Text(
                            text = stringResource(R.string.app_name),
                            style = MaterialTheme.typography.titleLarge,
                            color = PureWhite,
                        )
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = TopBarContainerColor,
                        titleContentColor = PureWhite,
                        actionIconContentColor = PureWhite,
                    ),
                    actions = {
                        IconButton(
                            onClick = onToggleMute,
                            modifier = Modifier.testTag("mute_button"),
                        ) {
                            Icon(
                                painter = painterResource(
                                    id = if (uiState.isMuted) {
                                        R.drawable.ic_volume_off
                                    } else {
                                        R.drawable.ic_volume_up
                                    },
                                ),
                                contentDescription = stringResource(
                                    id = if (uiState.isMuted) {
                                        R.string.action_unmute
                                    } else {
                                        R.string.action_mute
                                    },
                                ),
                                tint = PureWhite,
                            )
                        }
                        IconButton(
                            onClick = onShowAbout,
                            modifier = Modifier.testTag("about_button"),
                        ) {
                            Icon(
                                painter = painterResource(id = R.drawable.ic_info),
                                contentDescription = stringResource(R.string.help),
                                tint = PureWhite,
                            )
                        }
                    },
                )
            },
            bottomBar = {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .navigationBarsPadding()
                        .padding(horizontal = 24.dp, vertical = 20.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp, Alignment.CenterHorizontally),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    OutlinedButton(
                        onClick = {
                            hapticFeedback.performHapticFeedback(HapticFeedbackType.LongPress)
                            onManualTaunt()
                        },
                        modifier = Modifier.testTag("shake_button"),
                        colors = ButtonDefaults.outlinedButtonColors(
                            containerColor = IronSurfaceVariant.copy(alpha = 0.88f),
                            contentColor = PureWhite,
                        ),
                        border = BorderStroke(1.dp, IronOutline),
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_vibration),
                            contentDescription = null,
                            modifier = Modifier.size(18.dp),
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = stringResource(R.string.action_shake),
                            style = MaterialTheme.typography.labelLarge,
                        )
                    }

                    if (uiState.currentTaunt != null && !uiState.isMuted) {
                        OutlinedButton(
                            onClick = onReplayTaunt,
                            modifier = Modifier.testTag("replay_button"),
                            colors = ButtonDefaults.outlinedButtonColors(
                                containerColor = IronPrimaryDark.copy(alpha = 0.82f),
                                contentColor = SoftSilver,
                            ),
                            border = BorderStroke(1.dp, IronOutline.copy(alpha = 0.7f)),
                        ) {
                            Icon(
                                painter = painterResource(id = R.drawable.ic_replay),
                                contentDescription = null,
                                modifier = Modifier.size(18.dp),
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = stringResource(R.string.action_replay),
                                style = MaterialTheme.typography.labelLarge,
                            )
                        }
                    }
                }
            },
        ) { innerPadding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(horizontal = 24.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                val displayText = uiState.currentTaunt ?: stringResource(R.string.startup)

                AnimatedContent(
                    targetState = displayText,
                    transitionSpec = {
                        (fadeIn(animationSpec = tween(220)) + scaleIn(
                            initialScale = 0.94f,
                            animationSpec = tween(220),
                        )) togetherWith fadeOut(animationSpec = tween(140))
                    },
                    label = "TauntTransition",
                ) { text ->
                    Text(
                        text = text,
                        style = MaterialTheme.typography.displayMedium,
                        color = PureWhite,
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .offset { IntOffset(shakeOffsetX.value.roundToInt(), 0) }
                            .semantics { liveRegion = LiveRegionMode.Polite }
                            .testTag("taunt_text"),
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                val subtitleText = if (uiState.shakeCount > 0) {
                    pluralStringResource(
                        id = R.plurals.shake_count,
                        count = uiState.shakeCount,
                        uiState.shakeCount,
                    )
                } else if (uiState.hasAccelerometer) {
                    stringResource(R.string.hint_shake_or_tap)
                } else {
                    stringResource(R.string.hint_tap_only)
                }

                Text(
                    text = subtitleText,
                    style = MaterialTheme.typography.bodyMedium,
                    color = SoftSilver,
                    textAlign = TextAlign.Center,
                )
            }
        }

        if (uiState.isAboutDialogVisible) {
            AboutDialog(
                onDismiss = onDismissAbout,
                onOpenGitHub = onOpenGitHub,
            )
        }
    }
}

@Composable
private fun AboutDialog(
    onDismiss: () -> Unit,
    onOpenGitHub: () -> Unit,
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = IronSurface,
        titleContentColor = PureWhite,
        textContentColor = SoftSilver,
        title = {
            Text(
                text = stringResource(R.string.about_title),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Medium,
            )
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = stringResource(R.string.about_version_format, BuildConfig.VERSION_NAME),
                    style = MaterialTheme.typography.bodyMedium,
                    color = PureWhite,
                )
                Text(
                    text = stringResource(R.string.about_description),
                    style = MaterialTheme.typography.bodyMedium,
                )
                Text(
                    text = stringResource(R.string.copyright),
                    style = MaterialTheme.typography.bodyMedium,
                )
                Text(
                    text = stringResource(R.string.about_license),
                    style = MaterialTheme.typography.bodyMedium,
                )
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text(
                    text = stringResource(R.string.action_close),
                    color = PureWhite,
                )
            }
        },
        dismissButton = {
            TextButton(onClick = onOpenGitHub) {
                Text(
                    text = stringResource(R.string.about_source_code),
                    color = SoftSilver,
                )
            }
        },
    )
}

@Preview(showBackground = true)
@Composable
private fun ShakeTauntScreenInitialPreview() {
    ShakeTaunterTheme {
        ShakeTauntScreen(
            uiState = ShakeTauntUiState(),
            onManualTaunt = {},
            onReplayTaunt = {},
            onToggleMute = {},
            onShowAbout = {},
            onDismissAbout = {},
            onOpenGitHub = {},
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun ShakeTauntScreenTauntedPreview() {
    ShakeTaunterTheme {
        ShakeTauntScreen(
            uiState = ShakeTauntUiState(
                currentTaunt = "You are fabulous",
                shakeCount = 3,
                tauntGeneration = 3,
            ),
            onManualTaunt = {},
            onReplayTaunt = {},
            onToggleMute = {},
            onShowAbout = {},
            onDismissAbout = {},
            onOpenGitHub = {},
        )
    }
}
