package com.example.skgarm.ui.Screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.skgarm.data.Local.Entity.User

import com.example.skgarm.data.Local.Entity.Availability
import com.example.skgarm.Viewmodel.AppViewModel

import java.time.LocalDate
import java.time.format.DateTimeFormatter
import kotlin.collections.forEach
import kotlin.collections.map
import kotlin.getOrDefault
import kotlin.ranges.until

import com.example.skgarm.ui.theme.*


val TIME_SLOTS = listOf("17:00 - 19:00", "19:00 - 21:00", "21:00 - 23:00")
val DAY_FORMATTER: DateTimeFormatter = DateTimeFormatter.ofPattern("EEEE")
val DATE_FORMATTER: DateTimeFormatter = DateTimeFormatter.ofPattern("MMM d")
val KEY_FORMATTER: DateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")


@Composable
fun ScheduleScreen(
    user: User,
    viewModel: AppViewModel = hiltViewModel()
) {
    val days = remember {
        (0 until 7).map { LocalDate.now().plusDays(it.toLong()) }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BgPrimary)
    ) {
        Column {
            // ── Top Bar ──────────────────────────────────────────────────────
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(BgPrimary)
                    .padding(horizontal = 20.dp, vertical = 14.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // App icon
                Surface(
                    shape = RoundedCornerShape(11.dp),
                    color = Teal,
                    modifier = Modifier.size(44.dp)
                ) {
                    Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                        Icon(
                            Icons.Filled.FitnessCenter,
                            null,
                            tint = Color.White,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                }
                Spacer(Modifier.width(12.dp))
                Column(Modifier.weight(1f)) {
                    Text(
                        "Training Schedule",
                        color = TextPrimary,
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text("Next 7 days", color = TextSecondary, fontSize = 12.sp)
                }
                Text(
                    user.name,
                    color = TextSecondary,
                    fontSize = 13.sp,
                    modifier = Modifier.padding(end = 10.dp)
                )
                IconButton(
                    onClick = { viewModel.logout() },
                    modifier = Modifier
                        .size(38.dp)
                        .background(BgField, RoundedCornerShape(10.dp))
                ) {
                    Icon(
                        Icons.AutoMirrored.Filled.Logout,
                        null,
                        tint = TextPrimary,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            HorizontalDivider(color = BorderDefault, thickness = 1.dp)

            // ── Days List ────────────────────────────────────────────────────
            LazyColumn(
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                items(days) { day ->
                    DayCard(day = day, currentUser = user, viewModel = viewModel)
                }
            }
        }
    }
}

// ─── Day Card ─────────────────────────────────────────────────────────────────

@Composable
fun DayCard(day: LocalDate, currentUser: User, viewModel: AppViewModel) {
    val dateKey = day.format(KEY_FORMATTER)

    Surface(
        shape = RoundedCornerShape(18.dp),
        color = BgCard,
        border = androidx.compose.foundation.BorderStroke(1.dp, BorderDefault),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Text(
                day.format(DAY_FORMATTER),
                color = TextPrimary,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
            Text(day.format(DATE_FORMATTER), color = TextSecondary, fontSize = 13.sp)
            Spacer(Modifier.height(14.dp))
            TIME_SLOTS.forEach { slot ->
                SlotCard(
                    date = dateKey,
                    timeSlot = slot,
                    currentUser = currentUser,
                    viewModel = viewModel
                )
                Spacer(Modifier.height(10.dp))
            }
        }
    }
}

// ─── Slot Card ────────────────────────────────────────────────────────────────

@Composable
fun SlotCard(
    date: String,
    timeSlot: String,
    currentUser: User,
    viewModel: AppViewModel
) {
    val attendees by viewModel.attendeesFlow(date, timeSlot)
        .collectAsState(initial = kotlin.collections.emptyList())
    val isAvailable by viewModel.isAvailableFlow(date, timeSlot).collectAsState(initial = false)
    var showModal by remember { mutableStateOf(false) }

    val isActive = attendees.size >= 4
    val slotBg = if (isActive) BgSlotActive else BgSlot
    val slotBorder = if (isActive) BorderActive else Color(0xFF222222)

    Surface(
        shape = RoundedCornerShape(14.dp),
        color = slotBg,
        border = androidx.compose.foundation.BorderStroke(1.dp, slotBorder),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    timeSlot,
                    color = TextPrimary,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold
                )
                // Attendee count chip – tappable to show who's coming
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = Color.Black.copy(alpha = 0.3f),
                    modifier = Modifier.clickable { showModal = true }
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                    ) {
                        Icon(Icons.Filled.Group, null, tint = Teal, modifier = Modifier.size(16.dp))
                        Spacer(Modifier.width(5.dp))
                        Text(
                            "${attendees.size}",
                            color = Teal,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            Spacer(Modifier.height(12.dp))

            // Toggle button
            Button(
                onClick = { viewModel.toggleAvailability(date, timeSlot) },
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isAvailable) Teal else BgPersonIcon
                ),
                border = if (isAvailable) null else androidx.compose.foundation.BorderStroke(
                    1.dp,
                    Color(0xFF333333)
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(46.dp)
            ) {
                Text(
                    if (isAvailable) "✓  I'm Available" else "Mark Available",
                    color = TextPrimary,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )
            }
        }
    }

    if (showModal) {
        AttendeesModal(
            date = date,
            timeSlot = timeSlot,
            attendees = attendees,
            onDismiss = { showModal = false }
        )
    }
}


@Composable
fun AttendeesModal(
    date: String,
    timeSlot: String,
    attendees: List<Availability>,
    onDismiss: () -> Unit
) {
    // Parse date for display
    val displayDate = kotlin.runCatching {
        val ld = LocalDate.parse(date, KEY_FORMATTER)
        "${ld.format(DAY_FORMATTER)}, ${ld.format(DATE_FORMATTER)}"
    }.getOrDefault(date)

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(20.dp),
            color = BgModal,
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
        ) {
            Column(modifier = Modifier.padding(24.dp)) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Top
                ) {
                    Column {
                        Text(
                            displayDate,
                            color = TextPrimary,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(timeSlot, color = TextSecondary, fontSize = 14.sp)
                    }
                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier
                            .size(34.dp)
                            .background(BgPersonIcon, CircleShape)
                    ) {
                        Text("✕", color = TextPrimary, fontSize = 14.sp)
                    }
                }

                Spacer(Modifier.height(16.dp))

                Text(
                    "${attendees.size} ${if (attendees.size == 1) "person" else "people"} available",
                    color = TextSecondary,
                    fontSize = 13.sp
                )

                Spacer(Modifier.height(14.dp))

                if (attendees.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 24.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("No one has signed up yet", color = TextMuted, fontSize = 14.sp)
                    }
                } else {
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        attendees.forEach { a ->
                            PersonRow(name = a.userName)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun PersonRow(name: String) {
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = BgSlot,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 12.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(38.dp)
                    .clip(CircleShape)
                    .background(Teal),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Filled.Person, null, tint = Color.White, modifier = Modifier.size(20.dp))
            }
            Spacer(Modifier.width(14.dp))
            Text(name, color = TextPrimary, fontSize = 15.sp)
        }
    }
}
