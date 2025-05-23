package com.example.watch_together.group

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun ShareInviteLinkButton(inviteLink: Uri?) {
    val context = LocalContext.current

    if (inviteLink == null) {
        Button(onClick = {}) {
            Text("Ссылка не сгенерирована")
        }
    } else {
        Button(onClick = {
            val sendIntent = Intent().apply {
                action = Intent.ACTION_SEND
                putExtra(Intent.EXTRA_TEXT, inviteLink.toString())
                type = "text/plain"
            }
            val shareIntent = Intent.createChooser(sendIntent, null)
            context.startActivity(shareIntent)
        }) {
            Text("Поделиться ссылкой приглашения")
        }
        Spacer(modifier = Modifier.height(8.dp))
        Button(onClick = {
            copyLinkToClipboard(context, inviteLink)
        }) {
            Text("Копировать ссылку")
        }
    }
}



fun copyLinkToClipboard(context: Context, inviteLink: Uri) {
    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
    val clip = ClipData.newPlainText("Invite Link", inviteLink.toString())
    clipboard.setPrimaryClip(clip)
    Toast.makeText(context, "Ссылка скопирована", Toast.LENGTH_SHORT).show()
}


