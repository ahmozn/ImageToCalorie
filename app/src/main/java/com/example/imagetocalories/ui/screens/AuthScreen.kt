package com.example.imagetocalories.ui.screens

import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.imagetocalories.ui.viewmodel.AuthViewModel
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AuthScreen(
    initialIsLogin: Boolean = true,
    onAuthSuccess: () -> Unit,
    viewModel: AuthViewModel = hiltViewModel()
) {
    var isLogin by remember { mutableStateOf(initialIsLogin) }
    var step by remember { mutableStateOf(1) } // 1: Email/Pass, 2: Details

    // Step 1 fields
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }

    var emailError by remember { mutableStateOf<String?>(null) }
    var passwordError by remember { mutableStateOf<String?>(null) }
    var confirmPasswordError by remember { mutableStateOf<String?>(null) }

    // Step 2 fields
    var firstName by remember { mutableStateOf("") }
    var lastName by remember { mutableStateOf("") }
    var birthDate by remember { mutableStateOf<Long?>(null) }
    var gender by remember { mutableStateOf(2) } // 0: Male, 1: Female, 2: Secret
    var height by remember { mutableStateOf("") }
    var weight by remember { mutableStateOf("") }
    var targetCalories by remember { mutableStateOf("") }
    var activityLevel by remember { mutableStateOf(0) }

    var firstNameError by remember { mutableStateOf<String?>(null) }
    var lastNameError by remember { mutableStateOf<String?>(null) }
    var birthDateError by remember { mutableStateOf<String?>(null) }
    var heightError by remember { mutableStateOf<String?>(null) }
    var weightError by remember { mutableStateOf<String?>(null) }
    var targetCaloriesError by remember { mutableStateOf<String?>(null) }

    val context = LocalContext.current
    val authState by viewModel.authState.collectAsState()

    val validateStep1 = {
        emailError = if (email.isBlank()) "E-posta boş olamaz"
        else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) "Geçersiz e-posta formatı"
        else null

        passwordError = if (password.isBlank()) "Şifre boş olamaz"
        else if (password.length < 8 || password.length > 16) "Şifre 8-16 karakter arası olmalıdır"
        else null

        confirmPasswordError = if (confirmPassword != password) "Şifreler uyuşmuyor"
        else null

        emailError == null && passwordError == null && confirmPasswordError == null
    }

    val validateField: (String, String) -> String? = { field, value ->
        when (field) {
            "email" -> if (!android.util.Patterns.EMAIL_ADDRESS.matcher(value).matches()) "Geçersiz e-posta formatı" else null
            "password" -> if (value.length < 8 || value.length > 16) "Şifre 8-16 karakter arası olmalıdır" else null
            "height" -> {
                val h = value.toFloatOrNull()
                if (h == null || h < 50 || h > 250) "Boy 50-250 cm arası olmalıdır" else null
            }
            "weight" -> {
                val w = value.toFloatOrNull()
                if (w == null || w < 20 || w > 500) "Kilo 20-500 kg arası olmalıdır" else null
            }
            "targetCalories" -> {
                if (value.isEmpty()) null
                else {
                    val c = value.toIntOrNull()
                    if (c == null || c < 0 || c > 10000) "Kalori 0-10000 arası olmalıdır" else null
                }
            }
            "birthDate" -> {
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

    val validateStep2 = {
        firstNameError = if (firstName.isBlank()) "Ad boş olamaz" else null
        lastNameError = if (lastName.isBlank()) "Soyad boş olamaz" else null
        birthDateError = validateField("birthDate", birthDate?.toString() ?: "")
        heightError = validateField("height", height)
        weightError = validateField("weight", weight)
        targetCaloriesError = validateField("targetCalories", targetCalories)

        firstNameError == null && lastNameError == null && birthDateError == null && 
        heightError == null && weightError == null && targetCaloriesError == null
    }

    LaunchedEffect(authState) {
        if (authState is AuthViewModel.AuthState.Success) {
            onAuthSuccess()
        } else if (authState is AuthViewModel.AuthState.Error) {
            Toast.makeText(context, (authState as AuthViewModel.AuthState.Error).message, Toast.LENGTH_SHORT).show()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = if (isLogin) "Giriş Yap" else if (step == 1) "Kayıt Ol - Adım 1" else "Kayıt Ol - Adım 2",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(bottom = 24.dp)
        )

        if (isLogin) {
            // Login Fields
            OutlinedTextField(
                value = email,
                onValueChange = { 
                    email = it
                    emailError = null
                },
                label = { Text("E-posta") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                isError = emailError != null,
                supportingText = { emailError?.let { Text(it) } },
                modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)
            )
            OutlinedTextField(
                value = password,
                onValueChange = { 
                    password = it
                    passwordError = null
                },
                label = { Text("Şifre") },
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                isError = passwordError != null,
                supportingText = { passwordError?.let { Text(it) } },
                modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)
            )
            Button(
                onClick = { 
                    if (email.isBlank()) emailError = "E-posta giriniz"
                    if (password.isBlank()) passwordError = "Şifre giriniz"
                    if (emailError == null && passwordError == null) {
                        viewModel.login(email, password)
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Giriş Yap")
            }
        } else {
            // Signup flow
            if (step == 1) {
                OutlinedTextField(
                    value = email,
                    onValueChange = { 
                        email = it
                        emailError = validateField("email", it)
                    },
                    label = { Text("E-posta") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                    isError = emailError != null,
                    supportingText = { emailError?.let { Text(it) } },
                    modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)
                )
                OutlinedTextField(
                    value = password,
                    onValueChange = { 
                        password = it
                        passwordError = validateField("password", it)
                        if (confirmPassword.isNotEmpty()) {
                            confirmPasswordError = if (confirmPassword != it) "Şifreler uyuşmuyor" else null
                        }
                    },
                    label = { Text("Şifre") },
                    visualTransformation = PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    isError = passwordError != null,
                    supportingText = { passwordError?.let { Text(it) } },
                    modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)
                )
                OutlinedTextField(
                    value = confirmPassword,
                    onValueChange = { 
                        confirmPassword = it
                        confirmPasswordError = if (it != password) "Şifreler uyuşmuyor" else null
                    },
                    label = { Text("Şifre Tekrar") },
                    visualTransformation = PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    isError = confirmPasswordError != null,
                    supportingText = { confirmPasswordError?.let { Text(it) } },
                    modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)
                )
                Button(
                    onClick = {
                        if (validateStep1()) {
                            step = 2
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Sonraki Adım")
                }
            } else {
                // Step 2
                OutlinedTextField(
                    value = firstName,
                    onValueChange = { 
                        firstName = it
                        firstNameError = null
                    },
                    label = { Text("Ad") },
                    isError = firstNameError != null,
                    supportingText = { firstNameError?.let { Text(it) } },
                    modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)
                )
                OutlinedTextField(
                    value = lastName,
                    onValueChange = { 
                        lastName = it
                        lastNameError = null
                    },
                    label = { Text("Soyad") },
                    isError = lastNameError != null,
                    supportingText = { lastNameError?.let { Text(it) } },
                    modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)
                )

                // Date Picker for BirthDate
                val datePickerState = rememberDatePickerState()
                var showDatePicker by remember { mutableStateOf(false) }
                
                OutlinedTextField(
                    value = birthDate?.let { java.text.SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date(it)) } ?: "",
                    onValueChange = { },
                    label = { Text("Doğum Tarihi") },
                    readOnly = true,
                    isError = birthDateError != null,
                    supportingText = { birthDateError?.let { Text(it) } },
                    modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp).clickable { showDatePicker = true },
                    enabled = false,
                    colors = OutlinedTextFieldDefaults.colors(
                        disabledTextColor = MaterialTheme.colorScheme.onSurface,
                        disabledBorderColor = if (birthDateError != null) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.outline,
                        disabledLabelColor = if (birthDateError != null) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                )
                
                if (showDatePicker) {
                    DatePickerDialog(
                        onDismissRequest = { showDatePicker = false },
                        confirmButton = {
                            TextButton(onClick = {
                                birthDate = datePickerState.selectedDateMillis
                                birthDateError = validateField("birthDate", birthDate?.toString() ?: "")
                                showDatePicker = false
                            }) { Text("Seç") }
                        }
                    ) {
                        DatePicker(state = datePickerState)
                    }
                }

                // Gender Selection
                Text("Cinsiyet", style = MaterialTheme.typography.labelLarge, modifier = Modifier.align(Alignment.Start))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    listOf("Erkek", "Kadın", "Belirtme").forEachIndexed { index, label ->
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            RadioButton(selected = gender == index, onClick = { gender = index })
                            Text(label)
                        }
                    }
                }

                OutlinedTextField(
                    value = height,
                    onValueChange = { 
                        if (it.isEmpty() || (it.all { char -> char.isDigit() || char == '.' } && it.count { char -> char == '.' } <= 1)) {
                            height = it
                            heightError = validateField("height", it)
                        }
                    },
                    label = { Text("Boy (cm)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    isError = heightError != null,
                    supportingText = { heightError?.let { Text(it) } },
                    modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)
                )
                OutlinedTextField(
                    value = weight,
                    onValueChange = { 
                        if (it.isEmpty() || (it.all { char -> char.isDigit() || char == '.' } && it.count { char -> char == '.' } <= 1)) {
                            weight = it
                            weightError = validateField("weight", it)
                        }
                    },
                    label = { Text("Kilo (kg)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    isError = weightError != null,
                    supportingText = { weightError?.let { Text(it) } },
                    modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)
                )
                OutlinedTextField(
                    value = targetCalories,
                    onValueChange = { 
                        if (it.isEmpty() || it.all { char -> char.isDigit() }) {
                            targetCalories = it
                            targetCaloriesError = validateField("targetCalories", it)
                        }
                    },
                    label = { Text("Hedef Kalori (Opsiyonel)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    isError = targetCaloriesError != null,
                    supportingText = { targetCaloriesError?.let { Text(it) } },
                    modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)
                )

                // Activity Level
                Text("Hareket Seviyesi", style = MaterialTheme.typography.labelLarge, modifier = Modifier.align(Alignment.Start))
                val activityOptions = listOf("Sedanter", "Hafif", "Orta", "Çok", "Ekstra")
                FlowColumn(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Start
                ) {
                    activityOptions.forEachIndexed { index, label ->
                        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(end = 4.dp)) {
                            RadioButton(selected = activityLevel == index, onClick = { activityLevel = index })
                            Text(label, style = MaterialTheme.typography.bodyMedium)
                        }
                    }
                }

                Row(modifier = Modifier.fillMaxWidth().padding(top = 16.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedButton(onClick = { step = 1 }, modifier = Modifier.weight(1f)) {
                        Text("Geri")
                    }
                    Button(
                        onClick = {
                            if (validateStep2()) {
                                viewModel.signUp(
                                    firstName, lastName, email, password,
                                    height = height.toFloat(),
                                    weight = weight.toFloat(),
                                    targetCalories = targetCalories.toIntOrNull(),
                                    birthDate = birthDate,
                                    gender = gender,
                                    activityLevel = activityLevel
                                )
                            }
                        },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Kayıt Ol")
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        TextButton(onClick = { 
            isLogin = !isLogin
            step = 1 
        }) {
            Text(if (isLogin) "Hesabınız yok mu? Kayıt Ol" else "Zaten hesabınız var mı? Giriş Yap")
        }
    }
}

@Composable
fun ScrollableRow(modifier: Modifier = Modifier, content: @Composable () -> Unit) {
    Row(
        modifier = modifier.horizontalScroll(rememberScrollState()),
        verticalAlignment = Alignment.CenterVertically,
        content = { content() }
    )
}
