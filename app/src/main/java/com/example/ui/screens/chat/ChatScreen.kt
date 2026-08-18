package com.example.ui.screens.chat

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.MedicalServices
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.ChatMessage
import com.example.data.model.MessageSender
import com.example.data.repository.PodoRepository
import com.example.ui.components.PodoTopBar
import com.example.ui.theme.PodoBlueLight
import com.example.ui.theme.PodoBluePrimary
import com.example.ui.theme.PodoTealContainer
import com.example.ui.theme.PodoTealDark
import com.example.ui.theme.PodoTealLight
import com.example.ui.theme.PodoTealPrimary

@Composable
fun ChatScreen(
    repository: PodoRepository,
    onNavigateBack: () -> Unit
) {
    val messages by repository.doctorMessages.collectAsState()
    val isTyping by repository.isDoctorTyping.collectAsState()
    val doctorProfile by repository.doctorProfile.collectAsState()

    var inputText by remember { mutableStateOf("") }
    val listState = rememberLazyListState()

    val quickReplies = listOf(
        "🦶 I feel pain around my big toe nail edge",
        "🩺 Follow-up on diabetic foot inspection",
        "📅 When is our next clinical appointment?",
        "📝 How should I apply the prescribed foot cream?",
        "👍 Feet feel great and clear today"
    )

    LaunchedEffect(messages.size, isTyping) {
        if (messages.isNotEmpty()) {
            listState.animateScrollToItem(messages.size)
        }
    }

    Scaffold(
        topBar = {
            PodoTopBar(
                title = doctorProfile.fullName,
                subtitle = "${doctorProfile.title} • Online",
                onBackClick = onNavigateBack
            )
        },
        containerColor = MaterialTheme.colorScheme.background,
        modifier = Modifier
            .fillMaxSize()
            .navigationBarsPadding()
            .imePadding()
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Message List
            LazyColumn(
                state = listState,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .testTag("chat_messages_list"),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 14.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                items(messages, key = { it.id }) { message ->
                    ChatMessageBubble(message = message, doctorName = doctorProfile.fullName)
                }

                if (isTyping) {
                    item {
                        DoctorTypingIndicator()
                    }
                }
            }

            // Quick Reply Chips
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.surface)
                    .padding(top = 8.dp)
            ) {
                Text(
                    text = "Quick Templates:",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 2.dp)
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState())
                        .padding(horizontal = 12.dp, vertical = 6.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    quickReplies.forEach { reply ->
                        Surface(
                            onClick = {
                                repository.sendDoctorMessage(reply, MessageSender.USER)
                            },
                            shape = RoundedCornerShape(16.dp),
                            color = PodoBlueLight,
                            border = BorderStroke(1.dp, PodoBluePrimary.copy(alpha = 0.3f)),
                            modifier = Modifier.height(40.dp)
                        ) {
                            Box(
                                modifier = Modifier.padding(horizontal = 12.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = reply,
                                    color = PodoBluePrimary,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }
                    }
                }
            }

            // Text Input Box
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = MaterialTheme.colorScheme.surface,
                shadowElevation = 8.dp
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = inputText,
                        onValueChange = { inputText = it },
                        modifier = Modifier
                            .weight(1f)
                            .testTag("chat_input_text_field"),
                        placeholder = {
                            Text(
                                text = "Message your podiatrist...",
                                fontSize = 15.sp
                            )
                        },
                        shape = RoundedCornerShape(18.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = PodoTealPrimary,
                            unfocusedBorderColor = MaterialTheme.colorScheme.outline
                        ),
                        maxLines = 3
                    )

                    Spacer(modifier = Modifier.width(10.dp))

                    IconButton(
                        onClick = {
                            if (inputText.isNotBlank()) {
                                repository.sendDoctorMessage(inputText, MessageSender.USER)
                                inputText = ""
                            }
                        },
                        modifier = Modifier
                            .size(54.dp)
                            .background(PodoTealPrimary, RoundedCornerShape(16.dp))
                            .testTag("chat_send_button")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.Send,
                            contentDescription = "Send",
                            tint = Color.White,
                            modifier = Modifier.size(26.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ChatMessageBubble(message: ChatMessage, doctorName: String) {
    val isUser = message.sender == MessageSender.USER

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = if (isUser) Alignment.End else Alignment.Start
    ) {
        Row(
            verticalAlignment = Alignment.Bottom,
            horizontalArrangement = if (isUser) Arrangement.End else Arrangement.Start,
            modifier = Modifier.fillMaxWidth(0.92f)
        ) {
            if (!isUser) {
                Surface(
                    shape = CircleShape,
                    color = PodoTealContainer,
                    modifier = Modifier
                        .size(34.dp)
                        .padding(bottom = 2.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Default.MedicalServices,
                            contentDescription = null,
                            tint = PodoTealDark,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
                Spacer(modifier = Modifier.width(8.dp))
            }

            Surface(
                shape = RoundedCornerShape(
                    topStart = 18.dp,
                    topEnd = 18.dp,
                    bottomStart = if (isUser) 18.dp else 4.dp,
                    bottomEnd = if (isUser) 4.dp else 18.dp
                ),
                color = if (isUser) PodoBluePrimary else PodoTealLight,
                border = if (isUser) null else BorderStroke(1.2.dp, PodoTealContainer),
                shadowElevation = 1.dp,
                modifier = Modifier.weight(1f, fill = false)
            ) {
                Column(
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 12.dp)
                ) {
                    Text(
                        text = if (isUser) "You" else doctorName,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isUser) Color.White.copy(alpha = 0.85f) else PodoTealDark
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = message.text,
                        fontSize = 15.5.sp,
                        lineHeight = 22.sp,
                        color = if (isUser) Color.White else Color(0xFF1B2A27)
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = message.formattedTime,
                        fontSize = 11.sp,
                        color = if (isUser) Color.White.copy(alpha = 0.7f) else Color(0xFF526360),
                        modifier = Modifier.align(Alignment.End)
                    )
                }
            }

            if (isUser) {
                Spacer(modifier = Modifier.width(8.dp))
                Surface(
                    shape = CircleShape,
                    color = PodoBlueLight,
                    modifier = Modifier
                        .size(34.dp)
                        .padding(bottom = 2.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = null,
                            tint = PodoBluePrimary,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun DoctorTypingIndicator() {
    Row(
        modifier = Modifier.padding(start = 42.dp, top = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Surface(
            shape = RoundedCornerShape(14.dp),
            color = PodoTealLight,
            border = BorderStroke(1.dp, PodoTealContainer)
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                CircularProgressIndicator(
                    modifier = Modifier.size(16.dp),
                    strokeWidth = 2.dp,
                    color = PodoTealPrimary
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Doctor is typing...",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium,
                    color = PodoTealDark
                )
            }
        }
    }
}
