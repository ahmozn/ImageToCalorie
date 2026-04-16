package com.example.imagetocalories.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.imagetocalories.data.local.UserEntity
import com.example.imagetocalories.ui.viewmodel.AuthViewModel
import java.text.SimpleDateFormat
import java.util.*
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation

@Composable
fun ProfileScreen(
    onLogout: () -> Unit,
    viewModel: AuthViewModel = hiltViewModel()
) {
    val currentUser by viewModel.currentUser.collectAsState(initial = null)
    var selectedTab by remember { mutableStateOf(0) } // 0: Account, 1: Personal

    var showEditDialog by remember { mutableStateOf<EditField?>(null) }
    val dateFormatter = remember { SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(32.dp))
        
        Text(
            text = "Merhaba",
            style = MaterialTheme.typography.headlineSmall,
            color = MaterialTheme.colorScheme.primary
        )
        
        Text(
            text = currentUser?.firstName ?: "",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(bottom = 32.dp)
        )

        Row(
            modifier = Modifier.fillMaxWidth().padding(bottom = 24.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Button(
                onClick = { selectedTab = 0 },
                modifier = Modifier.weight(1f),
                colors = if (selectedTab == 0) ButtonDefaults.buttonColors() else ButtonDefaults.filledTonalButtonColors()
            ) {
                Text("Hesap Bilgileri")
            }
            Button(
                onClick = { selectedTab = 1 },
                modifier = Modifier.weight(1f),
                colors = if (selectedTab == 1) ButtonDefaults.buttonColors() else ButtonDefaults.filledTonalButtonColors()
            ) {
                Text("Kişisel Detaylar")
            }
        }

        Column(
            modifier = Modifier.weight(1f).fillMaxWidth().verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (selectedTab == 0) {
                ProfileCard(label = EditField.Email.label, value = currentUser?.email ?: "-", onEdit = { showEditDialog = EditField.Email })
                ProfileCard(label = EditField.Password.label, value = "********", onEdit = { showEditDialog = EditField.Password })
            } else {
                ProfileCard(label = EditField.Height.label, value = "${currentUser?.height ?: "-"} cm", onEdit = { showEditDialog = EditField.Height })
                ProfileCard(label = EditField.Weight.label, value = "${currentUser?.currentWeight ?: "-"} kg", onEdit = { showEditDialog = EditField.Weight })
                ProfileCard(label = EditField.TargetCalories.label, value = "${currentUser?.targetCalories ?: "-"} kcal", onEdit = { showEditDialog = EditField.TargetCalories })
                ProfileCard(label = EditField.Gender.label, value = when(currentUser?.gender) {
                    0 -> "Erkek"
                    1 -> "Kadın"
                    2 -> "Belirtmek istemiyorum"
                    else -> "-"
                }, onEdit = { showEditDialog = EditField.Gender })
                ProfileCard(label = EditField.BirthDate.label, value = currentUser?.birthDate?.let { dateFormatter.format(Date(it)) } ?: "-", onEdit = { showEditDialog = EditField.BirthDate })
                ProfileCard(label = EditField.ActivityLevel.label, value = when(currentUser?.activityLevel) {
                    0 -> "Sedanter"
                    1 -> "Hafif Hareketli"
                    2 -> "Orta Hareketli"
                    3 -> "Çok Hareketli"
                    4 -> "Ekstra Hareketli"
                    else -> "-"
                }, onEdit = { showEditDialog = EditField.ActivityLevel })
            }

            Spacer(modifier = Modifier.height(8.dp))
            
            Button(
                onClick = onLogout,
                modifier = Modifier.widthIn(min = 200.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
            ) {
                Text("Çıkış Yap")
            }
            
            Spacer(modifier = Modifier.height(4.dp))
        }
    }

    // Edit Dialog
    showEditDialog?.let { field ->
        EditDialog(
            field = field,
            currentUser = currentUser,
            onDismiss = { showEditDialog = null },
            onSave = { newValue ->
                currentUser?.let { user ->
                    val updatedUser = when (field) {
                        EditField.Email -> user.copy(email = newValue)
                        EditField.Password -> user.copy(password = newValue)
                        EditField.Height -> user.copy(height = newValue.toFloatOrNull())
                        EditField.Weight -> user.copy(currentWeight = newValue.toFloatOrNull())
                        EditField.TargetCalories -> user.copy(targetCalories = newValue.toIntOrNull())
                        EditField.Gender -> user.copy(gender = newValue.toIntOrNull())
                        EditField.BirthDate -> user.copy(birthDate = newValue.toLongOrNull())
                        EditField.ActivityLevel -> user.copy(activityLevel = newValue.toIntOrNull())
                    }
                    viewModel.updateUser(updatedUser)
                }
                showEditDialog = null
            }
        )
    }
}

enum class EditField(val label: String) {
    Email("E-posta"),
    Password("Şifre"),
    Height("Boy"),
    Weight("Mevcut Kilo"),
    TargetCalories("Hedef Kalori"),
    Gender("Cinsiyet"),
    BirthDate("Doğum Tarihi"),
    ActivityLevel("Hareket Seviyesi")
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditDialog(
    field: EditField,
    currentUser: UserEntity?,
    onDismiss: () -> Unit,
    onSave: (String) -> Unit
) {
    var textValue by remember { mutableStateOf(if (field == EditField.Password) "" else when (field) {
        EditField.Email -> currentUser?.email ?: ""
        EditField.Height -> currentUser?.height?.toString() ?: ""
        EditField.Weight -> currentUser?.currentWeight?.toString() ?: ""
        EditField.TargetCalories -> currentUser?.targetCalories?.toString() ?: ""
        EditField.Gender -> currentUser?.gender?.toString() ?: ""
        EditField.BirthDate -> currentUser?.birthDate?.toString() ?: ""
        EditField.ActivityLevel -> currentUser?.activityLevel?.toString() ?: ""
        else -> ""
    }) }
    
    var confirmPassword by remember { mutableStateOf("") }
    var currentPasswordInput by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var securityStep by remember { mutableStateOf(if (field == EditField.Email) "password_check" else "edit") }

    val validate: (String) -> String? = { value ->
        when (field) {
            EditField.Email -> {
                if (!android.util.Patterns.EMAIL_ADDRESS.matcher(value).matches()) "Geçersiz e-posta formatı"
                else null
            }
            EditField.Password -> {
                if (value.length < 8 || value.length > 16) "Şifre 8-16 karakter arası olmalıdır"
                else null
            }
            EditField.Height -> {
                val h = value.toFloatOrNull()
                if (h == null || h < 50 || h > 250) "Boy 50-250 cm arası olmalıdır"
                else null
            }
            EditField.Weight -> {
                val w = value.toFloatOrNull()
                if (w == null || w < 20 || w > 500) "Kilo 20-500 kg arası olmalıdır"
                else null
            }
            EditField.TargetCalories -> {
                val c = value.toIntOrNull()
                if (c == null || c < 0 || c > 10000) "Kalori 0-10000 arası olmalıdır"
                else null
            }
            EditField.BirthDate -> {
                val b = value.toLongOrNull()
                if (b == null) "Geçersiz tarih"
                else {
                    val fifteenYearsAgo = Calendar.getInstance().apply { add(Calendar.YEAR, -15) }.timeInMillis
                    if (b > fifteenYearsAgo) "En az 15 yaşında olmalısınız"
                    else null
                }
            }
            else -> null
        }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { 
            Text(text = if (securityStep == "password_check") "Güvenlik Doğrulaması" else "${field.label} Düzenle") 
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                if (securityStep == "password_check") {
                    Text("Değişiklik yapmak için şifrenizi giriniz:")
                    OutlinedTextField(
                        value = currentPasswordInput,
                        onValueChange = { 
                            currentPasswordInput = it
                            errorMessage = null
                        },
                        label = { Text("Şifre") },
                        singleLine = true,
                        visualTransformation = PasswordVisualTransformation(),
                        isError = errorMessage != null,
                        supportingText = { errorMessage?.let { Text(it) } },
                        modifier = Modifier.fillMaxWidth()
                    )
                } else {
                    when (field) {
                        EditField.Gender -> {
                            val options = listOf("Erkek", "Kadın", "Belirtmek istemiyorum")
                            var selectedIndex by remember { mutableStateOf(textValue.toIntOrNull() ?: 2) }
                            options.forEachIndexed { index, option ->
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.fillMaxWidth().clickable { selectedIndex = index }
                                ) {
                                    RadioButton(selected = selectedIndex == index, onClick = { selectedIndex = index })
                                    Text(text = option, modifier = Modifier.padding(start = 8.dp))
                                }
                            }
                            LaunchedEffect(selectedIndex) { textValue = selectedIndex.toString() }
                        }
                        EditField.ActivityLevel -> {
                            val options = listOf("Sedanter", "Hafif Hareketli", "Orta Hareketli", "Çok Hareketli", "Ekstra Hareketli")
                            var selectedIndex by remember { mutableStateOf(textValue.toIntOrNull() ?: 0) }
                            options.forEachIndexed { index, option ->
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.fillMaxWidth().clickable { selectedIndex = index }
                                ) {
                                    RadioButton(selected = selectedIndex == index, onClick = { selectedIndex = index })
                                    Text(text = option, modifier = Modifier.padding(start = 8.dp))
                                }
                            }
                            LaunchedEffect(selectedIndex) { textValue = selectedIndex.toString() }
                        }
                        EditField.BirthDate -> {
                            val datePickerState = rememberDatePickerState(
                                initialSelectedDateMillis = textValue.toLongOrNull() ?: System.currentTimeMillis()
                            )
                            DatePicker(state = datePickerState, showModeToggle = false)
                            LaunchedEffect(datePickerState.selectedDateMillis) {
                                textValue = datePickerState.selectedDateMillis?.toString() ?: ""
                                errorMessage = validate(textValue)
                            }
                        }
                        EditField.Password -> {
                            OutlinedTextField(
                                value = currentPasswordInput,
                                onValueChange = { 
                                    currentPasswordInput = it
                                    errorMessage = null
                                },
                                label = { Text("Şimdiki Şifre") },
                                singleLine = true,
                                visualTransformation = PasswordVisualTransformation(),
                                modifier = Modifier.fillMaxWidth()
                            )
                            OutlinedTextField(
                                value = textValue,
                                onValueChange = { 
                                    textValue = it
                                    errorMessage = validate(it)
                                },
                                label = { Text("Yeni Şifre") },
                                singleLine = true,
                                visualTransformation = PasswordVisualTransformation(),
                                isError = errorMessage != null,
                                supportingText = { errorMessage?.let { Text(it) } },
                                modifier = Modifier.fillMaxWidth()
                            )
                            OutlinedTextField(
                                value = confirmPassword,
                                onValueChange = { 
                                    confirmPassword = it
                                    if (it != textValue) errorMessage = "Şifreler uyuşmuyor"
                                    else errorMessage = validate(textValue)
                                },
                                label = { Text("Yeni Şifre (Tekrar)") },
                                singleLine = true,
                                visualTransformation = PasswordVisualTransformation(),
                                isError = errorMessage == "Şifreler uyuşmuyor",
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                        else -> {
                            OutlinedTextField(
                                value = textValue,
                                onValueChange = { 
                                    textValue = it
                                    errorMessage = validate(it)
                                },
                                label = { Text(field.label) },
                                singleLine = true,
                                isError = errorMessage != null,
                                supportingText = { errorMessage?.let { Text(it) } },
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }
                    
                    if ((field == EditField.BirthDate) && errorMessage != null) {
                        Text(
                            text = errorMessage!!,
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodySmall,
                            modifier = Modifier.padding(start = 16.dp)
                        )
                    }
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = { 
                    if (securityStep == "password_check") {
                        if (currentPasswordInput == currentUser?.password) {
                            securityStep = "edit"
                            currentPasswordInput = ""
                            errorMessage = null
                        } else {
                            errorMessage = "Hatalı şifre"
                        }
                    } else {
                        val err = validate(textValue)
                        if (err == null) {
                            if (field == EditField.Password) {
                                if (currentPasswordInput != currentUser?.password) {
                                    errorMessage = "Şimdiki şifre hatalı"
                                } else if (textValue != confirmPassword) {
                                    errorMessage = "Şifreler uyuşmuyor"
                                } else {
                                    onSave(textValue)
                                }
                            } else {
                                onSave(textValue)
                            }
                        } else {
                            errorMessage = err
                        }
                    }
                },
                enabled = errorMessage == null || (securityStep == "password_check" && currentPasswordInput.isNotEmpty())
            ) {
                Text(if (securityStep == "password_check") "Doğrula" else "Kaydet")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("İptal")
            }
        }
    )
}

@Composable
fun ProfileCard(label: String, value: String, onEdit: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(2.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = label,
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = value,
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }
            IconButton(onClick = onEdit) {
                Icon(
                    imageVector = Icons.Default.Edit,
                    contentDescription = "Düzenle",
                    tint = MaterialTheme.colorScheme.outline,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}