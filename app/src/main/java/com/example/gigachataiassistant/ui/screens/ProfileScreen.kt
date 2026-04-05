package com.example.gigachataiassistant.ui.screens

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import coil.compose.SubcomposeAsyncImage
import coil.compose.SubcomposeAsyncImageContent
import com.example.gigachataiassistant.R
import com.example.gigachataiassistant.data.settings.AppTheme
import com.example.gigachataiassistant.navigation.DrawerMenuItem
import com.example.gigachataiassistant.ui.components.AuthGroupedProfileFields
import com.example.gigachataiassistant.ui.navigation.MainModalNavigationDrawer
import com.example.gigachataiassistant.ui.theme.AppAuthGroupedFieldStrokeDark
import com.example.gigachataiassistant.ui.theme.AppAuthGroupedFieldStrokeLight
import com.example.gigachataiassistant.ui.theme.AppAuthInputLabelDark
import com.example.gigachataiassistant.ui.theme.AppAuthInputLabelLight
import com.example.gigachataiassistant.ui.theme.AppBackgroundDark
import com.example.gigachataiassistant.ui.theme.AuthInputLabelTextStyle
import com.example.gigachataiassistant.ui.theme.AuthInputStrokeWidth
import com.example.gigachataiassistant.ui.theme.AuthPrimaryButtonHeight
import com.example.gigachataiassistant.ui.theme.AuthPrimaryButtonTextStyle
import com.example.gigachataiassistant.ui.theme.AuthPrimaryButtonsSpacing
import com.example.gigachataiassistant.ui.theme.AuthScreenHorizontalPadding
import com.example.gigachataiassistant.ui.theme.ChatInputRowBottomPadding
import com.example.gigachataiassistant.ui.theme.ChatListAccentIconDark
import com.example.gigachataiassistant.ui.theme.ChatListAccentIconLight
import com.example.gigachataiassistant.ui.theme.ChatListCardDateDark
import com.example.gigachataiassistant.ui.theme.ChatListCardDateLight
import com.example.gigachataiassistant.ui.theme.ChatListCardDateStyle
import com.example.gigachataiassistant.ui.theme.ChatListCardTitleDark
import com.example.gigachataiassistant.ui.theme.ChatListCardTitleLight
import com.example.gigachataiassistant.ui.theme.ChatListCardSpacing
import com.example.gigachataiassistant.ui.theme.ChatListCardTitleStyle
import com.example.gigachataiassistant.ui.theme.CircularProgressStrokeWidth
import com.example.gigachataiassistant.ui.theme.CircularProgressStrokeWidthEmphasis
import com.example.gigachataiassistant.ui.theme.NavDrawerItemHorizontalPadding
import com.example.gigachataiassistant.ui.theme.ProfileAvatarLoadingProgressSize
import com.example.gigachataiassistant.ui.theme.ProfileAvatarOverlayProgressSize
import com.example.gigachataiassistant.ui.theme.ProfileAvatarSize
import com.example.gigachataiassistant.ui.theme.ProfileQuotaEntrySpacing
import com.example.gigachataiassistant.ui.theme.ProfileRetryButtonTopSpacing
import com.example.gigachataiassistant.ui.theme.ProfileSectionPaddingTight
import com.example.gigachataiassistant.ui.theme.ScreenTopBarTitleStyle
import com.example.gigachataiassistant.ui.theme.StandardIconSize
import com.example.gigachataiassistant.ui.theme.ThemeAlpha
import com.example.gigachataiassistant.ui.theme.ThemeSelectorRowHeight
import com.example.gigachataiassistant.ui.theme.topAppBarContentColor
import com.example.gigachataiassistant.ui.profile.ProfileGigaChatQuotaState
import com.example.gigachataiassistant.ui.profile.ProfileViewModel
import kotlinx.coroutines.launch
import java.text.NumberFormat
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    viewModel: ProfileViewModel,
    selectedDrawerItem: DrawerMenuItem,
    onDrawerNavigate: (DrawerMenuItem) -> Unit,
    onLogout: () -> Unit,
) {
    val uiState by viewModel.uiState.collectAsState()
    var editedName by remember(uiState.user) { mutableStateOf(uiState.user?.displayName ?: "") }
    val scope = rememberCoroutineScope()
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val user = uiState.user
    val phone = user?.phoneNumber?.takeIf { it.isNotBlank() }

    val pickPhotoLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
    ) { uri ->
        uri?.let { viewModel.uploadProfilePhoto(it) }
    }

    MainModalNavigationDrawer(
        drawerState = drawerState,
        selectedItem = selectedDrawerItem,
        onDrawerItemClick = { item ->
            scope.launch { drawerState.close() }
            onDrawerNavigate(item)
        },
    ) {
        Scaffold(
            containerColor = MaterialTheme.colorScheme.background,
            topBar = {
                val barColor = topAppBarContentColor()
                TopAppBar(
                    title = {
                        Text(
                            text = stringResource(R.string.profile_title),
                            style = ScreenTopBarTitleStyle,
                            color = barColor,
                        )
                    },
                    navigationIcon = {
                        IconButton(
                            onClick = { scope.launch { drawerState.open() } },
                        ) {
                            Icon(
                                imageVector = Icons.Default.Menu,
                                contentDescription = stringResource(R.string.nav_drawer_open_menu),
                                tint = barColor,
                            )
                        }
                    },
                    actions = {
                        if (!uiState.isEditing) {
                            TextButton(
                                onClick = { viewModel.toggleEditing() },
                                enabled = !uiState.isPhotoUploading,
                                colors = ButtonDefaults.textButtonColors(contentColor = barColor),
                            ) {
                                Text(
                                    stringResource(R.string.profile_edit_action),
                                    style = AuthPrimaryButtonTextStyle,
                                    color = barColor,
                                )
                            }
                        } else {
                            TextButton(
                                onClick = { viewModel.updateProfile(editedName) },
                                enabled = !uiState.isLoading && !uiState.isPhotoUploading,
                                colors = ButtonDefaults.textButtonColors(contentColor = barColor),
                            ) {
                                Text(
                                    stringResource(R.string.profile_save_action),
                                    style = AuthPrimaryButtonTextStyle,
                                    color = barColor,
                                )
                            }
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.background,
                        navigationIconContentColor = barColor,
                        titleContentColor = barColor,
                        actionIconContentColor = barColor,
                    ),
                )
            },
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = AuthScreenHorizontalPadding)
                    .padding(top = AuthPrimaryButtonsSpacing, bottom = ChatInputRowBottomPadding),
                verticalArrangement = Arrangement.spacedBy(AuthPrimaryButtonsSpacing),
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(AuthPrimaryButtonsSpacing),
                ) {
                    ProfileAvatarPhoto(
                        photoUrl = user?.photoUrl?.toString(),
                        isUploading = uiState.isPhotoUploading,
                        enabled = !uiState.isLoading && !uiState.isPhotoUploading,
                        onClick = {
                            pickPhotoLauncher.launch(
                                PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly),
                            )
                        },
                    )
                }

                AuthGroupedProfileFields(
                    name = if (uiState.isEditing) {
                        editedName
                    } else {
                        user?.displayName?.ifBlank { "—" } ?: "—"
                    },
                    onNameChange = { editedName = it },
                    nameEditable = uiState.isEditing,
                    nameEnabled = !uiState.isLoading && !uiState.isPhotoUploading,
                    email = user?.email ?: "—",
                    phone = phone,
                    balanceContent = {
                        ProfileGigaChatQuotaSection(
                            state = uiState.gigaChatQuota,
                            onRetry = { viewModel.refreshGigaChatQuota() },
                        )
                    },
                )

                ProfileDivider()

                uiState.errorMessageId?.let { errId ->
                    Text(
                        text = stringResource(errId),
                        color = MaterialTheme.colorScheme.error,
                        style = ChatListCardDateStyle,
                        modifier = Modifier.fillMaxWidth(),
                    )
                }

                Column(verticalArrangement = Arrangement.spacedBy(AuthPrimaryButtonsSpacing)) {
                    ProfileThemeSectionHeader()
                    ThemeSelector(
                        currentTheme = uiState.currentTheme,
                        onThemeSelected = { viewModel.setTheme(it) },
                    )
                }

                if (!uiState.isEditing) {
                    val onErrorContainer = MaterialTheme.colorScheme.onErrorContainer
                    val errorContainer = MaterialTheme.colorScheme.errorContainer
                    Button(
                        onClick = { viewModel.signOut(onSuccess = onLogout) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(AuthPrimaryButtonHeight),
                        enabled = !uiState.isLoading && !uiState.isPhotoUploading,
                        shape = ButtonDefaults.shape,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = errorContainer,
                            contentColor = onErrorContainer,
                            disabledContainerColor = errorContainer.copy(alpha = ThemeAlpha.DisabledComponentAlpha),
                            disabledContentColor = onErrorContainer.copy(alpha = ThemeAlpha.DisabledComponentAlpha),
                        ),
                    ) {
                        if (uiState.isLoading) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(StandardIconSize),
                                strokeWidth = CircularProgressStrokeWidth,
                                color = onErrorContainer,
                            )
                        } else {
                            Text(
                                text = stringResource(R.string.action_logout),
                                style = AuthPrimaryButtonTextStyle,
                                color = onErrorContainer,
                            )
                        }
                    }
                } else {
                    OutlinedButton(
                        onClick = { viewModel.toggleEditing() },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(AuthPrimaryButtonHeight),
                        enabled = !uiState.isLoading && !uiState.isPhotoUploading,
                    ) {
                        Text(
                            text = stringResource(R.string.profile_cancel_action),
                            style = AuthPrimaryButtonTextStyle,
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ProfileDivider() {
    val stroke = if (MaterialTheme.colorScheme.background == AppBackgroundDark) {
        AppAuthGroupedFieldStrokeDark
    } else {
        AppAuthGroupedFieldStrokeLight
    }
    HorizontalDivider(
        thickness = AuthInputStrokeWidth,
        color = stroke,
    )
}

@Composable
private fun ProfileThemeSectionHeader() {
    val isDark = MaterialTheme.colorScheme.background == AppBackgroundDark
    val iconTint = if (isDark) ChatListAccentIconDark else ChatListAccentIconLight
    val titleColor = if (isDark) ChatListCardTitleDark else ChatListCardTitleLight
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(ChatListCardSpacing),
    ) {
        Icon(
            imageVector = Icons.Default.Palette,
            contentDescription = null,
            tint = iconTint,
        )
        Text(
            text = stringResource(R.string.profile_theme_label),
            style = ChatListCardTitleStyle,
            color = titleColor,
        )
    }
}

@Composable
private fun ProfileAvatarPhoto(
    photoUrl: String?,
    isUploading: Boolean,
    enabled: Boolean,
    onClick: () -> Unit,
) {
    val url = photoUrl?.takeIf { it.isNotBlank() }
    Box(
        modifier = Modifier
            .size(ProfileAvatarSize)
            .clip(CircleShape)
            .clickable(enabled = enabled, onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        if (url != null) {
            SubcomposeAsyncImage(
                model = url,
                contentDescription = stringResource(R.string.profile_cd_avatar),
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop,
                loading = {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(modifier = Modifier.size(ProfileAvatarLoadingProgressSize))
                    }
                },
                error = {
                    Icon(
                        imageVector = Icons.Default.AccountCircle,
                        contentDescription = stringResource(R.string.profile_cd_avatar),
                        modifier = Modifier.fillMaxSize(),
                        tint = if (MaterialTheme.colorScheme.background == AppBackgroundDark) {
                            ChatListAccentIconDark
                        } else {
                            ChatListAccentIconLight
                        },
                    )
                },
                success = { SubcomposeAsyncImageContent() },
            )
        } else {
            Icon(
                imageVector = Icons.Default.AccountCircle,
                contentDescription = stringResource(R.string.profile_cd_avatar),
                modifier = Modifier.fillMaxSize(),
                tint = if (MaterialTheme.colorScheme.background == AppBackgroundDark) {
                    ChatListAccentIconDark
                } else {
                    ChatListAccentIconLight
                },
            )
        }
        if (isUploading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        MaterialTheme.colorScheme.scrim.copy(alpha = ThemeAlpha.ProfileAvatarUploadOverlayAlpha),
                    ),
                contentAlignment = Alignment.Center,
            ) {
                CircularProgressIndicator(
                    modifier = Modifier.size(ProfileAvatarOverlayProgressSize),
                    strokeWidth = CircularProgressStrokeWidthEmphasis,
                )
            }
        }
    }
}

@Composable
private fun ProfileGigaChatQuotaSection(
    state: ProfileGigaChatQuotaState,
    onRetry: () -> Unit,
) {
    val isDark = MaterialTheme.colorScheme.background == AppBackgroundDark
    val labelColor = if (isDark) AppAuthInputLabelDark else AppAuthInputLabelLight
    val bodyMuted = if (isDark) ChatListCardDateDark else ChatListCardDateLight
    val bodyEmphasis = if (isDark) ChatListCardTitleDark else ChatListCardTitleLight
    val numberFormat = remember {
        NumberFormat.getNumberInstance(Locale.forLanguageTag("ru-RU")).apply {
            maximumFractionDigits = 6
            minimumFractionDigits = 0
        }
    }
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = stringResource(R.string.profile_tokens_label),
            style = AuthInputLabelTextStyle,
            color = labelColor,
        )
        when (state) {
            ProfileGigaChatQuotaState.Loading -> {
                Row(
                    modifier = Modifier.padding(top = ProfileRetryButtonTopSpacing),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(ChatListCardSpacing),
                ) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(StandardIconSize),
                        strokeWidth = CircularProgressStrokeWidth,
                    )
                    Text(
                        text = stringResource(R.string.profile_tokens_loading),
                        style = ChatListCardDateStyle,
                        color = bodyMuted,
                    )
                }
            }
            is ProfileGigaChatQuotaState.Loaded -> {
                if (state.entries.isEmpty()) {
                    Text(
                        text = stringResource(R.string.profile_tokens_empty),
                        style = ChatListCardDateStyle,
                        color = bodyMuted,
                        modifier = Modifier.padding(top = ProfileSectionPaddingTight),
                    )
                } else {
                    Column(
                        modifier = Modifier.padding(top = ProfileSectionPaddingTight),
                        verticalArrangement = Arrangement.spacedBy(ProfileQuotaEntrySpacing),
                    ) {
                        state.entries.forEach { entry ->
                            Text(
                                text = "${entry.usage}: ${numberFormat.format(entry.value)}",
                                style = ChatListCardTitleStyle,
                                color = bodyEmphasis,
                            )
                        }
                    }
                }
            }
            ProfileGigaChatQuotaState.NotAvailable -> {
                Text(
                    text = stringResource(R.string.profile_tokens_pay_as_you_go),
                    style = ChatListCardDateStyle,
                    color = bodyMuted,
                    modifier = Modifier.padding(top = ProfileSectionPaddingTight),
                )
            }
            is ProfileGigaChatQuotaState.Error -> {
                Column(modifier = Modifier.padding(top = ProfileSectionPaddingTight)) {
                    Text(
                        text = stringResource(state.messageId),
                        style = ChatListCardDateStyle,
                        color = MaterialTheme.colorScheme.error,
                    )
                    TextButton(
                        onClick = onRetry,
                        colors = ButtonDefaults.textButtonColors(
                            contentColor = MaterialTheme.colorScheme.primary,
                        ),
                    ) {
                        Text(
                            text = stringResource(R.string.action_retry),
                            style = AuthPrimaryButtonTextStyle,
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ThemeSelector(
    currentTheme: AppTheme,
    onThemeSelected: (AppTheme) -> Unit,
) {
    val isDark = MaterialTheme.colorScheme.background == AppBackgroundDark
    val textColor = if (isDark) ChatListCardTitleDark else ChatListCardTitleLight
    val options = listOf(
        AppTheme.SYSTEM to stringResource(R.string.profile_theme_system),
        AppTheme.LIGHT to stringResource(R.string.profile_theme_light),
        AppTheme.DARK to stringResource(R.string.profile_theme_dark),
    )

    Column(Modifier.selectableGroup()) {
        options.forEach { (theme, label) ->
            Row(
                Modifier
                    .fillMaxWidth()
                    .height(ThemeSelectorRowHeight)
                    .selectable(
                        selected = (theme == currentTheme),
                        onClick = { onThemeSelected(theme) },
                        role = Role.RadioButton,
                    ),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                RadioButton(
                    selected = (theme == currentTheme),
                    onClick = null,
                )
                Text(
                    text = label,
                    style = ChatListCardTitleStyle,
                    color = textColor,
                    modifier = Modifier.padding(start = NavDrawerItemHorizontalPadding),
                )
            }
        }
    }
}
