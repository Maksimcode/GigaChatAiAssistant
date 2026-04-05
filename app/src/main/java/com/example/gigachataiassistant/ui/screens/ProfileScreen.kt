package com.example.gigachataiassistant.ui.screens

import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.gigachataiassistant.R
import com.example.gigachataiassistant.data.settings.AppTheme
import com.example.gigachataiassistant.navigation.DrawerMenuItem
import com.example.gigachataiassistant.ui.navigation.MainModalNavigationDrawer
import com.example.gigachataiassistant.ui.profile.ProfileViewModel
import kotlinx.coroutines.launch

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
                            TextButton(onClick = { viewModel.toggleEditing() }) {
                                Text(stringResource(R.string.profile_edit_action))
                            }
                        } else {
                            TextButton(
                                onClick = { viewModel.updateProfile(editedName) },
                                enabled = !uiState.isLoading,
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
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    Icon(
                        imageVector = Icons.Default.AccountCircle,
                        contentDescription = stringResource(R.string.profile_cd_avatar),
                        modifier = Modifier.size(100.dp),
                        tint = MaterialTheme.colorScheme.primary,
                    )

                    if (uiState.isEditing) {
                        OutlinedTextField(
                            value = editedName,
                            onValueChange = { editedName = it },
                            label = { Text(stringResource(R.string.profile_name_label)) },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true,
                        )
                    } else {
                        Text(
                            text = uiState.user?.displayName?.ifBlank { "—" } ?: "—",
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold,
                        )
                    }

                    Text(
                        text = uiState.user?.email ?: "...",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }

                HorizontalDivider()

                uiState.error?.let {
                    Text(
                        text = it,
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
                        enabled = !uiState.isLoading,
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
                        enabled = !uiState.isLoading,
                    ) {
                        Text(stringResource(R.string.profile_cancel_action))
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
