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
import androidx.compose.material.icons.automirrored.filled.ArrowBack
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
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
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
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.SubcomposeAsyncImage
import coil.compose.SubcomposeAsyncImageContent
import com.example.gigachataiassistant.R
import com.example.gigachataiassistant.data.settings.AppTheme
import com.example.gigachataiassistant.navigation.DrawerMenuItem
import com.example.gigachataiassistant.ui.navigation.MainModalNavigationDrawer
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
    onBack: () -> Unit,
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
            topBar = {
                TopAppBar(
                    title = { Text(stringResource(R.string.profile_title)) },
                    navigationIcon = {
                        IconButton(
                            onClick = { scope.launch { drawerState.open() } },
                        ) {
                            Icon(
                                imageVector = Icons.Default.Menu,
                                contentDescription = stringResource(R.string.nav_drawer_open_menu),
                            )
                        }
                    },
                    actions = {
                        IconButton(onClick = onBack) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = stringResource(R.string.chat_cd_back),
                            )
                        }
                        if (!uiState.isEditing) {
                            TextButton(
                                onClick = { viewModel.toggleEditing() },
                                enabled = !uiState.isPhotoUploading,
                            ) {
                                Text(stringResource(R.string.profile_edit_action))
                            }
                        } else {
                            TextButton(
                                onClick = { viewModel.updateProfile(editedName) },
                                enabled = !uiState.isLoading && !uiState.isPhotoUploading,
                            ) {
                                Text(stringResource(R.string.profile_save_action))
                            }
                        }
                    },
                )
            },
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .verticalScroll(rememberScrollState())
                    .padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(24.dp),
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp),
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
                    if (uiState.isEditing) {
                        OutlinedTextField(
                            value = editedName,
                            onValueChange = { editedName = it },
                            label = { Text(stringResource(R.string.profile_name_label)) },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true,
                            enabled = !uiState.isLoading && !uiState.isPhotoUploading,
                        )
                    }
                }

                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    if (!uiState.isEditing) {
                        ProfileLabeledValue(
                            label = stringResource(R.string.profile_name_label),
                            value = user?.displayName?.ifBlank { "—" } ?: "—",
                        )
                    }
                    ProfileLabeledValue(
                        label = stringResource(R.string.profile_email_label),
                        value = user?.email ?: "—",
                    )
                    phone?.let {
                        ProfileLabeledValue(
                            label = stringResource(R.string.profile_phone_label),
                            value = it,
                        )
                    }
                    ProfileGigaChatQuotaSection(
                        state = uiState.gigaChatQuota,
                        onRetry = { viewModel.refreshGigaChatQuota() },
                    )
                }

                HorizontalDivider()

                uiState.errorMessageId?.let { errId ->
                    Text(
                        text = stringResource(errId),
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.fillMaxWidth(),
                    )
                }

                Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                    ) {
                        Icon(
                            imageVector = Icons.Default.Palette,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                        )
                        Text(
                            text = stringResource(R.string.profile_theme_label),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold,
                        )
                    }

                    ThemeSelector(
                        currentTheme = uiState.currentTheme,
                        onThemeSelected = { viewModel.setTheme(it) },
                    )
                }

                Spacer(modifier = Modifier.weight(1f))

                if (!uiState.isEditing) {
                    Button(
                        onClick = { viewModel.signOut(onSuccess = onLogout) },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.errorContainer,
                            contentColor = MaterialTheme.colorScheme.onErrorContainer,
                        ),
                        enabled = !uiState.isLoading && !uiState.isPhotoUploading,
                        shape = MaterialTheme.shapes.medium,
                    ) {
                        if (uiState.isLoading) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(24.dp),
                                strokeWidth = 2.dp,
                                color = MaterialTheme.colorScheme.onErrorContainer,
                            )
                        } else {
                            Text(stringResource(R.string.action_logout))
                        }
                    }
                } else {
                    OutlinedButton(
                        onClick = { viewModel.toggleEditing() },
                        modifier = Modifier.fillMaxWidth(),
                        enabled = !uiState.isLoading && !uiState.isPhotoUploading,
                    ) {
                        Text(stringResource(R.string.profile_cancel_action))
                    }
                }
            }
        }
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
            .size(100.dp)
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
                        CircularProgressIndicator(modifier = Modifier.size(32.dp))
                    }
                },
                error = {
                    Icon(
                        imageVector = Icons.Default.AccountCircle,
                        contentDescription = stringResource(R.string.profile_cd_avatar),
                        modifier = Modifier.fillMaxSize(),
                        tint = MaterialTheme.colorScheme.primary,
                    )
                },
                success = { SubcomposeAsyncImageContent() },
            )
        } else {
            Icon(
                imageVector = Icons.Default.AccountCircle,
                contentDescription = stringResource(R.string.profile_cd_avatar),
                modifier = Modifier.fillMaxSize(),
                tint = MaterialTheme.colorScheme.primary,
            )
        }
        if (isUploading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.scrim.copy(alpha = 0.45f)),
                contentAlignment = Alignment.Center,
            ) {
                CircularProgressIndicator(
                    modifier = Modifier.size(40.dp),
                    strokeWidth = 3.dp,
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
    val numberFormat = remember {
        NumberFormat.getNumberInstance(Locale.forLanguageTag("ru-RU")).apply {
            maximumFractionDigits = 6
            minimumFractionDigits = 0
        }
    }
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = stringResource(R.string.profile_tokens_label),
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        when (state) {
            ProfileGigaChatQuotaState.Loading -> {
                Row(
                    modifier = Modifier.padding(top = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        strokeWidth = 2.dp,
                    )
                    Text(
                        text = stringResource(R.string.profile_tokens_loading),
                        style = MaterialTheme.typography.bodyMedium,
                    )
                }
            }
            is ProfileGigaChatQuotaState.Loaded -> {
                if (state.entries.isEmpty()) {
                    Text(
                        text = stringResource(R.string.profile_tokens_empty),
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.padding(top = 4.dp),
                    )
                } else {
                    Column(
                        modifier = Modifier.padding(top = 4.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp),
                    ) {
                        state.entries.forEach { entry ->
                            Text(
                                text = "${entry.usage}: ${numberFormat.format(entry.value)}",
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Medium,
                            )
                        }
                    }
                }
            }
            ProfileGigaChatQuotaState.NotAvailable -> {
                Text(
                    text = stringResource(R.string.profile_tokens_pay_as_you_go),
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(top = 4.dp),
                )
            }
            is ProfileGigaChatQuotaState.Error -> {
                Column(modifier = Modifier.padding(top = 4.dp)) {
                    Text(
                        text = stringResource(state.messageId),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.error,
                    )
                    TextButton(onClick = onRetry) {
                        Text(stringResource(R.string.action_retry))
                    }
                }
            }
        }
    }
}

@Composable
private fun ProfileLabeledValue(
    label: String,
    value: String,
    valueStyle: TextStyle = MaterialTheme.typography.bodyLarge,
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Text(
            text = value,
            style = valueStyle,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.padding(top = 4.dp),
        )
    }
}

@Composable
private fun ThemeSelector(
    currentTheme: AppTheme,
    onThemeSelected: (AppTheme) -> Unit,
) {
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
                    .height(56.dp)
                    .selectable(
                        selected = (theme == currentTheme),
                        onClick = { onThemeSelected(theme) },
                        role = Role.RadioButton,
                    )
                    .padding(horizontal = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                RadioButton(
                    selected = (theme == currentTheme),
                    onClick = null,
                )
                Text(
                    text = label,
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.padding(start = 16.dp),
                )
            }
        }
    }
}
